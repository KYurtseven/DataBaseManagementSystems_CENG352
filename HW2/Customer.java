/**
 * Customer object
 * @author koray
 *
 */
public class Customer {

    private int customerId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private int sessionCount;

    /**
     * Empty constructor
     */
    public Customer() {

    }
    /**
     * Customer constructor
     * Upon successful sign in, it is created
     * @param customerId customer id
     * @param email email
     * @param firstName first name
     * @param lastName last name
     * @param sessionCount session count
     */
    public Customer(int customerId, 
    		String email,
    		String password,
    		String firstName, 
    		String lastName,
    		int sessionCount) {
    	this.customerId = customerId;
    	this.email = email;
    	this.password = password;
    	this.firstName = firstName;
    	this.lastName = lastName;
    	this.sessionCount = sessionCount;
    }
    
    /**
     * 
     * @return customer id
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * 
     * @param customerId customer id to set
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * 
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * 
     * @param email email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * 
     * @param firstName first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * 
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * 
     * @param lastName last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * 
     * @return session count
     */
    public int getSessionCount() {
        return sessionCount;
    }

    /**
     * 
     * @param sessionCount the session count to set
     */
    public void setSessionCount(int sessionCount) {
        this.sessionCount = sessionCount;
    }
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
    
    
}
