import java.io.*;
import java.util.*;

/**
 * Generates a synthetic database.
 * Output is a set of generated tab-separated files (tables) that can be inserted into
 * a database using the DBMS's bulk import command.
 * See import_database.sql for the database schema.
 */

public class DatabaseGenerator {

    private class Student {

        private final int studentId;
        private final String firstName;
        private final String lastName;
        private final int age;
        private final String room;
        private final String dormitory;

        public Student(int sid, String fn, String ln, int a, String r, String d) {
            this.studentId = sid;
            this.firstName = fn;
            this.lastName = ln;
            this.age = a;
            this.room = r;
            this.dormitory = d;
        }

        @Override
        public String toString() {
            return studentId + FIELD_SEP + firstName + FIELD_SEP + lastName + FIELD_SEP + age + FIELD_SEP + room + FIELD_SEP + dormitory;
        }
    }

    private class Professor {

        private final int professorId;
        private final String firstName;
        private final String lastName;
        private final String title;

        public Professor(int pid, String fn, String ln, String t) {
            this.professorId = pid;
            this.firstName = fn;
            this.lastName = ln;
            this.title = t;
        }

        @Override
        public String toString() {
            return professorId + FIELD_SEP + firstName + FIELD_SEP + lastName + FIELD_SEP + title;
        }
    }

    private ArrayList<Student> generatedStudents = new ArrayList<>();

    private ArrayList<Professor> generatedProfessors = new ArrayList<>();

    private Random rand; // Pseudo-random number generator used by all synthesis methods.

    private int scale; // Scales the number of rows in each table.

    // Counts controlled (directly or indirectly) by scale.

    private int getCountStudents() { return 1000 * scale; }

    private int getCountProfessors() { return 50 * scale; }

    //private long getTotalStudentDepartments() { return Math.round(getCountStudents() * AVG_DEPARTMENTS_PER_STUDENT); }

    private long getTotalProfessorDepartments() { return Math.round(getCountProfessors() * AVG_DEPARTMENTS_PER_PROFESSOR); }

    private long getTotalAdvisors() { return Math.round(getCountStudents() * AVG_ADVISORS_PER_STUDENT); }

    private int getCountCourses() { return 50 * scale; }

    private long getTotalTeachings() { return Math.round(getCountProfessors() * AVG_COURSES_PER_PROFESSOR); }

    // Various parameters of the generated data

    private static final int DEFAULT_SCALE = 20;
    private static final int MAX_AGE = 50;

    //private static final double AVG_DEPARTMENTS_PER_STUDENT = 1.4;
    private static final double AVG_DEPARTMENTS_PER_PROFESSOR = 1.2;
    private static final double AVG_ADVISORS_PER_STUDENT = 1.5;
    private static final double AVG_COURSES_PER_PROFESSOR = 2;

    private static final String FIELD_SEP = "\t"; // String used to separate fields of a record in the data file.

    // Various base names for data

    private static final String[] FIRST_NAMES = new String[] {
            "Liam",
            "Noah",
            "William",
            "James",
            "Logan",
            "Benjamin",
            "Mason",
            "Elijah",
            "Oliver",
            "Jacob",
            "Lucas",
            "Michael",
            "Alexander",
            "Ethan",
            "Daniel",
            "Matthew",
            "Aiden",
            "Henry",
            "Joseph",
            "Jackson",
            "Emma",
            "Olivia",
            "Ava",
            "Isabella",
            "Sophia",
            "Mia",
            "Charlotte",
            "Amelia",
            "Evelyn",
            "Abigail",
            "Harper",
            "Emily",
            "Elizabeth",
            "Avery",
            "Sofia",
            "Ella",
            "Madison",
            "Scarlett",
            "Victoria",
            "Aria"
    };

    private static final String[] LAST_NAMES = new String[] {
            "Smith",
            "Johnson",
            "Williams",
            "Brown",
            "Jones",
            "Miller",
            "Davis",
            "Garcia",
            "Rodriguez",
            "Wilson",
            "Martinez",
            "Anderson",
            "Taylor",
            "Thomas",
            "Hernandez",
            "Moore",
            "Martin",
            "Jackson",
            "Thompson",
            "White"
    };

