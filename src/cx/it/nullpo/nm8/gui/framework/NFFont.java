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
}
