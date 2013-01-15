/*
    Copyright (c) 2010, NullNoname
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * Neither the name of NullNoname nor the names of its
          contributors may be used to endorse or promote products derived from
          this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.
*/
package mu.nu.nullpo.gui.slick;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.newdawn.slick.Input;

/**
 * Joystick Related processing
 */
public class ControllerManager {
	/** Log */
	static Logger log = Logger.getLogger(ControllerManager.class);

	/** Minimum/Maximum buttoncount */
	public static final int MIN_BUTTONS = 3, MAX_BUTTONS = 100;

	/** Joystick Constant state detection methodcount */
	public static final int CONTROLLER_METHOD_NONE = 0,
							CONTROLLER_METHOD_SLICK_DEFAULT = 1,
							CONTROLLER_METHOD_SLICK_ALTERNATE = 2,
							CONTROLLER_METHOD_LWJGL = 3,
							CONTROLLER_METHOD_MAX = 4;

	/** Joystick State detection method */
	public static int method = CONTROLLER_METHOD_SLICK_DEFAULT;

	/** Joystick  state */
	public static ArrayList<Controller> controllers;

	/** EachPlayerIs usedJoystick Of number */
	public static int[] controllerID;

	/** Joystick direction key Threshold for the reaction (Detection method can not be used in some) */
	public static float[] border;

	/** Ignore analog stick */
	public static boolean[] ignoreAxis;

	/** Ignore hat switch */
	public static boolean[] ignorePOV;

	/**
	 * Initialization
	 */
	public static void initControllers() {
		controllers = new ArrayList<Controller>();
		controllerID = new int[2];
		controllerID[0] = -1;
		controllerID[1] = -1;
		border = new float[2];
		border[0] = 0f;
		border[1] = 0f;
		ignoreAxis = new boolean[2];
		ignorePOV = new boolean[2];

		for(int i = 0; i < Controllers.getControllerCount(); i++) {
			Controller c = Controllers.getController(i);

			if((c.getButtonCount() >= MIN_BUTTONS) && (c.getButtonCount() < MAX_BUTTONS))
				controllers.add(c);
		}

		log.info("Found " + controllers.size() + " controllers from NullpoMinoSlick app");

		for(int i = 0; i < controllers.size(); i++) {
			Controller c = controllers.get(i);
			log.debug("ID:" + i + ", AxisCount:" + c.getAxisCount() + ", ButtonCount:" + c.getButtonCount());
		}
	}

	/**
	 * Joystick OfcountGet the
	 * @return Joystick Ofcount
	 */
	public static int getControllerCount() {
		if(controllers == null) return 0;
		return controllers.size();
	}

	/**
	 * Joystick If you hold ontrue
	 * @param player Player number
	 * @param input InputClass (container.getInput()Can be obtained by)
	 * @return Press down on thetrue
	 */
	public static boolean isControllerUp(int player, Input input) {
		try {
			int controller = controllerID[player];

			if(controller < 0) return false;

			if(method == CONTROLLER_METHOD_SLICK_DEFAULT) {
				return input.isControllerUp(controller);
			} else if(method == CONTROLLER_METHOD_SLICK_ALTERNATE) {
				return input.isControllerUp(controller) || (!ignoreAxis[player] && (input.getAxisValue(controller, 1) < -border[player]));
			} else if(method == CONTROLLER_METHOD_LWJGL) {
				if((controller >= 0) && (controller < controllers.size())) {
					float axisValue = controllers.get(controller).getYAxisValue();
					float povValue = controllers.get(controller).getPovY();
					return (!ignoreAxis[player] && (axisValue < -border[player])) || (!ignorePOV[player] && (povValue < -border[player]));
				}
			}
		} catch (Throwable e) {
			log.debug("Exception on isControllerUp", e);
		}
		return false;
	}

