package cx.it.nullpo.nm8.neuro.core;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cx.it.nullpo.nm8.neuro.event.NEUROEvent;
import cx.it.nullpo.nm8.neuro.plugin.PluginListener;

/**
 * Implements some common functionality in all NEURO implementations, such as passing events.
 * @author Zircean
 *
 */
public abstract class AbstractNEURO implements NEURO {
	
	/** The set of plugins registered with NEURO. */
	protected Set<NEUROPlugin> registeredPlugins;
	/** The map containing mappings of event types to the plugin listeners registered for that event type. */
	protected Map<Class<? extends NEUROEvent>,Set<PluginListener>> listeners;

	@Override
	public void addPlugin(NEUROPlugin p) {
		registeredPlugins.add(p);
	}
	
	@Override
	public void addListener(NEUROPlugin p, Class<? extends NEUROEvent> type) {
		PluginListener pl = PluginListener.create(p,type);
		if (pl != null) {
			if (listeners.get(type) == null) {
				listeners.put(type, new HashSet<PluginListener>());
			}
			listeners.get(type).add(PluginListener.create(p,type)); 
		}
	}
	
	@Override
	public void dispatchEvent(NEUROEvent e) {
		for (Class<? extends NEUROEvent> type : listeners.keySet()) {
			if (e.getClass().equals(type)) {
				for (PluginListener p : listeners.get(type)) {
					p.invoke(e);
				}
			}
		}
	}

}
