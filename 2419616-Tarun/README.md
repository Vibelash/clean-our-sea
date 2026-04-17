# frontend-v2 — Snake Infinity (clean merge bundle)

Self-contained front-end for the Snake Infinity / Clean Our Sea project. Drop this entire folder into the group repo and link to `login.html` — nothing else needs changing.

## What's in here

```
frontend-v2/
  login.html        Log-in page
  register.html     Account creation page
  game.html         Snake game (auth-gated)
  css/
    style.css       Single shared stylesheet, ocean-themed (SDG 14)
  js/
    auth.js         Shared auth helpers (register / login / session / logout)
    login.js        Wires the login form to auth.js
    register.js     Wires the register form to auth.js
    game.js         Snake game logic + backend score submission
```

Every page links to `css/style.css` — no per-page CSS, so it's easy to rebrand in one file.

## User flow

1. User opens `login.html`.
2. New users click "Create an account" → `register.html`.
3. After register, the user is auto-logged-in and redirected to `game.html`.
4. `game.html` calls `requireAuth()` on load — if there's no session, it bounces back to `login.html`.
5. When the game ends, the final score is POSTed to `http://localhost:8080/scores` with the logged-in user's display name as the `player` field.
6. The logout button in the nav clears the session and returns to login.

## Auth model (important — read before merging)

`auth.js` is a **client-side-only** auth layer using `localStorage`. It is intended as a placeholder while the group's real Spring Security / JWT backend is being merged. The API is designed so that only `auth.js` needs to change when the real backend lands.

Public functions exposed to the other scripts:

| Function | Purpose |
|---|---|
| `registerUser({ name, email, password })` | Create a new local account |
| `loginUser({ email, password })` | Verify credentials and start a session |
| `logoutUser()` | Clear session and redirect to login |
| `requireAuth()` | Use at the top of any protected page |
| `getSession()` | Returns `{ email, name, loggedInAt }` or `null` |
| `renderUserChip()` | Fills `#user-chip-slot` with avatar + logout button |

### Swapping in a real backend

When the real `/register` and `/login` endpoints are ready, replace the bodies of `registerUser` and `loginUser` in `auth.js` with `fetch()` calls — everything downstream (login.js, register.js, game.js, the session chip, `requireAuth()`) keeps working unchanged.

A minimal example using the reference project's JWT pattern:

```js
async function loginUser({ email, password }) {
    const res = await fetch(BACKEND_BASE + "/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: email, password })
    });
    if (!res.ok) return { ok: false, error: "Invalid credentials" };
    const token = res.headers.get("Authorization");
    localStorage.setItem("token", token);
    setSession(email, /* name from /me */ email);
    return { ok: true };
}
```

`game.js` would then add `Authorization: <token>` to its `sendScoreToBackend` fetch.

## How to test locally

No build step. Just open `login.html` in a browser (or serve the folder via any static server). If the snakebackend Spring Boot app is running on `localhost:8080`, scores from game-over events will show up in `GET /scores`.

Quick smoke test:

1. Start the snakebackend: `./mvnw spring-boot:run` from the repo root.
2. Open `frontend-v2/login.html` in a browser.
3. Register a new account (any email + 8+ char password).
4. Play a round of the game.
5. Lose on purpose.
6. Hit `http://localhost:8080/scores` — you should see a row with your display name and score.
7. Hit `http://localhost:8080/scores/leaderboard` — top 10 descending.
8. Hit `http://localhost:8080/scores/player/<your name>` — your stats DTO.
9. Hit `http://localhost:8080/scores/impact` — the SDG 14 community-impact DTO.

## Why a new folder?

The existing `src/main/Frontend/` is kept intact so the original Deliverable 4 submission is preserved. This folder is the clean version for merging with teammates — it uses a single stylesheet, a single auth module, and no file clashes with the old one.

## Known non-issues

- `localStorage` is domain-scoped. If you serve `frontend-v2` from a different origin than the Spring Boot backend, that's fine — CORS is already enabled on `ScoreController` via `@CrossOrigin`.
- The tiny `hash()` function in `auth.js` is **not** real security. It exists purely so devtools don't show plain-text passwords during the demo build. The real backend must use BCrypt server-side.
- The nav deliberately does not show "Log in" / "Register" links on `game.html` — those pages only make sense when you're signed out.
