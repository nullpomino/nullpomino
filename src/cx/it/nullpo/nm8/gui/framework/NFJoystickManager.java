package cx.it.nullpo.nm8.gui.framework;

import java.io.Serializable;

/**
 * NullpoMino Framework (NF) - NFJoystickManager<br>
 * Interface for Joystick Manager
 */
public interface NFJoystickManager extends Serializable {
	/**
	 * @return true if already inited
	 */
	public boolean isInited();

	/**
	 * Init the joystick manager.
	 * Because of the crash-prone nature of joysticks,
	 * you have to call this manually in order to find and use the joysticks.
	 * @return Number of joysticks found
	 */
	public int initJoystick();

	/**
	 * Get the number of joysticks
	 * @return Number of joysticks
	 */
	public int getJoystickCount();

	/**
	 * Get the specific joystick
	 * @param id Joystick ID
	 * @return NFJoystick
	 */
	public NFJoystick getJoystick(int id);

	/**
	 * @return true if polling is automatic
	 */
	public boolean isAutoPoll();

	/**
	 * Poll the joystick updates
	 */
	public void poll();
}
