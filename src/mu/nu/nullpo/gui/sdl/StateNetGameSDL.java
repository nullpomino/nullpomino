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

import java.io.IOException;

import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.net.NetPlayerClient;
import mu.nu.nullpo.game.net.NetRoomInfo;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.game.subsystem.ai.DummyAI;
import mu.nu.nullpo.game.subsystem.mode.GameMode;
import mu.nu.nullpo.game.subsystem.mode.NetDummyMode;
import mu.nu.nullpo.game.subsystem.wallkick.Wallkick;
import mu.nu.nullpo.gui.net.NetLobbyFrame;
import mu.nu.nullpo.gui.net.NetLobbyListener;
import mu.nu.nullpo.util.GeneralUtil;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

import sdljava.SDLException;
import sdljava.mixer.SDLMixer;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

/**
 * ネットゲーム画面のステート
 */
public class StateNetGameSDL extends BaseStateSDL implements NetLobbyListener {
	/** Log */
	static final Logger log = Logger.getLogger(StateNetGameSDL.class);

	/** ゲームのメインクラス */
	protected GameManager gameManager;

	/** ロビー画面 */
	public NetLobbyFrame netLobby;

	/** Mode name to enter (null=Exit) */
	protected String strModeToEnter = "";

	/*
	 * Called when entering this state
	 */
	@Override
	public void enter() throws SDLException {
		NullpoMinoSDL.disableAutoInputUpdate = true;

		// Observer停止
		NullpoMinoSDL.stopObserverClient();

		// 60FPS
		NullpoMinoSDL.maxFPS = 60;
		NullpoMinoSDL.allowQuit = false;

		// gameManager initialization
		gameManager = new GameManager(new RendererSDL());
		try {
			gameManager.receiver.setGraphics(SDLVideo.getVideoSurface());
		} catch (SDLException e) {
			log.warn("SDLException throwed", e);
		}

		// Lobby initialization
		netLobby = new NetLobbyFrame();
		netLobby.addListener(this);

		// Mode initialization
		enterNewMode(null);

		// Lobby start
		netLobby.init();
		netLobby.setVisible(true);
	}

	/*
	 * Called when leaving this state
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
		NullpoMinoSDL.disableAutoInputUpdate = false;
	}

	/*
	 * Draw the game screen
	 */
	@Override
	public void render(SDLSurface screen) throws SDLException {
		try {
			if(gameManager != null) {
				gameManager.renderAll();
			}
		} catch (NullPointerException e) {
			log.error("render NPE", e);
		} catch (Exception e) {
			log.error("render fail", e);
		}
	}

