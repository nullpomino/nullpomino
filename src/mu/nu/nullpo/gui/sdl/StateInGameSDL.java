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

import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.game.subsystem.ai.DummyAI;
import mu.nu.nullpo.game.subsystem.mode.GameMode;
import mu.nu.nullpo.game.subsystem.wallkick.Wallkick;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

import sdljava.SDLException;
import sdljava.mixer.SDLMixer;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

/**
 * Game screen state (Local play)
 */
public class StateInGameSDL extends BaseStateSDL {
	/** Log */
	static Logger log = Logger.getLogger(StateInGameSDL.class);

	/** Game main class */
	protected GameManager gameManager;

	/** Game paused flag */
	protected boolean pause = false;

	/** Hide pause menu */
	protected boolean pauseMessageHide = false;

	/** Frame step enabled flag */
	protected boolean enableframestep = false;

	/** Fast forward */
	protected int fastforward = 0;

	/** Pause menu cursor position */
	protected int cursor = 0;

	/** Number of frames remaining until pause key can be used */
	protected int pauseFrame = 0;

	/** Previous ingame flag (Used by title-bar text change) */
	protected boolean prevInGameFlag = false;

	/** Current game mode name */
	protected String modeName;

	/*
	 * Called when entering this state
	 */
	@Override
	public void enter() throws SDLException {
		NullpoMinoSDL.disableAutoInputUpdate = true;
		NullpoMinoSDL.isInGame = true;
		enableframestep = NullpoMinoSDL.propConfig.getProperty("option.enableframestep", false);
		fastforward = 0;
		cursor = 0;
		prevInGameFlag = false;
	}

	/**
	 * Start a new game (Rule will be user-selected one))
	 */
	public void startNewGame() {
		startNewGame(null);
	}

	/**
	 * Start a new game
	 * @param strRulePath Rule file path (null if you want to use user-selected one)
	 */
	public void startNewGame(String strRulePath) {
		gameManager = new GameManager(new RendererSDL());
		pause = false;

		try {
			gameManager.receiver.setGraphics(SDLVideo.getVideoSurface());
		} catch (SDLException e) {
			log.warn("SDLException throwed", e);
		}

		// Mode
		modeName = NullpoMinoSDL.propGlobal.getProperty("name.mode", "");
		GameMode modeObj = NullpoMinoSDL.modeManager.getMode(modeName);
		if(modeObj == null) {
			log.warn("Couldn't find mode:" + modeName);
		} else {
			gameManager.mode = modeObj;
		}

		gameManager.init();

		// Initialization for each player
		for(int i = 0; i < gameManager.getPlayers(); i++) {
			// チューニング設定
			gameManager.engine[i].owRotateButtonDefaultRight = NullpoMinoSDL.propGlobal.getProperty(i + ".tuning.owRotateButtonDefaultRight", -1);
			gameManager.engine[i].owSkin = NullpoMinoSDL.propGlobal.getProperty(i + ".tuning.owSkin", -1);
			gameManager.engine[i].owMinDAS = NullpoMinoSDL.propGlobal.getProperty(i + ".tuning.owMinDAS", -1);
			gameManager.engine[i].owMaxDAS = NullpoMinoSDL.propGlobal.getProperty(i + ".tuning.owMaxDAS", -1);
			gameManager.engine[i].owDasDelay = NullpoMinoSDL.propGlobal.getProperty(i + ".tuning.owDasDelay", -1);
			gameManager.engine[0].owReverseUpDown = NullpoMinoSDL.propGlobal.getProperty(0 + ".tuning.owReverseUpDown", false);
			gameManager.engine[i].owMoveDiagonal = NullpoMinoSDL.propGlobal.getProperty(i + ".tuning.owMoveDiagonal", -1);

			// ルール
			RuleOptions ruleopt = null;
			String rulename = strRulePath;
			if(rulename == null) {
				rulename = NullpoMinoSDL.propGlobal.getProperty(i + ".rule", "");
				if(gameManager.mode.getGameStyle() > 0) {
					rulename = NullpoMinoSDL.propGlobal.getProperty(i + ".rule." + gameManager.mode.getGameStyle(), "");
				}
			}
			if((rulename != null) && (rulename.length() > 0)) {
				log.debug("Load rule options from " + rulename);
				ruleopt = GeneralUtil.loadRule(rulename);
			} else {
				log.debug("Load rule options from setting file");
				ruleopt = new RuleOptions();
				ruleopt.readProperty(NullpoMinoSDL.propGlobal, i);
			}
			gameManager.engine[i].ruleopt = ruleopt;

			// NEXT順生成アルゴリズム
			if((ruleopt.strRandomizer != null) && (ruleopt.strRandomizer.length() > 0)) {
				Randomizer randomizerObject = GeneralUtil.loadRandomizer(ruleopt.strRandomizer);
				gameManager.engine[i].randomizer = randomizerObject;
			}

			// Wallkick
			if((ruleopt.strWallkick != null) && (ruleopt.strWallkick.length() > 0)) {
				Wallkick wallkickObject = GeneralUtil.loadWallkick(ruleopt.strWallkick);
				gameManager.engine[i].wallkick = wallkickObject;
			}

			// AI
			String aiName = NullpoMinoSDL.propGlobal.getProperty(i + ".ai", "");
			if(aiName.length() > 0) {
				DummyAI aiObj = GeneralUtil.loadAIPlayer(aiName);
				gameManager.engine[i].ai = aiObj;
				gameManager.engine[i].aiMoveDelay = NullpoMinoSDL.propGlobal.getProperty(i + ".aiMoveDelay", 0);
				gameManager.engine[i].aiThinkDelay = NullpoMinoSDL.propGlobal.getProperty(i + ".aiThinkDelay", 0);
				gameManager.engine[i].aiUseThread = NullpoMinoSDL.propGlobal.getProperty(i + ".aiUseThread", true);
				gameManager.engine[i].aiShowHint = NullpoMinoSDL.propGlobal.getProperty(i + ".aiShowHint", false);
				gameManager.engine[i].aiPrethink = NullpoMinoSDL.propGlobal.getProperty(i + ".aiPrethink", false);
			}

			// Called at initialization
			gameManager.engine[i].init();
		}

		updateTitleBarCaption();
	}

