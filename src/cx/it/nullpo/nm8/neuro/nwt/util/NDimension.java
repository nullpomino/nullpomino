package cx.it.nullpo.nm8.neuro.nwt.util;

/**
 * A class representing dimensions, or the size of something on the screen.
 * @author Zircean
 *
 */
public class NDimension {

	/** The width of this dimension. */
	private int w;
	/** The height of this dimension. */
	private int h;
	
	/**
	 * Constructor for NDimension.
	 * @param w the width of the dimension
	 * @param h the height of the dimension
	 */
	public NDimension(int w, int h) {
		this.w = w;
		this.h = h;
	}
	
	/**
	 * Gets the width of this dimension.
	 */
	public int getWidth() {
		return w;
	}
	
	/**
	 * Gets the height of this dimension.
	 */
	public int getHeight() {
		return h;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof NDimension) {
			NDimension nd = (NDimension)obj;
			return nd.getWidth() == w && nd.getHeight() == h;
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return (w << 16) ^ h;
	}
	
	public String toString() {
		return "["+w+"x"+h+"]";
	}
}
