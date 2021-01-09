'use strict';

let webSocket;

window.addEventListener("load", () => {

    function setup() {
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
                const pathArray = data.split("\n");
                for (let path of pathArray) {
                    if (path !== 'drawn shapes' && path !== '') {
                        drawShapeByData(path);
                    }
                }
            }
            if (data.startsWith('sent messages')) {
                const messagesArray = data.split("\n");
                for (let message of messagesArray) {
                    if (message !== 'sent messages' && message !== '') {
                        showMessage(message);
                    }
                }
            }
        };
    }

    function drawShapeByData(data) {
        const points = data.substring(4);
        const pointsArr = points.substring(2, points.length - 2).split(',');
        drawShape(pointsArr);
        ctx.beginPath();
    }

    function showMessage(data) {
        const chatElement = document.getElementById('chatMessages');
        const message = JSON.parse(data);
        const sender = document.createTextNode(message.sender);
        const time = document.createTextNode(message.time);
        const text = document.createTextNode("\n" + message.text);
        const messageElement = document.createElement("p");
        const senderSpan = document.createElement("span");
        senderSpan.style.color = "gray";
        senderSpan.appendChild(sender);
        const timeSpan = document.createElement("span");
        timeSpan.style.color = "gray";
        timeSpan.appendChild(time);
        timeSpan.style.float = "right";
        messageElement.appendChild(senderSpan);
        messageElement.appendChild(text);
        messageElement.appendChild(timeSpan);
        chatElement.appendChild(messageElement);
    }

    setup();
});

function sendLogin() {
    const userName = document.getElementById('userName').value;
    webSocket.send("login=" + userName);
}

function sendMessage() {
    const messageText = document.getElementById('messageText').value;
    webSocket.send("message=" + messageText);
}
