/* ============================================================
   effects.js — OPTIONAL visual flourish layer for Snake Infinity
   ------------------------------------------------------------
   This file is 100% optional. game.js does NOT depend on it.
   If you delete this file (or remove the <script> tag from
   game.html) the game still runs fine — you just don't get the
   extra particles, shake, and glow on top.

   How it works:
     * game.js dispatches CustomEvents on `document`:
         - "snake:trash-collected"  { x, y, score }
         - "snake:game-over"        { score }
     * This file subscribes to those events and draws overlays
       inside the .game-grid using a single absolutely-positioned
       effects layer. Nothing ever reads back from the DOM, so
       the effects can't slow the game down.
     * All required CSS is injected from here at load time, so
       this file is genuinely drop-in.
   ============================================================ */

(function () {
    const grid = document.getElementById("grid");
    if (!grid) {
        console.warn("[effects.js] #grid not found — skipping effects init");
        return;
    }

    // Must match GRID_SIZE in game.js. Kept in sync manually because
    // effects.js is designed to live without an import dependency.
    const GRID_SIZE = 6;

    // ---------- Inject required styles ----------
    const css = `
    .fx-layer {
        position: absolute;
        inset: 6px; /* match .game-grid padding */
        pointer-events: none;
        overflow: visible;
        z-index: 5;
    }
    .fx-particle {
        position: absolute;
        width: 8px;
        height: 8px;
        margin: -4px 0 0 -4px;
        border-radius: 50%;
        background: radial-gradient(circle at 30% 30%, #fff7c2, #f59e0b 70%);
        box-shadow: 0 0 10px rgba(245, 158, 11, 0.9);
        animation: fx-burst 520ms ease-out forwards;
        will-change: transform, opacity;
    }
    @keyframes fx-burst {
        0%   { transform: translate(0, 0) scale(0.4); opacity: 1; }
        60%  { opacity: 1; }
        100% { transform: translate(var(--dx), var(--dy)) scale(0.1); opacity: 0; }
    }

    .fx-ring {
        position: absolute;
        width: 10px;
        height: 10px;
        margin: -5px 0 0 -5px;
        border-radius: 50%;
        border: 2px solid rgba(179, 229, 252, 0.9);
        box-shadow: 0 0 12px rgba(41, 182, 246, 0.6);
        animation: fx-ring 500ms ease-out forwards;
        will-change: transform, opacity;
    }
    @keyframes fx-ring {
        0%   { transform: scale(0.4); opacity: 1; }
        100% { transform: scale(3.2); opacity: 0; }
    }

    .fx-shake {
        animation: fx-shake 420ms cubic-bezier(.36,.07,.19,.97) both;
    }
    @keyframes fx-shake {
        10%, 90% { transform: translate3d(-1px, 0, 0); }
        20%, 80% { transform: translate3d(2px, 0, 0); }
        30%, 50%, 70% { transform: translate3d(-4px, 0, 0); }
        40%, 60% { transform: translate3d(4px, 0, 0); }
    }

    .fx-flash {
        position: absolute;
        inset: 6px;
        border-radius: var(--radius-sm);
        background: radial-gradient(circle, rgba(239, 68, 68, 0.35), transparent 70%);
        pointer-events: none;
        animation: fx-flash 500ms ease-out forwards;
        z-index: 6;
    }
    @keyframes fx-flash {
        0%   { opacity: 0.9; }
        100% { opacity: 0; }
    }
    `;
    const styleTag = document.createElement("style");
    styleTag.setAttribute("data-effects", "snake-infinity");
    styleTag.textContent = css;
    document.head.appendChild(styleTag);

    // ---------- Effects overlay ----------
    const fxLayer = document.createElement("div");
    fxLayer.className = "fx-layer";
    grid.appendChild(fxLayer);

    // Helper: convert a grid cell (row, col) into a pixel centre inside
    // the effects layer. Measured live so it still works after resize.
    function cellCenter(row, col) {
        const rect = grid.getBoundingClientRect();
        // 6px padding on .game-grid, 3px gaps between cells
        const innerW = rect.width  - 12; // minus padding left+right
        const innerH = rect.height - 12;
        const step   = innerW / GRID_SIZE;
        // `.fx-layer` already has inset: 6px so we work in "inner" coords.
        const cx = (col + 0.5) * step;
        const cy = (row + 0.5) * (innerH / GRID_SIZE);
        return { cx, cy };
    }

    // ---------- Event: trash collected ----------
    document.addEventListener("snake:trash-collected", (e) => {
        const { x: row, y: col } = e.detail;
        const { cx, cy } = cellCenter(row, col);

        // Expanding ring
        const ring = document.createElement("span");
        ring.className = "fx-ring";
        ring.style.left = cx + "px";
        ring.style.top  = cy + "px";
        fxLayer.appendChild(ring);
        setTimeout(() => ring.remove(), 520);

        // Radial particle burst
        const n = 10;
        for (let i = 0; i < n; i++) {
            const p = document.createElement("span");
            p.className = "fx-particle";
            p.style.left = cx + "px";
            p.style.top  = cy + "px";
            const angle = (i / n) * Math.PI * 2 + Math.random() * 0.3;
            const dist  = 22 + Math.random() * 18;
            p.style.setProperty("--dx", (Math.cos(angle) * dist).toFixed(1) + "px");
            p.style.setProperty("--dy", (Math.sin(angle) * dist).toFixed(1) + "px");
            fxLayer.appendChild(p);
            setTimeout(() => p.remove(), 540);
        }
    });

    // ---------- Event: game over ----------
    document.addEventListener("snake:game-over", () => {
        // Red radial flash on top of the grid
        const flash = document.createElement("div");
        flash.className = "fx-flash";
        grid.appendChild(flash);
        setTimeout(() => flash.remove(), 520);

        // Screen shake on the grid itself
        grid.classList.add("fx-shake");
        setTimeout(() => grid.classList.remove("fx-shake"), 440);
    });

    console.log("[effects.js] ready");
})();
