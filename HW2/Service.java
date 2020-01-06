import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Service {

	private static Customer authenticatedCustomer = null;

	private static void usage() {
		// prints the choices for commands and parameters

		System.out.println("\n*** Please enter one of the following comands ***");
		System.out.println("> sign_up <email> <password> <first_name> <last_name> <plan_id>");
		System.out.println("> sign_in <email> <password>");
		System.out.println("> sign_out");
		System.out.println("> show_plans");
		System.out.println("> show_subscription");
		System.out.println("> subscribe <plan_id>");
		System.out.println("> watched_movie <movie_id>");
		System.out.println("> search_for_movies <movie_title>");
		System.out.println("> suggest_movies");
		System.out.println("> quit");
	}

	private static void menu(Query q) {
		// reads the user's command and parameter(s)

		String response = null;

		while (true) {
			try {
				usage();

				if (authenticatedCustomer == null) {
					System.out.print("> ");
				} else {
					System.out.print(authenticatedCustomer.getFirstName() + " " + authenticatedCustomer.getLastName() + " > ");
				}

				BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
				response = r.readLine();

				StringTokenizer st = new StringTokenizer(response);
				String t = st.nextToken();

				if (t.equals("sign_up")) {
					// create a new customer.

					// you should be signed out for this command.
					if (authenticatedCustomer == null) {
						// required parameters for this command are: email, password, first_name, last_name, plan_id.
						if (st.countTokens() >= 5) {
							String email = st.nextToken();
							String password = st.nextToken();
							String first_name = st.nextToken();
							String last_name = st.nextToken();
							int plan_id = Integer.parseInt(st.nextToken());

							// there shouldn't be an already registered customer with same email in the db.
							boolean already_signed_up = q.helper_already_signed_up(email);

							if (already_signed_up) {
								System.out.println("Error: There is an already registered customer with this email '" + email + "'");
							} else {
								// there should be a plan with given id in the db.
								boolean there_exists_such_plan = q.helper_there_exists_such_plan(plan_id);

								if (there_exists_such_plan) {
									System.out.println("Signing up ...");

									boolean successful = q.transaction_sign_up(email, password, first_name, last_name, plan_id);

									if (successful) {
										System.out.println("Signed up successfully!");
									} else {
										System.out.println("There was a problem during signing up!");
									}
								} else {
									System.out.println("Error: There exists no plan with id '" + plan_id + "'");
								}
							}
						} else {
							System.out.println("Error: Required parameters for this command are: email, password, first_name, last_name, and plan_id");
						}
					} else {
						System.out.println("Error: You should be signed out for this command");
					}
				} else if (t.equals("sign_in")) {
					// authenticate the customer.

					// you should be signed out for this command.
					if (authenticatedCustomer == null) {
						// required parameters for this command are: email, and password.
						if (st.countTokens() >= 2) {
							String email = st.nextToken();
							String password = st.nextToken();

							System.out.println("Signing in ...");

							// save the customer for future.
							authenticatedCustomer = q.transaction_sign_in(email, password);

							if (authenticatedCustomer != null) {
								System.out.println("Signed in successfully!");
							} else {
								System.out.println("There was a problem (wrong credentials or something else) during signing in!");
							}
						} else {
							System.out.println("Error: Required parameters for this command are: email, and password");
						}
					} else {
						System.out.println("Error: You should be signed out for this command");
					}
				} else if (t.equals("sign_out")) {
					// deauthenticate the customer.

					// you should be signed in for this command.
					if (authenticatedCustomer != null) {
						System.out.println("Signing out ...");

						boolean successful = q.transaction_sign_out(authenticatedCustomer.getCustomerId());

						if (successful) {
							authenticatedCustomer = null;
							System.out.println("Signed out successfully!");
						} else {
							System.out.println("There was a problem during signing out!");
						}
					} else {
						System.out.println("Error: You should be signed in for this command");
					}
				} else if (t.equals("show_plans")) {
					// show all available plans.

					q.transaction_show_plans();
				} else if (t.equals("show_subscription")) {
					// show subscription of the authenticated user.

					// you should be signed in for this command.
					if (authenticatedCustomer != null) {
						q.transaction_show_subscription(authenticatedCustomer.getCustomerId());
					} else {
						System.out.println("Error: You should be signed in for this command");
					}
				} else if (t.equals("subscribe")) {
					// subscribe authenticated user to given plan.

					// you should be signed in for this command.
					if (authenticatedCustomer != null) {
						// required parameters for this command are: plan_id.
						if (st.countTokens() >= 1) {
							int plan_id = Integer.parseInt(st.nextToken());

							// there should be a plan with given id in the db.
							boolean there_exists_such_plan = q.helper_there_exists_such_plan(plan_id);

							if (there_exists_such_plan) {
								System.out.println("Subscribing ...");

								boolean successful = q.transaction_subscribe(authenticatedCustomer.getCustomerId(), plan_id);

								if (successful) {
									System.out.println("Subscribed successfully!");
								} else {
									System.out.println("There was a problem during subscribing!");
								}
							} else {
								System.out.println("Error: There exists no plan with id '" + plan_id + "'");
							}
						} else {
							System.out.println("Error: Required parameters for this command are: plan_id");
						}
					} else {
						System.out.println("Error: You should be signed in for this command");
					}
				} else if (t.equals("watched_movie")) {
					// mark the given movie as watched by the authenticated user.

					// you should be signed in for this command.
					if (authenticatedCustomer != null) {
						// required parameters for this command are: movie_id.
						if (st.countTokens() >= 1) {
							String movie_id = st.nextToken();

							// there should be a movie with given id in the db.
							boolean there_exists_such_movie = q.helper_there_exists_such_movie(movie_id);

							if (there_exists_such_movie) {
								System.out.println("Saving ...");

								boolean successful = q.transaction_watch(movie_id, authenticatedCustomer.getCustomerId());

								if (successful) {
									System.out.println("Saved successfully!");
								} else {
									System.out.println("There was a problem during saving!");
								}
							} else {
								System.out.println("Error: There exists no movie with id '" + movie_id + "'");
							}
						} else {
							System.out.println("Error: Required parameters for this command are: movie_id");
						}
					} else {
						System.out.println("Error: You should be signed in for this command");
					}
				} else if (t.equals("search_for_movies")) {
					// search for the movies whose title is like the given title.

					// you should be signed in for this command.
					if (authenticatedCustomer != null) {
						// required parameters for this command are: movie_title.
						if (st.hasMoreTokens()) {
							String movie_title = st.nextToken("\n").trim(); // read the rest of the line.

							q.transaction_search_for_movies(authenticatedCustomer.getCustomerId(), movie_title);
						} else {
							System.out.println("Error: Required parameters for this command are: movie_title");
						}
					} else {
						System.out.println("Error: You should be signed in for this command");
					}
				} else if (t.equals("suggest_movies")) {
					// suggest movies to the authenticated user.

					// you should be signed in for this command.
					if (authenticatedCustomer != null) {
						q.transaction_suggest_movies(authenticatedCustomer.getCustomerId());
					} else {
						System.out.println("Error: You should be signed in for this command");
					}
				} else if (t.equals("quit")) {
					// quit the program.

					System.exit(0);
				} else {
					// unrecognized command.

					System.out.println("Error: unrecognized command '" + t + "'");
				}
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {

		try {
			// prepare the database connection stuff.
			Query q = new Query();
			q.openConnection();
			q.prepareStatements();

			// menu(...) does the real work.
			menu(q);

			// done with connection.
			q.closeConnection();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

}