	/**
	 * リプレイを読み込んで再生
	 * @param prop リプレイ dataの入ったプロパティセット
	 */
	public void startReplayGame(CustomProperties prop) {
		gameManager = new GameManager(new RendererSDL());
		gameManager.replayMode = true;
		gameManager.replayProp = prop;
		pause = false;

		try {
			gameManager.receiver.setGraphics(SDLVideo.getVideoSurface());
		} catch (SDLException e) {
			log.warn("SDLException throwed", e);
		}

		// Mode
		modeName = prop.getProperty("name.mode", "");
		GameMode modeObj = NullpoMinoSDL.modeManager.getMode(modeName);
		if(modeObj == null) {
			log.warn("Couldn't find mode:" + modeName);
		} else {
			gameManager.mode = modeObj;
		}

		gameManager.init();

		// Initialization for each player
		for(int i = 0; i < gameManager.getPlayers(); i++) {
			// ルール
			RuleOptions ruleopt = new RuleOptions();
			ruleopt.readProperty(prop, i);
			gameManager.engine[i].ruleopt = ruleopt;

			// NEXT順生成アルゴリズム
			if((ruleopt.strRandomizer != null) && (ruleopt.strRandomizer.length() > 0)) {
				Randomizer randomizerObject = GeneralUtil.loadRandomizer(ruleopt.strRandomizer);
				gameManager.engine[i].randomizer = randomizerObject;
			}

			// Wallkick
			if((ruleopt.strWallkick != null) && (ruleopt.strWallkick.length() > 0)) {
				Wallkick wallkickObject = GeneralUtil.loadWallkick(ruleopt.strWallkick);
				gameManager.engine[i].wallkick = wallkickObject;
			}

			// AI (リプレイ追記用）
			String aiName = NullpoMinoSDL.propGlobal.getProperty(i + ".ai", "");
			if(aiName.length() > 0) {
				DummyAI aiObj = GeneralUtil.loadAIPlayer(aiName);
				gameManager.engine[i].ai = aiObj;
				gameManager.engine[i].aiMoveDelay = NullpoMinoSDL.propGlobal.getProperty(i + ".aiMoveDelay", 0);
				gameManager.engine[i].aiThinkDelay = NullpoMinoSDL.propGlobal.getProperty(i + ".aiThinkDelay", 0);
				gameManager.engine[i].aiUseThread = NullpoMinoSDL.propGlobal.getProperty(i + ".aiUseThread", true);
				gameManager.engine[i].aiShowHint = NullpoMinoSDL.propGlobal.getProperty(i + ".aiShowHint", false);
				gameManager.engine[i].aiPrethink = NullpoMinoSDL.propGlobal.getProperty(i + ".aiPrethink", false);
			}

			// Called at initialization
			gameManager.engine[i].init();
		}

		updateTitleBarCaption();
	}

