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
 * 設定画面のステート
 */
public class StateConfigMainMenu extends DummyMenuChooseState {
	/** This state's ID */
	public static final int ID = 5;

	/** UI Text identifier Strings */
	private static final String[] UI_TEXT = {
		"ConfigMainMenu_General",
		"ConfigMainMenu_Rule",
		"ConfigMainMenu_GameTuning",
		"ConfigMainMenu_AI",
		"ConfigMainMenu_Keyboard",
		"ConfigMainMenu_Joystick"
	};

	/** Player number */
	protected int player = 0;

	public StateConfigMainMenu () {
		maxCursor = 5;
		minChoiceY = 3;
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
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
	}

	/*
	 * Draw the screen
	 */
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// Background
		g.drawImage(ResourceHolder.imgMenu, 0, 0);

		// Menu
		NormalFont.printFontGrid(1, 1, "CONFIG", NormalFont.COLOR_ORANGE);

		NormalFont.printFontGrid(1, 3 + cursor, "b", NormalFont.COLOR_RED);

		NormalFont.printFontGrid(2, 3, "[GENERAL OPTIONS]", (cursor == 0));
		NormalFont.printFontGrid(2, 4, "[RULE SELECT]:" + (player + 1) + "P", (cursor == 1));
		NormalFont.printFontGrid(2, 5, "[GAME TUNING]:" + (player + 1) + "P", (cursor == 2));
		NormalFont.printFontGrid(2, 6, "[AI SETTING]:" + (player + 1) + "P", (cursor == 3));
		NormalFont.printFontGrid(2, 7, "[KEYBOARD SETTING]:" + (player + 1) + "P", (cursor == 4));
		NormalFont.printFontGrid(2, 8, "[JOYSTICK SETTING]:" + (player + 1) + "P", (cursor == 5));

		NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText(UI_TEXT[cursor]));

		super.render(container, game, g);
	}

	@Override
	protected void onChange(GameContainer container, StateBasedGame game, int delta, int change) {
		player += change;
		if(player < 0) player = 1;
		if(player > 1) player = 0;
		ResourceHolder.soundManager.play("change");
	}

	@Override
	protected boolean onDecide(GameContainer container, StateBasedGame game, int delta) {
		ResourceHolder.soundManager.play("decide");

		switch (cursor) {
		case 0:
			game.enterState(StateConfigGeneral.ID);
			break;
		case 1:
			NullpoMinoSlick.stateConfigRuleSelect.player = player;
			game.enterState(StateConfigRuleSelect.ID);
			break;
		case 2:
			NullpoMinoSlick.stateConfigGameTuning.player = player;
			game.enterState(StateConfigGameTuning.ID);
			break;
		case 3:
			NullpoMinoSlick.stateConfigAISelect.player = player;
			game.enterState(StateConfigAISelect.ID);
			break;
		case 4:
			NullpoMinoSlick.stateConfigKeyboard.player = player;
			game.enterState(StateConfigKeyboard.ID);
			break;
		case 5:
			NullpoMinoSlick.stateConfigJoystickMain.player = player;
			game.enterState(StateConfigJoystickMain.ID);
			break;
		}

		return false;
	}

	@Override
	protected boolean onCancel(GameContainer container, StateBasedGame game, int delta) {
		game.enterState(StateTitle.ID);
		return false;
	}
}
