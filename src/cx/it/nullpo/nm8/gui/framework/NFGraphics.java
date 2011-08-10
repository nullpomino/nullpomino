package cx.it.nullpo.nm8.gui.framework;

import java.io.Serializable;

/**
 * NullpoMino Framework (NF) - NFGraphics<br>
 * A graphics context that can be used to render primatives and images.<br>
 * If you save an instance of this to a variable, don't forget to update to the new one every frame.
 * Because it might become null in near future.
 */
public interface NFGraphics extends Serializable {
	/**
	 * Check if this graphics context can use NFColor as a filter
	 * @return True if this graphics context can use NFColor as a filter
	 */
	public boolean isSupportColorFilter();

	/**
	 * Draw an image to the screen
	 * @param img The image to draw to the screen
	 * @param x The x location at which to draw the image
	 * @param y The y location at which to draw the image
	 */
	public void drawImage(NFImage img, int x, int y);

	/**
	 * Draw an image to the screen
	 * @param img The image to draw to the screen
	 * @param x The x location at which to draw the image
	 * @param y The y location at which to draw the image
	 * @param col The color to apply to the image as a filter
	 */
	public void drawImage(NFImage img, int x, int y, NFColor col);

	/**
	 * Draw an image to the screen
	 * @param img The image to draw to the screen
	 * @param dx1 The x location at which to draw the image
	 * @param dy1 The y location at which to draw the image
	 * @param dx2 The x position of the bottom right corner of the drawn image
	 * @param dy2 The y position of the bottom right corner of the drawn image
	 * @param sx1 The x position of the rectangle to draw from this image (i.e. relative to the image)
	 * @param sy1 The y position of the rectangle to draw from this image (i.e. relative to the image)
	 * @param sx2 The x position of the bottom right cornder of rectangle to draw from this image (i.e. relative to the image)
	 * @param sy2 The y position of the bottom right cornder of rectangle to draw from this image (i.e. relative to the image)
	 */
	public void drawImage(NFImage img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2);

	/**
	 * Draw an image to the screen
	 * @param img The image to draw to the screen
	 * @param dx1 The x location at which to draw the image
	 * @param dy1 The y location at which to draw the image
	 * @param dx2 The x position of the bottom right corner of the drawn image
	 * @param dy2 The y position of the bottom right corner of the drawn image
	 * @param sx1 The x position of the rectangle to draw from this image (i.e. relative to the image)
	 * @param sy1 The y position of the rectangle to draw from this image (i.e. relative to the image)
	 * @param sx2 The x position of the bottom right cornder of rectangle to draw from this image (i.e. relative to the image)
	 * @param sy2 The y position of the bottom right cornder of rectangle to draw from this image (i.e. relative to the image)
	 * @param col The color to apply to the image as a filter
	 */
	public void drawImage(NFImage img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, NFColor col);

	/**
	 * Draw a string to the screen using the current font
	 * @param str The string to draw
	 * @param x The x coordinate to draw the string at
	 * @param y The y coordinate to draw the string at
	 */
	public void drawString(String str, int x, int y);

	/**
	 * Get a string width of current font
	 * @param str String
	 * @return Width
	 */
	public int getStringWidth(String str);

	/**
	 * Get a string height of current font
	 * @param str String
	 * @return Height
	 */
	public int getStringHeight(String str);

	/**
	 * Draw a line on the canvas in the current colour
	 * @param x1 The x coordinate of the start point
	 * @param y1 The y coordinate of the start point
	 * @param x2 The x coordinate of the end point
	 * @param y2 The y coordinate of the end point
	 */
	public void drawLine(int x1, int y1, int x2, int y2);

	/**
	 * Draw a rectangle to the canvas in the current colour
	 * @param x The x coordinate of the top left corner
	 * @param y The y coordinate of the top left corner
	 * @param width The width of the rectangle to draw
	 * @param height The height of the rectangle to draw
	 */
	public void drawRect(int x, int y, int width, int height);

	/**
	 * Fill a rectangle on the canvas in the current colour
	 * @param x The x coordinate of the top left corner
	 * @param y The y coordinate of the top left corner
	 * @param width The width of the rectangle to draw
	 * @param height The height of the rectangle to draw
	 */
	public void fillRect(int x, int y, int width, int height);

	/**
	 * Set the color to use when rendering to this context
	 * @param col The color to use when rendering to this context
	 */
	public void setColor(NFColor col);

	/**
	 * Get the color in use by this graphics context
	 * @return The color in use by this graphics context
	 */
	public NFColor getColor();

	/**
	 * Set the font to be used when rendering text
	 * @param font The font to be used when rendering text
	 */
	public void setFont(NFFont font);

	/**
	 * Get the current font
	 * @return The current font
	 */
	public NFFont getFont();

	/**
	 * Reset to using the default font for this context
	 */
	public void resetFont();
	
	/**
	 * Translate this graphics context by the given x and y values with respect to the origin of
	 * the original context
	 */
	public void translate(int x, int y);
}
