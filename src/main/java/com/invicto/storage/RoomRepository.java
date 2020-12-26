package com.invicto.storage;

import com.invicto.domain.Room;

public interface RoomRepository {

	void save(Room room);

	void delete(Room room);

	void update(Room room);

	Room findById(String room_id);

	boolean existsById(String room_id);
}
