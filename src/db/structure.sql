CREATE TYPE user_type AS ENUM ('room_guest', 'room_owner');

CREATE TABLE Users (
  id bigserial PRIMARY KEY,
  login varchar(64) NOT NULL,
  room_id varchar(20),
  user_type user_type NOT NULL,
  write_permission boolean NOT NULL,
  draw_permission boolean NOT NULL
);

CREATE TABLE Rooms (
  id varchar(20) PRIMARY KEY,
  owner_id bigserial NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  background bytea NOT NULL
);

ALTER TABLE Users ADD CONSTRAINT fk_room_id FOREIGN KEY (room_id) REFERENCES Rooms(id) ON DELETE CASCADE;

CREATE TABLE Shapes (
  id bigserial PRIMARY KEY,
  room_id varchar(20) NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
  path PATH NOT NULL,
  thickness integer NOT NULL,
  dotted boolean NOT NULL,
  filled boolean,
  color bytea NOT NULL
);
CREATE TABLE Messages (
  id bigserial PRIMARY KEY,
  room_id varchar(20) NOT NULL REFERENCES Rooms(id) ON DELETE CASCADE,
  sender_id bigserial NOT NULL REFERENCES Users(id) ON DELETE CASCADE,
  time time NOT NULL,
  text text NOT NULL
);
