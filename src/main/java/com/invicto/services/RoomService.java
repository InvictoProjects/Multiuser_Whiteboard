package com.invicto.services;

import com.invicto.domain.*;
import com.invicto.exceptions.PermissionException;
import com.invicto.storage.UserRepository;
import com.invicto.storage.RoomRepository;

import java.util.List;

import static com.invicto.exceptions.EntityExistsException.roomAlreadyExists;
import static com.invicto.exceptions.EntityNotExistsException.userIsNotExist;
import static com.invicto.exceptions.EntityNotExistsException.roomIsNotExist;
import static com.invicto.exceptions.EntityNotExistsException.messageIsNotExist;
import static com.invicto.exceptions.PermissionException.notEnoughPermission;

public class RoomService {

	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
    
    public RoomService(RoomRepository roomRepository, UserRepository userRepository) {
		this.roomRepository = roomRepository;
		this.userRepository = userRepository;
	}

    public void save(User caller, Room room) throws PermissionException {
		if (caller.getUserType() != UserType.OWNER) {
			throw notEnoughPermission(caller);
		}
		if (roomRepository.existsById(room.getId())) {
			throw roomAlreadyExists(room);
		}
		roomRepository.save(room);
	}

    public void delete(User caller, String roomId) throws PermissionException {
		if (caller.getUserType() != UserType.OWNER) {
			throw notEnoughPermission(caller);
		}
		if (!roomRepository.existsById(roomId)) {
			throw roomIsNotExist(roomId);
		}
		Room room = roomRepository.findById(roomId);
		roomRepository.delete(room);
	}

    public void addUser(User user, String roomId) {
		if (!userRepository.existsById(user.getId())) {
			throw userIsNotExist(user);
		}
		if (!roomRepository.existsById(roomId)) {
			throw roomIsNotExist(roomId);
		}
		Room room = roomRepository.findById(roomId);
		List<User> participants = room.getParticipants();
		participants.add(user);
		room.setParticipants(participants);
		roomRepository.update(room);
	}

    public void deleteUser(int userId, String roomId) {
		if (!userRepository.existsById(userId)) {
			throw userIsNotExist(userId);
		}
		if (!roomRepository.existsById(roomId)) {
			throw roomIsNotExist(roomId);
		}
		User user = userRepository.findById(userId);
		Room room = roomRepository.findById(roomId);
		List<User> participants = room.getParticipants();
		if (participants.contains(user)) {
			participants.remove(user);
			room.setParticipants(participants);
			roomRepository.update(room);
		}
	}

    public void changeOwner(User caller, int userId, String roomId) throws PermissionException {
		if (caller.getUserType() != UserType.OWNER) {
			throw notEnoughPermission(caller);
		}
		if (!userRepository.existsById(userId)) {
			throw userIsNotExist(userId);
		}
		User user = userRepository.findById(userId);
		Room room = roomRepository.findById(roomId);
		List<User> participants = room.getParticipants();
		if (participants.contains(user)) {
			user.setUserType(UserType.OWNER);
			caller.setUserType(UserType.GUEST);
			room.setOwner(user);
			userRepository.update(user);
			userRepository.update(caller);
			roomRepository.update(room);
		}
	}

    public List<User> getUsers(String roomId) {
		if (!roomRepository.existsById(roomId)) {
			throw roomIsNotExist(roomId);
		}
		Room room = roomRepository.findById(roomId);
		return room.getParticipants();
	}
    
    public void updateBackgroundColor(User caller, String roomId, String backgroundColor) throws PermissionException {
        Room room = findById(roomId);
        boolean isCallerInRoom = room.getParticipants().contains(caller);
        boolean isCallerOwner = caller.getUserType() == UserType.OWNER;
        if (!isCallerInRoom || !isCallerOwner) {
            throw notEnoughPermission(caller);
        }
        if (!roomRepository.existsById(room.getId())) {
            throw roomIsNotExist(room);
        }
        room.setBackgroundColor(backgroundColor);
        roomRepository.update(room);
    }

    public void addShape(User caller, String roomId, Shape shape) throws PermissionException {
        Room room = findById(roomId);
        boolean isCallerInRoom = false;
        for (User participant : room.getParticipants()) {
            if (participant.getLogin().equals(caller.getLogin())) {
                isCallerInRoom = true;
                break;
            }
        }
        boolean isCallerCanDraw = caller.isDrawPermission();
        if (!isCallerInRoom || !isCallerCanDraw) {
            throw notEnoughPermission(caller);
        }
        if (!roomRepository.existsById(room.getId())) {
            throw roomIsNotExist(room);
        }
        room.getShapes().add(shape);
        shape.setRoomId(roomId);
        roomRepository.saveNewShape(shape);
    }

    public void addMessage(User caller, String roomId, Message message) throws PermissionException {
        Room room = findById(roomId);
        boolean isCallerInRoom = false;
        for (User participant : room.getParticipants()) {
            if (participant.getLogin().equals(caller.getLogin())) {
                isCallerInRoom = true;
                break;
            }
        }
        boolean isCallerCanWrite = caller.isWritePermission();
        if (!isCallerInRoom || !isCallerCanWrite) {
            throw notEnoughPermission(caller);
        }
        if (!roomRepository.existsById(room.getId())) {
            throw roomIsNotExist(room);
        }
        room.getMessages().add(message);
        message.setRoomId(roomId);
        roomRepository.saveNewMessage(message);
    }

    public void deleteMessage(User caller, String roomId, Message message) throws PermissionException {
        Room room = findById(roomId);
        if (caller.equals(message.getSender())) {
            throw notEnoughPermission(caller);
        }
        if (!roomRepository.existsById(room.getId())) {
            throw roomIsNotExist(room);
        }
        room.getMessages().remove(message);
        roomRepository.deleteMessage(message);
    }

    public Room findById(String roomId) {
        Room room = roomRepository.findById(roomId);
        if (room == null) {
            throw roomIsNotExist(roomId);
        }
        return room;
    }

    public boolean existsById(String roomId) {
        return roomRepository.existsById(roomId);
    }

    public Message findMessageById(int messageId) {
        Message message = roomRepository.findMessageById(messageId);
        if (message == null) {
            throw messageIsNotExist(messageId);
        }
        return message;
    }
}
