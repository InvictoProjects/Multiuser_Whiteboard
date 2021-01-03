package com.invicto.storage;

import com.invicto.domain.Room;

public interface RoomRepository {

	void save(Room room);

	void delete(Room room);

	void update(Room room);

	Room findById(String roomId);

	boolean existsById(String roomId);
}
