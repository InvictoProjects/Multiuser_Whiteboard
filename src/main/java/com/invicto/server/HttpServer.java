package com.invicto.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServer {

    public static final int DEFAULT_PORT = 8080;
    private final Logger logger = Logger.getLogger(HttpServer.class.getName());
    private ExecutorService service;
    private final int port;
    private ServerSocket socket = null;
    private final HttpRouter router;
    private boolean running = true;

    public HttpServer() {
        this(DEFAULT_PORT);
        this.service = Executors.newCachedThreadPool();
    }

    public HttpServer(int port) {
        this.port = port;
        this.router = new HttpRouter();
        this.service = Executors.newCachedThreadPool();
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            running = true;
            socket = serverSocket;
            logger.log(Level.INFO, () -> "Starting HttpServer at http://127.0.0.1:" + port);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(port));
            while (running) {
                getConnection();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Generic Exception", e);
        }
        logger.info("Server shutting down");
    }

    private void getConnection() {
        Socket connection;
        try {
            connection = socket.accept();
            HttpRequest request = new HttpRequest(router, connection);
            Thread thread = new Thread(request);
            service.execute(thread);
            logger.info("Http request from " + connection.getInetAddress() + ":" + connection.getPort());
        } catch (SocketException e) {
            logger.log(Level.WARNING, "Client broke connection early", e);
        } catch (IOException e) {
            logger.log(Level.WARNING, "IOException", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Generic Exception", e);
        }
    }

    public HttpRouter getRouter() {
        return this.router;
    }


}
