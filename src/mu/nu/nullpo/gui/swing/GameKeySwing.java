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

import mu.nu.nullpo.gui.common.GameKeyDummy;

/**
 *  Key input state manager (Only use with Swing. Don't use inside game modes!)
 */
public class GameKeySwing extends GameKeyDummy {
	/** Key input state (Used by all game states) */
	public static GameKeySwing gamekey[];

	/** Default key mappings */
	public static int[][][] DEFAULTKEYS =
	{
		// Ingame
		{
			// Blockbox type
			{
				KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,
				KeyEvent.VK_Z,KeyEvent.VK_X,KeyEvent.VK_A,KeyEvent.VK_SPACE,KeyEvent.VK_D,KeyEvent.VK_S,
				KeyEvent.VK_F12,KeyEvent.VK_ESCAPE,KeyEvent.VK_F11,KeyEvent.VK_F10,KeyEvent.VK_N,KeyEvent.VK_F5
			},
			// Guideline games type
			{
				KeyEvent.VK_SPACE,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,
				KeyEvent.VK_Z,KeyEvent.VK_UP,KeyEvent.VK_C,KeyEvent.VK_SHIFT,KeyEvent.VK_X,KeyEvent.VK_V,KeyEvent.VK_F12,
				KeyEvent.VK_ESCAPE,KeyEvent.VK_F11,KeyEvent.VK_F10,KeyEvent.VK_N,KeyEvent.VK_F5
			},
			// NullpoMino classic type
			{
				KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,
				KeyEvent.VK_A,KeyEvent.VK_S,KeyEvent.VK_D,KeyEvent.VK_Z,KeyEvent.VK_X,KeyEvent.VK_C,
				KeyEvent.VK_ESCAPE,KeyEvent.VK_F1,KeyEvent.VK_F12,KeyEvent.VK_F11,KeyEvent.VK_N,KeyEvent.VK_F10
			},
		},
		// Menu
		{
			// Blockbox type
			{
				KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,
				KeyEvent.VK_ENTER,KeyEvent.VK_ESCAPE,KeyEvent.VK_A,KeyEvent.VK_SPACE,KeyEvent.VK_D,KeyEvent.VK_S,
				KeyEvent.VK_F12,KeyEvent.VK_F1,KeyEvent.VK_F11,KeyEvent.VK_F10,KeyEvent.VK_N,KeyEvent.VK_F5
			},
			// Guideline games type
			{
				KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,
				KeyEvent.VK_ENTER,KeyEvent.VK_ESCAPE,KeyEvent.VK_C,KeyEvent.VK_SHIFT,KeyEvent.VK_X,KeyEvent.VK_V,
				KeyEvent.VK_F12,KeyEvent.VK_F1,KeyEvent.VK_F11,KeyEvent.VK_F10,KeyEvent.VK_N,KeyEvent.VK_F5
			},
			// NullpoMino classic type
			{
				KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,
				KeyEvent.VK_A,KeyEvent.VK_S,KeyEvent.VK_D,KeyEvent.VK_Z,KeyEvent.VK_X,KeyEvent.VK_C,
				KeyEvent.VK_ESCAPE,KeyEvent.VK_F1,KeyEvent.VK_F12,KeyEvent.VK_F11,KeyEvent.VK_N,KeyEvent.VK_F10
			},
		},
	};

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
		loadDefaultGameKeymap(type);
		loadDefaultMenuKeymap(type);
	}

	/**
	 * Reset in-game keyboard settings to default. Menu keys are unchanged.
	 * @param type Settings type (0=Blockbox 1=Guideline 2=NullpoMino-Classic)
	 */
	public void loadDefaultGameKeymap(int type) {
		for(int i = 0; i < keymap.length; i++) {
			keymap[i] = DEFAULTKEYS[0][type][i];
		}
	}

	/**
	 * Reset menu keyboard settings to default. In-game keys are unchanged.
	 * @param type Settings type (0=Blockbox 1=Guideline 2=NullpoMino-Classic)
	 */
	public void loadDefaultMenuKeymap(int type) {
		for(int i = 0; i < keymapNav.length; i++) {
			keymapNav[i] = DEFAULTKEYS[1][type][i];
		}
	}
}
