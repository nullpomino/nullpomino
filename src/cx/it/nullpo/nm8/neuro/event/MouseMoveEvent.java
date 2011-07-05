package cx.it.nullpo.nm8.neuro.event;

import cx.it.nullpo.nm8.gui.framework.NFMouse;

/**
 * A class representing a mouse movement event.
 */
public class MouseMoveEvent extends NEUROEvent {
	private static final long serialVersionUID = -761687066766723400L;

	/** The mouse this MouseMoveEvent refers to */
	private NFMouse mouse;
	/** true if the mouse is dragged */
	private boolean dragged;
	/** Old mouse position */
	private int oldx, oldy;
	/** New mouse position */
	private int newx, newy;

	/**
	 * Create a new MouseMoveEvent.
	 * @param source The event source
	 * @param mouse The mouse this event refers to
	 * @param dragged true if the mouse is dragged
	 * @param oldx Old X mouse position
	 * @param oldy Old Y mouse position
	 * @param newx New X mouse position
	 * @param newy New Y mouse position
	 */
	public MouseMoveEvent(Object source, NFMouse mouse, boolean dragged, int oldx, int oldy, int newx, int newy) {
		super(source);
		this.mouse = mouse;
		this.dragged = dragged;
		this.oldx = oldx;
		this.oldy = oldy;
		this.newx = newx;
		this.newy = newy;
	}

	/**
	 * Gets the NFMouse this Event happened on.
	 */
	public NFMouse getMouse() {
		return mouse;
	}

	/**
	 * Returns true if the Event was dispatched because the mouse was dragged.
	 * @return true if the Event was dispatched because the mouse was dragged.
	 */
	public boolean isDragged() {
		return dragged;
	}

	/**
	 * Get the old X mouse position.
	 * @return Old X mouse position
	 */
	public int getOldX() {
		return oldx;
	}

	/**
	 * Get the old Y mouse position.
	 * @return Old Y mouse position
	 */
	public int getOldY() {
		return oldy;
	}

	/**
	 * Get the new X mouse position.
	 * @return New X mouse position
	 */
	public int getNewX() {
		return newx;
	}

	/**
	 * Get the new Y mouse position.
	 * @return New Y mouse position
	 */
	public int getNewY() {
		return newy;
	}
}
