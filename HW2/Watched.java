import java.sql.Date;

/**
 * Watched object for connecting to db
 * @author koray
 *
 */
public class Watched{
	
	private int w_id;
	private String movie_id;
	private int customer_id;
	private Date when;
	/**
	 * @return the w_id
	 */
	public int getW_id() {
		return w_id;
	}
	/**
	 * @param w_id the w_id to set
	 */
	public void setW_id(int w_id) {
		this.w_id = w_id;
	}
	/**
	 * @return the movie_id
	 */
	public String getMovie_id() {
		return movie_id;
	}
	/**
	 * @param movie_id the movie_id to set
	 */
	public void setMovie_id(String movie_id) {
		this.movie_id = movie_id;
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
	 * @return the when
	 */
	public Date getWhen() {
		return when;
	}
	/**
	 * @param when the when to set
	 */
	public void setWhen(Date when) {
		this.when = when;
	}
	
}