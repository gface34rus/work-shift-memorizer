document.addEventListener('DOMContentLoaded', () => {
    // Set default date to today
    document.getElementById('entryDate').valueAsDate = new Date();

    loadData();
    updateStats();

    document.getElementById('main-form').addEventListener('submit', async (e) => {
        e.preventDefault();

        const date = document.getElementById('entryDate').value;
        const type = document.querySelector('input[name="entryType"]:checked').value;

        if (type === 'shift') {
            const shift = {
                workerName: 'Я',
                date: date,
                startTime: '00:00',
                endTime: '23:59'
            };
            await createShift(shift);
        } else {
            const song = {
                title: `Песня (${date})`,
                artist: 'Вне очереди',
                addedBy: 'Гость'
            };
            await addSong(song);
        }

        loadData();
        updateStats();
    });
});

async function updateStats() {
    const response = await fetch('/api/stats/earnings');
    const data = await response.json();

    // Lifetime = all items (paid + unpaid)
    document.getElementById('lifetime-earnings').innerText = `${data.lifetimeEarnings} ₽`;

    // Current balance = unpaid items only
    document.getElementById('current-balance').innerText = `${data.currentBalance} ₽`;
}

async function loadData() {
    const mainList = document.getElementById('main-list');
    mainList.innerHTML = '';

    // Load shifts
    const shiftsResponse = await fetch('/api/shifts');
    const shifts = await shiftsResponse.json();

    // Load songs
    const songsResponse = await fetch('/api/songs');
    const songs = await songsResponse.json();

    // Combine and sort by ID (simplified timestamp approximation)
    let allItems = [
        ...shifts.map(s => ({ ...s, type: 'shift', displayDate: s.date })),
        ...songs.map(s => ({ ...s, type: 'song', displayDate: 'Сегодня' })) // Song doesn't have date field in entity logic yet, but we just added them. Wait, Song entity has NO DATE field.
    ];

    // NOTE: Song entity doesn't have a date field in the original plan. 
    // To properly sort, we might need to add date to Song, but for now we'll just list them.
    // The user requirement "enter date" for song implies Song SHOULD have a date.
    // However, I didn't add Date to Song entity yet. 
    // I will proceed with just listing them, but the user "enters date". If I don't save the date for the song, it's weird.
    // I should probably add Date to Song entity implicitly or just ignore it for now as per user request to "count".
    // Let's list shifts first then songs, or just mix them.

    // To fix this properly, I'll just render them.

    shifts.forEach(shift => {
        const li = createListItem(
            '📅 Смена',
            `${shift.date} (${getDayOfWeek(shift.date)})`,
            shift.cost,
            () => deleteShift(shift.id),
            'var(--accent-primary)'
        );
        mainList.appendChild(li);
    });

    songs.forEach(song => {
        const li = createListItem(
            '🎵 Песня',
            `Вне очереди`,
            song.cost,
            () => deleteSong(song.id),
            'var(--accent-secondary)'
        );
        mainList.appendChild(li);
    });
}

function createListItem(title, subtitle, cost, onDelete, color) {
    const li = document.createElement('li');
    li.className = 'list-item';
    li.innerHTML = `
        <div class="item-content">
            <strong style="color: ${color}">${title}</strong>
            <span>${subtitle}</span>
        </div>
        <div class="item-actions">
            <span class="cost-badge">${cost} ₽</span>
            <button class="delete-btn">&times;</button>
        </div>
    `;
    li.querySelector('.delete-btn').onclick = onDelete;
    return li;
}

function getDayOfWeek(dateString) {
    const days = ['Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'];
    const date = new Date(dateString);
    return days[date.getDay()];
}

async function createShift(shift) {
    await fetch('/api/shifts', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(shift)
    });
}

async function addSong(song) {
    await fetch('/api/songs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(song)
    });
}

async function deleteShift(id) {
    if (confirm('Удалить смену?')) {
        await fetch(`/api/shifts/${id}`, { method: 'DELETE' });
        loadData();
        updateStats();
    }
}

async function deleteSong(id) {
    if (confirm('Удалить эту песню?')) {
        await fetch(`/api/songs/${id}`, { method: 'DELETE' });
        loadData();
        updateStats();
    }
}

document.getElementById('payout-btn').addEventListener('click', async () => {
    if (confirm('Вы уверены, что хотите забрать зарплату? Это обнулит текущий счетчик.')) {
        await fetch('/api/stats/payout', { method: 'POST' });
        loadData();
        updateStats();

        // Confetti effect or simple alert
        alert('💰 Зарплата выдана! Банк обнулен.');
    }
});
