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
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * State of the keyboard setting screen
 */
public class StateConfigKeyboardNavi extends DummyMenuChooseState {
	/** This state's ID */
	public static final int ID = 16;

	/** Player number */
	public int player = 0;

	/** StateBasedGame */
	protected StateBasedGame gameObj;

	public StateConfigKeyboardNavi () {
		maxCursor = 1;
		minChoiceY = 1;
	}

	/*
	 * Fetch this state's ID
	 */
	@Override
	public int getID() {
		return ID;
	}

	/*
	 * State initialization
	 */
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		gameObj = game;
	}

	/**
	 * Get key name
	 * @param key Keycode
	 * @return Key name
	 */
	protected String getKeyName(int key) {
		String str = org.lwjgl.input.Keyboard.getKeyName(key);
		return (str == null) ? String.valueOf(key) : str.toUpperCase();
	}

	/*
	 * Draw the screen
	 */
	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.drawImage(ResourceHolderSlick.imgMenu, 0, 0);

		NormalFontSlick.printFontGrid(1, 1, "KEYBOARD NAVIGATION SETTING (" + (player + 1) + "P)", NormalFontSlick.COLOR_ORANGE);

		NormalFontSlick.printFontGrid(1, 3 + cursor, "b", NormalFontSlick.COLOR_RED);

		NormalFontSlick.printFontGrid(2, 3, "COPY FROM GAME KEYS", (cursor == 0));
		NormalFontSlick.printFontGrid(2, 4, "CUSTOMIZE", (cursor == 1));
	}

	@Override
	protected boolean onDecide(GameContainer container, StateBasedGame game, int delta) {
		if (cursor == 0) {
			for(int i = 0; i < GameKeySlick.MAX_BUTTON; i++) {
				GameKeySlick.gamekey[player].keymapNav[i] = GameKeySlick.gamekey[player].keymap[i];
			}
		} else if (cursor == 1) {
			NullpoMinoSlick.stateConfigKeyboard.player = player;
			NullpoMinoSlick.stateConfigKeyboard.isNavSetting = true;
			game.enterState(StateConfigKeyboard.ID);
			return true;
		}

		GameKeySlick.gamekey[player].saveConfig(NullpoMinoSlick.propConfig);
		NullpoMinoSlick.saveConfig();

		ResourceHolderSlick.soundManager.play("decide");
		gameObj.enterState(StateConfigMainMenu.ID);
		return true;
	}

	@Override
	protected boolean onCancel(GameContainer container, StateBasedGame game, int delta) {
		game.enterState(StateConfigMainMenu.ID);
		return false;
	}

	/**
	 * Called when entering this state
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
	}

	/**
	 * Called when leaving this state
	 */
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
	}
}
