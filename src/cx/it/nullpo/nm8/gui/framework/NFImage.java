package cx.it.nullpo.nm8.gui.framework;

import java.io.Serializable;

/**
 * NullpoMino Framework (NF) - NFImage<br>
 * Interface for images
 */
public interface NFImage extends Serializable {
	/**
	 * Get a graphics context that can be used to draw to this image
	 * @return The graphics context used to render to this image
	 */
	public NFGraphics getGraphics();

	/**
	 * Get the width of this image
	 * @return The width of this image
	 */
	public int getWidth();

	/**
	 * Get the height of this image
	 * @return The height of this image
	 */
	public int getHeight();
}
