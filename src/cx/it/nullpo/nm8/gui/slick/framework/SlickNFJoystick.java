package cx.it.nullpo.nm8.gui.slick.framework;

import java.util.Iterator;

import org.lwjgl.input.Controller;

import cx.it.nullpo.nm8.gui.framework.NFJoystick;
import cx.it.nullpo.nm8.gui.framework.NFJoystickListener;
import cx.it.nullpo.nm8.gui.framework.NFJoystickManager;

/**
 * Slick implementation of NFJoystick.
 * Because Slick doesn't provide enough informations of joysicks, we use LWJGL's API directly.
 */
public class SlickNFJoystick implements NFJoystick {
	private static final long serialVersionUID = -6518328377811828166L;

	/** Joystick Manager */
	protected SlickNFJoystckManager joyManager;

	/** Joystick ID */
	protected int id;

	/** LWJGL Controller */
	protected Controller ctrl;

	/** Becomes true if the first update is done */
	protected boolean firstUpdateDone;

	/** Button status array */
	protected boolean[] buttonStatusArray;

	/** Axis value array */
	protected float[] axisValueArray;

	/** Axis X value */
	protected float axisXValue;

	/** Axis Y value */
	protected float axisYValue;

	/** POV X value */
	protected float povXValue;

	/** POV Y value */
	protected float povYValue;

	/**
	 * Create a new SlickNFJoystick
	 * @param ctrl LWJGL Controller
	 */
	public SlickNFJoystick(SlickNFJoystckManager joyManager, int id, Controller ctrl) {
		this.joyManager = joyManager;
		this.id = id;
		this.ctrl = ctrl;
	}

	public NFJoystickManager getJoystickManager() {
		return joyManager;
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return ctrl.getName();
	}

	public String getAxisName(int axis) {
		return ctrl.getAxisName(axis);
	}

	public String getButtonName(int button) {
		return ctrl.getButtonName(button);
	}

	public int getButtonCount() {
		return ctrl.getButtonCount();
	}

	public int getAxisCount() {
		return ctrl.getAxisCount();
	}

	public float getAxisValue(int axis) {
		return ctrl.getAxisValue(axis);
	}

	public float getXAxisValue() {
		return ctrl.getXAxisValue();
	}

	public float getYAxisValue() {
		return ctrl.getYAxisValue();
	}

	public float getPovX() {
		return ctrl.getPovX();
	}

	public float getPovY() {
		return ctrl.getPovY();
	}

	public boolean isButtonPressed(int button) {
		return ctrl.isButtonPressed(button);
	}

	public void poll() {
		ctrl.poll();
	}

	public void updateAndDispatchEvents() {
		if(!firstUpdateDone) {
			buttonStatusArray = new boolean[getButtonCount()];
			axisValueArray = new float[getAxisCount()];
			axisXValue = 0;
			axisYValue = 0;
			povXValue = 0;
			povYValue = 0;
			firstUpdateDone = true;
		} else {
			// Button update
			for(int i = 0; i < buttonStatusArray.length; i++) {
				boolean pressed = isButtonPressed(i);
				if(pressed != buttonStatusArray[i]) {
					if(pressed) {
						synchronized (joyManager.listeners) {
							Iterator<NFJoystickListener> it = joyManager.listeners.iterator();
							while(it.hasNext()) {
								it.next().joyButtonPressed(this, i);
							}
						}
					} else {
						synchronized (joyManager.listeners) {
							Iterator<NFJoystickListener> it = joyManager.listeners.iterator();
							while(it.hasNext()) {
								it.next().joyButtonReleased(this, i);
							}
						}
					}
					buttonStatusArray[i] = pressed;
				}
			}

			// Axis update
			for(int i = 0; i < axisValueArray.length; i++) {
				float newValue = getAxisValue(i);
				if(newValue != axisValueArray[i]) {
					synchronized (joyManager.listeners) {
						Iterator<NFJoystickListener> it = joyManager.listeners.iterator();
						while(it.hasNext()) {
							it.next().joyAxisMoved(this, i, axisValueArray[i], newValue);
						}
					}
					axisValueArray[i] = newValue;
				}
			}

			// Axis X update
			float newXAxis = getXAxisValue();
			if(newXAxis != axisXValue) {
				synchronized (joyManager.listeners) {
					Iterator<NFJoystickListener> it = joyManager.listeners.iterator();
					while(it.hasNext()) {
						it.next().joyXAxisMoved(this, axisXValue, newXAxis);
					}
				}
				axisXValue = newXAxis;
			}

			// Axis Y update
			float newYAxis = getYAxisValue();
			if(newYAxis != axisYValue) {
				synchronized (joyManager.listeners) {
					Iterator<NFJoystickListener> it = joyManager.listeners.iterator();
					while(it.hasNext()) {
						it.next().joyYAxisMoved(this, axisYValue, newYAxis);
					}
				}
				axisYValue = newYAxis;
			}

			// POV X update
			float newPovX = getPovX();
			if(newPovX != povXValue) {
				synchronized (joyManager.listeners) {
					Iterator<NFJoystickListener> it = joyManager.listeners.iterator();
					while(it.hasNext()) {
						it.next().joyPovXMoved(this, povXValue, newPovX);
					}
				}
			}

			// POV Y update
			float newPovY = getPovY();
			if(newPovY != povYValue) {
				synchronized (joyManager.listeners) {
					Iterator<NFJoystickListener> it = joyManager.listeners.iterator();
					while(it.hasNext()) {
						it.next().joyPovYMoved(this, povYValue, newPovY);
					}
				}
			}
		}
	}
}
