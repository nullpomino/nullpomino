package mu.nu.nullpo.gui.sdl;

import org.apache.log4j.Logger;

import sdljava.SDLException;
import sdljava.video.SDLColor;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;
import mu.nu.nullpo.gui.common.AbstractColor;

public class ColorSDL implements AbstractColor {
	
	static Logger log = Logger.getLogger(ColorSDL.class);
	
	SDLColor color;
	SDLSurface context;
	
	public ColorSDL(int r, int g, int b, SDLSurface context) {
		color = new SDLColor(r,g,b);
		this.context = context;
	}
	
	public ColorSDL(int r, int g, int b, int a, SDLSurface context) {
		color = new SDLColor(r,g,b,a);
		this.context = context;
	}

	@Override
	public int getRed() {
		return color.getRed();
	}

	@Override
	public int getGreen() {
		return color.getGreen();
	}

	@Override
	public int getBlue() {
		return color.getBlue();
	}

	@Override
	public int getAlpha() {
		return color.getAlpha();
	}

	public long toBits() {
		try {
			return SDLVideo.mapRGBA(context.getFormat(), getRed(), getGreen(), getBlue(), getAlpha());
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
		return 0;
	}

}
