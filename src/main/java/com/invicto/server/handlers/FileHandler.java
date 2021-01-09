package com.invicto.server.handlers;

import com.invicto.server.HttpRequest;
import com.invicto.server.HttpResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileHandler implements HttpHandler {

    private String body;
    private final int code;
    private final String fileName;

    public FileHandler(String fileName) {
        this.code = 200;
        this.fileName = fileName;
        createBody();
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        String ext = fileName.split("\\.")[1];
        switch (ext) {
            case "html":
                response.message(code, body, "text/html");
                break;
            case "css":
                response.message(code, body, "text/css");
                break;
            case "js":
                response.message(code, body, "application/javascript");
                break;
            default:
                response.message(code, body, "text/plain");
                break;
        }
    }

    private void createBody() {
        String separator = File.separator;
        String path = "client" + separator + fileName;
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
