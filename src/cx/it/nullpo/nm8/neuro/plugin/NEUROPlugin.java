package cx.it.nullpo.nm8.neuro.plugin;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.core.NEURO;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;

/**
 * NEUROPlugins are the subscribers to NEUROEvents, which makes them the heart and soul of NullpoMino.
 * @author Zircean
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
	 * Gets the author of this plugin.
	 */
	String getAuthor();
	
	/**
	 * Initializes the plugin.
	 * @param parent the NEURO to register this plugin with
	 * @throws PluginInitializationException if something goes wrong during the initialization process
	 */
	void init(NEURO parent) throws PluginInitializationException;
	
	/**
	 * Draws the plugin.
	 * @param g the NFGraphics to draw this plugin with.
	 */
	void draw(NFGraphics g);
	
	/**
	 * Stops the plugin. Used if the plugin has any resources that should be freed before shutdown.
	 */
	void stop();
	
}
