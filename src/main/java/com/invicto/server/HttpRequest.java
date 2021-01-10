package com.invicto.server;

import com.invicto.exceptions.HttpException;
import com.invicto.exceptions.PermissionException;
import com.invicto.server.handlers.ErrorHandler;
import com.invicto.server.handlers.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class HttpRequest implements Runnable {

    public static final String GET_REQUEST_TYPE = "GET";
    public static final String POST_REQUEST_TYPE = "POST";
    public static final String HEAD_REQUEST_TYPE = "HEAD";
    public static final String DELETE_REQUEST_TYPE = "DELETE";
    public static final String PUT_REQUEST_TYPE = "PUT";
    private static final Logger logger = Logger.getLogger(HttpRequest.class.getName());
    private final HttpRouter router;
    private final Socket connection;
    private String stringRequest;
    private String requestLine;
    private String requestType;
    private String path;
    private String fullPath;
    private final Map<String, String> headers = new HashMap<>();
    private final List<String> splitPath = new ArrayList<>();
    private final Map<String, String> params = new HashMap<>();

    public HttpRequest(HttpRouter router, Socket connection) throws IOException {
        this.router = router;
        connection.setKeepAlive(true);
        this.connection = connection;
    }

    @Override
    public void run() {
        if (connection.isClosed()) {
            logger.info("Socket is closed");
        }
        try {
            parseRequest();
            if (!headers.containsKey("Upgrade")) {
                HttpResponse resp = createResponse();
                resp.respond();
            } else {
                determineHandler().handle(this, null);
            }
        } catch (IOException | HttpException | PermissionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "HttpRequest from " + connection.getLocalAddress().getHostAddress() + "\n\t" +
                "Request Line: " + requestLine + "\n\t\t" + "Request Type " + requestType +
                "\n\t\t" + "Request Path " + fullPath;
    }

    public HttpResponse createResponse() throws IOException, HttpException, PermissionException {
        HttpResponse response = new HttpResponse(this);
        CompletableFuture.runAsync(() -> determineHandler().handle(this, response));
        return response;
    }

    public void parseRequest() throws IOException, HttpException {
        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder requestBuilder = new StringBuilder();
        String firstLine = input.readLine();
        if (firstLine == null) {
            throw new HttpException("Input is returning nulls");
        }
        while (firstLine.isEmpty()) {
            firstLine = input.readLine();
        }
        setRequestLine(firstLine);
        requestBuilder.append(requestLine);
        requestBuilder.append("\n");
        for (String line = input.readLine(); line != null && !line.isEmpty(); line = input.readLine()) {
            requestBuilder.append(line);
            requestBuilder.append("\n");
            String[] items = line.split(": ");
            if (items.length == 1) {
                throw new HttpException("No key value pair in \n\t" + line);
            }
            StringBuilder value = new StringBuilder(items[1]);
            for (int i = 2; i < items.length; i++) {
                value.append(": ").append(items[i]);
            }
            headers.put(items[0], String.valueOf(value));
        }
        if (requestType.equals(POST_REQUEST_TYPE) || requestType.equals(DELETE_REQUEST_TYPE) || requestType.equals(PUT_REQUEST_TYPE) && headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < contentLength; i++) {
                stringBuilder.append((char) input.read());
            }
            requestBuilder.append(stringBuilder.toString());
            String requestBody = stringBuilder.toString();
            String[] data = requestBody.split("&");
            params.putAll(parseInputData(data));
        }
        stringRequest = requestBuilder.toString();
    }

    private Map<String, String> parseInputData(String[] data) throws UnsupportedEncodingException {
        Map<String, String> out = new HashMap<>();
        for (String item : data) {
            if (!item.contains("=")) {
                out.put(item, null);
                continue;
            }
            String value = item.substring(item.indexOf('=') + 1);
            value = URLDecoder.decode(value, String.valueOf(StandardCharsets.UTF_8));
            out.put(item.substring(0, item.indexOf('=')), value);
        }
        return out;
    }

    public HttpHandler determineHandler() {
        if (router == null) {
            return new ErrorHandler();
        }
        return router.route(path);
    }

    public boolean isType(String requestTypeCheck) {
        return requestType.equalsIgnoreCase(requestTypeCheck);
    }

    public void setRequestLine(String line) throws HttpException, UnsupportedEncodingException {
        this.requestLine = line;
        String[] splitter = requestLine.trim().split(" ");
        if (splitter.length != 3) {
            throw new HttpException("Request line has a number of spaces other than 3.");
        }
        requestType = splitter[0].toUpperCase();
        setFullPath(splitter[1]);
    }

    public void setFullPath(String inPath) throws UnsupportedEncodingException {
        this.fullPath = inPath;
        path = inPath;
        setSplitPath(inPath);
    }

    public void setSplitPath(String fullPath) throws UnsupportedEncodingException {
        for (String segment : fullPath.substring(1).split("/")) {
            if (segment.isEmpty()) {
                continue;
            }
            splitPath.add(segment);
        }
        if (splitPath.isEmpty()) {
            return;
        }
        if (splitPath.get(splitPath.size() - 1).indexOf('?') != -1) {
            String lastItem = splitPath.get(splitPath.size() - 1);
            splitPath.set(splitPath.size() - 1, lastItem.substring(0, lastItem.indexOf('?')));
            String[] data = lastItem.substring(lastItem.indexOf('?') + 1).split("&");
            params.putAll(parseInputData(data));
        }
    }

    public Socket getConnection() {
        return connection;
    }

    public String getHttpRequest() {
        return stringRequest;
    }
}

