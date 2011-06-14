package cx.it.nullpo.nm8.game.component;

import java.io.Serializable;

/**
 * Wallkick Results
 */
public class WallkickResult implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = -7985029240622355609L;

	/** X-coordinate offset */
	public int offsetX;

	/** Y-coordinate offset */
	public int offsetY;

	/** Piece direction after the wallkick */
	public int direction;

	/**
	 * Constructor
	 */
	public WallkickResult() {
		reset();
	}

	/**
	 * Constructor with params
	 * @param offsetX X-coordinate offset
	 * @param offsetY Y-coordinate offset
	 * @param direction Piece direction after the wallkick
	 */
	public WallkickResult(int offsetX, int offsetY, int direction) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.direction = direction;
	}

	/**
	 * Copy constructor
	 * @param w Copy source
	 */
	public WallkickResult(WallkickResult w) {
		copy(w);
	}

	/**
	 * Reset to defaults
	 */
	public void reset() {
		offsetX = 0;
		offsetY = 0;
		direction = 0;
	}

	/**
	 * Copy from other WallkickResult
	 * @param w Copy source
	 */
	public void copy(WallkickResult w) {
		this.offsetX = w.offsetX;
		this.offsetY = w.offsetY;
		this.direction = w.direction;
	}

	/**
	 * Returns true if this kick is a floor kick.
	 * @return true if this kick is a floor kick
	 */
	public boolean isUpward() {
		return (offsetY < 0);
	}
}
