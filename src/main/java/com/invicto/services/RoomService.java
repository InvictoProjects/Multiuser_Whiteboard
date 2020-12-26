package com.invicto.services;

import com.invicto.domain.Room;
import com.invicto.domain.User;
import com.invicto.domain.UserType;
import com.invicto.exceptions.PermissionException;
import com.invicto.storage.RoomRepository;

import java.util.List;

import static com.invicto.exceptions.EntityExistsException.roomAlreadyExists;
import static com.invicto.exceptions.EntityExistsException.userAlreadyExists;
import static com.invicto.exceptions.EntityNotExistsException.roomIsNotExist;
import static com.invicto.exceptions.EntityNotExistsException.userIsNotExist;
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

	public void delete(User caller, String room_id) throws PermissionException {
		if (caller.getUserType() != UserType.OWNER) {
			throw notEnoughPermission(caller);
		}
		if (!roomRepository.existsById(room_id)) {
			throw roomIsNotExist(room_id);
		}
		Room room = roomRepository.findById(room_id);
		roomRepository.delete(room);
	}

	public void addUser(User user, String room_id) {
		if (userRepository.existsById(user.getId())) {
			throw userAlreadyExists(user);
		}
		if (!roomRepository.existsById(room_id)) {
			throw roomIsNotExist(room_id);
		}
		Room room = roomRepository.findById(room_id);
		List<User> participants = room.getParticipants();
		participants.add(user);
		room.setParticipants(participants);
		roomRepository.update(room);
	}

	public void deleteUser(int user_id, String room_id) {
		if (!userRepository.existsById(user_id)) {
			throw userIsNotExist(user_id);
		}
		if (!roomRepository.existsById(room_id)) {
			throw roomIsNotExist(room_id);
		}
		User user = userRepository.findById(user_id);
		Room room = roomRepository.findById(room_id);
		List<User> participants = room.getParticipants();
		if (participants.contains(user)) {
			participants.remove(user);
			room.setParticipants(participants);
			roomRepository.update(room);
		}
	}

	public void changeOwner(User caller, int user_id, String room_id) throws PermissionException {
		if (caller.getUserType() != UserType.OWNER) {
			throw notEnoughPermission(caller);
		}
		if (!userRepository.existsById(user_id)) {
			throw userIsNotExist(user_id);
		}
		User user = userRepository.findById(user_id);
		Room room = roomRepository.findById(room_id);
		List<User> participants = room.getParticipants();
		if (participants.contains(user)) {
			user.setUserType(UserType.OWNER);
			caller.setUserType(UserType.GUEST);
		}
	}

	public List<User> getUsers(String room_id) {
		if (!roomRepository.existsById(room_id)) {
			throw roomIsNotExist(room_id);
		}
		Room room = roomRepository.findById(room_id);
		return room.getParticipants();
	}
}
