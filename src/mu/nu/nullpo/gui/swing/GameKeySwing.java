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

import java.awt.event.KeyEvent;

import mu.nu.nullpo.gui.GameKeyDummy;

/**
 *  Key input state manager (Only use with Swing. Don't use inside game modes!)
 */
public class GameKeySwing extends GameKeyDummy {
	/** Key input state (Used by all game states) */
	public static GameKeySwing gamekey[];

	/**
	 * Init everything
	 */
	public static void initGlobalGameKeySwing() {
		gamekey = new GameKeySwing[2];
		gamekey[0] = new GameKeySwing(0);
		gamekey[1] = new GameKeySwing(1);
	}

	/**
	 * Default constructor
	 */
	public GameKeySwing() {
		super();
	}

	/**
	 * Constructor with player number param
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
	 * Set button pressed state
	 * @param key Button number
	 * @param pressed true when pressed, false otherwise
	 */
	public void setPressState(int key, boolean pressed) {
		if(!pressed) {
			pressstate[key] = false;
		} else {
			pressstate[key] = true;
		}
	}

	/**
	 * Reset keyboard settings to default (Uses Blockbox type settings)
	 */
	public void loadDefaultKeymap() {
		loadDefaultKeymap(0);
	}

	/**
	 * Reset keyboard settings to default
	 * @param type Settings type (0=Blockbox 1=Guideline 2=NullpoMino-Classic)
	 */
	public void loadDefaultKeymap(int type) {
		// Blockbox type
		if(type == 0) {
			// Ingame
			keymap[BUTTON_UP]         = KeyEvent.VK_UP;
			keymap[BUTTON_DOWN]       = KeyEvent.VK_DOWN;
			keymap[BUTTON_LEFT]       = KeyEvent.VK_LEFT;
			keymap[BUTTON_RIGHT]      = KeyEvent.VK_RIGHT;
			keymap[BUTTON_A]          = KeyEvent.VK_Z;
			keymap[BUTTON_B]          = KeyEvent.VK_X;
			keymap[BUTTON_C]          = KeyEvent.VK_A;
			keymap[BUTTON_D]          = KeyEvent.VK_SPACE;
			keymap[BUTTON_E]          = KeyEvent.VK_D;
			keymap[BUTTON_F]          = KeyEvent.VK_S;
			keymap[BUTTON_QUIT]       = KeyEvent.VK_F12;
			keymap[BUTTON_PAUSE]      = KeyEvent.VK_ESCAPE;
			keymap[BUTTON_GIVEUP]     = KeyEvent.VK_F11;
			keymap[BUTTON_RETRY]      = KeyEvent.VK_F10;
			keymap[BUTTON_FRAMESTEP]  = KeyEvent.VK_N;
			keymap[BUTTON_SCREENSHOT] = KeyEvent.VK_F5;

			// Menu
			keymapNav[BUTTON_UP]         = KeyEvent.VK_UP;
			keymapNav[BUTTON_DOWN]       = KeyEvent.VK_DOWN;
			keymapNav[BUTTON_LEFT]       = KeyEvent.VK_LEFT;
			keymapNav[BUTTON_RIGHT]      = KeyEvent.VK_RIGHT;
			keymapNav[BUTTON_A]          = KeyEvent.VK_ENTER;
			keymapNav[BUTTON_B]          = KeyEvent.VK_ESCAPE;
			keymapNav[BUTTON_C]          = KeyEvent.VK_A;
			keymapNav[BUTTON_D]          = KeyEvent.VK_SPACE;
			keymapNav[BUTTON_E]          = KeyEvent.VK_D;
			keymapNav[BUTTON_F]          = KeyEvent.VK_S;
			keymapNav[BUTTON_QUIT]       = KeyEvent.VK_F12;
			keymapNav[BUTTON_PAUSE]      = KeyEvent.VK_F1;
			keymapNav[BUTTON_GIVEUP]     = KeyEvent.VK_F11;
			keymapNav[BUTTON_RETRY]      = KeyEvent.VK_F10;
			keymapNav[BUTTON_FRAMESTEP]  = KeyEvent.VK_N;
			keymapNav[BUTTON_SCREENSHOT] = KeyEvent.VK_F5;
		}
		// Guideline games type
		if(type == 1) {
			// Ingame
			keymap[BUTTON_UP]         = KeyEvent.VK_SPACE;
			keymap[BUTTON_DOWN]       = KeyEvent.VK_DOWN;
			keymap[BUTTON_LEFT]       = KeyEvent.VK_LEFT;
			keymap[BUTTON_RIGHT]      = KeyEvent.VK_RIGHT;
			keymap[BUTTON_A]          = KeyEvent.VK_Z;
			keymap[BUTTON_B]          = KeyEvent.VK_UP;
			keymap[BUTTON_C]          = KeyEvent.VK_X;
			keymap[BUTTON_D]          = KeyEvent.VK_SHIFT;
			keymap[BUTTON_E]          = KeyEvent.VK_C;
			keymap[BUTTON_F]          = KeyEvent.VK_V;
			keymap[BUTTON_QUIT]       = KeyEvent.VK_F12;
			keymap[BUTTON_PAUSE]      = KeyEvent.VK_ESCAPE;
			keymap[BUTTON_GIVEUP]     = KeyEvent.VK_F11;
			keymap[BUTTON_RETRY]      = KeyEvent.VK_F10;
			keymap[BUTTON_FRAMESTEP]  = KeyEvent.VK_N;
			keymap[BUTTON_SCREENSHOT] = KeyEvent.VK_F5;

			// Menu
			keymapNav[BUTTON_UP]         = KeyEvent.VK_UP;
			keymapNav[BUTTON_DOWN]       = KeyEvent.VK_DOWN;
			keymapNav[BUTTON_LEFT]       = KeyEvent.VK_LEFT;
			keymapNav[BUTTON_RIGHT]      = KeyEvent.VK_RIGHT;
			keymapNav[BUTTON_A]          = KeyEvent.VK_ENTER;
			keymapNav[BUTTON_B]          = KeyEvent.VK_ESCAPE;
			keymapNav[BUTTON_C]          = KeyEvent.VK_X;
			keymapNav[BUTTON_D]          = KeyEvent.VK_SHIFT;
			keymapNav[BUTTON_E]          = KeyEvent.VK_C;
			keymapNav[BUTTON_F]          = KeyEvent.VK_V;
			keymapNav[BUTTON_QUIT]       = KeyEvent.VK_F12;
			keymapNav[BUTTON_PAUSE]      = KeyEvent.VK_F1;
			keymapNav[BUTTON_GIVEUP]     = KeyEvent.VK_F11;
			keymapNav[BUTTON_RETRY]      = KeyEvent.VK_F10;
			keymapNav[BUTTON_FRAMESTEP]  = KeyEvent.VK_N;
			keymapNav[BUTTON_SCREENSHOT] = KeyEvent.VK_F5;
		}
		// NullpoMino classic type
		if(type == 2) {
			// Ingame
			keymap[BUTTON_UP]         = KeyEvent.VK_UP;
			keymap[BUTTON_DOWN]       = KeyEvent.VK_DOWN;
			keymap[BUTTON_LEFT]       = KeyEvent.VK_LEFT;
			keymap[BUTTON_RIGHT]      = KeyEvent.VK_RIGHT;
			keymap[BUTTON_A]          = KeyEvent.VK_A;
			keymap[BUTTON_B]          = KeyEvent.VK_S;
			keymap[BUTTON_C]          = KeyEvent.VK_D;
			keymap[BUTTON_D]          = KeyEvent.VK_Z;
			keymap[BUTTON_E]          = KeyEvent.VK_X;
			keymap[BUTTON_F]          = KeyEvent.VK_C;
			keymap[BUTTON_QUIT]       = KeyEvent.VK_ESCAPE;
			keymap[BUTTON_PAUSE]      = KeyEvent.VK_F1;
			keymap[BUTTON_GIVEUP]     = KeyEvent.VK_F12;
			keymap[BUTTON_RETRY]      = KeyEvent.VK_F11;
			keymap[BUTTON_FRAMESTEP]  = KeyEvent.VK_N;
			keymap[BUTTON_SCREENSHOT] = KeyEvent.VK_F10;

			// Menu
			keymapNav[BUTTON_UP]         = KeyEvent.VK_UP;
			keymapNav[BUTTON_DOWN]       = KeyEvent.VK_DOWN;
			keymapNav[BUTTON_LEFT]       = KeyEvent.VK_LEFT;
			keymapNav[BUTTON_RIGHT]      = KeyEvent.VK_RIGHT;
			keymapNav[BUTTON_A]          = KeyEvent.VK_A;
			keymapNav[BUTTON_B]          = KeyEvent.VK_S;
			keymapNav[BUTTON_C]          = KeyEvent.VK_D;
			keymapNav[BUTTON_D]          = KeyEvent.VK_Z;
			keymapNav[BUTTON_E]          = KeyEvent.VK_X;
			keymapNav[BUTTON_F]          = KeyEvent.VK_C;
			keymapNav[BUTTON_QUIT]       = KeyEvent.VK_ESCAPE;
			keymapNav[BUTTON_PAUSE]      = KeyEvent.VK_F1;
			keymapNav[BUTTON_GIVEUP]     = KeyEvent.VK_F12;
			keymapNav[BUTTON_RETRY]      = KeyEvent.VK_F11;
			keymapNav[BUTTON_FRAMESTEP]  = KeyEvent.VK_N;
			keymapNav[BUTTON_SCREENSHOT] = KeyEvent.VK_F10;
		}
	}
}
