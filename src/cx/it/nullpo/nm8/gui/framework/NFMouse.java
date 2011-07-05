package cx.it.nullpo.nm8.gui.framework;

import java.awt.Point;
import java.io.Serializable;

/**
 * NullpoMino Framework (NF) - NFMouse<br>
 * Interface for mouse
 */
public interface NFMouse extends Serializable {
	/**
	 * Get the current mouse position (inside the game screen)<br>
	 * It might return null if the mouse cursor is out of the window.
	 * @return Current mouse position (inside the game screen)
	 */
	public Point getMousePosition();

	/**
	 * Get the current mouse X position.<br>
	 * It might return Integer.MAX_VALUE if the mouse cursor is out of the window.
	 * @return Current mouse X position
	 */
	public int getMouseX();

	/**
	 * Get the current mouse Y position.<br>
	 * It might return Integer.MAX_VALUE if the mouse cursor is out of the window.
	 * @return Current mouse Y position
	 */
	public int getMouseY();

	/**
	 * Get the current mouse position (entire screen)
	 * @return Current mouse position (entire screen)
	 */
	public Point getAbsoluteMousePosition();

	/**
	 * Get the current mouse X position (entire screen)
	 * @return Current mouse X position (entire screen)
	 */
	public int getAbsoluteMouseX();

	/**
	 * Get the current mouse Y position (entire screen)
	 * @return Current mouse Y position (entire screen)
	 */
	public int getAbsoluteMouseY();

	/**
	 * Returns true if the left mouse button is currently down
	 * @return true if the left mouse button is currently down
	 */
	public boolean isLeftButtonDown();

	/**
	 * Returns true if the middle mouse button is currently down
	 * @return true if the middle mouse button is currently down
	 */
	public boolean isMiddleButtonDown();

	/**
	 * Returns true if the right mouse button is currently down
	 * @return true if the right mouse button is currently down
	 */
	public boolean isRightButtonDown();

	/**
	 * Add a mouse event listener
	 * @param l Mouse event listener
	 */
	public void addMouseListener(NFMouseListener l);

	/**
	 * Remove a mouse event listener
	 * @param l Mouse event listener
	 * @return True if a mouse event listener is removed
	 */
	public boolean removeMouseListener(NFMouseListener l);
}
