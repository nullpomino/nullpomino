package cx.it.nullpo.nm8.gui.swing.framework;

import java.awt.Font;

import cx.it.nullpo.nm8.gui.framework.NFFont;

/**
 * Swing implementation of NFFont
 */
public class SwingNFFont implements NFFont {
	private static final long serialVersionUID = 2936784263456983513L;

	/** AWT font */
	protected Font font;

	/**
	 * Constructor
	 * @param font AWT native Font
	 */
	public SwingNFFont(Font font) {
		this.font = font;
	}

	public Font getFont() {
		return font;
	}

	public int getSize() {
		return font.getSize();
	}

	public boolean isBold() {
		return font.isBold();
	}

	public boolean isItalic() {
		return font.isItalic();
	}

	public boolean isGlyphLoadingRequired() {
		return false;
	}

	public void addGlyphs(String text) {
	}

	public boolean loadGlyphs() {
		return true;
	}
}
