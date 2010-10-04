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
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * キーボード設定画面のステート
 */
public class StateConfigKeyboard extends BasicGameState {
	/** This state's ID */
	public static final int ID = 9;

	/** Key input を受付可能になるまでの frame count */
	public static final int KEYACCEPTFRAME = 30;

	/** Number of keys to set */
	public static final int NUM_KEYS = 16;

	/** Player number */
	public int player = 0;

	/** true if navigation key setting mode */
	public boolean isNavSetting = false;

	/** StateBasedGame */
	protected StateBasedGame gameObj;

	/** true if no key is pressed now (for JInput mode) */
	protected boolean noPressedKey;

	/** Number of button currently being configured */
	protected int keynum;

	/** 経過 frame count */
	protected int frame;

	/** Button settings */
	protected int keymap[];

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
		noPressedKey = true;

		keynum = 0;
		frame = 0;

		keymap = new int[NUM_KEYS];

		for(int i = 0; i < NUM_KEYS; i++) {
			if(!isNavSetting)
				keymap[i] = GameKey.gamekey[player].keymap[i];
			else
				keymap[i] = GameKey.gamekey[player].keymapNav[i];
		}
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

		if(!isNavSetting) {
			NormalFont.printFontGrid(1, 1, "KEYBOARD SETTING (" + (player + 1) + "P)", NormalFont.COLOR_ORANGE);
		} else {
			NormalFont.printFontGrid(1, 1, "KEYBOARD NAVIGATION SETTING (" + (player + 1) + "P)", NormalFont.COLOR_ORANGE);
		}
		if(!NullpoMinoSlick.useJInputKeyboard) {
			NormalFont.printFontGrid(1, 2, "SLICK NATIVE MODE", NormalFont.COLOR_CYAN);
		} else {
			NormalFont.printFontGrid(1, 2, "JINPUT MODE", NormalFont.COLOR_PINK);
		}

		NormalFont.printFontGrid(2,  4, "UP          : " + getKeyName(keymap[GameKey.BUTTON_UP]), (keynum == 0));
		NormalFont.printFontGrid(2,  5, "DOWN        : " + getKeyName(keymap[GameKey.BUTTON_DOWN]), (keynum == 1));
		NormalFont.printFontGrid(2,  6, "LEFT        : " + getKeyName(keymap[GameKey.BUTTON_LEFT]), (keynum == 2));
		NormalFont.printFontGrid(2,  7, "RIGHT       : " + getKeyName(keymap[GameKey.BUTTON_RIGHT]), (keynum == 3));
		if(!isNavSetting) {
			NormalFont.printFontGrid(2,  8, "A (L/R-ROT) : " + getKeyName(keymap[GameKey.BUTTON_A]), (keynum == 4));
			NormalFont.printFontGrid(2,  9, "B (R/L-ROT) : " + getKeyName(keymap[GameKey.BUTTON_B]), (keynum == 5));
			NormalFont.printFontGrid(2, 10, "C (L/R-ROT) : " + getKeyName(keymap[GameKey.BUTTON_C]), (keynum == 6));
			NormalFont.printFontGrid(2, 11, "D (HOLD)    : " + getKeyName(keymap[GameKey.BUTTON_D]), (keynum == 7));
			NormalFont.printFontGrid(2, 12, "E (180-ROT) : " + getKeyName(keymap[GameKey.BUTTON_E]), (keynum == 8));
		} else {
			NormalFont.printFontGrid(2,  8, "A (SELECT)  : " + getKeyName(keymap[GameKey.BUTTON_A]), (keynum == 4));
			NormalFont.printFontGrid(2,  9, "B (CANCEL)  : " + getKeyName(keymap[GameKey.BUTTON_B]), (keynum == 5));
			NormalFont.printFontGrid(2, 10, "C           : " + getKeyName(keymap[GameKey.BUTTON_C]), (keynum == 6));
			NormalFont.printFontGrid(2, 11, "D           : " + getKeyName(keymap[GameKey.BUTTON_D]), (keynum == 7));
			NormalFont.printFontGrid(2, 12, "E           : " + getKeyName(keymap[GameKey.BUTTON_E]), (keynum == 8));
		}
		NormalFont.printFontGrid(2, 13, "F           : " + getKeyName(keymap[GameKey.BUTTON_F]), (keynum == 9));
		NormalFont.printFontGrid(2, 14, "QUIT        : " + getKeyName(keymap[GameKey.BUTTON_QUIT]), (keynum == 10));
		NormalFont.printFontGrid(2, 15, "PAUSE       : " + getKeyName(keymap[GameKey.BUTTON_PAUSE]), (keynum == 11));
		NormalFont.printFontGrid(2, 16, "GIVEUP      : " + getKeyName(keymap[GameKey.BUTTON_GIVEUP]), (keynum == 12));
		NormalFont.printFontGrid(2, 17, "RETRY       : " + getKeyName(keymap[GameKey.BUTTON_RETRY]), (keynum == 13));
		NormalFont.printFontGrid(2, 18, "FRAME STEP  : " + getKeyName(keymap[GameKey.BUTTON_FRAMESTEP]), (keynum == 14));
		NormalFont.printFontGrid(2, 19, "SCREEN SHOT : " + getKeyName(keymap[GameKey.BUTTON_SCREENSHOT]), (keynum == 15));

