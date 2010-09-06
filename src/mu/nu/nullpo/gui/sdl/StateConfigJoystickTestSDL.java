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
import sdljava.joystick.HatState;
import sdljava.video.SDLSurface;

/**
 * Joystick テスト画面のステート
 */
public class StateConfigJoystickTestSDL extends BaseStateSDL {
	/** キー input を受付可能になるまでの frame count */
	public static final int KEYACCEPTFRAME = 20;

	/** Player number */
	public int player;

	/** 使用するJoystick の number */
	protected int joyNumber;

	/** 最後に押された button */
	protected int lastPressButton;

	/** 経過 frame count */
	protected int frame;

	/** 前の frame のJoystick の input 状態 */
	protected boolean previousJoyPressedState[];

	/**
	 * Constructor
	 */
	public StateConfigJoystickTestSDL() {
		player = 0;
	}

	/**
	 * いろいろリセット
	 */
	protected void reset() {
		joyNumber = NullpoMinoSDL.joyUseNumber[player];
		lastPressButton = -1;
		frame = 0;
		if(joyNumber >= 0) previousJoyPressedState = new boolean[NullpoMinoSDL.joyMaxButton[joyNumber]];
		else previousJoyPressedState = null;
	}

	/**
	 * 押された buttonの numberを返す
	 * @param prev 前の frame での input 状態
	 * @param now この frame での input 状態
	 * @return 押された buttonの number、無いなら-1
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
	 * 画面描画
	 */
	@Override
	public void render(SDLSurface screen) throws SDLException {
		ResourceHolderSDL.imgMenu.blitSurface(screen);

		NormalFontSDL.printFontGrid(1, 1, "JOYSTICK INPUT TEST (" + (player + 1) + "P)", NormalFontSDL.COLOR_ORANGE);

		if(joyNumber < 0) {
			NormalFontSDL.printFontGrid(1, 3, "NO JOYSTICK", NormalFontSDL.COLOR_RED);
		} else if(frame >= KEYACCEPTFRAME) {
			NormalFontSDL.printFontGrid(1, 3, "JOYSTICK NUMBER:" + joyNumber, NormalFontSDL.COLOR_RED);

			NormalFontSDL.printFontGrid(1, 5, "LAST PRESSED BUTTON:" + ((lastPressButton == -1) ? "NONE" : String.valueOf(lastPressButton)));

			NormalFontSDL.printFontGrid(1, 7, "AXIS X:" + NullpoMinoSDL.joyAxisX[joyNumber]);
			NormalFontSDL.printFontGrid(1, 8, "AXIS Y:" + NullpoMinoSDL.joyAxisY[joyNumber]);

			String strHat = "";
			HatState hat = NullpoMinoSDL.joyHatState[joyNumber];
			if(hat != null) {
				if(hat.hatCentered()) strHat += "CENTER ";
				if(hat.hatUp()) strHat += "UP ";
				if(hat.hatDown()) strHat += "DOWN ";
				if(hat.hatLeft()) strHat += "LEFT ";
				if(hat.hatRight()) strHat += "RIGHT ";
			}
			NormalFontSDL.printFontGrid(1, 10, "POV:" + strHat);
		}

		if(frame >= KEYACCEPTFRAME) {
			NormalFontSDL.printFontGrid(1, 23, "ENTER/BACKSPACE: EXIT", NormalFontSDL.COLOR_GREEN);
		}
	}

	/*
	 * Update game state
	 */
	@Override
	public void update() throws SDLException {
		if(frame >= KEYACCEPTFRAME) {
			// Backspace & Enter/Return
			if(NullpoMinoSDL.keyPressedState[SDLKey.SDLK_BACKSPACE] || NullpoMinoSDL.keyPressedState[SDLKey.SDLK_RETURN]) {
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_JOYSTICK_MAIN);
				return;
			}
			// Joystick  input 
			else if(previousJoyPressedState != null) {
				int key = getPressedKeyNumber(previousJoyPressedState, NullpoMinoSDL.joyPressedState[joyNumber]);

				if(key != -1) {
					ResourceHolderSDL.soundManager.play("change");
					lastPressButton = key;
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
	 * このステートに入ったときの処理
	 */
	@Override
	public void enter() throws SDLException {
		reset();
		NullpoMinoSDL.enableSpecialKeys = false;
	}

	/*
	 * このステートを去るときの処理
	 */
	@Override
	public void leave() throws SDLException {
		reset();
		NullpoMinoSDL.enableSpecialKeys = true;
	}
}
