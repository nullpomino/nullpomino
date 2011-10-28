package cx.it.nullpo.nm8.gui.slick.framework;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Keyboard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cx.it.nullpo.nm8.gui.framework.NFKeyListener;
import cx.it.nullpo.nm8.gui.framework.NFKeyboard;
import cx.it.nullpo.nm8.util.NUtil;

/**
 * JInput implementation of NFKeyboard
 */
public class SlickNFJInputKeyboard implements NFKeyboard {
	private static final long serialVersionUID = -6482092019156456253L;

	/** Log */
	private static Log log = LogFactory.getLog(SlickNFJInputKeyboard.class);

	/** true if JInput has inited */
	public static boolean isInited;

	/** ControllerEnvironment: Main object of JInput */
	public static ControllerEnvironment controllerEnvironment;

	/** All available controllers */
	public static Controller[] controllers;

	/** JInput keyboard */
	public static Keyboard keyboard;

	/** Keysym map */
	public static HashMap<Integer, Component.Identifier.Key> keyMap;

	/** Key down array */
	protected boolean[] keyDownFlagArray = new boolean[KeyEvent.KEY_LAST];

	/** Keyboard Listeners */
	protected List<NFKeyListener> keyListeners = Collections.synchronizedList(new ArrayList<NFKeyListener>());

	public static void initJInput(int keyboardID) {
		log.trace("Init JInput library...");
		initKeyboard(keyboardID);
		initKeyMap();
	}

	protected static void initKeyboard(int keyboardID) {
		controllerEnvironment = ControllerEnvironment.getDefaultEnvironment();
		controllers = controllerEnvironment.getControllers();

		List<Keyboard> listKeyboard = new ArrayList<Keyboard>();

		log.trace("Start finding the controllers...");
		for(int i = 0; i < controllers.length; i++) {
			Controller c = controllers[i];

			if((c.getType() == Controller.Type.KEYBOARD) && (c instanceof Keyboard)) {
				listKeyboard.add((Keyboard)c);
			}

			log.trace("Controller " + i + " : " + c.getType().toString());
		}

		if(listKeyboard.size() == 0) {
			log.error("JInput couldn't find any keyboard.");
			if(NUtil.isLinux()) {
				log.error("If you can use sudo, try the following command and start NullpoMino again:");
				log.error("sudo chmod go+r /dev/input/*");
			}
			return;
		} else if(listKeyboard.size() >= 2) {
			log.debug(listKeyboard.size() + " keyboards are found by JInput. (#0 - #" + (listKeyboard.size()-1) + ")");
		}

		if(keyboardID < 0) {
			log.trace("Preferred keyboard ID is less than 0. Last Keyboard (#" + (listKeyboard.size()-1) + ") will be used.");
			keyboard = listKeyboard.get(listKeyboard.size()-1);
		} else if(keyboardID >= listKeyboard.size()) {
			log.warn("Keyboard #" + keyboardID + " doesn't exist. " +
					 "Last Keyboard (#" + (listKeyboard.size()-1) + ") will be used instead.");
			keyboard = listKeyboard.get(listKeyboard.size()-1);
		} else {
			log.trace("Preferred keyboard #" + keyboardID + " will be used.");
			keyboard = listKeyboard.get(keyboardID);
		}
	}

