package mu.nu.nullpo.gui.slick;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.newdawn.slick.Input;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Keyboard;

/**
 * JInput keyboard input manager
 */
public class JInputManager {
	/** Logger */
	static Logger log = Logger.getLogger(JInputManager.class);

	/** Number of keycodes found in Slick */
	public static final int MAX_SLICK_KEY = 224;

	/** ControllerEnvironment: Main object of JInput */
	public static ControllerEnvironment controllerEnvironment;

	/** All available controllers */
	public static Controller[] controllers;

	/** JInput keyboard */
	public static Keyboard keyboard;

	/** Keysym map */
	public static HashMap<Integer, Component.Identifier.Key> keyMap;

	/**
	 * Init keysym mappings
	 */
	public static void initKeymap() {
		keyMap = new HashMap<Integer, Component.Identifier.Key>();
		keyMap.put(0, Component.Identifier.Key.VOID);	// Most likely zero
		keyMap.put(Input.KEY_ESCAPE, Component.Identifier.Key.ESCAPE);
		keyMap.put(Input.KEY_1, Component.Identifier.Key._1);
		keyMap.put(Input.KEY_2, Component.Identifier.Key._2);
		keyMap.put(Input.KEY_3, Component.Identifier.Key._3);
		keyMap.put(Input.KEY_4, Component.Identifier.Key._4);
		keyMap.put(Input.KEY_5, Component.Identifier.Key._5);
		keyMap.put(Input.KEY_6, Component.Identifier.Key._6);
		keyMap.put(Input.KEY_7, Component.Identifier.Key._7);
		keyMap.put(Input.KEY_8, Component.Identifier.Key._8);
		keyMap.put(Input.KEY_9, Component.Identifier.Key._9);
		keyMap.put(Input.KEY_0, Component.Identifier.Key._0);
		keyMap.put(Input.KEY_MINUS, Component.Identifier.Key.MINUS);
		keyMap.put(Input.KEY_EQUALS, Component.Identifier.Key.EQUALS);
		keyMap.put(Input.KEY_BACK, Component.Identifier.Key.BACK);
		keyMap.put(Input.KEY_TAB, Component.Identifier.Key.TAB);
		keyMap.put(Input.KEY_Q, Component.Identifier.Key.Q);
		keyMap.put(Input.KEY_W, Component.Identifier.Key.W);
		keyMap.put(Input.KEY_E, Component.Identifier.Key.E);
		keyMap.put(Input.KEY_R, Component.Identifier.Key.R);
		keyMap.put(Input.KEY_T, Component.Identifier.Key.T);
		keyMap.put(Input.KEY_Y, Component.Identifier.Key.Y);
		keyMap.put(Input.KEY_U, Component.Identifier.Key.U);
		keyMap.put(Input.KEY_I, Component.Identifier.Key.I);
		keyMap.put(Input.KEY_O, Component.Identifier.Key.O);
		keyMap.put(Input.KEY_P, Component.Identifier.Key.P);
		keyMap.put(Input.KEY_LBRACKET, Component.Identifier.Key.LBRACKET);
		keyMap.put(Input.KEY_RBRACKET, Component.Identifier.Key.RBRACKET);
		keyMap.put(Input.KEY_RETURN, Component.Identifier.Key.RETURN);
		keyMap.put(Input.KEY_LCONTROL, Component.Identifier.Key.LCONTROL);
		keyMap.put(Input.KEY_A, Component.Identifier.Key.A);
		keyMap.put(Input.KEY_S, Component.Identifier.Key.S);
		keyMap.put(Input.KEY_D, Component.Identifier.Key.D);
		keyMap.put(Input.KEY_F, Component.Identifier.Key.F);
		keyMap.put(Input.KEY_G, Component.Identifier.Key.G);
		keyMap.put(Input.KEY_H, Component.Identifier.Key.H);
		keyMap.put(Input.KEY_J, Component.Identifier.Key.J);
		keyMap.put(Input.KEY_K, Component.Identifier.Key.K);
		keyMap.put(Input.KEY_L, Component.Identifier.Key.L);
		keyMap.put(Input.KEY_SEMICOLON, Component.Identifier.Key.SEMICOLON);
		keyMap.put(Input.KEY_APOSTROPHE, Component.Identifier.Key.APOSTROPHE);
		keyMap.put(Input.KEY_GRAVE, Component.Identifier.Key.GRAVE);
		keyMap.put(Input.KEY_LSHIFT, Component.Identifier.Key.LSHIFT);
		keyMap.put(Input.KEY_BACKSLASH, Component.Identifier.Key.BACKSLASH);
		keyMap.put(Input.KEY_Z, Component.Identifier.Key.Z);
		keyMap.put(Input.KEY_X, Component.Identifier.Key.X);
		keyMap.put(Input.KEY_C, Component.Identifier.Key.C);
		keyMap.put(Input.KEY_V, Component.Identifier.Key.V);
		keyMap.put(Input.KEY_B, Component.Identifier.Key.B);
		keyMap.put(Input.KEY_N, Component.Identifier.Key.N);
		keyMap.put(Input.KEY_M, Component.Identifier.Key.M);
		keyMap.put(Input.KEY_COMMA, Component.Identifier.Key.COMMA);
		keyMap.put(Input.KEY_PERIOD, Component.Identifier.Key.PERIOD);
		keyMap.put(Input.KEY_SLASH, Component.Identifier.Key.SLASH);
		keyMap.put(Input.KEY_RSHIFT, Component.Identifier.Key.RSHIFT);
		keyMap.put(Input.KEY_MULTIPLY, Component.Identifier.Key.MULTIPLY);
		keyMap.put(Input.KEY_LALT, Component.Identifier.Key.LALT);
		keyMap.put(Input.KEY_SPACE, Component.Identifier.Key.SPACE);
		keyMap.put(Input.KEY_CAPITAL, Component.Identifier.Key.CAPITAL);
		keyMap.put(Input.KEY_F1, Component.Identifier.Key.F1);
		keyMap.put(Input.KEY_F2, Component.Identifier.Key.F2);
		keyMap.put(Input.KEY_F3, Component.Identifier.Key.F3);
		keyMap.put(Input.KEY_F4, Component.Identifier.Key.F4);
		keyMap.put(Input.KEY_F5, Component.Identifier.Key.F5);
		keyMap.put(Input.KEY_F6, Component.Identifier.Key.F6);
		keyMap.put(Input.KEY_F7, Component.Identifier.Key.F7);
		keyMap.put(Input.KEY_F8, Component.Identifier.Key.F8);
		keyMap.put(Input.KEY_F9, Component.Identifier.Key.F9);
		keyMap.put(Input.KEY_F10, Component.Identifier.Key.F10);
		keyMap.put(Input.KEY_NUMLOCK, Component.Identifier.Key.NUMLOCK);
		keyMap.put(Input.KEY_SCROLL, Component.Identifier.Key.SCROLL);
		keyMap.put(Input.KEY_NUMPAD7, Component.Identifier.Key.NUMPAD7);
		keyMap.put(Input.KEY_NUMPAD8, Component.Identifier.Key.NUMPAD8);
		keyMap.put(Input.KEY_NUMPAD9, Component.Identifier.Key.NUMPAD9);
		keyMap.put(Input.KEY_SUBTRACT, Component.Identifier.Key.SUBTRACT);
		keyMap.put(Input.KEY_NUMPAD4, Component.Identifier.Key.NUMPAD4);
		keyMap.put(Input.KEY_NUMPAD5, Component.Identifier.Key.NUMPAD5);
		keyMap.put(Input.KEY_NUMPAD6, Component.Identifier.Key.NUMPAD6);
		keyMap.put(Input.KEY_ADD, Component.Identifier.Key.ADD);
		keyMap.put(Input.KEY_NUMPAD1, Component.Identifier.Key.NUMPAD1);
		keyMap.put(Input.KEY_NUMPAD2, Component.Identifier.Key.NUMPAD2);
		keyMap.put(Input.KEY_NUMPAD3, Component.Identifier.Key.NUMPAD3);
		keyMap.put(Input.KEY_NUMPAD0, Component.Identifier.Key.NUMPAD0);
		keyMap.put(Input.KEY_F11, Component.Identifier.Key.F11);
		keyMap.put(Input.KEY_F12, Component.Identifier.Key.F12);
		keyMap.put(Input.KEY_F13, Component.Identifier.Key.F13);
		keyMap.put(Input.KEY_F14, Component.Identifier.Key.F14);
		keyMap.put(Input.KEY_F15, Component.Identifier.Key.F15);
		keyMap.put(Input.KEY_KANA, Component.Identifier.Key.KANA);
		keyMap.put(Input.KEY_CONVERT, Component.Identifier.Key.CONVERT);
		keyMap.put(Input.KEY_NOCONVERT, Component.Identifier.Key.NOCONVERT);
		keyMap.put(Input.KEY_YEN, Component.Identifier.Key.YEN);
		keyMap.put(Input.KEY_NUMPADEQUALS, Component.Identifier.Key.NUMPADEQUAL);	// Different name
		keyMap.put(Input.KEY_CIRCUMFLEX, Component.Identifier.Key.CIRCUMFLEX);
		keyMap.put(Input.KEY_AT, Component.Identifier.Key.AT);
		keyMap.put(Input.KEY_COLON, Component.Identifier.Key.COLON);
		keyMap.put(Input.KEY_UNDERLINE, Component.Identifier.Key.UNDERLINE);
		keyMap.put(Input.KEY_KANJI, Component.Identifier.Key.KANJI);
		keyMap.put(Input.KEY_STOP, Component.Identifier.Key.STOP);
		keyMap.put(Input.KEY_AX, Component.Identifier.Key.AX);
		keyMap.put(Input.KEY_UNLABELED, Component.Identifier.Key.UNLABELED);
		keyMap.put(Input.KEY_NUMPADENTER, Component.Identifier.Key.NUMPADENTER);
		keyMap.put(Input.KEY_RCONTROL, Component.Identifier.Key.RCONTROL);
		keyMap.put(Input.KEY_NUMPADCOMMA, Component.Identifier.Key.NUMPADCOMMA);
		keyMap.put(Input.KEY_DIVIDE, Component.Identifier.Key.DIVIDE);
		keyMap.put(Input.KEY_SYSRQ, Component.Identifier.Key.SYSRQ);
		keyMap.put(Input.KEY_RALT, Component.Identifier.Key.RALT);
		keyMap.put(Input.KEY_PAUSE, Component.Identifier.Key.PAUSE);
		keyMap.put(Input.KEY_HOME, Component.Identifier.Key.HOME);
		keyMap.put(Input.KEY_UP, Component.Identifier.Key.UP);
		keyMap.put(Input.KEY_PRIOR, Component.Identifier.Key.PAGEUP);	// Different name
		keyMap.put(Input.KEY_LEFT, Component.Identifier.Key.LEFT);
		keyMap.put(Input.KEY_RIGHT, Component.Identifier.Key.RIGHT);
		keyMap.put(Input.KEY_END, Component.Identifier.Key.END);
		keyMap.put(Input.KEY_DOWN, Component.Identifier.Key.DOWN);
		keyMap.put(Input.KEY_NEXT, Component.Identifier.Key.PAGEDOWN);	// Different name
		keyMap.put(Input.KEY_INSERT, Component.Identifier.Key.INSERT);
		keyMap.put(Input.KEY_DELETE, Component.Identifier.Key.DELETE);
		keyMap.put(Input.KEY_LWIN, Component.Identifier.Key.LWIN);
		keyMap.put(Input.KEY_RWIN, Component.Identifier.Key.RWIN);
		keyMap.put(Input.KEY_APPS, Component.Identifier.Key.APPS);
		keyMap.put(Input.KEY_POWER, Component.Identifier.Key.POWER);
		keyMap.put(Input.KEY_SLEEP, Component.Identifier.Key.SLEEP);
		//keyMap.put(0, Component.Identifier.Key.KEY_UNKNOWN);	// Most likely zero
	}

