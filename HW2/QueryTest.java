import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class QueryTest {

	private static Query q;
	
	@BeforeClass
	public static void testSetup() {
		try {
			q = new Query();
			q.openConnection();
			q.prepareStatements();
		}
		catch(Exception e) {
			System.out.println("Test exception " + e);
		}
	}
	
	@Test
	public void testTest() {
		System.out.println("Test is working");
	}
	
	/**
	 * Test existing user's sign up.
	 */
	@Test
	public void testHelperAlreadySignUp() {
		try {
			System.out.println("testHelperAlreadySignUp BEGIN");
			final String email1 = "first@ceng";
			final String email2 = "koray@ceng";
			// initially no koray
			boolean result1 = q.helper_already_signed_up(email1);
			boolean result2 = q.helper_already_signed_up(email2);
			assertEquals(true, result1);
			
			assertEquals(false, result2);
		}
		catch(Exception e) {
			System.out.println("testHelperAlreadySignUp exception " + e);
		}
		finally {
			System.out.println("testHelperAlreadySignUp ENDS\n");
		}
	}
	
	/**
	 * Test whether the plan exist or not
	 */
	@Test
	public void testHelperAlreadySuchPlan() {
		try {
			System.out.println("testHelperAlreadySuchPlan BEGIN");
			final int planid1 = 1;
			final int planid10 = 10;
			
			boolean result1 = q.helper_there_exists_such_plan(planid1);
			boolean result2 = q.helper_there_exists_such_plan(planid10);
			assertEquals(true, result1);
			
			assertEquals(false, result2);
		}
		catch(Exception e) {
			System.out.println("testHelperAlreadySuchPlan exception " + e);
		}
		finally {
			System.out.println("testHelperAlreadySuchPlan ENDS\n");
		}
	}
	

	/**
	 * Helper testSignup method
	 * @return result set containing user
	 */
	private ResultSet testSignUpFetchUserHelper() {
		try {
			String fetchUser_sql = "Select * from \"Customer\" "
					+ "where email = ?";
			PreparedStatement fetchUser_statement = q.
					getCustomerDbConnection().
					prepareStatement(fetchUser_sql);
			fetchUser_statement.setString(1, "koray@ceng");
			ResultSet rs = fetchUser_statement.executeQuery();
			
			return rs;
		}
		catch(Exception e) {
			System.out.println("Exception testSignUpFetchUserHelper "  + e);
		}
		return null;
	}
	
	/**
	 * Helper testSignup method
	 * @return result set containing subscription
	 */
	private ResultSet testSignUpFetchSubscriptionHelper(int customer_id, int plan_id) {
		try {
			
			String fetchSubscription_sql = "Select * From \"Subscription\" "
					+ "where customer_id = ? and plan_id = ?";
			PreparedStatement fetchSubscription_statement = q.
					getCustomerDbConnection().
					prepareStatement(fetchSubscription_sql);
			fetchSubscription_statement.setInt(1, customer_id);
			fetchSubscription_statement.setInt(2, plan_id);
			ResultSet rs = fetchSubscription_statement.executeQuery();
			return rs;
		}
		catch(Exception e) {
			System.out.println("Exception testSignUpFetchSubscriptionHelper "  + e);
		}
		return null;
	}
	
	/**
	 * Clean database after sign up
	 * Delete user
	 * Delete subscription
	 */
	private void testSignUpCleanUp(int s_id, String email) {
		try {
			// delete subscription
			q.getCustomerDbConnection().setAutoCommit(false);
			String cleanupSubscription_sql = "Delete from \"Subscription\" "
					+ "where s_id = ?";
			PreparedStatement cleanupSubscription_statement = q.
					getCustomerDbConnection().
					prepareStatement(cleanupSubscription_sql);
			cleanupSubscription_statement.setInt(1, s_id);
			
			cleanupSubscription_statement.executeUpdate();
			// delete user
			String cleanupUser_sql = "Delete from \"Customer\" "
					+ "where email = ?";
			PreparedStatement cleanUpUser_statement = q.
					getCustomerDbConnection().
					prepareStatement(cleanupUser_sql);
			cleanUpUser_statement.setString(1, email);
			cleanUpUser_statement.executeUpdate();
			q.getCustomerDbConnection().commit();
		}
		catch(Exception e) {
			System.out.println("Exception testSignUpFetchSubscriptionHelper "  + e);
		}
	}
	
	/*
	 * Try to sign up a non existing email
	 * Also, try to use existing plan id.
	 */
	@Test
	public void testSignUp() {
		try {
			System.out.println("testSignUp BEGIN");
			
			final String email = "koray@ceng";
			final String password = "123123abc";
			final String first_name = "koray";
			final String last_name = "yurtseven";
			final int plan_id = 1;
			
			boolean result = q.transaction_sign_up(email, password, first_name, last_name, plan_id);
			
			assertEquals(true, result);
			if(result) {
				// try to fetch the user
				ResultSet rs = testSignUpFetchUserHelper();
				int s_id = -1;
				while(rs.next()){
					assertEquals(last_name, rs.getString("last_name"));
					assertEquals("koray@ceng", rs.getString("email"));
					
					// try to fetch subscription
					ResultSet rs2 = testSignUpFetchSubscriptionHelper(rs.getInt("customer_id"), plan_id);
					
					while(rs2.next()) {
						s_id = rs2.getInt("s_id");
						assertEquals(rs.getInt("customer_id"), rs2.getInt("customer_id"));
						assertEquals(1, rs2.getInt("plan_id"));
					}
					rs2.close();
				}
				// clean up
				rs.close();
				testSignUpCleanUp(s_id, email);
			}
		}
		catch(Exception e) {
			System.out.println("testSignUp exception " + e);
		}
		finally {
			System.out.println("testSignUp ENDS\n");
		}
	}
	
	/**
	 * Tests non existing user's sign in
	 */
	@Test
	public void testNonExistingSignIn() {
		try {
			System.out.println("testNonExistingSignIn BEGINS");
			final String email = "nonexisting@ceng";
			final String password = "nonexistingpassword";
			Customer customer = q.transaction_sign_in(email, password);
			assertEquals(null, customer);
		}
		catch(Exception e) {
			System.out.println("testNonExistingSignIn exception "  + e);
		}
		finally {
			System.out.println("testNonExistingSignIn ENDS\n");
		}
	}
	
	/**
	 * Cleans sign in records from the database
	 */
	private void testExistingSignInCleanUp(int session_count, String email) {
		try {
			q.getCustomerDbConnection().setAutoCommit(false);
			String decrement_session_count_sql = "UPDATE \"Customer\" "
					+ "SET session_count = ? WHERE email = ?";
			PreparedStatement ps;
			ps = q.getCustomerDbConnection().prepareStatement(decrement_session_count_sql);
			
			ps.setInt(1, session_count);
			ps.setString(2, email);
			int res = ps.executeUpdate();
			q.getCustomerDbConnection().commit();
		}
		catch(Exception e) {
			System.out.println("Exception testExistingSignInCleanUp "  + e);
		}
	}
	
	/**
	 * Tests existing user's sign in
	 */
	@Test
	public void testExistingSignIn() {
		try {
			System.out.println("testSignIn BEGINS");
			// "first@ceng" has plan with 1 max parallel connection
			
			final String email = "first@ceng";
			final String password = "123123abc";
			
			Customer customer = q.transaction_sign_in(email, password);
			assertEquals("first@ceng", customer.getEmail());
			assertEquals(1, customer.getSessionCount());
			
			testExistingSignInCleanUp(0, "first@ceng");
			
		}
		catch(Exception e) {
			System.out.println("testSignIn exception "  + e);
		}
		finally {
			System.out.println("testSignIn ENDS\n");
		}
	}
	
	/**
	 * Try to exceed max parallel session
	 */
	@Test
	public void testSignInExceedsMax() {
		try {
			System.out.println("testSignInExceedsMax BEGINS");
			// "first@ceng" has plan with 1 max parallel connection
			
			final String email = "first@ceng";
			final String password = "123123abc";
			
			Customer customer = q.transaction_sign_in(email, password);
			assertEquals("first@ceng", customer.getEmail());
			assertEquals(1, customer.getSessionCount());
			
			// try to increment again by signing in
			Customer customer2 = q.transaction_sign_in(email, password);
			assertEquals(null, customer2);
			// clean up
			testExistingSignInCleanUp(0, email);
			
		}
		catch(Exception e) {
			System.out.println("testSignInExceedsMax exception "  + e);
		}
		finally {
			System.out.println("testSignInExceedsMax ENDS\n");
		}
	}
	
	/**
	 * Test 1 sign out after successful sign in
	 */
	@Test
	public void testSignOut() {
		try {
			System.out.println("testSignOut BEGINS");
			
			final String email = "first@ceng";
			final String password = "123123abc";
			// first, sign in
			Customer customer = q.transaction_sign_in(email, password);
			
			// now sign out
			// session count should be 0
			boolean res = q.transaction_sign_out(customer.getCustomerId());
			
			assertEquals(res, true);
			
		}
		catch(Exception e) {
			System.out.println("testSignOut exception "  + e);
		}
		finally {
			System.out.println("testSignOut ENDS\n");
		}
	}
	
	/**
	 * Tests multiple sign out after multiple successful sign in
	 */
	@Test
	public void testSignInOutMultiple() {
		try {
			System.out.println("testSignInOutMultiple BEGINS");
			
			// Initially customer 8 is gold plan user.
			// It has 4 max parallel session
			final String email = "eigth@ceng";
			final String password = "123123abc";
			// first, sign in
			Customer customer1 = q.transaction_sign_in(email, password);
			Customer customer2 = q.transaction_sign_in(email, password);
			Customer customer3 = q.transaction_sign_in(email, password);
			Customer customer4 = q.transaction_sign_in(email, password);
			
			assertEquals(4, customer4.getSessionCount());
			
			String findCustomerById = "SELECT * FROM \"Customer\" where customer_id = ?";
			PreparedStatement ps = q.getCustomerDbConnection().prepareStatement(findCustomerById);
			
			// now sign out
			for(int i = 0; i < 4; i++) {
				boolean res = q.transaction_sign_out(customer1.getCustomerId());
				assertEquals(res, true);
				ps.clearParameters();
				ps.setInt(1, customer1.getCustomerId());
				ResultSet rs = ps.executeQuery();
				rs.next();
				assertEquals(3 - i, rs.getInt("session_count"));
			}
			// now we cannot sign out more
			boolean res2 = q.transaction_sign_out(customer1.getCustomerId());
			assertEquals(res2, false);
		}
		catch(Exception e) {
			System.out.println("testSignInOutMultiple exception "  + e);
		}
		finally {
			System.out.println("testSignInOutMultiple ENDS\n");
		}
	}
}