	protected static void initKeyMap() {
		keyMap = new HashMap<Integer, Component.Identifier.Key>();
		keyMap.put(KeyEvent.VK_UNDEFINED, Component.Identifier.Key.VOID);
		keyMap.put(KeyEvent.VK_ESCAPE, Component.Identifier.Key.ESCAPE);
		keyMap.put(KeyEvent.VK_1, Component.Identifier.Key._1);
		keyMap.put(KeyEvent.VK_2, Component.Identifier.Key._2);
		keyMap.put(KeyEvent.VK_3, Component.Identifier.Key._3);
		keyMap.put(KeyEvent.VK_4, Component.Identifier.Key._4);
		keyMap.put(KeyEvent.VK_5, Component.Identifier.Key._5);
		keyMap.put(KeyEvent.VK_6, Component.Identifier.Key._6);
		keyMap.put(KeyEvent.VK_7, Component.Identifier.Key._7);
		keyMap.put(KeyEvent.VK_8, Component.Identifier.Key._8);
		keyMap.put(KeyEvent.VK_9, Component.Identifier.Key._9);
		keyMap.put(KeyEvent.VK_0, Component.Identifier.Key._0);
		keyMap.put(KeyEvent.VK_MINUS, Component.Identifier.Key.MINUS);
		keyMap.put(KeyEvent.VK_EQUALS, Component.Identifier.Key.EQUALS);
		keyMap.put(KeyEvent.VK_BACK_SPACE, Component.Identifier.Key.BACK);
		keyMap.put(KeyEvent.VK_TAB, Component.Identifier.Key.TAB);
		keyMap.put(KeyEvent.VK_Q, Component.Identifier.Key.Q);
		keyMap.put(KeyEvent.VK_W, Component.Identifier.Key.W);
		keyMap.put(KeyEvent.VK_E, Component.Identifier.Key.E);
		keyMap.put(KeyEvent.VK_R, Component.Identifier.Key.R);
		keyMap.put(KeyEvent.VK_T, Component.Identifier.Key.T);
		keyMap.put(KeyEvent.VK_Y, Component.Identifier.Key.Y);
		keyMap.put(KeyEvent.VK_U, Component.Identifier.Key.U);
		keyMap.put(KeyEvent.VK_I, Component.Identifier.Key.I);
		keyMap.put(KeyEvent.VK_O, Component.Identifier.Key.O);
		keyMap.put(KeyEvent.VK_P, Component.Identifier.Key.P);
		keyMap.put(KeyEvent.VK_LEFT_PARENTHESIS, Component.Identifier.Key.LBRACKET);
		keyMap.put(KeyEvent.VK_RIGHT_PARENTHESIS, Component.Identifier.Key.RBRACKET);
		keyMap.put(KeyEvent.VK_ENTER, Component.Identifier.Key.RETURN);
		keyMap.put(KeyEvent.VK_CONTROL, Component.Identifier.Key.LCONTROL);
		keyMap.put(KeyEvent.VK_A, Component.Identifier.Key.A);
		keyMap.put(KeyEvent.VK_S, Component.Identifier.Key.S);
		keyMap.put(KeyEvent.VK_D, Component.Identifier.Key.D);
		keyMap.put(KeyEvent.VK_F, Component.Identifier.Key.F);
		keyMap.put(KeyEvent.VK_G, Component.Identifier.Key.G);
		keyMap.put(KeyEvent.VK_H, Component.Identifier.Key.H);
		keyMap.put(KeyEvent.VK_J, Component.Identifier.Key.J);
		keyMap.put(KeyEvent.VK_K, Component.Identifier.Key.K);
		keyMap.put(KeyEvent.VK_L, Component.Identifier.Key.L);
		keyMap.put(KeyEvent.VK_SEMICOLON, Component.Identifier.Key.SEMICOLON);
		//keyMap.put(KeyEvent., Component.Identifier.Key.APOSTROPHE);
		keyMap.put(KeyEvent.VK_DEAD_GRAVE, Component.Identifier.Key.GRAVE);
		keyMap.put(KeyEvent.VK_SHIFT, Component.Identifier.Key.LSHIFT);
		keyMap.put(KeyEvent.VK_BACK_SLASH, Component.Identifier.Key.BACKSLASH);
		keyMap.put(KeyEvent.VK_Z, Component.Identifier.Key.Z);
		keyMap.put(KeyEvent.VK_X, Component.Identifier.Key.X);
		keyMap.put(KeyEvent.VK_C, Component.Identifier.Key.C);
		keyMap.put(KeyEvent.VK_V, Component.Identifier.Key.V);
		keyMap.put(KeyEvent.VK_B, Component.Identifier.Key.B);
		keyMap.put(KeyEvent.VK_N, Component.Identifier.Key.N);
		keyMap.put(KeyEvent.VK_M, Component.Identifier.Key.M);
		keyMap.put(KeyEvent.VK_COMMA, Component.Identifier.Key.COMMA);
		keyMap.put(KeyEvent.VK_PERIOD, Component.Identifier.Key.PERIOD);
		keyMap.put(KeyEvent.VK_SLASH, Component.Identifier.Key.SLASH);
		//keyMap.put(KeyEvent.VK_SHIFT, Component.Identifier.Key.RSHIFT);
		keyMap.put(KeyEvent.VK_MULTIPLY, Component.Identifier.Key.MULTIPLY);
		keyMap.put(KeyEvent.VK_ALT, Component.Identifier.Key.LALT);
		keyMap.put(KeyEvent.VK_SPACE, Component.Identifier.Key.SPACE);
		keyMap.put(KeyEvent.VK_CAPS_LOCK, Component.Identifier.Key.CAPITAL);
		keyMap.put(KeyEvent.VK_F1, Component.Identifier.Key.F1);
		keyMap.put(KeyEvent.VK_F2, Component.Identifier.Key.F2);
		keyMap.put(KeyEvent.VK_F3, Component.Identifier.Key.F3);
		keyMap.put(KeyEvent.VK_F4, Component.Identifier.Key.F4);
		keyMap.put(KeyEvent.VK_F5, Component.Identifier.Key.F5);
		keyMap.put(KeyEvent.VK_F6, Component.Identifier.Key.F6);
		keyMap.put(KeyEvent.VK_F7, Component.Identifier.Key.F7);
		keyMap.put(KeyEvent.VK_F8, Component.Identifier.Key.F8);
		keyMap.put(KeyEvent.VK_F9, Component.Identifier.Key.F9);
		keyMap.put(KeyEvent.VK_F10, Component.Identifier.Key.F10);
		keyMap.put(KeyEvent.VK_NUM_LOCK, Component.Identifier.Key.NUMLOCK);
		keyMap.put(KeyEvent.VK_SCROLL_LOCK, Component.Identifier.Key.SCROLL);
		keyMap.put(KeyEvent.VK_NUMPAD7, Component.Identifier.Key.NUMPAD7);
		keyMap.put(KeyEvent.VK_NUMPAD8, Component.Identifier.Key.NUMPAD8);
		keyMap.put(KeyEvent.VK_NUMPAD9, Component.Identifier.Key.NUMPAD9);
		keyMap.put(KeyEvent.VK_SUBTRACT, Component.Identifier.Key.SUBTRACT);
		keyMap.put(KeyEvent.VK_NUMPAD4, Component.Identifier.Key.NUMPAD4);
		keyMap.put(KeyEvent.VK_NUMPAD5, Component.Identifier.Key.NUMPAD5);
		keyMap.put(KeyEvent.VK_NUMPAD6, Component.Identifier.Key.NUMPAD6);
		keyMap.put(KeyEvent.VK_ADD, Component.Identifier.Key.ADD);
		keyMap.put(KeyEvent.VK_NUMPAD1, Component.Identifier.Key.NUMPAD1);
		keyMap.put(KeyEvent.VK_NUMPAD2, Component.Identifier.Key.NUMPAD2);
		keyMap.put(KeyEvent.VK_NUMPAD3, Component.Identifier.Key.NUMPAD3);
		keyMap.put(KeyEvent.VK_NUMPAD0, Component.Identifier.Key.NUMPAD0);
		keyMap.put(KeyEvent.VK_F11, Component.Identifier.Key.F11);
		keyMap.put(KeyEvent.VK_F12, Component.Identifier.Key.F12);
		keyMap.put(KeyEvent.VK_F13, Component.Identifier.Key.F13);
		keyMap.put(KeyEvent.VK_F14, Component.Identifier.Key.F14);
		keyMap.put(KeyEvent.VK_F15, Component.Identifier.Key.F15);
		keyMap.put(KeyEvent.VK_KANA, Component.Identifier.Key.KANA);
		keyMap.put(KeyEvent.VK_CONVERT, Component.Identifier.Key.CONVERT);
		keyMap.put(KeyEvent.VK_NONCONVERT, Component.Identifier.Key.NOCONVERT);
		//keyMap.put(KeyEvent., Component.Identifier.Key.YEN);
		//keyMap.put(KeyEvent., Component.Identifier.Key.NUMPADEQUAL);
		keyMap.put(KeyEvent.VK_CIRCUMFLEX, Component.Identifier.Key.CIRCUMFLEX);
		keyMap.put(KeyEvent.VK_AT, Component.Identifier.Key.AT);
		keyMap.put(KeyEvent.VK_COLON, Component.Identifier.Key.COLON);
		keyMap.put(KeyEvent.VK_UNDERSCORE, Component.Identifier.Key.UNDERLINE);
		keyMap.put(KeyEvent.VK_KANJI, Component.Identifier.Key.KANJI);
		keyMap.put(KeyEvent.VK_STOP, Component.Identifier.Key.STOP);
		//keyMap.put(KeyEvent., Component.Identifier.Key.AX);
		//keyMap.put(KeyEvent., Component.Identifier.Key.UNLABELED);
		//keyMap.put(KeyEvent., Component.Identifier.Key.NUMPADENTER);
		//keyMap.put(KeyEvent.VK_CONTROL, Component.Identifier.Key.RCONTROL);
		//keyMap.put(KeyEvent., Component.Identifier.Key.NUMPADCOMMA);
		keyMap.put(KeyEvent.VK_DIVIDE, Component.Identifier.Key.DIVIDE);
		keyMap.put(KeyEvent.VK_PRINTSCREEN, Component.Identifier.Key.SYSRQ);
		//keyMap.put(KeyEvent.VK_ALT, Component.Identifier.Key.RALT);
		keyMap.put(KeyEvent.VK_PAUSE, Component.Identifier.Key.PAUSE);
		keyMap.put(KeyEvent.VK_HOME, Component.Identifier.Key.HOME);
		keyMap.put(KeyEvent.VK_UP, Component.Identifier.Key.UP);
		keyMap.put(KeyEvent.VK_PAGE_UP, Component.Identifier.Key.PAGEUP);
		keyMap.put(KeyEvent.VK_LEFT, Component.Identifier.Key.LEFT);
		keyMap.put(KeyEvent.VK_RIGHT, Component.Identifier.Key.RIGHT);
		keyMap.put(KeyEvent.VK_END, Component.Identifier.Key.END);
		keyMap.put(KeyEvent.VK_DOWN, Component.Identifier.Key.DOWN);
		keyMap.put(KeyEvent.VK_PAGE_DOWN, Component.Identifier.Key.PAGEDOWN);
		keyMap.put(KeyEvent.VK_INSERT, Component.Identifier.Key.INSERT);
		keyMap.put(KeyEvent.VK_DELETE, Component.Identifier.Key.DELETE);
		keyMap.put(KeyEvent.VK_WINDOWS, Component.Identifier.Key.LWIN);
		//keyMap.put(KeyEvent.VK_WINDOWS, Component.Identifier.Key.RWIN);
		//keyMap.put(KeyEvent., Component.Identifier.Key.APPS);
		//keyMap.put(KeyEvent., Component.Identifier.Key.POWER);
		//keyMap.put(KeyEvent., Component.Identifier.Key.SLEEP);
	}

