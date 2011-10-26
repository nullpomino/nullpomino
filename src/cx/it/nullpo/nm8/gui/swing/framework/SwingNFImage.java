package cx.it.nullpo.nm8.gui.swing.framework;

import java.awt.Image;
import java.awt.image.BufferedImage;

import cx.it.nullpo.nm8.gui.framework.NFColor;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFImage;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.util.NUtil;

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

	/** Current hash */
	protected String hash;

	/** true if we must update the hash */
	protected boolean needNewHash;

	/**
	 * Constructor
	 * @param nativeImage Swing native image
	 * @param sys NFSystem
	 */
	public SwingNFImage(Image nativeImage, NFSystem sys) {
		this.nativeImage = nativeImage;
		this.sys = sys;
		this.needNewHash = true;
	}

	/**
	 * Get Swing native image
	 * @return Swing native image
	 */
	public Image getNativeImage() {
		return nativeImage;
	}

	public NFGraphics getGraphics() {
		needNewHash = true;
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

	public NFColor getColor(int x, int y) {
		if(nativeImage instanceof BufferedImage) {
			// If the native image is an instanceof BufferedImage, just get RGB value from it.
			BufferedImage b = (BufferedImage)nativeImage;
			return new NFColor(b.getRGB(x, y));
		} else {
			// Otherwise, we must create a new empty Image (which is built on BufferedImage)
			// then paste the wanted pixel to it.
			SwingNFImage tempImage = (SwingNFImage)sys.createImage(1, 1);
			tempImage.getGraphics().drawImage(this, 0, 0, 1, 1, x, y, x+1, y+1);
			BufferedImage b = (BufferedImage)tempImage.getNativeImage();
			return new NFColor(b.getRGB(0, 0));
		}
	}

	public byte[] getBytes() {
		byte[] b = new byte[getWidth() * getHeight() * 4];

		for(int y = 0; y < getHeight(); y++) {
			for(int x = 0; x < getWidth(); x++) {
				NFColor c = getColor(x, y);
				b[(((getWidth() * y) + x) * 4) + 0] = (byte)c.getAlpha();
				b[(((getWidth() * y) + x) * 4) + 1] = (byte)c.getRed();
				b[(((getWidth() * y) + x) * 4) + 2] = (byte)c.getGreen();
				b[(((getWidth() * y) + x) * 4) + 3] = (byte)c.getBlue();
			}
		}

		return b;
	}

	public boolean isSameImage(NFImage other) {
		if(getHash().equals(other.getHash())) {
			return true;
		}
		return false;
	}

	public String getHash() {
		if(needNewHash) {
			hash = NUtil.getHashAsString(getBytes());
			needNewHash = false;
		}
		return hash;
	}
}
