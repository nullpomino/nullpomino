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
package mu.nu.nullpo.game.subsystem.mode;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * AVALANCHE-SPF VS-BATTLE mode (Release Candidate 1)
 */
public class AvalancheVSSPFMode extends AvalancheVSDummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 0;

	/** Block colors */
	private static final int[] BLOCK_COLORS =
	{
		Block.BLOCK_COLOR_RED,
		Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_BLUE,
		Block.BLOCK_COLOR_YELLOW
	};

	/** Names of drop map sets */
	private static final String[] DROP_SET_NAMES = {"CLASSIC", "REMIX", "SWORD", "S-MIRROR", "AVALANCHE", "A-MIRROR"};

	private static final int[][][][] DROP_PATTERNS = {
		{
			{{2,2,2,2}, {5,5,5,5}, {7,7,7,7}, {4,4,4,4}},
			{{2,2,4,4}, {2,2,4,4}, {5,5,2,2}, {5,5,2,2}, {7,7,5,5}, {7,7,5,5}},
			{{5,5,5,5}, {2,7,2,7}, {2,7,2,7}, {2,7,2,7}, {2,7,2,7}, {4,4,4,4}},
			{{2,5,7,4}},
			{{7,7,4,4}, {4,4,7,7}, {2,2,5,5}, {2,2,5,5}, {4,4,7,7}, {7,7,4,4}},
			{{4,7,7,5}, {7,7,5,5}, {7,5,5,2}, {5,5,2,2}, {5,2,2,4}, {2,2,4,4}},
			{{2,2,5,5}, {4,4,5,5}, {2,2,5,5}, {4,4,7,7}, {2,2,7,7}, {4,4,7,7}},
			{{5,5,5,5}, {2,2,7,7}, {2,2,7,7}, {7,7,2,2}, {7,7,2,2}, {4,4,4,4}},
			{{5,7,4,2}, {2,5,7,4}, {4,2,5,7}, {7,4,2,5}},
			{{2,5,7,4}, {5,7,4,2}, {7,4,2,5}, {4,2,5,7}},
			{{2,2,2,2}}
		},
		{
			{{2,2,7,2}, {5,5,4,5}, {7,7,5,7}, {4,4,2,4}},
			{{2,2,4,4}, {2,2,4,4}, {5,5,2,2}, {5,5,2,2}, {7,7,5,5}, {7,7,5,5}},
			{{5,5,4,4}, {2,7,2,7}, {2,7,2,7}, {2,7,2,7}, {2,7,2,7}, {4,4,5,5}},
			{{2,5,7,4}},
			{{7,7,4,4}, {4,4,7,7}, {2,5,5,5}, {2,2,2,5}, {4,4,7,7}, {7,7,4,4}},
			{{7,7,7,7}, {5,7,4,2}, {7,4,2,5}, {4,2,5,7}, {2,5,7,4}, {5,5,5,5}},
			{{2,2,5,5}, {4,4,5,5}, {2,2,5,5}, {4,4,7,7}, {2,2,7,7}, {4,4,7,7}},
			{{5,4,5,4}, {2,2,2,7}, {2,7,7,7}, {7,2,2,2}, {7,7,7,2}, {4,5,4,5}},
			{{5,7,4,2}, {2,5,7,4}, {4,2,5,7}, {7,4,2,5}},
			{{2,5,7,4}, {5,7,4,2}, {7,4,2,5}, {4,2,5,7}},
			{{2,2,2,2}}
		},
		{
			{{2,5,5,5}, {5,2,2,5}, {5,5,2,2}, {4,4,7,7}, {4,7,7,4}, {7,4,4,4}},
			{{2,2,2,5,5,5}, {5,3,7,5,4,5}, {5,5,7,7,4,4}, {4,4,2,4,4,7}, {4,2,4,4,7,4}, {2,4,4,7,4,4}},
			{{4,4,5,5,7,2}, {4,4,5,5,7,2}, {5,5,7,7,7,5}, {5,7,7,7,4,5}, {7,7,2,2,5,4}, {7,2,2,2,5,4}},
			{{2,2,5,4,2,7}, {2,7,4,5,7,2}, {2,7,4,4,7,7}, {2,7,5,5,2,2}, {2,7,5,4,2,7}, {7,7,4,5,7,2}},
			{{2,7,7,7,7}, {2,7,5,7,7}, {2,2,5,5,5}, {2,2,2,5,5}, {2,4,2,4,4}, {4,4,4,4,4}},
			{{2,2,5,5}, {2,7,7,5}, {5,7,4,4}, {5,5,2,4}, {4,2,2,7}, {4,4,7,7}},
			{{2,2,5,5}, {2,2,5,5}, {5,5,7,7}, {5,5,7,7}, {7,7,4,4}, {7,7,4,4}},
			{{2,2,5,4,2,7}, {2,2,4,5,7,2}, {7,7,4,5,7,2}, {7,7,5,4,2,7}, {2,2,5,4,2,7}, {2,2,4,5,7,2}},
			{{7,7,4,4,7,7}, {7,7,7,7,5,7}, {2,5,2,2,5,2}, {2,5,2,2,5,2}, {4,4,4,4,5,4}, {4,4,7,7,4,4}},
			{{2,5,5,5,5,4}, {5,2,5,5,4,4}, {2,2,2,2,2,2}, {7,7,7,7,7,7}, {4,7,4,4,5,5}, {7,4,4,4,4,5}},
			{{2,2,5,2,2,4}, {2,5,5,2,5,5}, {5,5,5,7,7,2}, {7,7,7,5,5,4}, {4,7,7,4,7,7}, {4,4,7,4,4,2}},
			{{7,7,5,5,5,5}, {7,2,2,5,5,7}, {7,2,2,4,4,7}, {2,7,7,4,4,2}, {2,7,7,5,5,2}, {7,7,5,5,5,5}},
			{{7,7,5,5}, {7,2,5,2}, {5,5,5,2}, {4,4,4,2}, {7,2,4,2}, {7,7,4,4}},
			{{2,2,5,5}, {2,7,5,5}, {5,5,7,7}, {5,5,7,7}, {4,7,4,4}, {7,7,4,4}},
			{{7,7,5,5,5}, {4,7,7,7,5}, {5,4,4,4,4}, {5,2,2,2,2}, {2,7,7,7,5}, {7,7,5,5,5}},
			{{2,2,4}, {2,2,2}, {7,7,7}, {7,7,7}, {5,5,5}, {5,5,4}},
			{{7,7,7,7}, {7,2,2,7}, {2,7,5,4}, {4,5,7,2}, {5,4,4,5}, {5,5,5,5}}
		},
		{
			{{7,4,4,4}, {4,7,7,4}, {4,4,7,7}, {5,5,2,2}, {5,2,2,5}, {2,5,5,5}},
			{{2,4,4,7,4,4}, {4,2,4,4,7,4}, {4,4,2,4,4,7}, {5,5,7,7,4,4}, {5,3,7,5,4,5}, {2,2,2,5,5,5}},
			{{7,2,2,2,5,4}, {7,7,2,2,5,4}, {5,7,7,7,4,5}, {5,5,7,7,7,5}, {4,4,5,5,7,2}, {4,4,5,5,7,2}},
			{{7,7,4,5,7,2}, {2,7,5,4,2,7}, {2,7,5,5,2,2}, {2,7,4,4,7,7}, {2,7,4,5,7,2}, {2,2,5,4,2,7}},
			{{4,4,4,4,4}, {2,4,2,4,4}, {2,2,2,5,5}, {2,2,5,5,5}, {2,7,5,7,7}, {2,7,7,7,7}},
			{{4,4,7,7}, {4,2,2,7}, {5,5,2,4}, {5,7,4,4}, {2,7,7,5}, {2,2,5,5}},
			{{7,7,4,4}, {7,7,4,4}, {5,5,7,7}, {5,5,7,7}, {2,2,5,5}, {2,2,5,5}},
			{{2,2,4,5,7,2}, {2,2,5,4,2,7}, {7,7,5,4,2,7}, {7,7,4,5,7,2}, {2,2,4,5,7,2}, {2,2,5,4,2,7}},
			{{4,4,7,7,4,4}, {4,4,4,4,5,4}, {2,5,2,2,5,2}, {2,5,2,2,5,2}, {7,7,7,7,5,7}, {7,7,4,4,7,7}},
			{{7,4,4,4,4,5}, {4,7,4,4,5,5}, {7,7,7,7,7,7}, {2,2,2,2,2,2}, {5,2,5,5,4,4}, {2,5,5,5,5,4}},
			{{4,4,7,4,4,2}, {4,7,7,4,7,7}, {7,7,7,5,5,4}, {5,5,5,7,7,2}, {2,5,5,2,5,5}, {2,2,5,2,2,4}},
			{{7,7,5,5,5,5}, {2,7,7,5,5,2}, {2,7,7,4,4,2}, {7,2,2,4,4,7}, {7,2,2,5,5,7}, {7,7,5,5,5,5}},
			{{7,7,4,4}, {7,2,4,2}, {4,4,4,2}, {5,5,5,2}, {7,2,5,2}, {7,7,5,5}},
			{{7,7,4,4}, {4,7,4,4}, {5,5,7,7}, {5,5,7,7}, {2,7,5,5}, {2,2,5,5}},
			{{7,7,5,5,5}, {2,7,7,7,5}, {5,2,2,2,2}, {5,4,4,4,4}, {4,7,7,7,5}, {7,7,5,5,5}},
			{{5,5,4}, {5,5,5}, {7,7,7}, {7,7,7}, {2,2,2}, {2,2,4}},
			{{5,5,5,5}, {5,4,4,5}, {4,5,7,2}, {2,7,5,4}, {7,2,2,7}, {7,7,7,7}}
		},
		{
			{{5,4,4,5,5}, {2,5,5,2,2}, {4,2,2,4,4}, {7,4,4,7,7}, {5,7,7,5,5}, {2,5,5,2,2}},
			{{2,7,7,7,2}, {5,2,2,2,5}, {5,4,4,4,5}, {4,5,5,5,4}, {4,7,7,7,4}, {7,2,2,2,7}},
			{{2,2,5,5,5}, {5,7,7,2,2}, {7,7,2,2,5}, {5,4,4,7,7}, {4,4,7,7,5}, {5,5,5,4,4}},
			{{7,2,2,5,5}, {4,4,5,5,2}, {4,7,7,2,2}, {7,7,4,4,5}, {5,4,4,7,7}, {2,2,7,7,4}},
			{{7,2,7,2,2}, {7,4,7,7,2}, {5,4,4,7,4}, {5,5,4,5,4}, {2,5,2,5,5}, {2,7,2,2,4}},
			{{5,5,4,2,2}, {5,4,4,2,7}, {4,2,2,7,7}, {4,2,7,5,5}, {2,7,7,5,4}, {7,5,5,4,4}},
			{{7,7,4,7,7}, {5,5,7,5,5}, {2,2,5,2,2}, {4,4,2,4,4}},
			{{4,4,2,2,5}, {2,2,5,5,7}, {5,5,7,7,4}, {7,7,4,4,2}},
			{{5,5,5,2,4}, {7,7,7,5,2}, {4,4,4,7,5}, {2,2,2,4,7}},
			{{4,4,4,5,7}, {2,2,2,7,4}, {5,5,5,4,2}, {7,7,7,2,5}},
			{{4,2,5,5,5}, {7,4,2,2,2}, {5,7,4,4,4}, {2,5,7,7,7}}
		},
		{
			{{2,5,5,2,2}, {5,7,7,5,5}, {7,4,4,7,7}, {4,2,2,4,4}, {2,5,5,2,2}, {5,4,4,5,5}},
			{{7,2,2,2,7}, {4,7,7,7,4}, {4,5,5,5,4}, {5,4,4,4,5}, {5,2,2,2,5}, {2,7,7,7,2}},
			{{5,5,5,4,4}, {4,4,7,7,5}, {5,4,4,7,7}, {7,7,2,2,5}, {5,7,7,2,2}, {2,2,5,5,5}},
			{{2,2,7,7,4}, {5,4,4,7,7}, {7,7,4,4,5}, {4,7,7,2,2}, {4,4,5,5,2}, {7,2,2,5,5}},
			{{2,7,2,2,4}, {2,5,2,5,5}, {5,5,4,5,4}, {5,4,4,7,4}, {7,4,7,7,2}, {7,2,7,2,2}},
			{{7,5,5,4,4}, {2,7,7,5,4}, {4,2,7,5,5}, {4,2,2,7,7}, {5,4,4,2,7}, {5,5,4,2,2}},
			{{5,5,7,5,5}, {7,7,4,7,7}, {4,4,2,4,4}, {2,2,5,2,2}},
			{{2,2,5,5,7}, {4,4,2,2,5}, {7,7,4,4,2}, {5,5,7,7,4}},
			{{7,7,7,5,2}, {5,5,5,2,4}, {2,2,2,4,7}, {4,4,4,7,5}},
			{{2,2,2,7,4}, {4,4,4,5,7}, {7,7,7,2,5}, {5,5,5,4,2}},
			{{7,4,2,2,2}, {4,2,5,5,5}, {2,5,7,7,7}, {5,7,4,4,4}}
		}
	};
	private static final double[][] DROP_PATTERNS_ATTACK_MULTIPLIERS = {
		{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.7, 0.7, 1.0},
		{1.0, 1.2, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.85, 1.0}
	};
	private static final double[][] DROP_PATTERNS_DEFEND_MULTIPLIERS = {
		{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
		{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.2, 1.0, 1.0}
	};

	/** Version */
	private int version;

	/** Settings for starting countdown for ojama blocks */
	private int[] ojamaCountdown;

	/** Drop patterns */
	private int[][][] dropPattern;

	/** Drop map set selected */
	private int[] dropSet;

	/** Drop map selected */
	private int[] dropMap;

	/** Drop multipliers */
	private double[] attackMultiplier, defendMultiplier;

	/** Flag set when counters have been decremented */
	private boolean[] countdownDecremented;

	/** Flag set when cleared ojama have been turned into normal blocks */
	private boolean[] ojamaChecked;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "AVALANCHE-SPF VS-BATTLE (BETA)";
	}

	/*
	 * Mode initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		super.modeInit(manager);
		ojamaCountdown = new int[MAX_PLAYERS];
		dropSet = new int[MAX_PLAYERS];
		dropMap = new int[MAX_PLAYERS];
		dropPattern = new int[MAX_PLAYERS][][];
		attackMultiplier = new double[MAX_PLAYERS];
		defendMultiplier = new double[MAX_PLAYERS];
		countdownDecremented = new boolean[MAX_PLAYERS];
		ojamaChecked = new boolean[MAX_PLAYERS];
	}

	/**
	 * Load settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 */
	private void loadOtherSetting(GameEngine engine, CustomProperties prop) {
		super.loadOtherSetting(engine, prop, "spf");
		int playerID = engine.playerID;
		ojamaHard[playerID] = 4;
		ojamaRate[playerID] = prop.getProperty("avalanchevsspf.ojamaRate.p" + playerID, 120);
		ojamaCountdown[playerID] = prop.getProperty("avalanchevsspf.ojamaCountdown.p" + playerID, 3);
		dropSet[playerID] = prop.getProperty("avalanchevsspf.dropSet.p" + playerID, 4);
		dropMap[playerID] = prop.getProperty("avalanchevsspf.dropMap.p" + playerID, 0);
	}

	/**
	 * Save settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 */
	private void saveOtherSetting(GameEngine engine, CustomProperties prop) {
		super.saveOtherSetting(engine, prop, "spf");
		int playerID = engine.playerID;
		prop.setProperty("avalanchevsspf.ojamaCountdown.p" + playerID, ojamaCountdown[playerID]);
		prop.setProperty("avalanchevsspf.dropSet.p" + playerID, dropSet[playerID]);
		prop.setProperty("avalanchevsspf.dropMap.p" + playerID, dropMap[playerID]);
	}

	private void loadDropMapPreview(GameEngine engine, int playerID, int[][] pattern) {
		if((pattern == null) && (engine.field != null)) {
			engine.field.reset();
		} else if(pattern != null) {
			engine.createFieldIfNeeded();
			engine.field.reset();
			int patternCol = 0;
			int maxHeight = engine.field.getHeight()-1;
			for (int x = 0; x < engine.field.getWidth(); x++)
			{
				if (patternCol >= pattern.length)
					patternCol = 0;
				for (int patternRow = 0; patternRow < pattern[patternCol].length; patternRow++)
				{
					engine.field.setBlockColor(x, maxHeight-patternRow, pattern[patternCol][patternRow]);
					Block blk = engine.field.getBlock(x, maxHeight-patternRow);
					blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
					blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
				}
				patternCol++;
			}
			engine.field.setAllSkin(engine.getSkin());
		}
	}

	/*
	 * Initialization for each player
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		super.playerInit(engine, playerID);
		numColors[playerID] = 4;
		ojamaHard[playerID] = 4;
		countdownDecremented[playerID] = true;
		ojamaChecked[playerID] = false;

		if(engine.owner.replayMode == false) {
			loadOtherSetting(engine, engine.owner.modeConfig);
			loadPreset(engine, engine.owner.modeConfig, -1 - playerID, "spf");
			version = CURRENT_VERSION;
		} else {
			loadOtherSetting(engine, engine.owner.replayProp);
			loadPreset(engine, engine.owner.replayProp, -1 - playerID, "spf");
			version = owner.replayProp.getProperty("avalanchevs.version", 0);
		}
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if((engine.owner.replayMode == false) && (engine.statc[4] == 0)) {
			// Configuration changes
			// Up
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
				engine.statc[2]--;
				if(engine.statc[2] < 0){
					engine.statc[2] = 33;
					loadDropMapPreview(engine, playerID, DROP_PATTERNS[dropSet[playerID]][dropMap[playerID]]);
				}
				else if (engine.statc[2] == 31)
					engine.field = null;
				engine.playSE("cursor");
			}
			// Down
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
				engine.statc[2]++;
				if(engine.statc[2] > 33) {
					engine.statc[2] = 0;
					engine.field = null;
				}
				else if (engine.statc[2] == 32)
					loadDropMapPreview(engine, playerID, DROP_PATTERNS[dropSet[playerID]][dropMap[playerID]]);
				engine.playSE("cursor");
			}

			// Configuration changes
			int change = 0;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT)) change = -1;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) change = 1;

			if(change != 0) {
				engine.playSE("change");

				int m = 1;
				if(engine.ctrl.isPress(Controller.BUTTON_E)) m = 100;
				if(engine.ctrl.isPress(Controller.BUTTON_F)) m = 1000;

				switch(engine.statc[2]) {
				case 0:
					engine.speed.gravity += change * m;
					if(engine.speed.gravity < -1) engine.speed.gravity = 99999;
					if(engine.speed.gravity > 99999) engine.speed.gravity = -1;
					break;
				case 1:
					engine.speed.denominator += change * m;
					if(engine.speed.denominator < -1) engine.speed.denominator = 99999;
					if(engine.speed.denominator > 99999) engine.speed.denominator = -1;
					break;
				case 2:
					engine.speed.are += change;
					if(engine.speed.are < 0) engine.speed.are = 99;
					if(engine.speed.are > 99) engine.speed.are = 0;
					break;
				case 3:
					engine.speed.areLine += change;
					if(engine.speed.areLine < 0) engine.speed.areLine = 99;
					if(engine.speed.areLine > 99) engine.speed.areLine = 0;
					break;
				case 4:
					engine.speed.lineDelay += change;
					if(engine.speed.lineDelay < 0) engine.speed.lineDelay = 99;
					if(engine.speed.lineDelay > 99) engine.speed.lineDelay = 0;
					break;
				case 5:
					if (m >= 10) engine.speed.lockDelay += change*10;
					else engine.speed.lockDelay += change;
					if(engine.speed.lockDelay < 0) engine.speed.lockDelay = 999;
					if(engine.speed.lockDelay > 999) engine.speed.lockDelay = 0;
					break;
				case 6:
					engine.speed.das += change;
					if(engine.speed.das < 0) engine.speed.das = 99;
					if(engine.speed.das > 99) engine.speed.das = 0;
					break;
				case 7:
					engine.cascadeDelay += change;
					if(engine.cascadeDelay < 0) engine.cascadeDelay = 20;
					if(engine.cascadeDelay > 20) engine.cascadeDelay = 0;
					break;
				case 8:
					engine.cascadeClearDelay += change;
					if(engine.cascadeClearDelay < 0) engine.cascadeClearDelay = 99;
					if(engine.cascadeClearDelay > 99) engine.cascadeClearDelay = 0;
					break;
				case 9:
					ojamaCounterMode[playerID] += change;
					if(ojamaCounterMode[playerID] < 0) ojamaCounterMode[playerID] = 2;
					if(ojamaCounterMode[playerID] > 2) ojamaCounterMode[playerID] = 0;
					break;
				case 10:
					if (m >= 10) maxAttack[playerID] += change*10;
					else maxAttack[playerID] += change;
					if(maxAttack[playerID] < 0) maxAttack[playerID] = 99;
					if(maxAttack[playerID] > 99) maxAttack[playerID] = 0;
					break;
				case 11:
					rensaShibari[playerID] += change;
					if(rensaShibari[playerID] < 1) rensaShibari[playerID] = 20;
					if(rensaShibari[playerID] > 20) rensaShibari[playerID] = 1;
					break;
				case 12:
					engine.colorClearSize += change;
					if(engine.colorClearSize < 2) engine.colorClearSize = 36;
					if(engine.colorClearSize > 36) engine.colorClearSize = 2;
					break;
				case 13:
					if (m >= 10) ojamaRate[playerID] += change*100;
					else ojamaRate[playerID] += change*10;
					if(ojamaRate[playerID] < 10) ojamaRate[playerID] = 1000;
					if(ojamaRate[playerID] > 1000) ojamaRate[playerID] = 10;
					break;
				case 14:
					if (m > 10) hurryupSeconds[playerID] += change*m/10;
					else hurryupSeconds[playerID] += change;
					if(hurryupSeconds[playerID] < 0) hurryupSeconds[playerID] = 300;
					if(hurryupSeconds[playerID] > 300) hurryupSeconds[playerID] = 0;
					break;
				case 15:
					dangerColumnDouble[playerID] = !dangerColumnDouble[playerID];
					break;
				case 16:
					dangerColumnShowX[playerID] = !dangerColumnShowX[playerID];
					break;
				case 17:
					ojamaCountdown[playerID] += change;
					if(ojamaCountdown[playerID] < 1) ojamaCountdown[playerID] = 10;
					if(ojamaCountdown[playerID] > 10) ojamaCountdown[playerID] = 1;
					break;
				case 18:
					zenKeshiType[playerID] += change;
					if(zenKeshiType[playerID] < 0) zenKeshiType[playerID] = 2;
					if(zenKeshiType[playerID] > 2) zenKeshiType[playerID] = 0;
					break;
				case 19:
					feverMapSet[playerID] += change;
					if(feverMapSet[playerID] < 0) feverMapSet[playerID] = FEVER_MAPS.length-1;
					if(feverMapSet[playerID] >= FEVER_MAPS.length) feverMapSet[playerID] = 0;
					break;
				case 20:
					outlineType[playerID] += change;
					if(outlineType[playerID] < 0) outlineType[playerID] = 2;
					if(outlineType[playerID] > 2) outlineType[playerID] = 0;
					break;
				case 21:
					chainDisplayType[playerID] += change;
					if(chainDisplayType[playerID] < 0) chainDisplayType[playerID] = 3;
					if(chainDisplayType[playerID] > 3) chainDisplayType[playerID] = 0;
					break;
				case 22:
					cascadeSlow[playerID] = !cascadeSlow[playerID];
					break;
				case 23:
					newChainPower[playerID] = !newChainPower[playerID];
					break;
				case 24:
					useMap[playerID] = !useMap[playerID];
					if(!useMap[playerID]) {
						if(engine.field != null) engine.field.reset();
					} else {
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 25:
					mapSet[playerID] += change;
					if(mapSet[playerID] < 0) mapSet[playerID] = 99;
					if(mapSet[playerID] > 99) mapSet[playerID] = 0;
					if(useMap[playerID]) {
						mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 26:
					if(useMap[playerID]) {
						mapNumber[playerID] += change;
						if(mapNumber[playerID] < -1) mapNumber[playerID] = mapMaxNo[playerID] - 1;
						if(mapNumber[playerID] > mapMaxNo[playerID] - 1) mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					} else {
						mapNumber[playerID] = -1;
					}
					break;
				case 27:
					bigDisplay = !bigDisplay;
					break;
				case 28:
					bgmno += change;
					if(bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
					if(bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
					break;
				case 29:
					enableSE[playerID] = !enableSE[playerID];
					break;
				case 30:
				case 31:
					presetNumber[playerID] += change;
					if(presetNumber[playerID] < 0) presetNumber[playerID] = 99;
					if(presetNumber[playerID] > 99) presetNumber[playerID] = 0;
					break;
				case 32:
					dropSet[playerID] += change;
					if(dropSet[playerID] < 0) dropSet[playerID] = DROP_PATTERNS.length-1;
					if(dropSet[playerID] >= DROP_PATTERNS.length) dropSet[playerID] = 0;
					if(dropMap[playerID] >= DROP_PATTERNS[dropSet[playerID]].length) dropMap[playerID] = 0;
					loadDropMapPreview(engine, playerID, DROP_PATTERNS[dropSet[playerID]][dropMap[playerID]]);
					break;
				case 33:
					dropMap[playerID] += change;
					if(dropMap[playerID] < 0) dropMap[playerID] = DROP_PATTERNS[dropSet[playerID]].length-1;
					if(dropMap[playerID] >= DROP_PATTERNS[dropSet[playerID]].length) dropMap[playerID] = 0;
					loadDropMapPreview(engine, playerID, DROP_PATTERNS[dropSet[playerID]][dropMap[playerID]]);
					break;
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");

				if(engine.statc[2] == 30) {
					loadPreset(engine, owner.modeConfig, presetNumber[playerID], "spf");
				} else if(engine.statc[2] == 31) {
					savePreset(engine, owner.modeConfig, presetNumber[playerID], "spf");
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					saveOtherSetting(engine, owner.modeConfig);
					savePreset(engine, owner.modeConfig, -1 - playerID, "spf");
					receiver.saveModeConfig(owner.modeConfig);
					engine.statc[4] = 1;
				}
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				engine.quitflag = true;
			}

			// プレビュー用Map読み込み
			if(useMap[playerID] && (engine.statc[3] == 0)) {
				loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
			}

			// Random map preview
			if(useMap[playerID] && (propMap[playerID] != null) && (mapNumber[playerID] < 0)) {
				if(engine.statc[3] % 30 == 0) {
					engine.statc[5]++;
					if(engine.statc[5] >= mapMaxNo[playerID]) engine.statc[5] = 0;
					loadMapPreview(engine, playerID, engine.statc[5], false);
				}
			}

			engine.statc[3]++;
		} else if(engine.statc[4] == 0) {
			engine.statc[3]++;
			engine.statc[2] = 0;

			if(engine.statc[3] >= 300)
				engine.statc[4] = 1;
			else if (engine.statc[3] == 240)
			{
				engine.statc[2] = 32;
				loadDropMapPreview(engine, playerID, DROP_PATTERNS[dropSet[playerID]][dropMap[playerID]]);
			}
			else if(engine.statc[3] >= 180)
				engine.statc[2] = 24;
			else if(engine.statc[3] >= 120)
				engine.statc[2] = 17;
			else if(engine.statc[3] >= 60)
				engine.statc[2] = 9;
		} else {
			// 開始
			if((owner.engine[0].statc[4] == 1) && (owner.engine[1].statc[4] == 1) && (playerID == 1)) {
				owner.engine[0].stat = GameEngine.STAT_READY;
				owner.engine[1].stat = GameEngine.STAT_READY;
				owner.engine[0].resetStatc();
				owner.engine[1].resetStatc();
			}
			// Cancel
			else if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				engine.statc[4] = 0;
			}
		}

		return true;
	}

	@Override
	public boolean onMove (GameEngine engine, int playerID) {
		cleared[playerID] = false;
		ojamaDrop[playerID] = false;
		countdownDecremented[playerID] = false;
		return false;
	}

	@Override
	public void onClear (GameEngine engine, int playerID) {
		ojamaChecked[playerID] = false;
	}

	/*
	 * 設定画面の描画
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if(engine.statc[4] == 0) {
			if(engine.statc[2] < 9) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_ORANGE, 0,
						"GRAVITY", String.valueOf(engine.speed.gravity),
						"G-MAX", String.valueOf(engine.speed.denominator),
						"ARE", String.valueOf(engine.speed.are),
						"ARE LINE", String.valueOf(engine.speed.areLine),
						"LINE DELAY", String.valueOf(engine.speed.lineDelay),
						"LOCK DELAY", String.valueOf(engine.speed.lockDelay),
						"DAS", String.valueOf(engine.speed.das),
						"FALL DELAY", String.valueOf(engine.cascadeDelay),
						"CLEAR DELAY", String.valueOf(engine.cascadeClearDelay));

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 1/5", EventReceiver.COLOR_YELLOW);
			} else if(engine.statc[2] < 17) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_CYAN, 9,
						"COUNTER", OJAMA_COUNTER_STRING[ojamaCounterMode[playerID]],
						"MAX ATTACK", String.valueOf(maxAttack[playerID]),
						"MIN CHAIN", String.valueOf(rensaShibari[playerID]),
						"CLEAR SIZE", String.valueOf(engine.colorClearSize),
						"OJAMA RATE", String.valueOf(ojamaRate[playerID]),
						"HURRYUP", (hurryupSeconds[playerID] == 0) ? "NONE" : hurryupSeconds[playerID]+"SEC",
						"X COLUMN", dangerColumnDouble[playerID] ? "3 AND 4" : "3 ONLY",
						"X SHOW", GeneralUtil.getONorOFF(dangerColumnShowX[playerID]));

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 2/5", EventReceiver.COLOR_YELLOW);
			} else if(engine.statc[2] < 24) {
				initMenu(EventReceiver.COLOR_CYAN, 17);
				drawMenu(engine, playerID, receiver,
						"COUNTDOWN", (ojamaCountdown[playerID] == 10) ? "NONE" :
							String.valueOf(ojamaCountdown[playerID]),
						"ZENKESHI", ZENKESHI_TYPE_NAMES[zenKeshiType[playerID]]);
				menuColor = (zenKeshiType[playerID] == ZENKESHI_MODE_FEVER) ?
						EventReceiver.COLOR_PURPLE : EventReceiver.COLOR_WHITE;
				drawMenu(engine, playerID, receiver, "F-MAP SET", FEVER_MAPS[feverMapSet[playerID]].toUpperCase());
				menuColor = EventReceiver.COLOR_DARKBLUE;
				drawMenu(engine, playerID, receiver,
						"OUTLINE", OUTLINE_TYPE_NAMES[outlineType[playerID]],
						"SHOW CHAIN", CHAIN_DISPLAY_NAMES[chainDisplayType[playerID]],
						"FALL ANIM", cascadeSlow[playerID] ? "FEVER" : "CLASSIC");
				menuColor = EventReceiver.COLOR_CYAN;
				drawMenu(engine, playerID, receiver, "CHAINPOWER", newChainPower[playerID] ? "FEVER" : "CLASSIC");

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 3/5", EventReceiver.COLOR_YELLOW);
			} else if(engine.statc[2] < 32) {
				initMenu(EventReceiver.COLOR_PINK, 24);
				drawMenu(engine, playerID, receiver,
						"USE MAP", GeneralUtil.getONorOFF(useMap[playerID]),
						"MAP SET", String.valueOf(mapSet[playerID]),
						"MAP NO.", (mapNumber[playerID] < 0) ? "RANDOM" : mapNumber[playerID]+"/"+(mapMaxNo[playerID]-1),
						"BIG DISP", GeneralUtil.getONorOFF(bigDisplay));
				menuColor = EventReceiver.COLOR_DARKBLUE;
				drawMenu(engine, playerID, receiver,
						"BGM", String.valueOf(bgmno),
						"SE", GeneralUtil.getONorOFF(enableSE[playerID]));
				menuColor = EventReceiver.COLOR_GREEN;
				drawMenu(engine, playerID, receiver,
						"LOAD", String.valueOf(presetNumber[playerID]),
						"SAVE", String.valueOf(presetNumber[playerID]));

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 4/5", EventReceiver.COLOR_YELLOW);
			} else {
				receiver.drawMenuFont(engine, playerID, 0,  0, "ATTACK", EventReceiver.COLOR_CYAN);
				int multiplier = (int) (100 * getAttackMultiplier(dropSet[playerID], dropMap[playerID]));
				if (multiplier >= 100)
					receiver.drawMenuFont(engine, playerID, 2,  1, multiplier + "%",
							multiplier == 100 ? EventReceiver.COLOR_YELLOW : EventReceiver.COLOR_GREEN);
				else
					receiver.drawMenuFont(engine, playerID, 3,  1, multiplier + "%", EventReceiver.COLOR_RED);
				receiver.drawMenuFont(engine, playerID, 0,  2, "DEFEND", EventReceiver.COLOR_CYAN);
				multiplier = (int) (100 * getDefendMultiplier(dropSet[playerID], dropMap[playerID]));
				if (multiplier >= 100)
					receiver.drawMenuFont(engine, playerID, 2,  3, multiplier + "%",
							multiplier == 100 ? EventReceiver.COLOR_YELLOW : EventReceiver.COLOR_RED);
				else
					receiver.drawMenuFont(engine, playerID, 3,  3, multiplier + "%", EventReceiver.COLOR_GREEN);

				drawMenu(engine, playerID, receiver, 14, EventReceiver.COLOR_CYAN, 32,
						"DROP SET", DROP_SET_NAMES[dropSet[playerID]],
						"DROP MAP", String.format("%2d", dropMap[playerID]+1) + "/" +
									String.format("%2d", DROP_PATTERNS[dropSet[playerID]].length));

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 5/5", EventReceiver.COLOR_YELLOW);
			}
		} else {
			receiver.drawMenuFont(engine, playerID, 3, 10, "WAIT", EventReceiver.COLOR_YELLOW);
		}
	}

	public static double getAttackMultiplier(int set, int map)
	{
		try {
			return DROP_PATTERNS_ATTACK_MULTIPLIERS[set][map];
		} catch (ArrayIndexOutOfBoundsException e) {
			return 1.0;
		}
	}
	public static double getDefendMultiplier(int set, int map)
	{
		try {
			return DROP_PATTERNS_DEFEND_MULTIPLIERS[set][map];
		} catch (ArrayIndexOutOfBoundsException e) {
			return 1.0;
		}
	}

	/*
	 * Called for initialization during Ready (before initialization)
	 */
	@Override
	public boolean readyInit(GameEngine engine, int playerID) {
		super.readyInit(engine, playerID);
		engine.blockColors = BLOCK_COLORS;
		engine.numColors = 4;
		dropPattern[playerID] = DROP_PATTERNS[dropSet[playerID]][dropMap[playerID]];
		attackMultiplier[playerID] = getAttackMultiplier(dropSet[playerID], dropMap[playerID]);
		defendMultiplier[playerID] = getDefendMultiplier(dropSet[playerID], dropMap[playerID]);
		return false;
	}

	/*
	 * When the current piece is in action
	 */
	@Override
	public void renderMove(GameEngine engine, int playerID) {
		if(engine.gameStarted)
			drawX(engine, playerID);
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		int fldPosX = receiver.getFieldDisplayPositionX(engine, playerID);
		int fldPosY = receiver.getFieldDisplayPositionY(engine, playerID);
		int playerColor = (playerID == 0) ? EventReceiver.COLOR_RED : EventReceiver.COLOR_BLUE;
		int fontColor = EventReceiver.COLOR_WHITE;

		// Timer
		if(playerID == 0) {
			receiver.drawDirectFont(engine, playerID, 224, 8, GeneralUtil.getTime(engine.statistics.time));
		}

		// Ojama Counter
		fontColor = EventReceiver.COLOR_WHITE;
		if(ojama[playerID] >= 1) fontColor = EventReceiver.COLOR_YELLOW;
		if(ojama[playerID] >= 6) fontColor = EventReceiver.COLOR_ORANGE;
		if(ojama[playerID] >= 12) fontColor = EventReceiver.COLOR_RED;

		String strOjama = String.valueOf(ojama[playerID]);
		if(ojamaAdd[playerID] > 0)
			strOjama = strOjama + "(+" + String.valueOf(ojamaAdd[playerID]) + ")";

		if(!strOjama.equals("0")) {
			receiver.drawDirectFont(engine, playerID, fldPosX + 4, fldPosY + 32, strOjama, fontColor);
		}

		// Score
		String strScoreMultiplier = "";
		if((lastscore[playerID] != 0) && (lastmultiplier[playerID] != 0) && (scgettime[playerID] > 0))
			strScoreMultiplier = "(" + lastscore[playerID] + "e" + lastmultiplier[playerID] + ")";

		if(engine.displaysize == 1) {
			receiver.drawDirectFont(engine, playerID, fldPosX + 4, fldPosY + 440, String.format("%12d", score[playerID]), playerColor);
			receiver.drawDirectFont(engine, playerID, fldPosX + 4, fldPosY + 456, String.format("%12s", strScoreMultiplier), playerColor);
		} else if(engine.gameStarted) {
			receiver.drawDirectFont(engine, playerID, fldPosX - 28, fldPosY + 248, String.format("%8d", score[playerID]), playerColor);
			receiver.drawDirectFont(engine, playerID, fldPosX - 28, fldPosY + 264, String.format("%8s", strScoreMultiplier), playerColor);
		}

		if((engine.stat != GameEngine.STAT_MOVE) && (engine.stat != GameEngine.STAT_RESULT) && (engine.gameStarted))
			drawX(engine, playerID);

		if (!owner.engine[playerID].gameActive)
			return;

		// Countdown Blocks
		Block b;
		int blockColor, textColor;
		int d = (engine.displaysize == 1) ? 2 : 1;
		String str;
		if((engine.field != null) && (engine.stat != GameEngine.STAT_RESULT) && (engine.gameStarted))
			for (int x = 0; x < engine.field.getWidth(); x++)
				for (int y = 0; y < engine.field.getHeight(); y++)
				{
					b = engine.field.getBlock(x, y);
					if (!b.isEmpty() && b.countdown > 0)
					{
						blockColor = b.secondaryColor;
						textColor = EventReceiver.COLOR_WHITE;
						if (blockColor == Block.BLOCK_COLOR_BLUE)
							textColor = EventReceiver.COLOR_BLUE;
						else if (blockColor == Block.BLOCK_COLOR_GREEN)
							textColor = EventReceiver.COLOR_GREEN;
						else if (blockColor == Block.BLOCK_COLOR_RED)
							textColor = EventReceiver.COLOR_RED;
						else if (blockColor == Block.BLOCK_COLOR_YELLOW)
							textColor = EventReceiver.COLOR_YELLOW;

						str = (b.countdown >= 10) ? "d" : String.valueOf(b.countdown);
						receiver.drawMenuFont(engine, playerID, x * d, y * d, str, textColor, 1.0f * d);
					}
				}

		super.renderLast(engine, playerID);
	}

	@Override
	protected int ptsToOjama(GameEngine engine, int playerID, int pts, int rate)
	{
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;
		return ((int) (pts * attackMultiplier[playerID] * defendMultiplier[enemyID])+rate-1)/rate;
	}

	@Override
	public boolean onLineClear(GameEngine engine, int playerID) {
		if (engine.field == null || ojamaChecked[playerID])
			return false;

		ojamaChecked[playerID] = true;
		//Turn cleared ojama into normal blocks
		for (int x = 0; x < engine.field.getWidth(); x++)
			for (int y = (-1* engine.field.getHiddenHeight()); y < engine.field.getHeight(); y++)
			{
				Block b = engine.field.getBlock(x, y);
				if (b.getAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE) && b.hard < 4)
				{
					b.hard = 0;
					b.color = b.secondaryColor;
					b.countdown = 0;
					b.setAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE, false);
				}
			}
		return false;
	}

	@Override
	public boolean lineClearEnd(GameEngine engine, int playerID) {
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;
		if (ojamaAdd[enemyID] > 0)
		{
			ojama[enemyID] += ojamaAdd[enemyID];
			ojamaAdd[enemyID] = 0;
		}
		if (zenKeshi[playerID] && zenKeshiType[playerID] == ZENKESHI_MODE_FEVER)
		{
			loadFeverMap(engine, playerID, 4);
			zenKeshi[playerID] = false;
			zenKeshiDisplay[playerID] = 120;
		}

		if (engine.field == null)
			return false;

		boolean result = false;
		//Decrement countdowns
		if (ojamaCountdown[playerID] != 10 && !countdownDecremented[playerID])
		{
			countdownDecremented[playerID] = true;
			for (int y = (engine.field.getHiddenHeight() * -1); y < engine.field.getHeight(); y++)
				for (int x = 0; x < engine.field.getWidth(); x++)
				{
					Block b = engine.field.getBlock(x, y);
					if (b == null)
						continue;
					if (b.countdown > 1)
						b.countdown--;
					else if (b.countdown == 1)
					{
						b.countdown = 0;
						b.hard = 0;
						b.setAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE, false);
						b.color = b.secondaryColor;
						result = true;
					}
				}
			if (result)
				return true;
		}
		//Drop garbage if needed.
		if (ojama[playerID] > 0 && !ojamaDrop[playerID] && (!cleared[playerID] ||
				(ojamaCounterMode[playerID] != OJAMA_COUNTER_FEVER)))
		{
			ojamaDrop[playerID] = true;
			int width = engine.field.getWidth();
			int hiddenHeight = engine.field.getHiddenHeight();
			int drop = Math.min(ojama[playerID], maxAttack[playerID]);
			ojama[playerID] -= drop;
			engine.field.garbageDrop(engine, drop, false, 4, ojamaCountdown[playerID]);
			engine.field.setAllSkin(engine.getSkin());
			int patternCol = 0;
			for (int x = 0; x < engine.field.getWidth(); x++)
			{
				if (patternCol >= dropPattern[enemyID].length)
					patternCol = 0;
				int patternRow = 0;
				for (int y = ((drop + width - 1) / width) - hiddenHeight; y >= (-1 * hiddenHeight); y--)
				{
					Block b = engine.field.getBlock(x, y);
					if (b.getAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE) && b.secondaryColor == 0)
					{
						if (patternRow >= dropPattern[enemyID][patternCol].length)
							patternRow = 0;
						b.secondaryColor = dropPattern[enemyID][patternCol][patternRow];
						patternRow++;
					}
				}
				patternCol++;
			}
			return true;
		}
		//Check for game over
		if (!engine.field.getBlockEmpty(2, 0) ||
				(dangerColumnDouble[playerID] && !engine.field.getBlockEmpty(3, 0)))
			engine.stat = GameEngine.STAT_GAMEOVER;
		return false;
	}

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveOtherSetting(engine, owner.replayProp);
		savePreset(engine, owner.replayProp, -1 - playerID, "spf");

		if(useMap[playerID] && (fldBackup[playerID] != null)) {
			saveMap(fldBackup[playerID], owner.replayProp, playerID);
		}

		owner.replayProp.setProperty("avalanchevs.version", version);
	}
}
