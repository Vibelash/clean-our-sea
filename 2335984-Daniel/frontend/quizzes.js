// Clean Our Sea — Quizzes listing page.
// Filters by difficulty, searches by title/description, and navigates
// to quiz-page.html with the backend quiz id + title when Start is clicked.

const BACKEND       = 'http://localhost:8080';
const quizCards     = document.querySelectorAll('.quiz-card');
const quizCounter   = document.getElementById('quiz-counter');
const searchInput   = document.getElementById('quiz-search');
const filterDropdown = document.getElementById('difficulty-filter');

let activeDifficulty = 'all';
let activeSearch     = '';

function applyFilters()
{
  let visible = 0;

  quizCards.forEach(card =>
  {
    const title       = card.querySelector('.quiz-title').textContent.toLowerCase();
    const description = card.querySelector('.quiz-description').textContent.toLowerCase();
    const difficulty  = card.querySelector('.quiz-difficulty').textContent.toLowerCase();

    const matchesDifficulty = activeDifficulty === 'all' || difficulty === activeDifficulty;
    const matchesSearch     = !activeSearch
      || title.includes(activeSearch)
      || description.includes(activeSearch);

    if (matchesDifficulty && matchesSearch)
    {
      card.style.display = 'block';
      visible++;
    }
    else
    {
      card.style.display = 'none';
    }
  });

  if (quizCounter) quizCounter.textContent = visible;
}

searchInput.addEventListener('input', function ()
{
  activeSearch = this.value.toLowerCase();
  applyFilters();
});

filterDropdown.addEventListener('change', function ()
{
  activeDifficulty = this.value;
  applyFilters();
});

// Start button → open quiz-page.html with the backend quiz id + title.
document.querySelectorAll('.start-btn').forEach(button =>
{
  button.addEventListener('click', function (e)
  {
    e.stopPropagation();
    const card     = this.closest('.quiz-card');
    const quizId   = card.getAttribute('data-quiz-id');
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

// H2 persists the auto-increment sequence across restarts, so the quiz IDs
// in the DB drift over time (1..6, 7..12, 13..18, ...). Hardcoded data-quiz-id
// values would stop matching real rows. On load, fetch the real list from the
// backend and remap each card by title.
fetch(`${BACKEND}/quiz`)
  .then(r => r.ok ? r.json() : [])
  .then(list =>
  {
    const byTitle = {};
    for (const q of list) byTitle[q.title] = q.quizId;

    quizCards.forEach(card =>
    {
      const title = card.querySelector('.quiz-title').textContent.trim();
      if (byTitle[title] != null)
      {
        card.setAttribute('data-quiz-id', byTitle[title]);
      }
      else
      {
        // No matching quiz in the backend — disable the start button.
        card.removeAttribute('data-quiz-id');
      }
    });
  })
  .catch(err =>
  {
    console.warn('Could not fetch quiz list from backend:', err);
  });

applyFilters();
