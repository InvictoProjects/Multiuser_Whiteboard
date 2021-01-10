package com.invicto.server.websocket;

import com.invicto.exceptions.WebSocketException;

import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebSocket extends Thread {
    private final Logger logger = Logger.getLogger(WebSocket.class.getName());
    private Socket connection;
    private WsEventListener wsEventListener;
    private byte[] data;
    private boolean isText;

    public final void accept(Socket connection, String request) throws IOException, NoSuchAlgorithmException {
        this.connection = connection;
        String code="";
        String[] headers = request.split("\n");
        for (String header : headers) {
            String[] strings = header.split(": ");
            if (strings[0].equals("Sec-WebSocket-Key")) {
                code = strings[1];
            }
        }
        write("HTTP/1.1 101 Switching Protocols");
        write("Upgrade: websocket");
        write("Connection: Upgrade");
        write("Sec-WebSocket-Accept: " + base64Enc(sha1(code + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")));
        write("");
    }

    @Override
    public void run(){
        try {
            onOpen();
            while (true){
                onMessage(receive());
            }
        } catch (WebSocketException | IOException e) {
            logger.info("WebSocket closed: " + e);
            onClose();
        } finally {
            try {
                connection.close();
            } catch (IOException ioException) {
                logger.log(Level.SEVERE, "IOException on close:", ioException);
            }
        }
    }

    public void onOpen() {
        wsEventListener.onOpen();
    }

    public void onMessage(String message) {
        wsEventListener.onMessage(message);
    }

    public void onClose() {
        wsEventListener.onClose();
    }

    public void send(String msg) throws IOException {
        send(1, msg.getBytes());
    }

    public void send(int opcode, byte[] data) throws IOException {
        connection.getOutputStream().write((byte)(128 + opcode));
        if (data.length > 65535){
            throw new IOException();
        } else if (data.length > 125){
            connection.getOutputStream().write((byte) 126);
            byte a = (byte)(data.length >> 8);
            byte b = (byte)(data.length % 256);
            connection.getOutputStream().write(a);
            connection.getOutputStream().write(b);
        } else {
            connection.getOutputStream().write((byte) data.length);
        }
        connection.getOutputStream().write(data);
        connection.getOutputStream().flush();
    }

    public String receive() throws WebSocketException, IOException {
        isText = true;
        do {
            parseFrame();
        } while (!isText);
        return utf8ToString(data);
    }

    private void parseFrame() throws WebSocketException, IOException {
        byte[] header = new byte[2];
        int end = connection.getInputStream().read(header, 0, 2);
        int opcode = header[0] & 0x0F;
        if (opcode == 8 || end == -1) {
            throw new WebSocketException("Connection closed");
        }
        if (opcode != 1) {
            isText = false;
        }
        int dataSize = getDataSize(header);
        parseData(header, dataSize);
        if (opcode == 0x9) {
            send(0xA, data);
        }
        if (opcode != 0x1) {
            String noTextMsg = "No Text message: opcode: " + opcode + "; data: " + utf8ToString(data);
            logger.info(noTextMsg);
        }
        if (opcode == 0x0) {
            throw new WebSocketException("Continuation frames aren't supported :" + utf8ToString(data) + ".");
        }
    }

    private int getDataSize(byte[] header) throws IOException, WebSocketException {
        int dataSize = header[1] & 0x7F;
        if (dataSize == 126) {
            byte[] extDataSize = new byte[2];
            connection.getInputStream().read(extDataSize, 0, 2);
            dataSize = (extDataSize[0] << 8) + (extDataSize[1] & 0xFF);
        } else if (dataSize == 127) {
            byte[] extDataSize = new byte[8];
            connection.getInputStream().read(extDataSize, 0, 2);
            if (extDataSize[0] != 0 || extDataSize[1] != 0 || extDataSize[2] != 0 ||
                    extDataSize[3] != 0 || (extDataSize[4] & 0x80) != 0)
                throw new WebSocketException("Received too big data-frame");
            dataSize = (extDataSize[4] << 24) + (extDataSize[5] << 16) +
                    (extDataSize[6] << 8) + (extDataSize[7] & 0xFF);
        }
        return dataSize;
    }

    private void parseData(byte[] header, int dataSize) throws IOException {
        boolean isMasked = (header[1] & 0x80) != 0;
        byte[] mask = null;
        if (isMasked) {
            mask = new byte[4];
            connection.getInputStream().read(mask, 0, 4);
        }

        data = new byte[dataSize];
        connection.getInputStream().read(data, 0, dataSize);
        if (isMasked) {
            for (int i = 0; i < dataSize; i++) {
                data[i] = (byte) (data[i] ^ mask[i % 4]);
            }
        }
    }

    public void write(String msg) throws IOException {
        connection.getOutputStream().write((msg + "\n").getBytes());
        connection.getOutputStream().flush();
    }

    public void setWsEventListener(WsEventListener wsEventListener) {
        this.wsEventListener = wsEventListener;
    }

    private String utf8ToString(byte[] a) {
        return new String(a, StandardCharsets.UTF_8);
    }

    private String base64Enc(byte[] a) {
        return Base64.getEncoder().encodeToString(a);
    }

    private byte[] sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        return mDigest.digest(input.getBytes());
    }

    public void close() {
        try {
            connection.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "SocketException: ", e);
        }
    }
}
