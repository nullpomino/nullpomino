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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.game_host.hebo.nullpomino.game.component.RuleOptions;
import org.game_host.hebo.nullpomino.game.net.NetPlayerClient;
import org.game_host.hebo.nullpomino.game.net.NetRoomInfo;
import org.game_host.hebo.nullpomino.game.play.GameManager;
import org.game_host.hebo.nullpomino.game.subsystem.ai.AIPlayer;
import org.game_host.hebo.nullpomino.game.subsystem.mode.GameMode;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;
import org.game_host.hebo.nullpomino.game.subsystem.wallkick.Wallkick;
import org.game_host.hebo.nullpomino.gui.net.NetLobbyFrame;
import org.game_host.hebo.nullpomino.gui.net.NetLobbyListener;
import org.game_host.hebo.nullpomino.util.GeneralUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * ネットゲーム画面のステート
 */
public class StateNetGame extends BasicGameState implements NetLobbyListener {
	/** ログ */
	static Logger log = Logger.getLogger(StateNetGame.class);

	/** このステートのID */
	public static final int ID = 11;

	/** ゲームのメインクラス */
	public GameManager gameManager = null;

	/** ロビー画面 */
	public NetLobbyFrame netLobby = null;

	/** FPS表示 */
	protected boolean showfps = true;

	/** スクリーンショット撮影フラグ */
	protected boolean ssflag = false;

