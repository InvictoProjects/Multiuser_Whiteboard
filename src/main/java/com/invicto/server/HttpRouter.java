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
        addHandler("/", new FileHandler("index.html"));
        addHandler("/js/index.js", new FileHandler("js/index.js"));
        addHandler("/js/board.js", new FileHandler("js/board.js"));
        addHandler("/css/board.css", new FileHandler("css/board.css"));
    }

    public HttpHandler route(String pathSegment) {
        if (handlers.containsKey(pathSegment)) {
            return handlers.get(pathSegment);
        } else if (defaultHandler != null) {
            return defaultHandler;
        }
        return errorHandler;
    }

    public void addHandler(String pathSegment, HttpHandler handler) {
        handlers.put(pathSegment, handler);
    }

    public void deleteHandler(String pathSegment) {
        handlers.remove(pathSegment);
    }
}