	/**
	 * Update title bar text
	 */
	public void updateTitleBarCaption() {
		String strTitle = "NullpoMino - " + modeName;

		if((gameManager != null) && (gameManager.engine != null) && (gameManager.engine.length > 0) && (gameManager.engine[0] != null)) {
			if(pause && !enableframestep)
				strTitle = "[PAUSE] NullpoMino - " + modeName;
			else if(gameManager.engine[0].isInGame && !gameManager.replayMode && !gameManager.replayRerecord)
				strTitle = "[PLAY] NullpoMino - " + modeName;
			else if(gameManager.replayMode && gameManager.replayRerecord)
				strTitle = "[RERECORD] NullpoMino - " + modeName;
			else if(gameManager.replayMode && !gameManager.replayRerecord)
				strTitle = "[REPLAY] NullpoMino - " + modeName;
			else
				strTitle = "[MENU] NullpoMino - " + modeName;
		}

		SDLVideo.wmSetCaption(strTitle, null);
	}

	/*
	 * Called when leaving this state
	 */
	@Override
	public void leave() throws SDLException {
		gameManager.shutdown();
		gameManager = null;
		NullpoMinoSDL.disableAutoInputUpdate = false;
		NullpoMinoSDL.isInGame = false;
	}

	/*
	 * Draw the game screen
	 */
	@Override
	public void render(SDLSurface screen) throws SDLException {
		if(gameManager != null) {
			gameManager.renderAll();

			if((gameManager.engine.length > 0) && (gameManager.engine[0] != null)) {
				int offsetX = gameManager.receiver.getFieldDisplayPositionX(gameManager.engine[0], 0);
				int offsetY = gameManager.receiver.getFieldDisplayPositionY(gameManager.engine[0], 0);

				// Pause menu
				if(pause && !enableframestep && !pauseMessageHide) {
					NormalFontSDL.printFont(offsetX + 12, offsetY + 188 + (cursor * 16), "b", NormalFontSDL.COLOR_RED);

					NormalFontSDL.printFont(offsetX + 28, offsetY + 188, "CONTINUE", (cursor == 0));
					NormalFontSDL.printFont(offsetX + 28, offsetY + 204, "RETRY", (cursor == 1));
					NormalFontSDL.printFont(offsetX + 28, offsetY + 220, "END", (cursor == 2));
					if(gameManager.replayMode && !gameManager.replayRerecord)
						NormalFontSDL.printFont(offsetX + 28, offsetY + 236, "RERECORD", (cursor == 3));
				}
				// Fast forward
				if(fastforward != 0)
					NormalFontSDL.printFont(offsetX, offsetY + 376, "e" + (fastforward + 1), NormalFontSDL.COLOR_ORANGE);
				if(gameManager.replayShowInvisible)
					NormalFontSDL.printFont(offsetX, offsetY + 392, "SHOW INVIS", NormalFontSDL.COLOR_ORANGE);
			}
		}
	}

