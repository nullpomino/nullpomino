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
 * Joystick buttonState of the configuration screen
 */
public class StateConfigJoystickButton extends BasicGameState {
	/** This state's ID */
	public static final int ID = 10;

	/** Key input Accepted to be enabled. frame count */
	public static final int KEYACCEPTFRAME = 20;

	/** Player number */
	public int player = 0;

	/** StateBasedGame */
	protected StateBasedGame gameObj;

	/** UseJoystick Of number */
	protected int joyNumber;

	/** Number of button currently being configured */
	protected int keynum;

	/** Course frame count */
	protected int frame;

	/** Button settings */
	protected int buttonmap[];

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
		keynum = 4;
		frame = 0;

		buttonmap = new int[GameKeySlick.MAX_BUTTON];

		joyNumber = ControllerManager.controllerID[player];

		for(int i = 0; i < GameKeySlick.MAX_BUTTON; i++) {
			buttonmap[i] = GameKeySlick.gamekey[player].buttonmap[i];
		}
	}

	/*
	 * State initialization
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		gameObj = game;
	}

	/*
	 * Draw the screen
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		if(!container.hasFocus()) {
			if(!NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
			return;
		}

		g.drawImage(ResourceHolderSlick.imgMenu, 0, 0);

		NormalFontSlick.printFontGrid(1, 1, "JOYSTICK SETTING (" + (player + 1) + "P)", NormalFontSlick.COLOR_ORANGE);

		if(joyNumber < 0)
			NormalFontSlick.printFontGrid(1, 3, "NO JOYSTICK", NormalFontSlick.COLOR_RED);
		else
			NormalFontSlick.printFontGrid(1, 3, "JOYSTICK NUMBER:" + joyNumber, NormalFontSlick.COLOR_RED);

		//NormalFont.printFontGrid(2, 3, "UP             : " + String.valueOf(buttonmap[GameKey.BUTTON_UP]), (keynum == 0));
		//NormalFont.printFontGrid(2, 4, "DOWN           : " + String.valueOf(buttonmap[GameKey.BUTTON_DOWN]), (keynum == 1));
		//NormalFont.printFontGrid(2, 5, "LEFT           : " + String.valueOf(buttonmap[GameKey.BUTTON_LEFT]), (keynum == 2));
		//NormalFont.printFontGrid(2, 6, "RIGHT          : " + String.valueOf(buttonmap[GameKey.BUTTON_RIGHT]), (keynum == 3));
		NormalFontSlick.printFontGrid(2, 5, "A (L/R-ROT)    : " + String.valueOf(buttonmap[GameKeySlick.BUTTON_A]), (keynum == 4));
		NormalFontSlick.printFontGrid(2, 6, "B (R/L-ROT)    : " + String.valueOf(buttonmap[GameKeySlick.BUTTON_B]), (keynum == 5));
		NormalFontSlick.printFontGrid(2, 7, "C (L/R-ROT)    : " + String.valueOf(buttonmap[GameKeySlick.BUTTON_C]), (keynum == 6));
		NormalFontSlick.printFontGrid(2, 8, "D (HOLD)       : " + String.valueOf(buttonmap[GameKeySlick.BUTTON_D]), (keynum == 7));
		NormalFontSlick.printFontGrid(2, 9, "E (180-ROT)    : " + String.valueOf(buttonmap[GameKeySlick.BUTTON_E]), (keynum == 8));
		NormalFontSlick.printFontGrid(2, 10, "F              : " + String.valueOf(buttonmap[GameKeySlick.BUTTON_F]), (keynum == 9));
		NormalFontSlick.printFontGrid(2, 11, "QUIT           : " + String.valueOf(buttonmap[GameKeySlick.BUTTON_QUIT]), (keynum == 10));
		NormalFontSlick.printFontGrid(2, 12, "PAUSE          : " + String.valueOf(buttonmap[GameKeySlick.BUTTON_PAUSE]), (keynum == 11));
		NormalFontSlick.printFontGrid(2, 13, "GIVEUP         : " + String.valueOf(buttonmap[GameKeySlick.BUTTON_GIVEUP]), (keynum == 12));
		NormalFontSlick.printFontGrid(2, 14, "RETRY          : " + String.valueOf(buttonmap[GameKeySlick.BUTTON_RETRY]), (keynum == 13));
		NormalFontSlick.printFontGrid(2, 15, "FRAME STEP     : " + String.valueOf(buttonmap[GameKeySlick.BUTTON_FRAMESTEP]), (keynum == 14));
		NormalFontSlick.printFontGrid(2, 16, "SCREEN SHOT    : " + String.valueOf(buttonmap[GameKeySlick.BUTTON_SCREENSHOT]), (keynum == 15));

		NormalFontSlick.printFontGrid(1, 5 + keynum - 4, "b", NormalFontSlick.COLOR_RED);
		if(frame >= KEYACCEPTFRAME) {
			NormalFontSlick.printFontGrid(1, 20, "UP/DOWN:   MOVE CURSOR", NormalFontSlick.COLOR_GREEN);
			NormalFontSlick.printFontGrid(1, 21, "ENTER:     OK",     NormalFontSlick.COLOR_GREEN);
			NormalFontSlick.printFontGrid(1, 22, "DELETE:    NO SET", NormalFontSlick.COLOR_GREEN);
			NormalFontSlick.printFontGrid(1, 23, "BACKSPACE: CANCEL", NormalFontSlick.COLOR_GREEN);
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

		// Joystick button
		if(frame >= KEYACCEPTFRAME) {
			for(int i = 0; i < ControllerManager.MAX_BUTTONS; i++) {
				try {
					if(ControllerManager.isControllerButton(player, container.getInput(), i)) {
						ResourceHolderSlick.soundManager.play("change");
						buttonmap[keynum] = i;
						frame = 0;
					}
				} catch (Throwable e) {}
			}
		}

		// JInput
		if(NullpoMinoSlick.useJInputKeyboard) {
			JInputManager.poll();

			if(frame >= KEYACCEPTFRAME) {
				for(int i = 0; i < JInputManager.MAX_SLICK_KEY; i++) {
					if(JInputManager.isKeyDown(i)) {
						onKey(i);
						frame = KEYACCEPTFRAME / 2;
						break;
					}
				}
			}
		}

		if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}

	/*
	 * Called when a key is pressed (Slick native)
	 */
	@Override
	public void keyPressed(int key, char c) {
		if(!NullpoMinoSlick.useJInputKeyboard) {
			onKey(key);
		}
	}

	/**
	 * When a key is pressed
	 * @param key Keycode
	 */
	protected void onKey(int key) {
		if(frame >= KEYACCEPTFRAME) {
			// Up
			if(key == Input.KEY_UP) {
				ResourceHolderSlick.soundManager.play("cursor");
				keynum--;
				if(keynum < 4) keynum = 15;
			}
			// Down
			else if(key == Input.KEY_DOWN) {
				ResourceHolderSlick.soundManager.play("cursor");
				keynum++;
				if(keynum > 15) keynum = 4;
			}
			// Delete
			else if(key == Input.KEY_DELETE) {
				ResourceHolderSlick.soundManager.play("change");
				buttonmap[keynum] = -1;
			}
			// Backspace
			else if(key == Input.KEY_BACK) {
				gameObj.enterState(StateConfigJoystickMain.ID);
				return;
			}
			// Enter/Return
			else if(key == Input.KEY_ENTER) {
				ResourceHolderSlick.soundManager.play("decide");

				for(int i = 0; i < GameKeySlick.MAX_BUTTON; i++) {
					GameKeySlick.gamekey[player].buttonmap[i] = buttonmap[i];
				}
				GameKeySlick.gamekey[player].saveConfig(NullpoMinoSlick.propConfig);
				NullpoMinoSlick.saveConfig();

				gameObj.enterState(StateConfigJoystickMain.ID);
				return;
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
