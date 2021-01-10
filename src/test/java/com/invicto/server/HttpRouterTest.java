/*package com.invicto.server;

import com.invicto.server.handlers.ErrorHandler;
import com.invicto.server.handlers.FileHandler;
import com.invicto.server.handlers.HttpHandler;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HttpRouterTest {

    private HttpRouter router;
    private HttpHandler handler;

    @Before
    public void setUp() {
        handler = mock(FileHandler.class);
        router = new HttpRouter();
    }

    @Test
    public void route() {
        router.addHandler("/path", handler);
        assertEquals(router.route("/path"), handler);
    }

    @Test
    public void addHandler() {
        router.addHandler("/path", handler);
        HttpHandler resHandler = router.route("/path");
        assertEquals(handler,resHandler);
    }

    @Test
    public void deleteHandler() {
        router.deleteHandler("/path");
        assertTrue(router.route("/path") instanceof ErrorHandler);
    }
}*/