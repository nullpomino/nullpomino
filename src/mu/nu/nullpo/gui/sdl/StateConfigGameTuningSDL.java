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
import sdljava.video.SDLRect;
import sdljava.video.SDLSurface;

/**
 * チューニング設定画面のステート
 */
public class StateConfigGameTuningSDL extends BaseStateSDL {
	/** Player number */
	public int player;

	/** Cursor position */
	protected int cursor;

	/** A buttonでのrotationDirectionを -1=ルールに従う 0= always 左rotation 1= always 右rotation */
	protected int owRotateButtonDefaultRight;

	/** Blockの絵柄 -1=ルールに従う 0以上=固定 */
	protected int owSkin;

	/** 最低/Maximum横溜め速度 -1=ルールに従う 0以上=固定 */
	protected int owMinDAS, owMaxDAS;

	/** 横移動速度 -1=ルールに従う 0以上=固定 */
	protected int owDasDelay;

	/** Reverse the roles of up/down keys in-game */
	protected boolean owReverseUpDown;

	/** Diagonal move (-1=Auto 0=Disable 1=Enable) */
	protected int owMoveDiagonal;

	/**
	 * Constructor
	 */
	public StateConfigGameTuningSDL() {
		player = 0;
		cursor = 0;
	}

	/**
	 * Load settings
	 * @param prop Property file to read from
	 */
	protected void loadConfig(CustomProperties prop) {
		owRotateButtonDefaultRight = prop.getProperty(player + ".tuning.owRotateButtonDefaultRight", -1);
		owSkin = prop.getProperty(player + ".tuning.owSkin", -1);
		owMinDAS = prop.getProperty(player + ".tuning.owMinDAS", -1);
		owMaxDAS = prop.getProperty(player + ".tuning.owMaxDAS", -1);
		owDasDelay = prop.getProperty(player + ".tuning.owDasDelay", -1);
		owReverseUpDown = prop.getProperty(player + ".tuning.owReverseUpDown", false);
		owMoveDiagonal = prop.getProperty(player + ".tuning.owMoveDiagonal", -1);
	}

	/**
	 * Save settings
	 * @param prop Property file to save to
	 */
	protected void saveConfig(CustomProperties prop) {
		prop.setProperty(player + ".tuning.owRotateButtonDefaultRight", owRotateButtonDefaultRight);
		prop.setProperty(player + ".tuning.owSkin", owSkin);
		prop.setProperty(player + ".tuning.owMinDAS", owMinDAS);
		prop.setProperty(player + ".tuning.owMaxDAS", owMaxDAS);
		prop.setProperty(player + ".tuning.owDasDelay", owDasDelay);
		prop.setProperty(player + ".tuning.owReverseUpDown", owReverseUpDown);
		prop.setProperty(player + ".tuning.owMoveDiagonal", owMoveDiagonal);
	}

	/*
	 * Called when entering this state
	 */
	@Override
	public void enter() throws SDLException {
		loadConfig(NullpoMinoSDL.propGlobal);
	}

