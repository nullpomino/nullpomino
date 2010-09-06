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
package mu.nu.nullpo.gui.swing;

import mu.nu.nullpo.gui.GameKeyDummy;

/**
 * Key input stateの管理
 */
public class GameKeySwing extends GameKeyDummy {
	/** Key input state (全ステート共通) */
	public static GameKeySwing gamekey[];

	/** Button number定count */
	public static final int BUTTON_UP = 0, BUTTON_DOWN = 1, BUTTON_LEFT = 2, BUTTON_RIGHT = 3, BUTTON_A = 4, BUTTON_B = 5, BUTTON_C = 6,
			BUTTON_D = 7, BUTTON_E = 8, BUTTON_F = 9, BUTTON_PAUSE = 10, BUTTON_QUIT = 11, BUTTON_RETRY = 12,
			BUTTON_FRAMESTEP = 13, BUTTON_SCREENSHOT = 14;

	/** Buttoncountの定count */
	public static final int MAX_BUTTON = 15;

	/**
	 * 全ステート共通のKey input stateオブジェクトをInitialization
	 */
	public static void initGlobalGameKeySwing() {
		gamekey = new GameKeySwing[2];
		gamekey[0] = new GameKeySwing(0);
		gamekey[1] = new GameKeySwing(1);
	}

	/**
	 *  default Constructor
	 */
	public GameKeySwing() {
		super();

	}

	/**
	 * Player numberを指定できるConstructor
	 * @param pl Player number
	 */
	public GameKeySwing(int pl) {
		super(pl);
	}

	/**
	 * Update button pressed times (run once per frame)
	 */
	public void update() {
		for(int i = 0; i < MAX_BUTTON; i++) {
			if(pressstate[i]) inputstate[i]++;
			else inputstate[i] = 0;
		}
	}

	/**
	 * Clear button input state
	 */
	public void clear() {
		for(int i = 0; i < MAX_BUTTON; i++) {
			inputstate[i] = 0;
			pressstate[i] = false;
		}
	}

	/**
	 *  buttonの input 状態を設定
	 * @param key Button number
	 * @param pressed 押している場合はtrue, 離した場合はfalse
	 */
	public void setPressState(int key, boolean pressed) {
		if(!pressed) {
			pressstate[key] = false;
		} else {
			pressstate[key] = true;
		}
	}
}
