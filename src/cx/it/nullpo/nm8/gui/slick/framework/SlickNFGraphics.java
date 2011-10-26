package cx.it.nullpo.nm8.gui.slick.framework;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Rectangle;

import cx.it.nullpo.nm8.gui.framework.NFColor;
import cx.it.nullpo.nm8.gui.framework.NFFont;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFImage;
import cx.it.nullpo.nm8.gui.framework.NFSystem;

/**
 * Slick implementation of NFGraphics
 */
public class SlickNFGraphics implements NFGraphics {
	private static final long serialVersionUID = -8818900599154270692L;

	/** Slick native graphics */
	protected Graphics g;

	/** NFSystem */
	protected NFSystem sys;

	/** Default SlickNFFont */
	protected SlickNFFont defaultFont;

	/** Current SlickNFFont */
	protected SlickNFFont curFont;

	/**
	 * Convert NFColor to Slick native Color
	 * @param col NFColor
	 * @return Slick native Color
	 */
	public static Color nfColor2Native(NFColor col) {
		return new Color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
	}

	/**
	 * Convert Slick native Color to NFColor
	 * @param col Slick native Color
	 * @return NFColor
	 */
	public static NFColor nativeColor2NF(Color col) {
		return new NFColor(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
	}

	/**
	 * Constructor
	 * @param g Slick native graphics
	 * @param sys NFSystem
	 */
	public SlickNFGraphics(Graphics g, NFSystem sys) {
		this.g = g;
		this.sys = sys;

		curFont = new SlickNFFont(g.getFont());
		defaultFont = curFont;
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
		//g.drawString(str, x, y);
		curFont.drawString(this, str, x, y);
	}

	public int getStringWidth(String str) {
		return curFont.getStringWidth(str);
	}

	public int getStringHeight(String str) {
		return curFont.getStringHeight(str);
	}

	public void fillRect(int x, int y, int width, int height) {
		g.fillRect(x, y, width, height);
	}

	public void gradientRect(int x, int y, int width, int height, int sx, int sy, NFColor sc, int ex, int ey, NFColor ec) {
		GradientFill gf = new GradientFill(sx, sy, nfColor2Native(sc), ex, ey, nfColor2Native(ec));
		Rectangle rect = new Rectangle(x, y, width, height);
		g.draw(rect, gf);
	}

	public void gradientFillRect(int x, int y, int width, int height, int sx, int sy, NFColor sc, int ex, int ey, NFColor ec) {
		GradientFill gf = new GradientFill(sx, sy, nfColor2Native(sc), ex, ey, nfColor2Native(ec));
		Rectangle rect = new Rectangle(x, y, width, height);
		g.fill(rect, gf);
	}

	public NFColor getColor() {
		return nativeColor2NF(g.getColor());
	}

	public NFFont getFont() {
		return new SlickNFFont(g.getFont());
	}

	public void resetFont() {
		g.resetFont();
		curFont = new SlickNFFont(g.getFont());
		defaultFont = curFont;
	}

	public void setColor(NFColor col) {
		g.setColor(nfColor2Native(col));
	}

	public void setFont(NFFont font) {
		if(font instanceof SlickNFFont) {
			SlickNFFont f = (SlickNFFont)font;
			curFont = f;
			g.setFont(f.getNativeFont());
		} else {
			throw new IllegalArgumentException("This NFFont is not an instance of SlickNFFont");
		}
	}

	public void translate(int x, int y) {
		g.translate(x,y);
	}

	public void setClip(int x, int y, int width, int height) {
		g.setClip(x,y,width,height);
	}

	public void clearClip() {
		g.clearClip();
	}

	public NFSystem getNFSystem() {
		return sys;
	}
}
