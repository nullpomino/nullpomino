package cx.it.nullpo.nm8.neuro;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;

/**
 * NEUROPlugins are the subscribers to NEUROEvents, which makes them the heart and soul of NullpoMino.
 * @author HP_Administrator
 *
 */
public interface NEUROPlugin {
	
	/**
	 * Gets the name of this plugin.
	 */
	String getName();
	
	/**
	 * Gets the version of this plugin.
	 */
	float getVersion();

	/**
	 * Draws this NEUROPlugin at the specified screen offset.
	 * @param g the NFGraphics with which to draw this NEUROPlugin 
	 * @param x the x offset
	 * @param y the y offset
	 */
	void draw(NFGraphics g, int x, int y);
	
}
