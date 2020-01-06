import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.FileInputStream;

/**
 * Runs the test query set.
 */

public class QueryTester {

	private static Connection conn;

	private static int scaleFactor = 1;  //controls how many times each query runs. relationship is linear.

	/* */

	// Examine these queries: you need to tune the database accordingly.

	// Look up a professor with a given title.
	private static final String professorTitle =
			"SELECT first_name, last_name " +
			"FROM Professor WHERE title = ?";

	// Look up all advisors (professors) for a student.
	private static final String professorsForStudent =
			"SELECT P.first_name, P.last_name " +
			"FROM Professor P, Advisor A, Student S " +
			"WHERE P.professor_id = A.professor_id AND A.student_id = S.student_id AND S.first_name = ? AND S.last_name = ?";

	// Look up all students for a professor.
	private static final String studentsForProfessor =
			"SELECT S.first_name, S.last_name " +
			"FROM Professor P, Advisor A, Student S " +
			"WHERE P.professor_id = A.professor_id AND A.student_id = S.student_id AND P.first_name = ? AND P.last_name = ?";

	// Check how many students are registered to a certain department.
	private static final String studentCountForDepartment =
			"SELECT R.department, COUNT(*) "  +
			"FROM Registered R " +
			"WHERE R.department = ? " +
			"GROUP BY R.department";

	// Check how many professors are faculty members of a certain department.
	private static final String professorCountForDepartment =
			"SELECT F.department, COUNT(*) "  +
			"FROM Faculty F " +
			"WHERE F.department = ? " +
			"GROUP BY F.department";

	// Count number of students within an age range.
	private static final String studentCountForAgeRange =
			"SELECT COUNT(*) " +
			"FROM Student " +
			"WHERE age >= ? AND age <= ?";

	// Count number of students living in a specific dormitory room.
	private static final String studentCountForDormitoryRoom =
			"SELECT COUNT(*) " +
			"FROM Student " +
			"WHERE dormitory = ? AND room = ?";

	/* */

	// Corresponding prepared statements.
	private static PreparedStatement professorTitleStatement;
	private static PreparedStatement professorsForStudentStatement;
	private static PreparedStatement studentsForProfessorStatement;
	private static PreparedStatement studentCountForDepartmentStatement;
	private static PreparedStatement professorCountForDepartmentStatement;
	private static PreparedStatement studentCountForAgeRangeStatement;
	private static PreparedStatement studentCountForDormitoryRoomStatement;

	private static void openConnection() throws Exception {

		Properties configProps = new Properties();

		configProps.load(new FileInputStream("dbconn.config"));
		String postgreSqlDriver = configProps.getProperty("postgreSqlDriver");
		String postgreSqlUrl = configProps.getProperty("postgreSqlUrl");
		String postgreSqlUser = configProps.getProperty("postgreSqlUser");
		String postgreSqlPassword = configProps.getProperty("postgreSqlPassword");

		Class.forName(postgreSqlDriver);

		conn = DriverManager.getConnection(postgreSqlUrl, postgreSqlUser, postgreSqlPassword);
	}

	private static void closeConnection() throws Exception {

		conn.close();
	}

	private static void prepareStatements() throws Exception {

		professorTitleStatement = conn.prepareStatement(professorTitle);
		professorsForStudentStatement = conn.prepareStatement(professorsForStudent);
		studentsForProfessorStatement = conn.prepareStatement(studentsForProfessor);
		studentCountForDepartmentStatement = conn.prepareStatement(studentCountForDepartment);
		professorCountForDepartmentStatement = conn.prepareStatement(professorCountForDepartment);
		studentCountForAgeRangeStatement = conn.prepareStatement(studentCountForAgeRange);
		studentCountForDormitoryRoomStatement = conn.prepareStatement(studentCountForDormitoryRoom);
	}

	/**
	 * Runs query, stores ResultSet into 2D array.
	 * @param sql the sql query string (must select only String columns).
	 * @param colCount number of columns selected by sql.
	 * @return 2D array containing the results of the query.
	 */
	private static ArrayList<ArrayList<String>> getResults(String sql, int colCount) throws Exception {

		Statement nameStatement = conn.createStatement();
		ResultSet rs = nameStatement.executeQuery(sql);

		ArrayList<ArrayList<String>> values = new ArrayList<>();

		while(rs.next()) {
			ArrayList temp = new ArrayList();
			for(int i = 0 ; i < colCount ; ++i) {
				temp.add(rs.getString(i+1));
			}
			values.add(temp);
		}

		rs.close();

		return values;
	}

