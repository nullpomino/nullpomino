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

import mu.nu.nullpo.game.play.GameManager;

import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * ロード画面のステート
 */
public class StateLoading extends BasicGameState {
	/** このステートのID */
	public static final int ID = 0;

	/** Log */
	static Logger log = Logger.getLogger(StateLoading.class);

	/** プリロード進行度 */
	protected int preloadCount, preloadSet;

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
	 * このステートに入ったときの処理
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		preloadCount = 0;
		preloadSet = 0;

		//  input 関連をInitialization
		GameKey.initGlobalGameKey();
		GameKey.gamekey[0].loadConfig(NullpoMinoSlick.propConfig);
		GameKey.gamekey[1].loadConfig(NullpoMinoSlick.propConfig);
		MouseInput.initializeMouseInput();
		

		// 設定を反映させる
		NullpoMinoSlick.setGeneralConfig();

		// 画像などを読み込み
		try {
			ResourceHolder.load();
		} catch(Throwable e) {
			log.error("Resource load failed", e);
		}
	}

	/*
	 * 画面描画
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// 巨大な画像をあらかじめ画面に描画することでメモリにキャッシュさせる
		if(preloadSet == 0) {
			if(preloadCount < ResourceHolder.BLOCK_BREAK_MAX) {
				try {
					ResourceHolder.imgBreak[preloadCount][0].draw(0, 0);
				} catch (Exception e) {}
				preloadCount++;
			} else {
				preloadCount = 0;
				preloadSet++;
			}
		}
		if(preloadSet == 1) {
			if(preloadCount < ResourceHolder.BLOCK_BREAK_MAX) {
				try {
					ResourceHolder.imgBreak[preloadCount][1].draw(0, 0);
				} catch (Exception e) {}
				preloadCount++;
			} else {
				preloadCount = 0;
				preloadSet++;
			}
		}
		if(preloadSet == 2) {
			if(preloadCount < ResourceHolder.PERASE_MAX) {
				try {
					ResourceHolder.imgPErase[preloadCount].draw(0, 0);
				} catch (Exception e) {}
				preloadCount++;
			} else {
				preloadCount = 0;
				preloadSet++;
			}
		}
		if(preloadSet == 3) {
			ResourceHolder.imgFont.draw(0, 0);
			preloadSet++;
		}

		g.setColor(Color.black);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());
	}

	/*
	 * ゲームの内部状態の更新
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(preloadSet > 2) {
			// タイトルバー更新
			if(container instanceof AppGameContainer) {
				((AppGameContainer) container).setTitle("NullpoMino version" + GameManager.getVersionString());
				((AppGameContainer) container).setUpdateOnlyWhenVisible(true);
			}

			// 初期設定
			if(NullpoMinoSlick.propConfig.getProperty("option.firstSetupMode", true) == true) {
				game.enterState(StateConfigKeyboard.ID);
			}
			// タイトル
			else {
				game.enterState(StateTitle.ID);
			}
		}
	}
}
