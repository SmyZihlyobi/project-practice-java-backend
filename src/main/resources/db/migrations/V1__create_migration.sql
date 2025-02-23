-- Создание последовательности для обычных проектов
CREATE SEQUENCE IF NOT EXISTS project_seq START WITH 1 INCREMENT BY 1;

-- Создание последовательности для студенческих проектов
CREATE SEQUENCE IF NOT EXISTS student_project_seq START WITH 10000 INCREMENT BY 1;