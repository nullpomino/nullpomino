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
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Game screen state (Netplay)
 */
public class StateNetGame extends BasicGameState implements NetLobbyListener {
	/** Log */
	static Logger log = Logger.getLogger(StateNetGame.class);

	/** This state's ID */
	public static final int ID = 11;

	/** Game main class */
	public GameManager gameManager = null;

	/** Lobby */
	public NetLobbyFrame netLobby = null;

	/** Screenshot flag */
	protected boolean ssflag = false;

	/** Show background flag */
	protected boolean showbg = true;

	/** Previous ingame flag (Used by title-bar text change) */
	protected boolean prevInGameFlag = false;

	/** AppGameContainer (Used by title-bar text change) */
	protected AppGameContainer appContainer = null;

	/** Mode name to enter (null=Exit) */
	protected String strModeToEnter = "";

	/** Current game mode name */
	protected String modeName;

	/*
	 * Fetch this state's ID
	 */
	@Override
	public int getID() {
		return ID;
	}

	/*
	 * State initialization
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		appContainer = (AppGameContainer)container;
	}

	/*
	 * Called when entering this state
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		// Init variables
		showbg = NullpoMinoSlick.propConfig.getProperty("option.showbg", true);
		prevInGameFlag = false;

		// Observer stop
		NullpoMinoSlick.stopObserverClient();

		// 60FPS
		NullpoMinoSlick.altMaxFPS = 60;
		appContainer.setAlwaysRender(true);
		appContainer.setUpdateOnlyWhenVisible(false);

		// Clear each frame
		appContainer.setClearEachFrame(!showbg);

		// gameManager initialization
		gameManager = new GameManager(new RendererSlick());
		gameManager.receiver.setGraphics(appContainer.getGraphics());

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
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		if(gameManager != null) {
			gameManager.shutdown();
			gameManager = null;
		}
		if(netLobby != null) {
			netLobby.shutdown();
			netLobby = null;
		}
		ResourceHolder.bgmStop();
		container.setClearEachFrame(false);

		// FPS restore
		NullpoMinoSlick.altMaxFPS = NullpoMinoSlick.propConfig.getProperty("option.maxfps", 60);
		appContainer.setAlwaysRender(!NullpoMinoSlick.alternateFPSTiming);
		appContainer.setUpdateOnlyWhenVisible(true);

		// Reload global config (because it can change rules)
		NullpoMinoSlick.loadGlobalConfig();
	}

	/*
	 * Draw the game screen
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		try {
			// Game screen
			if((gameManager != null) && (gameManager.mode != null)) {
				gameManager.renderAll();
			}

			// FPS
			NullpoMinoSlick.drawFPS(container, true);
			// Screenshot
			if(ssflag) {
				NullpoMinoSlick.saveScreenShot(container, g);
				ssflag = false;
			}

			if(!NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep(true);
		} catch (NullPointerException e) {
			log.error("render NPE", e);
		} catch (Exception e) {
			log.error("render fail", e);
		}
	}

	/*
	 * Update game state
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		try {
			// Clear input states if game window does not have focus
			if(!container.hasFocus() || netLobby.isFocused()) {
				GameKey.gamekey[0].clear();
			}

			// TTF font
			if(ResourceHolder.ttfFont != null) ResourceHolder.ttfFont.loadGlyphs();

			// Title bar update
			if((gameManager != null) && (gameManager.engine != null) && (gameManager.engine.length > 0) && (gameManager.engine[0] != null)) {
				boolean nowInGame = gameManager.engine[0].isInGame;
				if(prevInGameFlag != nowInGame) {
					prevInGameFlag = nowInGame;
					updateTitleBarCaption();
				}
			}

			// Update key input states
			if(container.hasFocus() && !netLobby.isFocused()) {
				if((gameManager != null) && (gameManager.engine.length > 0) &&
				   (gameManager.engine[0] != null) && (gameManager.engine[0].isInGame)) {
					GameKey.gamekey[0].update(container.getInput(), true);
				} else {
					GameKey.gamekey[0].update(container.getInput(), false);
				}
			}

			if((gameManager != null) && (gameManager.mode != null)) {
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

			// Execute game loops
			if((gameManager != null) && (gameManager.mode != null)) {
				GameKey.gamekey[0].inputStatusUpdate(gameManager.engine[0].ctrl);
				gameManager.updateAll();

				if(gameManager.getQuitFlag()) {
					game.enterState(StateTitle.ID);
					return;
				}

				// Retry button
				if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_RETRY)) {
					gameManager.mode.netplayOnRetryKey(gameManager.engine[0], 0);
				}
			}

			// Screenshot button
			if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_SCREENSHOT) || GameKey.gamekey[1].isPushKey(GameKey.BUTTON_SCREENSHOT))
				ssflag = true;

			// Enter to new mode
			if(strModeToEnter == null) {
				enterNewMode(null);
				strModeToEnter = "";
			} else if(strModeToEnter.length() > 0) {
				enterNewMode(strModeToEnter);
				strModeToEnter = "";
			}

			if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep(true);
		} catch (NullPointerException e) {
			log.error("update NPE", e);

			try {
				if(gameManager.getQuitFlag()) {
					game.enterState(StateTitle.ID);
					return;
				}
			} catch (Throwable e2) {}
		} catch (Exception e) {
			log.error("update fail", e);

			try {
				if(gameManager.getQuitFlag()) {
					game.enterState(StateTitle.ID);
					return;
				}
			} catch (Throwable e2) {}
		}
	}

	/**
	 * Enter to a new mode
	 * @param newModeName Mode name
	 */
	private void enterNewMode(String newModeName) {
		NullpoMinoSlick.loadGlobalConfig();	// Reload global config file

		GameMode previousMode = gameManager.mode;
		GameMode newModeTemp = (newModeName == null) ? new NetDummyMode() : NullpoMinoSlick.modeManager.getMode(newModeName);

		if(newModeTemp == null) {
			log.error("Cannot find a mode:" + newModeName);
		} else if(newModeTemp instanceof NetDummyMode) {
			log.info("Enter new mode:" + newModeTemp.getName());

			NetDummyMode newMode = (NetDummyMode)newModeTemp;
			modeName = newMode.getName();

			if(previousMode != null) {
				if(gameManager.engine[0].ai != null) {
					gameManager.engine[0].ai.shutdown(gameManager.engine[0], 0);
				}
				previousMode.netplayUnload(netLobby);
			}
			gameManager.mode = newMode;
			gameManager.init();

			// Tuning
			gameManager.engine[0].owRotateButtonDefaultRight = NullpoMinoSlick.propGlobal.getProperty(0 + ".tuning.owRotateButtonDefaultRight", -1);
			gameManager.engine[0].owSkin = NullpoMinoSlick.propGlobal.getProperty(0 + ".tuning.owSkin", -1);
			gameManager.engine[0].owMinDAS = NullpoMinoSlick.propGlobal.getProperty(0 + ".tuning.owMinDAS", -1);
			gameManager.engine[0].owMaxDAS = NullpoMinoSlick.propGlobal.getProperty(0 + ".tuning.owMaxDAS", -1);
			gameManager.engine[0].owDasDelay = NullpoMinoSlick.propGlobal.getProperty(0 + ".tuning.owDasDelay", -1);
			gameManager.engine[0].owReverseUpDown = NullpoMinoSlick.propGlobal.getProperty(0 + ".tuning.owReverseUpDown", false);
			gameManager.engine[0].owMoveDiagonal = NullpoMinoSlick.propGlobal.getProperty(0 + ".tuning.owMoveDiagonal", -1);
			gameManager.engine[0].owBlockOutlineType = NullpoMinoSlick.propGlobal.getProperty(0 + ".tuning.owBlockOutlineType", -1);
			gameManager.engine[0].owBlockShowOutlineOnly = NullpoMinoSlick.propGlobal.getProperty(0 + ".tuning.owBlockShowOutlineOnly", -1);

			// Rule
			RuleOptions ruleopt = null;
			String rulename = NullpoMinoSlick.propGlobal.getProperty(0 + ".rule", "");
			if(gameManager.mode.getGameStyle() > 0) {
				rulename = NullpoMinoSlick.propGlobal.getProperty(0 + ".rule." + gameManager.mode.getGameStyle(), "");
			}
			if((rulename != null) && (rulename.length() > 0)) {
				log.info("Load rule options from " + rulename);
				ruleopt = GeneralUtil.loadRule(rulename);
			} else {
				log.info("Load rule options from setting file");
				ruleopt = new RuleOptions();
				ruleopt.readProperty(NullpoMinoSlick.propGlobal, 0);
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
			String aiName = NullpoMinoSlick.propGlobal.getProperty(0 + ".ai", "");
			if(aiName.length() > 0) {
				DummyAI aiObj = GeneralUtil.loadAIPlayer(aiName);
				gameManager.engine[0].ai = aiObj;
				gameManager.engine[0].aiMoveDelay = NullpoMinoSlick.propGlobal.getProperty(0 + ".aiMoveDelay", 0);
				gameManager.engine[0].aiThinkDelay = NullpoMinoSlick.propGlobal.getProperty(0 + ".aiThinkDelay", 0);
				gameManager.engine[0].aiUseThread = NullpoMinoSlick.propGlobal.getProperty(0 + ".aiUseThread", true);
				gameManager.engine[0].aiShowHint = NullpoMinoSlick.propGlobal.getProperty(0 + ".aiShowHint", false);
				gameManager.engine[0].aiPrethink = NullpoMinoSlick.propGlobal.getProperty(0 + ".aiPrethink", false);
			}

			// Initialization for each player
			for(int i = 0; i < gameManager.getPlayers(); i++) {
				gameManager.engine[i].init();
			}

			newMode.netplayInit(netLobby);
		} else {
			log.error("This mode does not support netplay:" + newModeName);
		}

		updateTitleBarCaption();
	}

	/**
	 * Update title bar text
	 */
	public void updateTitleBarCaption() {
		String strTitle = "NullpoMino Netplay - " + modeName;

		if(modeName.equals("NET-DUMMY")) {
			strTitle = "NullpoMino Netplay";
		} else if((gameManager != null) && (gameManager.engine != null) && (gameManager.engine.length > 0) && (gameManager.engine[0] != null)) {
			if(gameManager.engine[0].isInGame && !gameManager.replayMode && !gameManager.replayRerecord)
				strTitle = "[PLAY] NullpoMino Netplay - " + modeName;
			else
				strTitle = "[MENU] NullpoMino Netplay - " + modeName;
		}

		appContainer.setTitle(strTitle);
	}

	public void netlobbyOnDisconnect(NetLobbyFrame lobby, NetPlayerClient client, Throwable ex) {
		strModeToEnter = null;
	}

	public void netlobbyOnExit(NetLobbyFrame lobby) {
		if((gameManager != null) && (gameManager.engine.length > 0) && (gameManager.engine[0] != null)) {
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
