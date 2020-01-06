import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.print.attribute.standard.DateTimeAtCompleted;

import java.io.FileInputStream;

public class Query {

	// DB Connections.
	private Connection _imdb;
	private Connection _customer_db;
	
	// Queries.
	private String _there_exists_such_plan_sql = "SELECT * FROM \"Plan\" WHERE plan_id = ?";
	private PreparedStatement _there_exists_such_plan_statement;

	private String _there_exists_such_movie_sql = "SELECT * FROM Movies WHERE tconst = ?";
	private PreparedStatement _there_exists_such_movie_statement;

	// My Queries
	private String _there_exists_such_user_sql = "SELECT * FROM \"Customer\" WHERE email = ?";
	private PreparedStatement _there_exists_such_user_statement;
	
	private String _sign_up_user_sql = "INSERT INTO \"Customer\" (email, password, "
										+ "first_name, last_name, session_count)"
										+ " VALUES(?, ?, ? , ?, 0)";
	private PreparedStatement _sign_up_user_statement;
	
	private String _add_subscription_sql = "INSERT INTO \"Subscription\" (customer_id, plan_id)"
										+ " VALUES(?,?)";
	private PreparedStatement _add_subscription_statement;
	
	private String _sign_in_user_sql = 	"SELECT c.customer_id, c.first_name, c.last_name, c.session_count, p.max_parallel_session "
										+ "FROM \"Customer\" as c, \"Plan\" as p, \"Subscription\" as s "
										+ "WHERE c.customer_id = s.customer_id and "
										+ "s.plan_id = p.plan_id and "
										+ "c.email = ? and c.password = ?";
	private PreparedStatement _sign_in_user_statement;
	
	private String _sign_in_increment_session_count_sql = "UPDATE \"Customer\" "
														+ "SET session_count = ? WHERE email = ?";
	private PreparedStatement _sign_in_increment_session_count_statement;
	
	private String _sign_out_decrement_session_count_sql = 	"UPDATE \"Customer\" "
															+ "SET session_count = ? WHERE customer_id = ?";
	private PreparedStatement _sign_out_decrement_session_count_statement;
	
	private String _find_user_by_id_sql = "SELECT * FROM \"Customer\" where customer_id = ?";
	private PreparedStatement _find_user_by_id_statement;
	
	private String _list_all_plans_sql = "SELECT * FROM \"Plan\" ORDER BY plan_id";
	private PreparedStatement _list_all_plans_statement;
	
	private String _list_all_subscription_sql = "SELECT * FROM \"Subscription\" ORDER BY s_id, customer_id";
	private PreparedStatement _list_all_subscription_statement;
	
	private String _find_customers_plan_by_customer_id_sql = "SELECT p.max_parallel_session "
															+ "FROM \"Subscription\" as s, \"Plan\" as p "
															+ "WHERE s.customer_id = ? and s.plan_id = p.plan_id";
	private PreparedStatement _find_customers_plan_by_customer_id_statement;
	
	private String _find_plan_by_id_sql = "SELECT * FROM \"Plan\" WHERE plan_id = ?";
	private PreparedStatement _find_plan_by_id_statement;
	
	private String _change_Subscription_sql = "UPDATE \"Subscription\" "
											+ "SET plan_id = ? WHERE customer_id = ?";
	private PreparedStatement _change_Subscription_statement;
	
	private String _add_watched_sql = "INSERT INTO \"Watched\" (movie_id, customer_id, \"when\") "
									+ "VALUES(?,?,?)";
	private PreparedStatement _add_watched_statement;
	
	private String _find_movie_like_name_sql = 	"SELECT tconst, originalTitle "
												+ "FROM Movies WHERE LOWER(originalTitle) LIKE ?";
	private PreparedStatement _find_movie_like_name_statement;

	private String _find_directors_of_movie_sql = "SELECT d.primaryName "
												+ "FROM Directors d, Directed d2 "
												+ "WHERE d2.tconst = ? "
												+ "and d.nconst = d2.director";
	private PreparedStatement _find_directors_of_movie_statement;
	
