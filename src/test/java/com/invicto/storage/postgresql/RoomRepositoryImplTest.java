package com.invicto.storage.postgresql;

import com.invicto.domain.*;
import com.invicto.storage.RoomRepository;
import com.invicto.storage.UserRepository;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RoomRepositoryImplTest {

    private Connector connector;
    private UserRepository userRepository;
    private RoomRepository roomRepository;
    private Room room;
    private Shape shape1;
    private Message message1;
    private ResultSet result1;

    @Before
    public void setUp() {
        connector = mock(Connector.class);
        result1 = mock(ResultSet.class);
        userRepository = mock(UserRepositoryImpl.class);
        roomRepository = new RoomRepositoryImpl(connector, userRepository);
        User user1 = new User(1, "Vasyl", "13o23fh09ffwefSFJF", UserType.OWNER, true, true);
        User user2 = new User(2, "Lev", "13o23fh09ffwefSFJF", UserType.GUEST, false, true);
        shape1 = new Shape("assdfsdfsd", "path=('1,2')", 3, false, false, "#FFFFFF");
        Shape shape2 = new Shape("assdfsdfsd", "path=('0,3')", 5, false, false, "#FFFFFF");
        message1 = new Message(1, "assdfsdfsd", user1, LocalTime.MIN, "text");
        Message message2 = new Message(2, "assdfsdfsd", user2, LocalTime.MIN, "text");
        room = new Room("assdfsdfsd", user1, List.of(user1, user2), List.of(shape1, shape2), List.of(message1, message2), "#FFFFFF");
    }

    @Test
    public void save() {
        String statement = "INSERT INTO Rooms (id, owner_id, background) " +
                "VALUES('assdfsdfsd', 1 , '#FFFFFF');";
        roomRepository.save(room);
        verify(connector, times(1)).executeUpdate(statement);
    }

    @Test
    public void delete() {
        String statement = "DELETE FROM Rooms WHERE id = 'assdfsdfsd'";
        roomRepository.delete(room);
        verify(connector, times(1)).executeUpdate(statement);
    }

    @Test
    public void update() {
        String statement = "UPDATE Rooms SET (owner_id, background) = " +
                "('1', '#FFFFFF') WHERE id = 'assdfsdfsd'";
        roomRepository.update(room);
        verify(connector, times(1)).executeUpdate(statement);
    }

    @Test()
    public void findById() throws SQLException {
        String statement1 = "SELECT * FROM Rooms WHERE id = 'assdfsd'";
        when(connector.executeQuery(statement1)).thenReturn(null);
        assertNull(roomRepository.findById("assdfsd"));
        verify(connector, times(1)).executeQuery(statement1);
    }

    @Test
    public void existsById() throws SQLException {
        String statement1 = "SELECT * FROM Rooms WHERE id = 'assdfsdfsd'";
        String statement2 = "SELECT * FROM Rooms WHERE id = 'asdasd'";
        when(connector.executeQuery(statement1)).thenReturn(result1);
        when(result1.next()).thenReturn(true);
        assertTrue(roomRepository.existsById("assdfsdfsd"));
        when(connector.executeQuery(statement2)).thenReturn(result1);
        when(result1.next()).thenReturn(false);
        assertFalse(roomRepository.existsById("asdasd"));
    }

    @Test
    public void saveNewShape() {
        String statement = "INSERT INTO Shapes (room_id, path, thickness, dotted, filled, color) " +
                "VALUES('assdfsdfsd', path=('1,2'), 3, FALSE, FALSE, '#FFFFFF')";
        roomRepository.saveNewShape(shape1);
        verify(connector, times(1)).executeUpdate(statement);
    }

    @Test
    public void saveNewMessage() {
        String statement = "INSERT INTO Messages (room_id, sender_id, time, text) " +
                "VALUES('assdfsdfsd', 1, '" + LocalTime.MIN + "', " + "'text') RETURNING id";
        roomRepository.saveNewMessage(message1);
        verify(connector, times(1)).executeQuery(statement);
    }

    @Test
    public void findMessageById() throws SQLException {
        String statement = "SELECT * FROM Messages WHERE id = 1";
        when(result1.next()).thenReturn(true);
        when(result1.getString("room_id")).thenReturn("assdfsdfsd");
        when(result1.getInt(3)).thenReturn(1);
        when(result1.getTime(4)).thenReturn(Time.valueOf(LocalTime.MIN));
        when(result1.getString(5)).thenReturn("text");
        when(connector.executeQuery(statement)).thenReturn(result1);
        User newUser = new User(1, "Vasyl", "13o23fh09ffwefSFJF", UserType.OWNER, true, true);
        when(userRepository.findById(1)).thenReturn(newUser);
        Message resMessage = roomRepository.findMessageById(1);
        assertEquals(resMessage.getRoomId(), message1.getRoomId());
        assertEquals(resMessage.getSender().getId(), message1.getSender().getId());
        assertEquals(resMessage.getTime().toString(), resMessage.getTime().toString());
        assertEquals(resMessage.getTime(), message1.getTime());
        assertEquals(resMessage.getText(), message1.getText());
    }

    @Test
    public void deleteMessage() {
        String statement = "DELETE FROM Messages WHERE id = 1";
        roomRepository.deleteMessage(message1);
        verify(connector, times(1)).executeUpdate(statement);

    }
}
