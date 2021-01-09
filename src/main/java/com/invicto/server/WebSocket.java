package com.invicto.server;

import java.io.IOException;
import java.net.ServerSocket;
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

    public final WebSocket accept(Socket connection, String request) throws IOException, NoSuchAlgorithmException {
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
        return this;
    }

    public void run(){
        try {
            onOpen();
            while (true){
                onMessage(receive());
            }
        } catch (Exception e) {
            logger.info("WebSocket closed: " + e);
            onClose();
        } finally {
            try {
                connection.close();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "WebsocketException on close:", ex);
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
        connection.getOutputStream().write((byte)(128+opcode));
        if(data.length>65535){
            throw new IOException();
        }else if(data.length>125){
            connection.getOutputStream().write((byte)126);
            byte a = (byte)(data.length>>8);
            byte b = (byte)(data.length%256);
            connection.getOutputStream().write(a);
            connection.getOutputStream().write(b);
        }else {
            connection.getOutputStream().write((byte) data.length);
        }
        connection.getOutputStream().write(data);
        connection.getOutputStream().flush();
    }

    public String receive() throws WebSocketException, IOException {
        byte[] data;
        boolean istext = true;
        do {
            //header
            byte[] header = new byte[2];
            int end = connection.getInputStream().read(header, 0, 2);
            int opcode = header[0] & 0x0F;
            if (opcode == 8 || end == -1) {
                throw new WebSocketException("Connection closed");
            }
            if (opcode != 1)
                istext = false;

            //datasize
            int datasize = header[1] & 0x7F;
            if (datasize == 126) {
                byte[] extdatasize = new byte[2];
                connection.getInputStream().read(extdatasize, 0, 2);
                datasize = (extdatasize[0] << 8) + (extdatasize[1] & 0xFF);
            } else if (datasize == 127) {
                byte[] extdatasize = new byte[8];
                connection.getInputStream().read(extdatasize, 0, 2);
                if (extdatasize[0] != 0 || extdatasize[1] != 0 || extdatasize[2] != 0 ||
                        extdatasize[3] != 0 || (extdatasize[4] & 0x80) != 0)
                    throw new WebSocketException("recv too big data-frame");
                datasize = (extdatasize[4] << 24) + (extdatasize[5] << 16) +
                        (extdatasize[6] << 8) + (extdatasize[7] & 0xFF);
            }

            //mask
            boolean ismasked = (header[1] & 0x80) != 0;
            byte[] mask = null;
            if (ismasked) {
                mask = new byte[4];
                connection.getInputStream().read(mask, 0, 4);
            }

            //data + unmask
            data = new byte[datasize];
            connection.getInputStream().read(data, 0, datasize);
            if (ismasked) {
                for (int i = 0; i < datasize; i++)
                    data[i] = (byte) (data[i] ^ mask[i % 4]);
            }
            if (opcode == 0x9) {
                send(0xA, data);
            }
            if (opcode != 0x1) {
                String noTextMsg = "No-Text-msg: opcode: " + opcode + "; data: " + utf8ToString(data);
                logger.info(noTextMsg);
            }
            if (opcode == 0x0) {
                throw new WebSocketException("Continuation frames aren't supported :" + utf8ToString(data) + ".");
            }
        } while (!istext);
        return utf8ToString(data);
    }

    public String read() throws IOException {
        StringBuilder ret = new StringBuilder();
        while (true) {
            byte[] proret = new byte[1];
            int end = connection.getInputStream().read(proret,0,1);
            if(end == -1 || proret[0] == 10)break;
            if(proret[0] != 13)
                ret.append((char) proret[0]);
        }
        return ret.toString();
    }

    public void write(String msg) throws IOException {
        msg += "\n";
        connection.getOutputStream().write(msg.getBytes());
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