	private String _find_actors_in_movie_sql = 	"SELECT a.primaryName "
												+ "FROM Actors a, Casts c "
												+ "WHERE c.nconst = a.nconst and "
												+ "c.tconst = ?";
	private PreparedStatement _find_actors_in_movie_statement;
	
	private String _find_watched_movie_by_movie_id_sql = "SELECT Count(*) as ct FROM \"Watched\" "
														+ "where customer_id = ? and movie_id = ?";
	private PreparedStatement _find_watched_movie_by_movie_id_statement;
	
	private String _find_latest_watched_movie_by_customer_id_sql = "SELECT movie_id "
																	+ "FROM \"Watched\" "
																	+ "WHERE customer_id = ? "
																	+ "ORDER BY \"when\" DESC "
																	+ "LIMIT 1";
	private PreparedStatement _find_latest_watched_movie_by_customer_id_statement;
	
	private String _find_lexicographically_greatest_genre_of_movie_sql = "SELECT genre "
																		+ "FROM Genres Where tconst = ? "
																		+ "ORDER BY genre DESC LIMIT 1";
	private PreparedStatement _find_lexicographically_greatest_genre_of_movie_statement;
			
	private String _find_movies_with_genre_and_released_this_year_sql = "SELECT m.tconst, m.originalTitle, m.AverageRating "
																		+ "FROM Genres g, Movies m "
																		+ "Where g.tconst = m.tconst and "
																		+ "g.genre = ? and "
																		+ "m.startYear = ? "
																		+ "ORDER BY m.averageRating DESC LIMIT 5";
	private PreparedStatement _find_movies_with_genre_and_released_this_year_statement;
	
	private String _find_watched_movies_of_customer_sql = "SELECT movie_id FROM \"Watched\" "
														+ "WHERE customer_id = ?";
	
	private PreparedStatement _find_watched_movies_of_customer_statement;
			
	public Query() {

	}

	public void openConnection() throws Exception {
		// DB connection configurations
		Properties configProps = new Properties();

		configProps.load(new FileInputStream(new File(this.getClass().getResource("dbconn.config").toURI())));

		String mysqlDriver			= configProps.getProperty("mysqlDriver");
		String mysqlUrl				= configProps.getProperty("mysqlUrl");
		String mysqlUser			= configProps.getProperty("mysqlUser");
		String mysqlPassword		= configProps.getProperty("mysqlPassword");

		String postgresqlDriver		= configProps.getProperty("postgresqlDriver");
		String postgresqlUrl		= configProps.getProperty("postgresqlUrl");
		String postgresqlUser		= configProps.getProperty("postgresqlUser");
		String postgresqlPassword	= configProps.getProperty("postgresqlPassword");

		// Load jdbc drivers
		Class.forName(mysqlDriver);
		Class.forName(postgresqlDriver);

		// Open connections to two databases: imdb and the customer database
		_imdb = DriverManager.getConnection(mysqlUrl, // database
				mysqlUser, // user
				mysqlPassword); // password

		_customer_db = DriverManager.getConnection(postgresqlUrl, // database
				postgresqlUser, // user
				postgresqlPassword); // password
	}

	public void closeConnection() throws Exception {
		_imdb.close();
		_customer_db.close();
	}

	// Prepare all the SQL statements in this method.
	// Preparing a statement is almost like compiling it.
	// Note that the parameters (with ?) are still not filled in.

