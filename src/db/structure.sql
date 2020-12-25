CREATE TYPE user_type AS ENUM ('room_guest', 'room_creator');

CREATE TABLE Users (
  id bigserial PRIMARY KEY,
  login varchar(64) NOT NULL,
  room_id varchar(20),
  user_type user_type NOT NULL,
  write_permission boolean NOT NULL,
  draw_permission boolean NOT NULL
);

