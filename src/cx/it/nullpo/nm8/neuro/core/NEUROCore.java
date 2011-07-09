package cx.it.nullpo.nm8.neuro.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cx.it.nullpo.nm8.gui.framework.NFGame;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFJoystick;
import cx.it.nullpo.nm8.gui.framework.NFJoystickListener;
import cx.it.nullpo.nm8.gui.framework.NFKeyListener;
import cx.it.nullpo.nm8.gui.framework.NFKeyboard;
import cx.it.nullpo.nm8.gui.framework.NFMouse;
import cx.it.nullpo.nm8.gui.framework.NFMouseListener;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.network.NMMPMessage;
import cx.it.nullpo.nm8.network.NMTPRequest;
import cx.it.nullpo.nm8.network.NMTPResponse;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.event.DebugEvent;
import cx.it.nullpo.nm8.neuro.event.JoyAxisEvent;
import cx.it.nullpo.nm8.neuro.event.JoyButtonEvent;
import cx.it.nullpo.nm8.neuro.event.JoyPOVEvent;
import cx.it.nullpo.nm8.neuro.event.JoyXYAxisEvent;
import cx.it.nullpo.nm8.neuro.event.KeyInputEvent;
import cx.it.nullpo.nm8.neuro.event.MouseButtonEvent;
import cx.it.nullpo.nm8.neuro.event.MouseClickEvent;
import cx.it.nullpo.nm8.neuro.event.MouseMoveEvent;
import cx.it.nullpo.nm8.neuro.event.MouseWheelEvent;
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
public abstract class NEUROCore implements NEURO, NFKeyListener, NFMouseListener, NFJoystickListener {
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

	/** true if the overlay is currently up and want updating. */
	protected boolean overlayUpdateFlag;

	/** true if the overlay is currently up and want rendering. */
	protected boolean overlayDrawFlag;

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
		if(sys.getJoystickManager() != null && sys.getJoystickManager().isInited()) {
			sys.getJoystickManager().addListener(this);
		}

		// Set up NEURO functions
		plugins = new HashSet<NEUROPlugin>();
		listeners = new HashMap<Class<? extends NEUROEvent>,Set<PluginListener>>();
		overlayUpdateFlag = false;
		overlayDrawFlag = false;
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
		overlayUpdateFlag = isOverlayUpdateEvent(e);
		overlayDrawFlag = isOverlayDrawEvent(e);

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

	public void update(long delta) {
		// Update the game
		game.update(sys, delta);
		// Update the overlay if applicable
		if (overlayUpdateFlag) {
			updateOverlay(delta);
		}
	}

	public void draw(NFGraphics g) {
		// Draw the game
		game.render(sys,g);
		// Draw the overlay if applicable
		if (overlayDrawFlag) {
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
				e.printStackTrace();
			} catch (Throwable e2) {}
		}
		sys.exit();
	}

	protected boolean isOverlayUpdateEvent(NEUROEvent e) {
		return false;
	}
	protected boolean isOverlayDrawEvent(NEUROEvent e) {
		return false;
	}

	protected void updateOverlay(long delta) {}
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
	public void mouseMoved(NFMouse mouse, int oldx, int oldy, int newx, int newy) {
		dispatchEvent(new MouseMoveEvent(this, mouse, false, oldx, oldy, newx, newy));
	}

	public void mouseDragged(NFMouse mouse, int oldx, int oldy, int newx, int newy) {
		dispatchEvent(new MouseMoveEvent(this, mouse, true, oldx, oldy, newx, newy));
	}

	public void mousePressed(NFMouse mouse, int button, int x, int y) {
		dispatchEvent(new MouseButtonEvent(this, mouse, button, x, y, true));
	}

	public void mouseReleased(NFMouse mouse, int button, int x, int y) {
		dispatchEvent(new MouseButtonEvent(this, mouse, button, x, y, false));
	}

	public void mouseClicked(NFMouse mouse, int button, int x, int y, int clickCount) {
		dispatchEvent(new MouseClickEvent(this, mouse, button, x, y, clickCount));
	}

	public void mouseWheelMoved(NFMouse mouse, int change) {
		dispatchEvent(new MouseWheelEvent(this, mouse, change));
	}

	// Joystick listener methods
	public void joyAxisMoved(NFJoystick joy, int axis, float oldValue, float newValue) {
		dispatchEvent(new JoyAxisEvent(this, joy, axis, oldValue, newValue));
	}

	public void joyXAxisMoved(NFJoystick joy, float oldValue, float newValue) {
		dispatchEvent(new JoyXYAxisEvent(this, joy, false, oldValue, newValue));
	}

	public void joyYAxisMoved(NFJoystick joy, float oldValue, float newValue) {
		dispatchEvent(new JoyXYAxisEvent(this, joy, true, oldValue, newValue));
	}

	public void joyPovXMoved(NFJoystick joy, float oldValue, float newValue) {
		dispatchEvent(new JoyPOVEvent(this, joy, false, oldValue, newValue));
	}

	public void joyPovYMoved(NFJoystick joy, float oldValue, float newValue) {
		dispatchEvent(new JoyPOVEvent(this, joy, true, oldValue, newValue));
	}

	public void joyButtonPressed(NFJoystick joy, int button) {
		dispatchEvent(new JoyButtonEvent(this, joy, button, true));
	}

	public void joyButtonReleased(NFJoystick joy, int button) {
		dispatchEvent(new JoyButtonEvent(this, joy, button, false));
	}
}
