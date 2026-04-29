// Clean Our Sea — Quizzes listing page.
// Calls GET /user-quiz/stats/{userId} to fill the four stat cards in the
// "Your Progress" section, then GET /quiz to render the quiz cards into the
// grid. Filters by difficulty, searches by title/description, and navigates
// to quiz-page.html with the backend quiz id + title when Start is clicked.

const BACKEND = 'https://clean-our-sea-backend.onrender.com';

// Use the logged-in user's id when available; falls back to 1 for guests so
// the demo stats panel still renders without a session. auth.js exposes
// window.getUserId() with the same fallback shape.
function currentUserId()
{
    if (typeof getUserId === 'function')        return getUserId();
    if (window.auth && window.auth.getCurrentUserId)
    {
        const id = window.auth.getCurrentUserId();
        if (id != null) return id;
    }
    return 1;
}

// maps quiz title keywords to an emoji icon
const ICONS = {
    'ocean basics': '🌊',
    'plastic':      '♻️',
    'biodiversity': '🐠',
    'climate':      '🌡️',
    'endangered':   '🐢',
    'beach':        '🏖️',
};

function getIcon(title)
{
    const key = Object.keys(ICONS).find(k => title.toLowerCase().includes(k));
    return key ? ICONS[key] : '🌍';
}

function diffClass(difficulty)
{
    const d = (difficulty || '').toLowerCase();
    if (d === 'easy')   return 'difficulty-easy';
    if (d === 'medium') return 'difficulty-medium';
    return 'difficulty-hard';
}

// stats — calls GET /user-quiz/stats/{userId} and updates the stat cards
async function loadStats()
{
    try
    {
        const userId = currentUserId();
        const res    = await fetch(`${BACKEND}/user-quiz/stats/${userId}`);
        if (!res.ok) return;
        const stats = await res.json();

        const completedEl = document.getElementById('stat-completed');
        const avgEl       = document.getElementById('stat-avg');
        const pointsEl    = document.getElementById('stat-points');

        if (completedEl) completedEl.textContent = stats.quizzesCompleted;
        if (avgEl)       avgEl.textContent       = stats.averageScore + 'pts avg';
        if (pointsEl)    pointsEl.textContent    = stats.totalPoints;
    }
    catch (err)
    {
        // stats just stay as dashes if the backend is not running
        console.warn('Could not load stats:', err);
    }
}

// cards — calls GET /quiz and builds a card for each quiz returned
async function loadQuizzes()
{
    try
    {
        const res = await fetch(`${BACKEND}/quiz`);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const quizzes = await res.json();
        renderCards(quizzes);
    }
    catch (err)
    {
        console.error('Could not load quizzes:', err);
        const grid = document.getElementById('quiz-grid');
        if (grid)
        {
            grid.innerHTML = `
                <div style="grid-column:1/-1;text-align:center;padding:40px;color:#9aa4b2;">
                    ⚠️ Could not connect to backend.
                    Make sure Spring Boot is running on <strong>https://clean-our-sea-backend.onrender.com</strong>.
                </div>`;
        }
    }
}

function renderCards(quizzes)
{
    const grid = document.getElementById('quiz-grid');
    if (!grid) return;
    grid.innerHTML = '';

    quizzes.forEach(quiz =>
    {
        const dc   = diffClass(quiz.difficulty);
        const icon = getIcon(quiz.title);

        const card = document.createElement('div');
        card.className          = 'quiz-card';
        card.dataset.quizId     = quiz.quizId;
        card.dataset.difficulty = (quiz.difficulty || '').toLowerCase();

        card.innerHTML = `
            <div class="quiz-icon">${icon}</div>
            <h3 class="quiz-title">${quiz.title}</h3>
            <p class="quiz-description">${quiz.description}</p>
            <div class="quiz-meta">
                <span class="quiz-difficulty ${dc}">${quiz.difficulty}</span>
                <span class="quiz-questions">${quiz.totalQuestions} Questions</span>
            </div>
            <button class="start-btn">Start Quiz</button>
        `;

        grid.appendChild(card);
    });

    // attach filter, search and start-button listeners after the cards exist
    attachListeners();
}

function attachListeners()
{
    const cards    = document.querySelectorAll('.quiz-card');
    const search   = document.getElementById('quiz-search');
    const dropdown = document.getElementById('difficulty-filter');
    const counter  = document.getElementById('quiz-counter');

    function applyFilters()
    {
        const term = (search ? search.value : '').toLowerCase();
        const diff = (dropdown ? dropdown.value : 'all').toLowerCase();
        let visible = 0;

        cards.forEach(card =>
        {
            const title    = card.querySelector('.quiz-title').textContent.toLowerCase();
            const desc     = card.querySelector('.quiz-description').textContent.toLowerCase();
            const cardDiff = card.dataset.difficulty;

            const matchSearch = !term || title.includes(term) || desc.includes(term);
            const matchDiff   = diff === 'all' || cardDiff === diff;
            const show        = matchSearch && matchDiff;

            card.style.display = show ? 'block' : 'none';
            if (show) visible++;
        });

        if (counter) counter.textContent = visible;
    }

    if (search)   search.addEventListener('input',   applyFilters);
    if (dropdown) dropdown.addEventListener('change', applyFilters);

    // Start button → open quiz-page.html with the backend quiz id + title.
    cards.forEach(card =>
    {
        const button = card.querySelector('.start-btn');
        if (!button) return;
        button.addEventListener('click', function (e)
        {
            e.stopPropagation();
            const quizId   = card.dataset.quizId;
            const quizName = card.querySelector('.quiz-title').textContent.trim();

            if (!quizId)
            {
                alert('This quiz is not available — the backend may be offline.');
                return;
            }

            window.location.href =
                `quiz-page.html?id=${encodeURIComponent(quizId)}&title=${encodeURIComponent(quizName)}`;
        });
    });

    applyFilters();
}

loadStats();
loadQuizzes();
