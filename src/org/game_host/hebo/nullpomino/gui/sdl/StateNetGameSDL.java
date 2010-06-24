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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.game_host.hebo.nullpomino.game.component.RuleOptions;
import org.game_host.hebo.nullpomino.game.net.NetPlayerClient;
import org.game_host.hebo.nullpomino.game.net.NetRoomInfo;
import org.game_host.hebo.nullpomino.game.play.GameManager;
import org.game_host.hebo.nullpomino.game.subsystem.ai.AIPlayer;
import org.game_host.hebo.nullpomino.game.subsystem.mode.GameMode;
import org.game_host.hebo.nullpomino.game.subsystem.randomizer.Randomizer;
import org.game_host.hebo.nullpomino.game.subsystem.wallkick.Wallkick;
import org.game_host.hebo.nullpomino.gui.net.NetLobbyFrame;
import org.game_host.hebo.nullpomino.gui.net.NetLobbyListener;
import org.game_host.hebo.nullpomino.util.GeneralUtil;

import sdljava.SDLException;
import sdljava.mixer.SDLMixer;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

/**
 * ネットゲーム画面のステート
 */
public class StateNetGameSDL extends BaseStateSDL implements NetLobbyListener {
	/** ログ */
	static final Logger log = Logger.getLogger(StateNetGameSDL.class);

	/** ゲームのメインクラス */
	protected GameManager gameManager;

	/** ロビー画面 */
	public NetLobbyFrame netLobby;

	/*
	 * このステートに入ったときの処理
	 */
	@Override
	public void enter() throws SDLException {
		// オブザーバー停止
		NullpoMinoSDL.stopObserverClient();

		// 60FPS
		NullpoMinoSDL.maxFPS = 60;
		NullpoMinoSDL.allowQuit = false;

		// gameManager初期化
		gameManager = new GameManager(new RendererSDL());
		try {
			gameManager.receiver.setGraphics(SDLVideo.getVideoSurface());
		} catch (SDLException e) {
			log.warn("SDLException throwed", e);
		}

		// モード
		String modeName = "NET-VS-BATTLE";
		GameMode modeObj = NullpoMinoSDL.modeManager.getMode(modeName);
		if(modeObj == null) {
			log.warn("Couldn't find mode:" + modeName);
		} else {
			SDLVideo.wmSetCaption("NullpoMino - " + modeName, null);
			gameManager.mode = modeObj;
		}

		gameManager.init();

		// チューニング設定
		gameManager.engine[0].owRotateButtonDefaultRight = NullpoMinoSDL.propGlobal.getProperty(0 + ".tuning.owRotateButtonDefaultRight", -1);
		gameManager.engine[0].owSkin = NullpoMinoSDL.propGlobal.getProperty(0 + ".tuning.owSkin", -1);
		gameManager.engine[0].owMinDAS = NullpoMinoSDL.propGlobal.getProperty(0 + ".tuning.owMinDAS", -1);
		gameManager.engine[0].owMaxDAS = NullpoMinoSDL.propGlobal.getProperty(0 + ".tuning.owMaxDAS", -1);
		gameManager.engine[0].owDasDelay = NullpoMinoSDL.propGlobal.getProperty(0 + ".tuning.owDasDelay", -1);

		// ルール
		RuleOptions ruleopt = null;
		String rulename = NullpoMinoSDL.propGlobal.getProperty(0 + ".rule", "");

		if((rulename != null) && (rulename.length() > 0)) {
			log.debug("Load rule options from " + rulename);
			ruleopt = GeneralUtil.loadRule(rulename);
		} else {
			log.debug("Load rule options from setting file");
			ruleopt = new RuleOptions();
			ruleopt.readProperty(NullpoMinoSDL.propGlobal, 0);
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
		String aiName = NullpoMinoSDL.propGlobal.getProperty(0 + ".ai", "");
		if(aiName.length() > 0) {
			AIPlayer aiObj = GeneralUtil.loadAIPlayer(aiName);
			gameManager.engine[0].ai = aiObj;
			gameManager.engine[0].aiMoveDelay = NullpoMinoSDL.propGlobal.getProperty(0 + ".aiMoveDelay", 0);
			gameManager.engine[0].aiThinkDelay = NullpoMinoSDL.propGlobal.getProperty(0 + ".aiThinkDelay", 0);
			gameManager.engine[0].aiUseThread = NullpoMinoSDL.propGlobal.getProperty(0 + ".aiUseThread", true);
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
	public void leave() throws SDLException {
		if(gameManager != null) {
			gameManager.shutdown();
			gameManager = null;
		}
		if(netLobby != null) {
			netLobby.shutdown();
			netLobby = null;
		}

		// FPS復帰
		NullpoMinoSDL.maxFPS = NullpoMinoSDL.propConfig.getProperty("option.maxfps", 60);
		NullpoMinoSDL.allowQuit = true;
	}

	/*
	 * ゲーム画面の描画
	 */
	@Override
	public void render(SDLSurface screen) throws SDLException {
		try {
			if(gameManager != null) {
				gameManager.renderAll();
			}
		} catch (Exception e) {
			log.error("render fail", e);
		}
	}

	/*
	 * ゲーム状態の更新
	 */
	@Override
	public void update() throws SDLException {
		try {
			if(gameManager != null) {
				// BGM
				if(ResourceHolderSDL.bgmPlaying != gameManager.bgmStatus.bgm) {
					ResourceHolderSDL.bgmStart(gameManager.bgmStatus.bgm);
				}
				if(ResourceHolderSDL.bgmIsPlaying()) {
					int basevolume = NullpoMinoSDL.propConfig.getProperty("option.bgmvolume", 128);
					float basevolume2 = (float)basevolume / 128;
					int newvolume = (int)(128 * (gameManager.bgmStatus.volume * basevolume2));
					if(newvolume < 0) newvolume = 0;
					if(newvolume > 128) newvolume = 128;
					SDLMixer.volumeMusic(newvolume);
					if(newvolume <= 0) ResourceHolderSDL.bgmStop();
				}
			}

			// ゲームの処理を実行
			if(gameManager != null) {
				GameKeySDL.gamekey[0].inputStatusUpdate(gameManager.engine[0].ctrl);
				gameManager.updateAll();

				if(gameManager.getQuitFlag()) {
					ResourceHolderSDL.bgmStop();
					NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_TITLE);
				}
			}
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
