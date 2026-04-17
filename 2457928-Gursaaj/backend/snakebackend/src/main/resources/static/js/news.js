/* ============================================================
   Snake Infinity — News & Facts feed
   Shared between index.html (preview strip + hero stats) and
   news.html (full list + modal + filter chips).
   ============================================================ */

const NEWS_BACKEND = "";
const NEWS_ENDPOINT        = NEWS_BACKEND + "/news";
const NEWS_PREVIEW_ENDPOINT = NEWS_BACKEND + "/news/preview";
const IMPACT_ENDPOINT       = NEWS_BACKEND + "/scores/impact";
const SCORES_ENDPOINT       = NEWS_BACKEND + "/scores";

/* Which category chip is active on news.html. "all" = no filter. */
let activeCategory = "all";

/* Cache of the full (unfiltered) feed so category switching is instant. */
let newsCache = null;

/* --------- Shared helpers --------- */

function escapeHtml(str) {
    if (str == null) return "";
    return String(str)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}

function formatDate(iso) {
    if (!iso) return "";
    const d = new Date(iso);
    if (isNaN(d.getTime())) return "";
    return d.toLocaleDateString(undefined, {
        year:  "numeric",
        month: "short",
        day:   "numeric",
    });
}

/* Turn plain body text with \n\n into paragraphs for the modal. */
function bodyToHtml(body) {
    if (!body) return "";
    return body
        .split(/\n\s*\n/)
        .map(p => `<p>${escapeHtml(p.trim())}</p>`)
        .join("");
}

/* --------- Home-page hero stats (index.html) --------- */

function loadHomeHeroStats() {
    // Pieces cleaned + games played come from the existing /scores/impact
    fetch(IMPACT_ENDPOINT)
        .then(r => r.ok ? r.json() : null)
        .then(data => {
            if (!data) return;
            const piecesEl = document.getElementById("home-stat-pieces");
            if (piecesEl && typeof data.piecesOfPlastic === "number") {
                piecesEl.textContent = data.piecesOfPlastic.toLocaleString();
            }
        })
        .catch(() => { /* offline: keep dash */ });

    fetch(SCORES_ENDPOINT)
        .then(r => r.ok ? r.json() : [])
        .then(list => {
            const el = document.getElementById("home-stat-games");
            if (el) el.textContent = (list.length || 0).toLocaleString();
        })
        .catch(() => { /* ignore */ });

    fetch(NEWS_ENDPOINT)
        .then(r => r.ok ? r.json() : [])
        .then(list => {
            const el = document.getElementById("home-stat-articles");
            if (el) el.textContent = (list.length || 0).toLocaleString();
        })
        .catch(() => { /* ignore */ });
}

/* --------- Home-page news preview (index.html) --------- */

function loadHomePreview() {
    const grid = document.getElementById("home-news-grid");
    if (!grid) return;  // not on index.html

    fetch(NEWS_PREVIEW_ENDPOINT)
        .then(r => {
            if (!r.ok) throw new Error("Backend returned " + r.status);
            return r.json();
        })
        .then(posts => renderHomePreview(posts))
        .catch(err => {
            grid.innerHTML = `
                <div class="home-news-card home-news-empty">
                    <div class="home-news-body">
                        <h3>News feed offline</h3>
                        <p>The news feed is temporarily unavailable. Please try again later.</p>
                    </div>
                </div>
            `;
            console.warn("[news] preview load failed:", err);
        });
}

function renderHomePreview(posts) {
    const grid = document.getElementById("home-news-grid");
    if (!grid) return;
    if (!posts || posts.length === 0) {
        grid.innerHTML = `
            <div class="home-news-card home-news-empty">
                <div class="home-news-body">
                    <h3>No articles yet</h3>
                    <p>Check back soon.</p>
                </div>
            </div>
        `;
        return;
    }
    grid.innerHTML = posts.map(p => homeCardHtml(p)).join("");
}

function homeCardHtml(p) {
    const date = formatDate(p.postedAt);
    const icon = p.icon || "📰";
    const cat = escapeHtml(p.category || "news");
    return `
        <a class="home-news-card" href="news.html#post-${p.id}">
            <div class="home-news-top">
                <span class="home-news-icon" aria-hidden="true">${icon}</span>
                <span class="home-news-cat">${cat}</span>
            </div>
            <div class="home-news-body">
                <h3>${escapeHtml(p.title)}</h3>
                <p>${escapeHtml(p.summary)}</p>
            </div>
            <div class="home-news-foot">
                <span>${escapeHtml(date)}</span>
                <span class="home-news-more">Read →</span>
            </div>
        </a>
    `;
}

/* --------- News-page full list (news.html) --------- */

