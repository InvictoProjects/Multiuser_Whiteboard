package domain;

import java.util.List;

public class Room {

	private String id;
	private int creatorId;
	private List<User> participants;
	private List<Shape> shapes;
	private List<Message> messages;
	private String backgroundColor;

	public Room(String id, int creatorId, List<User> participants, List<Shape> shapes, List<Message> messages, String backgroundColor) {
		this.id = id;
		this.creatorId = creatorId;
		this.participants = participants;
		this.shapes = shapes;
		this.messages = messages;
		this.backgroundColor = backgroundColor;
	}

	public boolean isOwner(User user) {
		return user.getUserType() == UserType.OWNER && user.getId() == creatorId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	public List<User> getParticipants() {
		return participants;
	}

	public void setParticipants(List<User> participants) {
		this.participants = participants;
	}

	public List<Shape> getShapes() {
		return shapes;
	}

	public void setShapes(List<Shape> shapes) {
		this.shapes = shapes;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
}
