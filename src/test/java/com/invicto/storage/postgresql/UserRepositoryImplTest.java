package com.invicto.storage.postgresql;

import com.invicto.domain.User;
import com.invicto.domain.UserType;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserRepositoryImplTest {

    private Connector connector;
    private UserRepositoryImpl userRepository;
    private ResultSet result1;
    private ResultSet result2;
    private User user1;
    private User user2;

    @Before
    public void setUp() {
        connector = mock(Connector.class);
        result1 = mock(ResultSet.class);
        result2 = mock(ResultSet.class);
        userRepository = new UserRepositoryImpl(connector);
        user1 = new User(1, "Vasyl", "13o23fh09ffwefSFJF", UserType.OWNER, true, true);
        user2 = new User(2, "Lev", "13o23fh09ffwefSFJF", UserType.GUEST, false, true);
    }

    @Test
    public void save() throws SQLException {
        String statement1 = "INSERT INTO Users(login, room_id, user_type, write_permission, draw_permission) " +
                "VALUES('Vasyl', '13o23fh09ffwefSFJF', 'room_owner', TRUE, TRUE) RETURNING id";
        String statement2 = "INSERT INTO Users(login, room_id, user_type, write_permission, draw_permission) " +
                "VALUES('Lev', '13o23fh09ffwefSFJF', 'room_guest', FALSE, TRUE) RETURNING id";
        when(connector.executeQuery(statement1)).thenReturn(result1);
        when(connector.executeQuery(statement2)).thenReturn(null);
        when(result1.next()).thenReturn(true);
        when(result1.getInt("id")).thenReturn(35);
        userRepository.save(user1);
        userRepository.save(user2);
        assertEquals(Integer.valueOf(35), user1.getId());
        assertNull(user2.getId());
    }

    @Test
    public void update() {
        String statement1 = "UPDATE Users SET (login, room_id, user_type, write_permission, draw_permission) " +
                "= ('NeVasyl', '13o23fh09ffwefSFJF', 'room_owner', TRUE, TRUE) WHERE id = 1";
        String statement2 = "UPDATE Users SET (login, room_id, user_type, write_permission, draw_permission) " +
                "= ('Lev', '13o23fh09', 'room_owner', TRUE, TRUE) WHERE id = 2";
        user1.setLogin("NeVasyl");
        userRepository.update(user1);
        verify(connector, times(1)).executeUpdate(statement1);
        user2.setRoomId("13o23fh09");
        user2.setUserType(UserType.OWNER);
        user2.setWritePermission(true);
        user2.setDrawPermission(true);
        userRepository.update(user2);
        verify(connector, times(1)).executeUpdate(statement2);
        user1.setLogin("Vasyl");
    }

    @Test
    public void delete() {
        String statement1 = "DELETE FROM Users WHERE id = 1";
        String statement2 = "DELETE FROM Users WHERE id = 2";
        userRepository.delete(user1);
        verify(connector, times(1)).executeUpdate(statement1);
        userRepository.delete(user2);
        verify(connector, times(1)).executeUpdate(statement2);
    }

    @Test
    public void findById() throws SQLException {
        String statement1 = "SELECT * FROM Users WHERE id = 1";
        String statement2 = "SELECT * FROM Users WHERE id = 2";
        when(connector.executeQuery(statement1)).thenReturn(result1);
        when(connector.executeQuery(statement2)).thenReturn(null);
        when(result1.getString("login")).thenReturn("Vasyl");
        when(result1.getString("room_id")).thenReturn("13o23fh09ffwefSFJF");
        when(result1.getString("user_type")).thenReturn(String.valueOf(UserType.OWNER));
        when(result1.getBoolean("write_permission")).thenReturn(true);
        when(result1.getBoolean("draw_permission")).thenReturn(true);
        User user12 = userRepository.findById(1);
        assertEquals(user1.getId(), user12.getId());
        assertEquals(user1.getLogin(), user12.getLogin());
        assertEquals(user1.getRoomId(), user12.getRoomId());
        assertEquals(user1.getUserType(), user12.getUserType());
        assertEquals(user1.isWritePermission(), user12.isWritePermission());
        assertEquals(user1.isDrawPermission(), user12.isDrawPermission());
        assertNull(userRepository.findById(2));
    }

    @Test
    public void existsById() throws SQLException {
        String statement1 = "SELECT * FROM Users WHERE id = 1";
        String statement2 = "SELECT * FROM Users WHERE id = 2";
        when(connector.executeQuery(statement1)).thenReturn(result1);
        when(connector.executeQuery(statement2)).thenReturn(result2);
        when(result1.next()).thenReturn(true);
        when(result2.next()).thenReturn(false);
        assertTrue(userRepository.existsById(1));
        assertFalse(userRepository.existsById(2));
    }
}
