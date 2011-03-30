package mu.nu.nullpo.gui.slick;

import mu.nu.nullpo.gui.common.AbstractColor;

import org.newdawn.slick.Color;

public class ColorSlick implements AbstractColor {

	Color color;
	
	public ColorSlick(int r, int g, int b) {
		color = new Color(r, g, b);
	}
	
	public ColorSlick(int r, int g, int b, int a) {
		color = new Color(r, g, b, a);
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

	public Color getColor() {
		return color;
	}
	
	
}
