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

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.util.CustomProperties;

import sdljava.joystick.HatState;

/**
 * キー入力状態の管理
 */
public class GameKeySDL {
	/** キー入力状態（全ステート共通） */
	public static GameKeySDL gamekey[];

	/** ボタン number定数 */
	public static final int BUTTON_UP = 0, BUTTON_DOWN = 1, BUTTON_LEFT = 2, BUTTON_RIGHT = 3, BUTTON_A = 4, BUTTON_B = 5, BUTTON_C = 6,
			BUTTON_D = 7, BUTTON_E = 8, BUTTON_F = 9, BUTTON_QUIT = 10, BUTTON_PAUSE = 11, BUTTON_GIVEUP = 12, BUTTON_RETRY = 13,
			BUTTON_FRAMESTEP = 14, BUTTON_SCREENSHOT = 15;

	/** ボタン数の定数 */
	public static final int MAX_BUTTON = 16;

	/**
	 * 全ステート共通のキー入力状態オブジェクトをInitialization
	 */
	public static void initGlobalGameKeySDL() {
		gamekey = new GameKeySDL[2];
		gamekey[0] = new GameKeySDL(0);
		gamekey[1] = new GameKeySDL(1);
	}

	/** キーコード */
	public int keymap[];

	/** ジョイスティックボタン number */
	public int buttonmap[];

	/** ジョイスティックのDirectionキーが反応する閾値 */
	public int joyBorder;

	/** プレイヤーID */
	public int player;

	/** ボタン入力フラグ兼入力時間 */
	protected int inputstate[];

	/**
	 * デフォルトConstructor
	 */
	public GameKeySDL() {
		keymap = new int[MAX_BUTTON];
		buttonmap = new int[MAX_BUTTON];
		joyBorder = 0;
		for(int i = 0; i < buttonmap.length; i++) buttonmap[i] = -1;
		player = 0;
		inputstate = new int[MAX_BUTTON];
	}

	/**
	 * プレイヤー numberを指定できるConstructor
	 * @param pl プレイヤー number
	 */
	public GameKeySDL(int pl) {
		this();
		player = pl;
	}

	/**
	 * ボタン入力状態を更新
	 * @param keyboard キーボードのキーが押されているかどうかの配列
	 */
	public void update(boolean[] keyboard) {
		update(keyboard, null, 0, 0, null);
	}

	/**
	 * ボタン入力状態を更新
	 * @param keyboard キーボードのキーが押されているかどうかの配列
	 * @param joyButton ジョイスティックのボタンが押されているかどうかの配列（nullにしても問題なし）
	 * @param joyX ジョイスティックのX軸の状態
	 * @param joyY ジョイスティックのY軸の状態
	 * @param hat ハットスイッチの状態（nullにしても問題なし）
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
				// その他のボタン
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
	 * ボタン入力状態をクリア
	 */
	public void clear() {
		for(int i = 0; i < MAX_BUTTON; i++) {
			inputstate[i] = 0;
		}
	}

	/**
	 * ボタンが1フレームだけ押されているか判定
	 * @param key ボタン number
	 * @return 押されていたらtrue
	 */
	public boolean isPushKey(int key) {
		return (inputstate[key] == 1);
	}

	/**
	 * ボタンが押されているか判定
	 * @param key ボタン number
	 * @return 押されていたらtrue
	 */
	public boolean isPressKey(int key) {
		return (inputstate[key] >= 1);
	}

	/**
	 * メニューでカーソルが動くかどうか判定
	 * @param key ボタン number
	 * @return カーソルが動くならtrue
	 */
	public boolean isMenuRepeatKey(int key) {
		if((inputstate[key] == 1) || ((inputstate[key] >= 25) && (inputstate[key] % 3 == 0)) || ((inputstate[key] >= 1) && isPressKey(BUTTON_C)))
			return true;

		return false;
	}

	/**
	 * ボタンを押している時間を取得
	 * @param key ボタン number
	 * @return ボタンを押している時間（0なら押してない）
	 */
	public int getInputState(int key) {
		return inputstate[key];
	}

	/**
	 * ボタンを押している時間を強制変更
	 * @param key ボタン number
	 * @param state ボタンを押している時間
	 */
	public void setInputState(int key, int state) {
		inputstate[key] = state;
	}

