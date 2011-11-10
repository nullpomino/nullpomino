package cx.it.nullpo.nm8.neuro.gui;

import de.lessvoid.nifty.Nifty;

/**
 * An object which implements ScreenHolder can be used in a ScreenManager, and subsequently,
 * it can be drawn by NEURO.
 * @author Zircean
 *
 */
public interface ScreenHolder {

	/**
	 * Sets the Nifty that will draw this object.
	 */
	void initGUI(Nifty n);
	
	/**
	 * Gets the Nifty that will draw this object.
	 */
	Nifty getGUI();
	
}
