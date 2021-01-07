package com.invicto.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MainHandler extends HttpHandler {
    private String body;
    private final int code;

    public MainHandler() {
        this.code = 200;
        createBody();
    }

    public MainHandler(int code) {
        this.code = code;
        createBody();
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        response.message(code, body, "text/html");
    }

    private void createBody() {
        String separator = File.separator;
        String path = "client" + separator + "index.html";
        File file = new File(path);
        StringBuilder data;
        try (Scanner sc = new Scanner(file)) {
            data = new StringBuilder("");
            while (sc.hasNext()) {
                data.append(sc.nextLine()).append("\n");
            }
            body = data.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
