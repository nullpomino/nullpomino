package cx.it.nullpo.nm8.neuro.core;

import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.plugin.NEUROPlugin;

/**
 * SubNEURO is a type of NEURO which has the event-passing functionality of NEUROBase, but which is also
 * a plugin. SubNEURO should be extended in order to get the full benefit of
 * @author Zircean
 *
 */
public abstract class SubNEURO extends NEUROBase implements NEUROPlugin {

	/** The parent NEURO. */
	protected NEURO neuro;

	public void stop() {
		stopAll();
	}

	public final void init(NEURO parent) throws PluginInitializationException {
		neuro = parent;
		parent.addPlugin(this);
	}

}
