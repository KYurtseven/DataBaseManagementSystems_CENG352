
-- delete previous roles
DROP ROLE guest;
DROP ROLE secretary;
DROP ROLE metusaisadmin;


-- delete previous users
DROP USER guest1;
DROP USER secreter1;
DROP USER admin1;

-- create roles
CREATE ROLE guest;
CREATE ROLE secretary;
CREATE ROLE metusaisadmin;

-- create users
CREATE USER guest1;
CREATE USER secreter1;
CREATE USER admin1;

-- create password for each user
ALTER USER guest1 WITH PASSWORD 'g1';
ALTER USER secreter1 WITH PASSWORD 's1';
ALTER USER admin1 WITH PASSWORD 'a1';

-- you can login by this command
-- psql -U guest1 -h localhost project3
-- password is g1

-- view for professor full name, last name, title and department
CREATE VIEW public_professor AS
	SELECT P.first_name, P.last_name, P.title, F.department
    FROM Professor P, Faculty F
    WHERE P.professor_id = F.professor_id;

-- view for course name
CREATE VIEW course_name_view AS
	SELECT course_name
    FROM Course;
    
-- grant read privileges to all roles
GRANT SELECT ON public_professor TO guest;
GRANT SELECT ON public_professor TO secretary;
GRANT SELECT ON public_professor TO metusaisadmin;
GRANT SELECT ON course_name_view TO guest;
GRANT SELECT ON course_name_view TO secretary;
GRANT SELECT ON course_name_view TO metusaisadmin;

-- grant secretary roles
-- grant read for all tables
GRANT SELECT ON ALL TABLES IN SCHEMA public TO secretary;
-- grant update, insert, delete on advisor, teaching and enrollment
GRANT UPDATE, INSERT, DELETE ON ADVISOR TO secretary;
GRANT UPDATE, INSERT, DELETE ON TEACHING TO secretary;
GRANT UPDATE, INSERT, DELETE ON ENROLLED TO secretary;
-- grant all priviliges to admins
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO metusaisadmin;

-- Grant roles to users
GRANT guest TO guest1;
GRANT secretary to secreter1;
GRANT metusaisadmin to admin1;
