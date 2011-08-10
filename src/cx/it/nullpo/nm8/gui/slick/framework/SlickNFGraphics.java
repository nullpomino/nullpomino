package cx.it.nullpo.nm8.gui.slick.framework;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import cx.it.nullpo.nm8.gui.framework.NFColor;
import cx.it.nullpo.nm8.gui.framework.NFFont;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFImage;

/**
 * Slick implementation of NFGraphics
 */
public class SlickNFGraphics implements NFGraphics {
	private static final long serialVersionUID = -8818900599154270692L;

	/** Slick native graphics */
	protected Graphics g;

	/**
	 * Constructor
	 * @param g Slick native graphics
	 */
	public SlickNFGraphics(Graphics g) {
		this.g = g;
	}

	/**
	 * Get Slick native graphics
	 * @return Slick native graphics
	 */
	public Graphics getNativeGraphics() {
		return g;
	}

	/**
	 * Get Slick native Image from an instance of SlickNFImage
	 * @param img SlickNFImage
	 * @return Slick native Image
	 */
	public Image getNativeImage(NFImage img) {
		if(img instanceof SlickNFImage) {
			SlickNFImage img2 = (SlickNFImage)img;
			return img2.getNativeImage();
		}
		throw new IllegalArgumentException("The img is not a SlickNFImage");
	}

	/**
	 * Convert NFColor to Slick native Color
	 * @param col NFColor
	 * @return Slick native Color
	 */
	public Color nfColor2Native(NFColor col) {
		return new Color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
	}

	/**
	 * Convert Slick native Color to NFColor
	 * @param col Slick native Color
	 * @return NFColor
	 */
	public NFColor nativeColor2NF(Color col) {
		return new NFColor(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
	}

	public boolean isSupportColorFilter() {
		return true;
	}

	public void drawImage(NFImage img, int x, int y) {
		Image nimg = getNativeImage(img);
		g.drawImage(nimg, x, y);
	}

	public void drawImage(NFImage img, int x, int y, NFColor col) {
		Image nimg = getNativeImage(img);
		g.drawImage(nimg, x, y, nfColor2Native(col));
	}

	public void drawImage(NFImage img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
		Image nimg = getNativeImage(img);
		g.drawImage(nimg, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
	}

	public void drawImage(NFImage img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, NFColor col) {
		Image nimg = getNativeImage(img);
		g.drawImage(nimg, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, nfColor2Native(col));
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		g.drawLine(x1, y1, x2, y2);
	}

	public void drawRect(int x, int y, int width, int height) {
		g.drawRect(x, y, width, height);
	}

	public void drawString(String str, int x, int y) {
		g.drawString(str, x, y);
	}

	public int getStringWidth(String str) {
		return g.getFont().getWidth(str);
	}

	public int getStringHeight(String str) {
		return g.getFont().getHeight(str);
	}

	public void fillRect(int x, int y, int width, int height) {
		g.fillRect(x, y, width, height);
	}

	public NFColor getColor() {
		return nativeColor2NF(g.getColor());
	}

	public NFFont getFont() {
		return new SlickNFFont(g.getFont());
	}

	public void resetFont() {
		g.resetFont();
	}

	public void setColor(NFColor col) {
		g.setColor(nfColor2Native(col));
	}

	public void setFont(NFFont font) {
		if(font instanceof SlickNFFont) {
			SlickNFFont f = (SlickNFFont)font;
			g.setFont(f.getNativeFont());
		} else {
			throw new IllegalArgumentException("This NFFont is not an instance of SlickNFFont");
		}
	}
	
	public void translate(int x, int y) {
		g.translate(x,y);
	}
}
