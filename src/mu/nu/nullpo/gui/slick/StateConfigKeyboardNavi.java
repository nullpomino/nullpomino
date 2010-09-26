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

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * キーボード設定画面のステート
 */
public class StateConfigKeyboardNavi extends DummyMenuChooseState {
	/** This state's ID */
	public static final int ID = 16;

	/** Key input を受付可能になるまでの frame count */
	public static final int KEYACCEPTFRAME = 30;

	/** Number of keys to set */
	public static final int NUM_KEYS = 6;

	public static final String[] KEY_NAMES = {
		"UP    ", "DOWN  ", "LEFT  ", "RIGHT ", "SELECT", "CANCEL"
	};

	public static final int[] DEFAULT_KEYS = {200, 208, 203, 205, 28, 1};

	/** Player number */
	public int player = 0;

	/** 初期設定Mode */
	protected boolean firstSetupMode;

	/** StateBasedGame */
	protected StateBasedGame gameObj;

	/** Number of button currently being configured */
	protected int keynum;

	/** 経過 frame count */
	protected int frame;

	/** Button settings */
	protected int keymap[];

	/** Flag set to true when setting custom keys */
	protected boolean setCustom;

	public StateConfigKeyboardNavi () {
		maxCursor = 3;
		minChoiceY = 3;
	}

	/*
	 * Fetch this state's ID
	 */
	@Override
	public int getID() {
		return ID;
	}

	/**
	 * Button settings initialization
	 */
	protected void reset() {
		firstSetupMode = NullpoMinoSlick.propConfig.getProperty("option.firstSetupMode", true);

		keynum = -1;
		frame = 0;
		setCustom = false;

		keymap = new int[NUM_KEYS];
		loadSettings();
	}

	/*
	 * State initialization
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		gameObj = game;
	}

	/**
	 * キーのNameを取得
	 * @param key キー
	 * @return キーのName
	 */
	protected String getKeyName(int key) {
		String str = org.lwjgl.input.Keyboard.getKeyName(key);
		return (str == null) ? String.valueOf(key) : str.toUpperCase();
	}

	/*
	 * Draw the screen
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.drawImage(ResourceHolder.imgMenu, 0, 0);

		NormalFont.printFontGrid(1, 1, "KEYBOARD NAVIGATION SETTING (" + (player + 1) + "P)", NormalFont.COLOR_ORANGE);

		if (!setCustom)
			NormalFont.printFontGrid(1, 3 + cursor, "b", NormalFont.COLOR_RED);

		NormalFont.printFontGrid(2, 3, "DEFAULT", (cursor == 0));
		NormalFont.printFontGrid(2, 4, "GAME KEYS", (cursor == 1));
		NormalFont.printFontGrid(2, 5, "CUSTOM", (cursor == 2));
		NormalFont.printFontGrid(2, 6, "[EDIT CUSTOM]", (cursor == 3));

		if (cursor == 0){
			for(int x = 0; x < NUM_KEYS; x++) {
				NormalFont.printFontGrid(2, x+8, KEY_NAMES[x] + " : " + getKeyName(DEFAULT_KEYS[x]), (keynum == x));
			}
		} else if (cursor == 1) {
			for(int x = 0; x < NUM_KEYS; x++)
				NormalFont.printFontGrid(2, x+8, KEY_NAMES[x] + " : "
						+ getKeyName(GameKey.gamekey[player].keymap[x]));
		} else {
			for (int x = 0; x < NUM_KEYS; x++)
				NormalFont.printFontGrid(2, x+8, KEY_NAMES[x] + " : " + getKeyName(keymap[x]), (keynum == x));
		}

		if(setCustom && frame >= KEYACCEPTFRAME) {
			if(keynum < NUM_KEYS) {
				NormalFont.printFontGrid(1, 8 + keynum, "b", NormalFont.COLOR_RED);

				NormalFont.printFontGrid(1, 25, "DELETE:    NO SET", NormalFont.COLOR_GREEN);
				if(!firstSetupMode) NormalFont.printFontGrid(1, 26, "BACKSPACE: CANCEL", NormalFont.COLOR_GREEN);
			} else {
				NormalFont.printFontGrid(1, 25, "ENTER:     OK", NormalFont.COLOR_GREEN);
				NormalFont.printFontGrid(1, 26, "DELETE:    AGAIN", NormalFont.COLOR_GREEN);
				if(!firstSetupMode) NormalFont.printFontGrid(1, 27, "BACKSPACE: CANCEL", NormalFont.COLOR_GREEN);
			}
		}

		// FPS
		NullpoMinoSlick.drawFPS(container);
		// Observer
		NullpoMinoSlick.drawObserverClient();
		if(!NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}

	/*
	 * Update game state
	 */
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if (!setCustom) {
			super.update(container, game, delta);
			frame = 0;
		} else {
			frame++;

			// JInput
			if(NullpoMinoSlick.useJInputKeyboard) {
				JInputManager.poll();

				if(frame >= KEYACCEPTFRAME) {
					for(int i = 0; i < JInputManager.MAX_SLICK_KEY; i++) {
						if(JInputManager.isKeyDown(i)) {
							onKey(i);
							frame = 0;
							break;
						}
					}
				}
			}
		}

