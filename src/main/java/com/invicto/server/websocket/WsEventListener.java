package com.invicto.server.websocket;

public interface WsEventListener {

    void onOpen();

    void onMessage(String message);

    void onClose();

}
