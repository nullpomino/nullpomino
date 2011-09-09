package cx.it.nullpo.nm8.game.component.hud;

import java.io.Serializable;

/**
 * Base HUD element. This element does nothing.
 */
public class HUDElement implements Serializable {
	private static final long serialVersionUID = -5807935489350339340L;

	/** Visible flag. If false, it will not appear on the screen, but getNumberOfLines() will still return the normal value. */
	public boolean isVisible = true;

	/**
	 * Constructor
	 */
	public HUDElement() {
	}

	/**
	 * Copy Constructor
	 * @param e Copy Source
	 */
	public HUDElement(HUDElement e) {
		this.copy(e);
	}

	/**
	 * Copy from another HUDElement
	 * @param e Copy Source
	 */
	public void copy(HUDElement e) {
		isVisible = e.isVisible;
	}

	/**
	 * Get the number of text lines of this HUD element
	 * @return Number of text lines
	 */
	public int getNumberOfLines() {
		return 0;
	}
}
