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
 * Joystick 関連の処理
 */
public class ControllerManager {
	/** Log */
	static Logger log = Logger.getLogger(ControllerManager.class);

	/** 最小/Maximum buttoncount */
	public static final int MIN_BUTTONS = 3, MAX_BUTTONS = 100;

	/** Joystick 状態検出法の定count */
	public static final int CONTROLLER_METHOD_NONE = 0,
							CONTROLLER_METHOD_SLICK_DEFAULT = 1,
							CONTROLLER_METHOD_SLICK_ALTERNATE = 2,
							CONTROLLER_METHOD_LWJGL = 3,
							CONTROLLER_METHOD_MAX = 4;

	/** Joystick 状態検出法 */
	public static int method = CONTROLLER_METHOD_SLICK_DEFAULT;

	/** Joystick の状態 */
	public static ArrayList<Controller> controllers;

	/** 各Playerが使用するJoystick の number */
	public static int[] controllerID;

	/** Joystick direction key が反応する閾値 (一部検出法では使えない) */
	public static float[] border;

	/** アナログスティック無視 */
	public static boolean[] ignoreAxis;

	/** ハットスイッチ無視 */
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
	 * Joystick のcountを取得
	 * @return Joystick のcount
	 */
	public static int getControllerCount() {
		if(controllers == null) return 0;
		return controllers.size();
	}

	/**
	 * Joystick の上を押しているとtrue
	 * @param player Player number
	 * @param input Inputクラス (container.getInput()で取得可能）
	 * @return 上を押しているとtrue
	 */
	public static boolean isControllerUp(int player, Input input) {
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
		return false;
	}

	/**
	 * Joystick の下を押しているとtrue
	 * @param player Player number
	 * @param input Inputクラス (container.getInput()で取得可能）
	 * @return 下を押しているとtrue
	 */
	public static boolean isControllerDown(int player, Input input) {
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
		return false;
	}

	/**
	 * Joystick の左を押しているとtrue
	 * @param player Player number
	 * @param input Inputクラス (container.getInput()で取得可能）
	 * @return 左を押しているとtrue
	 */
	public static boolean isControllerLeft(int player, Input input) {
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
		return false;
	}

	/**
	 * Joystick の右を押しているとtrue
	 * @param player Player number
	 * @param input Inputクラス (container.getInput()で取得可能）
	 * @return 右を押しているとtrue
	 */
	public static boolean isControllerRight(int player, Input input) {
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
		return false;
	}

	/**
	 * Joystick の特定の buttonが押されているならtrue
	 * @param player Player number
	 * @param input Inputクラス (container.getInput()で取得可能）
	 * @param button Button number
	 * @return 指定した buttonが押されているとtrue
	 */
	public static boolean isControllerButton(int player, Input input, int button) {
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

		return false;
	}
}
