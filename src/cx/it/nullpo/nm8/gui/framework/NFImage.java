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

	/**
	 * Create a sub-part of this image.
	 * @param x The x coordinate of the sub-image
	 * @param y The y coordinate of the sub-image
	 * @param width The width of the sub-image
	 * @param height The height of the sub-image
	 * @return The image represent the sub-part of this image
	 */
	public NFImage getSubImage(int x, int y, int width, int height);
}
