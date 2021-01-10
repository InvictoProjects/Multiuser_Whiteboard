package com.invicto.exceptions;

public class HttpException extends Exception {

    public HttpException() {
        super();
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Exception e) {
        super(message, e);
    }

    public HttpException(Exception e) {
        super(e);
    }
}
