package com.invicto.server;

public class WebSocketException extends Exception {

    public WebSocketException() {
        super();
    }

    public WebSocketException(String message) {
        super(message);
    }

    public WebSocketException(String message, Exception e) {
        super(message, e);
    }

    public WebSocketException(Exception e) {
        super(e);
    }
}
