/* ============================================================
   profile.js — dedicated profile page.
   Renders the logged-in user's info, stats, and score history,
   and wires up the logout button. Auth-gated: anyone not logged
   in is bounced to login.html by requireAuth().
   ============================================================ */

const session = requireAuth();
if (!session) {
    throw new Error("Not authenticated");
}

const BACKEND_BASE = "http://localhost:8080";

const playerName = session.name || session.email;
const initial = (playerName || "?")[0].toUpperCase();

document.getElementById("profile-avatar").textContent = initial;
document.getElementById("profile-name").textContent = playerName;
document.getElementById("profile-email").textContent = session.email;
document.getElementById("profile-since").textContent = "Signed in " + formatSince(session.loggedInAt);

renderUserChip();

const statsLoadEl  = document.getElementById("profile-stats-loading");
const statsEl      = document.getElementById("profile-stats");
const statsEmptyEl = document.getElementById("profile-stats-empty");
const historyEl    = document.getElementById("profile-history");

const encoded = encodeURIComponent(playerName);

fetch(BACKEND_BASE + "/scores/player/" + encoded)
    .then(r => {
        if (r.status === 404) return null;
        if (!r.ok) throw new Error("HTTP " + r.status);
        return r.json();
    })
    .then(stats => {
        statsLoadEl.hidden = true;
        if (!stats) {
            statsEmptyEl.hidden = false;
            return;
        }
        document.getElementById("stat-highest").textContent = stats.highestScore ?? 0;
        document.getElementById("stat-average").textContent = (stats.averageScore ?? 0).toFixed(1);
        document.getElementById("stat-total").textContent   = stats.totalScore ?? 0;
        document.getElementById("stat-games").textContent   = stats.gamesPlayed ?? 0;
        statsEl.hidden = false;
    })
    .catch(err => {
        console.error("Stats load failed:", err);
        statsLoadEl.textContent = "Could not load stats. Backend unreachable.";
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
        historyEl.innerHTML = '<li class="history-empty">Could not load score history.</li>';
    });

function renderHistory(rows) {
    if (!rows || rows.length === 0) {
        historyEl.innerHTML = '<li class="history-empty">No games played yet.</li>';
        return;
    }
    const max = Math.max(...rows.map(r => r.score));
    historyEl.innerHTML = rows.map(row => {
        const width = max <= 0 ? 0 : Math.max(6, Math.round((row.score / max) * 100));
        return `
            <li class="history-row">
                <span class="history-score">${row.score}</span>
                <span class="history-bar-wrap">
                    <span class="history-bar" style="width:${width}%"></span>
                </span>
            </li>
        `;
    }).join("");
}

document.getElementById("logout-btn").addEventListener("click", () => {
    logoutUser();
});
