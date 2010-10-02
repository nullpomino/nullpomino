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

import mu.nu.nullpo.game.play.GameManager;

import sdljava.SDLException;
import sdljava.event.SDLKey;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

/**
 * キーボード設定画面のステート
 */
public class StateConfigKeyboardSDL extends BaseStateSDL {
	/** Key input を受付可能になるまでの frame count */
	public static final int KEYACCEPTFRAME = 30;

	/** Number of keys to set */
	public static final int NUM_KEYS = 16;

	/** Player number */
	public int player = 0;

	/** true if navigation key setting mode */
	public boolean isNavSetting = false;

	/** 初期設定Mode */
	protected boolean firstSetupMode;

	/** Number of button currently being configured */
	protected int keynum;

	/** 経過 frame count */
	protected int frame;

	/** Button settings */
	protected int keymap[];

	/** 前の frame のKey input state */
	protected boolean previousKeyPressedState[];

	/**
	 * Button settings initialization
	 */
	protected void reset() {
		firstSetupMode = NullpoMinoSDL.propConfig.getProperty("option.firstSetupMode", true);

		keynum = 0;
		frame = 0;

		keymap = new int[NUM_KEYS];
		previousKeyPressedState = new boolean[NullpoMinoSDL.SDL_KEY_MAX];

		for(int i = 0; i < NUM_KEYS; i++) {
			if(!isNavSetting)
				keymap[i] = GameKeySDL.gamekey[player].keymap[i];
			else
				keymap[i] = GameKeySDL.gamekey[player].keymapNav[i];
		}
	}

	/**
	 * 押されたキーの numberを返す
	 * @param prev 前の frame での input 状態
	 * @param now この frame での input 状態
	 * @return 押されたキーの number, 無いならSDLKey.SDLK_UNKNOWN
	 */
	protected int getPressedKeyNumber(boolean[] prev, boolean[] now) {
		for(int i = 0; i < now.length; i++) {
			if(prev[i] != now[i]) {
				return i;
			}
		}

		return SDLKey.SDLK_UNKNOWN;
	}

	/**
	 * キーのNameを取得
	 * @param key キー
	 * @return キーのName
	 */
	protected String getKeyName(int key) {
		return "(" + key + ")";
	}

	/*
	 * Draw the screen
	 */
	@Override
	public void render(SDLSurface screen) throws SDLException {
		ResourceHolderSDL.imgMenu.blitSurface(screen);

		if(!isNavSetting) {
			NormalFontSDL.printFontGrid(1,  1, "KEYBOARD SETTING (" + (player + 1) + "P)", NormalFontSDL.COLOR_ORANGE);
		} else {
			NormalFontSDL.printFontGrid(1,  1, "KEYBOARD NAVIGATION SETTING (" + (player + 1) + "P)", NormalFontSDL.COLOR_ORANGE);
		}

		NormalFontSDL.printFontGrid(2,  3, "UP          : " + getKeyName(keymap[GameKeySDL.BUTTON_UP]), (keynum == 0));
		NormalFontSDL.printFontGrid(2,  4, "DOWN        : " + getKeyName(keymap[GameKeySDL.BUTTON_DOWN]), (keynum == 1));
		NormalFontSDL.printFontGrid(2,  5, "LEFT        : " + getKeyName(keymap[GameKeySDL.BUTTON_LEFT]), (keynum == 2));
		NormalFontSDL.printFontGrid(2,  6, "RIGHT       : " + getKeyName(keymap[GameKeySDL.BUTTON_RIGHT]), (keynum == 3));
		if(!isNavSetting) {
			NormalFontSDL.printFontGrid(2,  7, "A (L/R-ROT) : " + getKeyName(keymap[GameKeySDL.BUTTON_A]), (keynum == 4));
			NormalFontSDL.printFontGrid(2,  8, "B (R/L-ROT) : " + getKeyName(keymap[GameKeySDL.BUTTON_B]), (keynum == 5));
			NormalFontSDL.printFontGrid(2,  9, "C (L/R-ROT) : " + getKeyName(keymap[GameKeySDL.BUTTON_C]), (keynum == 6));
			NormalFontSDL.printFontGrid(2, 10, "D (HOLD)    : " + getKeyName(keymap[GameKeySDL.BUTTON_D]), (keynum == 7));
			NormalFontSDL.printFontGrid(2, 11, "E (180-ROT) : " + getKeyName(keymap[GameKeySDL.BUTTON_E]), (keynum == 8));
		} else {
			NormalFontSDL.printFontGrid(2,  7, "A (SELECT)  : " + getKeyName(keymap[GameKeySDL.BUTTON_A]), (keynum == 4));
			NormalFontSDL.printFontGrid(2,  8, "B (CANCEL)  : " + getKeyName(keymap[GameKeySDL.BUTTON_B]), (keynum == 5));
			NormalFontSDL.printFontGrid(2,  9, "C           : " + getKeyName(keymap[GameKeySDL.BUTTON_C]), (keynum == 6));
			NormalFontSDL.printFontGrid(2, 10, "D           : " + getKeyName(keymap[GameKeySDL.BUTTON_D]), (keynum == 7));
			NormalFontSDL.printFontGrid(2, 11, "E           : " + getKeyName(keymap[GameKeySDL.BUTTON_E]), (keynum == 8));
		}
		NormalFontSDL.printFontGrid(2, 12, "F           : " + getKeyName(keymap[GameKeySDL.BUTTON_F]), (keynum == 9));
		NormalFontSDL.printFontGrid(2, 13, "QUIT        : " + getKeyName(keymap[GameKeySDL.BUTTON_QUIT]), (keynum == 10));
		NormalFontSDL.printFontGrid(2, 14, "PAUSE       : " + getKeyName(keymap[GameKeySDL.BUTTON_PAUSE]), (keynum == 11));
		NormalFontSDL.printFontGrid(2, 15, "GIVEUP      : " + getKeyName(keymap[GameKeySDL.BUTTON_GIVEUP]), (keynum == 12));
		NormalFontSDL.printFontGrid(2, 16, "RETRY       : " + getKeyName(keymap[GameKeySDL.BUTTON_RETRY]), (keynum == 13));
		NormalFontSDL.printFontGrid(2, 17, "FRAME STEP  : " + getKeyName(keymap[GameKeySDL.BUTTON_FRAMESTEP]), (keynum == 14));
		NormalFontSDL.printFontGrid(2, 18, "SCREEN SHOT : " + getKeyName(keymap[GameKeySDL.BUTTON_SCREENSHOT]), (keynum == 15));

		if(frame >= KEYACCEPTFRAME) {
			if(keynum < NUM_KEYS) {
				NormalFontSDL.printFontGrid(1, 3 + keynum, "b", NormalFontSDL.COLOR_RED);

				NormalFontSDL.printFontGrid(1, 25, "DELETE:    NO SET", NormalFontSDL.COLOR_GREEN);

				if(!firstSetupMode) NormalFontSDL.printFontGrid(1, 26, "BACKSPACE: CANCEL", NormalFontSDL.COLOR_GREEN);
			} else {
				NormalFontSDL.printFontGrid(1, 25, "ENTER:     OK", NormalFontSDL.COLOR_GREEN);
				NormalFontSDL.printFontGrid(1, 26, "DELETE:    AGAIN", NormalFontSDL.COLOR_GREEN);
				if(!firstSetupMode) NormalFontSDL.printFontGrid(1, 27, "BACKSPACE: CANCEL", NormalFontSDL.COLOR_GREEN);
			}
		}
	}

