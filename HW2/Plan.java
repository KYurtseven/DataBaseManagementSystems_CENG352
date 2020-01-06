/**
 * Plan object for connecting to the db
 * @author koray
 *
 */
public class Plan{
	
	private int plan_id;
	private String name;
	private String resolution;
	private int max_parallel_session;
	private float monthly_fee;
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
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the resolution
	 */
	public String getResolution() {
		return resolution;
	}
	/**
	 * @param resolution the resolution to set
	 */
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	/**
	 * @return the max_parallel_session
	 */
	public int getMax_parallel_session() {
		return max_parallel_session;
	}
	/**
	 * @param max_parallel_session the max_parallel_session to set
	 */
	public void setMax_parallel_session(int max_parallel_session) {
		this.max_parallel_session = max_parallel_session;
	}
	/**
	 * @return the monthly_fee
	 */
	public float getMonthly_fee() {
		return monthly_fee;
	}
	/**
	 * @param monthly_fee the monthly_fee to set
	 */
	public void setMonthly_fee(float monthly_fee) {
		this.monthly_fee = monthly_fee;
	}
	
	
	
}