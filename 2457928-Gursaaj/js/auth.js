/* ============================================================
   auth.js — shared auth helpers for the frontend-v2 bundle.

   This is a lightweight CLIENT-SIDE auth layer intended to be
   dropped in while the group's real auth backend is not yet
   merged. It stores "accounts" in localStorage and uses the
   logged-in user's email as the player name when the Snake
   game posts a final score to the snakebackend scoreboard.

   When the team wires up a real /login + /register backend,
   ONLY this file needs to change — login.js, register.js and
   game.js all go through these helpers.
   ============================================================ */

const STORAGE_USERS   = "snakeInfinity.users";   // { [email]: { name, passwordHash } }
const STORAGE_SESSION = "snakeInfinity.session"; // { email, name, loggedInAt }

/* ---------- Tiny hash so we never store the plain password ---------- */
/* This is NOT real security — it's just to avoid plain-text in devtools.
   The real backend will use BCrypt on the server side. */
function hash(str) {
    let h = 5381;
    for (let i = 0; i < str.length; i++) {
        h = ((h << 5) + h) + str.charCodeAt(i);
        h |= 0;
    }
    return String(h);
}

/* ---------- Storage helpers ---------- */
function loadUsers() {
    try {
        return JSON.parse(localStorage.getItem(STORAGE_USERS)) || {};
    } catch {
        return {};
    }
}
function saveUsers(users) {
    localStorage.setItem(STORAGE_USERS, JSON.stringify(users));
}

function setSession(email, name) {
    const session = { email, name, loggedInAt: new Date().toISOString() };
    localStorage.setItem(STORAGE_SESSION, JSON.stringify(session));
    return session;
}
function getSession() {
    try {
        return JSON.parse(localStorage.getItem(STORAGE_SESSION));
    } catch {
        return null;
    }
}
function clearSession() {
    localStorage.removeItem(STORAGE_SESSION);
}

/* ---------- Public API ---------- */

/**
 * Register a new local account.
 * Returns { ok: true } on success or { ok: false, error } on failure.
 */
function registerUser({ name, email, password }) {
    const users = loadUsers();
    const key = email.toLowerCase();

    if (users[key]) {
        return { ok: false, error: "An account with that email already exists." };
    }

    users[key] = {
        name: name.trim(),
        passwordHash: hash(password)
    };
    saveUsers(users);
    return { ok: true };
}

/**
 * Verify credentials and start a session.
 * Returns { ok: true, session } or { ok: false, error }.
 */
function loginUser({ email, password }) {
    const users = loadUsers();
    const key = email.toLowerCase();
    const user = users[key];

    if (!user) {
        return { ok: false, error: "No account found with that email." };
    }
    if (user.passwordHash !== hash(password)) {
        return { ok: false, error: "Incorrect password." };
    }

    const session = setSession(key, user.name);
    return { ok: true, session };
}

function logoutUser() {
    clearSession();
    window.location.href = "login.html";
}

/**
 * Redirect to login.html if no session.
 * Call this at the top of any page that requires auth (e.g. game.html).
 */
function requireAuth() {
    const session = getSession();
    if (!session) {
        window.location.href = "login.html";
        return null;
    }
    return session;
}

/**
 * Render the logged-in user chip into any element with id="user-chip-slot".
 * By default the chip links to profile.html so it's clickable from any page.
 * If an `onChipClick` callback is provided, that takes precedence (e.g. an
 * in-page modal). Call after DOMContentLoaded.
 */
function renderUserChip(onChipClick) {
    const session = getSession();
    const slot = document.getElementById("user-chip-slot");
    if (!slot) return;

    if (!session) {
        slot.innerHTML = `
            <a href="login.html" class="auth-link">Log in</a>
            <a href="register.html" class="auth-link">Register</a>
        `;
        return;
    }

    const initial = (session.name || session.email)[0].toUpperCase();
    const displayName = session.name || session.email;
    const hasCallback = typeof onChipClick === "function";

    if (hasCallback) {
        slot.innerHTML = `
            <div class="user-chip clickable" id="user-chip" title="View account">
                <div class="avatar">${initial}</div>
                <span>${displayName}</span>
            </div>
        `;
        document.getElementById("user-chip").addEventListener("click", onChipClick);
    } else {
        slot.innerHTML = `
            <a href="profile.html" class="user-chip clickable" id="user-chip" title="View profile" style="text-decoration:none;">
                <div class="avatar">${initial}</div>
                <span>${displayName}</span>
            </a>
        `;
    }
}

/**
 * Humanise an ISO timestamp as "just now" / "N min ago" / "N hr ago" / "N days ago".
 * Shared by profile.js and game.js.
 */
function formatSince(iso) {
    if (!iso) return "just now";
    const diffMs = Date.now() - new Date(iso).getTime();
    const mins = Math.floor(diffMs / 60000);
    if (mins < 1)  return "just now";
    if (mins < 60) return mins + " min ago";
    const hrs = Math.floor(mins / 60);
    if (hrs < 24)  return hrs + " hr ago";
    const days = Math.floor(hrs / 24);
    return days + " day" + (days === 1 ? "" : "s") + " ago";
}
