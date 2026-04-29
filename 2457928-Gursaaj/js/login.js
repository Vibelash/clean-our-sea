/* login.js — wires the login form to the backend via auth.js */

const loginForm  = document.getElementById("login-form");
const msg        = document.getElementById("message");
const emailInput = document.getElementById("email");
const pwInput    = document.getElementById("password");

function showMessage(text, type) {
    msg.textContent = text;
    msg.className = "message visible " + type;
}

function clearMessage() {
    msg.className = "message";
    msg.textContent = "";
}

// Where to send the user after a successful login. ?next= takes priority
// (so requireLogin() round-trips correctly), otherwise default to profile.
function nextDestination() {
    const params = new URLSearchParams(window.location.search);
    const next = params.get("next");
    if (next && next.startsWith("/")) return next;          // server-relative
    if (next && /^[a-z0-9_\-]+\.html/i.test(next)) return next; // simple page
    return "profile.html";
}

// If already logged in, skip straight to the destination.
if (window.auth && window.auth.isLoggedIn && window.auth.isLoggedIn()) {
    window.location.replace(nextDestination());
}

loginForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearMessage();

    const email    = emailInput.value.trim();
    const password = pwInput.value;

    const mailformat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,})+$/;
    if (!email || !password) {
        showMessage("Please fill in both email and password.", "error");
        return;
    }
    if (!mailformat.test(email)) {
        showMessage("Please enter a valid email address.", "error");
        return;
    }

    showMessage("Logging in…", "info");

    try {
        await window.auth.loginUser({ email, password });
        showMessage("Logged in! Redirecting…", "success");
        setTimeout(() => { window.location.href = nextDestination(); }, 500);
    } catch (err) {
        showMessage(err && err.message ? err.message : "Login failed.", "error");
    }
});