	/**
	 * キー設定を読み込み
	 * @param prop 読み込み元のProperty file
	 */
	public void loadConfig(CustomProperties prop) {
		keymap[BUTTON_UP] = prop.getProperty("key.p" + player + ".up", 0);
		keymap[BUTTON_DOWN] = prop.getProperty("key.p" + player + ".down", 0);
		keymap[BUTTON_LEFT] = prop.getProperty("key.p" + player + ".left", 0);
		keymap[BUTTON_RIGHT] = prop.getProperty("key.p" + player + ".right", 0);
		keymap[BUTTON_A] = prop.getProperty("key.p" + player + ".a", 0);
		keymap[BUTTON_B] = prop.getProperty("key.p" + player + ".b", 0);
		keymap[BUTTON_C] = prop.getProperty("key.p" + player + ".c", 0);
		keymap[BUTTON_D] = prop.getProperty("key.p" + player + ".d", 0);
		keymap[BUTTON_E] = prop.getProperty("key.p" + player + ".e", 0);
		keymap[BUTTON_F] = prop.getProperty("key.p" + player + ".f", 0);
		keymap[BUTTON_QUIT] = prop.getProperty("key.p" + player + ".quit", 0);
		keymap[BUTTON_PAUSE] = prop.getProperty("key.p" + player + ".pause", 0);
		keymap[BUTTON_GIVEUP] = prop.getProperty("key.p" + player + ".giveup", 0);
		keymap[BUTTON_RETRY] = prop.getProperty("key.p" + player + ".retry", 0);
		keymap[BUTTON_FRAMESTEP] = prop.getProperty("key.p" + player + ".framestep", 0);
		keymap[BUTTON_SCREENSHOT] = prop.getProperty("key.p" + player + ".screenshot", 0);

		//buttonmap[BUTTON_UP] = prop.getProperty("button.p" + player + ".up", 0);
		//buttonmap[BUTTON_DOWN] = prop.getProperty("button.p" + player + ".down", 0);
		//buttonmap[BUTTON_LEFT] = prop.getProperty("button.p" + player + ".left", 0);
		//buttonmap[BUTTON_RIGHT] = prop.getProperty("button.p" + player + ".right", 0);
		buttonmap[BUTTON_A] = prop.getProperty("button.p" + player + ".a", -1);
		buttonmap[BUTTON_B] = prop.getProperty("button.p" + player + ".b", -1);
		buttonmap[BUTTON_C] = prop.getProperty("button.p" + player + ".c", -1);
		buttonmap[BUTTON_D] = prop.getProperty("button.p" + player + ".d", -1);
		buttonmap[BUTTON_E] = prop.getProperty("button.p" + player + ".e", -1);
		buttonmap[BUTTON_F] = prop.getProperty("button.p" + player + ".f", -1);
		buttonmap[BUTTON_QUIT] = prop.getProperty("button.p" + player + ".quit", -1);
		buttonmap[BUTTON_PAUSE] = prop.getProperty("button.p" + player + ".pause", -1);
		buttonmap[BUTTON_GIVEUP] = prop.getProperty("button.p" + player + ".giveup", -1);
		buttonmap[BUTTON_RETRY] = prop.getProperty("button.p" + player + ".retry", -1);
		buttonmap[BUTTON_FRAMESTEP] = prop.getProperty("button.p" + player + ".framestep", -1);
		buttonmap[BUTTON_SCREENSHOT] = prop.getProperty("button.p" + player + ".screenshot", -1);

		joyBorder = prop.getProperty("joyBorder.p" + player, 0);
	}

	/**
	 * キー設定を保存
	 * @param prop 保存先のProperty file
	 */
	public void saveConfig(CustomProperties prop) {
		prop.setProperty("key.p" + player + ".up", keymap[BUTTON_UP]);
		prop.setProperty("key.p" + player + ".down", keymap[BUTTON_DOWN]);
		prop.setProperty("key.p" + player + ".left", keymap[BUTTON_LEFT]);
		prop.setProperty("key.p" + player + ".right", keymap[BUTTON_RIGHT]);
		prop.setProperty("key.p" + player + ".a", keymap[BUTTON_A]);
		prop.setProperty("key.p" + player + ".b", keymap[BUTTON_B]);
		prop.setProperty("key.p" + player + ".c", keymap[BUTTON_C]);
		prop.setProperty("key.p" + player + ".d", keymap[BUTTON_D]);
		prop.setProperty("key.p" + player + ".e", keymap[BUTTON_E]);
		prop.setProperty("key.p" + player + ".f", keymap[BUTTON_F]);
		prop.setProperty("key.p" + player + ".quit", keymap[BUTTON_QUIT]);
		prop.setProperty("key.p" + player + ".pause", keymap[BUTTON_PAUSE]);
		prop.setProperty("key.p" + player + ".giveup", keymap[BUTTON_GIVEUP]);
		prop.setProperty("key.p" + player + ".retry", keymap[BUTTON_RETRY]);
		prop.setProperty("key.p" + player + ".framestep", keymap[BUTTON_FRAMESTEP]);
		prop.setProperty("key.p" + player + ".screenshot", keymap[BUTTON_SCREENSHOT]);

		//prop.setProperty("button.p" + player + ".up", buttonmap[BUTTON_UP]);
		//prop.setProperty("button.p" + player + ".down", buttonmap[BUTTON_DOWN]);
		//prop.setProperty("button.p" + player + ".left", buttonmap[BUTTON_LEFT]);
		//prop.setProperty("button.p" + player + ".right", buttonmap[BUTTON_RIGHT]);
		prop.setProperty("button.p" + player + ".a", buttonmap[BUTTON_A]);
		prop.setProperty("button.p" + player + ".b", buttonmap[BUTTON_B]);
		prop.setProperty("button.p" + player + ".c", buttonmap[BUTTON_C]);
		prop.setProperty("button.p" + player + ".d", buttonmap[BUTTON_D]);
		prop.setProperty("button.p" + player + ".e", buttonmap[BUTTON_E]);
		prop.setProperty("button.p" + player + ".f", buttonmap[BUTTON_F]);
		prop.setProperty("button.p" + player + ".quit", buttonmap[BUTTON_QUIT]);
		prop.setProperty("button.p" + player + ".pause", buttonmap[BUTTON_PAUSE]);
		prop.setProperty("button.p" + player + ".giveup", buttonmap[BUTTON_GIVEUP]);
		prop.setProperty("button.p" + player + ".retry", buttonmap[BUTTON_RETRY]);
		prop.setProperty("button.p" + player + ".framestep", buttonmap[BUTTON_FRAMESTEP]);
		prop.setProperty("button.p" + player + ".screenshot", buttonmap[BUTTON_SCREENSHOT]);

		prop.setProperty("joyBorder.p" + player, joyBorder);
	}

	/**
	 * Controllerに入力状況を伝える
	 * @param ctrl 入力状況を伝えるControllerのインスタンス
	 */
	public void inputStatusUpdate(Controller ctrl) {
		for(int i = 0; i < Controller.BUTTON_COUNT; i++) {
			ctrl.buttonPress[i] = isPressKey(i);
		}
	}
}