	/*
	 * Update game state
	 */
	@Override
	public void update() throws SDLException {
		// Update key input states
		for(int i = 0; i < 2; i++) {
			int joynum = NullpoMinoSDL.joyUseNumber[i];

			boolean ingame = (gameManager != null) && (gameManager.engine.length > i) &&
							 (gameManager.engine[i] != null) && (gameManager.engine[i].isInGame) &&
							 (!pause || enableframestep);

			if((NullpoMinoSDL.joystickMax > 0) && (joynum >= 0) && (joynum < NullpoMinoSDL.joystickMax)) {
				GameKeySDL.gamekey[i].update(
						NullpoMinoSDL.keyPressedState,
						NullpoMinoSDL.joyPressedState[joynum],
						NullpoMinoSDL.joyAxisX[joynum],
						NullpoMinoSDL.joyAxisY[joynum],
						NullpoMinoSDL.joyHatState[joynum],
						ingame);
			} else {
				GameKeySDL.gamekey[i].update(NullpoMinoSDL.keyPressedState, ingame);
			}
		}

		// Title bar update
		if((gameManager != null) && (gameManager.engine != null) && (gameManager.engine.length > 0) && (gameManager.engine[0] != null)) {
			boolean nowInGame = gameManager.engine[0].isInGame;
			if(prevInGameFlag != nowInGame) {
				prevInGameFlag = nowInGame;
				updateTitleBarCaption();
			}
		}

		// Pause
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_PAUSE) || GameKeySDL.gamekey[1].isPushKey(GameKeySDL.BUTTON_PAUSE)) {
			if(!pause) {
				if((gameManager != null) && (gameManager.isGameActive()) && (pauseFrame <= 0)) {
					ResourceHolderSDL.soundManager.play("pause");
					pause = true;
					cursor = 0;
					if(!enableframestep) pauseFrame = 5;
					if(!enableframestep) ResourceHolderSDL.bgmPause();
				}
			} else {
				ResourceHolderSDL.soundManager.play("pause");
				pause = false;
				pauseFrame = 0;
				if(!enableframestep) ResourceHolderSDL.bgmResume();
			}
			updateTitleBarCaption();
		}
		// Pause menu
		else if(pause && !enableframestep && !pauseMessageHide) {
			// Cursor movement
			if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_UP)) {
				cursor--;

				if(cursor < 0) {
					if(gameManager.replayMode && !gameManager.replayRerecord)
						cursor = 3;
					else
						cursor = 2;
				}

				ResourceHolderSDL.soundManager.play("cursor");
			}
			if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_DOWN)) {
				cursor++;
				if(cursor > 3) cursor = 0;

				if((!gameManager.replayMode || gameManager.replayRerecord) && (cursor > 2))
					cursor = 0;

				ResourceHolderSDL.soundManager.play("cursor");
			}

			// Confirm
			if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_A)) {
				ResourceHolderSDL.soundManager.play("decide");
				if(cursor == 0) {
					// 再開
					pause = false;
					pauseFrame = 0;
					GameKeySDL.gamekey[0].clear();
					ResourceHolderSDL.bgmResume();
				} else if(cursor == 1) {
					// リトライ
					ResourceHolderSDL.bgmStop();
					pause = false;
					gameManager.reset();
				} else if(cursor == 2) {
					// 終了
					ResourceHolderSDL.bgmStop();
					NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_TITLE);
					return;
				} else if(cursor == 3) {
					// Replay re-record
					gameManager.replayRerecord = true;
					ResourceHolderSDL.soundManager.play("tspin1");
					cursor = 0;
				}
				updateTitleBarCaption();
			}
			// Unpause by cancel key
			else if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_B) && (pauseFrame <= 0)) {
				ResourceHolderSDL.soundManager.play("pause");
				pause = false;
				pauseFrame = 5;
				GameKeySDL.gamekey[0].clear();
				ResourceHolderSDL.bgmResume();
				updateTitleBarCaption();
			}
		}
		if(pauseFrame > 0) pauseFrame--;

		// Hide pause menu
		pauseMessageHide = GameKeySDL.gamekey[0].isPressKey(GameKeySDL.BUTTON_C);

		if(gameManager.replayMode && !gameManager.replayRerecord && gameManager.engine[0].gameActive) {
			// Replay speed
			if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_LEFT)) {
				if(fastforward > 0) {
					fastforward--;
				}
			}
			if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_RIGHT)) {
				if(fastforward < 98) {
					fastforward++;
				}
			}

			// Replay re-record
			if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_D)) {
				gameManager.replayRerecord = true;
				ResourceHolderSDL.soundManager.play("tspin1");
				cursor = 0;
			}
			// Replay re-record
			if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_E)) {
				gameManager.replayShowInvisible = !gameManager.replayShowInvisible;
				ResourceHolderSDL.soundManager.play("tspin1");
				cursor = 0;
			}
		} else {
			fastforward = 0;
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

		// Execute game loops
		if(!pause || (GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_FRAMESTEP) && enableframestep)) {
			if(gameManager != null) {
				for(int i = 0; i < Math.min(gameManager.getPlayers(), 2); i++) {
					if(!gameManager.replayMode || gameManager.replayRerecord || !gameManager.engine[i].gameActive) {
						GameKeySDL.gamekey[i].inputStatusUpdate(gameManager.engine[i].ctrl);
					}
				}

				for(int i = 0; i <= fastforward; i++) gameManager.updateAll();
			}
		}

		if(gameManager != null) {
			// Retry button
			if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_RETRY) || GameKeySDL.gamekey[1].isPushKey(GameKeySDL.BUTTON_RETRY)) {
				ResourceHolderSDL.bgmStop();
				pause = false;
				gameManager.reset();
			}

			// Return to title
			if(gameManager.getQuitFlag() ||
			   GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_GIVEUP) ||
			   GameKeySDL.gamekey[1].isPushKey(GameKeySDL.BUTTON_GIVEUP))
			{
				ResourceHolderSDL.bgmStop();
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_TITLE);
				return;
			}
		}
	}
}