	public void prepareStatements() throws Exception {

		_there_exists_such_plan_statement = _customer_db.prepareStatement(_there_exists_such_plan_sql);
		_there_exists_such_movie_statement = _imdb.prepareStatement(_there_exists_such_movie_sql);

		// add here more prepare statements for all the other queries you need. TODO.
		// Prepare user check
		_there_exists_such_user_statement = _customer_db.prepareStatement(_there_exists_such_user_sql);
		// User save
		// User's customer_id will be returned upon creation
		_sign_up_user_statement = _customer_db.prepareStatement(_sign_up_user_sql, new String[] {"customer_id"});
		// Subscription adding
		_add_subscription_statement = _customer_db.prepareStatement(_add_subscription_sql);
		// Sign in user
		_sign_in_user_statement = _customer_db.prepareStatement(_sign_in_user_sql);
		// Upon successful sign in, i.e. session count is allowed, increment session count
		_sign_in_increment_session_count_statement = _customer_db.prepareStatement(_sign_in_increment_session_count_sql);
		// Decrement session count upon sign out
		_sign_out_decrement_session_count_statement = _customer_db.prepareStatement(_sign_out_decrement_session_count_sql);		
		// Find user by customer id
		_find_user_by_id_statement = _customer_db.prepareStatement(_find_user_by_id_sql);
		// list all available plans
		_list_all_plans_statement = _customer_db.prepareStatement(_list_all_plans_sql);
		// list current subscriptions
		_list_all_subscription_statement = _customer_db.prepareStatement(_list_all_subscription_sql);
		// finds customer's current plan by customer id
		_find_customers_plan_by_customer_id_statement = _customer_db.prepareStatement(_find_customers_plan_by_customer_id_sql);
		// find plan by plan id
		_find_plan_by_id_statement = _customer_db.prepareStatement(_find_plan_by_id_sql);
		// change subscription of the customer
		_change_Subscription_statement = _customer_db.prepareStatement(_change_Subscription_sql);
		// add movie to watched
		_add_watched_statement = _customer_db.prepareStatement(_add_watched_sql);
		// find id and name of the movie, given like "name"
		_find_movie_like_name_statement = _imdb.prepareStatement(_find_movie_like_name_sql);
		// find director name of the given movie
		_find_directors_of_movie_statement = _imdb.prepareStatement(_find_directors_of_movie_sql);
		// find name of actors of the given movie
		_find_actors_in_movie_statement = _imdb.prepareStatement(_find_actors_in_movie_sql);
		// checks whether the current user has watched the movie or not
		_find_watched_movie_by_movie_id_statement = _customer_db.prepareStatement(_find_watched_movie_by_movie_id_sql);
		// Finds movie id of latest watched movie, given customer id 
		_find_latest_watched_movie_by_customer_id_statement = _customer_db.prepareStatement(_find_latest_watched_movie_by_customer_id_sql);
		// finds lexicographically greatest genre of given movie id
		_find_lexicographically_greatest_genre_of_movie_statement = _imdb.prepareStatement(_find_lexicographically_greatest_genre_of_movie_sql);
		// finds movies with same given genre and released this year
		_find_movies_with_genre_and_released_this_year_statement = _imdb.prepareStatement(_find_movies_with_genre_and_released_this_year_sql);
		// finds watched movies of given customer, returns movie id's.
		_find_watched_movies_of_customer_statement = _customer_db.prepareStatement(_find_watched_movies_of_customer_sql);
	}

	/**
	 * Finds whether the plan exists or not
	 * @param plan_id plan id
	 * @return false if the plan is not found, true if the plan is found
	 * @throws Exception exception
	 */
	public boolean helper_there_exists_such_plan(int plan_id) throws Exception {
		_there_exists_such_plan_statement.clearParameters();
		_there_exists_such_plan_statement.setInt(1, plan_id);

		ResultSet rs = _there_exists_such_plan_statement.executeQuery();

		boolean there_exists_such_plan = rs.next();

		rs.close();

		return there_exists_such_plan;
	}

	/**
	 * Finds whether the movie exists or not
	 * @param movie_id imdb movie id
	 * @return false if the movie is not found, true if the movie is found
	 * @throws Exception exception
	 */
	public boolean helper_there_exists_such_movie(String movie_id) throws Exception {
		_there_exists_such_movie_statement.clearParameters();
		_there_exists_such_movie_statement.setString(1, movie_id);

		ResultSet rs = _there_exists_such_movie_statement.executeQuery();

		boolean there_exists_such_movie = rs.next();

		rs.close();

		return there_exists_such_movie;
	}

