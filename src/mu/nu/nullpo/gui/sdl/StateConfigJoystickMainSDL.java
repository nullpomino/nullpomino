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

import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

import sdljava.SDLException;
import sdljava.video.SDLSurface;

/**
 * Joystick 設定メインメニューのステート
 */
public class StateConfigJoystickMainSDL extends BaseStateSDL {
	/** Player number */
	public int player;

	/** カーソル位置 */
	protected int cursor;

	/** 使用するJoystick の number */
	protected int joyUseNumber;

	/** Joystick direction key が反応する閾値 */
	protected int joyBorder;

	/** アナログスティック無視 */
	protected boolean joyIgnoreAxis;

	/** ハットスイッチ無視 */
	protected boolean joyIgnorePOV;

	/**
	 * Constructor
	 */
	public StateConfigJoystickMainSDL() {
		player = 0;
		cursor = 0;
	}

	/**
	 * 設定読み込み
	 * @param prop Property file to read from
	 */
	protected void loadConfig(CustomProperties prop) {
		joyUseNumber = prop.getProperty("joyUseNumber.p" + player, -1);
		joyBorder = prop.getProperty("joyBorder.p" + player, 0);
		joyIgnoreAxis = prop.getProperty("joyIgnoreAxis.p" + player, false);
		joyIgnorePOV = prop.getProperty("joyIgnorePOV.p" + player, false);
	}

	/**
	 * 設定保存
	 * @param prop Property file to save to
	 */
	protected void saveConfig(CustomProperties prop) {
		prop.setProperty("joyUseNumber.p" + player, joyUseNumber);
		prop.setProperty("joyBorder.p" + player, joyBorder);
		prop.setProperty("joyIgnoreAxis.p" + player, joyIgnoreAxis);
		prop.setProperty("joyIgnorePOV.p" + player, joyIgnorePOV);
	}

	/*
	 * このステートに入ったときの処理
	 */
	@Override
	public void enter() throws SDLException {
		loadConfig(NullpoMinoSDL.propConfig);
	}

	/*
	 * ゲーム画面の描画
	 */
	@Override
	public void render(SDLSurface screen) throws SDLException {
		ResourceHolderSDL.imgMenu.blitSurface(screen);

		NormalFontSDL.printFontGrid(1, 1, "JOYSTICK SETTING (" + (player+1) + "P)", NormalFontSDL.COLOR_ORANGE);

		NormalFontSDL.printFontGrid(1, 3 + cursor, "b", NormalFontSDL.COLOR_RED);

		NormalFontSDL.printFontGrid(2, 3, "[BUTTON SETTING]", (cursor == 0));
		NormalFontSDL.printFontGrid(2, 4, "[INPUT TEST]", (cursor == 1));
		NormalFontSDL.printFontGrid(2, 5, "JOYSTICK NUMBER:" + ((joyUseNumber == -1) ? "NOTHING" : String.valueOf(joyUseNumber)), (cursor == 2));
		NormalFontSDL.printFontGrid(2, 6, "JOYSTICK BORDER:" + joyBorder, (cursor == 3));
		NormalFontSDL.printFontGrid(2, 7, "IGNORE AXIS:" + GeneralUtil.getONorOFF(joyIgnoreAxis), (cursor == 4));
		NormalFontSDL.printFontGrid(2, 8, "IGNORE POV:" + GeneralUtil.getONorOFF(joyIgnorePOV), (cursor == 5));

		if(cursor == 0) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigJoystickMain_ButtonSetting"));
		if(cursor == 1) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigJoystickMain_InputTest"));
		if(cursor == 2) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigJoystickMain_JoyUseNumber"));
		if(cursor == 3) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigJoystickMain_JoyBorder"));
		if(cursor == 4) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigJoystickMain_JoyIgnoreAxis"));
		if(cursor == 5) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigJoystickMain_JoyIgnorePOV"));
	}

	/*
	 * Update game state
	 */
	@Override
	public void update() throws SDLException {
		// カーソル移動
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_UP)) {
			cursor--;
			if(cursor < 0) cursor = 5;
			ResourceHolderSDL.soundManager.play("cursor");
		}
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_DOWN)) {
			cursor++;
			if(cursor > 5) cursor = 0;
			ResourceHolderSDL.soundManager.play("cursor");
		}

		// Configuration changes
		int change = 0;
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_LEFT)) change = -1;
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_RIGHT)) change = 1;

		if(change != 0) {
			ResourceHolderSDL.soundManager.play("change");

			switch(cursor) {
			case 2:
				joyUseNumber += change;
				if(joyUseNumber < -1) joyUseNumber = NullpoMinoSDL.joystickMax - 1;
				if(joyUseNumber > NullpoMinoSDL.joystickMax - 1) joyUseNumber = -1;
				break;
			case 3:
				joyBorder += change;
				if(joyBorder < 0) joyBorder = 32768;
				if(joyBorder > 32768) joyBorder = 0;
				break;
			case 4:
				joyIgnoreAxis = !joyIgnoreAxis;
				break;
			case 5:
				joyIgnorePOV = !joyIgnorePOV;
				break;
			}
		}

		// 決定 button
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_A)) {
			ResourceHolderSDL.soundManager.play("decide");

			saveConfig(NullpoMinoSDL.propConfig);
			NullpoMinoSDL.saveConfig();
			NullpoMinoSDL.joyUseNumber[player] = joyUseNumber;
			NullpoMinoSDL.joyIgnoreAxis[player] = joyIgnoreAxis;
			NullpoMinoSDL.joyIgnorePOV[player] = joyIgnorePOV;
			GameKeySDL.gamekey[player].joyBorder = joyBorder;

			if(cursor == 0) {
				//[BUTTON SETTING]
				StateConfigJoystickButtonSDL stateJ = (StateConfigJoystickButtonSDL)NullpoMinoSDL.gameStates[NullpoMinoSDL.STATE_CONFIG_JOYSTICK_BUTTON];
				stateJ.player = player;
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_JOYSTICK_BUTTON);
			} else if(cursor == 1) {
				//[INPUT TEST]
				StateConfigJoystickTestSDL stateT = (StateConfigJoystickTestSDL)NullpoMinoSDL.gameStates[NullpoMinoSDL.STATE_CONFIG_JOYSTICK_TEST];
				stateT.player = player;
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_JOYSTICK_TEST);
			} else {
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
			}
		}

		// Cancel button
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_B)) {
			loadConfig(NullpoMinoSDL.propConfig);
			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
		}
	}
}
