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

import mu.nu.nullpo.gui.slick.MouseInput;
import mu.nu.nullpo.gui.slick.ResourceHolder;
import sdljava.SDLException;
import sdljava.video.SDLSurface;

/**
 * 設定画面のステート
 */
public class StateConfigMainMenuSDL extends BaseStateSDL {
	/** カーソル位置 */
	protected int cursor = 0;

	/** プレイヤー number */
	protected int player = 0;

	/*
	 * 画面描画
	 */
	@Override
	public void render(SDLSurface screen) throws SDLException {
		ResourceHolderSDL.imgMenu.blitSurface(screen);

		NormalFontSDL.printFontGrid(1, 1, "CONFIG", NormalFontSDL.COLOR_ORANGE);

		NormalFontSDL.printFontGrid(1, 3 + cursor, "b", NormalFontSDL.COLOR_RED);

		NormalFontSDL.printFontGrid(2, 3, "[GENERAL OPTIONS]", (cursor == 0));
		NormalFontSDL.printFontGrid(2, 4, "[RULE SELECT]:" + (player + 1) + "P", (cursor == 1));
		NormalFontSDL.printFontGrid(2, 5, "[GAME TUNING]:" + (player + 1) + "P", (cursor == 2));
		NormalFontSDL.printFontGrid(2, 6, "[AI SETTING]:" + (player + 1) + "P", (cursor == 3));
		NormalFontSDL.printFontGrid(2, 7, "[KEYBOARD SETTING]:" + (player + 1) + "P", (cursor == 4));
		NormalFontSDL.printFontGrid(2, 8, "[JOYSTICK SETTING]:" + (player + 1) + "P", (cursor == 5));

		if(cursor == 0) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigMainMenu_General"));
		if(cursor == 1) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigMainMenu_Rule"));
		if(cursor == 2) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigMainMenu_GameTuning"));
		if(cursor == 3) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigMainMenu_AI"));
		if(cursor == 4) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigMainMenu_Keyboard"));
		if(cursor == 5) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigMainMenu_Joystick"));
	}

	/*
	 * Update game state
	 */
	@Override
	public void update() throws SDLException {
		// Mouse
		boolean mouseConfirm = false;
		MouseInputSDL.mouseInput.update();
		if (MouseInput.mouseInput.isMouseClicked())
		{
			int y = MouseInput.mouseInput.getMouseY() >> 4;
			int newCursor = y - 4;
			if (newCursor == cursor)
				mouseConfirm = true;
			else if (newCursor >= 0 && newCursor <= 4)
			{
				ResourceHolder.soundManager.play("cursor");
				cursor = newCursor;
			}
		}
		
		// カーソル移動
		// if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_UP)) {
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_NAV_UP)) {
			cursor--;
			if(cursor < 0) cursor = 5;
			ResourceHolderSDL.soundManager.play("cursor");
		}
		// if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_DOWN)) {
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_NAV_DOWN)) {
			cursor++;
			if(cursor > 5) cursor = 0;
			ResourceHolderSDL.soundManager.play("cursor");
		}

		// プレイヤー number変更
		// if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_LEFT)) {
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_NAV_LEFT)) {
			player--;
			if(player < 0) player = 1;
			ResourceHolderSDL.soundManager.play("change");
		}
		// if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_RIGHT)) {
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_NAV_RIGHT)) {
			player++;
			if(player > 1) player = 0;
			ResourceHolderSDL.soundManager.play("change");
		}

		// 決定 button
		// if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_A)) {
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_NAV_SELECT) || mouseConfirm) {
			ResourceHolderSDL.soundManager.play("decide");

			switch(cursor) {
			case 0:
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_GENERAL);
				break;
			case 1:
				StateConfigRuleSelectSDL stateR = (StateConfigRuleSelectSDL)NullpoMinoSDL.gameStates[NullpoMinoSDL.STATE_CONFIG_RULESELECT];
				stateR.player = player;
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_RULESELECT);
				break;
			case 2:
				StateConfigGameTuningSDL stateT = (StateConfigGameTuningSDL)NullpoMinoSDL.gameStates[NullpoMinoSDL.STATE_CONFIG_GAMETUNING];
				stateT.player = player;
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_GAMETUNING);
				break;
			case 3:
				StateConfigAISelectSDL stateA = (StateConfigAISelectSDL)NullpoMinoSDL.gameStates[NullpoMinoSDL.STATE_CONFIG_AISELECT];
				stateA.player = player;
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_AISELECT);
				break;
			case 4:
				StateConfigKeyboardSDL stateK = (StateConfigKeyboardSDL)NullpoMinoSDL.gameStates[NullpoMinoSDL.STATE_CONFIG_KEYBOARD];
				stateK.player = player;
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_KEYBOARD);
				break;
			case 5:
				StateConfigJoystickMainSDL stateJ = (StateConfigJoystickMainSDL)NullpoMinoSDL.gameStates[NullpoMinoSDL.STATE_CONFIG_JOYSTICK_MAIN];
				stateJ.player = player;
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_JOYSTICK_MAIN);
				break;
			}
		}

		// Cancel button
		// if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_B)) NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_TITLE);
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_NAV_CANCEL) || MouseInputSDL.mouseInput.isMouseRightClicked()) {
			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_TITLE);
		}
	}
}
