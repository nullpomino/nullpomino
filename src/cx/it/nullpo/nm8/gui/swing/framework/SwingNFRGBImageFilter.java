package cx.it.nullpo.nm8.gui.swing.framework;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

import cx.it.nullpo.nm8.gui.framework.NFColor;

/**
 * Color filter for Swing
 */
public class SwingNFRGBImageFilter extends RGBImageFilter {
	protected float r = 1f, g = 1f, b = 1f;

	public SwingNFRGBImageFilter(float r, float g, float b) {
		canFilterIndexColorModel = true;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public SwingNFRGBImageFilter(int r, int g, int b) {
		canFilterIndexColorModel = true;
		this.r = (float)(r / 255f);
		this.g = (float)(g / 255f);
		this.b = (float)(b / 255f);
	}

	public SwingNFRGBImageFilter(java.awt.Color color) {
		canFilterIndexColorModel = true;
		this.r = (float)(color.getRed() / 255f);
		this.g = (float)(color.getGreen() / 255f);
		this.b = (float)(color.getBlue() / 255f);
	}

	public SwingNFRGBImageFilter(NFColor color) {
		canFilterIndexColorModel = true;
		this.r = (float)(color.getRed() / 255f);
		this.g = (float)(color.getGreen() / 255f);
		this.b = (float)(color.getBlue() / 255f);
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		java.awt.Color color = new Color(rgb, true);
		float[] array = new float[4];
		color.getComponents(array);
		return new Color(array[0]*r,
						 array[1]*g,
						 array[2]*b,
						 array[3]).getRGB();
	}
}