	/**
	 * Convert Slick's keycode to JInput's key identifier.
	 * @param key Slick's keycode
	 * @return JInput's key identifier
	 */
	public static Component.Identifier.Key slickToJInputKey(int key) {
		return keyMap.get(key);
	}

	/**
	 * Init
	 */
	public static void initKeyboard() {
		controllerEnvironment = ControllerEnvironment.getDefaultEnvironment();
		controllers = controllerEnvironment.getControllers();

		for(int i = 0; i < controllers.length; i++) {
			Controller c = controllers[i];

			if((c.getType() == Controller.Type.KEYBOARD) && (c instanceof Keyboard)) {
				log.debug("initKeyboard: Keyboard found");
				keyboard = (Keyboard)c;
			}
		}

		if(keyboard == null) {
			log.warn("initKeyboard: Keyboard NOT FOUND");
		}
	}

	/**
	 * Polls keyboard input.
	 */
	public static void poll() {
		if(keyboard != null) {
			keyboard.poll();
		}
	}

	/**
	 * Check if specific key is down or not.
	 * @param key SLICK keycode
	 * @return true if the key is down
	 */
	public static boolean isKeyDown(int key) {
		if(keyboard != null) {
			Component.Identifier.Key jinputKey = keyMap.get(key);
			return keyboard.isKeyDown(jinputKey);
		}
		return false;
	}
}
