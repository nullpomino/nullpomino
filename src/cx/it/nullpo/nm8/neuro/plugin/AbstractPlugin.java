package cx.it.nullpo.nm8.neuro.plugin;

import cx.it.nullpo.nm8.neuro.core.NEURO;
import cx.it.nullpo.nm8.neuro.core.NEUROPlugin;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;

/**
 * AbstractPlugin is a parent class which implements common functionality for NEUROPlugins.
 * @author Zircean
 *
 */
public abstract class AbstractPlugin implements NEUROPlugin {

	/** The parent NEURO. */
	protected NEURO neuro;

	/**
	 * Constructor for AbstractPlugin.
	 */
	public AbstractPlugin() {}

	public void init(NEURO parent) throws PluginInitializationException {
		neuro = parent;
		parent.addPlugin(this);
	}



}
