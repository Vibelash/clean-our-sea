/* ============================================================
   auth.js — shared auth helpers for the Clean Our Sea frontend.

   Calls the combined Spring Boot backend at :8080 for register /
   login / me / logout. Stores an opaque session token + cached
   user record in localStorage so the rest of the site (Daniel's
   quizzes, Tala's leaderboard, the snake game) can read who is
   logged in without another network round-trip.

   Exposes two surfaces:
     * window.auth.{registerUser, loginUser, logout, getCurrentUser,
       getCurrentUserId, getToken, requireLogin, isLoggedIn, fetchMe}
     * legacy globals (registerUser, loginUser, logoutUser,
       getSession, requireAuth, renderUserChip, formatSince) so
       older pages keep working unchanged.
   ============================================================ */

(function () {
    "use strict";

    const BACKEND_BASE = "https://clean-our-sea-backend.onrender.com";

    /* ---------------------- connectivity banner ----------------------
       Render free-tier dynos sleep after ~15 min idle and a cold start
       takes 30-60s. Without UI feedback the site looks broken during
       that window. We:
         1. Fire a warmup GET on first script load so the dyno starts
            spinning up before the user clicks anything.
         2. Wrap fetch() so any backend call that takes >3s shows a
            "Waking the server..." banner until a response arrives.
       Pages that already render skeletons keep working unchanged; this
       just adds a global hint so users know it isn't frozen.
    -------------------------------------------------------------------- */

    let inflight = 0;
    let bannerTimer = null;
    let bannerEl = null;

    function ensureBanner() {
        if (bannerEl || typeof document === "undefined") return bannerEl;
        bannerEl = document.createElement("div");
        bannerEl.id = "cos-server-banner";
        bannerEl.setAttribute("role", "status");
        bannerEl.style.cssText =
            "position:fixed;top:0;left:0;right:0;z-index:9999;" +
            "padding:10px 16px;font:600 14px system-ui,sans-serif;" +
            "background:#0b3d5c;color:#fff;text-align:center;" +
            "box-shadow:0 2px 6px rgba(0,0,0,.2);" +
            "transform:translateY(-100%);transition:transform .25s ease;";
        bannerEl.textContent = "Waking the server up — first load can take up to a minute on the free tier...";
        if (document.body) document.body.appendChild(bannerEl);
        else document.addEventListener("DOMContentLoaded", () => document.body.appendChild(bannerEl));
        return bannerEl;
    }

    function showBanner() {
        const el = ensureBanner();
        if (el) el.style.transform = "translateY(0)";
    }

    function hideBanner() {
        if (bannerEl) bannerEl.style.transform = "translateY(-100%)";
    }

    function trackRequest(promise) {
        inflight++;
        if (!bannerTimer) {
            bannerTimer = setTimeout(() => { if (inflight > 0) showBanner(); }, 3000);
        }
        const finish = () => {
            inflight--;
            if (inflight <= 0) {
                inflight = 0;
                if (bannerTimer) { clearTimeout(bannerTimer); bannerTimer = null; }
                hideBanner();
            }
        };
        promise.then(finish, finish);
        return promise;
    }

    // Wrap window.fetch so every backend call to the Render origin runs
    // through the inflight tracker and triggers the banner if slow.
    if (typeof window !== "undefined" && window.fetch) {
        const origFetch = window.fetch.bind(window);
        window.fetch = function (input, init) {
            try {
                const url = typeof input === "string" ? input : (input && input.url) || "";
                if (url.indexOf(BACKEND_BASE) === 0) {
                    return trackRequest(origFetch(input, init));
                }
            } catch { /* fall through to plain fetch */ }
            return origFetch(input, init);
        };
    }

    // Fire-and-forget warmup so the dyno starts spinning up before the
    // user touches anything. Errors are intentionally swallowed.
    try {
        if (typeof window !== "undefined" && window.fetch) {
            window.fetch(BACKEND_BASE + "/", { method: "GET", cache: "no-store" }).catch(() => {});
        }
    } catch { /* ignore */ }

    // localStorage keys. `cleanOurSea.*` are the new canonical keys; the
    // legacy `snakeInfinity.session` shape is also written so any older
    // page that still reads it directly keeps functioning.
    const TOKEN_KEY        = "cleanOurSea.token";
    const USER_KEY         = "cleanOurSea.user";
    const LEGACY_SESSION_KEY = "snakeInfinity.session";

    /* ---------------------- storage helpers ---------------------- */

    function readJSON(key) {
        try {
            const raw = localStorage.getItem(key);
            return raw ? JSON.parse(raw) : null;
        } catch {
            return null;
        }
    }

    function writeJSON(key, value) {
        if (value == null) localStorage.removeItem(key);
        else localStorage.setItem(key, JSON.stringify(value));
    }

    function getToken() {
        return localStorage.getItem(TOKEN_KEY) || null;
    }

    function setToken(token) {
        if (token) localStorage.setItem(TOKEN_KEY, token);
        else       localStorage.removeItem(TOKEN_KEY);
    }

    function getCurrentUser() {
        return readJSON(USER_KEY);
    }

    function setCurrentUser(user) {
        writeJSON(USER_KEY, user);

        // Mirror to the legacy session shape so old code that calls
        // getSession() keeps seeing { email, name, loggedInAt }.
        if (user) {
            const legacy = {
                email: user.email || "",
                name:  user.username || user.email || "",
                userId: user.userId,
                loggedInAt: new Date().toISOString()
            };
            writeJSON(LEGACY_SESSION_KEY, legacy);
        } else {
            localStorage.removeItem(LEGACY_SESSION_KEY);
        }
    }

    function getCurrentUserId() {
        const u = getCurrentUser();
        return u && u.userId != null ? u.userId : null;
    }

    function isLoggedIn() {
        return !!getToken() && !!getCurrentUser();
    }

    /* ---------------------- backend calls ---------------------- */

    async function postJSON(path, body) {
        let res;
        try {
            res = await fetch(BACKEND_BASE + path, {
                method:  "POST",
                headers: { "Content-Type": "application/json" },
                body:    JSON.stringify(body)
            });
        } catch (err) {
            throw new Error("Cannot reach the server. Is the backend running on " + BACKEND_BASE + "?");
        }

        let data = null;
        try { data = await res.json(); } catch { /* tolerate empty bodies */ }

        if (!res.ok) {
            const msg = (data && data.error) ? data.error
                      : "Request failed (HTTP " + res.status + ").";
            const e = new Error(msg);
            e.status = res.status;
            e.body   = data;
            throw e;
        }

        return data;
    }

    /**
     * Register a new account. On success, the token + user are cached
     * and the user is logged in.
     *   registerUser({ name?, username?, email, password })  -> {userId, username, email}
     */
    async function registerUser(input) {
        const username = (input.username || input.name || "").trim();
        const email    = (input.email    || "").trim();
        const password =  input.password  || "";

        const data = await postJSON("/auth/register", { username, email, password });
        applyAuthResponse(data);
        return data;
    }

    /**
     * Verify credentials and start a session.
     *   loginUser({ email, password })  -> {userId, username, email}
     */
    async function loginUser(input) {
        const email    = (input.email    || "").trim();
        const password =  input.password  || "";

        const data = await postJSON("/auth/login", { email, password });
        applyAuthResponse(data);
        return data;
    }

    function applyAuthResponse(data) {
        if (!data || !data.token) {
            throw new Error("Server response was missing a token.");
        }
        setToken(data.token);
        setCurrentUser({
            userId:   data.userId,
            username: data.username,
            email:    data.email
        });
    }

    /** GET /auth/me — round-trip the cached token to confirm it's still valid. */
    async function fetchMe() {
        const token = getToken();
        if (!token) return null;
        try {
            const res = await fetch(BACKEND_BASE + "/auth/me", {
                headers: { "Authorization": "Bearer " + token }
            });
            if (!res.ok) return null;
            const u = await res.json();
            setCurrentUser(u);
            return u;
        } catch {
            return null;
        }
    }

    /** Forget the session locally and tell the backend to forget the token. */
    async function logout() {
        const token = getToken();
        if (token) {
            try {
                await fetch(BACKEND_BASE + "/auth/logout", {
                    method:  "POST",
                    headers: { "Authorization": "Bearer " + token }
                });
            } catch { /* network failures are non-fatal on logout */ }
        }
        setToken(null);
        setCurrentUser(null);
    }

    /**
     * Bounce to login.html if not signed in. Pass the current location as
     * `?next=` so login.js can return the user to where they were.
     * Returns the cached user, or null if redirected.
     */
    function requireLogin() {
        if (isLoggedIn()) return getCurrentUser();
        const next = encodeURIComponent(window.location.pathname + window.location.search);
        window.location.href = "login.html?next=" + next;
        return null;
    }

    /* ---------------------- chip + nav rendering ---------------------- */

    /**
     * Render the logged-in user chip into any element with id="user-chip-slot".
     * If there's no slot on the page, the function does nothing — pages that
     * don't have a chip slot in their nav are unaffected.
     */
    function renderUserChip(onChipClick) {
        const slot = document.getElementById("user-chip-slot");
        if (!slot) return;

        const user = getCurrentUser();
        if (!user) {
            slot.innerHTML =
                '<a href="login.html" class="auth-link">Log in</a>' +
                '<a href="register.html" class="auth-link">Register</a>';
            return;
        }

        const display = user.username || user.email || "Player";
        const initial = display.charAt(0).toUpperCase();
        const hasCallback = typeof onChipClick === "function";

        if (hasCallback) {
            slot.innerHTML =
                '<div class="user-chip clickable" id="user-chip" title="View account">' +
                    '<div class="avatar">' + initial + '</div>' +
                    '<span>' + escapeHtml(display) + '</span>' +
                '</div>';
            const el = document.getElementById("user-chip");
            if (el) el.addEventListener("click", onChipClick);
        } else {
            slot.innerHTML =
                '<a href="profile.html" class="user-chip clickable" id="user-chip" title="View profile" style="text-decoration:none;">' +
                    '<div class="avatar">' + initial + '</div>' +
                    '<span>' + escapeHtml(display) + '</span>' +
                '</a>';
        }
    }

    function escapeHtml(s) {
        return String(s)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    /**
     * Humanise an ISO timestamp as "just now" / "N min ago" / "N hr ago"
     * / "N days ago". Used by profile.js and game.js.
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

    /* ---------------------- legacy compatibility shims ---------------------- */

    /**
     * Legacy: returns the old { email, name, loggedInAt } shape. Profile pages
     * and game.js expect this. We synthesise it from the cached user record.
     */
    function getSession() {
        const u = getCurrentUser();
        if (!u) return null;
        const legacy = readJSON(LEGACY_SESSION_KEY) || {};
        return {
            email: u.email || legacy.email || "",
            name:  u.username || legacy.name || u.email || "",
            userId: u.userId,
            loggedInAt: legacy.loggedInAt || new Date().toISOString()
        };
    }

    /** Legacy: redirect to login.html if no session, else return it. */
    function requireAuth() {
        const session = getSession();
        if (!session) {
            const next = encodeURIComponent(window.location.pathname + window.location.search);
            window.location.href = "login.html?next=" + next;
            return null;
        }
        return session;
    }

    /** Legacy synchronous logout used by old profile/game pages. */
    function logoutUser() {
        // Fire-and-forget the backend call; clear local state immediately.
        try { logout(); } catch { /* swallow */ }
        setToken(null);
        setCurrentUser(null);
        window.location.href = "login.html";
    }

    /* ---------------------- exports ---------------------- */

    const api = {
        registerUser,
        loginUser,
        logout,
        fetchMe,
        getCurrentUser,
        getCurrentUserId,
        getToken,
        isLoggedIn,
        requireLogin,
        renderUserChip
    };

    // New canonical entry point.
    window.auth = api;

    // Legacy globals (so login.js, register.js, profile.js, game.js, and
    // Daniel's quiz JS keep working without coordinated edits).
    window.registerUser  = registerUser;
    window.loginUser     = loginUser;
    window.logoutUser    = logoutUser;
    window.getSession    = getSession;
    window.requireAuth   = requireAuth;
    window.requireLogin  = requireLogin;
    window.renderUserChip = renderUserChip;
    window.formatSince   = formatSince;

    // Daniel's quizzes call getUserId(); expose it.
    window.getUserId = function () {
        const id = getCurrentUserId();
        return id != null ? id : 1; // fallback so a logged-out user still sees the page
    };
})();
