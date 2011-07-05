package cx.it.nullpo.nm8.neuro.event;

import cx.it.nullpo.nm8.gui.framework.NFJoystick;

/**
 * A class representing a joystick button event.
 */
public class JoyButtonEvent extends NEUROEvent {
	private static final long serialVersionUID = 3329376390695100470L;

	/** The joystick this event refers to */
	private NFJoystick joystick;
	/** The button ID that was pressed/released */
	private int button;
	/** true if the button was pressed, false if it was released */
	private boolean pressed;

	/**
	 * Create a new JoyButtonEvent
	 * @param source The event source
	 * @param joystick The joystick this event refers to
	 * @param button The button ID that was pressed/released
	 * @param pressed true if the button was pressed, false if it was released
	 */
	public JoyButtonEvent(Object source, NFJoystick joystick, int button, boolean pressed) {
		super(source);
		this.joystick = joystick;
		this.button = button;
		this.pressed = pressed;
	}

	/**
	 * Gets the NFJoystick this Event happened on.
	 */
	public NFJoystick getJoystick() {
		return joystick;
	}

	/**
	 * Gets the button ID that was pressed/released
	 */
	public int getButton() {
		return button;
	}

	/**
	 * @return true if the button was pressed, false if it was released
	 */
	public boolean isPressed() {
		return pressed;
	}
}