	/** AppGameContainer（これを使ってタイトルバーを変える） */
	protected AppGameContainer appContainer = null;

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
		appContainer = (AppGameContainer)container;
	}

	/*
	 * このステートに入ったときの処理
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		// オブザーバー停止
		NullpoMinoSlick.stopObserverClient();

		// 60FPS
		if(NullpoMinoSlick.useAlternateFPSSleep) {
			NullpoMinoSlick.alternateTargetFPS = 60;
		} else {
			appContainer.setTargetFrameRate(60);
		}
		appContainer.setAlwaysRender(true);
		appContainer.setUpdateOnlyWhenVisible(false);

		// gameManager初期化
		gameManager = new GameManager(new RendererSlick());
		gameManager.receiver.setGraphics(appContainer.getGraphics());

		// モード
		String modeName = "NET-VS-BATTLE";
		GameMode modeObj = NullpoMinoSlick.modeManager.getMode(modeName);
		if(modeObj == null) {
			log.error("Couldn't find mode:" + modeName);
		} else {
			appContainer.setTitle("NullpoMino - " + modeName);
			gameManager.mode = modeObj;
		}

		gameManager.init();

		// チューニング設定
		gameManager.engine[0].owRotateButtonDefaultRight = NullpoMinoSlick.propGlobal.getProperty(0 + ".tuning.owRotateButtonDefaultRight", -1);
		gameManager.engine[0].owSkin = NullpoMinoSlick.propGlobal.getProperty(0 + ".tuning.owSkin", -1);
		gameManager.engine[0].owMinDAS = NullpoMinoSlick.propGlobal.getProperty(0 + ".tuning.owMinDAS", -1);
		gameManager.engine[0].owMaxDAS = NullpoMinoSlick.propGlobal.getProperty(0 + ".tuning.owMaxDAS", -1);
		gameManager.engine[0].owDasDelay = NullpoMinoSlick.propGlobal.getProperty(0 + ".tuning.owDasDelay", -1);

		// ルール
		RuleOptions ruleopt = null;
		String rulename = NullpoMinoSlick.propGlobal.getProperty(0 + ".rule", "");

		if((rulename != null) && (rulename.length() > 0)) {
			log.info("Load rule options from " + rulename);
			ruleopt = GeneralUtil.loadRule(rulename);
		} else {
			log.info("Load rule options from setting file");
			ruleopt = new RuleOptions();
			ruleopt.readProperty(NullpoMinoSlick.propGlobal, 0);
		}
		gameManager.engine[0].ruleopt = ruleopt;

		// NEXT順生成アルゴリズム
		if((ruleopt.strRandomizer != null) && (ruleopt.strRandomizer.length() > 0)) {
			Randomizer randomizerObject = GeneralUtil.loadRandomizer(ruleopt.strRandomizer);
			gameManager.engine[0].randomizer = randomizerObject;
		}

		// 壁蹴り
		if((ruleopt.strWallkick != null) && (ruleopt.strWallkick.length() > 0)) {
			Wallkick wallkickObject = GeneralUtil.loadWallkick(ruleopt.strWallkick);
			gameManager.engine[0].wallkick = wallkickObject;
		}

		// AI
		String aiName = NullpoMinoSlick.propGlobal.getProperty(0 + ".ai", "");
		if(aiName.length() > 0) {
			AIPlayer aiObj = GeneralUtil.loadAIPlayer(aiName);
			gameManager.engine[0].ai = aiObj;
			gameManager.engine[0].aiMoveDelay = NullpoMinoSlick.propGlobal.getProperty(0 + ".aiMoveDelay", 0);
			gameManager.engine[0].aiThinkDelay = NullpoMinoSlick.propGlobal.getProperty(0 + ".aiThinkDelay", 0);
			gameManager.engine[0].aiUseThread = NullpoMinoSlick.propGlobal.getProperty(0 + ".aiUseThread", true);
		}

		// 各プレイヤーの初期化
		for(int i = 0; i < gameManager.getPlayers(); i++) {
			gameManager.engine[i].init();
		}

		// ロビー初期化
		netLobby = new NetLobbyFrame();
		netLobby.addListener(this);
		gameManager.mode.netplayInit(netLobby);
		netLobby.init();
		netLobby.setVisible(true);
	}

	/*
	 * このステートを去るときの処理
	 */
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		if(gameManager != null) {
			gameManager.shutdown();
			gameManager = null;
		}
		if(netLobby != null) {
			netLobby.shutdown();
			netLobby = null;
		}

		// FPS復帰
		if(NullpoMinoSlick.useAlternateFPSSleep) {
			NullpoMinoSlick.alternateTargetFPS = NullpoMinoSlick.propConfig.getProperty("option.maxfps", 60);
		} else {
			appContainer.setTargetFrameRate(NullpoMinoSlick.propConfig.getProperty("option.maxfps", 60));
		}
		appContainer.setAlwaysRender(false);
		appContainer.setUpdateOnlyWhenVisible(true);
	}

	/*
	 * ゲーム画面の描画
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		try {
			// ゲーム画面
			if(gameManager != null) {
				gameManager.renderAll();
			}

			// FPS
			NullpoMinoSlick.drawFPS(container);
			// スクリーンショット
			if(ssflag) {
				NullpoMinoSlick.saveScreenShot(container, g);
				ssflag = false;
			}

			NullpoMinoSlick.alternateFPSSleep();
		} catch (Exception e) {
			log.error("render fail", e);
		}
	}

	/*
	 * ゲーム状態の更新
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		try {
			// TTFフォント描画
			if(ResourceHolder.ttfFont != null) ResourceHolder.ttfFont.loadGlyphs();

			// キー入力状態を更新
			GameKey.gamekey[0].update(container.getInput());

			if(gameManager != null) {
				// BGM
				if(ResourceHolder.bgmPlaying != gameManager.bgmStatus.bgm) {
					ResourceHolder.bgmStart(gameManager.bgmStatus.bgm);
				}
				if(ResourceHolder.bgmIsPlaying()) {
					int basevolume = NullpoMinoSlick.propConfig.getProperty("option.bgmvolume", 128);
					float basevolume2 = basevolume / (float)128;
					float newvolume = gameManager.bgmStatus.volume * basevolume2;
					if(newvolume < 0f) newvolume = 0f;
					if(newvolume > 1f) newvolume = 1f;
					container.setMusicVolume(newvolume);
					if(newvolume <= 0f) ResourceHolder.bgmStop();
				}
			}

			// ゲームの処理を実行
			if(gameManager != null) {
				GameKey.gamekey[0].inputStatusUpdate(gameManager.engine[0].ctrl);
				gameManager.updateAll();

				if(gameManager.getQuitFlag()) {
					ResourceHolder.bgmStop();
					game.enterState(StateTitle.ID);
					return;
				}
			}

			// スクリーンショットボタン
			if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_SCREENSHOT) || GameKey.gamekey[1].isPushKey(GameKey.BUTTON_SCREENSHOT))
				ssflag = true;
		} catch (Exception e) {
			log.error("update fail", e);
		}
	}

	public void netlobbyOnDisconnect(NetLobbyFrame lobby, NetPlayerClient client, Throwable ex) {
	}

	public void netlobbyOnExit(NetLobbyFrame lobby) {
		if(gameManager != null) {
			gameManager.engine[0].quitflag = true;
		}
	}

	public void netlobbyOnInit(NetLobbyFrame lobby) {
	}

	public void netlobbyOnLoginOK(NetLobbyFrame lobby, NetPlayerClient client) {
	}

	public void netlobbyOnMessage(NetLobbyFrame lobby, NetPlayerClient client, String[] message) throws IOException {
	}

	public void netlobbyOnRoomJoin(NetLobbyFrame lobby, NetPlayerClient client, NetRoomInfo roomInfo) {
	}

	public void netlobbyOnRoomLeave(NetLobbyFrame lobby, NetPlayerClient client) {
	}
}
