package cx.it.nullpo.nm8.gui.framework;

import java.io.Serializable;

/**
 * NullpoMino Framework (NF) - NFKeyListener<br>
 * Receiver of keyboard event
 */
public interface NFKeyListener extends Serializable {
	/**
	 * Notification that a key was pressed
	 * @param keyboard NFKeyboard for keyboard access
	 * @param key The AWT key code that was pressed
	 * @param c The character of the key that was pressed
	 */
	public void keyPressed(NFKeyboard keyboard, int key, char c);

	/**
	 * Notification that a key was released
	 * @param keyboard NFKeyboard for keyboard access
	 * @param key The AWT key code that was released
	 * @param c The character of the key that was released
	 */
	public void keyReleased(NFKeyboard keyboard, int key, char c);
}
