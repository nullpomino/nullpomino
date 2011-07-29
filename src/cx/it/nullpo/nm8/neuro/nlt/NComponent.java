package cx.it.nullpo.nm8.neuro.nlt;

import cx.it.nullpo.nm8.gui.framework.NFColor;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.nlt.util.NDimension;


public abstract class NComponent {
	
	/** The size of this NComponent. */
	protected NDimension size;
	/** The background color of this NComponent. */
	protected NFColor background;
	
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
		return size;
	}
	
	/**
	 * Sets the size of this component. The new size will not go below the minimum size.
	 */
	public final void setSize(NDimension nd) {
		NDimension md = getMinimumSize();
		size = new NDimension(Math.max(md.getWidth(),nd.getWidth()),
				Math.max(md.getHeight(),nd.getHeight()));
	}
}
