CREATE TABLE IF NOT EXISTS room_user (
    id int AUTO_INCREMENT  PRIMARY KEY,
    name varchar(100) NOT NULL,
    email varchar(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS booking (
    id int AUTO_INCREMENT  PRIMARY KEY,
    user_id int NOT NULL,
    room_id int NOT NULL,
    utc_start_time TIMESTAMP NOT NULL,
    utc_end_time TIMESTAMP NOT NULL,
    timeZone VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES room_user(id),
    FOREIGN KEY (room_id) REFERENCES room(id),
    CONSTRAINT `booking_unique` UNIQUE (room_id, utc_start_time, utc_end_time)
);