	/**
	 * Try to find whether the email is registered or not
	 * TODO
	 * @param email email of the user
	 * @return true if there is registered user with that email, 
	 * false if the email is not registered
	 * @throws Exception exception
	 */
	public boolean helper_already_signed_up(String email) throws Exception {
		_there_exists_such_user_statement.clearParameters();
		_there_exists_such_user_statement.setString(1, email);
		
		ResultSet rs = _there_exists_such_user_statement.executeQuery();
		
		boolean there_exists_such_user = rs.next();
		rs.close();
		
		return there_exists_such_user;
	}

	/**
	 * Need to register the user to Customer Table and
	 * Create subscription to the plan
	 * @param email email of the user
	 * @param password password of the user
	 * @param first_name first name of the user
	 * @param last_name last name of the user
	 * @param plan_id plan id
	 * @return true on successful, false on error
	 * @throws Exception SQL exception
	 */
	public boolean transaction_sign_up(String email, String password, String first_name, String last_name, int plan_id) throws Exception {
		try {
			_customer_db.setAutoCommit(false);
			_sign_up_user_statement.clearParameters();
			// set parameters
			// customer id is auto increment
			_sign_up_user_statement.setString(1, email);
			_sign_up_user_statement.setString(2, password);
			_sign_up_user_statement.setString(3, first_name);
			_sign_up_user_statement.setString(4, last_name);
			// initial session count is 0
			
			// Save the user, then fetch his id
			int affectedRows = _sign_up_user_statement.executeUpdate();
			if(affectedRows > 0) {
				ResultSet idResult = _sign_up_user_statement.getGeneratedKeys();
				if(idResult.next()) {
					int new_customer_id = idResult.getInt(1);
					_add_subscription_statement.clearParameters();
					// set parameters
					// subscription id is auto increment
					_add_subscription_statement.setInt(1, new_customer_id);
					_add_subscription_statement.setInt(2, plan_id);
					
					// save subscription
					int affectedRows2 = _add_subscription_statement.executeUpdate();
					if(affectedRows2 > 0) {
						_customer_db.commit();
						return true;
					}
					else {
						System.out.println("Exception in subscription after user signup");
						_customer_db.rollback();
					}
				}
				else {
					System.out.println("Exception in fetching the id of the customer");
					_customer_db.rollback();
				}			
			}
		}
		catch(SQLException e) {
			_customer_db.rollback();
			System.out.println("Exception in sign_up " + e);
		}
		return false;
	}

	
	/**
	 * Helper function. Given customer id, try to increment session count.
	 * @param rs result set containing customer's information
	 * @param email email of the customer
	 * @param password password of the customer
	 * @return Customer if successfully incremented, null if an error or the
	 * max parallel session is exceeded
	 */
	private Customer incrementSessionCount(	int customer_id, 
											ResultSet rs, 
											String email, 
											String password) throws SQLException{
		int session_count = rs.getInt("session_count");
		int max_parallel_session = rs.getInt("max_parallel_session");

		if(session_count < max_parallel_session) {
			// User can sign in
			session_count += 1;
			_sign_in_increment_session_count_statement.clearParameters();
			// set parameters
			_sign_in_increment_session_count_statement.setInt(1, session_count);
			_sign_in_increment_session_count_statement.setString(2, email);
			
			// sign in the user, increment session count
			int affectedRows = _sign_in_increment_session_count_statement.executeUpdate();
			if(affectedRows > 0) {
				_customer_db.commit();
				Customer customer = new Customer(customer_id,
						email,
						password,
						rs.getString("first_name"),
						rs.getString("last_name"),
						session_count);
				return customer;
			}
			else {
				System.out.println("Something went wrong during session increment");
				_customer_db.rollback();
				return null;
			}
		}
		else {
			// User cannot sign in, max parallel session limit is reached
			System.out.println("Cannot sign in. Max parallel session limit is reached");
			_customer_db.rollback();
			return null;
		}
	}
	
