package mu.nu.nullpo.gui.swing;

import java.awt.Color;

import mu.nu.nullpo.gui.common.AbstractColor;

public class ColorSwing implements AbstractColor {
	
	Color color;

	public ColorSwing(int r, int g, int b) {
		color = new Color(r, g, b);
	}
	
	public ColorSwing(int r, int g, int b, int a) {
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