	/*
	 * Update game state
	 */
	@Override
	public void update() throws SDLException {
		try {
			// Update key input states
			int joynum = NullpoMinoSDL.joyUseNumber[0];

			boolean ingame = (gameManager != null) && (gameManager.engine.length > 0) &&
							 (gameManager.engine[0] != null) && (gameManager.engine[0].isInGame);

			if((NullpoMinoSDL.joystickMax > 0) && (joynum >= 0) && (joynum < NullpoMinoSDL.joystickMax)) {
				GameKeySDL.gamekey[0].update(
						NullpoMinoSDL.keyPressedState,
						NullpoMinoSDL.joyPressedState[joynum],
						NullpoMinoSDL.joyAxisX[joynum],
						NullpoMinoSDL.joyAxisY[joynum],
						NullpoMinoSDL.joyHatState[joynum],
						ingame);
			} else {
				GameKeySDL.gamekey[0].update(NullpoMinoSDL.keyPressedState, ingame);
			}

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
			if((gameManager != null) && (gameManager.mode != null)) {
				GameKeySDL.gamekey[0].inputStatusUpdate(gameManager.engine[0].ctrl);
				gameManager.updateAll();

				if(gameManager.getQuitFlag()) {
					ResourceHolderSDL.bgmStop();
					NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_TITLE);
				}

				// Retry button
				if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_RETRY)) {
					gameManager.mode.netplayOnRetryKey(gameManager.engine[0], 0);
				}
			}

			// Enter to new mode
			if(strModeToEnter == null) {
				enterNewMode(null);
				strModeToEnter = "";
			} else if(strModeToEnter.length() > 0) {
				enterNewMode(strModeToEnter);
				strModeToEnter = "";
			}
		} catch (NullPointerException e) {
			log.error("update NPE", e);
		} catch (Exception e) {
			log.error("update fail", e);
		}
	}

	/**
	 * Enter to a new mode
	 * @param modeName Mode name
	 */
	private void enterNewMode(String modeName) {
		GameMode previousMode = gameManager.mode;
		GameMode newModeTemp = (modeName == null) ? new NetDummyMode() : NullpoMinoSDL.modeManager.getMode(modeName);

		if(newModeTemp == null) {
			log.error("Cannot find a mode:" + modeName);
		} else if(newModeTemp instanceof NetDummyMode) {
			log.info("Enter new mode:" + newModeTemp.getName());

			NetDummyMode newMode = (NetDummyMode)newModeTemp;
			SDLVideo.wmSetCaption("NullpoMino - " + newMode.getName(), null);

			if(previousMode != null) previousMode.netplayUnload(netLobby);
			gameManager.mode = newMode;
			gameManager.init();

			// Tuning
			gameManager.engine[0].owRotateButtonDefaultRight = NullpoMinoSDL.propGlobal.getProperty(0 + ".tuning.owRotateButtonDefaultRight", -1);
			gameManager.engine[0].owSkin = NullpoMinoSDL.propGlobal.getProperty(0 + ".tuning.owSkin", -1);
			gameManager.engine[0].owMinDAS = NullpoMinoSDL.propGlobal.getProperty(0 + ".tuning.owMinDAS", -1);
			gameManager.engine[0].owMaxDAS = NullpoMinoSDL.propGlobal.getProperty(0 + ".tuning.owMaxDAS", -1);
			gameManager.engine[0].owDasDelay = NullpoMinoSDL.propGlobal.getProperty(0 + ".tuning.owDasDelay", -1);
			gameManager.engine[0].owReverseUpDown = NullpoMinoSDL.propGlobal.getProperty(0 + ".tuning.owReverseUpDown", false);
			gameManager.engine[0].owMoveDiagonal = NullpoMinoSDL.propGlobal.getProperty(0 + ".tuning.owMoveDiagonal", -1);

			// Rule
			RuleOptions ruleopt = null;
			String rulename = NullpoMinoSDL.propGlobal.getProperty(0 + ".rule", "");
			if(gameManager.mode.getGameStyle() > 0) {
				rulename = NullpoMinoSDL.propGlobal.getProperty(0 + ".rule." + gameManager.mode.getGameStyle(), "");
			}
			if((rulename != null) && (rulename.length() > 0)) {
				log.info("Load rule options from " + rulename);
				ruleopt = GeneralUtil.loadRule(rulename);
			} else {
				log.info("Load rule options from setting file");
				ruleopt = new RuleOptions();
				ruleopt.readProperty(NullpoMinoSDL.propGlobal, 0);
			}
			gameManager.engine[0].ruleopt = ruleopt;

			// Randomizer
			if((ruleopt.strRandomizer != null) && (ruleopt.strRandomizer.length() > 0)) {
				Randomizer randomizerObject = GeneralUtil.loadRandomizer(ruleopt.strRandomizer);
				gameManager.engine[0].randomizer = randomizerObject;
			}

			// Wallkick
			if((ruleopt.strWallkick != null) && (ruleopt.strWallkick.length() > 0)) {
				Wallkick wallkickObject = GeneralUtil.loadWallkick(ruleopt.strWallkick);
				gameManager.engine[0].wallkick = wallkickObject;
			}

			// AI
			String aiName = NullpoMinoSDL.propGlobal.getProperty(0 + ".ai", "");
			if(aiName.length() > 0) {
				DummyAI aiObj = GeneralUtil.loadAIPlayer(aiName);
				gameManager.engine[0].ai = aiObj;
				gameManager.engine[0].aiMoveDelay = NullpoMinoSDL.propGlobal.getProperty(0 + ".aiMoveDelay", 0);
				gameManager.engine[0].aiThinkDelay = NullpoMinoSDL.propGlobal.getProperty(0 + ".aiThinkDelay", 0);
				gameManager.engine[0].aiUseThread = NullpoMinoSDL.propGlobal.getProperty(0 + ".aiUseThread", true);
				gameManager.engine[0].aiShowHint = NullpoMinoSDL.propGlobal.getProperty(0 + ".aiShowHint", false);
			}

			// Initialization for each player
			for(int i = 0; i < gameManager.getPlayers(); i++) {
				gameManager.engine[i].init();
			}

			newMode.netplayInit(netLobby);
		} else {
			log.error("This mode does not support netplay:" + modeName);
		}
	}

	public void netlobbyOnDisconnect(NetLobbyFrame lobby, NetPlayerClient client, Throwable ex) {
		strModeToEnter = null;
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
		//enterNewMode(roomInfo.strMode);
		strModeToEnter = roomInfo.strMode;
	}

	public void netlobbyOnRoomLeave(NetLobbyFrame lobby, NetPlayerClient client) {
		//enterNewMode(null);
		strModeToEnter = null;
	}
}
