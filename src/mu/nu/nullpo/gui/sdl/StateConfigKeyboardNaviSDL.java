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

import mu.nu.nullpo.game.play.GameManager;
import sdljava.SDLException;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

/**
 * キーボード設定画面のステート
 */
public class StateConfigKeyboardNaviSDL extends DummyMenuChooseStateSDL {
	/** This state's ID */
	public static final int ID = 16;

	/** Player number */
	public int player = 0;

	public StateConfigKeyboardNaviSDL () {
		maxCursor = 1;
		minChoiceY = 3;
	}

	/**
	 * Get key name
	 * @param key Keycode
	 * @return Key name
	 */
	protected String getKeyName(int key) {
		if((key < 0) || (key > NullpoMinoSDL.SDL_KEYNAMES.length)) {
			return "(" + key + ")";
		}
		return NullpoMinoSDL.SDL_KEYNAMES[key];
	}

	/*
	 * Draw the screen
	 */
	public void render(SDLSurface screen) throws SDLException {
		ResourceHolderSDL.imgMenu.blitSurface(screen);

		NormalFontSDL.printFontGrid(1, 1, "KEYBOARD NAVIGATION SETTING (" + (player + 1) + "P)", NormalFontSDL.COLOR_ORANGE);

		NormalFontSDL.printFontGrid(1, 3 + cursor, "b", NormalFontSDL.COLOR_RED);

		NormalFontSDL.printFontGrid(2, 3, "COPY FROM GAME KEYS", (cursor == 0));
		NormalFontSDL.printFontGrid(2, 4, "CUSTOMIZE", (cursor == 1));
	}

	@Override
	protected boolean onDecide() throws SDLException {
		if (cursor == 0) {
			for(int i = 0; i < GameKeySDL.MAX_BUTTON; i++) {
				GameKeySDL.gamekey[player].keymapNav[i] = GameKeySDL.gamekey[player].keymap[i];
			}
		} else if (cursor == 1) {
			StateConfigKeyboardSDL stateK = (StateConfigKeyboardSDL)NullpoMinoSDL.gameStates[NullpoMinoSDL.STATE_CONFIG_KEYBOARD];
			stateK.player = player;
			stateK.isNavSetting = true;
			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_KEYBOARD);
			return true;
		}
		GameKeySDL.gamekey[player].saveConfig(NullpoMinoSDL.propConfig);
		NullpoMinoSDL.saveConfig();

		ResourceHolderSDL.soundManager.play("decide");
		NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
		return true;
	}

	@Override
	protected boolean onCancel() throws SDLException {
		NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
		return false;
	}

	/*
	 * Called when entering this state
	 */
	@Override
	public void enter() throws SDLException {
		SDLVideo.wmSetCaption("NullpoMino version" + GameManager.getVersionString(), null);
	}

	/*
	 * Called when leaving this state
	 */
	@Override
	public void leave() throws SDLException {
	}
}
