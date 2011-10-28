package cx.it.nullpo.nm8.gui.swing.framework;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cx.it.nullpo.nm8.gui.framework.NFKeyListener;
import cx.it.nullpo.nm8.gui.framework.NFKeyboard;

/**
 * Swing implementation of NFKeyboard
 */
public class SwingNFKeyboard implements NFKeyboard {
	private static final long serialVersionUID = -999067824381394737L;

	/** Number of keys */
	protected final int MAX_KEYS = 0x10000;

	/** Key down status */
	protected boolean[] keyDown;

	/** Keyboard Listeners */
	protected List<NFKeyListener> keyListeners = Collections.synchronizedList(new ArrayList<NFKeyListener>());

	/**
	 * Constructor
	 */
	public SwingNFKeyboard() {
		keyDown = new boolean[MAX_KEYS];
	}

	/**
	 * Set key down status
	 * @param key Keycode
	 * @param isDown true if the key is down
	 */
	public void setKeyDown(int key, boolean isDown) {
		if((key < 0) || (key >= MAX_KEYS)) return;
		keyDown[key] = isDown;
	}

	public boolean isKeyDown(int key) {
		if((key < 0) || (key >= MAX_KEYS)) return false;
		return keyDown[key];
	}

	/**
	 * Check if a particular AWT keycode is supported.
	 * Since it uses Swing, every AWT keycodes can be detected.
	 * @return Always true
	 */
	public boolean isKeySupported(int key) {
		return true;
	}

	public String getKeyName(int key) {
		return KeyEvent.getKeyText(key);
	}

	public void addKeyListener(NFKeyListener l) {
		keyListeners.add(l);
	}

	public boolean removeKeyListener(NFKeyListener l) {
		return keyListeners.remove(l);
	}

	public void dispatchKeyPressed(int key, char c) {
		int awtkey = key;
		synchronized (keyListeners) {
			Iterator<NFKeyListener> it = keyListeners.iterator();
			while(it.hasNext()) {
				it.next().keyPressed(this, awtkey, c);
			}
		}
	}

	public void dispatchKeyReleased(int key, char c) {
		int awtkey = key;
		synchronized (keyListeners) {
			Iterator<NFKeyListener> it = keyListeners.iterator();
			while(it.hasNext()) {
				it.next().keyReleased(this, awtkey, c);
			}
		}
	}

	public void poll() {
	}
}
