package com.invicto.services;

import com.invicto.domain.User;
import com.invicto.domain.UserType;
import com.invicto.domain.Room;
import com.invicto.exceptions.EntityExistsException;
import com.invicto.exceptions.EntityNotExistsException;
import com.invicto.exceptions.PermissionException;
import com.invicto.storage.UserRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static java.util.Collections.emptyList;


public class UserServiceTest {

    private UserService service;
    private UserRepository mockedRepo;
    private User user1;
    private User user2;

    @Before
    public void setUp() {
        mockedRepo = mock(UserRepository.class);
        service = new UserService(mockedRepo);
        user1 = new User(1, "Yahoo", "1", UserType.OWNER, true, true);
        user2 = new User(2, "Makoto", "1", UserType.GUEST, false, true);
    }

    @Test(expected = EntityExistsException.class)
    public void save() {
        when(mockedRepo.existsById(1)).thenReturn(true);
        service.save(user1);
        verify(mockedRepo, times(1)).save(user1);
        when(mockedRepo.existsById(2)).thenReturn(false);
        service.save(user2);
    }

    @Test(expected = EntityNotExistsException.class)
    public void delete() {
        when(mockedRepo.existsById(1)).thenReturn(true);
        service.delete(user1);
        verify(mockedRepo, times(1)).delete(user1);
        when(mockedRepo.existsById(2)).thenReturn(false);
        service.delete(user2);
    }

    @Test(expected = PermissionException.class)
    public void updateLogin() throws PermissionException {
        when(mockedRepo.existsById(1)).thenReturn(true);
        service.updateLogin(user1, "Yaroslav");
        verify(mockedRepo, times(1)).update(user1);
        when(mockedRepo.existsById(2)).thenReturn(false);
        service.updateLogin(user2, "NewLoginForUser2");
    }

    @Test
    public void updatePermissionsByOwnerForTheSameRoom() throws PermissionException {
        Room room = new Room("afReef32pj9WJ23", user1, List.of(user1, user2), emptyList(), emptyList(), "#FFFFFF");
        when(mockedRepo.existsById(2)).thenReturn(true);
        service.updatePermissions(user1, user2, room, true, false);
        verify(mockedRepo, times(1)).update(user2);
    }

    @Test(expected = PermissionException.class)
    public void updatePermissionsByNotOwner() throws PermissionException {
        Room room = new Room("afReef32pj9WJ23", user1, List.of(user1, user2), emptyList(), emptyList(), "#FFFFFF");
        when(mockedRepo.existsById(2)).thenReturn(true);
        service.updatePermissions(user2, user2, room, false, false);
    }

    @Test(expected = PermissionException.class)
    public void updatePermissionsForDifferentRooms() throws PermissionException {
        Room room = new Room("afReef32pj9WJ23", user1, List.of(user2), emptyList(), emptyList(), "#FFFFFF");
        when(mockedRepo.existsById(2)).thenReturn(true);
        service.updatePermissions(user1, user2, room, false, false);
    }

    @Test
    public void findByExistsId() {
        when(mockedRepo.findById(1)).thenReturn(user1);
        assertEquals(service.findById(1), user1);
    }

    @Test(expected = EntityNotExistsException.class)
    public void findByNotExistsId() {
        when(mockedRepo.findById(1)).thenReturn(user1);
        service.findById(3);
    }
}
