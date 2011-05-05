package cx.it.nullpo.nm8.gui.swing.framework;

import java.awt.Image;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFImage;

/**
 * Swing implementation of NFImage
 */
public class SwingNFImage implements NFImage {
	private static final long serialVersionUID = 1L;

	/** Swing (AWT) native image */
	protected Image nativeImage;

	/** SwingNFGraphics of this image */
	protected SwingNFGraphics g;

	/**
	 * Constructor
	 * @param nativeImage Swing native image
	 */
	public SwingNFImage(Image nativeImage) {
		this.nativeImage = nativeImage;
	}

	/**
	 * Get Swing native image
	 * @return Swing native image
	 */
	public Image getNativeImage() {
		return nativeImage;
	}

	public NFGraphics getGraphics() {
		if(g != null) return g;
		g = new SwingNFGraphics(nativeImage.getGraphics());
		return g;
	}

	public int getWidth() {
		return nativeImage.getWidth(null);
	}

	public int getHeight() {
		return nativeImage.getHeight(null);
	}
}