    private static final String[] DORMITORIES = new String[] {
            "Dormitory 1",
            "Dormitory 2",
            "Dormitory 3",
            "Dormitory 4",
            "Dormitory 5",
            "Dormitory 6",
            "Dormitory 7",
            "Dormitory 8",
            "Dormitory 9",
            "Osman Yazici Girls' Guest House",
            "Faik Hiziroglu Boys' Guest House",
            "METU Guest House",
            "ODTUKENT Guest House",
            "EBI Guest House",
            "METU Mustafa Parlar Dormitory",
            "Isa Demiray Dormitory",
            "Faika Demiray Dormitory",
            "Refika Aksoy Dormitory"
    };

    private static final String[] DORMITORY_BLOCKS = new String[] {
            "A",
            "B",
            "C"
    };

    private static final String[] TITLES = new String[] {
            "Professor",
            "Associate Professor",
            "Assistant Professor"
    };

    private static final String[] DEPARTMENTS = new String[] {
            "Architecture",
            "City and Regional Planning",
            "Industrial Design",
            "Biological Sciences",
            "Chemistry",
            "History",
            "Mathematics",
            "Philosophy",
            "Physics",
            "Psychology",
            "Sociology",
            "Statistics",
            "Business Administration",
            "Economics",
            "International Relations",
            "Political Science and Public Administration",
            "Computer Education and Instructional Technology",
            "Educational Sciences",
            "Elementary and Early Childhood Education",
            "Foreign Language Education",
            "Physical Education and Sports",
            "Mathematics and Science Education",
            "Aerospace Engineering",
            "Chemical Engineering",
            "Civil Engineering",
            "Computer Engineering",
            "Electrical and Electronics Engineering",
            "Engineering Sciences",
            "Environmental Engineering",
            "Food Engineering",
            "Geological Engineering",
            "Industrial Engineering",
            "Mechanical Engineering",
            "Metallurgical and Materials Engineering",
            "Mining Engineering",
            "Petroleum and Natural Gas Engineering"
    };

    private static final String[] COURSE_NAMES = new String[] {
            "Computers And Fortran Programming",
            "Introduction to Computers and Pascal Programming",
            "Practice of Algorithms",
            "Rapid Application Development",
            "Database Management Systems",
            "Scientific Computing",
            "Analysis of Dynamic Systems with Feedback",
            "Logic for Computer Sciences",
            "Introduction to Object Oriented Programming Languages and System",
            "Language Processors"
    };

    public static void main(String[] args) throws Exception {

        if (args.length != 0 && args.length != 1) {
            System.out.println("Usage: java DatabaseGenerator [scale]");
            System.exit(1);
        }

        DatabaseGenerator dbgen = null;
        if (args.length == 1) {
            int scale = Integer.parseInt(args[0]);
            dbgen = new DatabaseGenerator(scale);
        } else {
            dbgen = new DatabaseGenerator();
        }

        dbgen.writeStudentTable("tableStudent.txt");
        dbgen.writeProfessorTable("tableProfessor.txt");
        dbgen.writeRegisteredTable("tableRegistered.txt");
        dbgen.writeFacultyTable("tableFaculty.txt");
        dbgen.writeAdvisorTable("tableAdvisor.txt");
        dbgen.writeCourseTable("tableCourse.txt");
        dbgen.writeTeachingTable("tableTeaching.txt");
        dbgen.writeEnrolledTable("tableEnrolled.txt");
    }

    /* Constructors */

    public DatabaseGenerator() {
        this(DEFAULT_SCALE);
    }

    public DatabaseGenerator(int scale) {
        this.rand = new Random();
        this.scale = scale;
    }

    /* Student Table */

    /**
     * Generates, writes out the data for the Student table.
     */
    private void writeStudentTable(String filename) throws FileNotFoundException {

        PrintWriter outFile = null;

        try {
            outFile = new PrintWriter(filename);

            int countStudents = this.getCountStudents();
            for (int i = 0 ; i < countStudents ; ++i) {
                Student newStudent = makeStudentRecord(i);
                generatedStudents.add(newStudent);
                outFile.println(newStudent.toString());
            }
        } finally {
            if (outFile != null) outFile.close();
        }
    }

    /**
     * Generates a student record (student_id, first_name, last_name, age, room, dormitory).
     */
    private Student makeStudentRecord(int id) {

        String uniqueFirstName = FIRST_NAMES[rand.nextInt(FIRST_NAMES.length)] + "_" + id;
        String lastName = LAST_NAMES[rand.nextInt(LAST_NAMES.length)];
        int age = 18 + 1 + rand.nextInt(MAX_AGE - 18);
        String room = DORMITORY_BLOCKS[rand.nextInt(DORMITORY_BLOCKS.length)] + (rand.nextInt(300) + 100);
        String dormitory = DORMITORIES[rand.nextInt(DORMITORIES.length)];

        Student stdnt = new Student(id, uniqueFirstName, lastName, age, room, dormitory);

        return stdnt;
    }

