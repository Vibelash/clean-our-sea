const API    = 'https://clean-our-sea-backend.onrender.com';
const params = new URLSearchParams(window.location.search);
const QUIZ_ID = params.get('id');
const TITLE   = decodeURIComponent(params.get('title') || 'Quiz');
// Use the logged-in user's id when available; falls back to 1 for guests
// so the demo quiz still runs without a session.
function currentUserId() {
  if (typeof getUserId === 'function')        return getUserId();
  if (window.auth && window.auth.getCurrentUserId) {
    const id = window.auth.getCurrentUserId();
    if (id != null) return id;
  }
  return 1;
}

let questions   = [];
let current     = 0;
let selected    = null;
let userAnswers = [];

// refs dom elements
const loadingScreen    = document.getElementById('loading-screen');
const quizScreen       = document.getElementById('quiz-screen');
const resultsScreen    = document.getElementById('results-screen');
const titleLabel       = document.getElementById('quiz-title-label');
const progressLabel    = document.getElementById('progress-label');
const progressBar      = document.getElementById('progress-bar');
const typeBadge        = document.getElementById('question-type-badge');
const questionText     = document.getElementById('question-text');
const optionsContainer = document.getElementById('options-container');
const nextBtn          = document.getElementById('next-btn');

function show(el) { el.classList.remove('hidden'); }
function hide(el) { el.classList.add('hidden'); }

// fetches questions from GET /questions/quiz/{id} 
async function init() {
  if (!QUIZ_ID) { window.location.href = 'quizzes.html'; return; }

  titleLabel.textContent = TITLE;
  document.title = `${TITLE} - Clean Our Sea`;

  try {
    const res = await fetch(`${API}/questions/quiz/${QUIZ_ID}`);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    questions = await res.json();

    hide(loadingScreen);
    show(quizScreen);
    renderQuestion();
  } catch (err) {
    loadingScreen.innerHTML = `
      <p style="color:#e74c3c;font-size:1.05rem;text-align:center">
        ⚠️ Could not load questions.<br>
        Make sure Spring Boot is running on <strong>port 8080</strong>.
      </p>
      <a href="quizzes.html" style="display:inline-block;margin-top:20px;color:#3b82f6;">← Back to Quizzes</a>
    `;
  }
}

// renders the current question
function renderQuestion() {
  const q     = questions[current];
  const total = questions.length;

  progressLabel.textContent = `Question ${current + 1} of ${total}`;
  progressBar.style.width   = `${(current / total) * 100}%`;

  typeBadge.textContent = q.type === 'TRUE_FALSE' ? 'True / False' : 'Multiple Choice';
  typeBadge.className   = `type-badge ${q.type === 'TRUE_FALSE' ? 'badge-tf' : 'badge-mc'}`;

  questionText.textContent = q.questionText;

  optionsContainer.innerHTML = '';
  selected = null;
  nextBtn.disabled = true;

  buildOptions(q).forEach(({ letter, text }) => {
    if (!text) return;
    const btn = document.createElement('button');
    btn.className    = 'option-btn';
    btn.dataset.letter = letter;
    btn.innerHTML    = `<span class="option-letter">${letter}</span><span class="option-text">${text}</span>`;
    btn.addEventListener('click', () => selectOption(btn, letter, q.correctAnswer));
    optionsContainer.appendChild(btn);
  });
}

function buildOptions(q) {
  return [
    { letter: 'A', text: q.optionA },
    { letter: 'B', text: q.optionB },
    { letter: 'C', text: q.optionC },
    { letter: 'D', text: q.optionD },
  ];
}

// handles answer selecion
function selectOption(btn, letter, correct) {
  if (selected !== null) return;
  selected = letter;
  nextBtn.disabled = false;

  optionsContainer.querySelectorAll('.option-btn').forEach(b => {
    b.disabled = true;
    if (b.dataset.letter === correct)                       b.classList.add('correct');
    else if (b.dataset.letter === letter && letter !== correct) b.classList.add('wrong');
  });
}

// next/finish
nextBtn.addEventListener('click', () => {
  const q = questions[current];
  userAnswers.push({
    questionText: q.questionText,
    selected,
    correct:   q.correctAnswer,
    isCorrect: selected === q.correctAnswer,
    options:   buildOptions(q),
  });

  current++;
  if (current < questions.length) {
    renderQuestion();
  } else {
    showResults();
  }
});

// results screen
async function showResults() {
  hide(quizScreen);
  show(resultsScreen);

  const score = userAnswers.filter(a => a.isCorrect).length;
  const total = questions.length;
  const pct   = Math.round((score / total) * 100);

  document.getElementById('result-emoji').textContent    = pct >= 80 ? '🎉' : pct >= 50 ? '👍' : '💪';
  document.getElementById('result-title').textContent    = pct >= 80 ? 'Excellent Work!' : pct >= 50 ? 'Good Effort!' : 'Keep Practising!';
  document.getElementById('result-subtitle').textContent = `You scored ${score} out of ${total} — ${pct}%`;
  document.getElementById('score-number').textContent    = score;
  document.getElementById('score-denom').textContent     = `/ ${total}`;
  document.getElementById('score-percent').textContent   = `${pct}%`;

  // Animated SVG ring
  const circ = 2 * Math.PI * 52;
  const ring  = document.getElementById('ring-fill');
  ring.style.strokeDasharray  = circ;
  ring.style.strokeDashoffset = circ;
  ring.style.stroke = pct >= 80 ? '#22c55e' : pct >= 50 ? '#f59e0b' : '#ef4444';
  setTimeout(() => { ring.style.strokeDashoffset = circ - (pct / 100) * circ; }, 100);

  // Question breakdown
  const breakdown = document.getElementById('breakdown');
  breakdown.innerHTML = '<h3 class="breakdown-title">Question Breakdown</h3>';

  userAnswers.forEach((a, i) => {
    const selOpt  = a.options.find(o => o.letter === a.selected);
    const corrOpt = a.options.find(o => o.letter === a.correct);

    const item = document.createElement('div');
    item.className = `breakdown-item ${a.isCorrect ? 'correct-item' : 'wrong-item'}`;
    item.innerHTML = `
      <div class="bd-num">${i + 1}</div>
      <div class="bd-body">
        <p class="bd-q">${a.questionText}</p>
        ${!a.isCorrect ? `<p class="bd-your">Your answer: <strong>${selOpt?.text ?? a.selected}</strong></p>` : ''}
        <p class="bd-correct ${a.isCorrect ? 'green' : 'red'}">
          ${a.isCorrect ? '✓' : '✗ Correct:'} <strong>${a.isCorrect ? selOpt?.text : corrOpt?.text}</strong>
        </p>
      </div>
    `;
    breakdown.appendChild(item);
  });

  // POST score to POST /user-quiz
  try {
    await fetch(`${API}/user-quiz`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: currentUserId(), quizId: parseInt(QUIZ_ID), score }),
    });
    console.log(`Score saved: ${score}/${total}`);
  } catch (err) {
    console.warn('Could not save score to backend:', err);
  }
}

init();
