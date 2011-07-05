package cx.it.nullpo.nm8.gui.framework;

import java.io.Serializable;

/**
 * NullpoMino Framework (NF) - NFJoystick<br>
 * Interface for each Joystick
 */
public interface NFJoystick extends Serializable {
	/**
	 * Get a human-readable description of this joystick
	 * @return Human-readable description of this joystick (Might be null)
	 */
	public String getName();

	/**
	 * Get a human-readable description of specified axis.
	 * @param axis Axis ID
	 * @return Human-readable description of specified axis (Might be null)
	 */
	public String getAxisName(int axis);

	/**
	 * Get a human-readable description of specified button.
	 * @param button Button ID
	 * @return Human-readable description of specified button (Might be null)
	 */
	public String getButtonName(int button);

	/**
	 * Get the number of buttons
	 * @return Number of buttons
	 */
	public int getButtonCount();

	/**
	 * Get the number of axis
	 * @return Number of axis
	 */
	public int getAxisCount();

	/**
	 * Get the specific axis value
	 * @param axis Axis ID
	 * @return Axis value
	 */
	public float getAxisValue(int axis);

	/**
	 * Get the X axis value
	 * @return X axis value
	 */
	public float getXAxisValue();

	/**
	 * Get the Y axis value
	 * @return Y axis value
	 */
	public float getYAxisValue();

	/**
	 * Get X POV value
	 * @return X POV value
	 */
	public float getPovX();

	/**
	 * Get Y POV value
	 * @return Y POV value
	 */
	public float getPovY();

	/**
	 * Checks the button press status
	 * @param button Button ID
	 * @return true if the button is pressed
	 */
	public boolean isButtonPressed(int button);

	/**
	 * Poll the joystick updates
	 */
	public void poll();
}
