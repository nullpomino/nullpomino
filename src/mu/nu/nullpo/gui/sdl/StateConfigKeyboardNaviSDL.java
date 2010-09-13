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
public class StateConfigKeyboardNaviSDL extends DummyMenuChooseStateSDL {
	/** This state's ID */
	public static final int ID = 16;

	/** Key input を受付可能になるまでの frame count */
	public static final int KEYACCEPTFRAME = 30;

	/** Number of keys to set */
	public static final int NUM_KEYS = 6;
	
	public static final String[] KEY_NAMES = {
		"UP    ", "DOWN  ", "LEFT  ", "RIGHT ", "SELECT", "CANCEL"
	};
	
	public static final int[] DEFAULT_KEYS = {273, 274, 276, 275, 13, 27};

	/** Player number */
	public int player = 0;

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

	/** Flag set to true when setting custom keys */
	protected boolean setCustom;

	public StateConfigKeyboardNaviSDL () {
		maxCursor = 3;
		minChoiceY = 3;
	}

	/**
	 * Button settings initialization
	 */
	protected void reset() {
		firstSetupMode = NullpoMinoSDL.propConfig.getProperty("option.firstSetupMode", true);

		keynum = -1;
		frame = 0;
		setCustom = false;

		keymap = new int[NUM_KEYS];
		previousKeyPressedState = new boolean[NullpoMinoSDL.SDL_KEY_MAX];
		loadSettings();
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
	public void render(SDLSurface screen) throws SDLException {
		ResourceHolderSDL.imgMenu.blitSurface(screen);

		NormalFontSDL.printFontGrid(1, 1, "KEYBOARD NAVIGATION SETTING (" + (player + 1) + "P)", NormalFontSDL.COLOR_ORANGE);
		
		if (!setCustom)
			NormalFontSDL.printFontGrid(1, 3 + cursor, "b", NormalFontSDL.COLOR_RED);

		NormalFontSDL.printFontGrid(2, 3, "DEFAULT", (cursor == 0));
		NormalFontSDL.printFontGrid(2, 4, "GAME KEYS", (cursor == 1));
		NormalFontSDL.printFontGrid(2, 5, "CUSTOM", (cursor == 2));
		NormalFontSDL.printFontGrid(2, 6, "[EDIT CUSTOM]", (cursor == 3));

		if (cursor == 0){
			for(int x = 0; x < NUM_KEYS; x++) {
				NormalFontSDL.printFontGrid(2, x+8, KEY_NAMES[x] + " : " + getKeyName(DEFAULT_KEYS[x]), (keynum == x));
			}
		} else if (cursor == 1) {
			for(int x = 0; x < NUM_KEYS; x++)
				NormalFontSDL.printFontGrid(2, x+8, KEY_NAMES[x] + " : "
						+ getKeyName(GameKeySDL.gamekey[player].keymap[x]));
		} else {
			for (int x = 0; x < NUM_KEYS; x++)
				NormalFontSDL.printFontGrid(2, x+8, KEY_NAMES[x] + " : " + getKeyName(keymap[x]), (keynum == x));
		}

		if(setCustom && frame >= KEYACCEPTFRAME) {
			if(keynum < NUM_KEYS) {
				NormalFontSDL.printFontGrid(1, 8 + keynum, "b", NormalFontSDL.COLOR_RED);

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
		if (!setCustom) {
			super.update();
			frame = 0;
			return;
		}
		if(frame >= KEYACCEPTFRAME) {
			if (keynum < 0)
				keynum = 0;
			else if(keynum < NUM_KEYS) {
				if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_DELETE]) {
					ResourceHolderSDL.soundManager.play("move");
					keymap[keynum] = 0;
					keynum++;
					frame = 0;
				} else if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_BACKSPACE]) {
					setCustom = false;
					loadSettings();
					keynum = -1;
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

					for(int i = 0; i < NUM_KEYS; i++) {
						NullpoMinoSDL.propConfig.setProperty("option.keyCustomNavi." + i, keymap[i]);
					}
					setCustom = false;
					keynum = -1;
				} else if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_DELETE]) {
					ResourceHolderSDL.soundManager.play("move");
					loadSettings();
					keynum = 0;
				} else if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_BACKSPACE]) {
					setCustom = false;
					loadSettings();
					keynum = -1;
				}
			}
		}

		for(int i = 0; i < NullpoMinoSDL.keyPressedState.length; i++) {
			previousKeyPressedState[i] = NullpoMinoSDL.keyPressedState[i];
		}
		frame++;
	}

	@Override
	protected boolean onDecide() throws SDLException {
		if (cursor == 3) {
			setCustom = true;
			keynum = -1;
			frame = 0;
			return true;
		} else if (cursor == 0){
			for(int i = 0; i < NUM_KEYS; i++) {
				GameKeySDL.gamekey[player].keymap[i+GameKeySDL.BUTTON_NAV_UP] = DEFAULT_KEYS[i];
			}
		} else if (cursor == 1) {
			for(int i = 0; i < NUM_KEYS; i++) {
				GameKeySDL.gamekey[player].keymap[i+GameKeySDL.BUTTON_NAV_UP] = GameKeySDL.gamekey[player].keymap[i];
			}
		} else if (cursor == 2) {
			for(int i = 0; i < NUM_KEYS; i++) {
				GameKeySDL.gamekey[player].keymap[i+GameKeySDL.BUTTON_NAV_UP] = keymap[i];
			}
		}
		GameKeySDL.gamekey[player].saveConfig(NullpoMinoSDL.propConfig);
		NullpoMinoSDL.saveConfig();

		ResourceHolderSDL.soundManager.play("decide");
		NullpoMinoSDL.propConfig.setProperty("option.keyCustomNaviType", cursor);
		if(!firstSetupMode)
			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
		else
			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_RULESELECT);
		return true;
	}

	@Override
	protected boolean onCancel() throws SDLException {
		NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
		return false;
	}
	
	private void loadSettings() {
		for(int i = 0; i < NUM_KEYS; i++) {
			keymap[i] = NullpoMinoSDL.propConfig.getProperty("option.keyCustomNavi." + i, DEFAULT_KEYS[i]);
		}
	}

	/*
	 * Called when entering this state
	 */
	@Override
	public void enter() throws SDLException {
		reset();
		cursor = NullpoMinoSDL.propConfig.getProperty("option.keyCustomNaviType", 0);
		NullpoMinoSDL.enableSpecialKeys = false;
		SDLVideo.wmSetCaption("NullpoMino version" + GameManager.getVersionString(), null);
		if (firstSetupMode)
			for(int i = 0; i < NUM_KEYS; i++)
				GameKeySDL.gamekey[player].keymap[i+GameKeySDL.BUTTON_NAV_UP] = DEFAULT_KEYS[i];
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
