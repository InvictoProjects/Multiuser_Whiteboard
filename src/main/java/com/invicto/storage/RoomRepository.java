package com.invicto.storage;

import com.invicto.domain.Room;
import com.invicto.domain.Message;
import com.invicto.domain.Shape;

public interface RoomRepository {

    void save(Room room);

    void delete(Room room);

    void update(Room room);

    Room findById(String roomId);

    boolean existsById(String roomId);

    void saveNewShape(Shape shape);

    void saveNewMessage(Message message);

    Message findMessageById(int messageId);

    void deleteMessage(Message message);
}
