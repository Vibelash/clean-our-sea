// Game variables
const grid = document.getElementById('grid');
const scoreEl = document.getElementById('score');
const trashEl = document.getElementById('trash');
const lengthEl = document.getElementById('length');
const messageEl = document.getElementById('message');
const startBtn = document.getElementById('start-btn');
const resetBtn = document.getElementById('reset-btn');

const size = 6;
let snake = [{x:2,y:2},{x:2,y:1},{x:2,y:0}];
let trash = [];
let dir = 'right';
let nextDir = 'right';
let score = 0;
let trashCount = 0;
let gameRunning = false;
let gameLoop;

// Create 6x6 grid
for(let i = 0; i < size * size; i++){
    const cell = document.createElement('div');
    cell.className = 'cell';
    grid.appendChild(cell);
}

// Start game
function startGame(){
    if(gameRunning) return;
    gameRunning = true;
    messageEl.textContent = "Game running! Use arrow keys";
    if(trash.length === 0) generateTrash();
    gameLoop = setInterval(update, 250);
    startBtn.disabled = true;
}

// Reset game
function resetGame(){
    clearInterval(gameLoop);
    snake = [{x:2,y:2},{x:2,y:1},{x:2,y:0}];
    trash = [];
    dir = 'right';
    nextDir = 'right';
    score = 0;
    trashCount = 0;
    gameRunning = false;
    messageEl.textContent = "Press arrow keys to start";
    startBtn.disabled = false;
    updateDisplay();
    generateTrash();
    draw();
}

// Generate trash
function generateTrash(){
    let x, y;
    do {
        x = Math.floor(Math.random() * size);
        y = Math.floor(Math.random() * size);
    } while(snake.some(s => s.x === x && s.y === y) || trash.some(t => t.x === x && t.y === y));
    trash.push({x, y});
}

// Update game
function update(){
    dir = nextDir;
    const head = {...snake[0]};
    
    // Move head
    if(dir === 'right') head.y = (head.y + 1) % size;
    if(dir === 'left') head.y = (head.y - 1 + size) % size;
    if(dir === 'down') head.x = (head.x + 1) % size;
    if(dir === 'up') head.x = (head.x - 1 + size) % size;
    
    // Check collision with self
    if(snake.slice(1).some(s => s.x === head.x && s.y === head.y)){
        gameOver();
        return;
    }
    
    snake.unshift(head);
    
    // Check if ate trash
    const trashIndex = trash.findIndex(t => t.x === head.x && t.y === head.y);
    if(trashIndex > -1){
        trash.splice(trashIndex, 1);
        score += 10;
        trashCount++;
        generateTrash();
        messageEl.textContent = "Good! You ate trash!";
    } else {
        snake.pop();
    }
    
    updateDisplay();
    draw();
}

// Game over
function gameOver(){
    clearInterval(gameLoop);
    gameRunning = false;
    messageEl.textContent = `Game Over! Final Score: ${score}`;
    startBtn.disabled = false;
}

// Update display
function updateDisplay(){
    scoreEl.textContent = score;
    trashEl.textContent = trashCount;
    lengthEl.textContent = snake.length;
}

// Draw game
function draw(){
    const cells = document.querySelectorAll('.cell');
    cells.forEach(cell => {
        cell.className = 'cell';
    });
    
    // Draw snake
    snake.forEach((seg, i) => {
        const index = seg.x * size + seg.y;
        cells[index].classList.add(i === 0 ? 'snake-head' : 'snake-body');
    });
    
    // Draw trash
    trash.forEach(t => {
        const index = t.x * size + t.y;
        cells[index].classList.add('trash');
    });
}

// Controls
document.addEventListener('keydown', (e) => {
    // Direction controls
    if(['ArrowUp', 'w', 'W'].includes(e.key) && dir !== 'down') nextDir = 'up';
    if(['ArrowDown', 's', 'S'].includes(e.key) && dir !== 'up') nextDir = 'down';
    if(['ArrowLeft', 'a', 'A'].includes(e.key) && dir !== 'right') nextDir = 'left';
    if(['ArrowRight', 'd', 'D'].includes(e.key) && dir !== 'left') nextDir = 'right';
    
    // Space to pause/resume
    if(e.key === ' ') {
        e.preventDefault();
        if(gameRunning){
            clearInterval(gameLoop);
            messageEl.textContent = "Game Paused. Press Space to resume.";
            gameRunning = false;
            startBtn.disabled = false;
        } else if(!gameRunning && snake.length > 0){
            startGame();
        }
    }
    
    // R to restart
    if(e.key === 'r' || e.key === 'R') resetGame();
    
    // Start game with first arrow key press
    if(!gameRunning && ['ArrowUp','ArrowDown','ArrowLeft','ArrowRight','w','a','s','d'].includes(e.key)){
        startGame();
    }
});

// Button events
startBtn.addEventListener('click', startGame);
resetBtn.addEventListener('click', resetGame);

// Initialize game
resetGame();