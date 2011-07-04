package cx.it.nullpo.nm8.gui.swing.framework;

import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

import cx.it.nullpo.nm8.gui.framework.NFMouse;
import cx.it.nullpo.nm8.gui.framework.NFMouseListener;

/**
 * Swing implementation of NFMouse
 */
public class SwingNFMouse implements NFMouse {
	private static final long serialVersionUID = -723833630507354301L;

	/** SwingNFSystem */
	protected SwingNFSystem sys;

	/** Game screen component */
	protected Component mainComponent;

	/** Game screen insets */
	public Insets insets;

	/** Mouse Listeners */
	public List<NFMouseListener> mouseListeners = Collections.synchronizedList(new ArrayList<NFMouseListener>());

	/** Last mouse button status */
	public boolean lastLeft, lastRight, lastMiddle;

	/**
	 * Constructor
	 * @param sys SwingNFSystem
	 * @param mainComponent Game screen component
	 */
	public SwingNFMouse(SwingNFSystem sys, Component mainComponent) {
		this.sys = sys;
		this.mainComponent = mainComponent;

		if(mainComponent instanceof Container) {
			Container c = (Container)mainComponent;
			insets = c.getInsets();
		}
	}

	/**
	 * Get NullpoMino's mouse button ID by using MouseEvent
	 * @param e MouseEvent
	 * @return NullpoMino's mouse button ID (0:Left 1:Right 2:Middle -1:Unknown)
	 */
	public int getNFMouseButtonID(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)) {
			return 0;
		} else if(SwingUtilities.isRightMouseButton(e)) {
			return 1;
		} else if(SwingUtilities.isMiddleMouseButton(e)) {
			return 2;
		}
		return -1;
	}

	/**
	 * Fix the mouse cursor Point by using Insets and window size
	 * @param p Point to fix
	 * @return Fixed Point
	 */
	public Point fixPoint(Point p) {
		Point np = new Point(p);
		if(insets != null) {
			np.x -= insets.left;
			np.y -= insets.top;
		}
		if((sys != null) && sys.isGameWindowScalingUsed()) {
			float xscale = (float)sys.getOriginalWidth() / (float)sys.getWidth();
			float yscale = (float)sys.getOriginalHeight() / (float)sys.getHeight();
			np.x = (int)(np.x * xscale);
			np.y = (int)(np.y * yscale);
		}
		return np;
	}

	public Point getMousePosition() {
		Point p = mainComponent.getMousePosition();
		if(p != null) p = fixPoint(p);
		return p;
	}

	public Point getAbsoluteMousePosition() {
		return MouseInfo.getPointerInfo().getLocation();
	}

	public boolean isLeftButtonDown() {
		return lastLeft;
	}

	public boolean isMiddleButtonDown() {
		return lastMiddle;
	}

	public boolean isRightButtonDown() {
		return lastRight;
	}

	public void addMouseListener(NFMouseListener l) {
		mouseListeners.add(l);
	}

	public boolean removeMouseListener(NFMouseListener l) {
		return mouseListeners.remove(l);
	}
}
