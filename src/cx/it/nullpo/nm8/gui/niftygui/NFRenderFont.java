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
		// Does the text has \#something# ?
		if(text.contains("\\#")) {
			// Yes. We must deal with the color change command
			StringBuilder strbuf = new StringBuilder();
			int i = 0;
			int w = 0;
			while(i < text.length()) {
				// Is next 2 characters are \\# ?
				if((i < text.length() - 2) && text.substring(i, i+2).equals("\\#")) {
					// If so, we must erase the color change codes
					int nextSharp = text.indexOf('#', i+2);

					if(nextSharp != -1) {
						// Add the width of current text
						if(strbuf.length() > 0) {
							w += nfFont.getStringWidth(strbuf.toString());
							strbuf = new StringBuilder();
						}

						String colorCode = text.substring(i+2, nextSharp);
						i += colorCode.length() + 3;
					} else {
						i += 2;
					}
				} else {
					strbuf.append(text.charAt(i));
					i++;
				}
			}

			// Add the width of last text
			if(strbuf.length() > 0) {
				w += nfFont.getStringWidth(strbuf.toString());
			}

			return w;
		}

		// No. Just get the width normally
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
