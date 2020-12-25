INSERT INTO Users (login, user_type, write_permission, draw_permission) VALUES('login_test_1', 'room_owner', TRUE, TRUE);

INSERT INTO Users (login, user_type, write_permission, draw_permission) VALUES('login_test_2', 'room_owner', TRUE, TRUE);

INSERT INTO Users (login, user_type, write_permission, draw_permission) VALUES('login_test_3', 'room_guest', TRUE, TRUE);

INSERT INTO Users (login, user_type, write_permission, draw_permission) VALUES('login_test_4', 'room_guest', TRUE, FALSE);

INSERT INTO Users (login, user_type, write_permission, draw_permission) VALUES('login_test_5', 'room_guest', TRUE, FALSE);

INSERT INTO Rooms (id, owner_id, background) VALUES('fdf5d1d3-2d7f-46ef', 1, '#ffffff');

UPDATE Users set room_id = 'fdf5d1d3-2d7f-46ef' WHERE id = 1;

UPDATE Users set room_id = 'fdf5d1d3-2d7f-46ef' WHERE id = 5;

INSERT INTO Rooms (id, owner_id, background) VALUES('72f53380-6678-4b98', 2, '#00ff00');

UPDATE Users set room_id = '72f53380-6678-4b98' WHERE id = 2;

UPDATE Users set room_id = '72f53380-6678-4b98' WHERE id = 3;

UPDATE Users set room_id = '72f53380-6678-4b98' WHERE id = 4;

INSERT INTO Shapes (room_id, path, thickness, dotted, filled, color) VALUES('fdf5d1d3-2d7f-46ef',
                                                                     path('(0, 0), (15, 45), (16, 45)'), 1, FALSE, NULL, '#00ff00');

INSERT INTO Shapes (room_id, path, thickness, dotted, filled, color) VALUES('72f53380-6678-4b98',
                                                                     path('(0, 0), (15, 23), (12, 56)'), 1, FALSE, NULL, '#000000');

INSERT INTO Shapes (room_id, path, thickness, dotted, filled, color) VALUES('72f53380-6678-4b98',
                                                                     path('(0, 0), (15, 76), (45, 22)'), 1, FALSE, NULL, '#ffffff');

INSERT INTO Messages (room_id, sender_id, time, text) VALUES('72f53380-6678-4b98', 1, '04:05:06', 'test_text_11');

INSERT INTO Messages (room_id, sender_id, time, text) VALUES('72f53380-6678-4b98', 5, '04:05:10', 'test_text_12');

INSERT INTO Messages (room_id, sender_id, time, text) VALUES('72f53380-6678-4b98', 5, '00:01:02', 'test_text_13');

INSERT INTO Messages (room_id, sender_id, time, text)
VALUES('fdf5d1d3-2d7f-46ef', 2, '05:37:05', 'test_text_21');

INSERT INTO Messages (room_id, sender_id, time, text)
VALUES('fdf5d1d3-2d7f-46ef', 3, '05:44:09', 'test_text_22');

INSERT INTO Messages (room_id, sender_id, time, text)
VALUES('fdf5d1d3-2d7f-46ef', 4, '06:01:02', 'test_text_23');
