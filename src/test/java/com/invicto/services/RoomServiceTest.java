package com.invicto.services;

import com.invicto.domain.*;
import com.invicto.exceptions.EntityExistsException;
import com.invicto.exceptions.EntityNotExistsException;
import com.invicto.exceptions.PermissionException;
import com.invicto.storage.RoomRepository;
import com.invicto.storage.UserRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RoomServiceTest {

    private RoomService service;
    private RoomRepository mockedRoomRepo;
    private UserRepository mockedUserRepo;
    private String roomId;
    private Room room1;
    private Room room2;
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
        mockedRoomRepo = mock(RoomRepository.class);
        mockedUserRepo = mock(UserRepository.class);
        service = new RoomService(mockedRoomRepo, mockedUserRepo);
        user1 = new User(1, "Yahoo", "1", UserType.OWNER, true, true);
        user2 = new User(2, "Makoto", "1", UserType.GUEST, true, true);
        user3 = new User(3, "Barry", roomId, UserType.GUEST, false, false);
        participants.add(user1);
        participants.add(user2);
        participants.add(user3);
        Shape shape1 = new Shape(roomId, "path('(1, 5), (15, 20)')", 3, false, false, "#000");
        shape2 = new Shape(roomId, "path('(5, 1), (20, 30)')", 3, false, false, "#000");
        shapes.add(shape1);
        message1 = new Message(1, roomId, user2, LocalTime.now(), "Hello");
        message2 = new Message(2, roomId, user1, LocalTime.now(), "Hi!");
        messages.add(message1);
        room1 = new Room(roomId, user1, participants, shapes, messages, "#000");
        room2 = new Room("1", user1, null, null, null, "#111");
        user1.setRoomId(roomId);
        user2.setRoomId(roomId);
    }

    @Test(expected = EntityExistsException.class)
    public void save() throws PermissionException {
        when(mockedRoomRepo.existsById(roomId)).thenReturn(true);
        when(mockedRoomRepo.findById(roomId)).thenReturn(room1);
        service.save(user1, room1);
        verify(mockedRoomRepo, times(1)).save(room1);
        when(mockedRoomRepo.existsById(room2.getId())).thenReturn(false);
        service.save(user1, room2);
    }

    @Test(expected = EntityNotExistsException.class)
    public void delete() throws PermissionException {
        when(mockedRoomRepo.existsById(roomId)).thenReturn(true);
        when(mockedRoomRepo.findById(roomId)).thenReturn(room1);
        service.delete(user1, roomId);
        verify(mockedRoomRepo, times(1)).delete(room1);
        when(mockedRoomRepo.existsById(room2.getId())).thenReturn(false);
        service.delete(user1, room2.getId());
    }

    @Test(expected = EntityNotExistsException.class)
    public void addUser() {
        when(mockedRoomRepo.existsById(roomId)).thenReturn(true);
        when(mockedRoomRepo.findById(roomId)).thenReturn(room1);
        when(mockedUserRepo.existsById(2)).thenReturn(true);
        service.addUser(user2, roomId);
        verify(mockedRoomRepo, times(1)).update(room1);
        when(mockedUserRepo.existsById(2)).thenReturn(false);
        service.addUser(user2, roomId);
    }

    @Test(expected = EntityNotExistsException.class)
    public void deleteUser() {
        when(mockedRoomRepo.existsById(roomId)).thenReturn(true);
        when(mockedRoomRepo.findById(roomId)).thenReturn(room1);
        when(mockedUserRepo.existsById(2)).thenReturn(true);
        when(mockedUserRepo.findById(2)).thenReturn(user2);
        service.deleteUser(user2.getId(), roomId);
        verify(mockedRoomRepo, times(1)).update(room1);
        when(mockedUserRepo.existsById(2)).thenReturn(false);
        service.deleteUser(user2.getId(), roomId);
    }

    @Test(expected = PermissionException.class)
    public void changeOwner() throws PermissionException {
        when(mockedRoomRepo.existsById(roomId)).thenReturn(true);
        when(mockedRoomRepo.findById(roomId)).thenReturn(room1);
        when(mockedUserRepo.existsById(2)).thenReturn(true);
        service.changeOwner(user1, user2.getId(), roomId);
        verify(mockedRoomRepo, times(0)).update(room1);
        when(mockedUserRepo.existsById(3)).thenReturn(true);
        service.changeOwner(user3, user2.getId(), roomId);
    }

    @Test
    public void getUsers() {
        when(mockedRoomRepo.existsById(roomId)).thenReturn(true);
        when(mockedRoomRepo.findById(roomId)).thenReturn(room1);
        assertEquals(service.getUsers(roomId), participants);
    }

    @Test(expected = PermissionException.class)
    public void updateBackgroundColor() throws PermissionException {
        when(mockedRoomRepo.existsById(roomId)).thenReturn(true);
        when(mockedRoomRepo.findById(roomId)).thenReturn(room1);
        service.updateBackgroundColor(user1, roomId, "#111111");
        verify(mockedRoomRepo, times(1)).update(room1);
        service.updateBackgroundColor(user2, roomId, "#111111");
    }

    @Test(expected = PermissionException.class)
    public void addShape() throws PermissionException {
        when(mockedRoomRepo.existsById(roomId)).thenReturn(true);
        when(mockedRoomRepo.findById(roomId)).thenReturn(room1);
        service.addShape(user2, roomId, shape2);
        verify(mockedRoomRepo, times(1)).saveNewShape(shape2);
        service.addShape(user3, roomId, shape2);
    }

    @Test(expected = PermissionException.class)
    public void addMessage() throws PermissionException {
        when(mockedRoomRepo.existsById(roomId)).thenReturn(true);
        when(mockedRoomRepo.findById(roomId)).thenReturn(room1);
        service.addMessage(user2, roomId, message2);
        verify(mockedRoomRepo, times(1)).saveNewMessage(message2);
        service.addMessage(user3, roomId, message2);
    }

    @Test(expected = PermissionException.class)
    public void deleteMessage() throws PermissionException {
        when(mockedRoomRepo.existsById(roomId)).thenReturn(true);
        when(mockedRoomRepo.findById(roomId)).thenReturn(room1);
        service.deleteMessage(user2, roomId, message1);
        verify(mockedRoomRepo, times(1)).deleteMessage(message1);
        service.deleteMessage(user3, roomId, message1);
    }

    @Test
    public void findById() {
        when(mockedRoomRepo.findById(roomId)).thenReturn(room1);
        assertEquals(room1, service.findById(room1.getId()));
    }

    @Test
    public void existsById() {
        when(mockedRoomRepo.existsById(roomId)).thenReturn(true);
        assertTrue(service.existsById(roomId));
    }

    @Test
    public void findMessageById() {
        when(mockedRoomRepo.findMessageById(1)).thenReturn(message1);
        assertEquals(service.findMessageById(message1.getId()), message1);
    }
}
