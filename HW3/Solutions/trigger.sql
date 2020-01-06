-- Trigger.1 


-- Trigger for deletion
-- Usage:
-- After creating the database with scale 5, use the command below to add
-- a new entry to the registered table, assuming that the 5000th student does not have
-- 'Philosophy' department
-- INSERT INTO Registered values(4999, 'Philosophy');
-- Now, if you look at the table, you can see that he is registered his second
-- department to the Registered relation
-- SELECT * from Registered order by student_id desc limit 10;
-- Next, if you try to delete it, you can.
-- DELETE FROM Registered R 
-- where R.student_id = 4999 and R.department = 'Philosophy';
-- If you try to delete remaining registered relation of the student,
-- you will encounter an error:
-- ERROR:  Student must be registered to at least one department
-- CONTEXT:  PL/pgSQL function student_department_delete() line 11 at RAISE


-- CREATE TRIGGER student_department_delete BEFORE DELETE ON Registered
-- FOR EACH ROW EXECUTE PROCEDURE student_department_delete();

CREATE FUNCTION student_department_delete() Returns trigger AS $student_department_delete$
	DECLARE 
		deptCount integer;
    BEGIN
		-- find the new one in the table
        -- count entries
        SELECT count(*) into deptCount
        FROM Registered
        Where student_id = OLD.student_id;
        IF deptCount = 1 THEN
			RAISE EXCEPTION 'Student must be registered to at least one department';
		END IF;
        
        RETURN OLD;
	END;
$student_department_delete$ LANGUAGE plpgsql;


-- Trigger for insertion
-- Usage:

-- CREATE TRIGGER student_department_insert BEFORE INSERT ON Registered
-- FOR EACH ROW EXECUTE PROCEDURE student_department_insert();

CREATE FUNCTION student_department_insert() Returns trigger as $student_department_insert$
	DECLARE
		deptCount integer;
	BEGIN
		-- find the new one in the table
        -- count entries
        SELECT count(*) into deptCount
        FROM Registered
        Where student_id = NEW.student_id;
        -- since it is before trigger
        IF deptCount = 2 THEN
			RAISE EXCEPTION 'Student are not allowed to be registered more than 2 departments';
		END IF;
        
        RETURN NEW;
    END;
$student_department_insert$ LANGUAGE plpgsql;




-- Trigger.2
-- Since I have chosen the scaling factor as 5 in the initial creation,
-- I have 5 entry in each course. I am assuming that you are not asking us to
-- write a trigger in case of there are less than 5 courses at the beginning of
-- the homework/execution.


-- Usage:
-- CREATE TRIGGER enroll_requirement AFTER DELETE on Enrolled 
-- FOR EACH ROW EXECUTE PROCEDURE enroll_requirement();


CREATE FUNCTION enroll_requirement() Returns trigger as $enroll_requirement$
	DECLARE
		newStudentCount integer;
    BEGIN
		-- read count
        SELECT COUNT(*) INTO newStudentCount
        FROM ENROLLED
        WHERE ENROLLED.course_code = OLD.course_code;
        
        -- if the count is 4
        -- that means it is below, 5. Delete entries
        IF(newStudentCount = 4) THEN
            -- This if will be executed only once.
            
			DELETE FROM ENROLLED
            WHERE course_code =  OLD.course_code;
            
            DELETE FROM TEACHING
            WHERE course_code = OLD.course_code;
            
			DELETE FROM Course
            WHERE course_code = OLD.course_code;
            
            RAISE notice 'Deleted 5 course code';
		END IF;
        
        RETURN OLD;
	END;
$enroll_requirement$ LANGUAGE plpgsql;