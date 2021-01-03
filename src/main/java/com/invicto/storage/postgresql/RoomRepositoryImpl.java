package com.invicto.storage.postgresql;

import com.invicto.domain.Message;
import com.invicto.domain.Room;
import com.invicto.domain.Shape;
import com.invicto.domain.User;
import com.invicto.storage.UserRepository;
import com.invicto.storage.RoomRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomRepositoryImpl implements RoomRepository {

	private Connector connector;
	private UserRepository userRepository;
	private RoomRepository roomRepository;

	@Override
	public void save(Room room) {
		String roomId = room.getId();
		User owner = room.getOwner();
		int ownerId = owner.getId();
		String backgroundColor = room.getBackgroundColor();
		String statement = "INSERT INTO Rooms (id, owner_id, background) " +
				"VALUES('" + roomId + "', " + ownerId + " , '"+ backgroundColor +"');";
		connector.executeQuery(statement);
	}

	@Override
	public void delete(Room room) {
		String id = room.getId();
		String statement = "DELETE FROM Rooms WHERE id = '" + id + "'";
		connector.executeQuery(statement);
	}

	@Override
	public void update(Room room) {
		String roomId = room.getId();
		User owner = room.getOwner();
		int ownerId = owner.getId();
		String backgroundColor = room.getBackgroundColor();
		String statement = "UPDATE Rooms SET " + "(owner_id, background) = " +
				"('" + ownerId + "', " + backgroundColor + "') " +
				"WHERE id = '" + roomId + "'";
		connector.executeQuery(statement);
	}

	@Override
	public Room findById(String roomId) {
		String statement = "SELECT * FROM Users WHERE id = '" + roomId + "'";
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
		}
		else {
			return null;
		}
	}

	ArrayList<User> getParticipants(String roomId) throws SQLException {
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

	ArrayList<Shape> getShapes(String roomId) throws SQLException {
		String statement = "Select * FROM Shapes WHERE room_id = '" + roomId + "'";
		ArrayList<Shape> shapes = new ArrayList<>();

		ResultSet result = connector.executeQuery(statement);
		while (result.next()) {
			String color = result.getString(7);
			int thickness = result.getInt(4);
			boolean dotted = result.getBoolean(5);
			boolean filled = result.getBoolean(6);
			Shape shape = new Shape(color, thickness, dotted, filled);
			shapes.add(shape);
		}
		return shapes;
	}

	ArrayList<Message> getMessages(String roomId) throws SQLException {
		String statement = "Select * FROM Messages WHERE room_id = '" + roomId + "'";
		ArrayList<Message> messages = new ArrayList<>();

		ResultSet result = connector.executeQuery(statement);
		while (result.next()) {
			int messageId = result.getInt(1);
			Message message = roomRepository.findMessageById(messageId);
			messages.add(message);
		}
		return messages;
	}

	@Override
	public boolean existsById(String roomId) {
		String statement = "SELECT * FROM Users WHERE id = '"
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

	}

	@Override
	public void saveNewMessage(Message message) {

	}

	@Override
	public Message findMessageById(int messageId) {
		return null;
	}

	@Override
	public void deleteMessage(Message message) {

	}
}
