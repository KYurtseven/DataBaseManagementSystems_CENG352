/**
 * Subscription object for connecting to the db
 * @author koray
 *
 */
public class Subscription{
	
	private int s_id;
	private int customer_id;
	private int plan_id;
	/**
	 * @return the s_id
	 */
	public int getS_id() {
		return s_id;
	}
	/**
	 * @param s_id the s_id to set
	 */
	public void setS_id(int s_id) {
		this.s_id = s_id;
	}
	/**
	 * @return the customer_id
	 */
	public int getCustomer_id() {
		return customer_id;
	}
	/**
	 * @param customer_id the customer_id to set
	 */
	public void setCustomer_id(int customer_id) {
		this.customer_id = customer_id;
	}
	/**
	 * @return the plan_id
	 */
	public int getPlan_id() {
		return plan_id;
	}
	/**
	 * @param plan_id the plan_id to set
	 */
	public void setPlan_id(int plan_id) {
		this.plan_id = plan_id;
	}
	
	
}