	/*
	 * Update game state
	 */
	@Override
	public void update() throws SDLException {
		if(frame >= KEYACCEPTFRAME) {
			if(keynum < NUM_KEYS) {
				if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_DELETE]) {
					ResourceHolderSDL.soundManager.play("move");
					keymap[keynum] = 0;
					keynum++;
					frame = 0;
				} else if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_BACKSPACE]) {
					if(!firstSetupMode) {
						NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
						return;
					}
				} else {
					int key = getPressedKeyNumber(previousKeyPressedState, NullpoMinoSDL.keyPressedState);

					if(key != SDLKey.SDLK_UNKNOWN) {
						ResourceHolderSDL.soundManager.play("move");
						keymap[keynum] = key;
						keynum++;
						frame = 0;
					}
				}
			} else {
				if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_RETURN]) {
					ResourceHolderSDL.soundManager.play("decide");

					//NullpoMinoSDL.propConfig.setProperty("option.firstSetupMode", false);
					for(int i = 0; i < NUM_KEYS; i++) {
						if(!isNavSetting)
							GameKeySDL.gamekey[player].keymap[i] = keymap[i];
						else
							GameKeySDL.gamekey[player].keymapNav[i] = keymap[i];
					}
					/*
					if(!firstSetupMode && NullpoMinoSDL.propConfig.getProperty("option.keyCustomNaviType", 0) == 1) {
						for(int i = 0; i < StateConfigKeyboardNaviSDL.NUM_KEYS; i++) {
							GameKeySDL.gamekey[player].keymap[i+GameKeySDL.BUTTON_NAV_UP] = keymap[i];
						}
					}
					*/
					GameKeySDL.gamekey[player].saveConfig(NullpoMinoSDL.propConfig);
					NullpoMinoSDL.saveConfig();

					if(firstSetupMode)
						NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_TITLE);
					else
						NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);

					return;
				} else if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_DELETE]) {
					ResourceHolderSDL.soundManager.play("move");
					reset();
				} else if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_BACKSPACE]) {
					if(!firstSetupMode) {
						NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
						return;
					}
				}
			}
		}

		for(int i = 0; i < NullpoMinoSDL.keyPressedState.length; i++) {
			previousKeyPressedState[i] = NullpoMinoSDL.keyPressedState[i];
		}
		frame++;
	}

	/*
	 * Called when entering this state
	 */
	@Override
	public void enter() throws SDLException {
		reset();
		NullpoMinoSDL.enableSpecialKeys = false;
		SDLVideo.wmSetCaption("NullpoMino version" + GameManager.getVersionString(), null);
	}

	/*
	 * Called when leaving this state
	 */
	@Override
	public void leave() throws SDLException {
		reset();
		NullpoMinoSDL.enableSpecialKeys = true;
	}
}
