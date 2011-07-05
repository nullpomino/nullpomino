package cx.it.nullpo.nm8.neuro.core;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cx.it.nullpo.nm8.gui.framework.NFGame;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFKeyListener;
import cx.it.nullpo.nm8.gui.framework.NFKeyboard;
import cx.it.nullpo.nm8.gui.framework.NFMouseListener;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.network.NMMPMessage;
import cx.it.nullpo.nm8.network.NMTPRequest;
import cx.it.nullpo.nm8.network.NMTPResponse;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.event.DebugEvent;
import cx.it.nullpo.nm8.neuro.event.KeyInputEvent;
import cx.it.nullpo.nm8.neuro.event.NEUROEvent;
import cx.it.nullpo.nm8.neuro.event.QuitEvent;
import cx.it.nullpo.nm8.neuro.network.NetworkCommunicator;
import cx.it.nullpo.nm8.neuro.plugin.NEUROPlugin;
import cx.it.nullpo.nm8.neuro.plugin.PluginListener;

/**
 * Implements some common functionality in all NEURO implementations, such as passing events.
 * @author Zircean
 *
 */
public abstract class NEUROCore implements NEURO, NFKeyListener, NFMouseListener {

	private static final long serialVersionUID = -2229525130691250578L;

	/** The top-level NullpoMino container this NEURO can control. */
	protected NFSystem sys;

	/** The set of plugins registered with NEURO. */
	protected Set<NEUROPlugin> plugins;
	/** The map containing mappings of event types to the plugin listeners registered for that event type. */
	protected Map<Class<? extends NEUROEvent>,Set<PluginListener>> listeners;

	/** The NFGame which this NEURO gives rendering privileges to. */
	protected NFGame game;
	
	/** The network communicator. */
	protected NetworkCommunicator network;
	
	/** true if the overlay is currently up. */
	protected boolean overlay;

	/**
	 * Constructor for AbstractNEURO.
	 */
	public NEUROCore(NFSystem sys) {
		// Set up system and input handling
		this.sys = sys;
		sys.getKeyboard().addKeyListener(this);
		if(sys.getMouse() != null) {
			sys.getMouse().addMouseListener(this);
		}
		
		// Set up NEURO functions
		plugins = new HashSet<NEUROPlugin>();
		listeners = new HashMap<Class<? extends NEUROEvent>,Set<PluginListener>>();
		overlay = false;
	}

	public void addPlugin(NEUROPlugin p) {
		plugins.add(p);
		if (p instanceof NFGame) {
			game = (NFGame)p;
		}
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
		// Check if this event should trigger a quit
		// TODO factor out into a helper method like overlay
		if (e instanceof QuitEvent) {
			quit();
		}
		// Check if this event should trigger the overlay
		overlay = isOverlayEvent(e);
		
		// TODO NEURO should accept/block input events here if the overlay is on
		
		// Dispatch the event
		for (Class<? extends NEUROEvent> type : listeners.keySet()) {
			if (e.getClass().equals(type)) {
				for (PluginListener p : listeners.get(type)) {
					p.invoke(e);
				}
			}
		}
	}

	public void draw(NFGraphics g) {
		// Draw the game
		game.render(sys,g);
		// Draw the overlay if applicable
		if (overlay) {
			drawOverlay(g);
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
			dispatchEvent(new DebugEvent(this,DebugEvent.TYPE_ERROR,"Failed to load plugin: "+classpath));
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
					it.remove();	// FIXME: It causes IllegalStateException
				}
			}
		}
		//dispatchEvent(new DebugEvent(this, DebugEvent.TYPE_DEBUG, "listeners remain:" + listeners.size()));
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
			}
		}
	}

	/**
	 * Stops all plugins and quits.
	 */
	protected void quit() {
		try {
			stopAll();
		} catch (Throwable e) {
			try {
				dispatchEvent(new DebugEvent(this, DebugEvent.TYPE_DEBUG,
						"stopAll thrown an exception: " + e.toString() + " (" + e.getMessage() + ")"));
			} catch (Throwable e2) {}
		}
		sys.exit();
	}
	
	protected boolean isOverlayEvent(NEUROEvent e) {
		return false;
	}
	
	protected void drawOverlay(NFGraphics g) {}
	
	// Key listener methods

	public void keyPressed(NFKeyboard keyboard, int key, char c) {
		onKey(keyboard, key, c, true);
	}

	public void keyReleased(NFKeyboard keyboard, int key, char c) {
		onKey(keyboard, key, c, false);
	}
	
	private void onKey(NFKeyboard keyboard, int key, char c, boolean pressed) {
		dispatchEvent(new KeyInputEvent(this,keyboard,key,c,pressed));
	}
	
	// Mouse listener methods
	
	public void mouseMoved(Point oldPoint, Point newPoint) {
		// TODO
	}

	public void mouseDragged(Point oldPoint, Point newPoint) {
		// TODO
	}

	public void mousePressed(int button, Point point) {
		// TODO
	}

	public void mouseReleased(int button, Point point) {
		// TODO
	}

	public void mouseClicked(int button, Point point, int clickCount) {
		// TODO
	}

	public void mouseWheelMoved(int change) {
		// TODO
	}
}
