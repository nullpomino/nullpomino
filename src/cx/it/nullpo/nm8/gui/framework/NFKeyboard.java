package cx.it.nullpo.nm8.gui.framework;

import java.io.Serializable;

/**
 * NullpoMino Framework (NF) - NFKeyboard<br>
 * Interface for keyboard
 */
public interface NFKeyboard extends Serializable {
	/**
	 * Check if a particular key is down
	 * @param key The AWT key code of the key to check
	 * @return True if the key is down
	 */
	public boolean isKeyDown(int key);

	/**
	 * Check if a particular AWT keycode is supported
	 * @param key The AWT key code of the key to check
	 * @return True if the key is supported (detectable) by this system
	 */
	public boolean isKeySupported(int key);

	/**
	 * Get the character representation of the key identified by the specified code
	 * @param key The key code of the key to retrieve the name of
	 * @return The name or character representation of the key requested
	 */
	public String getKeyName(int key);

	/**
	 * Add a key event listener
	 * @param l Key event listener
	 */
	public void addKeyListener(NFKeyListener l);

	/**
	 * Remove a key event listener
	 * @param l Key event listener
	 * @return True if a key event listener is removed
	 */
	public boolean removeKeyListener(NFKeyListener l);

	/**
	 * Send keyPressed event to all listeners
	 * @param key The key code that was pressed
	 * @param c The character of the key that was pressed
	 */
	public void dispatchKeyPressed(int key, char c);

	/**
	 * Send keyReleased event to all listeners
	 * @param key The key code that was released
	 * @param c The character of the key that was released
	 */
	public void dispatchKeyReleased(int key, char c);

	/**
	 * Poll the input. It will be called every frame.
	 */
	public void poll();
}
