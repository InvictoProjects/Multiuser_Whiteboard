'use strict';

window.addEventListener('load', () => {
    document.getElementById('createRoom').onclick = createRoom;
    document.getElementById('joinRoom').onclick = joinRoom;
});

function createRoom() {
    fetch('create').then(response => {
        response.text().then(text => {
            window.location.href += text;
        });
    });
}

function joinRoom() {
    const roomId = document.getElementById('roomId').value;
    window.location.href += roomId;
}
