package mu.nu.nullpo.gui.sdl;

import sdljava.video.SDLSurface;
import mu.nu.nullpo.gui.common.AbstractImage;

public class ImageSDL implements AbstractImage {
	
	SDLSurface surface;
	
	public ImageSDL(SDLSurface surface) {
		this.surface = surface;
	}
	
	public SDLSurface getSurface() {
		return surface;
	}
}
