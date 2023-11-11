INSERT INTO USERS (EMAIL, NAME)
VALUES ('user1@test.ru', 'Имя Фамилия1'),
       ('user2@test.ru', 'Имя Фамилия2'),
       ('user3@test.ru', 'Имя Фамилия3');

INSERT INTO ITEMS (NAME, DESCRIPTION, IS_AVAILABLE, OWNER_ID)
VALUES ('Название1', 'Описание1', true, 2),
       ('Название2', 'Описание2', true, 1),
       ('Название3', 'Описание3', true, 1);

INSERT INTO BOOKINGS (START_TIMESTAMP, END_TIMESTAMP, STATUS, BOOKER_ID, ITEM_ID)
VALUES ('2024-11-11 18:00:00', '2024-11-11 19:00:00', 'WAITING', 1, 1),
       ('2024-11-11 20:00:00', '2024-11-11 21:00:00', 'WAITING', 1, 1),
       ('2024-11-11 10:00:00', '2024-11-11 11:00:00', 'APPROVED', 1, 1),
       ('2024-11-11 12:00:00', '2024-11-11 13:00:00', 'REJECTED', 1, 1),
       ('2024-11-11 14:00:00', '2024-11-11 15:00:00', 'APPROVED', 1, 1),
       ('2024-11-11 16:00:00', '2024-11-11 17:00:00', 'REJECTED', 1, 1),
       ('2022-11-12 10:00:00', '2024-11-10 11:00:00', 'APPROVED', 1, 1),
       ('2022-11-11 10:00:00', '2022-11-11 11:00:00', 'APPROVED', 1, 1),
       ('2022-11-11 12:00:00', '2022-11-11 13:00:00', 'REJECTED', 1, 1),
       ('2022-11-11 14:00:00', '2022-11-11 15:00:00', 'APPROVED', 1, 1),
       ('2022-11-11 16:00:00', '2022-11-11 17:00:00', 'REJECTED', 1, 1);

INSERT INTO REQUESTS (DESCRIPTION, USER_ID)
VALUES ('Описание1', 1),
       ('Описание2', 2);

INSERT INTO COMMENTS (TEXT, AUTHOR_ID, ITEM_ID)
VALUES ('Текст1', 1, 1),
       ('Текст2', 2, 1);