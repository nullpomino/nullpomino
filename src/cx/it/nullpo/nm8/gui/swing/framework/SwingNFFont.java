package cx.it.nullpo.nm8.gui.swing.framework;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import cx.it.nullpo.nm8.gui.framework.NFFont;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFSystem;

/**
 * Swing implementation of NFFont
 */
public class SwingNFFont implements NFFont {
	private static final long serialVersionUID = 2936784263456983513L;

	/** AWT font */
	protected Font font;

	/** Current graphics context */
	protected NFGraphics g;

	/** NFSystem */
	protected NFSystem sys;

	/**
	 * Constructor
	 * @param font AWT native Font
	 */
	public SwingNFFont(Font font) {
		this.font = font;
	}

	/**
	 * Constructor
	 * @param font AWT native Font
	 * @param g NFGraphics
	 */
	public SwingNFFont(Font font, NFGraphics g) {
		this.font = font;
		this.g = g;
	}

	/**
	 * Constructor
	 * @param font AWT native Font
	 * @param g NFGraphics
	 * @param sys NFSystem
	 */
	public SwingNFFont(Font font, NFGraphics g, NFSystem sys) {
		this.font = font;
		this.g = g;
		this.sys = sys;
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

	public int getStringWidth(String str) {
		if(g == null && sys != null) {
			g = sys.getGraphics();
		}
		if(g != null && g instanceof SwingNFGraphics) {
			SwingNFGraphics g2 = (SwingNFGraphics)g;
			Graphics nativeGraphics = g2.getNativeGraphics();
			Rectangle2D r = nativeGraphics.getFontMetrics(font).getStringBounds(str, nativeGraphics);
			return (int)r.getWidth();
		}
		return 0;
	}

	public int getStringHeight(String str) {
		if(g == null && sys != null) {
			g = sys.getGraphics();
		}
		if(g != null && g instanceof SwingNFGraphics) {
			SwingNFGraphics g2 = (SwingNFGraphics)g;
			Graphics nativeGraphics = g2.getNativeGraphics();
			Rectangle2D r = nativeGraphics.getFontMetrics(font).getStringBounds(str, nativeGraphics);
			return (int)r.getHeight();
		}
		return 0;
	}

	public int getLineHeight() {
		if(g == null && sys != null) {
			g = sys.getGraphics();
		}
		if(g != null && g instanceof SwingNFGraphics) {
			SwingNFGraphics g2 = (SwingNFGraphics)g;
			Graphics nativeGraphics = g2.getNativeGraphics();
			return nativeGraphics.getFontMetrics(font).getHeight();
		}
		return 0;
	}

	public void setGraphics(NFGraphics g) {
		this.g = g;
	}

	public NFGraphics getGraphics() {
		return g;
	}

	public void drawString(NFGraphics g, String str, int x, int y) {
		SwingNFGraphics g2 = (SwingNFGraphics)g;
		g2.g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Font curNativeFont = g2.g.getFont();
		g2.g.setFont(font);
		int yoffset = g2.g.getFontMetrics(font).getAscent();
		g2.g.drawString(str, x, y+yoffset);
		g2.g.setFont(curNativeFont);
		g2.g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
	}
}
