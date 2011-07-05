package cx.it.nullpo.nm8.gui.slick.framework;

import org.lwjgl.input.Controller;

import cx.it.nullpo.nm8.gui.framework.NFJoystick;

/**
 * Slick implementation of NFJoystick.
 * Because Slick doesn't provide enough informations of joysicks, we use LWJGL's API directly.
 */
public class SlickNFJoystick implements NFJoystick {
	private static final long serialVersionUID = -6518328377811828166L;

	/** LWJGL Controller */
	protected Controller ctrl;

	/**
	 * Create a new SlickNFJoystick
	 * @param ctrl LWJGL Controller
	 */
	public SlickNFJoystick(Controller ctrl) {
		this.ctrl = ctrl;
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
}
