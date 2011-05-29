package cx.it.nullpo.nm8.neuro.plugin;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.NEURO;
import cx.it.nullpo.nm8.neuro.NEUROPlugin;

public abstract class AbstractPlugin implements NEUROPlugin {

	protected NEURO neuro;
	
	public AbstractPlugin(NEURO parent) {
		neuro = parent;
		parent.addPlugin(this);
	}

}
