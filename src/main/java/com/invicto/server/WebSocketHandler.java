package com.invicto.server;

import java.util.ArrayList;
import java.util.List;

public class WebSocketHandler implements HttpHandler {

    private final List<WebSocket> webSockets = new ArrayList<>();

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        try {
            WebSocket webSocket = new WebSocket();
            webSocket.setWsEventListener(new WsEventListener() {
                @Override
                public void onOpen() {
                    webSockets.add(webSocket);
                }

                @Override
                public void onMessage(String message) {
                    for (WebSocket ws : webSockets) {
                        try {
                            ws.send(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onClose() {
                    webSockets.remove(webSocket);
                }
            });
            webSocket.accept(request.getConnection(), request.getHttpRequest());
            webSocket.start();
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }
}
