package cx.it.nullpo.nm8.neuro.nwt;

import cx.it.nullpo.nm8.gui.framework.NFColor;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.nwt.util.NDimension;

public abstract class NComponent {
	
	/** The minimum size of this NComponent. */
	private NDimension minSize;
	/** The size of this NComponent. */
	private NDimension size;
	/** The maximum size of this NComponent. */
	private NDimension maxSize;
	/** The background color of this NComponent. */
	private NFColor background;
	/** The visibility of this NComponent. */
	private boolean visible;
	
	public NComponent() {
		size = new NDimension(0,0);
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
	 * Gets the size of this component.
	 */
	public final NDimension getSize() {
		return size;
	}
	
	/**
	 * Gets the minimum size of this component.
	 */
	public NDimension getMinimumSize() {
		return minSize;
	}
	
	/**
	 * Gets the maximum size of this component.
	 */
	public NDimension getMaximumSize() {
		return maxSize;
	}
	
	/**
	 * Sets the size of this component. The new size will not go below the minimum size.
	 */
	public final void setSize(NDimension nd) {
		NDimension md = minSize;
		size = new NDimension(Math.max(md.getWidth(),nd.getWidth()),
				Math.max(md.getHeight(),nd.getHeight()));
	}
	
	/**
	 * Sets the minimum size of this component.
	 */
	public void setMinimumSize(NDimension nd) {
		if (nd.getWidth() >= 0 && nd.getHeight() >= 0) {
			minSize = nd;
		}
	}
	
	/**
	 * Sets the maximum size of this component. The new size will not go below the minimum size.
	 */
	public void setMaximumSize(NDimension nd) {
		NDimension md = minSize;
		maxSize = new NDimension(Math.max(md.getWidth(),nd.getWidth()),
				Math.max(md.getHeight(),nd.getHeight()));
	}
	
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
