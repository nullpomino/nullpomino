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
package org.game_host.hebo.nullpomino.gui.slick;

import org.game_host.hebo.nullpomino.util.CustomProperties;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class StateConfigGameTuning extends BasicGameState {
	/** このステートのID */
	public static final int ID = 14;

	/** プレイヤー番号 */
	public int player = 0;

	/** カーソル位置 */
	protected int cursor = 0;

	/** スクリーンショット撮影フラグ */
	protected boolean ssflag = false;

	/** Aボタンでの回転方向を -1=ルールに従う 0=常に左回転 1=常に右回転 */
	protected int owRotateButtonDefaultRight;

	/** ブロックの絵柄 -1=ルールに従う 0以上=固定 */
	protected int owSkin;

	/** 最低/最大横溜め速度 -1=ルールに従う 0以上=固定 */
	protected int owMinDAS, owMaxDAS;

	/** 横移動速度 -1=ルールに従う 0以上=固定 */
	protected int owDasDelay;

	/*
	 * このステートのIDを取得
	 */
	@Override
	public int getID() {
		return ID;
	}

	/*
	 * ステートの初期化
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
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
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		loadConfig(NullpoMinoSlick.propGlobal);
	}

	/*
	 * ゲーム画面の描画
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// メニュー
		String strTemp = "";
		g.drawImage(ResourceHolder.imgMenu, 0, 0);

		NormalFont.printFontGrid(1, 1, "GAME TUNING (" + (player+1) + "P)", NormalFont.COLOR_ORANGE);
		NormalFont.printFontGrid(1, 3 + cursor, "b", NormalFont.COLOR_RED);

		if(owRotateButtonDefaultRight == -1) strTemp = "AUTO";
		if(owRotateButtonDefaultRight == 0) strTemp = "LEFT";
		if(owRotateButtonDefaultRight == 1) strTemp = "RIGHT";
		NormalFont.printFontGrid(2, 3, "A BUTTON ROTATE:" + strTemp, (cursor == 0));

		NormalFont.printFontGrid(2, 4, "BLOCK SKIN:" + ((owSkin == -1) ? "AUTO": String.valueOf(owSkin)), (cursor == 1));
		if((owSkin >= 0) && (owSkin * 16 < ResourceHolder.imgBlock.getHeight())) {
			ResourceHolder.imgBlock.draw(256, 64, 256 + 144, 64 + 16, 0, owSkin * 16, 144, (owSkin * 16) + 16);
		}

		NormalFont.printFontGrid(2, 5, "MIN DAS:" + ((owMinDAS == -1) ? "AUTO" : String.valueOf(owMinDAS)), (cursor == 2));
		NormalFont.printFontGrid(2, 6, "MAX DAS:" + ((owMaxDAS == -1) ? "AUTO" : String.valueOf(owMaxDAS)), (cursor == 3));
		NormalFont.printFontGrid(2, 7, "DAS DELAY:" + ((owDasDelay == -1) ? "AUTO" : String.valueOf(owDasDelay)), (cursor == 4));

		// FPS
		NullpoMinoSlick.drawFPS(container);
		// オブザーバー
		NullpoMinoSlick.drawObserverClient();
		// スクリーンショット
		if(ssflag) {
			NullpoMinoSlick.saveScreenShot(container, g);
			ssflag = false;
		}
		NullpoMinoSlick.alternateFPSSleep();
	}

	/*
	 * ゲーム状態の更新
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		// キー入力状態を更新
		GameKey.gamekey[0].update(container.getInput());

		// カーソル移動
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_UP)) {
			cursor--;
			if(cursor < 0) cursor = 4;
			ResourceHolder.soundManager.play("cursor");
		}
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_DOWN)) {
			cursor++;
			if(cursor > 4) cursor = 0;
			ResourceHolder.soundManager.play("cursor");
		}

		// 設定変更
		int change = 0;
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_LEFT)) change = -1;
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_RIGHT)) change = 1;

		if(change != 0) {
			ResourceHolder.soundManager.play("change");

			switch(cursor) {
			case 0:
				owRotateButtonDefaultRight += change;
				if(owRotateButtonDefaultRight < -1) owRotateButtonDefaultRight = 1;
				if(owRotateButtonDefaultRight > 1) owRotateButtonDefaultRight = -1;
				break;
			case 1:
				owSkin += change;
				if(owSkin < -1) owSkin = (ResourceHolder.imgBlock.getHeight() / 16) - 1;
				if(owSkin > (ResourceHolder.imgBlock.getHeight() / 16) - 1) owSkin = -1;
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
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_A)) {
			ResourceHolder.soundManager.play("decide");

			saveConfig(NullpoMinoSlick.propGlobal);
			NullpoMinoSlick.saveConfig();

			game.enterState(StateConfigMainMenu.ID);
		}

		// キャンセルボタン
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_B)) {
			loadConfig(NullpoMinoSlick.propGlobal);
			game.enterState(StateConfigMainMenu.ID);
		}

		// スクリーンショットボタン
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_SCREENSHOT)) ssflag = true;

		// 終了ボタン
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_QUIT)) container.exit();
	}
}
