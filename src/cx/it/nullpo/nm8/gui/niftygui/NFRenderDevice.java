package cx.it.nullpo.nm8.gui.niftygui;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cx.it.nullpo.nm8.gui.framework.NFColor;
import cx.it.nullpo.nm8.gui.framework.NFFont;
import cx.it.nullpo.nm8.gui.framework.NFImage;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import de.lessvoid.nifty.render.BlendMode;
import de.lessvoid.nifty.spi.render.MouseCursor;
import de.lessvoid.nifty.spi.render.RenderDevice;
import de.lessvoid.nifty.spi.render.RenderFont;
import de.lessvoid.nifty.spi.render.RenderImage;
import de.lessvoid.nifty.tools.Color;

/**
 * NullpoMino Framework implementation of NiftyGUI's RenderDevice
 */
public class NFRenderDevice implements RenderDevice {
	/** Log */
	//private Log log = LogFactory.getLog(NFRenderDevice.class);
	/** NFSystem */
	protected NFSystem sys;
	/** Font cache */
	protected List<NFRenderFont> listFontCache = Collections.synchronizedList(new ArrayList<NFRenderFont>());

	public NFRenderDevice(NFSystem sys) {
		this.sys = sys;
	}

	public RenderImage createImage(String filename, boolean filterLinear) {
		try {
			return new NFRenderImage(sys.loadImage(filename));
		} catch (IOException e) {
			throw new RuntimeException("Image " + filename + " not found", e);
		} catch (Exception e) {
			throw new RuntimeException("Image " + filename + " Unknown loading failure", e);
		}
	}

	public RenderFont createFont(String filename) {
		try {
			NFRenderFont f = new NFRenderFont(sys.loadFont(filename, 16, false, false), filename);
			listFontCache.add(f);
			return f;
		} catch (IOException e) {
			throw new RuntimeException("Font " + filename + " not found", e);
		} catch (UnsupportedOperationException e) {
			if(filename.endsWith(".fnt")) {
				throw new RuntimeException("This system doesn't have the support of .fnt files (" + filename + ")", e);
			} else {
				throw new RuntimeException("Font " + filename + " is not supported in this system", e);
			}
		} catch (Exception e) {
			throw new RuntimeException("Font " + filename + " Unknown loading failure", e);
		}
	}

	public int getWidth() {
		return sys.getOriginalWidth();
	}

	public int getHeight() {
		return sys.getOriginalHeight();
	}

	public void beginFrame() {

	}

	public void endFrame() {

	}


	public void clear() {
		sys.getGraphics().setColor(NFColor.black);
		sys.getGraphics().fillRect(0, 0, sys.getOriginalWidth(), sys.getOriginalHeight());
	}

	public void setBlendMode(BlendMode renderMode) {

	}

	public void renderQuad(int x, int y, int width, int height, Color color) {
		sys.getGraphics().setColor(nifty2NFColor(color));
		sys.getGraphics().fillRect(x, y, width, height);
	}


	public void renderQuad(int x, int y, int width, int height, Color topLeft,
			Color topRight, Color bottomRight, Color bottomLeft) {
		if(topLeft.getColorString().equals(topRight.getColorString())) {
			sys.getGraphics().gradientFillRect(x, y, width, height, x, y, nifty2NFColor(topLeft), x, y+height, nifty2NFColor(bottomLeft));
		} else if(topLeft.getColorString().equals(bottomLeft.getColorString())) {
			sys.getGraphics().gradientFillRect(x, y, width, height, x, y, nifty2NFColor(topLeft), x+width, y, nifty2NFColor(topRight));
		}
	}

	public void renderImage(RenderImage image, int x, int y, int width, int height, Color color, float imageScale) {
		NFRenderImage nfRenderImage = (NFRenderImage)image;
		NFImage nfImage = nfRenderImage.getNFImage();
		sys.getGraphics().drawImage(nfImage, x, y, x+width, y+height, 0, 0, width, height, nifty2NFColor(color));
	}

	public void renderImage(RenderImage image, int x, int y, int w, int h,
			int srcX, int srcY, int srcW, int srcH, Color color, float scale,
			int centerX, int centerY)
	{
		NFRenderImage nfRenderImage = (NFRenderImage)image;
		NFImage nfImage = nfRenderImage.getNFImage();
		sys.getGraphics().drawImage(nfImage, x, y, x+w, y+h, srcX, srcY, srcX+srcW, srcY+srcH, nifty2NFColor(color));
	}

	public void renderFont(RenderFont font, String text, int x, int y, Color fontColor, float size) {
		NFRenderFont nfRenderFontOriginal = (NFRenderFont)font;
		int originalSize = nfRenderFontOriginal.getNFFont().getSize();
		int newSize = (int)(size * originalSize);
		NFRenderFont nfRenderFontNew = getCachedFont(nfRenderFontOriginal, newSize);	// Try to get the cached font
		if(nfRenderFontNew == null) {
			// Not cached? Then create a new font with desired size
			Font awtOriginalFont = nfRenderFontOriginal.getNFFont().getFont();
			if(awtOriginalFont != null) {
				NFFont nfFontNew = sys.loadFont(awtOriginalFont, newSize, false, false);
				if(nfFontNew != null) {
					//log.debug("FontFilename:" + nfRenderFontOriginal.getFilename() + ", OriginalSize:" + nfRenderFontOriginal.getNFFont().getSize() +
					//		  ", NewSize:" + newSize);
					nfRenderFontNew = new NFRenderFont(nfFontNew, nfRenderFontOriginal.getFilename());
					listFontCache.add(nfRenderFontNew);
				}
			}
		}
		if(nfRenderFontNew == null) {
			// If can't create a new desired sized font, fallback to the original
			nfRenderFontNew = nfRenderFontOriginal;
		}

		NFFont nfFont = nfRenderFontNew.getNFFont();
		if(nfFont.isGlyphLoadingRequired()) {
			nfFont.addGlyphs(text);
			nfFont.loadGlyphs();
		}
		sys.getGraphics().setFont(nfFont);
		sys.getGraphics().setColor(nifty2NFColor(fontColor));
		sys.getGraphics().drawString(text, x, y);
	}

	public void enableClip(int x0, int y0, int x1, int y1) {
		sys.getGraphics().setClip(x0, y0, x1-x0, y1-y0);
	}

	public void disableClip() {
		sys.getGraphics().clearClip();
	}

	public MouseCursor createMouseCursor(String filename, int hotspotX, int hotspotY) throws IOException {
		return null;
	}

	public void enableMouseCursor(MouseCursor mouseCursor) {

	}

	public void disableMouseCursor() {

	}

	protected NFRenderFont getCachedFont(NFRenderFont f1, int size) {
		String filename1 = f1.getFilename();

		synchronized (listFontCache) {
			Iterator<NFRenderFont> it = listFontCache.iterator();
			while(it.hasNext()) {
				NFRenderFont f2 = it.next();
				String filename2 = f2.getFilename();

				if(filename1.equals(filename2) && f2.getNFFont().getSize() == size) {
					return f2;
				}
			}
		}

		return null;
	}

	public static NFColor nifty2NFColor(Color c) {
		NFColor nfColor = new NFColor((int)(c.getRed() * 255), (int)(c.getGreen() * 255), (int)(c.getBlue() * 255), (int)(c.getAlpha() * 255));
		return nfColor;
	}
}
