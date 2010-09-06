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

import mu.nu.nullpo.gui.GameKeyDummy;
import mu.nu.nullpo.util.CustomProperties;

import org.newdawn.slick.Input;

/**
 * Key input stateの管理
 */
public class GameKey extends GameKeyDummy {
	/** Key input state (全ステート共通) */
	public static GameKey[] gamekey;

	/**
	 * 全ステート共通のKey input stateオブジェクトをInitialization
	 */
	public static void initGlobalGameKey() {
		ControllerManager.initControllers();
		gamekey = new GameKey[2];
		gamekey[0] = new GameKey(0);
		gamekey[1] = new GameKey(1);
	}

	/**
	 * Default constructor
	 */
	public GameKey() {
		super();
	}

	/**
	 * Player numberを指定できるConstructor
	 * @param pl Player number
	 */
	public GameKey(int pl) {
		super(pl);
	}

	/**
	 *  button input状態を更新
	 * @param input Inputクラス (container.getInput()で取得可能）
	 */
	public void update(Input input) {
		for(int i = 0; i < MAX_BUTTON; i++) {
			boolean flag = input.isKeyDown(keymap[i]);

			switch(i) {
			case BUTTON_UP:
				flag |= ControllerManager.isControllerUp(player, input);
				break;
			case BUTTON_DOWN:
				flag |= ControllerManager.isControllerDown(player, input);
				break;
			case BUTTON_LEFT:
				flag |= ControllerManager.isControllerLeft(player, input);
				break;
			case BUTTON_RIGHT:
				flag |= ControllerManager.isControllerRight(player, input);
				break;
			default:
				flag |= ControllerManager.isControllerButton(player, input, buttonmap[i]);
				break;
			}

			if(flag){
				inputstate[i]++;
			}
			else inputstate[i] = 0;
		}
	}

	/**
	 * キー設定を読み込み
	 * @param prop Property file to read from
	 */
	public void loadConfig(CustomProperties prop) {
		super.loadConfig(prop);
		keymap[BUTTON_NAV_UP] = prop.getProperty("key.p" + player + ".navigationup", Input.KEY_UP);
		keymap[BUTTON_NAV_DOWN] = prop.getProperty("key.p" + player + ".navigationdown", Input.KEY_DOWN);
		keymap[BUTTON_NAV_LEFT] = prop.getProperty("key.p" + player + ".navigationleft", Input.KEY_LEFT);
		keymap[BUTTON_NAV_RIGHT] = prop.getProperty("key.p" + player + ".navigationright", Input.KEY_RIGHT);
		keymap[BUTTON_NAV_SELECT] = prop.getProperty("key.p" + player + ".navigationselect", Input.KEY_ENTER);
		keymap[BUTTON_NAV_CANCEL] = prop.getProperty("key.p" + player + ".navigationcancel", Input.KEY_ESCAPE);
	}
}
