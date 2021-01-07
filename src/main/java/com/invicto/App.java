package com.invicto;

import com.invicto.server.HttpServer;

public class App {
    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.run();
    }
}
