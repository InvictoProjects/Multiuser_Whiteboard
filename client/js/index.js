'use strict';

function createRoom() {
    fetch('create').then(response => {
        response.text().then(text => {
            window.location.href += text;
        })
    });
}

function joinRoom() {
    const roomId = document.getElementById('roomId').value;
    window.location.href += roomId;
}
