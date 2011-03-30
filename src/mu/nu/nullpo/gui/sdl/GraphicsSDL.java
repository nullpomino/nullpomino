package mu.nu.nullpo.gui.sdl;

import org.apache.log4j.Logger;

import mu.nu.nullpo.gui.common.AbstractColor;
import mu.nu.nullpo.gui.common.AbstractGraphics;
import mu.nu.nullpo.gui.common.AbstractImage;
import sdljava.SDLException;
import sdljava.video.SDLRect;
import sdljava.video.SDLSurface;

public class GraphicsSDL implements AbstractGraphics {
	
	static Logger log = Logger.getLogger(GraphicsSDL.class);
	
	SDLSurface surface;
	ColorSDL c;
	
	public GraphicsSDL(SDLSurface surface) {
		this.surface = surface;
	}

	@Override
	public void drawImage(AbstractImage img, int dx1, int dy1, int dx2,
			int dy2, int sx1, int sy1, int sx2, int sy2) {
		
		SDLSurface s = getSurface(img);
		if (s == null) {
			return;
		}
		
		SDLRect rectDst = new SDLRect(dx1, dy1, dx2 - dx1, dy2 - dy1);
		SDLRect rectSrc = new SDLRect(sx1, sy1, sx2 - sx1, sy2 - sx2);
		NullpoMinoSDL.fixRect(rectSrc, rectDst);
		
		try {
			s.blitSurface(rectSrc, surface, rectDst);
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
		
	}

	@Override
	public void drawImage(AbstractImage img, int x, int y) {

		SDLSurface s = getSurface(img);
		if (s == null) {
			return;
		}
		
		int w = s.getWidth();
		int h = s.getHeight();
		
		SDLRect rectDst = new SDLRect(x, y, w, h);
		SDLRect rectSrc = new SDLRect(0, 0, w, h);
		NullpoMinoSDL.fixRect(rectSrc, rectDst);
		
		try {
			s.blitSurface(rectSrc, surface, rectDst);
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
		
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		try {
			surface.fillRect(new SDLRect(x, y, width, height), c.toBits());
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
		
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
		int sx = (x1 < x2 ? 1 : -1);
		int sy = (y1 < y2 ? 1 : -1);
		int err = dx-dy;
		
		SDLRect s = new SDLRect(x1, y1, 1, 1);
		int err2;
		while (true) {
			try {
				surface.fillRect(s, c.toBits());
			} catch (SDLException e) {
				log.debug("SDLException thrown", e);
			}
			if (x1 == x2 && y1 == y2) {
				break;
			}
			err2 = 2*err;
			if (err2 > -dy) {
				err -= dy;
				x1 += sx;
			}
			if (err2 < dx) {
				err += dx;
				y1 += sy;
			}
			s.setX(x1);
			s.setY(y1);
		}
		
	}

	@Override
	public void setColor(AbstractColor color) {
		
		ColorSDL c;
		try {
			c = (ColorSDL) color;
		} catch (ClassCastException e) {
			log.debug("Failed to set color: wrong type");
			return;
		}
		this.c = c;
	}
	
	private SDLSurface getSurface(AbstractImage img) {
		ImageSDL i;
		try {
			i = (ImageSDL) img;
		} catch (ClassCastException e) {
			log.debug("Failed to get image: wrong type");
			return null;
		}
		return i.getSurface();
	}
	
}
