CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO movies (id, title, duration_minutes)
VALUES ('5e5d5f9f-4f4a-4ad0-93d4-bb7d5731e41b', 'Movie A', 120),
       ('e223c28b-1d15-4c0b-8ff1-ba162aecc476', 'Movie B', 100),
       ('b5c67e5d-6aaf-4a7c-8ed5-5dd5e5d9a4e4', 'Movie C', 140),
       ('675a8f31-6faa-4aa7-89a1-8d7a6a3a51d3', 'Movie D', 130);

INSERT INTO rooms (id, name, total_seats)
VALUES ('389f0866-8596-40a6-98d6-d7b6a601a310', 'Room 1', 50),
       ('6ff82ed6-c1f0-4a8a-a82e-63f79df54a3e', 'Room 2', 60),
       ('5ebe3a93-0a7a-4f58-ba82-0d2e9ac4b4f4', 'Room 3', 70);


INSERT INTO screenings (id, movie_id, room_id, start_time)
VALUES
    -- Screenings for Room 1
    ('d21f04af-4c84-4a4d-9d4c-5665b5f7cfcc', '5e5d5f9f-4f4a-4ad0-93d4-bb7d5731e41b', '389f0866-8596-40a6-98d6-d7b6a601a310', '2023-04-13 12:00:00'),
    ('1e285aa2-d9a9-47d9-99e8-07f258fc2f1a', 'e223c28b-1d15-4c0b-8ff1-ba162aecc476', '389f0866-8596-40a6-98d6-d7b6a601a310', '2023-04-13 15:00:00'),
    ('0aae167e-76b9-4ef7-95d2-c8e7e93f100e', 'b5c67e5d-6aaf-4a7c-8ed5-5dd5e5d9a4e4', '389f0866-8596-40a6-98d6-d7b6a601a310', '2023-04-13 18:00:00'),
    ('17de56bb-33d9-4687-a698-f47ed6a1db3a', '675a8f31-6faa-4aa7-89a1-8d7a6a3a51d3', '389f0866-8596-40a6-98d6-d7b6a601a310', '2023-04-13 21:00:00'),

    -- Screenings for Room 2
    ('53f17472-8f2f-463c-84db-38d17f26631c', 'e223c28b-1d15-4c0b-8ff1-ba162aecc476', '6ff82ed6-c1f0-4a8a-a82e-63f79df54a3e', '2023-04-13 12:30:00'),
    ('aae0597e-cc1d-4b71-b4c4-4a1a81414fa9', 'b5c67e5d-6aaf-4a7c-8ed5-5dd5e5d9a4e4', '6ff82ed6-c1f0-4a8a-a82e-63f79df54a3e', '2023-04-13 15:30:00'),
    ('2827ec05-4614-4a4e-b15b-9e6c5f6d5c2c', '675a8f31-6faa-4aa7-89a1-8d7a6a3a51d3', '6ff82ed6-c1f0-4a8a-a82e-63f79df54a3e', '2023-04-13 18:30:00'),

    ('d7df1cf6-bb3a-4ef6-b905-03fc9d8fc2dc', '5e5d5f9f-4f4a-4ad0-93d4-bb7d5731e41b', '5ebe3a93-0a7a-4f58-ba82-0d2e9ac4b4f4', '2023-04-13 13:00:00'),
    ('1c356c51-19e1-4c47-b3db-5cf5a5f57d5e', 'e223c28b-1d15-4c0b-8ff1-ba162aecc476', '5ebe3a93-0a7a-4f58-ba82-0d2e9ac4b4f4', '2023-04-13 16:00:00'),
    ('01b7d2d2-138d-4b1a-a1f7-df926a8e3a3d', 'b5c67e5d-6aaf-4a7c-8ed5-5dd5e5d9a4e4', '5ebe3a93-0a7a-4f58-ba82-0d2e9ac4b4f4', '2023-04-13 19:00:00'),
    ('d16b75a1-7031-49c9-9f11-3f8d69b5d5bf', '675a8f31-6faa-4aa7-89a1-8d7a6a3a51d3', '5ebe3a93-0a7a-4f58-ba82-0d2e9ac4b4f4', '2023-04-13 22:00:00');


WITH room_seats AS (SELECT id as room_id, generate_series(1, total_seats / 10) AS row_number
                    FROM rooms),
     seat_numbers AS (SELECT generate_series(1, 10) AS seat_number)
INSERT
INTO seats (id, room_id, row_number, seat_number)
SELECT uuid_generate_v4(), room_id, row_number, seat_number
FROM room_seats
         CROSS JOIN seat_numbers;
