package cx.it.nullpo.nm8.gui.niftygui;

import cx.it.nullpo.nm8.gui.framework.NFFont;
import de.lessvoid.nifty.spi.render.RenderFont;

public class NFRenderFont implements RenderFont {
	protected NFFont nfFont;
	protected String filename;

	public NFRenderFont(NFFont nfFont, String filename) {
		this.nfFont = nfFont;
		this.filename = filename;
	}

	public int getWidth(String text) {
		return nfFont.getStringWidth(text);
	}

	public int getHeight() {
		return nfFont.getLineHeight();
	}

	public Integer getCharacterAdvance(char currentCharacter, char nextCharacter, float size) {
		return null;
	}

	public void dispose() {

	}

	public NFFont getNFFont() {
		return nfFont;
	}

	public String getFilename() {
		return filename;
	}
}
