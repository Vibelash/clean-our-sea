/* register.js — wires the register form to auth.js */

const regForm    = document.getElementById("register-form");
const msg        = document.getElementById("message");
const nameInput  = document.getElementById("name");
const emailInput = document.getElementById("email");
const pwInput    = document.getElementById("password");
const repPwInput = document.getElementById("rep-password");
const tosInput   = document.getElementById("tos");

function showMessage(text, type) {
    msg.textContent = text;
    msg.className = "message visible " + type;
}
function clearMessage() {
    msg.className = "message";
    msg.textContent = "";
}

regForm.addEventListener("submit", (e) => {
    e.preventDefault();
    clearMessage();

    const name     = nameInput.value.trim();
    const email    = emailInput.value.trim();
    const password = pwInput.value.trim();
    const repPw    = repPwInput.value.trim();
    const mailformat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,})+$/;

    if (!name || !email || !password || !repPw) {
        showMessage("Please fill in every field.", "error");
        return;
    }
    if (!mailformat.test(email)) {
        showMessage("That doesn't look like a valid email address.", "error");
        return;
    }
    if (password.length < 8) {
        showMessage("Password must be at least 8 characters.", "error");
        return;
    }
    if (password !== repPw) {
        showMessage("Passwords do not match.", "error");
        return;
    }
    if (!tosInput.checked) {
        showMessage("Please accept the terms to continue.", "error");
        return;
    }

    const result = registerUser({ name, email, password });
    if (!result.ok) {
        showMessage(result.error, "error");
        return;
    }

    // Auto-login on successful registration.
    loginUser({ email, password });
    showMessage("Account created! Taking you to the game…", "success");
    setTimeout(() => {
        window.location.href = "game.html";
    }, 700);
});
