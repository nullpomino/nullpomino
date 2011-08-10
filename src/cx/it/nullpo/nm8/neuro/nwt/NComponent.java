package cx.it.nullpo.nm8.neuro.nwt;

import cx.it.nullpo.nm8.gui.framework.NFColor;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;

public abstract class NComponent {
	
	/** The background color of this NComponent. */
	private NFColor background;
	/** The width of this NComponent. */
	private float width;
	/** The height of this NComponent. */
	private float height;
	/** The visibility of this NComponent. */
	private boolean visible;
	
	public NComponent() {
		background = new NFColor(0,0,0);
	}

	/**
	 * Update this component.
	 */
	public final void update(NFGraphics g) {
		paint(g);
	}
	
	/**
	 * Paint this component.
	 */
	public abstract void paint(NFGraphics g);
	
	/**
	 * Gets the background color of this component.
	 */
	public final NFColor getBackground() {
		return background;
	}
	
	/**
	 * Sets the background color to the given color.
	 */
	public void setBackground(NFColor color) {
		background = color;
	}
	
	/**
	 * Gets the width of this component.
	 */
	public float getWidth() {
		return width;
	}
	
	/**
	 * Gets the height of this component.
	 */
	public float getHeight() {
		return height;
	}
	
	/**
	 * Gets the visibility of this component.
	 */
	public boolean getVisible() {
		return visible;
	}
	
	/**
	 * Sets the visibility of this component to the given value.
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