	/**
	 * Runs the workload.
	 */
	public static long runQueries () throws Exception {

		ArrayList<ArrayList<String>> studentNames = getResults(
				"SELECT DISTINCT first_name, last_name FROM Student",
				2
		);
		ArrayList<ArrayList<String>> professorNames = getResults(
				"SELECT DISTINCT first_name, last_name FROM Professor",
				2
		);
		ArrayList<ArrayList<String>> professorTitles = getResults(
				"SELECT DISTINCT title FROM Professor",
				1
		);
		ArrayList<ArrayList<String>> dormitoryRooms = getResults(
				"SELECT DISTINCT dormitory, room FROM Student",
				2
		);
		ArrayList<ArrayList<String>> studentDepartments = getResults(
				"SELECT DISTINCT department FROM Registered",
				1
		);
		ArrayList<ArrayList<String>> professorDepartments = getResults(
				"SELECT DISTINCT department FROM Faculty",
				1
		);

		long totalTime = 0;
		for (int i = 0 ; i < 1000 * scaleFactor ; ++i) {
			// Query 1.
			professorTitleStatement.setString(1, professorTitles.get((int) (Math.random() * professorTitles.size())).get(0));

			// Query 2.
			int randomStudentIndex = (int) (Math.random() * studentNames.size());
			String firstName = studentNames.get(randomStudentIndex).get(0);
			String lastName = studentNames.get(randomStudentIndex).get(1);
			professorsForStudentStatement.setString(1, firstName);
			professorsForStudentStatement.setString(2, lastName);

			// Query 3.
			int randomProfessorIndex = (int) (Math.random() * professorNames.size());
			firstName = professorNames.get(randomProfessorIndex).get(0);
			lastName = professorNames.get(randomProfessorIndex).get(1);
			studentsForProfessorStatement.setString(1, firstName);
			studentsForProfessorStatement.setString(2, lastName);

			// Query 4.
			studentCountForDepartmentStatement.setString(1, studentDepartments.get((int) (Math.random() * studentDepartments.size())).get(0));

			// Query 5.
			professorCountForDepartmentStatement.setString(1, professorDepartments.get((int) (Math.random() * professorDepartments.size())).get(0));

			// Query 6.
			int randomAge = 18 + 1 + (new Random()).nextInt(50 - 18 - 2);
			studentCountForAgeRangeStatement.setInt(1, randomAge);
			studentCountForAgeRangeStatement.setInt(2, randomAge + 2);

			// Query 7.
			int randomDormitoryRoomIndex = (int) (Math.random() * dormitoryRooms.size());
			String dormitory = dormitoryRooms.get(randomDormitoryRoomIndex).get(0);
			String room = dormitoryRooms.get(randomDormitoryRoomIndex).get(1);
			studentCountForDormitoryRoomStatement.setString(1, dormitory);
			studentCountForDormitoryRoomStatement.setString(2, room);

			// Run queries, time them and add to totalTime.
			long startTime = System.currentTimeMillis();
			professorTitleStatement.executeQuery().close();
			professorsForStudentStatement.executeQuery().close();
			studentsForProfessorStatement.executeQuery().close();
			studentCountForDepartmentStatement.executeQuery().close();
			professorCountForDepartmentStatement.executeQuery().close();
			studentCountForAgeRangeStatement.executeQuery().close();
			studentCountForDormitoryRoomStatement.executeQuery().close();
			long endTime = System.currentTimeMillis();

			totalTime += (endTime - startTime);
		}

		return totalTime;
	}

	public static void main (String args[]) throws Exception {

		// Get the scale factor (if it is specified).
		if (args.length == 0) {
			scaleFactor = 1; //i.e. the default
		} else if (args.length == 1) {
			try {
				scaleFactor = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println ("Unable to parse scale factor given as: \"" + args[0] + "\"");
				System.exit(2);
			}
		} else {
			System.err.println ("Usage: java QueryTester [scale factor]");
			System.exit(1);
		}

		openConnection();

		prepareStatements();

		System.out.println ("Running the test query workload in database. \r\n");
		long queryTime = runQueries();
		System.out.println ("Queries complete. \r\n Total time: " + queryTime + " ms.");

		closeConnection();
	}
}
