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

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.game.subsystem.ai.DummyAI;
import mu.nu.nullpo.game.subsystem.mode.PreviewMode;
import mu.nu.nullpo.game.subsystem.wallkick.Wallkick;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Game Tuning menu state
 */
public class StateConfigGameTuning extends BaseGameState {
	/** This state's ID */
	public static final int ID = 14;

	/** UI Text identifier Strings */
	protected static final String[] UI_TEXT = {
		"GameTuning_RotateButtonDefaultRight",
		"GameTuning_Skin",
		"GameTuning_MinDAS",
		"GameTuning_MaxDAS",
		"GameTuning_DasDelay",
		"GameTuning_ReverseUpDown",
		"GameTuning_MoveDiagonal",
		"GameTuning_BlockOutlineType",
		"GameTuning_BlockShowOutlineOnly",
		"GameTuning_Preview",
	};

	/** Log */
	static Logger log = Logger.getLogger(StateConfigGameTuning.class);

	/** Outline type names */
	protected static final String[] OUTLINE_TYPE_NAMES = {"AUTO", "NONE", "NORMAL", "CONNECT", "SAMECOLOR"};

	/** Player number */
	public int player = 0;

	/** Preview flag */
	protected boolean isPreview;

	/** AppGameContainer (Used by preview) */
	protected AppGameContainer appContainer;

	/** Game Manager for preview */
	protected GameManager gameManager;

	/** Cursor position */
	protected int cursor = 0;

	/** A button rotation -1=Auto 0=Always CCW 1=Always CW */
	protected int owRotateButtonDefaultRight;

	/** Block Skin -1=Auto 0 or above=Fixed */
	protected int owSkin;

	/** Min/Max DAS -1=Auto 0 or above=Fixed */
	protected int owMinDAS, owMaxDAS;

	/** DAS Delay -1=Auto 0 or above=Fixed */
	protected int owDasDelay;

	/** Reverse the roles of up/down keys in-game */
	protected boolean owReverseUpDown;

	/** Diagonal move (-1=Auto 0=Disable 1=Enable) */
	protected int owMoveDiagonal;

	/** Outline type (-1:Auto 0orAbove:Fixed) */
	protected int owBlockOutlineType;

