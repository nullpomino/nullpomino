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

import sdljava.SDLException;
import sdljava.event.SDLKey;
import sdljava.video.SDLSurface;

/**
 * Joystick button設定画面のステート
 */
public class StateConfigJoystickButtonSDL extends BaseStateSDL {
	/** Key input を受付可能になるまでの frame count */
	public static final int KEYACCEPTFRAME = 20;

	/** Player number */
	public int player;

	/** 使用するJoystick の number */
	protected int joyNumber;

	/** Number of button currently being configured */
	protected int keynum;

	/** 経過 frame count */
	protected int frame;

	/** Button settings */
	protected int buttonmap[];

	/** 前の frame のJoystick の input 状態 */
	protected boolean previousJoyPressedState[];

	/**
	 * Button settings initialization
	 */
	protected void reset() {
		keynum = 4;
		frame = 0;

		buttonmap = new int[GameKeySDL.MAX_BUTTON];

		joyNumber = NullpoMinoSDL.joyUseNumber[player];

		if(joyNumber >= 0)
			previousJoyPressedState = new boolean[NullpoMinoSDL.joyMaxButton[joyNumber]];
		else
			previousJoyPressedState = null;

		for(int i = 0; i < GameKeySDL.MAX_BUTTON; i++) {
			buttonmap[i] = GameKeySDL.gamekey[player].buttonmap[i];
		}
	}

	/**
	 * 押された buttonの numberを返す
	 * @param prev 前の frame での input 状態
	 * @param now この frame での input 状態
	 * @return 押された buttonの number, 無いなら-1
	 */
	protected int getPressedKeyNumber(boolean[] prev, boolean[] now) {
		for(int i = 0; i < now.length; i++) {
			if(prev[i] != now[i]) {
				return i;
			}
		}

		return -1;
	}

	/*
	 * Draw the screen
	 */
	@Override
	public void render(SDLSurface screen) throws SDLException {
		ResourceHolderSDL.imgMenu.blitSurface(screen);

		NormalFontSDL.printFontGrid(1, 1, "JOYSTICK BUTTON SETTING (" + (player + 1) + "P)", NormalFontSDL.COLOR_ORANGE);

		if(previousJoyPressedState == null)
			NormalFontSDL.printFontGrid(1, 3, "NO JOYSTICK", NormalFontSDL.COLOR_RED);
		else
			NormalFontSDL.printFontGrid(1, 3, "JOYSTICK NUMBER:" + joyNumber, NormalFontSDL.COLOR_RED);

		//NormalFontSDL.printFontGrid(2, 3, "UP             : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_UP]), (keynum == 0));
		//NormalFontSDL.printFontGrid(2, 4, "DOWN           : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_DOWN]), (keynum == 1));
		//NormalFontSDL.printFontGrid(2, 5, "LEFT           : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_LEFT]), (keynum == 2));
		//NormalFontSDL.printFontGrid(2, 6, "RIGHT          : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_RIGHT]), (keynum == 3));
		NormalFontSDL.printFontGrid(2, 5, "A (L/R-ROT)    : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_A]), (keynum == 4));
		NormalFontSDL.printFontGrid(2, 6, "B (R/L-ROT)    : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_B]), (keynum == 5));
		NormalFontSDL.printFontGrid(2, 7, "C (L/R-ROT)    : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_C]), (keynum == 6));
		NormalFontSDL.printFontGrid(2, 8, "D (HOLD)       : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_D]), (keynum == 7));
		NormalFontSDL.printFontGrid(2, 9, "E (180-ROT)    : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_E]), (keynum == 8));
		NormalFontSDL.printFontGrid(2, 10, "F              : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_F]), (keynum == 9));
		NormalFontSDL.printFontGrid(2, 11, "QUIT           : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_QUIT]), (keynum == 10));
		NormalFontSDL.printFontGrid(2, 12, "PAUSE          : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_PAUSE]), (keynum == 11));
		NormalFontSDL.printFontGrid(2, 13, "GIVEUP         : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_GIVEUP]), (keynum == 12));
		NormalFontSDL.printFontGrid(2, 14, "RETRY          : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_RETRY]), (keynum == 13));
		NormalFontSDL.printFontGrid(2, 15, "FRAME STEP     : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_FRAMESTEP]), (keynum == 14));
		NormalFontSDL.printFontGrid(2, 16, "SCREEN SHOT    : " + String.valueOf(buttonmap[GameKeySDL.BUTTON_SCREENSHOT]), (keynum == 15));

		NormalFontSDL.printFontGrid(1, 5 + keynum - 4, "b", NormalFontSDL.COLOR_RED);

		if(frame >= KEYACCEPTFRAME) {
			NormalFontSDL.printFontGrid(1, 20, "UP/DOWN:   MOVE CURSOR", NormalFontSDL.COLOR_GREEN);
			NormalFontSDL.printFontGrid(1, 21, "ENTER:     OK",     NormalFontSDL.COLOR_GREEN);
			NormalFontSDL.printFontGrid(1, 22, "DELETE:    NO SET", NormalFontSDL.COLOR_GREEN);
			NormalFontSDL.printFontGrid(1, 23, "BACKSPACE: CANCEL", NormalFontSDL.COLOR_GREEN);
		}
	}

	/*
	 * Update game state
	 */
	@Override
	public void update() throws SDLException {
		if(frame >= KEYACCEPTFRAME) {
			// Up
			if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_UP]) {
				ResourceHolderSDL.soundManager.play("cursor");
				keynum--;
				if(keynum < 4) keynum = 15;
				frame = 0;
			}
			// Down
			else if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_DOWN]) {
				ResourceHolderSDL.soundManager.play("cursor");
				keynum++;
				if(keynum > 15) keynum = 4;
				frame = 0;
			}
			// Delete
			else if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_DELETE]) {
				ResourceHolderSDL.soundManager.play("change");
				buttonmap[keynum] = -1;
				frame = 0;
			}
			// Backspace
			else if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_BACKSPACE]) {
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_JOYSTICK_MAIN);
				return;
			}
			// Enter/Return
			else if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_RETURN]) {
				ResourceHolderSDL.soundManager.play("decide");

				for(int i = 0; i < GameKeySDL.MAX_BUTTON; i++) {
					GameKeySDL.gamekey[player].buttonmap[i] = buttonmap[i];
				}
				GameKeySDL.gamekey[player].saveConfig(NullpoMinoSDL.propConfig);
				NullpoMinoSDL.saveConfig();

				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_JOYSTICK_MAIN);
				return;
			}
			// Joystick input
			else if(previousJoyPressedState != null) {
				int key = getPressedKeyNumber(previousJoyPressedState, NullpoMinoSDL.joyPressedState[joyNumber]);

				if(key != -1) {
					ResourceHolderSDL.soundManager.play("change");
					buttonmap[keynum] = key;
					frame = 0;
				}
			}
		}

		if(previousJoyPressedState != null) {
			for(int i = 0; i < NullpoMinoSDL.joyPressedState.length; i++) {
				previousJoyPressedState[i] = NullpoMinoSDL.joyPressedState[joyNumber][i];
			}
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
