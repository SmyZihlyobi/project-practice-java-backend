-- Создание последовательности для обычных проектов
CREATE SEQUENCE IF NOT EXISTS project_seq START WITH 1 INCREMENT BY 1;

-- Создание последовательности для студенческих проектов
CREATE SEQUENCE IF NOT EXISTS student_project_seq START WITH 10000 INCREMENT BY 1;

-- Создание компании
INSERT INTO company (
    id,
    name,
    representative,
    contacts,
    email,
    password,
    student_company,
    created_at
)
VALUES (
           1,
           'ИКНТ',
           'ИКНТ',
           'ИКНТ',
           'iknt@psu.ru',
           '$2a$10$0uC5/UOGnRiLuXYWFz4kAugO6FolwBTmPOadb4k60BuRLWwkyYvQG',
           false,
           NOW()
       );

-- Присвоение роли администратора
INSERT INTO user_roles (user_id, role)
VALUES (1, 'ROLE_ADMIN');

-- Присвоение роли администратора
INSERT INTO user_roles (user_id, role)
VALUES (1, 'ROLE_COMPANY');