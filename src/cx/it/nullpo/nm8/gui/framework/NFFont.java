package cx.it.nullpo.nm8.gui.framework;

import java.awt.Font;
import java.io.Serializable;

/**
 * NullpoMino Framework (NF) - NFFont<br>
 * Interface for fonts
 */
public interface NFFont extends Serializable {
	/**
	 * Returns the AWT font being used
	 * @return AWT font, or null if it's not using an AWT font
	 */
	public Font getFont();

	/**
	 * Get the point size of this font
	 * @return Point size of this font (-1 if unknown)
	 */
	public int getSize();

	/**
	 * Returns true if this font is rendered in bold typeface
	 * @return true if this font is rendered in bold typeface
	 */
	public boolean isBold();

	/**
	 * Returns true if this font is rendered in italic typeface
	 * @return true if this font is rendered in italic typeface
	 */
	public boolean isItalic();

	/**
	 * Returns true if this font requires glyph loading
	 * @return true if this font requires glyph loading
	 */
	public boolean isGlyphLoadingRequired();

	/**
	 * Queues the glyphs in the specified text to be loaded. Note that the glyphs are not actually loaded until loadGlyphs() is called.
	 * @param text The text containing the glyphs to be added
	 */
	public void addGlyphs(String text);

	/**
	 * Loads all queued glyphs; Some font may require this in order to show up on the screen.
	 * @return True if the glyphs were loaded entirely
	 */
	public boolean loadGlyphs();

	/**
	 * Get a string width of current font
	 * @param str String
	 * @return Width (-1 if unknown)
	 */
	public int getStringWidth(String str);

	/**
	 * Get a string height of current font
	 * @param str String
	 * @return Height (-1 if unknown)
	 */
	public int getStringHeight(String str);

	/**
	 * Get the maximum height of any line drawn by this font
	 * @return The maxium height of any line drawn by this font
	 */
	public int getLineHeight();

	/**
	 * Draw a String to any NFGraphics context with this font
	 * @param g NFGraphics
	 * @param str String to draw
	 * @param x X position
	 * @param y Y position
	 */
	public void drawString(NFGraphics g, String str, int x, int y);
}
