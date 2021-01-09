package com.invicto.exceptions;

import com.invicto.domain.Message;
import com.invicto.domain.Room;
import com.invicto.domain.User;

public class EntityNotExistsException extends IllegalArgumentException {

    public final String entityType;

    EntityNotExistsException(String entityType, String entityId) {
        super(entityType + " doesn't exist: " + entityId);
        this.entityType = entityType;
    }

    public static EntityNotExistsException roomIsNotExist(String roomId) {
        return new EntityNotExistsException("Room", roomId);
    }

    public static EntityNotExistsException userIsNotExist(int userId) {
        return new EntityNotExistsException("User", String.valueOf(userId));
    }

    public static EntityNotExistsException messageIsNotExist(int messageId) {
        return new EntityNotExistsException("Message", String.valueOf(messageId));
    }

    public static EntityNotExistsException roomIsNotExist(Room room) {
        return roomIsNotExist(room.getId());
    }

    public static EntityNotExistsException userIsNotExist(User user) {
        return userIsNotExist(user.getId());
    }

    public static EntityNotExistsException messageIsNotExist(Message message) {
        return messageIsNotExist(message.getId());
    }
}
