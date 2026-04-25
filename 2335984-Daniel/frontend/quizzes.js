// talks to Spring Boot on port 8080
const API = 'http://localhost:8080';

// hardcoded user id for now — replace this when login is added
const USER_ID = 1;

//maps quiz title keywords to an emoji icon and difficulty CSS class
const ICONS = {
    'ocean basics': { icon: '🌊' },
    'plastic':      { icon: '♻️' },
    'biodiversity': { icon: '🐠' },
    'climate':      { icon: '🌡️' },
    'endangered':   { icon: '🐢' },
    'beach':        { icon: '🏖️' },
};

function getIcon(title) 
{
    const key = Object.keys(ICONS).find(k => title.toLowerCase().includes(k));
    return key ? ICONS[key].icon : '🌍';
}

function diffClass(difficulty) 
{
    const d = difficulty.toLowerCase();
    if (d === 'easy')   return 'difficulty-easy';
    if (d === 'medium') return 'difficulty-medium';
    return 'difficulty-hard';
}

// stats
//calls GET /user-quiz/stats/1 and fills in the four stat cards
async function loadStats() 
{
    try 
    {
        const res   = await fetch(`${API}/user-quiz/stats/${USER_ID}`);
        const stats = await res.json();

        // update the DOM elements with real data from the database
        document.getElementById('stat-completed').textContent = stats.quizzesCompleted;
        document.getElementById('stat-avg').textContent       = stats.averageScore + ' pts avg';
        document.getElementById('stat-points').textContent    = stats.totalPoints;

        // badges stay static for now until an achievements table is added
    } 
    catch (err) 
    {
        // if backend is down, the page still works — stats just show dashes
        console.warn('Could not load stats from backend:', err);
    }
}

// cards
// calls GET /quiz and builds a card for each quiz returned
async function loadQuizzes() 
{
    try 
    {
        const res     = await fetch(`${API}/quiz`);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const quizzes = await res.json();

        renderCards(quizzes);
    } 
    catch (err) 
    {
        console.error('Could not load quizzes from backend:', err);
        document.getElementById('quiz-grid').innerHTML = `
            <div style="grid-column:1/-1;text-align:center;padding:40px;color:#9aa4b2;">
                ⚠️ Could not connect to backend.
                Make sure Spring Boot is running on <strong>http://localhost:8080</strong>.
            </div>`;
    }
}

function renderCards(quizzes) 
{
    const grid = document.getElementById('quiz-grid');
    grid.innerHTML = '';

    quizzes.forEach(quiz => 
    {
        const dc   = diffClass(quiz.difficulty);
        const icon = getIcon(quiz.title);

        const card = document.createElement('div');
        card.className            = 'quiz-card';
        card.dataset.quizId       = quiz.quizId;
        card.dataset.difficulty   = quiz.difficulty.toLowerCase();

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

    // attach filter and search listeners after the cards are in the DOM
    attachListeners();
}

// filters and searches
function attachListeners() 
{
    const cards    = document.querySelectorAll('.quiz-card');
    const search   = document.getElementById('quiz-search');
    const dropdown = document.getElementById('difficulty-filter');

    function applyFilters() 
    {
        const term = search.value.toLowerCase();
        const diff = dropdown.value.toLowerCase();

        cards.forEach(card => 
        {
            const title    = card.querySelector('.quiz-title').textContent.toLowerCase();
            const desc     = card.querySelector('.quiz-description').textContent.toLowerCase();
            const cardDiff = card.dataset.difficulty;

            const matchSearch = title.includes(term) || desc.includes(term);
            const matchDiff   = diff === 'all' || cardDiff === diff;

            card.style.display = matchSearch && matchDiff ? 'block' : 'none';
        });
    }

    search.addEventListener('input', applyFilters);
    dropdown.addEventListener('change', applyFilters);

    // clicking start quiz sends the user to the quiz page with the id in the URL
    document.querySelectorAll('.start-btn').forEach(btn => 
    {
        btn.addEventListener('click', function (e) 
        {
            e.stopPropagation();
            const card   = this.closest('.quiz-card');
            const quizId = card.dataset.quizId;
            const title  = encodeURIComponent(card.querySelector('.quiz-title').textContent);
            window.location.href = `quiz-page.html?id=${quizId}&title=${title}`;
        });
    });
}

// runs both fetches when the page finishes loading
document.addEventListener('DOMContentLoaded', () => 
{
    loadStats();
    loadQuizzes();
});
