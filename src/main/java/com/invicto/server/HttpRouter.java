package com.invicto.server;

import java.util.HashMap;
import java.util.Map;

public class HttpRouter {
    private final Map<String, HttpHandler> handlers;
    private HttpHandler errorHandler;
    private HttpHandler defaultHandler;

    public HttpRouter() {
        handlers = new HashMap<>();
        errorHandler = new ErrorHandler(501);
        defaultHandler = null;
        addHandler("", new MainHandler());
        addHandler("create", new CreateHandler(this));
    }

    public HttpHandler route(String pathSegment, HttpRequest request) {
        if (handlers.containsKey(pathSegment)) {
            String requestPath = request.getPath();
            request.setPath(requestPath.substring(pathSegment.length() + 1));
            return handlers.get(pathSegment);
        } else if (defaultHandler != null) {
            return defaultHandler;
        }
        return errorHandler;
    }

    public void addHandler(String pathSegment, HttpHandler handler) {
        handlers.put(pathSegment, handler);
    }

    public HttpHandler getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(HttpHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public HttpHandler getDefaultHandler() {
        return defaultHandler;
    }

    public void setDefaultHandler(HttpHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }
}
