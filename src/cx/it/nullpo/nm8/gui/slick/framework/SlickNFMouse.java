package cx.it.nullpo.nm8.gui.slick.framework;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.newdawn.slick.Input;

import cx.it.nullpo.nm8.gui.framework.NFMouse;
import cx.it.nullpo.nm8.gui.framework.NFMouseListener;

/**
 * Slick implementation of NFMouse
 */
public class SlickNFMouse implements NFMouse {
	private static final long serialVersionUID = -1961267067902153112L;

	/** Slick native Input */
	protected Input nativeInput;

	/** Mouse Listeners */
	public List<NFMouseListener> mouseListeners = Collections.synchronizedList(new ArrayList<NFMouseListener>());

	/**
	 * Constructor
	 * @param nativeInput Slick native Input
	 */
	public SlickNFMouse(Input nativeInput) {
		this.nativeInput = nativeInput;
	}

	/**
	 * Get Slick native Input
	 */
	public Input getNativeInput() {
		return nativeInput;
	}

	/**
	 * Set Slick native input
	 */
	public void setNativeInput(Input nativeInput) {
		this.nativeInput = nativeInput;
	}

	public Point getMousePosition() {
		return new Point(nativeInput.getMouseX(), nativeInput.getMouseY());
	}

	public int getMouseX() {
		return nativeInput.getMouseX();
	}

	public int getMouseY() {
		return nativeInput.getMouseY();
	}

	public Point getAbsoluteMousePosition() {
		return MouseInfo.getPointerInfo().getLocation();
	}

	public int getAbsoluteMouseX() {
		return MouseInfo.getPointerInfo().getLocation().x;
	}

	public int getAbsoluteMouseY() {
		return MouseInfo.getPointerInfo().getLocation().y;
	}

	public boolean isLeftButtonDown() {
		return nativeInput.isMouseButtonDown(0);
	}

	public boolean isMiddleButtonDown() {
		return nativeInput.isMouseButtonDown(2);
	}

	public boolean isRightButtonDown() {
		return nativeInput.isMouseButtonDown(1);
	}

	public void addMouseListener(NFMouseListener l) {
		mouseListeners.add(l);
	}

	public boolean removeMouseListener(NFMouseListener l) {
		return mouseListeners.remove(l);
	}
}
