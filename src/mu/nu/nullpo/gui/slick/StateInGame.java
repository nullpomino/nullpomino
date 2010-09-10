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

import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.game.subsystem.ai.DummyAI;
import mu.nu.nullpo.game.subsystem.mode.GameMode;
import mu.nu.nullpo.game.subsystem.wallkick.Wallkick;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * ゲーム画面のステート
 */
public class StateInGame extends BasicGameState {
	/** This state's ID */
	public static final int ID = 2;

	/** ゲームのメインクラス */
	public GameManager gameManager = null;

	/** Log */
	static Logger log = Logger.getLogger(StateInGame.class);

	/** ポーズ flag */
	protected boolean pause = false;

	/** ポーズメッセージ非表示 */
	protected boolean pauseMessageHide = false;

	/**  frame ステップ有効 flag */
	protected boolean enableframestep = false;

	/** 倍速Mode */
	protected int fastforward = 0;

	/** Pause menuのCursor position */
	protected int cursor = 0;

	/** Screenshot撮影 flag */
	protected boolean ssflag = false;

	/** AppGameContainer (これを使ってタイトルバーを変える) */
	protected AppGameContainer appContainer = null;

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
		enableframestep = NullpoMinoSlick.propConfig.getProperty("option.enableframestep", false);
		fastforward = 0;
		cursor = 0;
	}

	/**
	 * 新しいゲームの開始処理
	 */
	public void startNewGame() {
		gameManager = new GameManager(new RendererSlick());
		pause = false;

		gameManager.receiver.setGraphics(appContainer.getGraphics());

		// Mode
		String modeName = NullpoMinoSlick.propGlobal.getProperty("name.mode", "");
		GameMode modeObj = NullpoMinoSlick.modeManager.getMode(modeName);
		if(modeObj == null) {
			log.error("Couldn't find mode:" + modeName);
		} else {
			appContainer.setTitle("NullpoMino - " + modeName);
			gameManager.mode = modeObj;
		}

		gameManager.init();

		// Initialization for each player
		for(int i = 0; i < gameManager.getPlayers(); i++) {
			// チューニング設定
			gameManager.engine[i].owRotateButtonDefaultRight = NullpoMinoSlick.propGlobal.getProperty(i + ".tuning.owRotateButtonDefaultRight", -1);
			gameManager.engine[i].owSkin = NullpoMinoSlick.propGlobal.getProperty(i + ".tuning.owSkin", -1);
			gameManager.engine[i].owMinDAS = NullpoMinoSlick.propGlobal.getProperty(i + ".tuning.owMinDAS", -1);
			gameManager.engine[i].owMaxDAS = NullpoMinoSlick.propGlobal.getProperty(i + ".tuning.owMaxDAS", -1);
			gameManager.engine[i].owDasDelay = NullpoMinoSlick.propGlobal.getProperty(i + ".tuning.owDasDelay", -1);

			// ルール
			RuleOptions ruleopt = null;
			String rulename = NullpoMinoSlick.propGlobal.getProperty(i + ".rule", "");
			if(gameManager.mode.getGameStyle() > 0) {
				rulename = NullpoMinoSlick.propGlobal.getProperty(i + ".rule." + gameManager.mode.getGameStyle(), "");
			}
			if((rulename != null) && (rulename.length() > 0)) {
				log.info("Load rule options from " + rulename);
				ruleopt = GeneralUtil.loadRule(rulename);
			} else {
				log.info("Load rule options from setting file");
				ruleopt = new RuleOptions();
				ruleopt.readProperty(NullpoMinoSlick.propGlobal, i);
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
			String aiName = NullpoMinoSlick.propGlobal.getProperty(i + ".ai", "");
			if(aiName.length() > 0) {
				DummyAI aiObj = GeneralUtil.loadAIPlayer(aiName);
				gameManager.engine[i].ai = aiObj;
				gameManager.engine[i].aiMoveDelay = NullpoMinoSlick.propGlobal.getProperty(i + ".aiMoveDelay", 0);
				gameManager.engine[i].aiThinkDelay = NullpoMinoSlick.propGlobal.getProperty(i + ".aiThinkDelay", 0);
				gameManager.engine[i].aiUseThread = NullpoMinoSlick.propGlobal.getProperty(i + ".aiUseThread", true);
				gameManager.engine[i].aiShowHint = NullpoMinoSlick.propGlobal.getProperty(i + ".aiShowHint",false);
			}

			// Called at initialization
			gameManager.engine[i].init();
		}
	}

	/**
	 * リプレイを読み込んで再生
	 * @param prop リプレイ dataの入ったプロパティセット
	 */
	public void startReplayGame(CustomProperties prop) {
		gameManager = new GameManager(new RendererSlick());
		gameManager.replayMode = true;
		gameManager.replayProp = prop;
		pause = false;

		gameManager.receiver.setGraphics(appContainer.getGraphics());

		// Mode
		String modeName = prop.getProperty("name.mode", "");
		GameMode modeObj = NullpoMinoSlick.modeManager.getMode(modeName);
		if(modeObj == null) {
			log.error("Couldn't find mode:" + modeName);
		} else {
			appContainer.setTitle("NullpoMino - " + modeName + " (Replay)");
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
			String aiName = NullpoMinoSlick.propGlobal.getProperty(i + ".ai", "");
			if(aiName.length() > 0) {
				DummyAI aiObj = GeneralUtil.loadAIPlayer(aiName);
				gameManager.engine[i].ai = aiObj;
				gameManager.engine[i].aiMoveDelay = NullpoMinoSlick.propGlobal.getProperty(i + ".aiMoveDelay", 0);
				gameManager.engine[i].aiThinkDelay = NullpoMinoSlick.propGlobal.getProperty(i + ".aiThinkDelay", 0);
				gameManager.engine[i].aiUseThread = NullpoMinoSlick.propGlobal.getProperty(i + ".aiUseThread", true);
			}

			// Called at initialization
			gameManager.engine[i].init();
		}
	}

	/**
	 * 終了時の処理
	 */
	public void shutdown() {
		gameManager.shutdown();
		gameManager = null;
		ResourceHolder.bgmUnloadAll();
	}

	/*
	 * Called when leaving this state
	 */
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		shutdown();
	}

	/*
	 * Draw the screen
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// ゲーム画面
		if(gameManager != null) {
			gameManager.renderAll();

			// Pause menu
			if(pause && !enableframestep && !pauseMessageHide) {
				int offsetX = RendererSlick.FIELD_OFFSET_X[0];
				int offsetY = RendererSlick.FIELD_OFFSET_Y[0];

				NormalFont.printFont(offsetX + 12, offsetY + 188 + (cursor * 16), "b", NormalFont.COLOR_RED);

				NormalFont.printFont(offsetX + 28, offsetY + 188, "CONTINUE", (cursor == 0));
				NormalFont.printFont(offsetX + 28, offsetY + 204, "RETRY", (cursor == 1));
				NormalFont.printFont(offsetX + 28, offsetY + 220, "END", (cursor == 2));
				if(gameManager.replayMode && !gameManager.replayRerecord)
					NormalFont.printFont(offsetX + 28, offsetY + 236, "RERECORD", (cursor == 3));
			}

			int offsetX = RendererSlick.FIELD_OFFSET_X[0];
			int offsetY = RendererSlick.FIELD_OFFSET_Y[0];
			// 早送り
			if(fastforward != 0)
				NormalFont.printFont(offsetX, offsetY + 376, "e" + (fastforward + 1), NormalFont.COLOR_ORANGE);
			if(gameManager.replayShowInvisible)
				NormalFont.printFont(offsetX, offsetY + 392, "SHOW INVIS", NormalFont.COLOR_ORANGE);
		}

		// FPS
		NullpoMinoSlick.drawFPS(container, true);
		// Observer
		NullpoMinoSlick.drawObserverClient();
		// Screenshot
		if(ssflag) {
			NullpoMinoSlick.saveScreenShot(container, g);
			ssflag = false;
		}

		if(!NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep(true);
	}

	/*
	 * ゲームの状態を更新
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(!container.hasFocus()) {
			if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
			return;
		}

		// TTF font 描画
		if(ResourceHolder.ttfFont != null) ResourceHolder.ttfFont.loadGlyphs();

		// Update key input states
		GameKey.gamekey[0].update(container.getInput());
		GameKey.gamekey[1].update(container.getInput());

		// ポーズ
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_PAUSE) || GameKey.gamekey[1].isPushKey(GameKey.BUTTON_PAUSE)) {
			if(!pause) {
				if((gameManager != null) && (gameManager.isGameActive())) {
					ResourceHolder.soundManager.play("pause");
					pause = true;
					cursor = 0;
					if(!enableframestep) ResourceHolder.bgmPause();
				}
			} else {
				ResourceHolder.soundManager.play("pause");
				pause = false;
				if(!enableframestep) ResourceHolder.bgmResume();
			}
		}
		// Pause menu
		if(pause && !enableframestep && !pauseMessageHide) {
			if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_UP)) {
				cursor--;

				if(cursor < 0) {
					if(gameManager.replayMode && !gameManager.replayRerecord)
						cursor = 3;
					else
						cursor = 2;
				}

				ResourceHolder.soundManager.play("cursor");
			}
			if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_DOWN)) {
				cursor++;
				if(cursor > 3) cursor = 0;

				if((!gameManager.replayMode || gameManager.replayRerecord) && (cursor > 2))
					cursor = 0;

				ResourceHolder.soundManager.play("cursor");
			}
			if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_A)) {
				ResourceHolder.soundManager.play("decide");
				if(cursor == 0) {
					// 再開
					pause = false;
					ResourceHolder.bgmResume();
				} else if(cursor == 1) {
					// リトライ
					ResourceHolder.bgmStop();
					pause = false;
					gameManager.reset();
				} else if(cursor == 2) {
					// 終了
					ResourceHolder.bgmStop();
					game.enterState(StateTitle.ID);
					return;
				} else if(cursor == 3) {
					// Replay re-record
					gameManager.replayRerecord = true;
					ResourceHolder.soundManager.play("tspin1");
					cursor = 0;
				}
			}
		}
		// Hide pause menu
		pauseMessageHide = GameKey.gamekey[0].isPressKey(GameKey.BUTTON_C);

		if(gameManager.replayMode && !gameManager.replayRerecord && gameManager.engine[0].gameActive) {
			// Replay speed
			if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_LEFT)) {
				if(fastforward > 0) {
					fastforward--;
				}
			}
			if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_RIGHT)) {
				if(fastforward < 98) {
					fastforward++;
				}
			}

			// Replay re-record
			if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_D)) {
				gameManager.replayRerecord = true;
				ResourceHolder.soundManager.play("tspin1");
				cursor = 0;
			}
			// Show invisible blocks during replays
			if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_E)) {
				gameManager.replayShowInvisible = !gameManager.replayShowInvisible;
				ResourceHolder.soundManager.play("tspin1");
				cursor = 0;
			}
		} else {
			fastforward = 0;
		}

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
		if(!pause || (GameKey.gamekey[0].isPushKey(GameKey.BUTTON_FRAMESTEP) && enableframestep)) {
			if(gameManager != null) {
				for(int i = 0; i < Math.min(gameManager.getPlayers(), 2); i++) {
					if(!gameManager.replayMode || gameManager.replayRerecord || !gameManager.engine[i].gameActive) {
						GameKey.gamekey[i].inputStatusUpdate(gameManager.engine[i].ctrl);
					}
				}

				for(int i = 0; i <= fastforward; i++) gameManager.updateAll();
			}
		}

		if(gameManager != null) {
			// Retry button
			if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_RETRY) || GameKey.gamekey[1].isPushKey(GameKey.BUTTON_RETRY)) {
				ResourceHolder.bgmStop();
				pause = false;
				gameManager.reset();
			}

			// Return to title
			if(gameManager.getQuitFlag() ||
			   GameKey.gamekey[0].isPushKey(GameKey.BUTTON_GIVEUP) ||
			   GameKey.gamekey[1].isPushKey(GameKey.BUTTON_GIVEUP))
			{
				ResourceHolder.bgmStop();
				game.enterState(StateTitle.ID);
				return;
			}
		}

		// Screenshot button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_SCREENSHOT) || GameKey.gamekey[1].isPushKey(GameKey.BUTTON_SCREENSHOT))
			ssflag = true;

		// Exit button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_QUIT) || GameKey.gamekey[1].isPushKey(GameKey.BUTTON_QUIT)) {
			shutdown();
			container.exit();
		}

		if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep(true);
	}
}
