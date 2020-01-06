-- This script assumes the data for table X is in the file tableX.txt .
-- If that is not true, feel free to modify the script in the appropriate places.

-- Note that Postgres automatically creates indices on primary key columns.
-- The existence of the index affects a few of the queries in the test query set.

CREATE TABLE Student (
    student_id int PRIMARY KEY,
    first_name varchar(20),
    last_name varchar(20),
    age int,
    room varchar(4),
    dormitory varchar(40)
);
\copy Student from 'tableStudent.txt'

CREATE TABLE Professor (
    professor_id int PRIMARY KEY,
    first_name varchar(20),
    last_name varchar(20),
    title varchar(20)
);
\copy Professor from 'tableProfessor.txt'

CREATE TABLE Registered (
    student_id int REFERENCES Student(student_id) ON DELETE CASCADE,
    department varchar(50),
    PRIMARY KEY(student_id,department)
);
\copy Registered from 'tableRegistered.txt'

CREATE TABLE Faculty (
    professor_id int REFERENCES Professor(professor_id) ON DELETE CASCADE,
    department varchar(50),
    PRIMARY KEY(professor_id,department)
);
\copy Faculty from 'tableFaculty.txt'

CREATE TABLE Advisor (
    student_id int REFERENCES Student(student_id) ON DELETE CASCADE,
    professor_id int REFERENCES Professor(professor_id) ON DELETE CASCADE,
    PRIMARY KEY(student_id,professor_id)
);
\copy Advisor from 'tableAdvisor.txt'

CREATE TABLE Course (
    course_code int PRIMARY KEY,
    course_name varchar(70)
);
\copy Course from 'tableCourse.txt'

CREATE TABLE Teaching (
    professor_id int REFERENCES Professor(professor_id) ON DELETE CASCADE,
    course_code int REFERENCES Course(course_code) ON DELETE CASCADE,
    PRIMARY KEY(professor_id, course_code)
);
\copy Teaching from 'tableTeaching.txt'

CREATE TABLE Enrolled (
    student_id int REFERENCES Student(student_id) ON DELETE CASCADE,
    course_code int REFERENCES Course(course_code) ON DELETE CASCADE,
    PRIMARY KEY(student_id, course_code)
);
\copy Enrolled from 'tableEnrolled.txt'