		if(frame >= KEYACCEPTFRAME) {
			if(keynum < NUM_KEYS) {
				NormalFont.printFontGrid(1, 4 + keynum, "b", NormalFont.COLOR_RED);

				NormalFont.printFontGrid(1, 25, "DELETE:    NO SET", NormalFont.COLOR_GREEN);
				NormalFont.printFontGrid(1, 26, "BACKSPACE: CANCEL", NormalFont.COLOR_GREEN);
			} else {
				NormalFont.printFontGrid(1, 25, "ENTER:     OK", NormalFont.COLOR_GREEN);
				NormalFont.printFontGrid(1, 26, "DELETE:    AGAIN", NormalFont.COLOR_GREEN);
				NormalFont.printFontGrid(1, 27, "BACKSPACE: CANCEL", NormalFont.COLOR_GREEN);
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
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(!container.hasFocus()) {
			if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
			return;
		}

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

		if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}

	/*
	 * When a key is released (Slick native)
	 */
	@Override
	public void keyReleased(int key, char c) {
		if(!NullpoMinoSlick.useJInputKeyboard) {
			onKey(key);
		}
	}

	/**
	 * When a key is released
	 * @param key Keycode
	 */
	protected void onKey(int key) {
		if(frame >= KEYACCEPTFRAME) {
			if(keynum < NUM_KEYS) {
				if(key == Input.KEY_DELETE) {
					ResourceHolder.soundManager.play("move");
					keymap[keynum] = 0;
				} else if(key == Input.KEY_BACK) {
					if(isNavSetting)
						gameObj.enterState(StateConfigKeyboardNavi.ID);
					else
						gameObj.enterState(StateConfigMainMenu.ID);
					return;
				} else {
					ResourceHolder.soundManager.play("move");
					keymap[keynum] = key;
				}

				keynum++;
			} else {
				if(key == Input.KEY_ENTER) {
					ResourceHolder.soundManager.play("decide");

					//NullpoMinoSlick.propConfig.setProperty("option.firstSetupMode", false);
					for(int i = 0; i < NUM_KEYS; i++) {
						if(!isNavSetting)
							GameKey.gamekey[player].keymap[i] = keymap[i];
						else
							GameKey.gamekey[player].keymapNav[i] = keymap[i];
					}
					/*
					if(!firstSetupMode && NullpoMinoSlick.propConfig.getProperty("option.keyCustomNaviType", 0) == 1) {
						for(int i = 0; i < StateConfigKeyboardNavi.NUM_KEYS; i++) {
							GameKey.gamekey[player].keymap[i+GameKey.BUTTON_NAV_UP] = keymap[i];
						}
					}
					*/
					GameKey.gamekey[player].saveConfig(NullpoMinoSlick.propConfig);
					NullpoMinoSlick.saveConfig();

					gameObj.enterState(StateConfigMainMenu.ID);
				} else if(key == Input.KEY_DELETE) {
					ResourceHolder.soundManager.play("move");
					reset();
				} else if(key == Input.KEY_BACK) {
					if(isNavSetting)
						gameObj.enterState(StateConfigKeyboardNavi.ID);
					else
						gameObj.enterState(StateConfigMainMenu.ID);
				}
			}
		}
	}

	/**
	 * Called when entering this state
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		reset();
	}

	/**
	 * Called when leaving this state
	 */
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		reset();
	}
}
