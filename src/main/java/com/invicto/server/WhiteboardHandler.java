package com.invicto.server;

public class WhiteboardHandler extends HttpHandler {
    private String body;
    private final int code;

    public WhiteboardHandler() {
        this.code = 200;
        createBody();
    }

    public WhiteboardHandler(int code) {
        this.code = code;
        createBody();
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        response.message(code, body, "text/html");
    }

    private void createBody() {
        body = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <link rel=\"stylesheet\" href=\"index.css\"/>\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Some WHITEBOARD</h1>\n" +
                "</body>\n" +
                "</html>";
    }
}
