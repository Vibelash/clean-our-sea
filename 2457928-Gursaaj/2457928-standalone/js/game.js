/* ============================================================
   game.js — Snake Infinity
   Protected by auth.js: requires a valid session.

   Responsibilities:
     - game state + loop
     - cached / diffed grid rendering (cheap per tick)
     - leaderboard + impact card loaders
     - generic player detail modal (self OR other player)
     - dispatches custom events for an OPTIONAL effects layer
       (see js/effects.js). If that file is missing, the game
       still runs exactly the same, just without the flourish.
   ============================================================ */

// ---------- Auth gate ----------
const session = requireAuth();
if (!session) {
    throw new Error("Not authenticated");
}

// ---------- Config ----------
const BACKEND_BASE   = "https://clean-our-sea-backend.onrender.com";
const GRID_SIZE      = 6;
const BASE_TICK_MS   = 220;  // normal game speed
const POINTS_PER_HIT = 10;   // every piece of trash is worth this much
const EFFECT_EVERY   = 50;   // trigger a random effect at every multiple of this score

// ---------- DOM (game) ----------
const grid      = document.getElementById("grid");
const scoreEl   = document.getElementById("score");
const trashEl   = document.getElementById("trash");
const lengthEl  = document.getElementById("length");
const messageEl = document.getElementById("message");
const startBtn  = document.getElementById("start-btn");
const resetBtn  = document.getElementById("reset-btn");

// ---------- DOM (side cards + modal) ----------
const leaderboardEl   = document.getElementById("leaderboard");
const refreshLbBtn    = document.getElementById("refresh-lb");
const impactPiecesEl  = document.getElementById("impact-pieces");
const impactMessageEl = document.getElementById("impact-message");
const impactCompareEl = document.getElementById("impact-comparison");

// Crisis / Why-it-matters card
const crisisFactEl   = document.querySelector(".crisis-fact");
const crisisNumberEl = document.getElementById("crisis-number");
const crisisLabelEl  = document.getElementById("crisis-label");
const crisisSourceEl = document.getElementById("crisis-source");
const crisisDotsEl   = document.getElementById("crisis-dots");
const crisisGoalValueEl = document.getElementById("crisis-goal-value");
const crisisGoalFillEl  = document.getElementById("crisis-goal-fill");
const crisisModal    = document.getElementById("crisis-modal");
const learnMoreBtn   = document.getElementById("learn-more-btn");
const crisisPrevBtn  = document.getElementById("crisis-prev");
const crisisNextBtn  = document.getElementById("crisis-next");

const accountModal       = document.getElementById("account-modal");
const accountAvatarEl    = document.getElementById("account-avatar");
const accountTitleEl     = document.getElementById("account-title");
const accountEmailEl     = document.getElementById("account-email");
const accountSinceEl     = document.getElementById("account-since");
const youBadgeEl         = document.getElementById("you-badge");
const statsTitleEl       = document.getElementById("stats-title");
const accountStatsLoad   = document.getElementById("account-stats-loading");
const accountStatsEl     = document.getElementById("account-stats");
const accountStatsEmpty  = document.getElementById("account-stats-empty");
const statHighestEl      = document.getElementById("stat-highest");
const statAverageEl      = document.getElementById("stat-average");
const statTotalEl        = document.getElementById("stat-total");
const statGamesEl        = document.getElementById("stat-games");
const historyTitleEl     = document.getElementById("history-title");
const historyListEl      = document.getElementById("history-list");
const logoutBtn          = document.getElementById("logout-btn");

// ---------- Game state ----------
let snake       = [{x:2,y:2},{x:2,y:1},{x:2,y:0}];
let trash       = [];
let dir         = "right";
let nextDir     = "right";
let score       = 0;
let trashCount  = 0;
let gameRunning = false;
let gameLoop    = null;
// Separate from gameRunning. True the moment the snake dies and stays
// true until an explicit reset, so arrow keys can't silently "revive"
// the dead snake by auto-starting a new round on top of the old state.
let isGameOver  = false;

// Random-effect engine state
let activeEffect       = null;       // an entry from EFFECTS, or null
let effectEndsAt       = 0;          // wall-clock ms timestamp
let effectExpireTimer  = null;       // setTimeout handle
let effectTickerTimer  = null;       // setInterval handle for the countdown UI
let lastEffectMilestone = 0;         // last score milestone we already reacted to