    /* Professor Table */

    /**
     * Generates, writes out the data for the Professor table.
     */
    private void writeProfessorTable(String filename) throws FileNotFoundException {

        PrintWriter outFile = null;

        try {
            outFile = new PrintWriter(filename);

            int countProfessors = getCountProfessors();
            for (int i = 0 ; i < countProfessors ; ++i) {
                Professor newProfessor = makeProfessorRecord(i);
                generatedProfessors.add(newProfessor);
                outFile.println(newProfessor.toString());
            }
        } finally {
            if (outFile != null) outFile.close();
        }
    }

    /**
     * Generates a professor record (professor_id, first_name, last_name, title)
     */
    private Professor makeProfessorRecord(int id) {

        String uniqueFirstName = FIRST_NAMES[rand.nextInt(FIRST_NAMES.length)] + "_" + id;
        String lastName = LAST_NAMES[rand.nextInt(LAST_NAMES.length)];
        String title = TITLES[rand.nextInt(TITLES.length)];

        Professor prfssr = new Professor(id, uniqueFirstName, lastName, title);

        return prfssr;
    }

    /* Registered Table */

    /**
     * Generates, writes out the data for the Registered table.
     */
    private void writeRegisteredTable(String filename) throws Exception {

        // Use this table to map student_ids to departments, so
        // no student is registered to "multiple copies" of the same department.
        Map<Integer, List<String>> studentsToDepartments = new HashMap<Integer, List<String>>();

        PrintWriter outFile = null;

        try {
            outFile = new PrintWriter(filename);

            long numberOfStudents = this.getCountStudents();
            for (long i = 0 ; i < numberOfStudents; ++i) {
                int studentId = (int) i;
                String department = DEPARTMENTS[rand.nextInt(DEPARTMENTS.length)];

                List<String> existingDepartments = studentsToDepartments.get(studentId);
                if (existingDepartments == null) {
                    assert !studentsToDepartments.containsKey(studentId);
                    existingDepartments = new ArrayList<String>();
                    studentsToDepartments.put(studentId, existingDepartments);
                }

                if (!existingDepartments.contains(department)) {
                    existingDepartments.add(department);
                    outFile.println(studentId + FIELD_SEP + department);
                }
                // else (if the student already registered to the department), skip it
            }
        } finally {
            if (outFile != null) outFile.close();
        }
    }

    /* Faculty Table */

    /**
     * Generates, writes out the data for the Faculty table.
     */
    private void writeFacultyTable(String filename) throws Exception {

        // Use this table to map professor_ids to departments, so
        // no professor is faculty member of "multiple copies" of the same department.
        Map<Integer, List<String>> professorsToDepartments = new HashMap<Integer, List<String>>();

        PrintWriter outFile = null;

        try {
            outFile = new PrintWriter(filename);

            long totalDepartments = this.getTotalProfessorDepartments();
            for (long i = 0 ; i < totalDepartments; ++i) {
                int professorId = rand.nextInt(getCountProfessors());
                String department = DEPARTMENTS[rand.nextInt(DEPARTMENTS.length)];

                List<String> existingDepartments = professorsToDepartments.get(professorId);
                if (existingDepartments == null) {
                    assert !professorsToDepartments.containsKey(professorId);
                    existingDepartments = new ArrayList<String>();
                    professorsToDepartments.put(professorId, existingDepartments);
                }

                if (!existingDepartments.contains(department)) {
                    existingDepartments.add(department);
                    outFile.println(professorId + FIELD_SEP + department);
                }
                // else (if the professor already is faculty member of the department), skip it
            }
        } finally {
            if (outFile != null) outFile.close();
        }
    }

    /* Advisor Table */