	public SlickNFJInputKeyboard() {
		if(!isInited) {
			initJInput(-1);
		}
	}

	public SlickNFJInputKeyboard(int keyboardID) {
		if(!isInited) {
			initJInput(keyboardID);
		}
	}

	public boolean isKeyDown(int key) {
		if(keyboard != null) {
			Component.Identifier.Key jiKey = keyMap.get(key);
			if((jiKey != null) && (jiKey != Component.Identifier.Key.VOID)) {
				return keyboard.isKeyDown(jiKey);
			}
		}
		return false;
	}

	public String getKeyName(int key) {
		return KeyEvent.getKeyText(key);
	}

	public boolean isKeySupported(int key) {
		Component.Identifier.Key jiKey = keyMap.get(key);
		if((jiKey != null) && (jiKey != Component.Identifier.Key.VOID)) {
			return true;
		}
		return false;
	}

	public void addKeyListener(NFKeyListener l) {
		keyListeners.add(l);
	}

	public boolean removeKeyListener(NFKeyListener l) {
		return keyListeners.remove(l);
	}

	public void dispatchKeyPressed(int key, char c) {
		synchronized (keyListeners) {
			Iterator<NFKeyListener> it = keyListeners.iterator();
			while(it.hasNext()) {
				//log.trace("Pressed key:" + key + " (" + getKeyName(key) + ")");
				it.next().keyPressed(this, key, c);
			}
		}
	}

	public void dispatchKeyReleased(int key, char c) {
		synchronized (keyListeners) {
			Iterator<NFKeyListener> it = keyListeners.iterator();
			while(it.hasNext()) {
				it.next().keyReleased(this, key, c);
			}
		}
	}

	public void poll() {
		if(keyboard != null) {
			keyboard.poll();

			for(int i = 0; i < KeyEvent.KEY_LAST; i++) {
				if(isKeySupported(i)) {
					boolean down = isKeyDown(i);

					if(down != keyDownFlagArray[i]) {
						if(down) {
							dispatchKeyPressed(i, (char)0);
						} else {
							dispatchKeyReleased(i, (char)0);
						}
						keyDownFlagArray[i] = down;
					}
				}
			}
		}
	}
}
