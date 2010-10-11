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
import mu.nu.nullpo.util.GeneralUtil;

import sdljava.SDLException;
import sdljava.event.SDLKey;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

/**
 * Keyboard config screen state
 */
public class StateConfigKeyboardSDL extends BaseStateSDL {
	/** Number of frames you have to wait */
	public static final int KEYACCEPTFRAME = 15;

	/** Number of keys to set */
	public static final int NUM_KEYS = 16;

	/** Player number */
	public int player = 0;

	/** true if navigation key setting mode */
	public boolean isNavSetting = false;

	/** Number of button currently being configured */
	protected int keynum;

	/** Frame counter */
	protected int frame;

	/** Nunber of frames left in key-set mode */
	protected int keyConfigRestFrame;

	/** Button settings */
	protected int[] keymap;

	/** Previous key input state */
	protected boolean[] previousKeyPressedState;

	/**
	 * Button settings initialization
	 */
	protected void reset() {
		keynum = 0;
		frame = 0;
		keyConfigRestFrame = 0;

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
	 * Get newly pressed key code
	 * @param prev Previous input state
	 * @param now New input state
	 * @return The newly pressed key code. It will return SDLKey.SDLK_UNKNOWN if none are pressed.
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
	 * Get key name
	 * @param key Keycode
	 * @return Key name
	 */
	protected String getKeyName(int key) {
		if((key < 0) || (key >= NullpoMinoSDL.SDL_KEYNAMES.length)) {
			return "(" + key + ")";
		}
		return NullpoMinoSDL.SDL_KEYNAMES[key];
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
		NormalFontSDL.printFontGrid(2, 19, "[SAVE & EXIT]", (keynum == 16));

		NormalFontSDL.printFontGrid(1, 3 + keynum, "b", NormalFontSDL.COLOR_RED);

		if(frame >= KEYACCEPTFRAME) {
			if(keyConfigRestFrame > 0) {
				NormalFontSDL.printFontGrid(1, 21, "PUSH KEY... " + GeneralUtil.getTime(keyConfigRestFrame), NormalFontSDL.COLOR_PINK);
			} else if(keynum < NUM_KEYS) {
				NormalFontSDL.printFontGrid(1, 21, "UP/DOWN:   MOVE CURSOR", NormalFontSDL.COLOR_GREEN);
				NormalFontSDL.printFontGrid(1, 22, "ENTER:     SET KEY", NormalFontSDL.COLOR_GREEN);
				NormalFontSDL.printFontGrid(1, 23, "DELETE:    SET TO NONE", NormalFontSDL.COLOR_GREEN);
				NormalFontSDL.printFontGrid(1, 24, "BACKSPACE: CANCEL", NormalFontSDL.COLOR_GREEN);
			} else {
				NormalFontSDL.printFontGrid(1, 21, "UP/DOWN:   MOVE CURSOR", NormalFontSDL.COLOR_GREEN);
				NormalFontSDL.printFontGrid(1, 22, "ENTER:     SAVE & EXIT", NormalFontSDL.COLOR_GREEN);
				NormalFontSDL.printFontGrid(1, 23, "BACKSPACE: CANCEL", NormalFontSDL.COLOR_GREEN);
			}
		}
	}

	/*
	 * Update game state
	 */
	@Override
	public void update() throws SDLException {
		if(frame >= KEYACCEPTFRAME) {
			if(keyConfigRestFrame > 0) {
				// Key-set mode
				int key = getPressedKeyNumber(previousKeyPressedState, NullpoMinoSDL.keyPressedState);

				if(key != SDLKey.SDLK_UNKNOWN) {
					ResourceHolderSDL.soundManager.play("change");
					keymap[keynum] = key;
					frame = 0;
					keyConfigRestFrame = 0;
					return;
				}

				keyConfigRestFrame--;
			} else {
				// Menu mode
				if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_UP]) {
					frame = 0;
					ResourceHolderSDL.soundManager.play("cursor");
					keynum--;
					if(keynum < 0) keynum = NUM_KEYS;
				}
				if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_DOWN]) {
					frame = 0;
					ResourceHolderSDL.soundManager.play("cursor");
					keynum++;
					if(keynum > NUM_KEYS) keynum = 0;
				}

				// Enter
				if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_RETURN]) {
					ResourceHolderSDL.soundManager.play("decide");

					if(keynum >= NUM_KEYS) {
						// Save & Exit
						for(int i = 0; i < NUM_KEYS; i++) {
							if(!isNavSetting)
								GameKeySDL.gamekey[player].keymap[i] = keymap[i];
							else
								GameKeySDL.gamekey[player].keymapNav[i] = keymap[i];
						}
						GameKeySDL.gamekey[player].saveConfig(NullpoMinoSDL.propConfig);
						NullpoMinoSDL.saveConfig();
						NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
					} else {
						// Set Key
						frame = 0;
						keyConfigRestFrame = 60 * 5;
					}
					return;
				}

				// Delete
				if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_DELETE]) {
					if((keynum < NUM_KEYS) && (keymap[keynum] != SDLKey.SDLK_UNKNOWN)) {
						ResourceHolderSDL.soundManager.play("change");
						keymap[keynum] = SDLKey.SDLK_UNKNOWN;
					}
				}

				// Backspace
				if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_BACKSPACE]) {
					if(isNavSetting)
						NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_KEYBOARD_NAVI);
					else
						NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
					return;
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
