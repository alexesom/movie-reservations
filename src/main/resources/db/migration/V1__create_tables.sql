CREATE TABLE movies (
                        id UUID PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        duration_minutes INT NOT NULL
);

CREATE INDEX ON movies (id);

CREATE TABLE rooms (
                       id UUID PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       total_seats INT NOT NULL
);

CREATE INDEX ON rooms (id);

CREATE TABLE screenings (
                            id UUID PRIMARY KEY,
                            movie_id UUID NOT NULL,
                            room_id UUID NOT NULL,
                            start_time TIMESTAMP NOT NULL,
                            FOREIGN KEY (movie_id) REFERENCES movies(id),
                            FOREIGN KEY (room_id) REFERENCES rooms(id)
);

CREATE INDEX ON screenings (id);
CREATE INDEX ON screenings (movie_id);
CREATE INDEX ON screenings (room_id);

CREATE TABLE seats (
                       id UUID PRIMARY KEY,
                       room_id UUID NOT NULL,
                       row_number INT NOT NULL,
                       seat_number INT NOT NULL,
                       FOREIGN KEY (room_id) REFERENCES rooms(id)
);

CREATE INDEX ON seats (id);
CREATE INDEX ON seats (room_id);

CREATE TABLE reservations (
                              id UUID PRIMARY KEY,
                              screening_id UUID NOT NULL,
                              first_name VARCHAR(255) NOT NULL,
                              last_name VARCHAR(255) NOT NULL,
                              reservation_time TIMESTAMP NOT NULL,
                              expiration_time TIMESTAMP NOT NULL,
                              payment_status VARCHAR(20) NOT NULL,
                              FOREIGN KEY (screening_id) REFERENCES screenings(id)
);

CREATE INDEX ON reservations (id);
CREATE INDEX ON reservations (screening_id);

CREATE TABLE reserved_seats (
                                id UUID PRIMARY KEY,
                                reservation_id UUID NOT NULL,
                                seat_id UUID NOT NULL,
                                ticket_type VARCHAR(255) NOT NULL,
                                expired BOOLEAN NOT NULL,
                                FOREIGN KEY (reservation_id) REFERENCES reservations(id),
                                FOREIGN KEY (seat_id) REFERENCES seats(id)
);

CREATE INDEX ON reserved_seats (id);
CREATE INDEX ON reserved_seats (reservation_id);
CREATE INDEX ON reserved_seats (seat_id);
