package cx.it.nullpo.nm8.neuro.event;

import cx.it.nullpo.nm8.gui.framework.NFMouse;

/**
 * A class representing a mouse click event.
 */
public class MouseClickEvent extends InputEvent {
	private static final long serialVersionUID = 763549717991724055L;

	/** The mouse this event refers to */
	private NFMouse mouse;
	/** Mouse button ID */
	private int button;
	/** Mouse position */
	private int x, y;
	/** Button click count */
	private int clickCount;

	/**
	 * Create a new MouseClickEvent
	 * @param source The event source
	 * @param mouse The mouse this event refers to
	 * @param button Mouse button ID (0:Left 1:Right 2:Middle)
	 * @param x Mouse X position
	 * @param y Mouse Y position
	 * @param clickCount Button click count
	 */
	public MouseClickEvent(Object source, NFMouse mouse, int button, int x, int y, int clickCount) {
		super(source);
		this.mouse = mouse;
		this.button = button;
		this.x = x;
		this.y = y;
		this.clickCount = clickCount;
	}

	/**
	 * Gets the NFMouse this Event happened on.
	 */
	public NFMouse getMouse() {
		return mouse;
	}

	/**
	 * Gets the mouse button ID that was clicked
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
	 * Gets the number of button clicks (2 is double-click)
	 * @return The number of button clicks (2 is double-click)
	 */
	public int getClickCount() {
		return clickCount;
	}
}
