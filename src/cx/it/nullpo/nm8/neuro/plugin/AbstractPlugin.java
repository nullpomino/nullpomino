package cx.it.nullpo.nm8.neuro.plugin;

import cx.it.nullpo.nm8.neuro.core.NEURO;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.event.NEUROEvent;
import cx.it.nullpo.nm8.neuro.gui.ScreenHolder;
import de.lessvoid.nifty.Nifty;

/**
 * AbstractPlugin is a parent class which implements common functionality for NEUROPlugins.
 * @author Zircean
 *
 */
public abstract class AbstractPlugin implements NEUROPlugin, ScreenHolder {

	/** The parent NEURO. */
	private NEURO neuro;
	
	/** The Nifty instance that draws this GUI. */
	protected Nifty gui;

	/**
	 * Constructor for AbstractPlugin.
	 */
	public AbstractPlugin() {}

	public final void init(NEURO parent) throws PluginInitializationException {
		neuro = parent;
		parent.addPlugin(this);
		gui = null;
		init();
	}
	
	protected abstract void init() throws PluginInitializationException;
	
	protected final void addListener(Class<? extends NEUROEvent> type) {
		neuro.addListener(this,type);
	}
	
	protected final void dispatchEvent(NEUROEvent e) {
		neuro.dispatchEvent(e);
	}
	
	public void initGUI(Nifty n) {
		gui = n;
	}

	public Nifty getGUI() { 
		return gui; 
	}

}
