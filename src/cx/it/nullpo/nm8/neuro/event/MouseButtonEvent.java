package cx.it.nullpo.nm8.neuro.event;

import cx.it.nullpo.nm8.gui.framework.NFMouse;

/**
 * A class representing a mouse button press/release event.
 */
public class MouseButtonEvent extends InputEvent {
	private static final long serialVersionUID = 6017350767075947562L;

	/** The mouse this event refers to */
	private NFMouse mouse;
	/** Mouse button ID */
	private int button;
	/** Mouse position */
	private int x, y;
	/** true if the mouse button is pressed, false if it was released */
	private boolean pressed;

	/**
	 * Create a MouseButtonEvent
	 * @param source The event source
	 * @param mouse The mouse this event refers to
	 * @param button Mouse button ID (0:Left 1:Right 2:Middle)
	 * @param x Mouse X position
	 * @param y Mouse Y position
	 * @param pressed true if the mouse button is pressed, false if it was released
	 */
	public MouseButtonEvent(Object source, NFMouse mouse, int button, int x, int y, boolean pressed) {
		super(source);
		this.mouse = mouse;
		this.button = button;
		this.x = x;
		this.y = y;
		this.pressed = pressed;
	}

	/**
	 * Gets the NFMouse this Event happened on.
	 */
	public NFMouse getMouse() {
		return mouse;
	}

	/**
	 * Gets the mouse button ID that was pressed/released
	 * @return Mouse button ID (0:Left 1:Right 2:Middle)
	 */
	public int getButton() {
		return button;
	}

	/**
	 * Gets the mouse X position
	 * @return X position
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the mouse Y position
	 * @return Y position
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns true if the Event was dispatched because the mouse button was pressed, false if it was released.
	 * @return true if the Event was dispatched because the mouse button was pressed, false if it was released.
	 */
	public boolean isPressed() {
		return pressed;
	}
}