// ---------- Build the grid ONCE and cache the cells ----------
// Reusing the same DOM nodes every tick (instead of
// querySelectorAll'ing and re-creating class strings on every cell)
// is the single biggest win for frame-rate on slower machines.
const cellsArr = new Array(GRID_SIZE * GRID_SIZE);
for (let i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
    const cell = document.createElement("div");
    cell.className = "cell";
    grid.appendChild(cell);
    cellsArr[i] = cell;
}

/*  Track what kind of thing is currently in each cell so we can
    diff against the next state and only touch cells that changed.
    Values: "" | "snake-head" | "snake-body" | "trash" */
const currentState = new Array(GRID_SIZE * GRID_SIZE).fill("");
const nextState    = new Array(GRID_SIZE * GRID_SIZE).fill("");

// ---------- Game loop ----------
// Effects can override the tick rate temporarily (Calm Waters, Fast Tide).
// When no override is in play we run at BASE_TICK_MS.
let tickOverrideMs = null;
function currentTickMs() {
    return tickOverrideMs ?? BASE_TICK_MS;
}

function scheduleTick() {
    if (gameLoop) clearInterval(gameLoop);
    gameLoop = setInterval(update, currentTickMs());
}

function startGame() {
    if (gameRunning) return;
    // If the player died and is trying to start again, force a clean
    // reset first so they can't revive the dead snake mid-collision.
    if (isGameOver) {
        resetGame();
    }
    gameRunning = true;
    setMessage("Game running — use the arrow keys", "");
    if (trash.length === 0) generateTrash();
    scheduleTick();
    startBtn.disabled = true;
    document.body.classList.add("playing");
}

function resetGame() {
    clearInterval(gameLoop);
    snake       = [{x:2,y:2},{x:2,y:1},{x:2,y:0}];
    trash       = [];
    dir         = "right";
    nextDir     = "right";
    score       = 0;
    trashCount  = 0;
    gameRunning = false;
    isGameOver  = false;
    lastEffectMilestone = 0;
    clearActiveEffect(/* silent */ true);
    setMessage("Press an arrow key to dive in", "");
    startBtn.disabled = false;
    document.body.classList.remove("playing");
    updateDisplay();
    generateTrash();
    draw();
}

function generateTrash() {
    let x, y;
    do {
        x = Math.floor(Math.random() * GRID_SIZE);
        y = Math.floor(Math.random() * GRID_SIZE);
    } while (
        snake.some(s => s.x === x && s.y === y) ||
        trash.some(t => t.x === x && t.y === y)
    );
    trash.push({ x, y });
}

function update() {
    dir = nextDir;
    const head = { ...snake[0] };

    if (dir === "right") head.y = (head.y + 1) % GRID_SIZE;
    if (dir === "left")  head.y = (head.y - 1 + GRID_SIZE) % GRID_SIZE;
    if (dir === "down")  head.x = (head.x + 1) % GRID_SIZE;
    if (dir === "up")    head.x = (head.x - 1 + GRID_SIZE) % GRID_SIZE;

    // Self-collision — but Ghost Mode lets the snake pass through itself.
    if (snake.slice(1).some(s => s.x === head.x && s.y === head.y)) {
        if (!isEffectActive("ghost")) {
            gameOver();
            return;
        }
    }

    snake.unshift(head);

    const idx = trash.findIndex(t => t.x === head.x && t.y === head.y);
    if (idx > -1) {
        trash.splice(idx, 1);
        // Double Points doubles the value of every piece while active.
        const points = isEffectActive("double") ? POINTS_PER_HIT * 2 : POINTS_PER_HIT;
        score += points;
        trashCount++;
        generateTrash();
        setMessage(`Trash collected! +${points}`, "");

        // Did this push us past a 50-point milestone? Trigger an effect.
        maybeTriggerMilestoneEffect();

        // Notify the optional effects layer.
        document.dispatchEvent(new CustomEvent("snake:trash-collected", {
            detail: { x: head.x, y: head.y, score, points }
        }));
    } else {
        snake.pop();
    }

    updateDisplay();
    draw();
}

function gameOver() {
    clearInterval(gameLoop);
    gameRunning = false;
    isGameOver  = true;
    clearActiveEffect(/* silent */ true);
    setMessage(`Game over — final score ${score}. Press R or click Reset to play again.`, "game-over");
    startBtn.disabled = false;
    document.body.classList.remove("playing");

    // Notify the optional effects layer.
    document.dispatchEvent(new CustomEvent("snake:game-over", {
        detail: { score }
    }));

    sendScoreToBackend(score);
}

