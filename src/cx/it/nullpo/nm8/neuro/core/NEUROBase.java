package cx.it.nullpo.nm8.neuro.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.event.DebugEvent;
import cx.it.nullpo.nm8.neuro.event.NEUROEvent;
import cx.it.nullpo.nm8.neuro.plugin.NEUROPlugin;
import cx.it.nullpo.nm8.neuro.plugin.PluginListener;

/**
 * NEUROBase implements the event-passing functionality of NEURO along with some basic plugin loading and stopping
 * functions. 
 * @author Zircean
 *
 */
public abstract class NEUROBase implements NEURO {

	/** The set of plugins registered with NEURO. */
	protected Set<NEUROPlugin> plugins;
	/** The map containing mappings of event types to the plugin listeners registered for that event type. */
	protected Map<Class<? extends NEUROEvent>,Set<PluginListener>> listeners;
	
	/**
	 * Constructor for NEUROBase.
	 */
	public NEUROBase() {
		// Set up NEURO functions
		plugins = new HashSet<NEUROPlugin>();
		listeners = new HashMap<Class<? extends NEUROEvent>,Set<PluginListener>>();
	}
	
	public void addPlugin(NEUROPlugin p) {
		plugins.add(p);
	}
	
	public void addListener(NEUROPlugin p, Class<? extends NEUROEvent> type) {
		PluginListener pl = PluginListener.create(p,type);
		if (pl != null) {
			if (listeners.get(type) == null) {
				listeners.put(type, new HashSet<PluginListener>());
			}
			listeners.get(type).add(pl);
			dispatchEvent(new DebugEvent(this, DebugEvent.TYPE_DEBUG,
					"Successfully created plugin listener. Plugin: "+p.getName()+", type: "+type));
		} else {
			dispatchEvent(new DebugEvent(this, DebugEvent.TYPE_WARNING,
					"Failed to created plugin listener. Plugin: "+p.getName()+", type: "+type));
		}
	}

	public synchronized void dispatchEvent(NEUROEvent e) {
		if (e == null) {
			return;
		}
		// Dispatch the event
		for (PluginListener p : listeners.get(e.getClass())) {
			p.invoke(e);
		}
	}
	
	public void update(long delta) { }

	public void draw(NFGraphics g) { }
	
	/**
	 * Loads the plugin at the given classpath.
	 */
	protected void load(String classpath) {
		try {
			addPlugin(PluginLoader.load(this, classpath));
		} catch (PluginInitializationException e) {
			dispatchEvent(new DebugEvent(this,DebugEvent.TYPE_ERROR,"Failed to load plugin: "+classpath));
		}
	}

	/**
	 * Stops the given plugin.
	 */
	protected void stop(NEUROPlugin plugin) {
		dispatchEvent(new DebugEvent(this, DebugEvent.TYPE_DEBUG, "Try to stop plugin:" + plugin.getName() + " v" + plugin.getVersion()));

		// Remove the plugin from the list.
		NEUROPlugin np = null;
		for (Iterator<NEUROPlugin> it = plugins.iterator(); it.hasNext(); np = it.next()) {
			if (plugin == np) {
				it.remove();
			}
		}
		
		// Remove the listeners attached to this plugin.
		PluginListener pl = null;

		for (Set<PluginListener> ls : listeners.values()) {
			Iterator<PluginListener> it = ls.iterator();
			while(it.hasNext()) {
				pl = it.next();

				if ((pl != null) && pl.isListeningForPlugin(plugin)) {
					dispatchEvent(new DebugEvent(this, DebugEvent.TYPE_DEBUG, "Try to remove listener:" + pl.toString()));
					it.remove();
				}
			}
		}

		plugin.stop();
	}

	/**
	 * Stops all plugins
	 */
	protected void stopAll() {		
		Set<NEUROPlugin> bPlugins = new HashSet<NEUROPlugin>(plugins);

		for (NEUROPlugin p : bPlugins) {
			try {
				stop(p);
			} catch (Throwable e) {
				dispatchEvent(new DebugEvent(this, DebugEvent.TYPE_DEBUG,
						"plugin" + p.getName() + " thrown an exception during stop: " + e.toString() + " (" + e.getMessage() + ")"));
				e.printStackTrace();
			}
		}
	}
}
