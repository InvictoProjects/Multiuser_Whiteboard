package com.invicto.server;

interface HttpHandler {

    void handle(HttpRequest request, HttpResponse response);

}
