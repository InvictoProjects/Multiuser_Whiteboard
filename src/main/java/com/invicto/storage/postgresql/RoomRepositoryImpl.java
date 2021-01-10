package com.invicto.storage.postgresql;

import com.invicto.domain.Message;
import com.invicto.domain.Room;
import com.invicto.domain.Shape;
import com.invicto.domain.User;
import com.invicto.storage.UserRepository;
import com.invicto.storage.RoomRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RoomRepositoryImpl implements RoomRepository {

    private final Connector connector;
    private final UserRepository userRepository;

    public RoomRepositoryImpl(Connector connector, UserRepository userRepository) {
        this.connector = connector;
        this.userRepository = userRepository;
    }

    @Override
    public void save(Room room) {
        String roomId = room.getId();
        User owner = room.getOwner();
        int ownerId = owner.getId();
        String backgroundColor = room.getBackgroundColor();
        String statement = "INSERT INTO Rooms (id, owner_id, background) " +
                "VALUES('" + roomId + "', " + ownerId + " , '" + backgroundColor + "');";
        connector.executeUpdate(statement);
    }

    @Override
    public void delete(Room room) {
        String id = room.getId();
        String statement = "DELETE FROM Rooms WHERE id = '" + id + "'";
        connector.executeUpdate(statement);
    }

    @Override
    public void update(Room room) {
        String roomId = room.getId();
        User owner = room.getOwner();
        int ownerId = owner.getId();
        String backgroundColor = room.getBackgroundColor();
        String statement = "UPDATE Rooms SET " + "(owner_id, background) = " +
                "('" + ownerId + "', '" + backgroundColor + "') " +
                "WHERE id = '" + roomId + "'";
        connector.executeUpdate(statement);
    }

    @Override
    public Room findById(String roomId) {
        String statement = "SELECT * FROM Rooms WHERE id = '" + roomId + "'";
        ResultSet result = connector.executeQuery(statement);
        if (result != null) {
            try {
                result.next();
                int ownerId = result.getInt(2);
                User owner = userRepository.findById(ownerId);
                List<User> participants = getParticipants(roomId);
                List<Shape> shapes = getShapes(roomId);
                List<Message> messages = getMessages(roomId);
                String backgroundColor = result.getString(3);
                Room room = new Room(roomId, owner, participants, shapes, messages, backgroundColor);
                return room;
            } catch (SQLException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private ArrayList<User> getParticipants(String roomId) throws SQLException {
        String statement = "Select * FROM Users WHERE room_id = '" + roomId + "'";
        ArrayList<User> participants = new ArrayList<>();

        ResultSet result = connector.executeQuery(statement);
        while (result.next()) {
            int userId = result.getInt(1);
            User user = userRepository.findById(userId);
            participants.add(user);
        }
        return participants;
    }

    private ArrayList<Shape> getShapes(String roomId) throws SQLException {
        String statement = "Select * FROM Shapes WHERE room_id = '" + roomId + "'";
        ArrayList<Shape> shapes = new ArrayList<>();

        ResultSet result = connector.executeQuery(statement);
        while (result.next()) {
            String pathString = result.getString(3);
            StringBuilder numArray = new StringBuilder(pathString.replaceAll("\\D+", ","));
            numArray.deleteCharAt(numArray.length() - 1).deleteCharAt(0);
            String path = "path('" + numArray + "')";
            int thickness = result.getInt(4);
            boolean dotted = result.getBoolean(5);
            boolean filled = result.getBoolean(6);
            String color = result.getString(7);
            Shape shape = new Shape(roomId, path, thickness, dotted, filled, color);
            shapes.add(shape);
        }
        return shapes;
    }

    private ArrayList<Message> getMessages(String roomId) throws SQLException {
        String statement = "SELECT * FROM Messages WHERE room_id = '" + roomId + "'";
        ArrayList<Message> messages = new ArrayList<>();
        ResultSet result = connector.executeQuery(statement);
        while (result.next()) {
            int messageId = result.getInt(1);
            Message message = findMessageById(messageId);
            messages.add(message);
        }
        return messages;
    }

    @Override
    public boolean existsById(String roomId) {
        if (roomId == null) {
            return false;
        }
        String statement = "SELECT * FROM Rooms WHERE id = '"
                + roomId + "'";
        ResultSet result = connector.executeQuery(statement);
        try {
            return result.next();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void saveNewShape(Shape shape) {
        String roomId = shape.getRoomId();
        String path = shape.getPath();
        int thickness = shape.getThickness();
        boolean dotted = shape.isDotted();
        boolean filled = shape.isFilled();
        String color = shape.getColor();
        String statement = "INSERT INTO Shapes (room_id, path, thickness, dotted, filled, color) " +
                "VALUES('" + roomId + "', " +
                path + ", " +
                thickness + ", " +
                String.valueOf(dotted).toUpperCase() + ", " +
                String.valueOf(filled).toUpperCase() + ", " +
                "'" + color + "')";
        connector.executeUpdate(statement);
    }

    @Override
    public void saveNewMessage(Message message) {
        int senderId = message.getSender().getId();
        String roomId = message.getRoomId();
        LocalTime time = message.getTime();
        String text = message.getText();
        String statement = "INSERT INTO Messages (room_id, sender_id, time, text) " +
                "VALUES('" + roomId + "', " +
                senderId + ", " +
                "'" + time + "', " +
                "'" + text + "') RETURNING id";
        ResultSet result = connector.executeQuery(statement);
        try {
            if (result.next()) {
                int id = result.getInt("id");
                message.setId(id);
            }
        } catch (SQLException | NullPointerException e) {
            message.setId(null);
        }
    }

    @Override
    public Message findMessageById(int messageId) {
        String statement = "SELECT * FROM Messages WHERE id = " + messageId;
        ResultSet result = connector.executeQuery(statement);
        if (result != null) {
            try {
                result.next();
                String roomId = result.getString("room_id");
                int senderId = result.getInt(3);
                User sender = userRepository.findById(senderId);
                LocalTime time = result.getTime(4).toLocalTime();
                String text = result.getString(5);
                return new Message(messageId, roomId, sender, time, text);
            } catch (SQLException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void deleteMessage(Message message) {
        Integer id = message.getId();
        String statement = "DELETE FROM Messages WHERE id" + id;
        connector.executeUpdate(statement);
    }
}
