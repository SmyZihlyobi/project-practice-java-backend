-- Создание последовательности для обычных проектов
CREATE SEQUENCE project_seq START WITH 1 INCREMENT BY 1;

-- Создание последовательности для студенческих проектов
CREATE SEQUENCE student_project_seq START WITH 10000 INCREMENT BY 1;

-- Создание функции для установки ID
CREATE OR REPLACE FUNCTION set_project_id() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_student_project THEN
        NEW.id := nextval('student_project_seq');
ELSE
        NEW.id := nextval('project_seq');
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создание триггера для установки ID
CREATE TRIGGER project_id_trigger
    BEFORE INSERT ON project
    FOR EACH ROW
    EXECUTE FUNCTION set_project_id();