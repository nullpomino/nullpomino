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

import mu.nu.nullpo.gui.GameKeyDummy;
import mu.nu.nullpo.util.CustomProperties;
import sdljava.event.SDLKey;
import sdljava.joystick.HatState;

/**
 * Key input state manager for SDL
 */
public class GameKeySDL extends GameKeyDummy {
	/** Key input state (Used by all game states) */
	public static GameKeySDL gamekey[];

	/**
	 * Init everything
	 */
	public static void initGlobalGameKeySDL() {
		gamekey = new GameKeySDL[2];
		gamekey[0] = new GameKeySDL(0);
		gamekey[1] = new GameKeySDL(1);
	}

	/**
	 * Default constructor
	 */
	public GameKeySDL() {
		super();
	}

	/**
	 * Constructor with player number param
	 * @param pl Player number
	 */
	public GameKeySDL(int pl) {
		super(pl);
	}

	/**
	 * Update button input status
	 * @param keyboard Keyboard input array
	 */
	public void update(boolean[] keyboard) {
		update(keyboard, null, 0, 0, null, false);
	}

	/**
	 * Update button input status
	 * @param keyboard Keyboard input array
	 * @param ingame true if ingame
	 */
	public void update(boolean[] keyboard, boolean ingame) {
		update(keyboard, null, 0, 0, null, ingame);
	}

	/**
	 * Update button input status
	 * @param keyboard Keyboard input array
	 * @param joyButton Joystick button input array (Can be null)
	 * @param joyX Joystick X
	 * @param joyY Joystick Y
	 * @param hat Joystick HatState (Can be null)
	 */
	public void update(boolean[] keyboard, boolean[] joyButton, int joyX, int joyY, HatState hat) {
		update(keyboard, joyButton, joyX, joyY, hat, false);
	}

	/**
	 * Update button input status
	 * @param keyboard Keyboard input array
	 * @param joyButton Joystick button input array (Can be null)
	 * @param joyX Joystick X
	 * @param joyY Joystick Y
	 * @param hat Joystick HatState (Can be null)
	 * @param ingame true if ingame
	 */
	public void update(boolean[] keyboard, boolean[] joyButton, int joyX, int joyY, HatState hat, boolean ingame) {
		for(int i = 0; i < MAX_BUTTON; i++) {
			int[] kmap = ingame ? keymap : keymapNav;
			boolean flag = keyboard[kmap[i]];

			if(i == BUTTON_UP) {
				// Up
				if( (flag) || (joyY < -joyBorder) || ((hat != null) && (hat.hatUp())) ) {
					inputstate[i]++;
				} else {
					inputstate[i] = 0;
				}
			} else if(i == BUTTON_DOWN) {
				// Down
				if( (flag) || (joyY > joyBorder) || ((hat != null) && (hat.hatDown())) ) {
					inputstate[i]++;
				} else {
					inputstate[i] = 0;
				}
			} else if(i == BUTTON_LEFT) {
				// Left
				if((flag) || (joyX < -joyBorder) || ((hat != null) && (hat.hatLeft())) ) {
					inputstate[i]++;
				} else {
					inputstate[i] = 0;
				}
			} else if(i == BUTTON_RIGHT) {
				// Right
				if((flag) || (joyX > joyBorder) || ((hat != null) && (hat.hatRight())) ) {
					inputstate[i]++;
				} else {
					inputstate[i] = 0;
				}
			} else {
				// Misc buttons
				boolean flag2 = false;

				if(joyButton != null) {
					try {
						flag2 = joyButton[buttonmap[i]];
					} catch (ArrayIndexOutOfBoundsException e) {}
				}

				if((flag) || (flag2)) {
					inputstate[i]++;
				} else {
					inputstate[i] = 0;
				}
			}
		}
	}

