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

	/** Default key mappings */
	public static int[][][] DEFAULTKEYS =
	{
		// Ingame
		{
			// Blockbox type
			{
				SDLKey.SDLK_UP,SDLKey.SDLK_DOWN,SDLKey.SDLK_LEFT,SDLKey.SDLK_RIGHT,
				SDLKey.SDLK_z,SDLKey.SDLK_x,SDLKey.SDLK_a,SDLKey.SDLK_SPACE,SDLKey.SDLK_d,SDLKey.SDLK_s,
				SDLKey.SDLK_F12,SDLKey.SDLK_ESCAPE,SDLKey.SDLK_F11,SDLKey.SDLK_F10,SDLKey.SDLK_n,SDLKey.SDLK_F5
			},
			// Guideline games type
			{
				SDLKey.SDLK_SPACE,SDLKey.SDLK_DOWN,SDLKey.SDLK_LEFT,SDLKey.SDLK_RIGHT,
				SDLKey.SDLK_z,SDLKey.SDLK_UP,SDLKey.SDLK_c,SDLKey.SDLK_LSHIFT,SDLKey.SDLK_x,SDLKey.SDLK_v,SDLKey.SDLK_F12,
				SDLKey.SDLK_ESCAPE,SDLKey.SDLK_F11,SDLKey.SDLK_F10,SDLKey.SDLK_n,SDLKey.SDLK_F5
			},
			// NullpoMino classic type
			{
				SDLKey.SDLK_UP,SDLKey.SDLK_DOWN,SDLKey.SDLK_LEFT,SDLKey.SDLK_RIGHT,
				SDLKey.SDLK_a,SDLKey.SDLK_s,SDLKey.SDLK_d,SDLKey.SDLK_z,SDLKey.SDLK_x,SDLKey.SDLK_c,
				SDLKey.SDLK_ESCAPE,SDLKey.SDLK_F1,SDLKey.SDLK_F12,SDLKey.SDLK_F11,SDLKey.SDLK_n,SDLKey.SDLK_F10
			},
		},
		// Menu
		{
			// Blockbox type
			{
				SDLKey.SDLK_UP,SDLKey.SDLK_DOWN,SDLKey.SDLK_LEFT,SDLKey.SDLK_RIGHT,
				SDLKey.SDLK_RETURN,SDLKey.SDLK_ESCAPE,SDLKey.SDLK_a,SDLKey.SDLK_SPACE,SDLKey.SDLK_d,SDLKey.SDLK_s,
				SDLKey.SDLK_F12,SDLKey.SDLK_F1,SDLKey.SDLK_F11,SDLKey.SDLK_F10,SDLKey.SDLK_n,SDLKey.SDLK_F5
			},
			// Guideline games type
			{
				SDLKey.SDLK_UP,SDLKey.SDLK_DOWN,SDLKey.SDLK_LEFT,SDLKey.SDLK_RIGHT,
				SDLKey.SDLK_RETURN,SDLKey.SDLK_ESCAPE,SDLKey.SDLK_c,SDLKey.SDLK_LSHIFT,SDLKey.SDLK_x,SDLKey.SDLK_v,
				SDLKey.SDLK_F12,SDLKey.SDLK_F1,SDLKey.SDLK_F11,SDLKey.SDLK_F10,SDLKey.SDLK_n,SDLKey.SDLK_F5
			},
			// NullpoMino classic type
			{
				SDLKey.SDLK_UP,SDLKey.SDLK_DOWN,SDLKey.SDLK_LEFT,SDLKey.SDLK_RIGHT,
				SDLKey.SDLK_a,SDLKey.SDLK_s,SDLKey.SDLK_d,SDLKey.SDLK_z,SDLKey.SDLK_x,SDLKey.SDLK_c,
				SDLKey.SDLK_ESCAPE,SDLKey.SDLK_F1,SDLKey.SDLK_F12,SDLKey.SDLK_F11,SDLKey.SDLK_n,SDLKey.SDLK_F10
			},
		},
	};

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
