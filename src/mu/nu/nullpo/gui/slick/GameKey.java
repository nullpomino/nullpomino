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

import mu.nu.nullpo.gui.GameKeyDummy;
import mu.nu.nullpo.util.CustomProperties;

import org.newdawn.slick.Input;

/**
 * Key input state manager (Only use with Slick. Don't use inside game modes!)
 */
public class GameKey extends GameKeyDummy {
	/** Key input state (Used by all game states) */
	public static GameKey[] gamekey;

	/** Default key mappings */
	public static int[][][] DEFAULTKEYS =
	{
		// Ingame
		{
			// Blockbox type
			{
				Input.KEY_UP,Input.KEY_DOWN,Input.KEY_LEFT,Input.KEY_RIGHT,
				Input.KEY_Z,Input.KEY_X,Input.KEY_A,Input.KEY_SPACE,Input.KEY_D,Input.KEY_S,
				Input.KEY_F12,Input.KEY_ESCAPE,Input.KEY_F11,Input.KEY_F10,Input.KEY_N,Input.KEY_F5
			},
			// Guideline games type
			{
				Input.KEY_SPACE,Input.KEY_DOWN,Input.KEY_LEFT,Input.KEY_RIGHT,
				Input.KEY_Z,Input.KEY_UP,Input.KEY_X,Input.KEY_LSHIFT,Input.KEY_C,Input.KEY_V,Input.KEY_F12,
				Input.KEY_ESCAPE,Input.KEY_F11,Input.KEY_F10,Input.KEY_N,Input.KEY_F5
			},
			// NullpoMino classic type
			{
				Input.KEY_UP,Input.KEY_DOWN,Input.KEY_LEFT,Input.KEY_RIGHT,
				Input.KEY_A,Input.KEY_S,Input.KEY_D,Input.KEY_Z,Input.KEY_X,Input.KEY_C,
				Input.KEY_ESCAPE,Input.KEY_F1,Input.KEY_F12,Input.KEY_F11,Input.KEY_N,Input.KEY_F10
			},
		},
		// Menu
		{
			// Blockbox type
			{
				Input.KEY_UP,Input.KEY_DOWN,Input.KEY_LEFT,Input.KEY_RIGHT,
				Input.KEY_ENTER,Input.KEY_ESCAPE,Input.KEY_A,Input.KEY_SPACE,Input.KEY_D,Input.KEY_S,
				Input.KEY_F12,Input.KEY_F1,Input.KEY_F11,Input.KEY_F10,Input.KEY_N,Input.KEY_F5
			},
			// Guideline games type
			{
				Input.KEY_UP,Input.KEY_DOWN,Input.KEY_LEFT,Input.KEY_RIGHT,
				Input.KEY_ENTER,Input.KEY_ESCAPE,Input.KEY_X,Input.KEY_LSHIFT,Input.KEY_C,Input.KEY_V,
				Input.KEY_F12,Input.KEY_F1,Input.KEY_F11,Input.KEY_F10,Input.KEY_N,Input.KEY_F5
			},
			// NullpoMino classic type
			{
				Input.KEY_UP,Input.KEY_DOWN,Input.KEY_LEFT,Input.KEY_RIGHT,
				Input.KEY_A,Input.KEY_S,Input.KEY_D,Input.KEY_Z,Input.KEY_X,Input.KEY_C,
				Input.KEY_ESCAPE,Input.KEY_F1,Input.KEY_F12,Input.KEY_F11,Input.KEY_N,Input.KEY_F10
			},
		},
	};

	/**
	 * Init everything
	 */
	public static void initGlobalGameKey() {
		ControllerManager.initControllers();
		JInputManager.initKeymap();
		JInputManager.initKeyboard();
		gamekey = new GameKey[2];
		gamekey[0] = new GameKey(0);
		gamekey[1] = new GameKey(1);
	}

	/**
	 * Default constructor
	 */
	public GameKey() {
		super();
	}

	/**
	 * Constructor with player number param
	 * @param pl Player number
	 */
	public GameKey(int pl) {
		super(pl);
	}

	/**
	 * Update button input status
	 * @param input Slick's Input class (You can get it with container.getInput())
	 */
	public void update(Input input) {
		update(input, false);
	}

	/**
	 * Update button input status
	 * @param input Slick's Input class (You can get it with container.getInput())
	 * @param ingame true if ingame
	 */
	public void update(Input input, boolean ingame) {
		if((player == 0) && (NullpoMinoSlick.useJInputKeyboard)) {
			JInputManager.poll();
		}

		for(int i = 0; i < MAX_BUTTON; i++) {
			int[] kmap = ingame ? keymap : keymapNav;

			boolean flag = NullpoMinoSlick.useJInputKeyboard ?
								JInputManager.isKeyDown(kmap[i]) : input.isKeyDown(kmap[i]);

			switch(i) {
			case BUTTON_UP:
				flag |= ControllerManager.isControllerUp(player, input);
				break;
			case BUTTON_DOWN:
				flag |= ControllerManager.isControllerDown(player, input);
				break;
			case BUTTON_LEFT:
				flag |= ControllerManager.isControllerLeft(player, input);
				break;
			case BUTTON_RIGHT:
				flag |= ControllerManager.isControllerRight(player, input);
				break;
			default:
				flag |= ControllerManager.isControllerButton(player, input, buttonmap[i]);
				break;
			}

			if(flag){
				inputstate[i]++;
			}
			else inputstate[i] = 0;
		}
	}

	/**
	 * Load navigation key settings
	 * @param prop Property file to read from
	 */
	public void loadConfig(CustomProperties prop) {
		super.loadConfig(prop);

		/*
		keymap[BUTTON_NAV_UP] = prop.getProperty("key.p" + player + ".navigationup", Input.KEY_UP);
		keymap[BUTTON_NAV_DOWN] = prop.getProperty("key.p" + player + ".navigationdown", Input.KEY_DOWN);
		keymap[BUTTON_NAV_LEFT] = prop.getProperty("key.p" + player + ".navigationleft", Input.KEY_LEFT);
		keymap[BUTTON_NAV_RIGHT] = prop.getProperty("key.p" + player + ".navigationright", Input.KEY_RIGHT);
		keymap[BUTTON_NAV_SELECT] = prop.getProperty("key.p" + player + ".navigationselect", Input.KEY_ENTER);
		keymap[BUTTON_NAV_CANCEL] = prop.getProperty("key.p" + player + ".navigationcancel", Input.KEY_ESCAPE);
		*/
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
