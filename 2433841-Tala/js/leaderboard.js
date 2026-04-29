let weeklyQuota = 0;
let weeklyPoints = 0;
let loggedInUserId = null;

const loggedInUser = "Tala";

let currentMode = "daily";

const BACKEND = "https://clean-our-sea-backend.onrender.com";
async function loadLeaderboard() {
    try {
        const selectedCountry = document.getElementById('country-filter').value;

        let url = "";

        if (currentMode === "daily") {
            url = `${BACKEND}/leaderboard/weekly?country=${selectedCountry}`;
        } else {
            url = `${BACKEND}/leaderboard?country=${selectedCountry}`;
        }

        const response = await fetch(url);
        const data = await response.json();

        populateLeaderboard(data);

    } catch (error) {
        console.error("Error loading leaderboard:", error);
    }
}

function populateLeaderboard(data) {
    const currentUserData = data.find(user => user.username === loggedInUser );//to find the logged in users data

     if (currentUserData) {
        loggedInUserId = currentUserData.id;
        weeklyPoints = currentUserData.weeklyPoints;
        weeklyQuota = currentUserData.weeklyGoal;

        document.getElementById('quota-value').textContent = weeklyQuota;
        document.getElementById('weekly-points').textContent = weeklyPoints;

        updateProgressBar();
    } else {
        console.warn("Logged-in user not found in leaderboard data");
    }
    
    const leaderboardBody = document.getElementById('leaderboard-body');
    const topThreeContainer = document.getElementById('top-three');
    leaderboardBody.innerHTML = '';
    topThreeContainer.innerHTML = '';

    
    const sortedData = data.sort((a,b) => b.totalScore - a.totalScore);

 
    const topThree = sortedData.slice(0, 3);
    const restOfTheData = sortedData.slice(3);


   topThree.forEach((user, index) => {
    const crown = index === 0 ? '🥇' : (index === 1 ? '🥈' : '🥉');
    const contributorDiv = document.createElement('div');
    contributorDiv.className = 'top-contributor';

    if (user.username === loggedInUser) {
        contributorDiv.classList.add('highlight-user');
    }

    contributorDiv.innerHTML = `
        <div>${crown} ${user.username}</div>
        <div>Followers: ${user.followers}</div>
        <div>Points: ${user.totalScore}</div>
    `;

    topThreeContainer.appendChild(contributorDiv);
});

    
   restOfTheData.forEach((user, index) => {
    const row = document.createElement('tr');

    if (user.username === loggedInUser) {
        row.classList.add('highlight-user-row');
    }

    row.innerHTML = `
        <td>${index + 4}</td>
        <td>${user.username}</td>
        <td>${user.followers}</td>
        <td>${user.totalScore}</td>
    `;

    leaderboardBody.appendChild(row);
});
}


let countdownInterval;
let countdownSeconds = 3600; 

function formatTime(seconds) {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const remainingSeconds = seconds % 60;
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
}

function startCountdown() {
    clearInterval(countdownInterval);

    countdownInterval = setInterval(() => {
        countdownSeconds--;
        document.getElementById('countdown').textContent = formatTime(countdownSeconds);

        if (countdownSeconds === 0) {
            countdownSeconds = 3600;
            loadLeaderboard();
        }
    }, 1000);
}


const dailyTab = document.getElementById('daily-tab');
const yearlyTab = document.getElementById('yearly-tab');

dailyTab.addEventListener('click', () => {
    currentMode = "daily";
    dailyTab.classList.add('active');
    yearlyTab.classList.remove('active');
    loadLeaderboard();
});

yearlyTab.addEventListener('click', () => {
    currentMode = "yearly";
    yearlyTab.classList.add('active');
    dailyTab.classList.remove('active');
    loadLeaderboard();
    
});


document.getElementById('country-filter').addEventListener('change', () => {
    loadLeaderboard();
});




document.getElementById('set-quota-btn').addEventListener('click', async () => {

    const inputValue = parseInt(document.getElementById('quota-input-field').value);

    if (isNaN(inputValue) || inputValue <= 0) {
        alert("Please enter a valid quota.");
        return;
    }
     if (loggedInUserId === null) {
        alert("User not found yet. Wait for the leaderboard to load, then try again.");
        return;
    }

    weeklyQuota = inputValue;
    document.getElementById('quota-value').textContent = weeklyQuota;

    updateProgressBar();
     try {
        const response = await fetch(`${BACKEND}/leaderboard/${loggedInUserId}/weekly-goal`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ weeklyGoal: inputValue })
        });

        if (!response.ok) {
            throw new Error(`Server returned ${response.status}`);
        }

        const data = await response.json();
        weeklyQuota = data.weeklyGoal;
        weeklyPoints = data.weeklyPoints;
        document.getElementById('quota-value').textContent = weeklyQuota;
        document.getElementById('weekly-points').textContent = weeklyPoints;
        updateProgressBar();

    } catch (err) {
        console.error("Failed to save weekly goal:", err);
        alert("Could not save your goal — check the backend is running on localhost:8080.");
    }
});

function updateProgressBar() {

    document.getElementById('weekly-points').textContent = weeklyPoints;

    const percentage = weeklyQuota === 0 ? 0 : Math.min((weeklyPoints / weeklyQuota) * 100, 100);

    document.getElementById('progress-bar').style.width = percentage + "%";
}

updateProgressBar();
loadLeaderboard();
startCountdown();


populateLeaderboard(dailyData);
startCountdown();
