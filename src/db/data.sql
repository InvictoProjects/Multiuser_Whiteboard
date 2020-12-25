INSERT INTO Users (login, user_type, write_permission, draw_permission) VALUES('login_test_1', 'room_creator', TRUE, TRUE);

INSERT INTO Users (login, user_type, write_permission, draw_permission) VALUES('login_test_2', 'room_creator', TRUE, TRUE);

INSERT INTO Users (login, user_type, write_permission, draw_permission) VALUES('login_test_3', 'room_guest', TRUE, TRUE);

INSERT INTO Users (login, user_type, write_permission, draw_permission) VALUES('login_test_4', 'room_guest', TRUE, FALSE);

INSERT INTO Users (login, user_type, write_permission, draw_permission) VALUES('login_test_5', 'room_guest', TRUE, FALSE);

INSERT INTO Rooms (id, creator_id, background) VALUES('fdf5d1d3-2d7f-46ef', 1, '#ffffff');

UPDATE Users set room_id = 'fdf5d1d3-2d7f-46ef' WHERE id = 1;

UPDATE Users set room_id = 'fdf5d1d3-2d7f-46ef' WHERE id = 5;

INSERT INTO Rooms (id, creator_id, background) VALUES('72f53380-6678-4b98', 2, '#00ff00');

UPDATE Users set room_id = '72f53380-6678-4b98' WHERE id = 2;

UPDATE Users set room_id = '72f53380-6678-4b98' WHERE id = 3;

UPDATE Users set room_id = '72f53380-6678-4b98' WHERE id = 4;