function updateDisplay() {
    scoreEl.textContent  = score;
    trashEl.textContent  = trashCount;
    lengthEl.textContent = snake.length;
}

/* ---------- Random-effect engine ----------
   Every EFFECT_EVERY points the game picks a random short-lived
   effect that bends the rules until it expires. The active effect
   is shown in a banner above the message bar with a live countdown.

   This block is intentionally self-contained: removing it (and the
   matching banner element + CSS) would leave the game playable,
   just less surprising. All effects are also defensively cleared
   on resetGame() and gameOver(). */

const effectBannerEl = document.getElementById("effect-banner");
const effectNameEl   = document.getElementById("effect-name");
const effectTimerEl  = document.getElementById("effect-timer");

const EFFECTS = [
    {
        id: "double",
        name: "Double Points",
        emoji: "x2",
        color: "linear-gradient(135deg, #f59e0b, #ef4444)",
        durationMs: 8000,
        onStart() { /* handled inline in update() */ },
        onEnd()   { /* nothing to undo */ }
    },
    {
        id: "calm",
        name: "Calm Waters",
        emoji: "~",
        color: "linear-gradient(135deg, #38bdf8, #6366f1)",
        durationMs: 6000,
        onStart() {
            tickOverrideMs = 320;
            if (gameRunning) scheduleTick();
        },
        onEnd() {
            tickOverrideMs = null;
            if (gameRunning) scheduleTick();
        }
    },
    {
        id: "fast",
        name: "Fast Tide",
        emoji: ">>",
        color: "linear-gradient(135deg, #ef4444, #f97316)",
        durationMs: 5000,
        onStart() {
            tickOverrideMs = 130;
            if (gameRunning) scheduleTick();
        },
        onEnd() {
            tickOverrideMs = null;
            if (gameRunning) scheduleTick();
        }
    },
    {
        id: "storm",
        name: "Trash Storm",
        emoji: "+",
        color: "linear-gradient(135deg, #10b981, #14b8a6)",
        durationMs: 10000,
        onStart() {
            // Drop two extra pieces immediately. They sit on the field
            // until eaten — no special cleanup needed when the effect ends.
            generateTrash();
            generateTrash();
        },
        onEnd() { /* extras are eaten naturally */ }
    },
    {
        id: "ghost",
        name: "Ghost Mode",
        emoji: "o",
        color: "linear-gradient(135deg, #8b5cf6, #ec4899)",
        durationMs: 5000,
        onStart() { /* checked inline in update() */ },
        onEnd()   { /* nothing to undo */ }
    }
];

function isEffectActive(id) {
    return activeEffect !== null && activeEffect.id === id;
}

function clearActiveEffect(silent) {
    if (effectExpireTimer) { clearTimeout(effectExpireTimer); effectExpireTimer = null; }
    if (effectTickerTimer) { clearInterval(effectTickerTimer); effectTickerTimer = null; }
    if (activeEffect) {
        try { activeEffect.onEnd(); } catch (e) { console.warn("Effect onEnd failed:", e); }
        if (!silent) {
            setMessage(`${activeEffect.name} ended`, "");
        }
    }
    activeEffect = null;
    effectEndsAt = 0;
    if (effectBannerEl) {
        effectBannerEl.classList.remove("active");
        effectBannerEl.style.background = "";
    }
}

function triggerRandomEffect() {
    // Replace any currently active effect cleanly first.
    clearActiveEffect(/* silent */ true);

    const choice = EFFECTS[Math.floor(Math.random() * EFFECTS.length)];
    activeEffect = choice;
    effectEndsAt = Date.now() + choice.durationMs;

    try { choice.onStart(); } catch (e) { console.warn("Effect onStart failed:", e); }

    setMessage(`Effect: ${choice.name}!`, "");

    // Auto-expire
    effectExpireTimer = setTimeout(() => {
        clearActiveEffect(/* silent */ false);
    }, choice.durationMs);

    // Live banner countdown
    updateEffectBanner();
    effectTickerTimer = setInterval(updateEffectBanner, 200);
}

