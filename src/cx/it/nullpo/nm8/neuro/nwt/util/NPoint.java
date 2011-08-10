package cx.it.nullpo.nm8.neuro.nwt.util;

/**
 * NPoint is a class which represents a point in space.
 * @author Zircean
 *
 */
public class NPoint {

	/** The X coordiante of this point. */
	private int x;
	/** The Y coordinate of this point. */
	private int y;
	
	/**
	 * Constructor for NPoint.
	 * @param x the X coordinate of the point.
	 * @param y the Y coordiante of the point.
	 */
	public NPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Gets the X coordinate of this point.
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Gets the Y coordinate of this point.
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Sets the X coordinate of this point.
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * Sets the Y coordinate of this point.
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof NPoint) {
			NPoint np = (NPoint)obj;
			return np.getX() == x && np.getY() == y;
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return (x << 16) ^ y;
	}
	
	public String toString() {
		return "("+x+","+y+")";
	}
}
