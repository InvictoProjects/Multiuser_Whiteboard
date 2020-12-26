package com.invicto.services;

import com.invicto.domain.Message;
import com.invicto.domain.Room;
import com.invicto.domain.Shape;
import com.invicto.domain.User;
import com.invicto.domain.UserType;
import com.invicto.exceptions.PermissionException;
import com.invicto.storage.RoomRepository;

import static com.invicto.exceptions.EntityNotExistsException.messageIsNotExist;
import static com.invicto.exceptions.EntityNotExistsException.roomIsNotExist;
import static com.invicto.exceptions.PermissionException.notEnoughPermission;

public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public void updateBackgroundColor(User caller, Room room, String backgroundColor) throws PermissionException {
        if (!room.getParticipants().contains(caller) || caller.getUserType() != UserType.OWNER) {
            throw notEnoughPermission(caller);
        }
        if (!roomRepository.existsById(room.getId())) {
            throw roomIsNotExist(room);
        }
        room.setBackgroundColor(backgroundColor);
        roomRepository.update(room);
    }

    public void addShape(User caller, Room room, Shape shape) throws PermissionException {
        if (!room.getParticipants().contains(caller) || !caller.isDrawPermission()) {
            throw notEnoughPermission(caller);
        }
        if (!roomRepository.existsById(room.getId())) {
            throw roomIsNotExist(room);
        }
        room.getShapes().add(shape);
        roomRepository.saveNewShape(shape);
    }

    public void addMessage(User caller, Room room, Message message) throws PermissionException {
        if (!room.getParticipants().contains(caller) || !caller.isWritePermission()) {
            throw notEnoughPermission(caller);
        }
        if (!roomRepository.existsById(room.getId())) {
            throw roomIsNotExist(room);
        }
        room.getMessages().add(message);
        roomRepository.saveNewMessage(message);
    }

    public void deleteMessage(User caller, Room room, Message message) throws PermissionException {
        if (message.getSender() != caller) {
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

    public Message findMessageById(int messageId) {
        Message message = roomRepository.findMessageById(messageId);
        if (message == null) {
            throw messageIsNotExist(messageId);
        }
        return message;
    }
}
