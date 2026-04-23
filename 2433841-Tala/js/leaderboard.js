let weeklyQuota = 0;
let weeklyPoints = 10; // temporary demo value

const dailyData = [
    { name: "Alice", followers: 59, points: 100, country: "London" },
    { name: "Bob", followers: 33, points: 250, country: "Bristol" },
    { name: "Charlie", followers: 22, points: 219, country: "Southampton" },
    { name: "Diana", followers: 50, points: 113, country: "London" },
    { name: "Ahmed", followers: 70, points: 113, country: "Bristol" },
    { name: "Nay", followers: 53, points: 113, country: "Southampton" },
    { name: "Chizzy", followers: 39, points: 113, country: "London" },
    { name: "Michelle", followers: 50, points: 113, country: "Bristol" },
    { name: "Qassim", followers: 25, points: 113, country: "Southampton" },
    { name: "Jeremy", followers: 87, points: 113, country: "London" },
    { name: "Amjad", followers: 98, points: 113, country: "Bristol" },
    { name: "Rania", followers: 22, points: 113, country: "Southampton" },
    { name: "Kadi", followers: 10, points: 113, country: "London" },
    { name: "Mustafa", followers: 180, points: 113, country: "Bristol" },
    { name: "Lara", followers: 99, points: 113, country: "Southampton" },
    { name: "Jojo", followers: 20, points: 113, country: "London" },
    { name: "MK", followers: 90, points: 113, country: "Bristol" },
    { name: "Tala", followers: 37, points: 113, country: "Southampton" },
    { name: "May", followers: 34, points: 498, country: "London" }
];

const yearlyData = [
    { name: "Henrietta", followers: 84, points: 1000, country: "London" },
    { name: "Darrel", followers: 38, points: 2500, country: "Bristol" },
    { name: "Jolie Joie", followers: 28, points: 1238, country: "Southampton" },
    { name: "Brian", followers: 12, points: 670, country: "London" },
    { name: "Alice", followers: 59, points: 100, country: "Bristol" },
    { name: "Bob", followers: 33, points: 250, country: "Southampton" },
    { name: "Charlie", followers: 22, points: 219, country: "London" },
    { name: "Diana", followers: 50, points: 113, country: "Bristol" },
    { name: "Ahmed", followers: 70, points: 113, country: "Southampton" },
    { name: "Nay", followers: 53, points: 113, country: "London" },
    { name: "Chizzy", followers: 39, points: 113, country: "Bristol" },
    { name: "Malik", followers: 50, points: 113, country: "Southampton" },
    { name: "Nadeen", followers: 25, points: 113, country: "London" },
    { name: "Luffy", followers: 87, points: 113, country: "Bristol" },
    { name: "Zoro", followers: 98, points: 113, country: "Southampton" },
    { name: "Nami", followers: 22, points: 113, country: "London" },
    { name: "Robin", followers: 10, points: 113, country: "Bristol" },
    { name: "Franky", followers: 180, points: 113, country: "Southampton" },
    { name: "Buggy", followers: 99, points: 113, country: "London" },
    { name: "Sanji", followers: 20, points: 113, country: "Bristol" },
    { name: "Bonclay", followers: 90, points: 113, country: "Southampton" },
    { name: "Brook", followers: 37, points: 113, country: "London" },
    { name: "David", followers: 67, points: 1507, country: "Bristol" }
];

// Simulated logged-in user
const loggedInUser = "Tala";

function populateLeaderboard(data) {
    const leaderboardBody = document.getElementById('leaderboard-body');
    const topThreeContainer = document.getElementById('top-three');
    leaderboardBody.innerHTML = '';
    topThreeContainer.innerHTML = '';

    const selectedCountry = document.getElementById('country-filter').value;

    
    const filteredData = selectedCountry === 'all' ? data : data.filter(user => user.country === selectedCountry);

    
    const sortedData = filteredData.sort((a, b) => b.points - a.points);

 
    const topThree = sortedData.slice(0, 3);
    const restOfTheData = sortedData.slice(3);


   topThree.forEach((user, index) => {
    const crown = index === 0 ? '🥇' : (index === 1 ? '🥈' : '🥉');
    const contributorDiv = document.createElement('div');
    contributorDiv.className = 'top-contributor';

    if (user.name === loggedInUser) {
        contributorDiv.classList.add('highlight-user');
    }

    contributorDiv.innerHTML = `
        <div>${crown} ${user.name}</div>
        <div>Followers: ${user.followers}</div>
        <div>Points: ${user.points}</div>
    `;

    topThreeContainer.appendChild(contributorDiv);
});

    
   restOfTheData.forEach((user, index) => {
    const row = document.createElement('tr');

    if (user.name === loggedInUser) {
        row.classList.add('highlight-user-row');
    }

    row.innerHTML = `
        <td>${index + 4}</td>
        <td>${user.name}</td>
        <td>${user.followers}</td>
        <td>${user.points}</td>
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
    countdownInterval = setInterval(() => {
        countdownSeconds--;
        document.getElementById('countdown').textContent = formatTime(countdownSeconds);

        if (countdownSeconds === 0) {
            clearInterval(countdownInterval);
            countdownSeconds = 3600;
            populateLeaderboard(dailyData);
        }
    }, 1000);
}


const dailyTab = document.getElementById('daily-tab');
const yearlyTab = document.getElementById('yearly-tab');

dailyTab.addEventListener('click', () => {
    dailyTab.classList.add('active');
    yearlyTab.classList.remove('active');
    populateLeaderboard(dailyData);
    startCountdown();
});

yearlyTab.addEventListener('click', () => {
    yearlyTab.classList.add('active');
    dailyTab.classList.remove('active');
    populateLeaderboard(yearlyData);
    startCountdown();
});


document.getElementById('country-filter').addEventListener('change', () => {
    const activeTab = document.querySelector('.tab-buttons button.active').id;
    if (activeTab === 'daily-tab') {
        populateLeaderboard(dailyData);
    } else {
        populateLeaderboard(yearlyData);
    }
});

document.getElementById('set-quota-btn').addEventListener('click', () => {

    const inputValue = parseInt(document.getElementById('quota-input').value);

    if (isNaN(inputValue) || inputValue <= 0) {
        alert("Please enter a valid quota.");
        return;
    }

    weeklyQuota = inputValue;

    document.getElementById('quota-value').textContent = weeklyQuota;

    updateProgressBar();
});

function updateProgressBar() {

    document.getElementById('weekly-points').textContent = weeklyPoints;

    const percentage = weeklyQuota === 0 ? 0 : Math.min((weeklyPoints / weeklyQuota) * 100, 100);

    document.getElementById('progress-bar').style.width = percentage + "%";
}

updateProgressBar();

/*document.getElementById('cleanup-form').addEventListener('submit', (e) => {

    e.preventDefault();

    const location = document.getElementById('location').value;
    const weight = parseFloat(document.getElementById('weight').value);

    if (!location || isNaN(weight) || weight <= 0) {
        alert("Please fill all fields correctly.");
        return;
    }

    const pointsEarned = weight * 10; // simple calculation

    weeklyPoints += pointsEarned;

    document.getElementById('weekly-points').textContent = weeklyPoints;

    updateProgressBar();

    alert(`Activity submitted! You earned ${pointsEarned} points.`);

    document.getElementById('cleanup-form').reset();
});*/

populateLeaderboard(dailyData);
startCountdown();