		if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}

	@Override
	protected boolean onDecide(GameContainer container, StateBasedGame game, int delta) {
		if(setCustom) return true;

		if (cursor == 3) {
			setCustom = true;
			//keynum = -1;
			keynum = 0;
			frame = 0;
			return true;
		} else if (cursor == 0){
			for(int i = 0; i < NUM_KEYS; i++) {
				GameKey.gamekey[player].keymap[i+GameKey.BUTTON_NAV_UP] = DEFAULT_KEYS[i];
			}
		} else if (cursor == 1) {
			for(int i = 0; i < NUM_KEYS; i++) {
				GameKey.gamekey[player].keymap[i+GameKey.BUTTON_NAV_UP] = GameKey.gamekey[player].keymap[i];
			}
		} else if (cursor == 2) {
			for(int i = 0; i < NUM_KEYS; i++) {
				GameKey.gamekey[player].keymap[i+GameKey.BUTTON_NAV_UP] = keymap[i];
			}
		}
		GameKey.gamekey[player].saveConfig(NullpoMinoSlick.propConfig);
		NullpoMinoSlick.saveConfig();

		ResourceHolder.soundManager.play("decide");
		NullpoMinoSlick.propConfig.setProperty("option.keyCustomNaviType", cursor);
		if(!firstSetupMode)
			gameObj.enterState(StateConfigMainMenu.ID);
		else
			gameObj.enterState(StateConfigRuleSelect.ID);
		return true;
	}

	@Override
	protected boolean onCancel(GameContainer container, StateBasedGame game, int delta) {
		if(!setCustom) {
			game.enterState(StateConfigMainMenu.ID);
		}
		return false;
	}

	/*
	 * When a key is released (Slick native)
	 */
	@Override
	public void keyReleased(int key, char c) {
		if(setCustom && !NullpoMinoSlick.useJInputKeyboard) {
			onKey(key);
		}
	}

	/**
	 * When a key is released
	 * @param key Keycode
	 */
	protected void onKey(int key) {
		if(frame >= KEYACCEPTFRAME) {
			if (keynum < 0)
				keynum = 0;
			else if(keynum < NUM_KEYS) {
				if(key == Input.KEY_DELETE) {
					ResourceHolder.soundManager.play("move");
					keymap[keynum] = 0;
				} else if(key == Input.KEY_BACK) {
					setCustom = false;
					loadSettings();
					keynum = -1;
				} else {
					ResourceHolder.soundManager.play("move");
					keymap[keynum] = key;
				}

				keynum++;
			} else {
				if(key == Input.KEY_ENTER) {
					ResourceHolder.soundManager.play("decide");

					for(int i = 0; i < NUM_KEYS; i++) {
						NullpoMinoSlick.propConfig.setProperty("option.keyCustomNavi." + i, keymap[i]);
					}
					setCustom = false;
					keynum = -1;
				} else if(key == Input.KEY_DELETE) {
					ResourceHolder.soundManager.play("move");
					loadSettings();
					keynum = 0;
				} else if(key == Input.KEY_BACK) {
					setCustom = false;
					loadSettings();
					keynum = -1;
				}
			}
		}
	}

	private void loadSettings() {
		for(int i = 0; i < NUM_KEYS; i++) {
			keymap[i] = NullpoMinoSlick.propConfig.getProperty("option.keyCustomNavi." + i, DEFAULT_KEYS[i]);
		}
	}

	/**
	 * Called when entering this state
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		reset();
		cursor = NullpoMinoSlick.propConfig.getProperty("option.keyCustomNaviType", 0);
		if (firstSetupMode)
			for(int i = 0; i < NUM_KEYS; i++)
				GameKey.gamekey[player].keymap[i+GameKey.BUTTON_NAV_UP] = DEFAULT_KEYS[i];
	}

	/**
	 * Called when leaving this state
	 */
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		reset();
	}
}
