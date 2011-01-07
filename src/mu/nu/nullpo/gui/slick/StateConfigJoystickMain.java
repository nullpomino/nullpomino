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

import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Joystick 設定メインMenu のステート
 */
public class StateConfigJoystickMain extends BaseGameState {
	/** This state's ID */
	public static final int ID = 12;

	/** Key input を受付可能になるまでの frame count */
	public static final int KEYACCEPTFRAME = 20;

	/** Joystick method names */
	protected static final String[] JOYSTICK_METHOD_STRINGS = {"NONE", "SLICK DEFAULT", "SLICK ALTERNATE", "LWJGL"};

	/** UI Text identifier Strings */
	protected static final String[] UI_TEXT = {
		"ConfigJoystickMain_ButtonSetting",
		"ConfigJoystickMain_InputTest",
		"ConfigJoystickMain_JoyUseNumber",
		"ConfigJoystickMain_JoyBorder",
		"ConfigJoystickMain_JoyIgnoreAxis",
		"ConfigJoystickMain_JoyIgnorePOV",
		"ConfigJoystickMain_JoyMethod",
	};

	/** Player number */
	public int player = 0;

	/** Cursor position */
	protected int cursor = 0;

	/** 使用するJoystick の number */
	protected int joyUseNumber;

	/** Joystick direction key が反応する閾値 */
	protected int joyBorder;

	/** アナログスティック無視 */
	protected boolean joyIgnoreAxis;

	/** ハットスイッチ無視 */
	protected boolean joyIgnorePOV;

	/** Joystick input method */
	protected int joyMethod;

	/*
	 * Fetch this state's ID
	 */
	@Override
	public int getID() {
		return ID;
	}

	/**
	 * Load settings
	 * @param prop Property file to read from
	 */
	protected void loadConfig(CustomProperties prop) {
		joyUseNumber = prop.getProperty("joyUseNumber.p" + player, -1);
		joyBorder = prop.getProperty("joyBorder.p" + player, 0);
		joyIgnoreAxis = prop.getProperty("joyIgnoreAxis.p" + player, false);
		joyIgnorePOV = prop.getProperty("joyIgnorePOV.p" + player, false);
		joyMethod = prop.getProperty("option.joymethod", ControllerManager.CONTROLLER_METHOD_SLICK_DEFAULT);
	}

	/**
	 * Save settings
	 * @param prop Property file to save to
	 */
	protected void saveConfig(CustomProperties prop) {
		prop.setProperty("joyUseNumber.p" + player, joyUseNumber);
		prop.setProperty("joyBorder.p" + player, joyBorder);
		prop.setProperty("joyIgnoreAxis.p" + player, joyIgnoreAxis);
		prop.setProperty("joyIgnorePOV.p" + player, joyIgnorePOV);
		prop.setProperty("option.joymethod", joyMethod);
	}

	/*
	 * Called when entering this state
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		loadConfig(NullpoMinoSlick.propConfig);
	}

	/*
	 * State initialization
	 */
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
	}

	/*
	 * Draw the game screen
	 */
	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// Menu
		g.drawImage(ResourceHolder.imgMenu, 0, 0);

		NormalFont.printFontGrid(1, 1, "JOYSTICK SETTING (" + (player+1) + "P)", NormalFont.COLOR_ORANGE);

		NormalFont.printFontGrid(1, 3 + cursor, "b", NormalFont.COLOR_RED);

		NormalFont.printFontGrid(2, 3, "[BUTTON SETTING]", (cursor == 0));
		NormalFont.printFontGrid(2, 4, "[INPUT TEST]", (cursor == 1));
		NormalFont.printFontGrid(2, 5, "JOYSTICK NUMBER:" + ((joyUseNumber == -1) ? "NOTHING" : String.valueOf(joyUseNumber)), (cursor == 2));
		NormalFont.printFontGrid(2, 6, "JOYSTICK BORDER:" + joyBorder, (cursor == 3));
		NormalFont.printFontGrid(2, 7, "IGNORE AXIS:" + GeneralUtil.getONorOFF(joyIgnoreAxis), (cursor == 4));
		NormalFont.printFontGrid(2, 8, "IGNORE POV:" + GeneralUtil.getONorOFF(joyIgnorePOV), (cursor == 5));
		NormalFont.printFontGrid(2, 9, "JOYSTICK METHOD:" + JOYSTICK_METHOD_STRINGS[joyMethod], (cursor == 6));

		if(cursor < UI_TEXT.length) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText(UI_TEXT[cursor]));
	}

	/*
	 * Update game state
	 */
	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		// TTF font load
		if(ResourceHolder.ttfFont != null) ResourceHolder.ttfFont.loadGlyphs();

		// Update key input states
		GameKey.gamekey[0].update(container.getInput());

		// Cursor movement
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_UP)) {
			cursor--;
			if(cursor < 0) cursor = 6;
			ResourceHolder.soundManager.play("cursor");
		}
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_DOWN)) {
			cursor++;
			if(cursor > 6) cursor = 0;
			ResourceHolder.soundManager.play("cursor");
		}

		// Configuration changes
		int change = 0;
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_LEFT)) change = -1;
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_RIGHT)) change = 1;

		if(change != 0) {
			ResourceHolder.soundManager.play("change");

			switch(cursor) {
			case 2:
				joyUseNumber += change;
				if(joyUseNumber < -1) joyUseNumber = ControllerManager.getControllerCount() - 1;
				if(joyUseNumber > ControllerManager.getControllerCount() - 1) joyUseNumber = -1;
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
			case 6:
				joyMethod += change;
				if(joyMethod < 0) joyMethod = ControllerManager.CONTROLLER_METHOD_MAX - 1;
				if(joyMethod > ControllerManager.CONTROLLER_METHOD_MAX - 1) joyMethod = 0;
				break;
			}
		}

		// Confirm button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_A)) {
			ResourceHolder.soundManager.play("decide");

			saveConfig(NullpoMinoSlick.propConfig);
			NullpoMinoSlick.saveConfig();
			NullpoMinoSlick.setGeneralConfig();

			if(cursor == 0) {
				//[BUTTON SETTING]
				NullpoMinoSlick.stateConfigJoystickButton.player = player;
				game.enterState(StateConfigJoystickButton.ID);
			} else if(cursor == 1) {
				//[INPUT TEST]
				NullpoMinoSlick.stateConfigJoystickTest.player = player;
				game.enterState(StateConfigJoystickTest.ID);
			} else {
				game.enterState(StateConfigMainMenu.ID);
			}
		}

		// Cancel button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_B)) {
			loadConfig(NullpoMinoSlick.propConfig);
			game.enterState(StateConfigMainMenu.ID);
		}
	}
}
