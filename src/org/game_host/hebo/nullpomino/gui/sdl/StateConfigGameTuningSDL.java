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
package org.game_host.hebo.nullpomino.gui.sdl;

import org.game_host.hebo.nullpomino.util.CustomProperties;

import sdljava.SDLException;
import sdljava.video.SDLRect;
import sdljava.video.SDLSurface;

/**
 * チューニング設定画面のステート
 */
public class StateConfigGameTuningSDL extends BaseStateSDL {
	/** プレイヤー番号 */
	public int player;

	/** カーソル位置 */
	protected int cursor;

	/** Aボタンでの回転方向を -1=ルールに従う 0=常に左回転 1=常に右回転 */
	protected int owRotateButtonDefaultRight;

	/** ブロックの絵柄 -1=ルールに従う 0以上=固定 */
	protected int owSkin;

	/** 最低/最大横溜め速度 -1=ルールに従う 0以上=固定 */
	protected int owMinDAS, owMaxDAS;

	/** 横移動速度 -1=ルールに従う 0以上=固定 */
	protected int owDasDelay;

	/**
	 * コンストラクタ
	 */
	public StateConfigGameTuningSDL() {
		player = 0;
		cursor = 0;
	}

	/**
	 * 設定読み込み
	 * @param prop 読み込み元のプロパティファイル
	 */
	protected void loadConfig(CustomProperties prop) {
		owRotateButtonDefaultRight = prop.getProperty(player + ".tuning.owRotateButtonDefaultRight", -1);
		owSkin = prop.getProperty(player + ".tuning.owSkin", -1);
		owMinDAS = prop.getProperty(player + ".tuning.owMinDAS", -1);
		owMaxDAS = prop.getProperty(player + ".tuning.owMaxDAS", -1);
		owDasDelay = prop.getProperty(player + ".tuning.owDasDelay", -1);
	}

	/**
	 * 設定保存
	 * @param prop 保存先のプロパティファイル
	 */
	protected void saveConfig(CustomProperties prop) {
		prop.setProperty(player + ".tuning.owRotateButtonDefaultRight", owRotateButtonDefaultRight);
		prop.setProperty(player + ".tuning.owSkin", owSkin);
		prop.setProperty(player + ".tuning.owMinDAS", owMinDAS);
		prop.setProperty(player + ".tuning.owMaxDAS", owMaxDAS);
		prop.setProperty(player + ".tuning.owDasDelay", owDasDelay);
	}

	/*
	 * このステートに入ったときの処理
	 */
	@Override
	public void enter() throws SDLException {
		loadConfig(NullpoMinoSDL.propGlobal);
	}

	/*
	 * ゲーム画面の描画
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
		if((owSkin >= 0) && (owSkin * 16 < ResourceHolderSDL.imgBlock.getHeight())) {
			SDLRect rectSkinSrc = new SDLRect(0, owSkin * 16, 144, 16);
			SDLRect rectSkinDst = new SDLRect(256, 64, 144, 16);
			ResourceHolderSDL.imgBlock.blitSurface(rectSkinSrc, screen, rectSkinDst);
		}

		NormalFontSDL.printFontGrid(2, 5, "MIN DAS:" + ((owMinDAS == -1) ? "AUTO" : String.valueOf(owMinDAS)), (cursor == 2));
		NormalFontSDL.printFontGrid(2, 6, "MAX DAS:" + ((owMaxDAS == -1) ? "AUTO" : String.valueOf(owMaxDAS)), (cursor == 3));
		NormalFontSDL.printFontGrid(2, 7, "DAS DELAY:" + ((owDasDelay == -1) ? "AUTO" : String.valueOf(owDasDelay)), (cursor == 4));
	}

	/*
	 * ゲーム状態の更新
	 */
	@Override
	public void update() throws SDLException {
		// カーソル移動
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_UP)) {
			cursor--;
			if(cursor < 0) cursor = 4;
			ResourceHolderSDL.soundManager.play("cursor");
		}
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_DOWN)) {
			cursor++;
			if(cursor > 4) cursor = 0;
			ResourceHolderSDL.soundManager.play("cursor");
		}

		// 設定変更
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
				if(owSkin < -1) owSkin = (ResourceHolderSDL.imgBlock.getHeight() / 16) - 1;
				if(owSkin > (ResourceHolderSDL.imgBlock.getHeight() / 16) - 1) owSkin = -1;
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
			}
		}

		// 決定ボタン
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_A)) {
			ResourceHolderSDL.soundManager.play("decide");

			saveConfig(NullpoMinoSDL.propGlobal);
			NullpoMinoSDL.saveConfig();

			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
		}

		// キャンセルボタン
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_B)) {
			loadConfig(NullpoMinoSDL.propGlobal);
			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
		}
	}
}