function loadNewsPage() {
    const list = document.getElementById("news-list");
    if (!list) return;  // not on news.html

    fetch(NEWS_ENDPOINT)
        .then(r => {
            if (!r.ok) throw new Error("Backend returned " + r.status);
            return r.json();
        })
        .then(posts => {
            newsCache = posts;
            renderNewsList(posts);
            // If there's a #post-123 anchor, scroll to + open it.
            maybeOpenFromHash();
        })
        .catch(err => {
            list.innerHTML = `
                <div class="news-error">
                    <h3>Couldn't load articles</h3>
                    <p>Couldn't reach the server. Please try again later.</p>
                </div>
            `;
            console.warn("[news] page load failed:", err);
        });
}

function renderNewsList(posts) {
    const list = document.getElementById("news-list");
    if (!list) return;

    const filtered = activeCategory === "all"
        ? posts
        : posts.filter(p => (p.category || "").toLowerCase() === activeCategory);

    if (!filtered.length) {
        list.innerHTML = `
            <div class="news-empty">
                <h3>No articles in this category yet.</h3>
                <p>Try a different filter.</p>
            </div>
        `;
        return;
    }

    list.innerHTML = filtered.map(p => newsItemHtml(p)).join("");

    // wire up the card click → modal
    list.querySelectorAll("[data-article-id]").forEach(el => {
        el.addEventListener("click", (ev) => {
            ev.preventDefault();
            const id = Number(el.getAttribute("data-article-id"));
            const found = (newsCache || []).find(p => p.id === id);
            if (found) openArticleModal(found);
        });
    });
}

function newsItemHtml(p) {
    const date = formatDate(p.postedAt);
    const icon = p.icon || "📰";
    const cat = escapeHtml(p.category || "news");
    return `
        <article class="news-item" id="post-${p.id}" data-article-id="${p.id}">
            <div class="news-item-icon" aria-hidden="true">${icon}</div>
            <div class="news-item-body">
                <div class="news-item-meta">
                    <span class="news-item-cat">${cat}</span>
                    <span class="news-item-date">${escapeHtml(date)}</span>
                </div>
                <h2 class="news-item-title">${escapeHtml(p.title)}</h2>
                <p class="news-item-summary">${escapeHtml(p.summary)}</p>
                <span class="news-item-more">Read full article →</span>
            </div>
        </article>
    `;
}

/* --------- Article modal (news.html) --------- */

function openArticleModal(post) {
    const modal = document.getElementById("article-modal");
    if (!modal) return;

    document.getElementById("article-category").textContent = post.category || "news";
    document.getElementById("article-title").textContent    = post.title || "";
    document.getElementById("article-date").textContent     = formatDate(post.postedAt);
    document.getElementById("article-source").textContent   = post.source || "—";
    document.getElementById("article-body").innerHTML       = bodyToHtml(post.body);

    const link = document.getElementById("article-source-link");
    if (link) {
        if (post.sourceUrl) {
            link.href = post.sourceUrl;
            link.hidden = false;
        } else {
            link.hidden = true;
        }
    }

    modal.classList.add("open");
    modal.setAttribute("aria-hidden", "false");
}

function closeArticleModal() {
    const modal = document.getElementById("article-modal");
    if (!modal) return;
    modal.classList.remove("open");
    modal.setAttribute("aria-hidden", "true");
}

function wireArticleModal() {
    document.querySelectorAll("[data-close-article]").forEach(el => {
        el.addEventListener("click", closeArticleModal);
    });
    document.addEventListener("keydown", (ev) => {
        if (ev.key === "Escape") closeArticleModal();
    });
}

function maybeOpenFromHash() {
    if (!location.hash || !location.hash.startsWith("#post-")) return;
    const id = Number(location.hash.replace("#post-", ""));
    const found = (newsCache || []).find(p => p.id === id);
    if (found) {
        // smooth scroll to the card, then open it
        const el = document.getElementById("post-" + id);
        if (el) el.scrollIntoView({ behavior: "smooth", block: "center" });
        openArticleModal(found);
    }
}

/* --------- Filter chips (news.html) --------- */

function wireCategoryChips() {
    const filter = document.getElementById("news-filter");
    if (!filter) return;
    filter.addEventListener("click", (ev) => {
        const btn = ev.target.closest(".chip");
        if (!btn) return;
        filter.querySelectorAll(".chip").forEach(b => b.classList.remove("active"));
        btn.classList.add("active");
        activeCategory = btn.getAttribute("data-cat") || "all";
        if (newsCache) renderNewsList(newsCache);
    });
}

/* --------- Boot --------- */

document.addEventListener("DOMContentLoaded", () => {
    // Draw user chip if present + session exists
    if (typeof renderUserChip === "function") renderUserChip();

    // Home page wiring
    if (document.getElementById("home-news-grid")) {
        loadHomePreview();
        loadHomeHeroStats();
    }

    // News page wiring
    if (document.getElementById("news-list")) {
        wireArticleModal();
        wireCategoryChips();
        loadNewsPage();
    }
});
