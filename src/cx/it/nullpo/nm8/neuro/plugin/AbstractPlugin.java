package cx.it.nullpo.nm8.neuro.plugin;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.core.NEURO;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.event.NEUROEvent;

/**
 * AbstractPlugin is a parent class which implements common functionality for NEUROPlugins.
 * @author Zircean
 *
 */
public abstract class AbstractPlugin implements NEUROPlugin {

	/** The parent NEURO. */
	private NEURO neuro;

	/**
	 * Constructor for AbstractPlugin.
	 */
	public AbstractPlugin() {}

	public final void init(NEURO parent) throws PluginInitializationException {
		neuro = parent;
		parent.addPlugin(this);
		init();
	}
	
	protected abstract void init() throws PluginInitializationException;
	
	protected final void addListener(Class<? extends NEUROEvent> type) {
		neuro.addListener(this,type);
	}
	
	protected final void dispatchEvent(NEUROEvent e) {
		neuro.dispatchEvent(e);
	}

	public void draw(NFGraphics g) { }

}
