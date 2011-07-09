package cx.it.nullpo.nm8.neuro.event;

import cx.it.nullpo.nm8.gui.framework.NFJoystick;

/**
 * A class representing a joystick X or Y only axis event.
 */
public class JoyXYAxisEvent extends NEUROEvent {
	private static final long serialVersionUID = -2667021911875886328L;

	/** The joystick this event refers to */
	private NFJoystick joystick;
	/** true if Y axis, false if X axis */
	private boolean isYAxis;
	/** Old value of axis */
	private float oldValue;
	/** New value of axis */
	private float newValue;

	/**
	 * Create a new JoyXYAxisEvent
	 * @param source The event source
	 * @param joystick The joystick this event refers to
	 * @param isYAxis true if Y axis, false if X axis
	 * @param oldValue Old value of axis
	 * @param newValue New value of axis
	 */
	public JoyXYAxisEvent(Object source, NFJoystick joystick, boolean isYAxis, float oldValue, float newValue) {
		super(source);
		this.joystick = joystick;
		this.isYAxis = isYAxis;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/**
	 * Gets the NFJoystick this Event happened on.
	 */
	public NFJoystick getJoystick() {
		return joystick;
	}

	/**
	 * Returns true if this event was happened on Y axis, false if X axis
	 */
	public boolean isYAxis() {
		return isYAxis;
	}

	/**
	 * Gets the old value of axis
	 */
	public float getOldValue() {
		return oldValue;
	}

	/**
	 * Gets the new value of axis
	 */
	public float getNewValue() {
		return newValue;
	}
}
