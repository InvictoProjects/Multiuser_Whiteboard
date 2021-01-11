package com.invicto.domain;

import java.util.List;

public class Room {

	private String id;
	private User owner;
	private List<User> participants;
	private List<Shape> shapes;
	private List<Message> messages;
	private String backgroundColor;

	public Room(String id, User owner, List<User> participants, List<Shape> shapes, List<Message> messages, String backgroundColor) {
		this.id = id;
		this.owner = owner;
		this.participants = participants;
		this.shapes = shapes;
		this.messages = messages;
		this.backgroundColor = backgroundColor;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
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
