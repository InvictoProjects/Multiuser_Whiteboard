/*package com.invicto.server;

import org.junit.Before;
import org.junit.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class HttpResponseTest {

    private HttpResponse response;
    private HttpRequest request;
    private  DataOutputStream writer;

    @Before
    public void setUp() throws Exception {
        request = mock(HttpRequest.class);
        Socket socket = mock(Socket.class);
        writer = mock(DataOutputStream.class);
        when(request.getConnection()).thenReturn(socket);
        when(socket.getOutputStream());
        response = new HttpResponse(request);
    }

    @Test
    public void message() {
        response.message(200, "Some message", "text/plain");
        assertEquals(200, response.getCode());
        assertArrayEquals("Some message".getBytes(), response.getBody());
        assertEquals("text/plain", response.getMimeType());
    }

    @Test
    public void noContent() {
        response.noContent();
        assertEquals(204, response.getCode());
        assertArrayEquals("".getBytes(), response.getBody());
        assertEquals("", response.getMimeType());
    }

    @Test
    public void respond() {

    }

    @Test
    public void writeLine() throws IOException {
        verify(writer, times(1)).writeBytes("Line");
        response.writeLine("Line");
    }
}*/