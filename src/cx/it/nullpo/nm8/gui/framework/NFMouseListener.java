package cx.it.nullpo.nm8.gui.framework;

import java.io.Serializable;

/**
 * NullpoMino Framework (NF) - NFMouseListener<br>
 * Receiver of mouse event
 */
public interface NFMouseListener extends Serializable {
	/**
	 * Notification that mouse cursor was moved
	 * @param mouse NFMouse
	 * @param oldx Old X position of the mouse
	 * @param oldy Old Y position of the mouse
	 * @param newx New X position of the mouse
	 * @param newy New Y position of the mouse
	 */
	public void mouseMoved(NFMouse mouse, int oldx, int oldy, int newx, int newy);

	/**
	 * Notification that mouse cursor was dragged
	 * @param mouse NFMouse
	 * @param oldx Old X position of the mouse
	 * @param oldy Old Y position of the mouse
	 * @param newx New X position of the mouse
	 * @param newy New Y position of the mouse
	 */
	public void mouseDragged(NFMouse mouse, int oldx, int oldy, int newx, int newy);

	/**
	 * Notification that a mouse button was pressed
	 * @param mouse NFMouse
	 * @param button The index of the button. 0:Left 1:Right 2:Middle
	 * @param x The X position of the mouse when the button was pressed
	 * @param y The Y position of the mouse when the button was pressed
	 */
	public void mousePressed(NFMouse mouse, int button, int x, int y);

	/**
	 * Notification that a mouse button was released
	 * @param mouse NFMouse
	 * @param button The index of the button. 0:Left 1:Right 2:Middle
	 * @param x The X position of the mouse when the button was pressed
	 * @param y The Y position of the mouse when the button was pressed
	 */
	public void mouseReleased(NFMouse mouse, int button, int x, int y);

	/**
	 * Notification that a mouse button was clicked.
	 * Due to double click handling the single click may be delayed slightly.
	 * For absolute notification of single clicks use mousePressed().
	 * To be absolute this method should only be used when considering double clicks
	 * @param mouse NFMouse
	 * @param button The index of the button. 0:Left 1:Right 2:Middle
	 * @param x The X position of the mouse when the button was pressed
	 * @param y The Y position of the mouse when the button was pressed
	 * @param clickCount The number of times the button was clicked
	 */
	public void mouseClicked(NFMouse mouse, int button, int x, int y, int clickCount);

	/**
	 * Notification that the mouse wheel position was updated
	 * @param mouse NFMouse
	 * @param change The amount of the wheel has moved. change<0 for reverse (up) movement, change>0 for normal (down) movement.
	 */
	public void mouseWheelMoved(NFMouse mouse, int change);
}
