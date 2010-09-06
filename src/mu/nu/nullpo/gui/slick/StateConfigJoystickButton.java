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
 * Joystick  button設定画面のステート
 */
public class StateConfigJoystickButton extends BasicGameState {
	/** このステートのID */
	public static final int ID = 10;

	/** キー input を受付可能になるまでの frame count */
	public static final int KEYACCEPTFRAME = 20;

	/** Player number */
	public int player = 0;

	/** StateBasedGame */
	protected StateBasedGame gameObj;

	/** 使用するJoystick の number */
	protected int joyNumber;

	/** 現在設定中の button */
	protected int keynum;

	/** 経過 frame count */
	protected int frame;

	/**  button設定 */
	protected int buttonmap[];

	/*
	 * このステートのIDを取得
	 */
	@Override
	public int getID() {
		return ID;
	}

	/**
	 *  button設定をInitialization
	 */
	protected void reset() {
		keynum = 4;
		frame = 0;

		buttonmap = new int[GameKey.MAX_BUTTON];

		joyNumber = ControllerManager.controllerID[player];

		for(int i = 0; i < GameKey.MAX_BUTTON; i++) {
			buttonmap[i] = GameKey.gamekey[player].buttonmap[i];
		}
	}

	/*
	 * ステートのInitialization
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		gameObj = game;
	}

	/*
	 * 画面描画
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.drawImage(ResourceHolder.imgMenu, 0, 0);

		NormalFont.printFontGrid(1, 1, "JOYSTICK SETTING (" + (player + 1) + "P)", NormalFont.COLOR_ORANGE);

		if(joyNumber < 0)
			NormalFont.printFontGrid(1, 3, "NO JOYSTICK", NormalFont.COLOR_RED);
		else
			NormalFont.printFontGrid(1, 3, "JOYSTICK NUMBER:" + joyNumber, NormalFont.COLOR_RED);

		//NormalFont.printFontGrid(2, 3, "UP             : " + String.valueOf(buttonmap[GameKey.BUTTON_UP]), (keynum == 0));
		//NormalFont.printFontGrid(2, 4, "DOWN           : " + String.valueOf(buttonmap[GameKey.BUTTON_DOWN]), (keynum == 1));
		//NormalFont.printFontGrid(2, 5, "LEFT           : " + String.valueOf(buttonmap[GameKey.BUTTON_LEFT]), (keynum == 2));
		//NormalFont.printFontGrid(2, 6, "RIGHT          : " + String.valueOf(buttonmap[GameKey.BUTTON_RIGHT]), (keynum == 3));
		NormalFont.printFontGrid(2, 5, "A (L/R-ROT)    : " + String.valueOf(buttonmap[GameKey.BUTTON_A]), (keynum == 4));
		NormalFont.printFontGrid(2, 6, "B (R/L-ROT)    : " + String.valueOf(buttonmap[GameKey.BUTTON_B]), (keynum == 5));
		NormalFont.printFontGrid(2, 7, "C (L/R-ROT)    : " + String.valueOf(buttonmap[GameKey.BUTTON_C]), (keynum == 6));
		NormalFont.printFontGrid(2, 8, "D (HOLD)       : " + String.valueOf(buttonmap[GameKey.BUTTON_D]), (keynum == 7));
		NormalFont.printFontGrid(2, 9, "E (180-ROT)    : " + String.valueOf(buttonmap[GameKey.BUTTON_E]), (keynum == 8));
		NormalFont.printFontGrid(2, 10, "F              : " + String.valueOf(buttonmap[GameKey.BUTTON_F]), (keynum == 9));
		NormalFont.printFontGrid(2, 11, "QUIT           : " + String.valueOf(buttonmap[GameKey.BUTTON_QUIT]), (keynum == 10));
		NormalFont.printFontGrid(2, 12, "PAUSE          : " + String.valueOf(buttonmap[GameKey.BUTTON_PAUSE]), (keynum == 11));
		NormalFont.printFontGrid(2, 13, "GIVEUP         : " + String.valueOf(buttonmap[GameKey.BUTTON_GIVEUP]), (keynum == 12));
		NormalFont.printFontGrid(2, 14, "RETRY          : " + String.valueOf(buttonmap[GameKey.BUTTON_RETRY]), (keynum == 13));
		NormalFont.printFontGrid(2, 15, "FRAME STEP     : " + String.valueOf(buttonmap[GameKey.BUTTON_FRAMESTEP]), (keynum == 14));
		NormalFont.printFontGrid(2, 16, "SCREEN SHOT    : " + String.valueOf(buttonmap[GameKey.BUTTON_SCREENSHOT]), (keynum == 15));

		NormalFont.printFontGrid(1, 5 + keynum - 4, "b", NormalFont.COLOR_RED);
		if(frame >= KEYACCEPTFRAME) {
			NormalFont.printFontGrid(1, 20, "UP/DOWN:   MOVE CURSOR", NormalFont.COLOR_GREEN);
			NormalFont.printFontGrid(1, 21, "ENTER:     OK",     NormalFont.COLOR_GREEN);
			NormalFont.printFontGrid(1, 22, "DELETE:    NO SET", NormalFont.COLOR_GREEN);
			NormalFont.printFontGrid(1, 23, "BACKSPACE: CANCEL", NormalFont.COLOR_GREEN);
		}

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

		// Joystick  button判定
		if(frame >= KEYACCEPTFRAME) {
			for(int i = 0; i < ControllerManager.MAX_BUTTONS; i++) {
				try {
					if(ControllerManager.isControllerButton(player, container.getInput(), i)) {
						ResourceHolder.soundManager.play("change");
						buttonmap[keynum] = i;
						frame = 0;
					}
				} catch (Throwable e) {}
			}
		}

		if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}

	/*
	 * キーを押したときの処理
	 */
	@Override
	public void keyPressed(int key, char c) {
		if(frame >= KEYACCEPTFRAME) {
			// Up
			if(key == Input.KEY_UP) {
				ResourceHolder.soundManager.play("cursor");
				keynum--;
				if(keynum < 4) keynum = 15;
			}
			// Down
			else if(key == Input.KEY_DOWN) {
				ResourceHolder.soundManager.play("cursor");
				keynum++;
				if(keynum > 15) keynum = 4;
			}
			// Delete
			else if(key == Input.KEY_DELETE) {
				ResourceHolder.soundManager.play("change");
				buttonmap[keynum] = -1;
			}
			// Backspace
			else if(key == Input.KEY_BACK) {
				gameObj.enterState(StateConfigJoystickMain.ID);
				return;
			}
			// Enter/Return
			else if(key == Input.KEY_ENTER) {
				ResourceHolder.soundManager.play("decide");

				for(int i = 0; i < GameKey.MAX_BUTTON; i++) {
					GameKey.gamekey[player].buttonmap[i] = buttonmap[i];
				}
				GameKey.gamekey[player].saveConfig(NullpoMinoSlick.propConfig);
				NullpoMinoSlick.saveConfig();

				gameObj.enterState(StateConfigJoystickMain.ID);
				return;
			}
		}
	}

	/**
	 * このステートに入ったときの処理
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		reset();
	}

	/**
	 * このステートを去るときの処理
	 */
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		reset();
	}
}