	/** Show outline only flag (-1:Auto 0:Always Normal 1:Always Outline Only) */
	protected int owBlockShowOutlineOnly;

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
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		if(container instanceof AppGameContainer) {
			appContainer = (AppGameContainer)container;
		} else {
			log.error("This container isn't AppGameContainer");
		}
	}

	/**
	 * Load settings
	 * @param prop Property file to read from
	 */
	protected void loadConfig(CustomProperties prop) {
		owRotateButtonDefaultRight = prop.getProperty(player + ".tuning.owRotateButtonDefaultRight", -1);
		owSkin = prop.getProperty(player + ".tuning.owSkin", -1);
		owMinDAS = prop.getProperty(player + ".tuning.owMinDAS", -1);
		owMaxDAS = prop.getProperty(player + ".tuning.owMaxDAS", -1);
		owDasDelay = prop.getProperty(player + ".tuning.owDasDelay", -1);
		owReverseUpDown = prop.getProperty(player + ".tuning.owReverseUpDown", false);
		owMoveDiagonal = prop.getProperty(player + ".tuning.owMoveDiagonal", -1);
		owBlockOutlineType = prop.getProperty(player + ".tuning.owBlockOutlineType", -1);
		owBlockShowOutlineOnly = prop.getProperty(player + ".tuning.owBlockShowOutlineOnly", -1);
	}

	/**
	 * Save settings
	 * @param prop Property file to save to
	 */
	protected void saveConfig(CustomProperties prop) {
		prop.setProperty(player + ".tuning.owRotateButtonDefaultRight", owRotateButtonDefaultRight);
		prop.setProperty(player + ".tuning.owSkin", owSkin);
		prop.setProperty(player + ".tuning.owMinDAS", owMinDAS);
		prop.setProperty(player + ".tuning.owMaxDAS", owMaxDAS);
		prop.setProperty(player + ".tuning.owDasDelay", owDasDelay);
		prop.setProperty(player + ".tuning.owReverseUpDown", owReverseUpDown);
		prop.setProperty(player + ".tuning.owMoveDiagonal", owMoveDiagonal);
		prop.setProperty(player + ".tuning.owBlockOutlineType", owBlockOutlineType);
		prop.setProperty(player + ".tuning.owBlockShowOutlineOnly", owBlockShowOutlineOnly);
	}

	/*
	 * Called when entering this state
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		isPreview = false;
		loadConfig(NullpoMinoSlick.propGlobal);
	}

	/*
	 * Called when leaving the state
	 */
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		stopPreviewGame();
	}

	/**
	 * Start the preview game
	 */
	protected void startPreviewGame() {
		gameManager = new GameManager(new RendererSlick());
		gameManager.receiver.setGraphics(appContainer.getGraphics());

		gameManager.mode = new PreviewMode();
		gameManager.init();

		gameManager.backgroundStatus.bg = -1;	// Force no BG

		// Initialization for each player
		for(int i = 0; i < gameManager.getPlayers(); i++) {
			// Tuning
			gameManager.engine[i].owRotateButtonDefaultRight = owRotateButtonDefaultRight;
			gameManager.engine[i].owSkin = owSkin;
			gameManager.engine[i].owMinDAS = owMinDAS;
			gameManager.engine[i].owMaxDAS = owMaxDAS;
			gameManager.engine[i].owDasDelay = owDasDelay;
			gameManager.engine[i].owReverseUpDown = owReverseUpDown;
			gameManager.engine[i].owMoveDiagonal = owMoveDiagonal;
			gameManager.engine[i].owBlockOutlineType = owBlockOutlineType;
			gameManager.engine[i].owBlockShowOutlineOnly = owBlockShowOutlineOnly;

			// Rule
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

			// Randomizer
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
				gameManager.engine[i].aiShowHint = NullpoMinoSlick.propGlobal.getProperty(i + ".aiShowHint", false);
				gameManager.engine[i].aiPrethink = NullpoMinoSlick.propGlobal.getProperty(i + ".aiPrethink", false);
				gameManager.engine[i].aiShowState = NullpoMinoSlick.propGlobal.getProperty(i + ".aiShowState", false);
			}
			gameManager.showInput = NullpoMinoSlick.propConfig.getProperty("option.showInput", false);

			// Init
			gameManager.engine[i].init();
		}

		isPreview = true;
	}

	/**
	 * Stop the preview game
	 */
	protected void stopPreviewGame() {
		if(isPreview) {
			isPreview = false;
			if(gameManager != null) {
				gameManager.shutdown();
				gameManager = null;
			}
		}
	}

	/*
	 * Draw the game screen
	 */
	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.drawImage(ResourceHolder.imgMenu, 0, 0);

		if(isPreview) {
			// Preview
			try {
				String strButtonF = gameManager.receiver.getKeyNameByButtonID(gameManager.engine[0], Controller.BUTTON_F);
				int fontY = (gameManager.receiver.getNextDisplayType() == 2) ? 1 : 27;
				NormalFont.printFontGrid(1, fontY, "PUSH F BUTTON (" + strButtonF.toUpperCase() + " KEY) TO EXIT", NormalFont.COLOR_YELLOW);

				gameManager.renderAll();
			} catch (Exception e) {
				log.error("Render fail", e);
			}
		} else {
			// Menu
			String strTemp = "";

			NormalFont.printFontGrid(1, 1, "GAME TUNING (" + (player+1) + "P)", NormalFont.COLOR_ORANGE);
			NormalFont.printFontGrid(1, 3 + cursor, "b", NormalFont.COLOR_RED);

			if(owRotateButtonDefaultRight == -1) strTemp = "AUTO";
			if(owRotateButtonDefaultRight == 0) strTemp = "LEFT";
			if(owRotateButtonDefaultRight == 1) strTemp = "RIGHT";
			NormalFont.printFontGrid(2, 3, "A BUTTON ROTATE:" + strTemp, (cursor == 0));

			NormalFont.printFontGrid(2, 4, "BLOCK SKIN:" + ((owSkin == -1) ? "AUTO": String.valueOf(owSkin)), (cursor == 1));
			if((owSkin >= 0) && (owSkin < ResourceHolder.imgNormalBlockList.size())) {
				//ResourceHolder.imgBlock.draw(256, 64, 256 + 144, 64 + 16, 0, owSkin * 16, 144, (owSkin * 16) + 16);
				Image imgBlock = ResourceHolder.imgNormalBlockList.get(owSkin);

				if(ResourceHolder.blockStickyFlagList.get(owSkin) == true) {
					for(int j = 0; j < 9; j++) {
						imgBlock.draw(256 + (j * 16), 64, 256 + (j * 16) + 16, 64 + 16, 0, (j * 16), 16, (j * 16) + 16);
					}
				} else {
					imgBlock.draw(256, 64, 256+144, 64+16, 0, 0, 144, 16);
				}
			}

			NormalFont.printFontGrid(2, 5, "MIN DAS:" + ((owMinDAS == -1) ? "AUTO" : String.valueOf(owMinDAS)), (cursor == 2));
			NormalFont.printFontGrid(2, 6, "MAX DAS:" + ((owMaxDAS == -1) ? "AUTO" : String.valueOf(owMaxDAS)), (cursor == 3));
			NormalFont.printFontGrid(2, 7, "DAS DELAY:" + ((owDasDelay == -1) ? "AUTO" : String.valueOf(owDasDelay)), (cursor == 4));
			NormalFont.printFontGrid(2, 8, "REVERSE UP/DOWN:" + GeneralUtil.getOorX(owReverseUpDown), (cursor == 5));

			if(owMoveDiagonal == -1) strTemp = "AUTO";
			if(owMoveDiagonal == 0) strTemp = "e";
			if(owMoveDiagonal == 1) strTemp = "c";
			NormalFont.printFontGrid(2, 9, "DIAGONAL MOVE:" + strTemp, (cursor == 6));

			NormalFont.printFontGrid(2, 10, "OUTLINE TYPE:" + OUTLINE_TYPE_NAMES[owBlockOutlineType + 1], (cursor == 7));

			if(owBlockShowOutlineOnly == -1) strTemp = "AUTO";
			if(owBlockShowOutlineOnly == 0) strTemp = "e";
			if(owBlockShowOutlineOnly == 1) strTemp = "c";
			NormalFont.printFontGrid(2, 11, "SHOW OUTLINE ONLY:" + strTemp, (cursor == 8));

			NormalFont.printFontGrid(2, 12, "[PREVIEW]", (cursor == 9));

			if((cursor >= 0) && (cursor < UI_TEXT.length)) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText(UI_TEXT[cursor]));
		}
	}

	/*
	 * Update game state
	 */
	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(isPreview) {
			// Preview
			try {
				GameKey.gamekey[0].update(container.getInput(), true);

				// Execute game loops
				GameKey.gamekey[0].inputStatusUpdate(gameManager.engine[0].ctrl);
				gameManager.updateAll();

				// Retry button
				if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_RETRY)) {
					gameManager.reset();
					gameManager.backgroundStatus.bg = -1;	// Force no BG
				}

				// Exit
				if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_F) || GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_GIVEUP) ||
				   gameManager.getQuitFlag())
				{
					stopPreviewGame();
				}
			} catch (Exception e) {
				log.error("Update fail", e);
			}
		} else {
			// Menu screen
			GameKey.gamekey[0].update(container.getInput(), false);

			// TTF font
			if(ResourceHolder.ttfFont != null) ResourceHolder.ttfFont.loadGlyphs();

			// Cursor movement
			if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_UP)) {
				cursor--;
				if(cursor < 0) cursor = 9;
				ResourceHolder.soundManager.play("cursor");
			}
			if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_DOWN)) {
				cursor++;
				if(cursor > 9) cursor = 0;
				ResourceHolder.soundManager.play("cursor");
			}

			// Configuration changes
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
					if(owSkin < -1) owSkin = ResourceHolder.imgNormalBlockList.size() - 1;
					if(owSkin > ResourceHolder.imgNormalBlockList.size() - 1) owSkin = -1;
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
				case 5:
					owReverseUpDown ^= true;
					break;
				case 6:
					owMoveDiagonal += change;
					if(owMoveDiagonal < -1) owMoveDiagonal = 1;
					if(owMoveDiagonal > 1) owMoveDiagonal = -1;
					break;
				case 7:
					owBlockOutlineType += change;
					if(owBlockOutlineType < -1) owBlockOutlineType = 3;
					if(owBlockOutlineType > 3) owBlockOutlineType = -1;
					break;
				case 8:
					owBlockShowOutlineOnly += change;
					if(owBlockShowOutlineOnly < -1) owBlockShowOutlineOnly = 1;
					if(owBlockShowOutlineOnly > 1) owBlockShowOutlineOnly = -1;
					break;
				case 9:
					break;
				}
			}

			// Preview by D button
			if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_D)) {
				ResourceHolder.soundManager.play("decide");
				startPreviewGame();
				return;
			}

			// Confirm button
			if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_A)) {
				ResourceHolder.soundManager.play("decide");

				if(cursor == 9) {
					// Preview
					startPreviewGame();
					return;
				} else {
					// Save
					saveConfig(NullpoMinoSlick.propGlobal);
					NullpoMinoSlick.saveConfig();
					game.enterState(StateConfigMainMenu.ID);
				}
			}

			// Cancel button
			if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_B)) {
			    loadConfig(NullpoMinoSlick.propGlobal);
				game.enterState(StateConfigMainMenu.ID);
			}
		}
	}
}