	/**
	 * Fetches the email, password, session count, plan id, max parallel session.
	 * Check credentials. 
	 * If the session count is below max parallel session, allow user to sign in.
	 * @param email
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public Customer transaction_sign_in(String email, String password) throws Exception {
		try {
			_customer_db.setAutoCommit(false);
			_sign_in_user_statement.clearParameters();
			// set parameters
			_sign_in_user_statement.setString(1, email);
			_sign_in_user_statement.setString(2, password);
			
			ResultSet rs = _sign_in_user_statement.executeQuery();
			rs.next();
			
			int customer_id = rs.getInt("customer_id");

			if(customer_id > 0) {
				return incrementSessionCount(customer_id, rs, email, password);
			}
			else {
				// There is no result
				System.out.println("Wrong email or password.");
				_customer_db.rollback();
			}
		}
		catch(Exception e) {
			System.out.println("Exception in transaction_sign_in " + e);
			_customer_db.rollback();
		}
		return null;
	}

	
	/**
	 * Decrements session count.
	 * @param customer_id customer id
	 * @param rs result set containing customer information
	 * @return
	 */
	private boolean decrementSessionCount(int customer_id, ResultSet rs) throws SQLException {
		int session_count = rs.getInt("session_count");
		if(session_count <= 0) {
			return false;
		}
		session_count -= 1;
		_sign_out_decrement_session_count_statement.clearParameters();
		// set parameters
		_sign_out_decrement_session_count_statement.setInt(1, session_count);
		_sign_out_decrement_session_count_statement.setInt(2, customer_id);
		
		int affectedRows = _sign_out_decrement_session_count_statement.executeUpdate();
		
		return (affectedRows>0);
	}
	/**
	 * Finds user, decrements session count
	 * @param customer_id customer id
	 * @return true on successful sign out
	 * @throws Exception
	 */
	public boolean transaction_sign_out(int customer_id) throws Exception {
		try {
			_customer_db.setAutoCommit(false);
			_find_user_by_id_statement.clearParameters();
			
			// set parameters
			_find_user_by_id_statement.setInt(1, customer_id);
			ResultSet rs = _find_user_by_id_statement.executeQuery();
			
			rs.next();
			
			if(decrementSessionCount(customer_id, rs)) {
				_customer_db.commit();
				System.out.println("Successfully signed out");
				return true;
			}
			else {
				_customer_db.rollback();
				System.out.println("Cannot sign out");
			}
		}
		catch(Exception e) {
			System.out.println("Exception in transaction_sign_in " + e);
			_customer_db.rollback();
		}
		return false;
	}

	/**
	 * Prints all available plans
	 * @throws Exception SQL exception
	 */
	public void transaction_show_plans() throws Exception {
		try {
			_list_all_plans_statement.clearParameters();
			ResultSet rs = _list_all_plans_statement.executeQuery();
			System.out.printf("%s%s%s%s%s\n",
					String.format("%1$-15s", "Plan name"),
					String.format("%1$-10s", "Plan id"),
					String.format("%1$-15s", "Resolution"),
					String.format("%1$-25s", "Max parallel session"),
					String.format("%1$-10s", " Monthly fee"));
			System.out.printf("---------------"); // 15 -
			System.out.printf("----------"); // 10 -
			System.out.printf("---------------"); // 15 -
			System.out.printf("-------------------------"); // 25 -
			System.out.printf("----------\n"); // 10 -
			
			while(rs.next()) {
				System.out.printf("%s%s%s%s%s\n", 
						String.format("%1$-15s", rs.getString("name")),
						String.format("%1$-10s", rs.getInt("plan_id")),
						String.format("%1$-15s", rs.getString("resolution")),
						String.format("%1$-25s", rs.getInt("max_parallel_session")),
						String.format("%1$-10s", rs.getFloat("monthly_fee")));
			}
		}
		catch(Exception e) {
			System.out.println("Exception during fetching plans " + e);
		}
	}

	/**
	 * Prints all available subscriptions
	 * @param customer_id customer id
	 * @throws Exception SQL exception
	 * 
	 */
	public void transaction_show_subscription(int customer_id) throws Exception {
		try {
			_list_all_subscription_statement.clearParameters();
			ResultSet rs = _list_all_subscription_statement.executeQuery();
			System.out.printf("%s%s%s\n",
					String.format("%1$-15s", "Subcription id"),
					String.format("%1$-15s", "Customer id"),
					String.format("%1$-10s", "Plan id"));
			System.out.printf("---------------"); // 15 -
			System.out.printf("---------------"); // 15 -
			System.out.printf("----------\n"); // 10 -
			
			while(rs.next()) {
				System.out.printf("%s%s%s\n", 
						String.format("%1$-15s", rs.getString("s_id")),
						String.format("%1$-15s", rs.getInt("customer_id")),
						String.format("%1$-10s", rs.getString("plan_id")));
			}
		}
		catch(Exception e) {
			System.out.println("Exception during fetching subscriptions " + e);
		}
	}

