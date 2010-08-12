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
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * 設定画面のステート
 */
public class StateConfigMainMenu extends BasicGameState {
	/** このステートのID */
	public static final int ID = 5;

	/** カーソル位置 */
	protected int cursor = 0;

	/** プレイヤー number */
	protected int player = 0;

	/** スクリーンショット撮影 flag */
	protected boolean ssflag = false;

	/*
	 * このステートのIDを取得
	 */
	@Override
	public int getID() {
		return ID;
	}

	/*
	 * ステートのInitialization
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
	}

	/*
	 * 画面描画
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// 背景
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

		if(cursor == 0) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigMainMenu_General"));
		if(cursor == 1) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigMainMenu_Rule"));
		if(cursor == 2) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigMainMenu_GameTuning"));
		if(cursor == 3) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigMainMenu_AI"));
		if(cursor == 4) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigMainMenu_Keyboard"));
		if(cursor == 5) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigMainMenu_Joystick"));

		// FPS
		NullpoMinoSlick.drawFPS(container);
		// オブザーバー
		NullpoMinoSlick.drawObserverClient();
		// スクリーンショット
		if(ssflag) {
			NullpoMinoSlick.saveScreenShot(container, g);
			ssflag = false;
		}

		if(!NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}

	/*
	 * Update game state
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(!container.hasFocus()) {
			if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
			return;
		}

		// TTFフォント描画
		if(ResourceHolder.ttfFont != null) ResourceHolder.ttfFont.loadGlyphs();

		// キー入力状態を更新
		GameKey.gamekey[0].update(container.getInput());
		// Mouse
		boolean mouseConfirm = false;
		MouseInput.mouseInput.update(container.getInput());
		if (MouseInput.mouseInput.isMouseClicked())
		{
			int y = MouseInput.mouseInput.getMouseY() >> 4;
			int newCursor = y - 3;
			if (newCursor == cursor)
				mouseConfirm = true;
			else if (newCursor >= 0 && newCursor <= 5)
			{
				ResourceHolder.soundManager.play("cursor");
				cursor = newCursor;
			}
		}
		
		// カーソル移動
		// if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_UP)) {
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_NAV_UP)) {
			cursor--;
			if(cursor < 0) cursor = 5;
			ResourceHolder.soundManager.play("cursor");
		}
		// if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_DOWN)) {
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_NAV_DOWN)) {
			cursor++;
			if(cursor > 5) cursor = 0;
			ResourceHolder.soundManager.play("cursor");
		}

		// プレイヤー number変更
		// if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_LEFT)) {
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_NAV_LEFT)) {
			player--;
			if(player < 0) player = 1;
			ResourceHolder.soundManager.play("change");
		}
		// if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_RIGHT)) {
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_NAV_RIGHT)) {
			player++;
			if(player > 1) player = 0;
			ResourceHolder.soundManager.play("change");
		}

		// 決定 button
		// if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_A)) {
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_NAV_SELECT) || mouseConfirm) {
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
		}

		// Cancel button
		//if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_B)) game.enterState(StateTitle.ID);
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_NAV_CANCEL) || MouseInput.mouseInput.isMouseRightClicked())
			game.enterState(StateTitle.ID);
		// スクリーンショット button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_SCREENSHOT)) ssflag = true;

		// 終了 button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_QUIT)) container.exit();

		if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}
}
