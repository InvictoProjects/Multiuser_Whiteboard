package com.invicto.server.handlers;

import com.invicto.server.HttpRequest;
import com.invicto.server.HttpResponse;

public class ErrorHandler implements HttpHandler {

  private final int code;

  public ErrorHandler() {
    this(500);
  }

  public ErrorHandler(int statusCode) {
    super();
    code = statusCode;
  }

  @Override
  public void handle(HttpRequest request, HttpResponse resp) {
    String message = "Internal Server Error";
    resp.message(code, message, "text/plain");
  }
}