	/**
	 * Update customer's subscription to new subscription
	 * Do not allow customer to change his subscription to lower parallel session
	 * @param customer_id customer id
	 * @param plan_id plan id
	 * @return true on success, false otherwise
	 * @throws Exception SQL exception
	 */
	public boolean transaction_subscribe(int customer_id, int plan_id) throws Exception {
		try {
			_customer_db.setAutoCommit(false);
			// set parameters for current plan
			_find_customers_plan_by_customer_id_statement.clearParameters();
			_find_customers_plan_by_customer_id_statement.setInt(1, customer_id);
			// set parameters for other plan
			_find_plan_by_id_statement.clearParameters();
			_find_plan_by_id_statement.setInt(1, plan_id);
			
			// Fetch customer's current plan
			ResultSet rs1 = _find_customers_plan_by_customer_id_statement.executeQuery();
			rs1.next();
			// Fetch other plan
			ResultSet rs2 = _find_plan_by_id_statement.executeQuery();
			rs2.next();
			
			int currentMax = rs1.getInt("max_parallel_session");
			int otherMax = rs2.getInt("max_parallel_session");
			
			if(currentMax <= otherMax) {
				// allowed
				_change_Subscription_statement.clearParameters();
				// set parameters
				_change_Subscription_statement.setInt(1, plan_id);
				_change_Subscription_statement.setInt(2, customer_id);
				
				int affectedRows = _change_Subscription_statement.executeUpdate();
				if(affectedRows > 0) {
					_customer_db.commit();
					return true;
				}
			}
			else {
				// not allowed
				System.out.println("Cannot subscribe to lower max parallel session");
				_customer_db.rollback();
			}
		}
		catch(Exception e) {
			System.out.println("Exception in transaction_subscribe " + e);
			_customer_db.rollback();
		}		
		return false;
	}

	/**
	 * Add watched entry to the DB(customer DB).
	 * 
	 * @param movie_id IMDB movie id
	 * @param customer_id customer id
 	 * @return true on successful add, false otherwise
	 * @throws Exception SQL exception
	 */
	public boolean transaction_watch(String movie_id, int customer_id) throws Exception {
		try {
			_customer_db.setAutoCommit(false);
			_add_watched_statement.clearParameters();
			// Set parameters
			_add_watched_statement.setString(1, movie_id);
			_add_watched_statement.setInt(2, customer_id);
			// Set date to now
			Calendar calendar = Calendar.getInstance();
			java.sql.Date date = new java.sql.Date(calendar.getTime().getTime());
			_add_watched_statement.setDate(3, date);
			// execute query
			int affectedRows = _add_watched_statement.executeUpdate();
			if(affectedRows > 0) {
				_customer_db.commit();
				return true;
			}
			else {
				System.out.println("Error in transaction watch");
				_customer_db.rollback();
			}
		}
		catch(Exception e) {
			System.out.println("Exception in transaction_watch " + e);
			_customer_db.rollback();
		}

		return false;
	}

	/**
	 * Helper for transaction_search_for_movies.
	 * Finds actors that played in the movie
	 * @param tconst movie id
	 * @return list of actors' names
	 * @throws SQLException SQL exception
	 */
	private List<String> findActorsOfMovie(String tconst) throws SQLException{
		_find_actors_in_movie_statement.clearParameters();
		_find_actors_in_movie_statement.setString(1, tconst);
		
		List<String> actors = new ArrayList<>();
		ResultSet rsActors = _find_actors_in_movie_statement.executeQuery();
		while(rsActors.next()) {
			actors.add(rsActors.getString("primaryName"));
		}
		return actors;
	}
	
