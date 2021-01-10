'use strict';

let webSocket;

async function sendLogin() {
    const userName = document.getElementById('userName').value;
    webSocket.send('login=' + userName);
}

async function sendMessage() {
    const messageText = document.getElementById('messageText').value;
    webSocket.send('message=' + messageText);
}

window.addEventListener('load', () => {

    function setupWebSocket() {
        const loc = window.location;
        const url = 'ws://' + loc.host + loc.pathname + '/ws';
        webSocket = new WebSocket(url);
        webSocket.onmessage = event => {
            const data = event.data;
            if (data.startsWith('path')) {
                drawShapeByData(data);
            }
            if (data.startsWith('message')) {
                showMessage(data.substring(7));
            }
            if (data.startsWith('drawn shapes')) {
                const pathArray = data.split('\n');
                for (const path of pathArray) {
                    if (path !== 'drawn shapes' && path !== '') {
                        drawShapeByData(path);
                    }
                }
            }
            if (data.startsWith('sent messages')) {
                const messagesArray = data.split('\n');
                for (const message of messagesArray) {
                    if (message !== 'sent messages' && message !== '') {
                        showMessage(message);
                    }
                }
            }
        };
    }

    function showMessage(data) {
        const chatElement = document.getElementById('chatMessages');
        const message = JSON.parse(data);
        const sender = document.createTextNode(message.sender);
        const time = document.createTextNode(message.time);
        const text = document.createTextNode('\n' + message.text);
        const messageElement = document.createElement('p');
        const senderSpan = document.createElement('span');
        senderSpan.style.color = 'gray';
        senderSpan.appendChild(sender);
        const timeSpan = document.createElement('span');
        timeSpan.style.color = 'gray';
        timeSpan.appendChild(time);
        timeSpan.style.float = 'right';
        messageElement.appendChild(senderSpan);
        messageElement.appendChild(text);
        messageElement.appendChild(timeSpan);
        chatElement.appendChild(messageElement);
    }

    setupWebSocket();

    document.getElementById('sendMessage').onclick = sendMessage;
    document.getElementById('sendLogin').onclick = sendLogin;

    const canvas = document.getElementById('canvas');
    const ctx = canvas.getContext('2d');

    canvas.height = window.innerHeight * 0.98;
    canvas.width = window.innerWidth * 0.7;

    let painting = false;
    let pointArray;

    function startPosition() {
        painting = true;
        pointArray = [];
    }

    function finishedPosition() {
        painting = false;
        if (pointArray.length > 0) {
            webSocket.send('path(\'' + pointArray.toString() + '\')');
        }
        ctx.beginPath();
        pointArray = [];
    }

    function draw(e) {
        if (!painting) return;
        ctx.lineWidth = 3;
        ctx.lineCap = 'round';

        const pos = getMousePos(canvas, e);
        const x = pos.x;
        const y = pos.y;
        pointArray.push(x, y);
        ctx.lineTo(x, y);
        ctx.stroke();
        ctx.beginPath();
        ctx.moveTo(x, y);
    }

    function getMousePos(canvas1, e) {
        const rect = canvas1.getBoundingClientRect();
        return {
            x: e.clientX - rect.left,
            y: e.clientY - rect.top
        };
    }

    function drawShapeByData(data) {
        const points = data.substring(4);
        const pointsArr = points.substring(2, points.length - 2).split(',');
        drawShape(pointsArr);
        ctx.beginPath();
    }

    function drawShape(arr) {
        ctx.lineWidth = 3;
        ctx.lineCap = 'round';
        ctx.beginPath();
        ctx.moveTo(arr[0], arr[1]);
        for (let i = 2; i < arr.length; i += 2) {
            const x = arr[i];
            const y = arr[i + 1];
            ctx.lineTo(x, y);
        }
        ctx.stroke();
    }

    canvas.addEventListener('mousedown', startPosition);
    canvas.addEventListener('mouseup', finishedPosition);
    canvas.addEventListener('mousemove', draw);
});
