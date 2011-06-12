package cx.it.nullpo.nm8.neuro.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cx.it.nullpo.nm8.neuro.core.NEUROPlugin;
import cx.it.nullpo.nm8.neuro.event.NEUROEvent;

/**
 * PluginListener is a class which contains a plugin and a listener method, which is invoked by NEURO
 * whenever the given event type is handed to the system.
 * @author Zircean
 *
 */
public class PluginListener {

	/** The plugin which this listener corresponds to. */
	private NEUROPlugin plugin;
	/** The method this listener will invoke.*/
	private Method method;

	/**
	 * Private constructor for PluginListener. Use the factory method to create a PluginListener.
	 */
	private PluginListener(NEUROPlugin p, Method m) {
		plugin = p;
		method = m;
	}

	/**
	 * Creates a new PluginListener with the given plugin and event type.
	 * @param p the plugin to create a listener for
	 * @param type the event type the listener will catch
	 * @return a new PluginListener, or null if the PluginListener was invalid (i.e.
	 * the given plugin did not implement the required listener function)
	 */
	public static PluginListener create(NEUROPlugin p, Class<? extends NEUROEvent> type) {
		try {
			Method m = p.getClass().getMethod("receiveEvent", type);
			return new PluginListener(p,m);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	/**
	 * Returns true if the given plugin is the one this listener is listening for.
	 */
	public boolean isListeningForPlugin(NEUROPlugin p) {
		return plugin == p;
	}

	/**
	 * Invokes this Listener on the given event.
	 */
	public void invoke(NEUROEvent e) {
		try {
			method.invoke(plugin,e);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
