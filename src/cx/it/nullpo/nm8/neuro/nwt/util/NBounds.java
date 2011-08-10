package cx.it.nullpo.nm8.neuro.nwt.util;

public class NBounds {

	/** X coordinate of the top-left corner of this Bounds object. */
	private float x;
	/** Y coordinate of the top-left corner of this Bounds object. */
	private float y;
	/** Width of this Bounds object. */
	private float width;
	/** Height of this Bounds object. */
	private float height;
	
	public NBounds(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public boolean intersects(NBounds nb) {
		return ((x < nb.getX()+nb.getWidth()) && (x+width > nb.getX()) &&
				(y < nb.getY()+nb.getHeight()) && (y+height > nb.getY()));
	}
	
}