	/**
	 * Helper for transaction_search_for_movies.
	 * Finds directors that directed the movie
	 * @param tconst movie id
	 * @return list of directors' names
	 * @throws SQLException SQL exception
	 */
	private List<String> findDirectorsOfMovie(String tconst) throws SQLException{
		_find_directors_of_movie_statement.clearParameters();
		_find_directors_of_movie_statement.setString(1, tconst);
		
		List<String> directors = new ArrayList<>();
		// populate directors list
		ResultSet rsDirectors = _find_directors_of_movie_statement.executeQuery();
		while(rsDirectors.next()) {
			directors.add(rsDirectors.getString("primaryName"));
		}
		return directors;
	}
	
	/**
	 * Helper function for printing search for movies
	 * @param tconst movie id
	 * @param originalTitle movie name
	 * @param actors list of actors' names
	 * @param directors list of directors' names
	 */
	private void printSearchForMovies(String tconst, 
			String originalTitle, 
			List<String> actors,
			List<String> directors) {
		System.out.println("movie id:" + tconst);
		System.out.println("movie name:" + originalTitle);
		// print directors
		System.out.printf("directors: ");
		for(String director: directors) {
			System.out.printf(director + "\t");	
		}
		System.out.printf("\n");
		// print actors
		System.out.printf("actors: ");
		for(String actor: actors) {
			System.out.printf(actor + "\t");	
		}
		System.out.printf("\n");
	}
	/**
	 * Helper function for transaction_search_for_movies
	 * Prints whether the user has watched the movie or not
	 */
	private void printWatchedInfo(int customer_id, String tconst) throws SQLException{
		_find_watched_movie_by_movie_id_statement.clearParameters();
		_find_watched_movie_by_movie_id_statement.setInt(1, customer_id);
		_find_watched_movie_by_movie_id_statement.setString(2, tconst);
		ResultSet rs2 = _find_watched_movie_by_movie_id_statement.executeQuery();
		rs2.next();
		int watchedCount = rs2.getInt("ct");
		if(watchedCount > 0) {
			System.out.println("Watched " + watchedCount + " times");
		}
		else {
			System.out.println("Not watched");
		}
	}
	
	public void transaction_search_for_movies(int customer_id, String movie_title) throws Exception {
		try {
			_find_movie_like_name_statement.clearParameters();
			// set parameter
			_find_movie_like_name_statement.setString(1, "%" + movie_title + "%");
			
			ResultSet rs = _find_movie_like_name_statement.executeQuery();
			while(rs.next()) {
				String tconst = rs.getString("tconst");
				String originalTitle = rs.getString("originalTitle");
				
				// find directors
				List<String> directors = findDirectorsOfMovie(tconst);
				// find actors
				List<String> actors = findActorsOfMovie(tconst);
				// print movie info
				printSearchForMovies(tconst, originalTitle, actors, directors);
				// print whether the user has watched this movie or not
				printWatchedInfo(customer_id, tconst);
				System.out.println("-------------------------------------------------");
			}
		}
		catch(Exception e) {
			System.out.println("Exception in transaction_search_for_movies " + e);
		}
	}

	/**
	 * Helper method for transaction_suggest_movies.
	 * Finds movies that customer have watched, populates array with movie id's
	 * @param customer_id customer id
	 * @return list of movie id's
	 */
	private List<String> listOfMoviesCustomerWatched(int customer_id) throws SQLException{
		List<String> movieList = new ArrayList<>();
		// We are sure that user have watched at least 1 movie, because of the rs loop
		_find_watched_movies_of_customer_statement.clearParameters();
		_find_watched_movies_of_customer_statement.setInt(1, customer_id);
		
		ResultSet rsCustomersMovies = _find_watched_movies_of_customer_statement.executeQuery();
		while(rsCustomersMovies.next()) {
			movieList.add(rsCustomersMovies.getString("movie_id"));
		}
		return movieList;
	}
	
