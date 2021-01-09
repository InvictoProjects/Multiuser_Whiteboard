package com.invicto.server;

import com.invicto.domain.*;
import com.invicto.exceptions.PermissionException;
import com.invicto.services.RoomService;
import com.invicto.services.UserService;

import java.io.IOException;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

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
                        Room room = roomService.findById(roomId);
                        StringBuilder data = new StringBuilder();
                        for (Shape shape : room.getShapes()) {
                            data.append(shape.getPath()).append("\n");
                        }
                        StringBuilder data1 = new StringBuilder();
                        for (Message message : room.getMessages()) {
                            String obj = "{ " + "\"sender\": \"" + message.getSender().getLogin() + "\", " +
                                    "\"text\": \"" + message.getText() + "\", " +
                                    "\"time\": \"" + message.getTime().toString() + "\" }";
                            data1.append(obj).append("\n");
                        }
                        CompletableFuture.runAsync(wrap(webSocket, "drawn shapes\n" + data)).thenRunAsync(wrap(webSocket, "sent messages\n" + data1));
                        //CompletableFuture.runAsync(wrap(webSocket, "sent messages\n" + data1));
                    } else {
                        user = createRoomAndOwner();
                    }
                    users.put(webSocket, user);
                    webSockets.put(user, webSocket);
                }

                @Override
                public void onMessage(String data) {
                    if (data.startsWith("login=")) {
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
                    } else if (data.startsWith("message=")) {
                        String messageText = data.substring(data.indexOf('=') + 1);
                        LocalTime time = LocalTime.now();
                        User caller = users.get(webSocket);
                        Message message = new Message(null, roomId, caller, time, messageText);
                        try {
                            roomService.addMessage(caller, roomId, message);
                        } catch (PermissionException e) {
                            e.printStackTrace();
                        }
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
                    } else {
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
                    }
                }

                @Override
                public void onClose() {
                    webSockets.remove(webSocket);
                }
            });
            webSocket.accept(request.getConnection(), request.getHttpRequest());
            webSocket.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private Runnable wrap(WebSocket webSocket, String data) {
        return () -> {
            try {
                webSocket.send(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}

