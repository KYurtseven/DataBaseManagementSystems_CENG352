-- The following needs to be loaded from psql with the command: \i sample_trigger.sql
-- The trigger then needs to be registered with the following statement:
-- CREATE TRIGGER too_old_student BEFORE INSERT OR UPDATE ON Student
--     FOR EACH ROW EXECUTE PROCEDURE too_old_student();

CREATE FUNCTION too_old_student() RETURNS trigger AS $too_old_student$
    BEGIN
        IF NEW.age > 50 THEN
            RAISE EXCEPTION 'Student is too old!';
        END IF;

        RETURN NEW;
    END;
$too_old_student$ LANGUAGE plpgsql;