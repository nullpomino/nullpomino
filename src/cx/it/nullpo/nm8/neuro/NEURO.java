package cx.it.nullpo.nm8.neuro;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.event.EndGameListener;
import cx.it.nullpo.nm8.neuro.event.NEUROEvent;

/**
 * NEURO (NullpoMino End-User Relational Overlayer) is the layer that controls everything, so to speak. It
 * establishes an event-subscriber network with all of the plugins that register with it, and passes the
 * events between them. It can also bring up an overlay and draw things to the screen.
 * @author Zircean
 *
 */
public interface NEURO {

	/**
	 * Registers the given listener so it will receive EndGameEvents from this NEURO instance.
	 * @param l the EndGameListener to register
	 */
	void addEndGameListener(EndGameListener l);
	
	/**
	 * Dispatches the given event to all plugins that are subscribed to it.
	 * @param e the NEUROEvent to dispatch
	 */
	void dispatchEvent(NEUROEvent e);
	
	/**
	 * Draws this NEURO instance to the screen. NEURO should be drawn on top of everything else.
	 * @param g the NFGraphics with which to draw this NEURO instance
	 */
	void draw(NFGraphics g);
}
