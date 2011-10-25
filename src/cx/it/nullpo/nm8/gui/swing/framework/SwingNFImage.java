package cx.it.nullpo.nm8.gui.swing.framework;

import java.awt.Image;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFImage;
import cx.it.nullpo.nm8.gui.framework.NFSystem;

/**
 * Swing implementation of NFImage
 */
public class SwingNFImage implements NFImage {
	private static final long serialVersionUID = 1L;

	/** Swing (AWT) native image */
	protected Image nativeImage;

	/** SwingNFGraphics of this image */
	protected SwingNFGraphics g;

	/** NFSystem */
	protected NFSystem sys;

	/**
	 * Constructor
	 * @param nativeImage Swing native image
	 * @deprecated Use SwingNFImage(Image nativeImage, NFSystem sys) instead
	 */
	public SwingNFImage(Image nativeImage) {
		this.nativeImage = nativeImage;
	}

	/**
	 * Constructor
	 * @param nativeImage Swing native image
	 * @param sys NFSystem
	 */
	public SwingNFImage(Image nativeImage, NFSystem sys) {
		this.nativeImage = nativeImage;
		this.sys = sys;
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
		g = new SwingNFGraphics(nativeImage.getGraphics(), sys);
		return g;
	}

	public int getWidth() {
		return nativeImage.getWidth(null);
	}

	public int getHeight() {
		return nativeImage.getHeight(null);
	}

	public NFImage getSubImage(int x, int y, int width, int height) {
		NFGraphics g = getGraphics();
		NFSystem sys = g.getNFSystem();
		NFImage newImage = sys.createImage(width, height);
		newImage.getGraphics().drawImage(this, 0, 0, width, height, x, y, x+width, y+height);
		return newImage;
	}
}
