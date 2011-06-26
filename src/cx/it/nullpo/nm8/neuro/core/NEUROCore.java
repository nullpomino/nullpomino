package cx.it.nullpo.nm8.neuro.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cx.it.nullpo.nm8.network.NMMPMessage;
import cx.it.nullpo.nm8.network.NMTPRequest;
import cx.it.nullpo.nm8.network.NMTPResponse;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.event.DebugEvent;
import cx.it.nullpo.nm8.neuro.event.NEUROEvent;
import cx.it.nullpo.nm8.neuro.network.NetworkCommunicator;
import cx.it.nullpo.nm8.neuro.plugin.NEUROPlugin;
import cx.it.nullpo.nm8.neuro.plugin.PluginListener;

/**
 * Implements some common functionality in all NEURO implementations, such as passing events.
 * @author Zircean
 *
 */
public abstract class NEUROCore implements NEURO {

	/** The set of plugins registered with NEURO. */
	protected Set<NEUROPlugin> plugins;
	/** The map containing mappings of event types to the plugin listeners registered for that event type. */
	protected Map<Class<? extends NEUROEvent>,Set<PluginListener>> listeners;
	
	/** The network communicator. */
	protected NetworkCommunicator network;

	/**
	 * Constructor for AbstractNEURO.
	 */
	public NEUROCore() {
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
		}
		dispatchEvent(new DebugEvent(this, "Successfully created plugin listener for plugin: "+p.getName()+
				" for type: "+type));
	}

	public void dispatchEvent(NEUROEvent e) {
		for (Class<? extends NEUROEvent> type : listeners.keySet()) {
			if (e.getClass().equals(type)) {
				for (PluginListener p : listeners.get(type)) {
					p.invoke(e);
				}
			}
		}
	}
	
	public NMTPResponse send(NMTPRequest req) {
		return network.send(req);
	}
	
	public void send(NMMPMessage message) {
		network.send(message);
	}

	/**
	 * Loads the plugin at the given classpath.
	 */
	protected void load(String classpath) {
		try {
			PluginLoader.load(this, classpath);
		} catch (PluginInitializationException e) {
			// TODO handle this error
		}
	}

	/**
	 * Stops the given plugin.
	 */
	protected void stop(NEUROPlugin plugin) {
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
			for (Iterator<PluginListener> it = ls.iterator(); it.hasNext(); pl = it.next()) {
				if ((pl != null) && pl.isListeningForPlugin(plugin)) {
					it.remove();
				}
			}
		}
		plugin.stop();
	}

	/**
	 * Stops all plugins.
	 */
	protected void stopAll() {
		for (NEUROPlugin p : plugins) {
			stop(p);
		}
	}
}
