package cx.it.nullpo.nm8.neuro.gui;

import java.util.Collection;

/**
 * ScreenManager is an interface for defining ways of managing the GUIs of different plugins.
 * It is useful for NEURO implementations that use the overlay, and it allows standard NEURO
 * implementations to have extended GUI capabilities.
 * @author Zircean
 *
 */
public interface ScreenManager {

	/**
	 * Adds a GUI to this manager.
	 * @param s the holder to register this GUI to
	 */
	void register(ScreenHolder s);
	
	/**
	 * Gets the table of registered holders for this manager.
	 * @return a collection with all registered holders, or null if this
	 * manager does not allow manual changing.
	 */
	Collection<ScreenHolder> getChangeTable();
	
	/**
	 * Changes the current screen, if this operation is allowed.
	 * @param s the holder whose GUI should be the current screen
	 */
	void change(ScreenHolder s);
	
	/**
	 * Removes the current screen from the manager.
	 */
	void remove();
	
	/**
	 * Removes the screen corresponding to the given holder.
	 */
	void remove(ScreenHolder s);
	
	/**
	 * Removes all screens from this manager.
	 */
	void removeAll();
	
	/**
	 * Renders the current state of the GUI.
	 */
	void render();
}
