package cx.it.nullpo.nm8.gui.swing.framework;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.image.FilteredImageSource;

import cx.it.nullpo.nm8.gui.framework.NFColor;
import cx.it.nullpo.nm8.gui.framework.NFFont;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFImage;
import cx.it.nullpo.nm8.gui.framework.NFSystem;

/**
 * Swing implementation of NFGraphics
 */
public class SwingNFGraphics implements NFGraphics {
	private static final long serialVersionUID = 1L;

	/** Swing native graphics */
	protected Graphics2D g;

	/** NFSystem */
	protected NFSystem sys;

	/** Default NFFont */
	protected NFFont defaultFont;

	/** Current NFFont */
	protected NFFont curFont;

	/** Default Composite */
	protected Composite defaultComposite;

	/** Current color */
	protected NFColor currentColor;

	/**
	 * Constructor
	 * @param g Swing native graphics
	 * @deprecated Use SwingNFGraphics(Graphics g, NFSystem sys) instead
	 */
	@Deprecated
	public SwingNFGraphics(Graphics g) {
		this.g = (Graphics2D)g;
		defaultFont = new SwingNFFont(this.g.getFont(), this, sys);
		curFont = defaultFont;
		defaultComposite = this.g.getComposite();
		currentColor = new NFColor(this.g.getColor());
	}

	/**
	 * Constructor
	 * @param g Swing native graphics
	 * @param sys NFSystem
	 */
	public SwingNFGraphics(Graphics g, NFSystem sys) {
		this.g = (Graphics2D)g;
		this.sys = sys;
		defaultFont = new SwingNFFont(this.g.getFont(), this, sys);
		curFont = defaultFont;
		defaultComposite = this.g.getComposite();
		currentColor = new NFColor(this.g.getColor());
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
		this.g = (Graphics2D)g;
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
	 * Set the AlphaComposite for transparency.
	 * @param nfColor NFColor with alpha setting
	 */
	protected void setAlphaComposite(NFColor nfColor) {
		if(nfColor.getAlpha() >= 255) return;
		AlphaComposite newComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(nfColor.getAlpha() / 255f));
		g.setComposite(newComposite);
	}

	/**
	 * Set the AlphaComposite for transparency.
	 * @param awtColor AWT Color with alpha setting
	 */
	protected void setAlphaComposite(Color awtColor) {
		if(awtColor.getAlpha() >= 255) return;
		AlphaComposite newComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(awtColor.getAlpha() / 255f));
		g.setComposite(newComposite);
	}

	/**
	 * Reset the Composite (disable any transparency)
	 */
	protected void clearAlphaComposite() {
		g.setComposite(defaultComposite);
	}

	/**
	 * We have experimental color filter support, but it's slow so be careful.
	 * @return Always true
	 */
	public boolean isSupportColorFilter() {
		return true;
	}

	public void drawImage(NFImage img, int x, int y) {
		drawImage(img, x, y, getColor());
	}

	public void drawImage(NFImage img, int x, int y, NFColor col) {
		Image nimg = getNativeImage(img);

		if(col.isColorFilter()) {
			SwingNFRGBImageFilter filter = new SwingNFRGBImageFilter(col);
			FilteredImageSource fis = new FilteredImageSource(nimg.getSource(), filter);
			SwingNFSystem swSys = (SwingNFSystem)sys;
			Image nimgF = swSys.gameWrapper.createImage(fis);
			setAlphaComposite(col);
			g.drawImage(nimgF, x, y, null);
			clearAlphaComposite();
		} else {
			setAlphaComposite(col);
			g.drawImage(nimg, x, y, null);
			clearAlphaComposite();
		}
	}

	public void drawImage(NFImage img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
		drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, getColor());
	}

	public void drawImage(NFImage img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, NFColor col) {
		Image nimg = getNativeImage(img);

		if(col.isColorFilter()) {
			SwingNFRGBImageFilter filter = new SwingNFRGBImageFilter(col);
			FilteredImageSource fis = new FilteredImageSource(nimg.getSource(), filter);
			SwingNFSystem swSys = (SwingNFSystem)sys;
			Image nimgF = swSys.gameWrapper.createImage(fis);
			setAlphaComposite(col);
			g.drawImage(nimgF, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
			clearAlphaComposite();
		} else {
			setAlphaComposite(col);
			g.drawImage(nimg, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
			clearAlphaComposite();
		}
	}

	public void drawString(String str, int x, int y) {
		/*
		setAlphaComposite(getColor());
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		int yoffset = g.getFontMetrics().getAscent();
		g.drawString(str, x, y + yoffset);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
		clearAlphaComposite();
		*/
		setAlphaComposite(getColor());
		curFont.drawString(this, str, x, y);
		clearAlphaComposite();
	}

	public int getStringWidth(String str) {
		//Rectangle2D r = g.getFontMetrics().getStringBounds(str, g);
		//return (int)r.getWidth();
		return curFont.getStringWidth(str);
	}

	public int getStringHeight(String str) {
		//Rectangle2D r = g.getFontMetrics().getStringBounds(str, g);
		//return (int)r.getHeight();
		return curFont.getStringHeight(str);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		setAlphaComposite(getColor());
		g.drawLine(x1, y1, x2, y2);
		clearAlphaComposite();
	}

	public void drawRect(int x, int y, int width, int height) {
		setAlphaComposite(getColor());
		g.drawRect(x, y, width, height);
		clearAlphaComposite();
	}

	public void fillRect(int x, int y, int width, int height) {
		setAlphaComposite(getColor());
		g.fillRect(x, y, width, height);
		clearAlphaComposite();
	}

	public void gradientRect(int x, int y, int width, int height, int sx, int sy, NFColor sc, int ex, int ey, NFColor ec) {
		Paint curPaint = g.getPaint();
		GradientPaint gradient = new GradientPaint(sx, sy, sc.toAWTColor(), ex, ey, ec.toAWTColor());
		g.setPaint(gradient);
		setAlphaComposite(getColor());
		g.drawRect(x, y, width, height);
		clearAlphaComposite();
		g.setPaint(curPaint);
	}

	public void gradientFillRect(int x, int y, int width, int height, int sx, int sy, NFColor sc, int ex, int ey, NFColor ec) {
		Paint curPaint = g.getPaint();
		GradientPaint gradient = new GradientPaint(sx, sy, sc.toAWTColor(), ex, ey, ec.toAWTColor());
		g.setPaint(gradient);
		setAlphaComposite(getColor());
		g.fillRect(x, y, width, height);
		clearAlphaComposite();
		g.setPaint(curPaint);
	}

	public void setColor(NFColor col) {
		currentColor = col;
		g.setColor(col.toAWTColor());
	}

	public NFColor getColor() {
		//return new NFColor(g.getColor());
		return currentColor;
	}

	public void setFont(NFFont font) {
		if(font instanceof SwingNFFont) {
			SwingNFFont f = (SwingNFFont)font;
			f.setGraphics(this);
			g.setFont(font.getFont());
			curFont = f;
		} else {
			curFont = font;
		}
	}

	public NFFont getFont() {
		return curFont;
	}

	public void resetFont() {
		g.setFont(defaultFont.getFont());
		curFont = defaultFont;
	}

	public void translate(int x, int y) {
		g.translate(x,y);
	}

	public void setClip(int x, int y, int width, int height) {
		g.setClip(x,y,width,height);
	}

	public void clearClip() {
		g.setClip(null);
	}

	public NFSystem getNFSystem() {
		return sys;
	}
}
