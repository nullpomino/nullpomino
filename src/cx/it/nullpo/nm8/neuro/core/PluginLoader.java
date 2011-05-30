package cx.it.nullpo.nm8.neuro.core;

import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;

public class PluginLoader {

	/**
	 * Loads a NEURO plugin.
	 * @param classpath the classpath of the plugin to load
	 * @return a NEUROPlugin on the classpath
	 * @throws PluginInitializationException if initialization of the plugin fails
	 */
	public static NEUROPlugin load(NEURO neuro, String classpath) throws PluginInitializationException {
		// Get the class (fails if no class is found or if loading the class fails)
		Class<?> cl;
		try {
			cl = Class.forName(classpath);
		} catch (ExceptionInInitializerError e) {
			throw new PluginInitializationException("Exception in initializer error: "+classpath,e);
		} catch (LinkageError e) {
			throw new PluginInitializationException("Linkage error: "+classpath,e);
		} catch (ClassNotFoundException e) {
			throw new PluginInitializationException("Could not find class: "+classpath,e);
		}
		// Get the actual plugin class (fails if the class is not a plugin class)
		Class<? extends NEUROPlugin> pluginType;
		try {
			pluginType = cl.asSubclass(NEUROPlugin.class);
		} catch (ClassCastException e) {
			throw new PluginInitializationException("Class found was not a plugin: "+classpath,e);
		}
		// Create a new plugin
		NEUROPlugin plugin = null;
		try {
			plugin = pluginType.newInstance();
		} catch (InstantiationException e) {
			throw new PluginInitializationException("Can't instantiate plugin: "+classpath,e);
		} catch (IllegalAccessException e) {
			throw new PluginInitializationException("Plugin or nullary constructor is not available: "+classpath,e);
		} catch (ExceptionInInitializerError e) {
			throw new PluginInitializationException("Exception in initializer error: "+classpath,e);
		} catch (SecurityException e) {
			throw new PluginInitializationException("Creation of plugin denied: "+classpath,e);
		}
		// Initialize the plugin
		plugin.init(neuro);
		return plugin;
	}
	
}