	/**
	 * Load key settings
	 * @param prop Property file to read from
	 */
	public void loadConfig(CustomProperties prop) {
		super.loadConfig(prop);
		joyBorder = prop.getProperty("joyBorder.p" + player, 0);
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
			keymap[BUTTON_UP]         = SDLKey.SDLK_UP;		// Up
			keymap[BUTTON_DOWN]       = SDLKey.SDLK_DOWN;	// Down
			keymap[BUTTON_LEFT]       = SDLKey.SDLK_LEFT;	// Left
			keymap[BUTTON_RIGHT]      = SDLKey.SDLK_RIGHT;	// Right
			keymap[BUTTON_A]          = SDLKey.SDLK_z;		// Z
			keymap[BUTTON_B]          = SDLKey.SDLK_x;		// X
			keymap[BUTTON_C]          = SDLKey.SDLK_a;		// A
			keymap[BUTTON_D]          = SDLKey.SDLK_SPACE;	// Space
			keymap[BUTTON_E]          = SDLKey.SDLK_d;		// D
			keymap[BUTTON_F]          = SDLKey.SDLK_s;		// S
			keymap[BUTTON_QUIT]       = SDLKey.SDLK_F12;	// F12
			keymap[BUTTON_PAUSE]      = SDLKey.SDLK_ESCAPE;	// Escape
			keymap[BUTTON_GIVEUP]     = SDLKey.SDLK_F11;	// F11
			keymap[BUTTON_RETRY]      = SDLKey.SDLK_F10;	// F10
			keymap[BUTTON_FRAMESTEP]  = SDLKey.SDLK_n;		// N
			keymap[BUTTON_SCREENSHOT] = SDLKey.SDLK_F5;		// F5

			// Menu
			keymapNav[BUTTON_UP]         = SDLKey.SDLK_UP;		// Up
			keymapNav[BUTTON_DOWN]       = SDLKey.SDLK_DOWN;	// Down
			keymapNav[BUTTON_LEFT]       = SDLKey.SDLK_LEFT;	// Left
			keymapNav[BUTTON_RIGHT]      = SDLKey.SDLK_RIGHT;	// Right
			keymapNav[BUTTON_A]          = SDLKey.SDLK_RETURN;	// Return
			keymapNav[BUTTON_B]          = SDLKey.SDLK_ESCAPE;	// Escape
			keymapNav[BUTTON_C]          = SDLKey.SDLK_a;		// A
			keymapNav[BUTTON_D]          = SDLKey.SDLK_SPACE;	// Space
			keymapNav[BUTTON_E]          = SDLKey.SDLK_d;		// D
			keymapNav[BUTTON_F]          = SDLKey.SDLK_s;		// S
			keymapNav[BUTTON_QUIT]       = SDLKey.SDLK_F12;		// F12
			keymapNav[BUTTON_PAUSE]      = SDLKey.SDLK_F1;		// F1
			keymapNav[BUTTON_GIVEUP]     = SDLKey.SDLK_F11;		// F11
			keymapNav[BUTTON_RETRY]      = SDLKey.SDLK_F10;		// F10
			keymapNav[BUTTON_FRAMESTEP]  = SDLKey.SDLK_n;		// N
			keymapNav[BUTTON_SCREENSHOT] = SDLKey.SDLK_F5;		// F5
		}
		// Guideline games type
		if(type == 1) {
			// Ingame
			keymap[BUTTON_UP]         = SDLKey.SDLK_SPACE;
			keymap[BUTTON_DOWN]       = SDLKey.SDLK_DOWN;
			keymap[BUTTON_LEFT]       = SDLKey.SDLK_LEFT;
			keymap[BUTTON_RIGHT]      = SDLKey.SDLK_RIGHT;
			keymap[BUTTON_A]          = SDLKey.SDLK_z;
			keymap[BUTTON_B]          = SDLKey.SDLK_UP;
			keymap[BUTTON_C]          = SDLKey.SDLK_x;
			keymap[BUTTON_D]          = SDLKey.SDLK_LSHIFT;
			keymap[BUTTON_E]          = SDLKey.SDLK_c;
			keymap[BUTTON_F]          = SDLKey.SDLK_v;
			keymap[BUTTON_QUIT]       = SDLKey.SDLK_F12;
			keymap[BUTTON_PAUSE]      = SDLKey.SDLK_ESCAPE;
			keymap[BUTTON_GIVEUP]     = SDLKey.SDLK_F11;
			keymap[BUTTON_RETRY]      = SDLKey.SDLK_F10;
			keymap[BUTTON_FRAMESTEP]  = SDLKey.SDLK_n;
			keymap[BUTTON_SCREENSHOT] = SDLKey.SDLK_F5;

			// Menu
			keymapNav[BUTTON_UP]         = SDLKey.SDLK_UP;
			keymapNav[BUTTON_DOWN]       = SDLKey.SDLK_DOWN;
			keymapNav[BUTTON_LEFT]       = SDLKey.SDLK_LEFT;
			keymapNav[BUTTON_RIGHT]      = SDLKey.SDLK_RIGHT;
			keymapNav[BUTTON_A]          = SDLKey.SDLK_RETURN;
			keymapNav[BUTTON_B]          = SDLKey.SDLK_ESCAPE;
			keymapNav[BUTTON_C]          = SDLKey.SDLK_x;
			keymapNav[BUTTON_D]          = SDLKey.SDLK_LSHIFT;
			keymapNav[BUTTON_E]          = SDLKey.SDLK_c;
			keymapNav[BUTTON_F]          = SDLKey.SDLK_v;
			keymapNav[BUTTON_QUIT]       = SDLKey.SDLK_F12;
			keymapNav[BUTTON_PAUSE]      = SDLKey.SDLK_F1;
			keymapNav[BUTTON_GIVEUP]     = SDLKey.SDLK_F11;
			keymapNav[BUTTON_RETRY]      = SDLKey.SDLK_F10;
			keymapNav[BUTTON_FRAMESTEP]  = SDLKey.SDLK_n;
			keymapNav[BUTTON_SCREENSHOT] = SDLKey.SDLK_F5;
		}
		// NullpoMino classic type
		if(type == 2) {
			// Ingame
			keymap[BUTTON_UP]         = SDLKey.SDLK_UP;
			keymap[BUTTON_DOWN]       = SDLKey.SDLK_DOWN;
			keymap[BUTTON_LEFT]       = SDLKey.SDLK_LEFT;
			keymap[BUTTON_RIGHT]      = SDLKey.SDLK_RIGHT;
			keymap[BUTTON_A]          = SDLKey.SDLK_a;
			keymap[BUTTON_B]          = SDLKey.SDLK_s;
			keymap[BUTTON_C]          = SDLKey.SDLK_d;
			keymap[BUTTON_D]          = SDLKey.SDLK_z;
			keymap[BUTTON_E]          = SDLKey.SDLK_x;
			keymap[BUTTON_F]          = SDLKey.SDLK_c;
			keymap[BUTTON_QUIT]       = SDLKey.SDLK_ESCAPE;
			keymap[BUTTON_PAUSE]      = SDLKey.SDLK_F1;
			keymap[BUTTON_GIVEUP]     = SDLKey.SDLK_F12;
			keymap[BUTTON_RETRY]      = SDLKey.SDLK_F11;
			keymap[BUTTON_FRAMESTEP]  = SDLKey.SDLK_n;
			keymap[BUTTON_SCREENSHOT] = SDLKey.SDLK_F10;

			// Menu
			keymapNav[BUTTON_UP]         = SDLKey.SDLK_UP;
			keymapNav[BUTTON_DOWN]       = SDLKey.SDLK_DOWN;
			keymapNav[BUTTON_LEFT]       = SDLKey.SDLK_LEFT;
			keymapNav[BUTTON_RIGHT]      = SDLKey.SDLK_RIGHT;
			keymapNav[BUTTON_A]          = SDLKey.SDLK_a;
			keymapNav[BUTTON_B]          = SDLKey.SDLK_s;
			keymapNav[BUTTON_C]          = SDLKey.SDLK_d;
			keymapNav[BUTTON_D]          = SDLKey.SDLK_z;
			keymapNav[BUTTON_E]          = SDLKey.SDLK_x;
			keymapNav[BUTTON_F]          = SDLKey.SDLK_c;
			keymapNav[BUTTON_QUIT]       = SDLKey.SDLK_ESCAPE;
			keymapNav[BUTTON_PAUSE]      = SDLKey.SDLK_F1;
			keymapNav[BUTTON_GIVEUP]     = SDLKey.SDLK_F12;
			keymapNav[BUTTON_RETRY]      = SDLKey.SDLK_F11;
			keymapNav[BUTTON_FRAMESTEP]  = SDLKey.SDLK_n;
			keymapNav[BUTTON_SCREENSHOT] = SDLKey.SDLK_F10;
		}
	}
}
