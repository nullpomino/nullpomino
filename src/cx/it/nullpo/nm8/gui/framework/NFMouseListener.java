package cx.it.nullpo.nm8.gui.framework;

import java.awt.Point;
import java.io.Serializable;

/**
 * NullpoMino Framework (NF) - NFMouseListener<br>
 * Receiver of mouse event
 */
public interface NFMouseListener extends Serializable {
	/**
	 * Notification that mouse cursor was moved
	 * @param oldPoint The old position of the mouse
	 * @param newPoint The new position of the mouse
	 */
	public void mouseMoved(Point oldPoint, Point newPoint);

	/**
	 * Notification that mouse cursor was dragged
	 * @param oldPoint The old position of the mouse
	 * @param newPoint The new position of the mouse
	 */
	public void mouseDragged(Point oldPoint, Point newPoint);

	/**
	 * Notification that a mouse button was pressed
	 * @param button The index of the button. 0:Left 1:Right 2:Middle
	 * @param point The position of the mouse when the button was pressed
	 */
	public void mousePressed(int button, Point point);

	/**
	 * Notification that a mouse button was released
	 * @param button The index of the button. 0:Left 1:Right 2:Middle
	 * @param point The position of the mouse when the button was released
	 */
	public void mouseReleased(int button, Point point);

	/**
	 * Notification that a mouse button was clicked.
	 * Due to double click handling the single click may be delayed slightly.
	 * For absolute notification of single clicks use mousePressed().
	 * To be absolute this method should only be used when considering double clicks
	 * @param button The index of the button. 0:Left 1:Right 2:Middle
	 * @param point The position of the mouse when the button was clicked
	 * @param clickCount The number of times the button was clicked
	 */
	public void mouseClicked(int button, Point point, int clickCount);

	/**
	 * Notification that the mouse wheel position was updated
	 * @param change The amount of the wheel has moved. change<0 for reverse (up) movement, change>0 for normal (down) movement.
	 */
	public void mouseWheelMoved(int change);
}
