package com.invicto.server.handlers;

import com.invicto.server.HttpRequest;
import com.invicto.server.HttpResponse;

import java.util.concurrent.ExecutorService;

public interface HttpHandler {

    void handle(HttpRequest request, HttpResponse response);

}
