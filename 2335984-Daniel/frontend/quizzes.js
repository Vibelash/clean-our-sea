// Search functionality - filters quizzes as you type
const searchInput = document.getElementById('quiz-search');

searchInput.addEventListener('input', function() 
{
  const searchTerm = this.value.toLowerCase();
  let visibleCount = 0;
  
  quizCards.forEach(card => 
    {
    const title = card.querySelector('.quiz-title').textContent.toLowerCase();
    const description = card.querySelector('.quiz-description').textContent.toLowerCase();
    
    // check if search term is in title or description
    if (title.includes(searchTerm) || description.includes(searchTerm)) 
      {
      card.style.display = 'block';
      visibleCount++;
    } 
    else 
    {
      card.style.display = 'none';
    }
  });
  
  // update counter
  quizCounter.textContent = visibleCount;
});
// filter function that allows users to sort quizzes by difficulty
const filterDropdown = document.getElementById('difficulty-filter');
const quizCards = document.querySelectorAll('.quiz-card');

//listens for when the users changers the dropdown choice
filterDropdown.addEventListener('change', function() 
{
  const selectedDifficulty = this.value;
  
  quizCards.forEach(card => //loops through each card to check if it matches filer
    { 
    const difficulty = card.querySelector('.quiz-difficulty').textContent.toLowerCase();
    
    if (selectedDifficulty === 'all') 
    {
      card.style.display = 'block';
    } 

      else if (difficulty === selectedDifficulty)
      {
      card.style.display = 'block';
      } 
   // hide cards that don't match
        else 
        {
      card.style.display = 'none';
        }
  }); 
});


//this handles clicking the start button for quizzes
document.querySelectorAll('.start-btn').forEach(button => 
  {
  button.addEventListener('click', function(e) 
  { 
    e.stopPropagation();
    const card = this.closest('.quiz-card');
    const quizTitle = card.querySelector('.quiz-title').textContent;
    //shows alert with quiz name and will open another quiz page when back end is fully integrated
    alert(`Starting quiz: ${quizTitle}\n\nIn progress quiz page with questions`);
  });
});