	/**
	 * Helper method for transaction_suggest_movies.
	 * Finds top 5 average rating movie with given genre in this year.
	 * @param genre genre of the movie
	 * @return list of suggested movies
	 * @throws SQLException SQL exception
	 */
	private List<SuggestMovie> find5MovieWithGenreAndYear(String genre) throws SQLException{
		List<SuggestMovie> suggestedMovies = new ArrayList<>();
		
		int startYear = Calendar.getInstance().get(Calendar.YEAR);
		
		_find_movies_with_genre_and_released_this_year_statement.clearParameters();
		// set parameters
		_find_movies_with_genre_and_released_this_year_statement.setString(1, genre);
		_find_movies_with_genre_and_released_this_year_statement.setInt(2, startYear);
		
		ResultSet rs = _find_movies_with_genre_and_released_this_year_statement.executeQuery();
		while(rs.next()) {
			String tconst = rs.getString("tconst");
			String originalTitle = rs.getString("originalTitle");
			double averageRating = rs.getDouble("AverageRating");
			SuggestMovie sm = new SuggestMovie(tconst, originalTitle, averageRating);
			suggestedMovies.add(sm);
		}
		
		return suggestedMovies;
	}
	
	
	/**
	 * Helper method for transaction_suggest_movies.
	 * Prints suggested movies. If the user has watched the movie, does not print. 
	 * @param customerMovieList customer movie list, list of id's
	 * @param suggestedMovies suggested movies, list of SuggestMovie
	 */
	private void printSuggestedMovies(List<String> customerMovieList, List<SuggestMovie> suggestedMovies) {
		System.out.println("Suggested movies for you are:");
		System.out.printf("%s%s%s\n",
				String.format("%1$-15s", "Movie id"),
				String.format("%1$-25s", "Title"),
				String.format("%1$-15s", "Average Rating"));
		System.out.printf("---------------"); // 15 -
		System.out.printf("-------------------------"); // 25 -
		System.out.printf("---------------\n"); // 15 -
		
		for(SuggestMovie sm: suggestedMovies) {
			boolean watched = false;
			for(String customerMovie: customerMovieList) {
				if(customerMovie.equals(sm.getTconst())) {
					watched = true;
					break;
				}
			}
			// suggest the movie, if it is not watched
			if(!watched) {
				System.out.printf("%s%s%s\n",
						String.format("%1$-15s", sm.getTconst()),
						String.format("%1$-25s", sm.getOriginalTitle()),
						String.format("%1$-15s", sm.getAverageRating()));
			}
		}
	}
	
	/**
	 * Suggest movies by recently watched movie
	 * @param customer_id customer id
	 * @throws Exception SQL Exception
	 */
	public void transaction_suggest_movies(int customer_id) throws Exception {
		try {
			_customer_db.setAutoCommit(false);
			_find_latest_watched_movie_by_customer_id_statement.clearParameters();
			// set parameters
			_find_latest_watched_movie_by_customer_id_statement.setInt(1, customer_id);
			ResultSet rs = _find_latest_watched_movie_by_customer_id_statement.executeQuery();
			
			// this loop will be executed at most once, due to query above returns 1 row
			while(rs.next()) {
				String tconst = rs.getString("movie_id");
				_find_lexicographically_greatest_genre_of_movie_statement.clearParameters();
				// set parameters
				_find_lexicographically_greatest_genre_of_movie_statement.setString(1, tconst);
				ResultSet rs2 = _find_lexicographically_greatest_genre_of_movie_statement.executeQuery();
				// this loop will be executed at most once, due to query above returns 1 row
				while(rs2.next()) {
					String genre = rs2.getString("genre");
					
					// finds list of movies that the customer have watched
					List<String> customerMovieList = listOfMoviesCustomerWatched(customer_id);
					List<SuggestMovie> suggestedMovies = find5MovieWithGenreAndYear(genre);
					// print
					printSuggestedMovies(customerMovieList, suggestedMovies);
				}
			}
			// If everything executed correctly
			_customer_db.commit();
		}
		catch(Exception e) {
			System.out.println("Exception in transaction_suggest_movies " + e);
			_customer_db.rollback();
		}
	}

	/**
	 * For test purposes
	 * @return customer DB connection
	 */
	public Connection getCustomerDbConnection() {
		return _customer_db;
	}
}

