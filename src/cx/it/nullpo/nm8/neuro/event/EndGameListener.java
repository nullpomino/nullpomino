package cx.it.nullpo.nm8.neuro.event;

import java.util.EventListener;

/**
 * The EndGameEventListener interface is implemented by all plugins which wish to receive EndGameEvents.
 * @author Zircean
 *
 */
public interface EndGameListener extends EventListener {

	/**
	 * Invoked when a game has ended.
	 */
	void gameEnded(EndGameEvent e);
	
}
