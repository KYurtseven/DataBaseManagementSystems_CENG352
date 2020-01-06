/**
 * Helper class for Query 9.
 * When I fetched movies from the database, I make them an object, SuggestMovie.
 * @author koray
 *
 */
public class SuggestMovie {
	
	// movie id
	private String tconst;
	// movie name
	private String originalTitle;
	// averageRating
	private double averageRating;
	
	public SuggestMovie(String tconst, String originalTitle, double averageRating) {
		this.tconst = tconst;
		this.originalTitle = originalTitle;
		this.averageRating = averageRating;
	}
	
	/**
	 * @return the tconst
	 */
	public String getTconst() {
		return tconst;
	}
	/**
	 * @param tconst the tconst to set
	 */
	public void setTconst(String tconst) {
		this.tconst = tconst;
	}
	/**
	 * @return the originalTitle
	 */
	public String getOriginalTitle() {
		return originalTitle;
	}
	/**
	 * @param originalTitle the originalTitle to set
	 */
	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = originalTitle;
	}
	/**
	 * @return the averageRating
	 */
	public double getAverageRating() {
		return averageRating;
	}
	/**
	 * @param averageRating the averageRating to set
	 */
	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}
	
	
}