	/**
	 * Joystick When I press the bottom of thetrue
	 * @param player Player number
	 * @param input InputClass (container.getInput()Can be obtained by)
	 * @return If you hold undertrue
	 */
	public static boolean isControllerDown(int player, Input input) {
		try {
			int controller = controllerID[player];

			if(controller < 0) return false;

			if(method == CONTROLLER_METHOD_SLICK_DEFAULT) {
				return input.isControllerDown(controller);
			} else if(method == CONTROLLER_METHOD_SLICK_ALTERNATE) {
				return input.isControllerDown(controller) || (!ignoreAxis[player] && (input.getAxisValue(controller, 1) > border[player]));
			} else if(method == CONTROLLER_METHOD_LWJGL) {
				if((controller >= 0) && (controller < controllers.size())) {
					float axisValue = controllers.get(controller).getYAxisValue();
					float povValue = controllers.get(controller).getPovY();
					return (!ignoreAxis[player] && (axisValue > border[player])) || (!ignorePOV[player] && (povValue > border[player]));
				}
			}
		} catch (Throwable e) {
			log.debug("Exception on isControllerDown", e);
		}
		return false;
	}

	/**
	 * Joystick When I press the lefttrue
	 * @param player Player number
	 * @param input InputClass (container.getInput()Can be obtained by)
	 * @return If you hold the lefttrue
	 */
	public static boolean isControllerLeft(int player, Input input) {
		try {
			int controller = controllerID[player];

			if(controller < 0) return false;

			if(method == CONTROLLER_METHOD_SLICK_DEFAULT) {
				return input.isControllerLeft(controller);
			} else if(method == CONTROLLER_METHOD_SLICK_ALTERNATE) {
				return input.isControllerLeft(controller) || (!ignoreAxis[player] && (input.getAxisValue(controller, 0) < -border[player]));
			} else if(method == CONTROLLER_METHOD_LWJGL) {
				if((controller >= 0) && (controller < controllers.size())) {
					float axisValue = controllers.get(controller).getXAxisValue();
					float povValue = controllers.get(controller).getPovX();
					return (!ignoreAxis[player] && (axisValue < -border[player])) || (!ignorePOV[player] && (povValue < -border[player]));
				}
			}
		} catch (Throwable e) {
			log.debug("Exception on isControllerLeft", e);
		}
		return false;
	}

	/**
	 * Joystick When I press the righttrue
	 * @param player Player number
	 * @param input InputClass (container.getInput()Can be obtained by)
	 * @return If you hold the righttrue
	 */
	public static boolean isControllerRight(int player, Input input) {
		try {
			int controller = controllerID[player];

			if(controller < 0) return false;

			if(method == CONTROLLER_METHOD_SLICK_DEFAULT) {
				return input.isControllerRight(controller);
			} else if(method == CONTROLLER_METHOD_SLICK_ALTERNATE) {
				return input.isControllerRight(controller) || (!ignoreAxis[player] && (input.getAxisValue(controller, 0) > border[player]));
			} else if(method == CONTROLLER_METHOD_LWJGL) {
				if((controller >= 0) && (controller < controllers.size())) {
					float axisValue = controllers.get(controller).getXAxisValue();
					float povValue = controllers.get(controller).getPovX();
					return (!ignoreAxis[player] && (axisValue > border[player])) || (!ignorePOV[player] && (povValue > border[player]));
				}
			}
		} catch (Throwable e) {
			log.debug("Exception on isControllerRight", e);
		}
		return false;
	}

	/**
	 * Joystick Specific buttonHas not been pressedtrue
	 * @param player Player number
	 * @param input InputClass (container.getInput()Can be obtained by)
	 * @param button Button number
	 * @return Specified buttonWith Shifttrue
	 */
	public static boolean isControllerButton(int player, Input input, int button) {
		try {
			int controller = controllerID[player];

			if(controller < 0) return false;
			if(button < 0) return false;

			if((method == CONTROLLER_METHOD_SLICK_DEFAULT) || (method == CONTROLLER_METHOD_SLICK_ALTERNATE)) {
				return input.isButtonPressed(button, controller);
			} else if(method == CONTROLLER_METHOD_LWJGL) {
				if((controller >= 0) && (controller < controllers.size())) {
					Controller c = controllers.get(controller);
					if(button < c.getButtonCount()) {
						return c.isButtonPressed(button);
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// Invalid button
		} catch (Throwable e) {
			log.debug("Exception on isControllerButton (button:" + button + ")", e);
		}
		return false;
	}
}
