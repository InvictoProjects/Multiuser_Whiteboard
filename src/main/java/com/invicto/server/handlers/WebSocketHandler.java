package com.invicto.server.handlers;

import com.invicto.domain.*;
import com.invicto.exceptions.PermissionException;
import com.invicto.server.HttpRequest;
import com.invicto.server.HttpResponse;
import com.invicto.server.HttpRouter;
import com.invicto.server.websocket.WebSocket;
import com.invicto.server.websocket.WsEventListener;
import com.invicto.services.RoomService;
import com.invicto.services.UserService;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class WebSocketHandler implements HttpHandler {
    private final Map<WebSocket, User> users = new HashMap<>();
    private final Map<User, WebSocket> webSockets = new HashMap<>();
    private final UserService userService;
    private final RoomService roomService;
    private final String roomId;
    private final HttpRouter router;

    public WebSocketHandler(UserService userService, RoomService roomService, String roomId, HttpRouter router) {
        this.userService = userService;
        this.roomService = roomService;
        this.roomId = roomId;
        this.router = router;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        try {
            WebSocket webSocket = new WebSocket();
            webSocket.setWsEventListener(new WsEventListener() {
                @Override
                public void onOpen() {
                    User user;
                    if (roomService.existsById(roomId)) {
                        user = addUserToRoom();
                        doOpen(webSocket);
                    } else {
                        user = createRoomAndOwner();
                    }
                    users.put(webSocket, user);
                    webSockets.put(user, webSocket);
                }

                @Override
                public void onMessage(String data) {
                    if (data.startsWith("login=")) {
                        changeLogin(webSocket, data);
                    } else if (data.startsWith("message=")) {
                        sendMessage(webSocket, data);
                    } else {
                        sendShape(webSocket, data);
                    }
                }

                @Override
                public void onClose() {
                    doClose(webSocket);
                }
            });
            webSocket.accept(request.getConnection(), request.getHttpRequest());
            webSocket.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doOpen(WebSocket webSocket) {
        CompletableFuture.supplyAsync(() -> roomService.findById(roomId))
                .thenApply(room -> {
                    StringBuilder data = new StringBuilder();
                    for (Shape shape : room.getShapes()) {
                        data.append(shape.getPath()).append("\n");
                    }
                    try {
                        webSocket.send("drawn shapes\n" + data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return room;
                })
                .thenAcceptAsync(room -> {
                    StringBuilder data = new StringBuilder();
                    for (Message message : room.getMessages()) {
                        String obj = "{ " + "\"sender\": \"" + message.getSender().getLogin() + "\", " +
                                "\"text\": \"" + message.getText() + "\", " +
                                "\"time\": \"" + message.getTime().toString() + "\" }";
                        data.append(obj).append("\n");
                        try {
                            webSocket.send("sent messages\n" + data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void changeLogin(WebSocket webSocket, String data) {
        CompletableFuture.runAsync(() -> {
            String login = data.substring(data.indexOf('=') + 1);
            User user = users.get(webSocket);
            try {
                userService.updateLogin(user, login);
            } catch (PermissionException e) {
                try {
                    webSocket.send("Permission denied");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    private void sendMessage(WebSocket webSocket, String data) {
        CompletableFuture.supplyAsync(() -> {
            String messageText = data.substring(data.indexOf('=') + 1);
            LocalTime time = LocalTime.now();
            User caller = users.get(webSocket);
            Message message = new Message(null, roomId, caller, time, messageText);
            try {
                roomService.addMessage(caller, roomId, message);
            } catch (PermissionException e) {
                e.printStackTrace();
            }
            return message;
        }).thenAccept(message -> {
            for (WebSocket ws : users.keySet()) {
                try {
                    String obj = "{ " + "\"sender\": \"" + message.getSender().getLogin() + "\", " +
                            "\"text\": \"" + message.getText() + "\", " +
                            "\"time\": \"" + message.getTime().withNano(0).toString() + "\" }";
                    ws.send("message" + obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendShape(WebSocket webSocket, String data) {
        CompletableFuture.runAsync(() -> {
            try {
                Shape shape = new Shape(roomId, data, 3, false, false, "#000000");
                roomService.addShape(users.get(webSocket), roomId, shape);
            } catch (PermissionException e) {
                e.printStackTrace();
            }
        }).thenRun(() -> {
            try {
                Shape shape = new Shape(roomId, data, 3, false, false, "#000000");
                roomService.addShape(users.get(webSocket), roomId, shape);
            } catch (PermissionException e) {
                e.printStackTrace();
            }
            for (WebSocket ws : users.keySet()) {
                try {
                    if (!ws.equals(webSocket)) {
                        ws.send(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doClose(WebSocket webSocket) {
        User user = users.get(webSocket);
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            roomService.deleteUser(user.getId(), roomId);
            webSockets.remove(user);
            users.remove(webSocket);
        });
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            try {
                roomService.delete(user, roomId);
                for (WebSocket ws : users.keySet()) {
                    ws.close();
                    router.deleteHandler("/" + roomId);
                    router.deleteHandler("/" + roomId + "/ws");
                }
            } catch (PermissionException e) {
                userService.delete(user);
            }
        });
        CompletableFuture.allOf(future1, future2);
    }

    private User createRoomAndOwner() {
        User user = new User(null, "DefaultOwner", null, UserType.OWNER, true, true);
        Room room = new Room(roomId, user, List.of(user), new ArrayList<>(), new ArrayList<>(), "#FFFFFF");
        userService.save(user);
        try {
            roomService.save(user, room);
            userService.updateRoomId(user, roomId);
        } catch (PermissionException e) {
            WebSocket ws = webSockets.get(user);
            try {
                ws.send("Permission Denied");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return user;
    }

    private User addUserToRoom() {
        User user = new User(null, "DefaultUser", roomId, UserType.GUEST, true, true);
        userService.save(user);
        roomService.addUser(user, roomId);
        return user;
    }
}
