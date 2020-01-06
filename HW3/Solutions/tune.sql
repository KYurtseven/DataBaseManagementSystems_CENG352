
-- See Tuning_explanation_2099547.docx for details

-- ---------------------------------------------------------------------------------------- 
-- index for studentCountForDormitoryRoom
 CREATE INDEX dorm_room ON STUDENT(room, dormitory);
-- -------------------------------------------------------------------------------------
-- index for studentCountForAgeRange
CREATE INDEX age_range ON STUDENT(age);
-- -------------------------------------------------------------------------------------
-- index for professorCountForDepartment
 CREATE INDEX prof_department ON FACULTY(department);
-- -----------------------------------------------------------------------------------------
-- index for studentCountForDepartment
 CREATE INDEX student_department ON Registered(department);
 CLUSTER student_department ON Registered;
 CLUSTER Registered;
-- -----------------------------------------------------------------------------------------
-- index for studentsForProfessor
 CREATE INDEX advisor_professor ON Advisor(professor_id, student_id);
-- --------------------------------------------------------------------------------------
-- index for professorsForStudent
 CREATE INDEX student_name_surname ON Student(first_name, last_name);
-- --------------------------------------------------------------------------------------
-- index for professorTitle
-- nothing is used