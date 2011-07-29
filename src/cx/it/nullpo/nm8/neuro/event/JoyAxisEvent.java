package cx.it.nullpo.nm8.neuro.event;

import cx.it.nullpo.nm8.gui.framework.NFJoystick;

/**
 * A class representing a joystick all-axis event.
 */
public class JoyAxisEvent extends InputEvent {
	private static final long serialVersionUID = 6768231515605309288L;

	/** The joystick this event refers to */
	private NFJoystick joystick;
	/** The axis ID */
	private int axis;
	/** Old value of axis */
	private float oldValue;
	/** New value of axis */
	private float newValue;

	/**
	 * Create a new JoyAxisEvent
	 * @param source The event source
	 * @param joystick The joystick this event refers to
	 * @param axis The axis ID
	 * @param oldValue Old value of axis
	 * @param newValue New value of axis
	 */
	public JoyAxisEvent(Object source, NFJoystick joystick, int axis, float oldValue, float newValue) {
		super(source);
		this.joystick = joystick;
		this.axis = axis;
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
	 * Gets the axis ID this Event happened on.
	 */
	public int getAxis() {
		return axis;
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