    /**
     * Generates, writes out the data for the Advisor table
     */
    private void writeAdvisorTable(String filename) throws Exception {

        // Use this table to map students to professors and to avoid duplicate (student, professor) tuples.
        Map<Integer, List<Integer>> studentsToProfessors = new HashMap<Integer, List<Integer>>();

        PrintWriter outFile = null;
        try {
            outFile = new PrintWriter(filename);

            long totalAdvisors = this.getTotalAdvisors();
            for (long i = 0 ; i < totalAdvisors ; ++i) {
                int studentId = rand.nextInt(getCountStudents());
                int professorId = rand.nextInt(getCountProfessors());

                List<Integer> existingProfessors = studentsToProfessors.get(studentId);
                if (existingProfessors == null) {
                    assert !studentsToProfessors.containsKey(studentId);
                    existingProfessors = new ArrayList<Integer>();
                    studentsToProfessors.put(studentId, existingProfessors);
                }

                boolean isProfessor = false;
                for (Professor p : generatedProfessors) {
                    if (p.professorId == professorId && p.title.equalsIgnoreCase("Professor")) {
                        isProfessor = true;
                        break;
                    }
                }

                if (!existingProfessors.contains(professorId) && existingProfessors.size() < 2) {
                    // if there exists advisor(s) for a student, at least one of them should have the title “Professor”.
                    if ((isProfessor) || (!isProfessor && existingProfessors.size() == 1)) {
                        existingProfessors.add(professorId);
                        outFile.println(studentId + FIELD_SEP + professorId);
                    }
                }
                // else (if the student already advised by that professor), skip it
                // else (if the student already has two advisors), skip it
            }
        } finally {
            if (outFile != null) outFile.close();
        }
    }

    /* Course Table */

    /**
     * Generates, writes out the data for the Course table.
     */
    private void writeCourseTable(String filename) throws FileNotFoundException {

        PrintWriter outFile = null;

        try {
            outFile = new PrintWriter(filename);

            int countCourses = this.getCountCourses();
            for (int i = 0 ; i < countCourses ; ++i) {
                outFile.println(makeCourseRecord(i));
            }
        } finally {
            if (outFile != null) outFile.close();
        }
    }

    /**
     * Generates a course record (course_code, course_name).
     */
    private String makeCourseRecord(int id) {

        String uniqueCourseName = COURSE_NAMES[rand.nextInt(COURSE_NAMES.length)] + "_" + id;

        return id + FIELD_SEP + uniqueCourseName;
    }

    /* Teaching Table */

    /**
     * Generates, writes out the data for the Teaching table
     */
    private void writeTeachingTable(String filename) throws Exception {

        // Use this table to map professors to courses and to avoid duplicate (professor, course) tuples.
        Map<Integer, List<Integer>> professorsToCourses = new HashMap<Integer, List<Integer>>();

        PrintWriter outFile = null;
        try {
            outFile = new PrintWriter(filename);

            long totalTeachings = this.getTotalTeachings();
            for (long i = 0 ; i < totalTeachings ; ++i) {
                int professorId = rand.nextInt(getCountProfessors());
                int courseCode = rand.nextInt(getCountCourses());

                List<Integer> existingCourses = professorsToCourses.get(professorId);
                if (existingCourses == null) {
                    assert !professorsToCourses.containsKey(professorId);
                    existingCourses = new ArrayList<Integer>();
                    professorsToCourses.put(professorId, existingCourses);
                }

                if (!existingCourses.contains(courseCode)) {
                    existingCourses.add(courseCode);
                    outFile.println(professorId + FIELD_SEP + courseCode);
                }
                // else (if the professor already teaches that course), skip it
            }
        } finally {
            if (outFile != null) outFile.close();
        }
    }

    /* Enrolled Table */

    /**
     * Generates, writes out the data for the Enrolled table
     */
    private void writeEnrolledTable(String filename) throws Exception {

        // Use this table to map students to courses and to avoid duplicate (student, course) tuples.
        Map<Integer, List<Integer>> studentsToCourses = new HashMap<Integer, List<Integer>>();

        PrintWriter outFile = null;
        try {
            outFile = new PrintWriter(filename);

            long numberOfCourses = this.getCountCourses();
            for (long i = 0 ; i < numberOfCourses ; ++i) {
                for (long j = 0 ; j < 5 ; ++j) {
                    int studentId = rand.nextInt(getCountStudents());
                    int courseCode = (int) i;

                    List<Integer> existingCourses = studentsToCourses.get(studentId);
                    if (existingCourses == null) {
                        assert !studentsToCourses.containsKey(studentId);
                        existingCourses = new ArrayList<Integer>();
                        studentsToCourses.put(studentId, existingCourses);
                    }

                    if (!existingCourses.contains(courseCode)) {
                        existingCourses.add(courseCode);
                        outFile.println(studentId + FIELD_SEP + courseCode);
                    }
                    // else (if the student already enrolled to that course), skip it
                }
            }
        } finally {
            if (outFile != null) outFile.close();
        }
    }

}
