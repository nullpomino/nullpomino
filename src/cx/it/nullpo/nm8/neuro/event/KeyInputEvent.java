package cx.it.nullpo.nm8.neuro.event;

import cx.it.nullpo.nm8.gui.framework.NFKeyboard;

/**
 * A class representing a keyboard input event.
 * @author Zircean
 *
 */
public class KeyInputEvent extends NEUROEvent {
	
	private static final long serialVersionUID = -555528077026852959L;
	
	/** The keyboard this KeyInputEvent refers to. */
	private NFKeyboard keyboard;
	/** The key code for the key this Event refers to. */
	private int key;
	/** The character this Event represents. */
	private char c;
	/** true if the key was pressed, false if it was released. */
	private boolean pressed;

	public KeyInputEvent(Object source, NFKeyboard keyboard, int key, char c, boolean pressed) {
		super(source);
		this.keyboard = keyboard;
		this.key = key;
		this.c = c;
		this.pressed = pressed;
	}
	
	/**
	 * Gets the NFKeyboard this Event happened on.
	 */
	public NFKeyboard getKeyboard() {
		return keyboard;
	}
	
	/**
	 * Gets the key code held by this Event.
	 */
	public int getKey() {
		return key;
	}
	
	/**
	 * Gets the character of the key this Event refers to.
	 */
	public char getChar() {
		return c;
	}
	
	/**
	 * Returns true if the Event was dispatched because the key was pressed, false if it was released.
	 */
	public boolean getPressed() {
		return pressed;
	}

}