function maybeTriggerMilestoneEffect() {
    // How many full multiples of EFFECT_EVERY have we passed?
    const milestone = Math.floor(score / EFFECT_EVERY) * EFFECT_EVERY;
    if (milestone > 0 && milestone > lastEffectMilestone) {
        lastEffectMilestone = milestone;
        triggerRandomEffect();
    }
}

function updateEffectBanner() {
    if (!effectBannerEl || !effectNameEl || !effectTimerEl) return;
    if (!activeEffect) {
        effectBannerEl.classList.remove("active");
        return;
    }
    const remaining = Math.max(0, effectEndsAt - Date.now());
    effectBannerEl.classList.add("active");
    effectBannerEl.style.background = activeEffect.color;
    effectNameEl.textContent  = `${activeEffect.emoji}  ${activeEffect.name}`;
    effectTimerEl.textContent = (remaining / 1000).toFixed(1) + "s";
}

/*  Diffed render:
      1. Build a fresh nextState snapshot.
      2. Walk it alongside currentState.
      3. Only touch cells whose class actually changed.
    Cuts per-tick DOM writes from ~36 to a handful. */
function draw() {
    nextState.fill("");

    for (let i = 0; i < snake.length; i++) {
        const seg = snake[i];
        const idx = seg.x * GRID_SIZE + seg.y;
        nextState[idx] = (i === 0) ? "snake-head" : "snake-body";
    }
    for (let i = 0; i < trash.length; i++) {
        const t = trash[i];
        const idx = t.x * GRID_SIZE + t.y;
        nextState[idx] = "trash";
    }

    for (let i = 0; i < nextState.length; i++) {
        if (nextState[i] !== currentState[i]) {
            cellsArr[i].className = "cell" + (nextState[i] ? " " + nextState[i] : "");
            currentState[i] = nextState[i];
        }
    }
}

function setMessage(text, className) {
    messageEl.textContent = text;
    messageEl.className = "game-message" + (className ? " " + className : "");
}

// ---------- Controls ----------
// Keys that would otherwise scroll the page while playing.
const GAME_KEYS = new Set([
    "ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight",
    " ", "Spacebar",
    "w", "a", "s", "d", "W", "A", "S", "D",
    "r", "R"
]);

document.addEventListener("keydown", (e) => {
    if (accountModal.classList.contains("open")) {
        if (e.key === "Escape") closeAccountModal();
        return;
    }
    if (crisisModal.classList.contains("open")) {
        if (e.key === "Escape") closeCrisisModal();
        return;
    }

    if (GAME_KEYS.has(e.key)) e.preventDefault();

    // R / Reset must work even from the game-over state.
    if (e.key === "r" || e.key === "R") {
        resetGame();
        return;
    }

    // Dead snake can't move, can't unpause, can't auto-start.
    // The only way out is R or clicking Reset / Start.
    if (isGameOver) {
        return;
    }

    if (["ArrowUp", "w", "W"].includes(e.key) && dir !== "down") nextDir = "up";
    if (["ArrowDown", "s", "S"].includes(e.key) && dir !== "up") nextDir = "down";
    if (["ArrowLeft", "a", "A"].includes(e.key) && dir !== "right") nextDir = "left";
    if (["ArrowRight", "d", "D"].includes(e.key) && dir !== "left") nextDir = "right";

    if (e.key === " ") {
        if (gameRunning) {
            clearInterval(gameLoop);
            setMessage("Paused — press Space to resume", "");
            gameRunning = false;
            startBtn.disabled = false;
            document.body.classList.remove("playing");
        } else if (snake.length > 0) {
            startGame();
        }
    }

    if (!gameRunning && [
        "ArrowUp","ArrowDown","ArrowLeft","ArrowRight","w","a","s","d"
    ].includes(e.key)) {
        startGame();
    }
});

startBtn.addEventListener("click", startGame);
resetBtn.addEventListener("click", resetGame);

// ---------- Initial draw ----------
resetGame();

// ---------- Backend integration ----------
function sendScoreToBackend(finalScore) {
    fetch(BACKEND_BASE + "/scores", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            player: session.name || session.email,
            score:  finalScore
        })
    })
    .then(r => r.json())
    .then(data => {
        console.log("Score saved:", data);
        loadLeaderboard();
        loadImpact();
    })
    .catch(err => {
        console.error("Error sending score to backend:", err);
        leaderboardEl.innerHTML =
            '<li class="lb-empty">Could not reach the backend. Is it running on :8080?</li>';
    });
}

