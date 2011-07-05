package cx.it.nullpo.nm8.gui.framework;

import java.io.Serializable;

/**
 * NullpoMino Framework (NF) - NFJoystickListener<br>
 * Receiver of joystick event
 */
public interface NFJoystickListener extends Serializable {
	/**
	 * Notification that a value of an axis was changed
	 * @param joy NFJoystick
	 * @param axis Axis ID
	 * @param oldValue Old value
	 * @param newValue New value
	 */
	public void joyAxisMoved(NFJoystick joy, int axis, float oldValue, float newValue);

	/**
	 * Notification that a value of X axis was changed
	 * @param joy NFJoystick
	 * @param oldValue Old value
	 * @param newValue New value
	 */
	public void joyXAxisMoved(NFJoystick joy, float oldValue, float newValue);

	/**
	 * Notification that a value of Y axis was changed
	 * @param joy NFJoystick
	 * @param oldValue Old value
	 * @param newValue New value
	 */
	public void joyYAxisMoved(NFJoystick joy, float oldValue, float newValue);

	/**
	 * Notification that a value of X POV was changed
	 * @param joy NFJoystick
	 * @param oldValue Old value
	 * @param newValue New value
	 */
	public void joyPovXMoved(NFJoystick joy, float oldValue, float newValue);

	/**
	 * Notification that a value of Y POV was changed
	 * @param joy NFJoystick
	 * @param oldValue Old value
	 * @param newValue New value
	 */
	public void joyPovYMoved(NFJoystick joy, float oldValue, float newValue);

	/**
	 * Notification that a button was pressed
	 * @param joy NFJoystick
	 * @param button The button ID that was pressed
	 */
	public void joyButtonPressed(NFJoystick joy, int button);

	/**
	 * Notification that a button was released
	 * @param joy NFJoystick
	 * @param button The button ID that was released
	 */
	public void joyButtonReleased(NFJoystick joy, int button);
}
