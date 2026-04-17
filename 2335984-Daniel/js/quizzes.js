const searchInput = document.getElementById('quiz-search');
const filterDropdown = document.getElementById('difficulty-filter');
const quizCards = document.querySelectorAll('.quiz-card');
searchInput.addEventListener('input', function() {
  const searchTerm = this.value.toLowerCase();
  quizCards.forEach(card => {
    const title = card.querySelector('.quiz-title').textContent.toLowerCase();
    const desc = card.querySelector('.quiz-description').textContent.toLowerCase();
    card.style.display = (title.includes(searchTerm) || desc.includes(searchTerm)) ? 'block' : 'none';
  });
});
filterDropdown.addEventListener('change', function() {
  const sel = this.value;
  quizCards.forEach(card => {
    const diff = card.querySelector('.quiz-difficulty').textContent.toLowerCase();
    card.style.display = (sel === 'all' || diff === sel) ? 'block' : 'none';
  });
});
document.querySelectorAll('.start-btn').forEach(button => {
  button.addEventListener('click', function(e) {
    e.stopPropagation();
    const title = this.closest('.quiz-card').querySelector('.quiz-title').textContent;
    alert('Starting quiz: ' + title + '\n\nQuiz page coming soon!');
  });
});
