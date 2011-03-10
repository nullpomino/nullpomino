package cx.it.nullpo.nm8.gui.slick.framework;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFImage;

/**
 * Slick implementation of NFImage
 */
public class SlickNFImage implements NFImage {
	private static final long serialVersionUID = 7635534276019899998L;

	/** Slick native image */
	protected Image nativeImage;

	/** SlickNFGraphics of this image */
	protected SlickNFGraphics g;

	/**
	 * Constructor
	 * @param nativeImage Slick native image
	 */
	public SlickNFImage(Image nativeImage) {
		this.nativeImage = nativeImage;
	}

	/**
	 * Get Slick native image
	 * @return Slick native image
	 */
	public Image getNativeImage() {
		return nativeImage;
	}

	public NFGraphics getGraphics() {
		try {
			if(g != null) return g;
			g = new SlickNFGraphics(nativeImage.getGraphics());
			return g;
		} catch (SlickException e) {
			throw new RuntimeException("Can't create Graphics context of the Image", e);
		}
	}

	public int getHeight() {
		return nativeImage.getHeight();
	}

	public int getWidth() {
		return nativeImage.getWidth();
	}
}
