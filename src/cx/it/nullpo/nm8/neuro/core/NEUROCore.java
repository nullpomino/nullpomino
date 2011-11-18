package cx.it.nullpo.nm8.neuro.core;

import cx.it.nullpo.nm8.gui.framework.NFGame;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFJoystick;
import cx.it.nullpo.nm8.gui.framework.NFJoystickListener;
import cx.it.nullpo.nm8.gui.framework.NFKeyListener;
import cx.it.nullpo.nm8.gui.framework.NFKeyboard;
import cx.it.nullpo.nm8.gui.framework.NFMouse;
import cx.it.nullpo.nm8.gui.framework.NFMouseListener;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.gui.niftygui.NFInputSystem;
import cx.it.nullpo.nm8.gui.niftygui.NFRenderDevice;
import cx.it.nullpo.nm8.gui.niftygui.NFSoundDevice;
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
import cx.it.nullpo.nm8.neuro.gui.DummyManager;
import cx.it.nullpo.nm8.neuro.gui.ScreenManager;
import cx.it.nullpo.nm8.neuro.network.NetworkCommunicator;
import cx.it.nullpo.nm8.neuro.plugin.NEUROPlugin;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.tools.TimeProvider;

/**
 * Implements some common functionality in all NEURO implementations, such as passing events.
 * @author Zircean
 *
 */
public abstract class NEUROCore extends NEUROBase implements NFKeyListener, NFMouseListener, NFJoystickListener {
	private static final long serialVersionUID = -2229525130691250578L;

	/** The top-level NullpoMino container this NEURO can control. */
	protected NFSystem sys;

	/** The NFGame which this NEURO gives rendering privileges to. */
	protected NFGame game;

	/** The network communicator. */
	protected NetworkCommunicator network;

	/** true if the overlay is currently up and want updating. */
	protected boolean overlayUpdateFlag;

	/** true if the overlay is currently up and want rendering. */
	protected boolean overlayDrawFlag;

	/** The GUI manager for this NEURO. */
	protected ScreenManager manager;

	/** The render device that will be used by this NEURO. */
	private NFRenderDevice renderDevice;

	/** The sound device that will be used by this NEURO. */
	private NFSoundDevice soundDevice;

	/** The input system that will be used by this NEURO. */
	private NFInputSystem inputSys;

	/** The time provider that will be used by this NEURO. */
	private TimeProvider timeProvider;

	/**
	 * Constructor for NEUROCore.
	 */
	public NEUROCore(NFSystem sys) {
		super();
		// Set up system and input handling
		this.sys = sys;
		if (sys != null) {
			sys.getKeyboard().addKeyListener(this);
			if(sys.getMouse() != null) {
				sys.getMouse().addMouseListener(this);
			}
			if(sys.getJoystickManager() != null && sys.getJoystickManager().isInited()) {
				sys.getJoystickManager().addListener(this);
			}
			inputSys = new NFInputSystem(sys.getKeyboard(),sys.getMouse());
		}

		overlayUpdateFlag = false;
		overlayDrawFlag = false;

		manager = new DummyManager();

		renderDevice = new NFRenderDevice(sys);
		soundDevice = new NFSoundDevice(sys);
		timeProvider = new TimeProvider();

	}

	@Override
	public void addPlugin(NEUROPlugin p) {
		if (p instanceof NFGame) {
			game = (NFGame) p;
		}
		p.initGUI(new Nifty(renderDevice,soundDevice,inputSys,timeProvider));
		super.addPlugin(p);
	}

	@Override
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

		super.dispatchEvent(e);
	}

	@Override
	public void update(long delta) {
		// Update the game
		game.update(sys, delta);
		// Update the overlay if applicable
		if (overlayUpdateFlag) {
			updateOverlay(delta);
		}
	}

	@Override
	public final void draw(NFGraphics g) {
		// Draw the game
		game.render(sys,g);
		// Draw the overlay if applicable
		if (overlayDrawFlag) {
			drawOverlay();
		}
		// Draw whatever else is necessary
		drawLast(g);
	}

	/**
	 * Stops all plugins and quits.
	 */
	protected void quit() {
		try {
			stopAll();
		} catch (Throwable e) {
			try {
				dispatchEvent(new DebugEvent(this, DebugEvent.TYPE_WARNING,
						"stopAll threw an exception: " + e.toString() + " (" + e.getMessage() + ")"));
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

	protected void drawOverlay() {
		manager.render();
	}

	protected abstract void drawLast(NFGraphics g);

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
