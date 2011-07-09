package cx.it.nullpo.nm8.neuro.event;

import cx.it.nullpo.nm8.gui.framework.NFJoystick;

/**
 * A class representing a joystick POV event.
 */
public class JoyPOVEvent extends NEUROEvent {
	private static final long serialVersionUID = 350225673331758026L;

	/** The joystick this event refers to */
	private NFJoystick joystick;
	/** true if Y POV, false if X POV */
	private boolean isYPov;
	/** Old value of axis */
	private float oldValue;
	/** New value of axis */
	private float newValue;

	/**
	 * Create a new JoyPOVEvent
	 * @param source The event source
	 * @param joystick The joystick this event refers to
	 * @param isYPov true if Y POV, false if X POV
	 * @param oldValue Old value of POV
	 * @param newValue New value of POV
	 */
	public JoyPOVEvent(Object source, NFJoystick joystick, boolean isYPov, float oldValue, float newValue) {
		super(source);
		this.joystick = joystick;
		this.isYPov = isYPov;
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
	 * Returns true if this event was happened on Y POV, false if X POV
	 */
	public boolean isYPov() {
		return isYPov;
	}

	/**
	 * Gets the old value of POV
	 */
	public float getOldValue() {
		return oldValue;
	}

	/**
	 * Gets the new value of POV
	 */
	public float getNewValue() {
		return newValue;
	}
}
