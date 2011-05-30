package cx.it.nullpo.nm8.neuro.plugin;

import cx.it.nullpo.nm8.neuro.core.NEURO;
import cx.it.nullpo.nm8.neuro.core.NEUROPlugin;

public abstract class AbstractPlugin implements NEUROPlugin {

	protected NEURO neuro;
	
	public AbstractPlugin(NEURO parent) {
		neuro = parent;
		parent.addPlugin(this);
	}

}