/* ---------- Leaderboard ---------- */
function loadLeaderboard() {
    fetch(BACKEND_BASE + "/scores/leaderboard")
        .then(r => {
            if (!r.ok) throw new Error("HTTP " + r.status);
            return r.json();
        })
        .then(renderLeaderboard)
        .catch(err => {
            console.error("Leaderboard load failed:", err);
            leaderboardEl.innerHTML =
                '<li class="lb-empty">Backend unreachable.</li>';
        });
}

function renderLeaderboard(rows) {
    if (!rows || rows.length === 0) {
        leaderboardEl.innerHTML =
            '<li class="lb-empty">No scores yet — be the first!</li>';
        return;
    }
    const me = (session.name || session.email).toLowerCase();
    leaderboardEl.innerHTML = rows.map(row => {
        const isSelf = (row.player || "").toLowerCase() === me;
        const safePlayer = escapeHtml(row.player);
        return `
            <li class="lb-row${isSelf ? " self" : ""}"
                data-player="${safePlayer}"
                title="View ${safePlayer}'s score history">
                <span class="lb-name">${safePlayer}</span>
                <span class="lb-score">${row.score}</span>
            </li>
        `;
    }).join("");

    // Leaderboard rows open the detail modal WITHOUT the logout button.
    leaderboardEl.querySelectorAll(".lb-row").forEach(row => {
        row.addEventListener("click", () => {
            const player = row.getAttribute("data-player");
            const meName = (session.name || session.email);
            const isSelf = player.toLowerCase() === meName.toLowerCase();
            openPlayerModal(player, isSelf, /* showLogout */ false);
        });
    });
}

refreshLbBtn.addEventListener("click", () => {
    leaderboardEl.innerHTML = '<li class="lb-empty">Refreshing…</li>';
    loadLeaderboard();
    loadImpact();
});

/* ---------- Community impact ---------- */
// Symbolic community target — once we hit this many virtual pieces, the
// crisis goal bar fills. Purely motivational, kept intentionally low so
// even a classroom demo feels meaningful.
const COMMUNITY_GOAL = 1000;

function loadImpact() {
    fetch(BACKEND_BASE + "/scores/impact")
        .then(r => {
            if (!r.ok) throw new Error("HTTP " + r.status);
            return r.json();
        })
        .then(data => {
            const pieces = data.piecesOfPlasticCleaned ?? 0;
            impactPiecesEl.textContent = pieces;
            impactMessageEl.textContent = data.message ||
                "The community hasn't played any rounds yet.";

            // Human-scale comparison: relate virtual pieces to everyday items.
            // (These ratios are deliberately simple so the point lands fast.)
            if (pieces > 0) {
                const bottles = pieces;                       // 1 piece ≈ 1 bottle
                const bags    = Math.max(1, Math.round(pieces * 0.6));
                impactCompareEl.hidden = false;
                impactCompareEl.textContent =
                    `Roughly equal to ${bottles} plastic bottles or ${bags} shopping bags removed.`;
            } else {
                impactCompareEl.hidden = true;
            }

            // Community goal progress bar (on the crisis card).
            updateCommunityGoal(pieces);
        })
        .catch(err => {
            console.error("Impact load failed:", err);
            impactPiecesEl.textContent = "—";
            impactMessageEl.textContent = "Backend unreachable.";
            impactCompareEl.hidden = true;
            updateCommunityGoal(0);
        });
}

function updateCommunityGoal(pieces) {
    if (!crisisGoalValueEl || !crisisGoalFillEl) return;
    const pct = Math.min(100, (pieces / COMMUNITY_GOAL) * 100);
    crisisGoalFillEl.style.width = pct.toFixed(1) + "%";
    crisisGoalValueEl.textContent = pieces.toLocaleString() + " / " +
        COMMUNITY_GOAL.toLocaleString();
}

/* ---------- "Why it matters" — rotating crisis facts ----------
   Real-world statistics about ocean plastic, cycled every few
   seconds to reinforce the SDG 14 framing of the game. Sources
   are shown beneath each fact so the reviewer can verify them. */
