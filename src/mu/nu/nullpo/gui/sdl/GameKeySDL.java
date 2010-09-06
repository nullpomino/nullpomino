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
package mu.nu.nullpo.gui.sdl;

import mu.nu.nullpo.gui.GameKeyDummy;
import mu.nu.nullpo.util.CustomProperties;
import sdljava.event.SDLKey;
import sdljava.joystick.HatState;

/**
 * Key input stateの管理
 */
public class GameKeySDL extends GameKeyDummy {
	/** Key input state (全ステート共通) */
	public static GameKeySDL gamekey[];

	/**
	 * 全ステート共通のKey input stateオブジェクトをInitialization
	 */
	public static void initGlobalGameKeySDL() {
		gamekey = new GameKeySDL[2];
		gamekey[0] = new GameKeySDL(0);
		gamekey[1] = new GameKeySDL(1);
	}

	/**
	 *  default Constructor
	 */
	public GameKeySDL() {
		super();
	}

	/**
	 * Player numberを指定できるConstructor
	 * @param pl Player number
	 */
	public GameKeySDL(int pl) {
		super(pl);
	}

	/**
	 *  button input状態を更新
	 * @param keyboard キーボードのキーが押されているかどうかの配列
	 */
	public void update(boolean[] keyboard) {
		update(keyboard, null, 0, 0, null);
	}

	/**
	 *  button input状態を更新
	 * @param keyboard キーボードのキーが押されているかどうかの配列
	 * @param joyButton Joystick の buttonが押されているかどうかの配列 (nullにしても問題なし）
	 * @param joyX Joystick のX軸の状態
	 * @param joyY Joystick のY軸の状態
	 * @param hat ハットスイッチの状態 (nullにしても問題なし）
	 */
	public void update(boolean[] keyboard, boolean[] joyButton, int joyX, int joyY, HatState hat) {
		for(int i = 0; i < MAX_BUTTON; i++) {
			if(i == BUTTON_UP) {
				// Up
				boolean flag = keyboard[keymap[i]];

				if( (flag) || (joyY < -joyBorder) || ((hat != null) && (hat.hatUp())) ) {
					inputstate[i]++;
				} else {
					inputstate[i] = 0;
				}
			} else if(i == BUTTON_DOWN) {
				// Down
				boolean flag = keyboard[keymap[i]];

				if( (flag) || (joyY > joyBorder) || ((hat != null) && (hat.hatDown())) ) {
					inputstate[i]++;
				} else {
					inputstate[i] = 0;
				}
			} else if(i == BUTTON_LEFT) {
				// 左
				boolean flag = keyboard[keymap[i]];

				if((flag) || (joyX < -joyBorder) || ((hat != null) && (hat.hatLeft())) ) {
					inputstate[i]++;
				} else {
					inputstate[i] = 0;
				}
			} else if(i == BUTTON_RIGHT) {
				// 右
				boolean flag = keyboard[keymap[i]];

				if((flag) || (joyX > joyBorder) || ((hat != null) && (hat.hatRight())) ) {
					inputstate[i]++;
				} else {
					inputstate[i] = 0;
				}
			} else {
				// その他の button
				boolean flag = keyboard[keymap[i]];
				boolean flag2 = false;

				if(joyButton != null) {
					try {
						flag2 = joyButton[buttonmap[i]];
					} catch (ArrayIndexOutOfBoundsException e) {}
				}

				if((flag) || (flag2)) {
					inputstate[i]++;
				} else {
					inputstate[i] = 0;
				}
			}
		}
	}

	/**
	 * キー設定を読み込み
	 * @param prop Property file to read from
	 */
	public void loadConfig(CustomProperties prop) {
		super.loadConfig(prop);

		keymap[BUTTON_NAV_UP] = prop.getProperty("key.p" + player + ".navigationup", SDLKey.SDLK_UP);
		keymap[BUTTON_NAV_DOWN] = prop.getProperty("key.p" + player + ".navigationdown", SDLKey.SDLK_DOWN);
		keymap[BUTTON_NAV_LEFT] = prop.getProperty("key.p" + player + ".navigationleft", SDLKey.SDLK_LEFT);
		keymap[BUTTON_NAV_RIGHT] = prop.getProperty("key.p" + player + ".navigationright", SDLKey.SDLK_RIGHT);
		keymap[BUTTON_NAV_SELECT] = prop.getProperty("key.p" + player + ".navigationselect", SDLKey.SDLK_RETURN);
		keymap[BUTTON_NAV_CANCEL] = prop.getProperty("key.p" + player + ".navigationcancel", SDLKey.SDLK_ESCAPE);

		joyBorder = prop.getProperty("joyBorder.p" + player, 0);
	}
}
