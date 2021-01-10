package com.invicto.services;

import com.invicto.domain.*;
import com.invicto.exceptions.EntityExistsException;
import com.invicto.exceptions.PermissionException;
import com.invicto.storage.RoomRepository;
import com.invicto.storage.UserRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class RoomServiceTest {

    private RoomService service;
    private RoomRepository mockedRepo1;
    private UserRepository mockedRepo2;
    private String roomId;
    private Room room1;
    private User user1;
    private User user2;
    private User user3;
    private Shape shape2;
    private Message message1;
    private Message message2;
    private final List<User> participants = new ArrayList<>();
    private final List<Shape> shapes = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();

    @Before
    public void setUp() {
        roomId = "AqZBfqXMzYKA7szXzLza";
        mockedRepo1 = mock(RoomRepository.class);
        mockedRepo2 = mock(UserRepository.class);
        service = new RoomService(mockedRepo1, mockedRepo2);
        user1 = new User(1, "Yahoo", "1", UserType.OWNER, true, true);
        user2 = new User(2, "Makoto", "1", UserType.GUEST, true, true);
        user3 = new User(3, "Barry", roomId, UserType.GUEST, true, false);
        participants.add(user1);
        participants.add(user2);
        Shape shape1 = new Shape(roomId, "path('(1, 5), (15, 20)')", 3, false, false, "#000");
        shape2 = new Shape(roomId, "path('(5, 1), (20, 30)')", 3, false, false, "#000");
        shapes.add(shape1);
        message1 = new Message(1, roomId, user2, LocalTime.now(), "Hello");
        message2 = new Message(2, roomId, user1, LocalTime.now(), "Hi!");
        messages.add(message1);
        room1 = new Room(roomId, user1, participants, shapes, messages, "#000000");
        user1.setRoomId(roomId);
        user2.setRoomId(roomId);
    }

    @Test(expected = EntityExistsException.class)
    public void testSave() throws PermissionException {
        when(mockedRepo1.existsById(roomId)).thenReturn(true);
        service.save(user1, room1);
    }

    @Test
    public void testDelete() throws PermissionException {
        when(mockedRepo1.existsById(roomId)).thenReturn(true);
        service.delete(user1, roomId);
    }

    @Test
    public void testAddUser() {
        when(mockedRepo2.existsById(3)).thenReturn(true);
        when(mockedRepo1.existsById(roomId)).thenReturn(true);
        when(mockedRepo1.findById(roomId)).thenReturn(room1);
        service.addUser(user3, roomId);
    }

    @Test
    public void testDeleteUser() {
        when(mockedRepo1.existsById(roomId)).thenReturn(true);
        when(mockedRepo1.findById(roomId)).thenReturn(room1);
        when(mockedRepo2.existsById(2)).thenReturn(true);
        service.deleteUser(user2.getId(), roomId);
    }

    @Test
    public void testChangeOwner() throws PermissionException {
        when(mockedRepo1.existsById(roomId)).thenReturn(true);
        when(mockedRepo1.findById(roomId)).thenReturn(room1);
        when(mockedRepo2.existsById(2)).thenReturn(true);
        service.changeOwner(user1, user2.getId(), roomId);
    }

    @Test
    public void testGetUsers() {
        when(mockedRepo1.existsById(roomId)).thenReturn(true);
        when(mockedRepo1.findById(roomId)).thenReturn(room1);
        assertEquals(service.getUsers(roomId), participants);
    }

    @Test
    public void testUpdateBackgroundColor() throws PermissionException {
        when(mockedRepo1.existsById(roomId)).thenReturn(true);
        when(mockedRepo1.findById(roomId)).thenReturn(room1);
        service.updateBackgroundColor(user1, roomId, "#111111");
    }

    @Test
    public void testAddShape() throws PermissionException {
        when(mockedRepo1.existsById(roomId)).thenReturn(true);
        when(mockedRepo1.findById(roomId)).thenReturn(room1);
        service.addShape(user2, roomId, shape2);
    }

    @Test
    public void testAddMessage() throws PermissionException {
        when(mockedRepo1.existsById(roomId)).thenReturn(true);
        when(mockedRepo1.findById(roomId)).thenReturn(room1);
        service.addMessage(user2, roomId, message2);
    }

    @Test
    public void testDeleteMessage() throws PermissionException {
        when(mockedRepo1.existsById(roomId)).thenReturn(true);
        when(mockedRepo1.findById(roomId)).thenReturn(room1);
        service.deleteMessage(user2, roomId, message2);
    }

    @Test
    public void testFindById() {
        when(mockedRepo1.findById(roomId)).thenReturn(room1);
        assertEquals(room1, service.findById(room1.getId()));
    }

    @Test
    public void testExistsById() {
        when(mockedRepo1.existsById(roomId)).thenReturn(true);
        assertTrue(service.existsById(roomId));
    }

    @Test
    public void testFindMessageById() {
        when(mockedRepo1.findMessageById(1)).thenReturn(message1);
        assertEquals(service.findMessageById(message1.getId()), message1);
    }
}
