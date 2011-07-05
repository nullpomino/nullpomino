package cx.it.nullpo.nm8.neuro.event;

import cx.it.nullpo.nm8.gui.framework.NFMouse;

/**
 * A class representing a mouse wheel event.
 */
public class MouseWheelEvent extends NEUROEvent {
	private static final long serialVersionUID = 5048178996840172874L;

	/** The mouse this event refers to */
	private NFMouse mouse;
	/** Mouse wheel movement */
	private int change;

	/**
	 * Create a new MouseWheelEvent
	 * @param source The event source
	 * @param mouse The mouse this event refers to
	 * @param change Mouse wheel movement
	 */
	public MouseWheelEvent(Object source, NFMouse mouse, int change) {
		super(source);
		this.mouse = mouse;
		this.change = change;
	}

	/**
	 * Gets the NFMouse this Event happened on.
	 */
	public NFMouse getMouse() {
		return mouse;
	}

	/**
	 * Gets the amount of the wheel has moved. change<0 for reverse (up) movement, change>0 for normal (down) movement.
	 * @return Wheel movement
	 */
	public int getChange() {
		return change;
	}
}