	/*
	 * Draw the game screen
	 */
	@Override
	public void render(SDLSurface screen) throws SDLException {
		String strTemp = "";
		ResourceHolderSDL.imgMenu.blitSurface(screen);

		NormalFontSDL.printFontGrid(1, 1, "GAME TUNING (" + (player+1) + "P)", NormalFontSDL.COLOR_ORANGE);
		NormalFontSDL.printFontGrid(1, 3 + cursor, "b", NormalFontSDL.COLOR_RED);

		if(owRotateButtonDefaultRight == -1) strTemp = "AUTO";
		if(owRotateButtonDefaultRight == 0) strTemp = "LEFT";
		if(owRotateButtonDefaultRight == 1) strTemp = "RIGHT";
		NormalFontSDL.printFontGrid(2, 3, "A BUTTON ROTATE:" + strTemp, (cursor == 0));

		NormalFontSDL.printFontGrid(2, 4, "BLOCK SKIN:" + ((owSkin == -1) ? "AUTO": String.valueOf(owSkin)), (cursor == 1));
		if((owSkin >= 0) && (owSkin < ResourceHolderSDL.imgNormalBlockList.size())) {
			SDLSurface imgBlock = ResourceHolderSDL.imgNormalBlockList.get(owSkin);

			if(ResourceHolderSDL.blockStickyFlagList.get(owSkin) == true) {
				for(int j = 0; j < 9; j++) {
					SDLRect rectSkinSrc = new SDLRect(0, j * 16, 16, 16);
					SDLRect rectSkinDst = new SDLRect(256 + (j * 16), 64, 144, 16);
					imgBlock.blitSurface(rectSkinSrc, screen, rectSkinDst);
				}
			} else {
				SDLRect rectSkinSrc = new SDLRect(0, 0, 144, 16);
				SDLRect rectSkinDst = new SDLRect(256, 64, 144, 16);
				imgBlock.blitSurface(rectSkinSrc, screen, rectSkinDst);
			}
		}

		NormalFontSDL.printFontGrid(2, 5, "MIN DAS:" + ((owMinDAS == -1) ? "AUTO" : String.valueOf(owMinDAS)), (cursor == 2));
		NormalFontSDL.printFontGrid(2, 6, "MAX DAS:" + ((owMaxDAS == -1) ? "AUTO" : String.valueOf(owMaxDAS)), (cursor == 3));
		NormalFontSDL.printFontGrid(2, 7, "DAS DELAY:" + ((owDasDelay == -1) ? "AUTO" : String.valueOf(owDasDelay)), (cursor == 4));
		NormalFontSDL.printFontGrid(2, 8, "REVERSE UP/DOWN:" + GeneralUtil.getOorX(owReverseUpDown), (cursor == 5));

		if(owMoveDiagonal == -1) strTemp = "AUTO";
		if(owMoveDiagonal == 0) strTemp = "e";
		if(owMoveDiagonal == 1) strTemp = "c";
		NormalFontSDL.printFontGrid(2, 9, "DIAGONAL MOVE:" + strTemp, (cursor == 6));
	}

	/*
	 * Update game state
	 */
	@Override
	public void update() throws SDLException {
		// Cursor movement
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_UP)) {
			cursor--;
			if(cursor < 0) cursor = 6;
			ResourceHolderSDL.soundManager.play("cursor");
		}
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_DOWN)) {
			cursor++;
			if(cursor > 6) cursor = 0;
			ResourceHolderSDL.soundManager.play("cursor");
		}

		// Configuration changes
		int change = 0;
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_LEFT)) change = -1;
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_RIGHT)) change = 1;

		if(change != 0) {
			ResourceHolderSDL.soundManager.play("change");

			switch(cursor) {
			case 0:
				owRotateButtonDefaultRight += change;
				if(owRotateButtonDefaultRight < -1) owRotateButtonDefaultRight = 1;
				if(owRotateButtonDefaultRight > 1) owRotateButtonDefaultRight = -1;
				break;
			case 1:
				owSkin += change;
				if(owSkin < -1) owSkin = ResourceHolderSDL.imgNormalBlockList.size() - 1;
				if(owSkin > ResourceHolderSDL.imgNormalBlockList.size() - 1) owSkin = -1;
				break;
			case 2:
				owMinDAS += change;
				if(owMinDAS < -1) owMinDAS = 99;
				if(owMinDAS > 99) owMinDAS = -1;
				break;
			case 3:
				owMaxDAS += change;
				if(owMaxDAS < -1) owMaxDAS = 99;
				if(owMaxDAS > 99) owMaxDAS = -1;
				break;
			case 4:
				owDasDelay += change;
				if(owDasDelay < -1) owDasDelay = 99;
				if(owDasDelay > 99) owDasDelay = -1;
				break;
			case 5:
				owReverseUpDown ^= true;
				break;
			case 6:
				owMoveDiagonal += change;
				if(owMoveDiagonal < -1) owMoveDiagonal = 1;
				if(owMoveDiagonal > 1) owMoveDiagonal = -1;
				break;
			}
		}

		// 決定 button
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_A)) {
			ResourceHolderSDL.soundManager.play("decide");

			saveConfig(NullpoMinoSDL.propGlobal);
			NullpoMinoSDL.saveConfig();

			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
		}

		// Cancel button
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_B)) {
			loadConfig(NullpoMinoSDL.propGlobal);
			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
		}
	}
}
