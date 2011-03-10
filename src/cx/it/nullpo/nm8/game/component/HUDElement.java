package cx.it.nullpo.nm8.game.component;

import java.awt.Color;
import java.io.Serializable;

/**
 * Element for HUD
 */
public class HUDElement implements Serializable {
	private static final long serialVersionUID = -5807935489350339340L;

	/** Label text */
	protected Object labelText;

	/** Label color */
	protected Color labelColor;

	/** Body text */
	protected Object bodyText;

	/** Body color */
	protected Color bodyColor;
}
