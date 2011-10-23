package cx.it.nullpo.nm8.gui.niftygui;

import cx.it.nullpo.nm8.gui.framework.NFImage;
import de.lessvoid.nifty.spi.render.RenderImage;

public class NFRenderImage implements RenderImage {
	protected NFImage nfImage;

	public NFRenderImage(NFImage nfImage) {
		this.nfImage = nfImage;
	}

	public int getWidth() {
		return nfImage.getWidth();
	}

	public int getHeight() {
		return nfImage.getHeight();
	}

	public void dispose() {

	}

	public NFImage getNFImage() {
		return nfImage;
	}
}
