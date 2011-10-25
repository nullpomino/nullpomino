package cx.it.nullpo.nm8.gui.slick.framework;

import java.awt.Font;

import org.newdawn.slick.AngelCodeFont;

import cx.it.nullpo.nm8.gui.common.font.angelcode.AngelCodeMainInfo;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;

/**
 * NFFont for AngelCodeFont
 */
public class SlickNFAngelCodeFont extends SlickNFFont {
	private static final long serialVersionUID = -1116842979042765483L;

	/** Slick native AngelCodeFont */
	protected AngelCodeFont aFont;

	/** fnt file info */
	protected AngelCodeMainInfo info;

	public SlickNFAngelCodeFont() {
	}

	public SlickNFAngelCodeFont(AngelCodeFont aFont) {
		this.aFont = aFont;
	}

	public SlickNFAngelCodeFont(AngelCodeFont aFont, AngelCodeMainInfo info) {
		this.aFont = aFont;
		this.info = info;
	}

	public AngelCodeFont getAngelCodeFont() {
		return aFont;
	}

	public AngelCodeMainInfo getAngelCodeMainInfo() {
		return info;
	}

	@Override
	public org.newdawn.slick.Font getNativeFont() {
		return aFont;
	}

	@Override
	public Font getFont() {
		return null;
	}

	@Override
	public int getSize() {
		return (info == null) ? -1 : info.getSize();
	}

	@Override
	public boolean isBold() {
		return (info == null) ? false : info.isBold();
	}

	@Override
	public boolean isItalic() {
		return (info == null) ? false : info.isItalic();
	}

	@Override
	public boolean isGlyphLoadingRequired() {
		return false;
	}

	@Override
	public void addGlyphs(String text) {
	}

	@Override
	public boolean loadGlyphs() {
		return true;
	}

	@Override
	public int getStringWidth(String str) {
		return aFont.getWidth(str);
	}

	@Override
	public int getStringHeight(String str) {
		return aFont.getHeight(str);
	}

	@Override
	public int getLineHeight() {
		return aFont.getLineHeight();
	}

	@Override
	public void drawString(NFGraphics g, String str, int x, int y) {
		SlickNFGraphics g2 = (SlickNFGraphics)g;
		org.newdawn.slick.Font curNativeFont = g2.g.getFont();
		g2.g.setFont(aFont);
		g2.g.drawString(str, x, y);
		g2.g.setFont(curNativeFont);
	}
}
