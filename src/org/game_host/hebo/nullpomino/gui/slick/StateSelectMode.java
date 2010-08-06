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

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * モード選択画面のステート
 */
public class StateSelectMode extends BasicGameState {
	/** このステートのID */
	public static final int ID = 3;

	/** 1画面に表示する最大モード数 */
	public static final int MAX_MODE_IN_ONE_PAGE = 25;

	/** モード名の配列 */
	protected String[] modenames;

	/** カーソル位置 */
	protected int cursor = 0;

	/** スクリーンショット撮影フラグ */
	protected boolean ssflag = false;

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
		String lastmode = NullpoMinoSlick.propGlobal.getProperty("name.mode", null);
		modenames = NullpoMinoSlick.modeManager.getModeNames(false);
		cursor = getIDbyName(lastmode);
		if(cursor < 0) cursor = 0;
		if(cursor > modenames.length - 1) cursor = 0;
	}

	/**
	 * Get mode ID (not including netplay modes)
	 * @param name Name of mode
	 * @return ID (-1 if not found)
	 */
	protected int getIDbyName(String name) {
		if((name == null) || (modenames == null)) return -1;

		for(int i = 0; i < modenames.length; i++) {
			if(name.equals(modenames[i])) {
				return i;
			}
		}

		return -1;
	}

	/*
	 * 画面描画
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// 背景
		g.drawImage(ResourceHolder.imgMenu, 0, 0);

		// メニュー
		NormalFont.printFontGrid(1, 1,
									"MODE SELECT (" + (cursor + 1) + "/" + modenames.length + ")",
									NormalFont.COLOR_ORANGE);

		int maxmode = modenames.length;
		if(maxmode > MAX_MODE_IN_ONE_PAGE) maxmode = MAX_MODE_IN_ONE_PAGE;
		int y = 0;
		int num = (cursor / MAX_MODE_IN_ONE_PAGE) * MAX_MODE_IN_ONE_PAGE;

		for(int i = 0; i < maxmode; i++) {
			if(num + i < modenames.length) {
				NormalFont.printFontGrid(2, 3 + y, modenames[num + i], (cursor == num + i));
				if(cursor == num + i) NormalFont.printFontGrid(1, 3 + y, "b", NormalFont.COLOR_RED);
				y++;
			}
		}

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
	 * ゲーム状態の更新
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(!container.hasFocus()) {
			if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
			return;
		}

		// キー入力状態を更新
		GameKey.gamekey[0].update(container.getInput());

		// カーソル移動
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_UP)) {
			cursor--;
			if(cursor < 0) cursor = modenames.length - 1;
			ResourceHolder.soundManager.play("cursor");
		}
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_DOWN)) {
			cursor++;
			if(cursor > modenames.length - 1) cursor = 0;
			ResourceHolder.soundManager.play("cursor");
		}

		// 決定ボタン
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_A)) {
			ResourceHolder.soundManager.play("decide");
			NullpoMinoSlick.propGlobal.setProperty("name.mode", modenames[cursor]);
			NullpoMinoSlick.saveConfig();
			NullpoMinoSlick.stateInGame.startNewGame();
			game.enterState(StateInGame.ID);
		}

		// キャンセルボタン
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_B)) game.enterState(StateTitle.ID);

		// スクリーンショットボタン
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_SCREENSHOT)) ssflag = true;

		// 終了ボタン
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_QUIT)) container.exit();

		if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}
}
