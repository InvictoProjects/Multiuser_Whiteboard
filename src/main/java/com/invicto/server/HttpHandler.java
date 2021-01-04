package com.invicto.server;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.*;

public abstract class HttpHandler {

    public static final List<String> DEFAULT_PATH = Collections.singletonList("*");

    private final HashMap<String, ArrayList<Route>> routes = new HashMap<>();
    private final HashMap<String, Route> defaultRoutes = new HashMap<>();

    private Socket socket;
    private DataOutputStream writer;

    public HttpHandler() {

    }

    public void handle(HttpRequest request, HttpResponse response) {
        String httpRequestType = request.getRequestType().toUpperCase();
        if (!routes.containsKey(httpRequestType)) {
            response.message(501, "No " + httpRequestType + " routes exist.", "text/plain");
            return;
        }
        Route route = defaultRoutes.get(httpRequestType);
        int bestFit = 0;
        for (Route testRoute : routes.get(httpRequestType)) {
            if (testRoute.matchesPerfectly(request.getSplitPath())) {
                route = testRoute;
                break;
            }
            int testScore = testRoute.howCorrect(request.getSplitPath());
            if (testScore > bestFit) {
                route = testRoute;
                bestFit = testScore;
            }
        }
        if (route == null) {
            response.message(501, HttpResponse.NOT_A_METHOD_ERROR, "text/plain");
            return;
        }
        route.invoke(request, response);
    }

    public void get(Route route) {
        addRoute(HttpRequest.GET_REQUEST_TYPE, route);
    }

    public void post(Route route) {
        addRoute(HttpRequest.POST_REQUEST_TYPE, route);
    }

    public void delete(Route route) {
        addRoute(HttpRequest.DELETE_REQUEST_TYPE, route);
    }

    public void addRoute(String httpMethod, Route route) {
        httpMethod = httpMethod.toUpperCase();

        if (!routes.containsKey(httpMethod)) {
            routes.put(httpMethod, new ArrayList<>());
        }

        routes.get(httpMethod).add(route);

        if (route.matchesPerfectly(DEFAULT_PATH)) {
            defaultRoutes.put(httpMethod, route);
        }
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setWriter(DataOutputStream writer) {
        this.writer = writer;
    }

    public DataOutputStream getWriter() {
        return writer;
    }
}
