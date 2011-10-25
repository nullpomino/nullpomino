package cx.it.nullpo.nm8.gui.slick.framework;

import java.awt.Font;

import org.newdawn.slick.UnicodeFont;

import cx.it.nullpo.nm8.gui.framework.NFFont;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;

/**
 * Slick implementation of NFFont
 */
public class SlickNFFont implements NFFont {
	private static final long serialVersionUID = 715752819023105681L;

	/** Slick native Font */
	protected org.newdawn.slick.Font font;

	/** Font point size */
	protected int pointSize;

	/** Bold */
	protected boolean bold;

	/** Italic */
	protected boolean italic;

	/**
	 * Constructor
	 */
	public SlickNFFont() {
	}

	/**
	 * Constructor
	 * @param font Slick native Font
	 */
	public SlickNFFont(org.newdawn.slick.Font font) {
		this.font = font;
		this.pointSize = -1;
		this.bold = false;
		this.italic = false;
	}

	/**
	 * Constructor
	 * @param font Slick native Font
	 * @param pointSize Point size
	 * @param bold Bold
	 * @param italic Italic
	 */
	public SlickNFFont(org.newdawn.slick.Font font, int pointSize, boolean bold, boolean italic) {
		this.font = font;
		this.pointSize = pointSize;
		this.bold = bold;
		this.italic = italic;
	}

	/**
	 * Get Slick native Font
	 * @return Slick native Font
	 */
	public org.newdawn.slick.Font getNativeFont() {
		return font;
	}

	public Font getFont() {
		if(font instanceof UnicodeFont) {
			return ((UnicodeFont) font).getFont();
		}
		return null;
	}

	public int getSize() {
		return pointSize;
	}

	public boolean isBold() {
		return bold;
	}

	public boolean isItalic() {
		return italic;
	}

	public boolean isGlyphLoadingRequired() {
		return true;
	}

	public void addGlyphs(String text) {
		if(font instanceof UnicodeFont) {
			((UnicodeFont) font).addGlyphs(text);
		}
	}

	public boolean loadGlyphs() {
		try {
			if(font instanceof UnicodeFont) {
				return ((UnicodeFont) font).loadGlyphs();
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public int getStringWidth(String str) {
		return font.getWidth(str);
	}

	public int getStringHeight(String str) {
		return font.getHeight(str);
	}

	public int getLineHeight() {
		return font.getLineHeight();
	}

	public void drawString(NFGraphics g, String str, int x, int y) {
		SlickNFGraphics g2 = (SlickNFGraphics)g;
		org.newdawn.slick.Font curNativeFont = g2.g.getFont();
		g2.g.setFont(font);
		g2.g.drawString(str, x, y);
		g2.g.setFont(curNativeFont);
	}
}
