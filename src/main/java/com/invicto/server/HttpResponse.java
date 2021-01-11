package com.invicto.server;

import com.invicto.exceptions.HttpException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {

    private static final Logger logger = Logger.getLogger(HttpResponse.class.getName());
    private static Map<Integer, String> responses;
    private final HttpRequest request;
    private int code = 200;
    private byte[] body;
    private String mimeType = "text/plain";
    private final Map<String, String> headers = new HashMap<>();
    private final Socket socket;
    private final DataOutputStream writer;

    public HttpResponse(HttpRequest req) throws IOException {
        socket = req.getConnection();
        writer = new DataOutputStream(socket.getOutputStream());
        request = req;
    }

    public void message(int code, String message, String mimeType) {
        this.code = code;
        setBody(message);
        this.mimeType = mimeType;
    }

    public void noContent() {
        this.code = 204;
        setBody("");
        mimeType = "";
    }

    public void respond() {
        try {
            if (socket == null) {
                throw new HttpException("Socket is null...");
            } else if (socket.isClosed()) {
                throw new HttpException("Socket is closed...");
            }
            if (body == null) {
                noContent();
            }
            writeLine("HTTP/1.1 " + getResponseCodeMessage(code));
            writeLine("Content-Type: " + mimeType);
            writeLine("Connection: close");
            writeLine("Content-Size: " + body.length);
            if (!headers.isEmpty()) {
                StringBuilder b = new StringBuilder();
                for (String key : headers.keySet()) {
                    b.append(key);
                    b.append(": ");
                    b.append(getHeader(key));
                    b.append("\n");
                }
                writeLine(b.toString());
            }
            writeLine("");
            if (request.isType(HttpRequest.HEAD_REQUEST_TYPE) || code == 204) {
                return;
            }
            writer.write(body);
        } catch (HttpException | IOException e) {
            logger.log(Level.SEVERE, "Something bad happened while trying to send data to the client");
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void writeLine(String line) throws IOException {
        writer.writeBytes(line + "\n");
    }

    public void setBody(String body) {
        this.body = body.getBytes();
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public static String getResponseCodeMessage(int code) {
        if (responses == null || responses.isEmpty()) {
            setupResponses();
        }
        if (responses.containsKey(code)) {
            return code + " " + responses.get(code);
        }
        return Integer.toString(code);
    }

    private static void setupResponses() {
        responses = new HashMap<>();
        responses.put(100, "Continue");
        responses.put(101, "Switching Protocols");
        responses.put(200, "OK");
        responses.put(201, "Created");
        responses.put(202, "Accepted");
        responses.put(203, "Non-Authoritative Information");
        responses.put(204, "No Content");
        responses.put(205, "Reset Content");
        responses.put(206, "Partial Content");
        responses.put(300, "Multiple Choices");
        responses.put(301, "Moved Permanently");
        responses.put(302, "Found");
        responses.put(303, "See Other");
        responses.put(304, "Not Modified");
        responses.put(305, "Use Proxy");
        responses.put(307, "Temporary Redirect");
        responses.put(400, "Bad Request");
        responses.put(401, "Unauthorized");
        responses.put(402, "Payment Required");
        responses.put(403, "Forbidden");
        responses.put(404, "Not Found");
        responses.put(405, "Method Not Allowed");
        responses.put(406, "Not Acceptable");
        responses.put(407, "Proxy Authentication Required");
        responses.put(408, "Request Timeout");
        responses.put(409, "Conflict");
        responses.put(410, "Gone");
        responses.put(411, "Length Required");
        responses.put(412, "Precondition Failed");
        responses.put(413, "Request Entity Too Large");
        responses.put(414, "Request-URI Too Long");
        responses.put(415, "Unsupported Media Type");
        responses.put(416, "Request Range Not Satisfiable");
        responses.put(417, "Expectation Failed");
        responses.put(418, "I'm a teapot");
        responses.put(420, "Enhance Your Calm");
        responses.put(500, "Internal Server Error");
        responses.put(501, "Not implemented");
        responses.put(502, "Bad Gateway");
        responses.put(503, "Service Unavaliable");
        responses.put(504, "Gateway Timeout");
        responses.put(505, "HTTP Version Not Supported");
    }
}