const CRISIS_FACTS = [
    {
        number: "8M",
        label:  "tonnes of plastic enter the ocean every year.",
        source: "UN Environment Programme"
    },
    {
        number: "800+",
        label:  "marine species are affected by ocean debris.",
        source: "UNESCO-IOC"
    },
    {
        number: "5.25T",
        label:  "plastic pieces are already floating in our oceans.",
        source: "PLOS ONE, 2014"
    },
    {
        number: "450 yrs",
        label:  "for a single plastic bottle to decompose.",
        source: "NOAA"
    },
    {
        number: "1 in 3",
        label:  "fish caught for food contains plastic.",
        source: "University of Plymouth"
    },
    {
        number: "2050",
        label:  "— by this year plastic in the ocean could outweigh fish.",
        source: "Ellen MacArthur Foundation"
    }
];

let crisisIdx = 0;
let crisisTimer = null;
const CRISIS_ROTATE_MS = 6000;

function renderCrisisFact(i) {
    const fact = CRISIS_FACTS[i];
    if (!fact || !crisisFactEl) return;

    // Fade out, swap, fade in — single transition, no DOM churn.
    crisisFactEl.classList.add("fading");
    setTimeout(() => {
        crisisNumberEl.textContent = fact.number;
        crisisLabelEl.textContent  = fact.label;
        crisisSourceEl.textContent = "Source: " + fact.source;
        crisisFactEl.classList.remove("fading");
    }, 280);

    // Dot indicator
    crisisDotsEl.querySelectorAll(".dot").forEach((dot, dotIdx) => {
        dot.classList.toggle("active", dotIdx === i);
    });
}

function startCrisisTimer() {
    if (crisisTimer) clearInterval(crisisTimer);
    crisisTimer = setInterval(() => {
        crisisIdx = (crisisIdx + 1) % CRISIS_FACTS.length;
        renderCrisisFact(crisisIdx);
    }, CRISIS_ROTATE_MS);
}

function stopCrisisTimer() {
    if (crisisTimer) { clearInterval(crisisTimer); crisisTimer = null; }
}

// Jump to a specific fact and restart the rotation so the user
// gets a full window to read it.
function goToCrisisFact(i) {
    crisisIdx = ((i % CRISIS_FACTS.length) + CRISIS_FACTS.length) % CRISIS_FACTS.length;
    renderCrisisFact(crisisIdx);
    startCrisisTimer();
}

function initCrisisCard() {
    if (!crisisDotsEl) return;

    // Build one dot per fact
    crisisDotsEl.innerHTML = CRISIS_FACTS
        .map((_, i) => `<span class="dot${i === 0 ? " active" : ""}" data-idx="${i}"></span>`)
        .join("");

    // First fact immediately (no fade on first draw)
    const first = CRISIS_FACTS[0];
    crisisNumberEl.textContent = first.number;
    crisisLabelEl.textContent  = first.label;
    crisisSourceEl.textContent = "Source: " + first.source;

    // Auto-rotate every 6s
    startCrisisTimer();

    // Pause rotation when hovered so you can actually read a fact
    crisisFactEl.addEventListener("mouseenter", stopCrisisTimer);
    crisisFactEl.addEventListener("mouseleave", startCrisisTimer);

    // Manual navigation — arrows + dots
    if (crisisPrevBtn) {
        crisisPrevBtn.addEventListener("click", () => goToCrisisFact(crisisIdx - 1));
    }
    if (crisisNextBtn) {
        crisisNextBtn.addEventListener("click", () => goToCrisisFact(crisisIdx + 1));
    }
    crisisDotsEl.addEventListener("click", (e) => {
        const dot = e.target.closest(".dot");
        if (!dot) return;
        const idx = parseInt(dot.getAttribute("data-idx"), 10);
        if (!isNaN(idx)) goToCrisisFact(idx);
    });
}

/* ---------- Crisis modal (Learn more / SDG 14 deep dive) ---------- */
function openCrisisModal() {
    crisisModal.classList.add("open");
    crisisModal.setAttribute("aria-hidden", "false");
}
function closeCrisisModal() {
    crisisModal.classList.remove("open");
    crisisModal.setAttribute("aria-hidden", "true");
}

if (learnMoreBtn) learnMoreBtn.addEventListener("click", openCrisisModal);
document.querySelectorAll("[data-close-crisis]").forEach(el => {
    el.addEventListener("click", closeCrisisModal);
});

/* ---------- Player detail modal ----------
   Used for BOTH the logged-in user (clicking the nav chip) and any
   other player (clicking a leaderboard row). `isSelf` controls
   whether self-only bits are shown (email, signed-in time, "You"
   badge). `showLogout` is separate because we only want the logout
   button on the nav-chip flow, NOT on the leaderboard flow. */
