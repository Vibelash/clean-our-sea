/* login.js — wires the login form to auth.js */

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

// If already logged in, skip straight to the game.
const existingSession = getSession();
if (existingSession) {
    window.location.replace("game.html");
}

loginForm.addEventListener("submit", (e) => {
    e.preventDefault();
    clearMessage();

    const email    = emailInput.value.trim();
    const password = pwInput.value.trim();

    // Client-side validation
    const mailformat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,})+$/;
    if (!email || !password) {
        showMessage("Please fill in both email and password.", "error");
        return;
    }
    if (!mailformat.test(email)) {
        showMessage("Please enter a valid email address.", "error");
        return;
    }

    const result = loginUser({ email, password });
    if (!result.ok) {
        showMessage(result.error, "error");
        return;
    }

    showMessage("Logged in! Redirecting…", "success");
    setTimeout(() => {
        window.location.href = "game.html";
    }, 600);
});
