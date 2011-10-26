package cx.it.nullpo.nm8.gui.slick.framework;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import cx.it.nullpo.nm8.gui.framework.NFColor;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFImage;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.util.NUtil;

/**
 * Slick implementation of NFImage
 */
public class SlickNFImage implements NFImage {
	private static final long serialVersionUID = 7635534276019899998L;

	/** Slick native image */
	protected Image nativeImage;

	/** SlickNFGraphics of this image */
	protected SlickNFGraphics g;

	/** NFSystem */
	protected NFSystem sys;

	/** Current hash */
	protected String hash;

	/** true if we must update the hash */
	protected boolean needNewHash;

	/**
	 * Constructor
	 * @param nativeImage Slick native image
	 * @param sys NFSystem
	 */
	public SlickNFImage(Image nativeImage, NFSystem sys) {
		this.nativeImage = nativeImage;
		this.sys = sys;
		this.hash = NUtil.getHashAsString(getBytes());
		this.needNewHash = false;
	}

	/**
	 * Get Slick native image
	 * @return Slick native image
	 */
	public Image getNativeImage() {
		return nativeImage;
	}

	public NFGraphics getGraphics() {
		needNewHash = true;

		try {
			if(g != null) return g;
			g = new SlickNFGraphics(nativeImage.getGraphics(), sys);
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

	public NFImage getSubImage(int x, int y, int width, int height) {
		NFGraphics g = getGraphics();
		NFSystem sys = g.getNFSystem();
		NFImage newImage = sys.createImage(width, height);
		newImage.getGraphics().drawImage(this, 0, 0, width, height, x, y, x+width, y+height);
		return newImage;
	}

	public NFColor getColor(int x, int y) {
		return SlickNFGraphics.nativeColor2NF(nativeImage.getColor(x, y));
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
