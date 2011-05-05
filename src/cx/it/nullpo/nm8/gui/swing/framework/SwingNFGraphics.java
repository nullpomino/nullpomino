package cx.it.nullpo.nm8.gui.swing.framework;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import cx.it.nullpo.nm8.gui.framework.NFColor;
import cx.it.nullpo.nm8.gui.framework.NFFont;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFImage;

/**
 * Swing implementation of NFGraphics
 */
public class SwingNFGraphics implements NFGraphics {
	private static final long serialVersionUID = 1L;

	/** Swing native graphics */
	protected Graphics g;

	/** Default native font; Used by resetFont */
	protected Font defaultNativeFont;

	/**
	 * Constructor
	 * @param g Swing native graphics
	 */
	public SwingNFGraphics(Graphics g) {
		this.g = g;
		defaultNativeFont = g.getFont();
	}

	/**
	 * Get Swing native graphics
	 * @return Swing native graphics
	 */
	public Graphics getNativeGraphics() {
		return g;
	}

	/**
	 * Set Swing native graphics. Only for internal use.
	 * @param g Swing native graphics
	 */
	public void setNativeGraphics(Graphics g) {
		this.g = g;
	}

	/**
	 * Get Swing native Image from an instance of SwingNFImage
	 * @param img SwingNFImage
	 * @return Swing native Image
	 */
	public Image getNativeImage(NFImage img) {
		if(img instanceof SwingNFImage) {
			SwingNFImage img2 = (SwingNFImage)img;
			return img2.getNativeImage();
		}
		throw new IllegalArgumentException("The img is not a SwingNFImage");
	}

	/**
	 * Sorry, there is no support of color filter :(
	 * @return Always false
	 */
	public boolean isSupportColorFilter() {
		return false;
	}

	public void drawImage(NFImage img, int x, int y) {
		Image nimg = getNativeImage(img);
		g.drawImage(nimg, x, y, null);
	}

	public void drawImage(NFImage img, int x, int y, NFColor col) {
		Image nimg = getNativeImage(img);
		g.drawImage(nimg, x, y, null);
	}

	public void drawImage(NFImage img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
		Image nimg = getNativeImage(img);
		g.drawImage(nimg, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}

	public void drawImage(NFImage img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, NFColor col) {
		Image nimg = getNativeImage(img);
		g.drawImage(nimg, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}

	public void drawString(String str, int x, int y) {
		int yoffset = g.getFontMetrics().getAscent();
		g.drawString(str, x, y + yoffset);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		g.drawLine(x1, y1, x2, y2);
	}

	public void drawRect(int x, int y, int width, int height) {
		g.drawRect(x, y, width, height);
	}

	public void fillRect(int x, int y, int width, int height) {
		g.fillRect(x, y, width, height);
	}

	public void setColor(NFColor col) {
		g.setColor(col.toAWTColor());
	}

	public NFColor getColor() {
		return new NFColor(g.getColor());
	}

	public void setFont(NFFont font) {
		g.setFont(font.getFont());
	}

	public NFFont getFont() {
		return new SwingNFFont(g.getFont());
	}

	public void resetFont() {
		g.setFont(defaultNativeFont);
	}
}
