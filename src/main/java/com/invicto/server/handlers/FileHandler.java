package com.invicto.server.handlers;

import com.invicto.server.HttpRequest;
import com.invicto.server.HttpResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileHandler implements HttpHandler {

    private static final Logger logger = Logger.getLogger(FileHandler.class.getName());
    private String body;
    private final int code;
    private final String fileName;
    private final Map<String, String> mimeTypes;

    public FileHandler(String fileName) {
        this.code = 200;
        this.fileName = fileName;
        this.mimeTypes = Map.ofEntries(
                Map.entry("html", "text/html"),
                Map.entry("css", "text/css"),
                Map.entry("js", "application/javascript"));
        createBody();
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        String ext = fileName.split("\\.")[1];
        String mimeType = "text/plain";
        if (mimeTypes.containsKey(ext)) {
            mimeType = mimeTypes.get(ext);
        }
        response.message(code, body, mimeType);
    }

    private void createBody() {
        String separator = File.separator;
        String path = "client" + separator + fileName;
        File file = new File(path);
        StringBuilder data;
        try (Scanner sc = new Scanner(file)) {
            data = new StringBuilder();
            while (sc.hasNext()) {
                data.append(sc.nextLine()).append("\n");
            }
            body = data.toString();
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "File not found", e);
        }
    }
}
