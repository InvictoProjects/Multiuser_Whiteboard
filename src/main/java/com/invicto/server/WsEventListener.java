package com.invicto.server;

public interface WsEventListener {

    void onOpen();

    void onMessage(String message);

    void onClose();

}