function openPlayerModal(playerName, isSelf, showLogout = false) {
    // Header
    const initial = (playerName || "?")[0].toUpperCase();
    accountAvatarEl.textContent = initial;
    accountTitleEl.textContent = playerName;

    // Self-only bits
    accountEmailEl.hidden = !isSelf;
    accountSinceEl.hidden = !isSelf;
    youBadgeEl.hidden     = !isSelf;
    logoutBtn.hidden      = !(isSelf && showLogout);
    if (isSelf) {
        accountEmailEl.textContent = session.email;
        accountSinceEl.textContent = "Signed in " + formatSince(session.loggedInAt);
    }

    statsTitleEl.textContent   = isSelf ? "Your stats" : "Stats";
    historyTitleEl.textContent = isSelf ? "Your score history" : "Score history";

    accountStatsLoad.hidden = false;
    accountStatsEl.hidden = true;
    accountStatsEmpty.hidden = true;
    historyListEl.innerHTML = '<li class="history-empty">Loading…</li>';

    accountModal.classList.add("open");
    accountModal.setAttribute("aria-hidden", "false");

    const encoded = encodeURIComponent(playerName);

    fetch(BACKEND_BASE + "/scores/player/" + encoded)
        .then(r => {
            if (r.status === 404) return null;
            if (!r.ok) throw new Error("HTTP " + r.status);
            return r.json();
        })
        .then(stats => {
            accountStatsLoad.hidden = true;
            if (!stats) {
                accountStatsEmpty.hidden = false;
                return;
            }
            statHighestEl.textContent = stats.highestScore ?? 0;
            statAverageEl.textContent = (stats.averageScore ?? 0).toFixed(1);
            statTotalEl.textContent   = stats.totalScore ?? 0;
            statGamesEl.textContent   = stats.gamesPlayed ?? 0;
            accountStatsEl.hidden = false;
        })
        .catch(err => {
            console.error("Stats load failed:", err);
            accountStatsLoad.textContent = "Could not load stats. Backend unreachable.";
        });

    fetch(BACKEND_BASE + "/scores/player/" + encoded + "/history")
        .then(r => {
            if (r.status === 404 || r.status === 400) return [];
            if (!r.ok) throw new Error("HTTP " + r.status);
            return r.json();
        })
        .then(renderHistory)
        .catch(err => {
            console.error("History load failed:", err);
            historyListEl.innerHTML =
                '<li class="history-empty">Could not load score history.</li>';
        });
}

function renderHistory(rows) {
    if (!rows || rows.length === 0) {
        historyListEl.innerHTML =
            '<li class="history-empty">No games played yet.</li>';
        return;
    }
    historyListEl.innerHTML = rows.map((row) => `
        <li class="history-row">
            <span class="history-score">${row.score}</span>
            <span class="history-bar-wrap">
                <span class="history-bar" style="width:${computeBarWidth(row.score, rows)}%"></span>
            </span>
        </li>
    `).join("");
}

function computeBarWidth(score, rows) {
    const max = Math.max(...rows.map(r => r.score));
    if (max <= 0) return 0;
    return Math.max(6, Math.round((score / max) * 100));
}

function closeAccountModal() {
    accountModal.classList.remove("open");
    accountModal.setAttribute("aria-hidden", "true");
}

document.querySelectorAll("[data-close-modal]").forEach(el => {
    el.addEventListener("click", closeAccountModal);
});

// Nav user chip → open modal for the logged-in user WITH logout button.
renderUserChip(() => {
    openPlayerModal(session.name || session.email, /* isSelf */ true, /* showLogout */ true);
});

/* ---------- Helpers ---------- */
function escapeHtml(str) {
    return String(str).replace(/[&<>"']/g, c => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    }[c]));
}

function formatSince(iso) {
    if (!iso) return "just now";
    const diffMs = Date.now() - new Date(iso).getTime();
    const mins = Math.floor(diffMs / 60000);
    if (mins < 1) return "just now";
    if (mins < 60) return mins + " min ago";
    const hrs = Math.floor(mins / 60);
    if (hrs < 24) return hrs + " hr ago";
    const days = Math.floor(hrs / 24);
    return days + " day" + (days === 1 ? "" : "s") + " ago";
}

/* ---------- Initial load ---------- */
loadLeaderboard();
loadImpact();
initCrisisCard();
