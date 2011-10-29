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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

	/** Log */
	private Log log = LogFactory.getLog(SwingNFGraphics.class);

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

	/** Color filter cache entry list */
	protected List<CFCacheEntry> listCFCacheEntry = Collections.synchronizedList(new ArrayList<CFCacheEntry>());

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
		if(!sys.isEnableAlphaImage()) return;
		if(nfColor.getAlpha() >= 255) return;
		AlphaComposite newComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (nfColor.getAlpha() / 255f));
		g.setComposite(newComposite);
	}

	/**
	 * Set the AlphaComposite for transparency.
	 * @param awtColor AWT Color with alpha setting
	 */
	protected void setAlphaComposite(Color awtColor) {
		if(!sys.isEnableAlphaImage()) return;
		if(awtColor.getAlpha() >= 255) return;
		AlphaComposite newComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (awtColor.getAlpha() / 255f));
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
		if(col.getAlpha() <= 0) return;
		Image nimg = getNativeImage(img);

		if(col.isColorFilter() && sys.isEnableColorFilter()) {
			String hash = img.getHash();
			CFCacheEntry cache = getCFCache(hash, col);

			if(cache != null) {
				SwingNFImage nfCachedImage = (SwingNFImage)cache.getImage();
				Image nimgC = nfCachedImage.getNativeImage();

				setAlphaComposite(col);
				g.drawImage(nimgC, x, y, null);
				clearAlphaComposite();
			} else {
				SwingNFRGBImageFilter filter = new SwingNFRGBImageFilter(col);
				FilteredImageSource fis = new FilteredImageSource(nimg.getSource(), filter);
				SwingNFSystem swSys = (SwingNFSystem)sys;
				Image nimgF = swSys.getGameWrapper().createImage(fis);

				setAlphaComposite(col);
				g.drawImage(nimgF, x, y, null);
				clearAlphaComposite();

				CFCacheEntry newCache = new CFCacheEntry(new SwingNFImage(nimgF, sys), col, hash);
				listCFCacheEntry.add(newCache);

				log.trace("New CFCacheEntry added: " + hash + " " + col);
			}
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
		if(col.getAlpha() <= 0) return;
		Image nimg = getNativeImage(img);

		if(col.isColorFilter() && sys.isEnableColorFilter()) {
			String hash = img.getHash();
			CFCacheEntry cache = getCFCache(hash, col);

			if(cache != null) {
				SwingNFImage nfCachedImage = (SwingNFImage)cache.getImage();
				Image nimgC = nfCachedImage.getNativeImage();

				setAlphaComposite(col);
				g.drawImage(nimgC, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
				clearAlphaComposite();
			} else {
				SwingNFRGBImageFilter filter = new SwingNFRGBImageFilter(col);
				FilteredImageSource fis = new FilteredImageSource(nimg.getSource(), filter);
				SwingNFSystem swSys = (SwingNFSystem)sys;
				Image nimgF = swSys.gameWrapper.createImage(fis);

				setAlphaComposite(col);
				g.drawImage(nimgF, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
				clearAlphaComposite();

				CFCacheEntry newCache = new CFCacheEntry(new SwingNFImage(nimgF, sys), col, hash);
				listCFCacheEntry.add(newCache);

				log.trace("New CFCacheEntry added: " + hash + " " + col);
			}
		} else {
			setAlphaComposite(col);
			g.drawImage(nimg, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
			clearAlphaComposite();
		}
	}

	public void drawString(String str, int x, int y) {
		if(getColor().getAlpha() <= 0) return;
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
		if(getColor().getAlpha() <= 0) return;
		setAlphaComposite(getColor());
		g.drawLine(x1, y1, x2, y2);
		clearAlphaComposite();
	}

	public void drawRect(int x, int y, int width, int height) {
		if(getColor().getAlpha() <= 0) return;
		setAlphaComposite(getColor());
		g.drawRect(x, y, width, height);
		clearAlphaComposite();
	}

	public void fillRect(int x, int y, int width, int height) {
		if(getColor().getAlpha() <= 0) return;
		setAlphaComposite(getColor());
		g.fillRect(x, y, width, height);
		clearAlphaComposite();
	}

	public void gradientRect(int x, int y, int width, int height, int sx, int sy, NFColor sc, int ex, int ey, NFColor ec) {
		if(getColor().getAlpha() <= 0) return;

		if(sys.isEnableGradient()) {
			Paint curPaint = g.getPaint();
			GradientPaint gradient = new GradientPaint(sx, sy, sc.toAWTColor(), ex, ey, ec.toAWTColor());
			g.setPaint(gradient);
			setAlphaComposite(getColor());
			g.drawRect(x, y, width, height);
			clearAlphaComposite();
			g.setPaint(curPaint);
		} else {
			setAlphaComposite(getColor());
			setColor(sc);
			g.drawRect(x, y, width, height);
			clearAlphaComposite();
		}
	}

	public void gradientFillRect(int x, int y, int width, int height, int sx, int sy, NFColor sc, int ex, int ey, NFColor ec) {
		if(getColor().getAlpha() <= 0) return;

		if(sys.isEnableGradient()) {
			Paint curPaint = g.getPaint();
			GradientPaint gradient = new GradientPaint(sx, sy, sc.toAWTColor(), ex, ey, ec.toAWTColor());
			g.setPaint(gradient);
			setAlphaComposite(getColor());
			g.fillRect(x, y, width, height);
			clearAlphaComposite();
			g.setPaint(curPaint);
		} else {
			setAlphaComposite(getColor());
			setColor(sc);
			g.fillRect(x, y, width, height);
			clearAlphaComposite();
		}
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

	/**
	 * Search the color filter cache
	 * @param strHash Hash of the image
	 * @param col NFColor
	 * @return The cache, or null if not found
	 */
	public CFCacheEntry getCFCache(String strHash, NFColor col) {
		synchronized (listCFCacheEntry) {
			Iterator<CFCacheEntry> it = listCFCacheEntry.iterator();

			while(it.hasNext()) {
				CFCacheEntry c = it.next();

				if(strHash.equals(c.getImageHash()) && col.equals(c.getColor())) {
					//log.debug("Cache found " + strHash + " " + col);
					return c;
				}
			}
		}
		return null;
	}

	/**
	 * Color filter cache entry
	 */
	static protected class CFCacheEntry implements Serializable {
		private static final long serialVersionUID = 1L;

		private NFImage img;
		private NFColor col;
		private String imgHash;

		public CFCacheEntry(NFImage img, NFColor col, String imgHash) {
			this.img = img;
			this.col = col;
			this.imgHash = imgHash;
		}

		public NFImage getImage() {
			return img;
		}

		public NFColor getColor() {
			return col;
		}

		public String getImageHash() {
			return imgHash;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((col == null) ? 0 : col.hashCode());
			result = prime * result
					+ ((imgHash == null) ? 0 : imgHash.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CFCacheEntry other = (CFCacheEntry) obj;
			if (col == null) {
				if (other.col != null)
					return false;
			} else if (!col.equals(other.col))
				return false;
			if (imgHash == null) {
				if (other.imgHash != null)
					return false;
			} else if (!imgHash.equals(other.imgHash))
				return false;
			return true;
		}
	}
}
