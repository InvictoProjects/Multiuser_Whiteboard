package com.invicto.exceptions;

import com.invicto.domain.Message;
import com.invicto.domain.User;
import com.invicto.domain.Room;

public class EntityExistsException extends IllegalArgumentException {

    public final String entityType;

    EntityExistsException(String entityType, String entityId) {
        super(entityType + " already exists: " + entityId);
        this.entityType = entityType;
    }

    public static EntityExistsException roomAlreadyExists(String entityId) {
        return new EntityExistsException("Room", entityId);
    }

    public static EntityExistsException userAlreadyExists(int entityId) {
        return new EntityExistsException("User", String.valueOf(entityId));
    }

    public static EntityExistsException messageAlreadyExists(int entityId) {
        return new EntityExistsException("Message", String.valueOf(entityId));
    }

    public static EntityExistsException roomAlreadyExists(Room room) {
        return roomAlreadyExists(room.getId());
    }

    public static EntityExistsException userAlreadyExists(User user) {
        return userAlreadyExists(user.getId());
    }

    public static EntityExistsException messageAlreadyExists(Message message) {
        return messageAlreadyExists(message.getId());
    }
}
