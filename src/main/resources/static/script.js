document.addEventListener('DOMContentLoaded', () => {
    loadShifts();
    loadSongs();
    updateStats();

    document.getElementById('shift-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const shift = {
            workerName: document.getElementById('workerName').value,
            date: document.getElementById('shiftDate').value,
            startTime: document.getElementById('startTime').value,
            endTime: document.getElementById('endTime').value
        };
        await createShift(shift);
        e.target.reset();
        loadShifts();
        updateStats();
    });

    document.getElementById('song-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const song = {
            title: document.getElementById('songTitle').value,
            artist: document.getElementById('artist').value,
            addedBy: document.getElementById('addedBy').value
        };
        await addSong(song);
        e.target.reset();
        loadSongs();
        updateStats();
    });
});

async function updateStats() {
    const response = await fetch('/api/stats/earnings');
    const data = await response.json();
    document.getElementById('total-earnings').innerText = `${data.totalEarnings} ₽`;
}

async function loadShifts() {
    const response = await fetch('/api/shifts');
    const shifts = await response.json();
    const list = document.getElementById('shifts-list');
    list.innerHTML = '';
    shifts.forEach(shift => {
        const li = document.createElement('li');
        li.className = 'list-item';
        li.innerHTML = `
            <div class="item-content">
                <strong>${shift.workerName}</strong>
                <span>${shift.date} | ${shift.startTime} - ${shift.endTime}</span>
                <span style="color: var(--accent-primary); font-weight: bold; margin-left: 10px;">${shift.cost} ₽</span>
            </div>
            <button class="delete-btn" onclick="deleteShift(${shift.id})">&times;</button>
        `;
        list.appendChild(li);
    });
}

async function createShift(shift) {
    await fetch('/api/shifts', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(shift)
    });
}

async function deleteShift(id) {
    if (confirm('Удалить смену?')) {
        await fetch(`/api/shifts/${id}`, { method: 'DELETE' });
        loadShifts();
        updateStats();
    }
}

async function loadSongs() {
    const response = await fetch('/api/songs');
    const songs = await response.json();
    const list = document.getElementById('songs-list');
    list.innerHTML = '';
    songs.forEach(song => {
        const li = document.createElement('li');
        li.className = 'list-item';
        li.innerHTML = `
            <div class="item-content">
                <strong>${song.title} - ${song.artist}</strong>
                <span>От: ${song.addedBy || 'Аноним'}</span>
                <span style="color: var(--accent-secondary); font-weight: bold; margin-left: 10px;">${song.cost} ₽</span>
            </div>
            <button class="delete-btn" onclick="deleteSong(${song.id})">&times;</button>
        `;
        list.appendChild(li);
    });
}

async function addSong(song) {
    await fetch('/api/songs', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(song)
    });
}

async function deleteSong(id) {
    if (confirm('Удалить песню?')) {
        await fetch(`/api/songs/${id}`, { method: 'DELETE' });
        loadSongs();
        updateStats();
    }
}
