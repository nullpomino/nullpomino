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

import java.util.Random;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

import org.apache.log4j.Logger;

/**
 * SPF VS-BATTLE mode (Beta)
 */
public class SPFMode extends DummyMode {
	/** Log (Apache log4j) */
	static Logger log = Logger.getLogger(SPFMode.class);

	/** Current version */
	private static final int CURRENT_VERSION = 0;

	/** Enabled piece types */
	private static final int[] PIECE_ENABLE = {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0};

	/** Block colors */
	private static final int[] BLOCK_COLORS =
	{
		Block.BLOCK_COLOR_RED,
		Block.BLOCK_COLOR_RED,
		Block.BLOCK_COLOR_RED,
		Block.BLOCK_COLOR_RED,
		Block.BLOCK_COLOR_GEM_RED,
		Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_GEM_GREEN,
		Block.BLOCK_COLOR_BLUE,
		Block.BLOCK_COLOR_BLUE,
		Block.BLOCK_COLOR_BLUE,
		Block.BLOCK_COLOR_BLUE,
		Block.BLOCK_COLOR_GEM_BLUE,
		Block.BLOCK_COLOR_YELLOW,
		Block.BLOCK_COLOR_YELLOW,
		Block.BLOCK_COLOR_YELLOW,
		Block.BLOCK_COLOR_YELLOW,
		Block.BLOCK_COLOR_GEM_YELLOW
	};

	private static final double[] ROW_VALUES =
	{
		2.3, 2.2, 2.1, 2.0, 1.9, 1.8, 1.7, 1.6, 1.5, 1.4, 1.3, 1.2, 1.1, 1.0
	};

	private static final int DIAMOND_COLOR = Block.BLOCK_COLOR_GEM_RAINBOW;

	/** Number of players */
	private static final int MAX_PLAYERS = 2;

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

	/** Names of rainbow power settings */
	private static final String[] RAINBOW_POWER_NAMES = {"NONE", "50%", "80%", "100%", "50/100%"};

	/** Each player's frame color */
	private static final int[] PLAYER_COLOR_FRAME = {GameEngine.FRAME_COLOR_RED, GameEngine.FRAME_COLOR_BLUE};

	/** GameManager that owns this mode */
	private GameManager owner;

	/** Drawing and event handling EventReceiver */
	private EventReceiver receiver;

	/** 溜まっている邪魔Blockのcount */
	private int[] ojama;

	/** 送った邪魔Blockのcount */
	private int[] ojamaSent;

	/** Time to display the most recent increase in score */
	private int[] scgettime;

	/** 使用するBGM */
	private int bgmno;

	/** Big */
	//private boolean[] big;

	/** Sound effectsON/OFF */
	private boolean[] enableSE;

	/** マップ使用 flag */
	private boolean[] useMap;

	/** 使用するマップセット number */
	private int[] mapSet;

	/** マップ number(-1でランダム) */
	private int[] mapNumber;

	/** Last preset number used */
	private int[] presetNumber;

	/** 勝者 */
	private int winnerID;

	/** マップセットのProperty file */
	private CustomProperties[] propMap;

	/** Maximumマップ number */
	private int[] mapMaxNo;

	/** バックアップ用フィールド (マップをリプレイに保存するときに使用) */
	private Field[] fldBackup;

	/** マップ選択用乱count */
	private Random randMap;

	/** Version */
	private int version;

	/** Amount of points earned from most recent clear */
	private int[] lastscore;

	/** Score */
	private int[] score;

	/** Settings for starting countdown for ojama blocks */
	private int[] ojamaCountdown;

	/** Hurryup開始までの秒count(0でHurryupなし) */
	private int[] hurryupSeconds;

	/** Time to display "ZENKESHI!" */
	private int[] zenKeshiDisplay;

	/** Time to display "TECH BONUS" */
	private int[] techBonusDisplay;

	/** Drop patterns */
	private int[][][] dropPattern;

	/** Drop map set selected */
	private int[] dropSet;

	/** Drop map selected */
	private int[] dropMap;

	/** Drop multipliers */
	private double[] attackMultiplier, defendMultiplier;

	/** Rainbow power settings for each player */
	private int[] diamondPower;

	/** Frame when squares were last checked */
	private int[] lastSquareCheck;

	/** Flag set when counters have been decremented */
	private boolean[] countdownDecremented;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "SPF VS-BATTLE (BETA)";
	}

	/*
	 * Number of players
	 */
	@Override
	public int getPlayers() {
		return MAX_PLAYERS;
	}

	/*
	 * Mode  initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		owner = manager;
		receiver = owner.receiver;

		ojama = new int[MAX_PLAYERS];
		ojamaSent = new int[MAX_PLAYERS];

		scgettime = new int[MAX_PLAYERS];
		bgmno = 0;
		//big = new boolean[MAX_PLAYERS];
		enableSE = new boolean[MAX_PLAYERS];
		hurryupSeconds = new int[MAX_PLAYERS];
		useMap = new boolean[MAX_PLAYERS];
		mapSet = new int[MAX_PLAYERS];
		mapNumber = new int[MAX_PLAYERS];
		presetNumber = new int[MAX_PLAYERS];
		propMap = new CustomProperties[MAX_PLAYERS];
		mapMaxNo = new int[MAX_PLAYERS];
		fldBackup = new Field[MAX_PLAYERS];
		randMap = new Random();

		lastscore = new int[MAX_PLAYERS];
		score = new int[MAX_PLAYERS];
		ojamaCountdown = new int[MAX_PLAYERS];

		zenKeshiDisplay = new int[MAX_PLAYERS];
		techBonusDisplay = new int[MAX_PLAYERS];

		dropSet = new int[MAX_PLAYERS];
		dropMap = new int[MAX_PLAYERS];
		dropPattern = new int[MAX_PLAYERS][][];
		attackMultiplier = new double[MAX_PLAYERS];
		defendMultiplier = new double[MAX_PLAYERS];
		diamondPower = new int[MAX_PLAYERS];
		lastSquareCheck = new int[MAX_PLAYERS];
		countdownDecremented = new boolean[MAX_PLAYERS];

		winnerID = -1;
	}

	/**
	 * Read speed presets
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 * @param preset Preset number
	 */
	private void loadPreset(GameEngine engine, CustomProperties prop, int preset) {
		engine.speed.gravity = prop.getProperty("spfvs.gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("spfvs.denominator." + preset, 256);
		engine.speed.are = prop.getProperty("spfvs.are." + preset, 24);
		engine.speed.areLine = prop.getProperty("spfvs.areLine." + preset, 24);
		engine.speed.lineDelay = prop.getProperty("spfvs.lineDelay." + preset, 10);
		engine.speed.lockDelay = prop.getProperty("spfvs.lockDelay." + preset, 30);
		engine.speed.das = prop.getProperty("spfvs.das." + preset, 14);
	}

	/**
	 * Save speed presets
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 * @param preset Preset number
	 */
	private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
		prop.setProperty("spfvs.gravity." + preset, engine.speed.gravity);
		prop.setProperty("spfvs.denominator." + preset, engine.speed.denominator);
		prop.setProperty("spfvs.are." + preset, engine.speed.are);
		prop.setProperty("spfvs.areLine." + preset, engine.speed.areLine);
		prop.setProperty("spfvs.lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("spfvs.lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("spfvs.das." + preset, engine.speed.das);
	}

	/**
	 * スピード以外の設定を読み込み
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 */
	private void loadOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		bgmno = prop.getProperty("spfvs.bgmno", 0);
		//big[playerID] = prop.getProperty("spfvs.big.p" + playerID, false);
		enableSE[playerID] = prop.getProperty("spfvs.enableSE.p" + playerID, true);
		hurryupSeconds[playerID] = prop.getProperty("vsbattle.hurryupSeconds.p" + playerID, 0);
		useMap[playerID] = prop.getProperty("spfvs.useMap.p" + playerID, false);
		mapSet[playerID] = prop.getProperty("spfvs.mapSet.p" + playerID, 0);
		mapNumber[playerID] = prop.getProperty("spfvs.mapNumber.p" + playerID, -1);
		presetNumber[playerID] = prop.getProperty("spfvs.presetNumber.p" + playerID, 0);
		ojamaCountdown[playerID] = prop.getProperty("spfvs.ojamaHard.p" + playerID, 5);
		dropSet[playerID] = prop.getProperty("spfvs.dropSet.p" + playerID, 0);
		dropMap[playerID] = prop.getProperty("spfvs.dropMap.p" + playerID, 0);
		diamondPower[playerID] = prop.getProperty("spfvs.rainbowPower.p" + playerID, 2);
	}

	/**
	 * スピード以外の設定を保存
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 */
	private void saveOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		prop.setProperty("spfvs.bgmno", bgmno);
		//prop.setProperty("spfvs.big.p" + playerID, big[playerID]);
		prop.setProperty("spfvs.enableSE.p" + playerID, enableSE[playerID]);
		prop.setProperty("vsbattle.hurryupSeconds.p" + playerID, hurryupSeconds[playerID]);
		prop.setProperty("spfvs.useMap.p" + playerID, useMap[playerID]);
		prop.setProperty("spfvs.mapSet.p" + playerID, mapSet[playerID]);
		prop.setProperty("spfvs.mapNumber.p" + playerID, mapNumber[playerID]);
		prop.setProperty("spfvs.presetNumber.p" + playerID, presetNumber[playerID]);
		prop.setProperty("spfvs.ojamaHard.p" + playerID, ojamaCountdown[playerID]);
		prop.setProperty("spfvs.dropSet.p" + playerID, dropSet[playerID]);
		prop.setProperty("spfvs.dropMap.p" + playerID, dropMap[playerID]);
		prop.setProperty("spfvs.rainbowPower.p" + playerID, diamondPower[playerID]);
	}

	/**
	 * マップ読み込み
	 * @param field フィールド
	 * @param prop Property file to read from
	 * @param preset 任意のID
	 */
	private void loadMap(Field field, CustomProperties prop, int id) {
		field.reset();
		//field.readProperty(prop, id);
		field.stringToField(prop.getProperty("map." + id, ""));
		field.setAllAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
		field.setAllAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
		field.setAllAttribute(Block.BLOCK_ATTRIBUTE_SELFPLACED, false);
	}

	/**
	 * マップ保存
	 * @param field フィールド
	 * @param prop Property file to save to
	 * @param id 任意のID
	 */
	private void saveMap(Field field, CustomProperties prop, int id) {
		//field.writeProperty(prop, id);
		prop.setProperty("map." + id, field.fieldToString());
	}

	/**
	 * プレビュー用にマップを読み込み
	 * @param engine GameEngine
	 * @param playerID Player number
	 * @param id マップID
	 * @param forceReload trueにするとマップファイルを強制再読み込み
	 */
	private void loadMapPreview(GameEngine engine, int playerID, int id, boolean forceReload) {
		if((propMap[playerID] == null) || (forceReload)) {
			mapMaxNo[playerID] = 0;
			propMap[playerID] = receiver.loadProperties("config/map/spf/" + mapSet[playerID] + ".map");
		}

		if((propMap[playerID] == null) && (engine.field != null)) {
			engine.field.reset();
		} else if(propMap[playerID] != null) {
			mapMaxNo[playerID] = propMap[playerID].getProperty("map.maxMapNumber", 0);
			engine.createFieldIfNeeded();
			loadMap(engine.field, propMap[playerID], id);
			engine.field.setAllSkin(engine.getSkin());
		}
	}

	private void loadDropMapPreview(GameEngine engine, int playerID, int[][] pattern) {
		if((pattern == null) && (engine.field != null)) {
			engine.field.reset();
		} else if(pattern != null) {
			log.debug("Loading drop map preview");
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
		if(playerID == 1) {
			engine.randSeed = owner.engine[0].randSeed;
			engine.random = new Random(owner.engine[0].randSeed);
		}

		engine.framecolor = PLAYER_COLOR_FRAME[playerID];
		engine.clearMode = GameEngine.CLEAR_GEM_COLOR;
		engine.garbageColorClear = true;
		engine.lineGravityType = GameEngine.LINE_GRAVITY_CASCADE;
		for(int i = 0; i < Piece.PIECE_COUNT; i++)
			engine.nextPieceEnable[i] = (PIECE_ENABLE[i] == 1);
		engine.blockColors = BLOCK_COLORS;
		engine.randomBlockColor = true;
		engine.connectBlocks = false;

		ojama[playerID] = 0;
		ojamaSent[playerID] = 0;
		score[playerID] = 0;
		scgettime[playerID] = 0;
		zenKeshiDisplay[playerID] = 0;
		techBonusDisplay[playerID] = 0;
		lastSquareCheck[playerID] = -1;
		countdownDecremented[playerID] = true;

		if(engine.owner.replayMode == false) {
			loadOtherSetting(engine, engine.owner.modeConfig);
			loadPreset(engine, engine.owner.modeConfig, -1 - playerID);
			version = CURRENT_VERSION;
		} else {
			loadOtherSetting(engine, engine.owner.replayProp);
			loadPreset(engine, engine.owner.replayProp, -1 - playerID);
			version = owner.replayProp.getProperty("spfvs.version", 0);
		}
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if((engine.owner.replayMode == false) && (engine.statc[4] == 0)) {
			// Up
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
				engine.statc[2]--;
				if(engine.statc[2] < 0){
					engine.statc[2] = 18;
					loadDropMapPreview(engine, playerID, DROP_PATTERNS[dropSet[playerID]][dropMap[playerID]]);
				}
				else if (engine.statc[2] == 16)
					engine.field = null;
				engine.playSE("cursor");
			}
			// Down
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
				engine.statc[2]++;
				if(engine.statc[2] > 18) {
					engine.statc[2] = 0;
					engine.field = null;
				}
				else if (engine.statc[2] == 17)
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
					engine.speed.lockDelay += change;
					if(engine.speed.lockDelay < 0) engine.speed.lockDelay = 99;
					if(engine.speed.lockDelay > 99) engine.speed.lockDelay = 0;
					break;
				case 6:
					engine.speed.das += change;
					if(engine.speed.das < 0) engine.speed.das = 99;
					if(engine.speed.das > 99) engine.speed.das = 0;
					break;
				case 7:
				case 8:
					presetNumber[playerID] += change;
					if(presetNumber[playerID] < 0) presetNumber[playerID] = 99;
					if(presetNumber[playerID] > 99) presetNumber[playerID] = 0;
					break;
				case 9:
					bgmno += change;
					if(bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
					if(bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
					break;
				case 10:
					useMap[playerID] = !useMap[playerID];
					if(!useMap[playerID]) {
						if(engine.field != null) engine.field.reset();
					} else {
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 11:
					mapSet[playerID] += change;
					if(mapSet[playerID] < 0) mapSet[playerID] = 99;
					if(mapSet[playerID] > 99) mapSet[playerID] = 0;
					if(useMap[playerID]) {
						mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 12:
					if(useMap[playerID]) {
						mapNumber[playerID] += change;
						if(mapNumber[playerID] < -1) mapNumber[playerID] = mapMaxNo[playerID] - 1;
						if(mapNumber[playerID] > mapMaxNo[playerID] - 1) mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					} else {
						mapNumber[playerID] = -1;
					}
					break;
				case 13:
					enableSE[playerID] = !enableSE[playerID];
					break;
				case 14:
					if (m > 10) hurryupSeconds[playerID] += change*m/10;
					else hurryupSeconds[playerID] += change;
					if(hurryupSeconds[playerID] < 0) hurryupSeconds[playerID] = 300;
					if(hurryupSeconds[playerID] > 300) hurryupSeconds[playerID] = 0;
					break;
				case 15:
					ojamaCountdown[playerID] += change;
					if(ojamaCountdown[playerID] < 1) ojamaCountdown[playerID] = 9;
					if(ojamaCountdown[playerID] > 9) ojamaCountdown[playerID] = 1;
					break;
				case 16:
					diamondPower[playerID] += change;
					if(diamondPower[playerID] < 0) diamondPower[playerID] = 3;
					if(diamondPower[playerID] > 3) diamondPower[playerID] = 0;
					break;
				case 17:
					dropSet[playerID] += change;
					if(dropSet[playerID] < 0) dropSet[playerID] = DROP_PATTERNS.length-1;
					if(dropSet[playerID] >= DROP_PATTERNS.length) dropSet[playerID] = 0;
					if(dropMap[playerID] >= DROP_PATTERNS[dropSet[playerID]].length) dropMap[playerID] = 0;
					loadDropMapPreview(engine, playerID, DROP_PATTERNS[dropSet[playerID]][dropMap[playerID]]);
					break;
				case 18:
					dropMap[playerID] += change;
					if(dropMap[playerID] < 0) dropMap[playerID] = DROP_PATTERNS[dropSet[playerID]].length-1;
					if(dropMap[playerID] >= DROP_PATTERNS[dropSet[playerID]].length) dropMap[playerID] = 0;
					loadDropMapPreview(engine, playerID, DROP_PATTERNS[dropSet[playerID]][dropMap[playerID]]);
					break;
				/*
				case 18:
					big[playerID] = !big[playerID];
					break;
					*/
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");

				if(engine.statc[2] == 7) {
					loadPreset(engine, owner.modeConfig, presetNumber[playerID]);
				} else if(engine.statc[2] == 8) {
					savePreset(engine, owner.modeConfig, presetNumber[playerID]);
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					saveOtherSetting(engine, owner.modeConfig);
					savePreset(engine, owner.modeConfig, -1 - playerID);
					receiver.saveModeConfig(owner.modeConfig);
					engine.statc[4] = 1;
				}
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				engine.quitflag = true;
			}

			// プレビュー用マップ読み込み
			if(useMap[playerID] && (engine.statc[3] == 0)) {
				loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
			}

			// ランダムマッププレビュー
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

			if(engine.statc[3] >= 180)
				engine.statc[4] = 1;
			else if(engine.statc[3] > 120)
				engine.statc[2] = 17;
			else if (engine.statc[3] == 120)
			{
				engine.statc[2] = 17;
				loadDropMapPreview(engine, playerID, DROP_PATTERNS[dropSet[playerID]][dropMap[playerID]]);
			}
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
						"DAS", String.valueOf(engine.speed.das));
				drawMenu(engine, playerID, receiver, 14, EventReceiver.COLOR_GREEN, 7,
						"LOAD", String.valueOf(presetNumber[playerID]),
						"SAVE", String.valueOf(presetNumber[playerID]));
				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 1/3", EventReceiver.COLOR_YELLOW);
			} else if (engine.statc[2] < 17){
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_PINK, 9,
						"BGM", String.valueOf(bgmno));
				drawMenu(engine, playerID, receiver, 2, EventReceiver.COLOR_CYAN, 10,
						"USE MAP", GeneralUtil.getONorOFF(useMap[playerID]),
						"MAP SET", String.valueOf(mapSet[playerID]),
						"MAP NO.", (mapNumber[playerID] < 0) ? "RANDOM" : mapNumber[playerID]+"/"+(mapMaxNo[playerID]-1),
						"SE", GeneralUtil.getONorOFF(enableSE[playerID]),
						"HURRYUP", (hurryupSeconds[playerID] == 0) ? "NONE" : hurryupSeconds[playerID]+"SEC",
						"COUNTDOWN", String.valueOf(ojamaCountdown[playerID]));
				receiver.drawMenuFont(engine, playerID, 0, 14, "RAINBOW", EventReceiver.COLOR_CYAN);
				drawMenu(engine, playerID, receiver, 15, EventReceiver.COLOR_CYAN, 16,
						"GEM POWER", RAINBOW_POWER_NAMES[diamondPower[playerID]]);

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 2/3", EventReceiver.COLOR_YELLOW);
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

				drawMenu(engine, playerID, receiver, 14, EventReceiver.COLOR_CYAN, 17,
						"DROP SET", DROP_SET_NAMES[dropSet[playerID]],
						"DROP MAP", String.format("%2d", dropMap[playerID]+1) + "/" +
									String.format("%2d", DROP_PATTERNS[dropSet[playerID]].length));

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 3/3", EventReceiver.COLOR_YELLOW);
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
	 * Readyの時のCalled at initialization (Initialization前）
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			engine.numColors = BLOCK_COLORS.length;
			engine.rainbowAnimate = (playerID == 0);
			engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_CONNECT;

			dropPattern[playerID] = DROP_PATTERNS[dropSet[playerID]][dropMap[playerID]];
			attackMultiplier[playerID] = getAttackMultiplier(dropSet[playerID], dropMap[playerID]);
			defendMultiplier[playerID] = getDefendMultiplier(dropSet[playerID], dropMap[playerID]);

			// マップ読み込み・リプレイ保存用にバックアップ
			if(useMap[playerID]) {
				if(owner.replayMode) {
					engine.createFieldIfNeeded();
					loadMap(engine.field, owner.replayProp, playerID);
					engine.field.setAllSkin(engine.getSkin());
				} else {
					if(propMap[playerID] == null) {
						propMap[playerID] = receiver.loadProperties("config/map/spf/" + mapSet[playerID] + ".map");
					}

					if(propMap[playerID] != null) {
						engine.createFieldIfNeeded();

						if(mapNumber[playerID] < 0) {
							if((playerID == 1) && (useMap[0]) && (mapNumber[0] < 0)) {
								engine.field.copy(owner.engine[0].field);
							} else {
								int no = (mapMaxNo[playerID] < 1) ? 0 : randMap.nextInt(mapMaxNo[playerID]);
								loadMap(engine.field, propMap[playerID], no);
							}
						} else {
							loadMap(engine.field, propMap[playerID], mapNumber[playerID]);
						}

						engine.field.setAllSkin(engine.getSkin());
						fldBackup[playerID] = new Field(engine.field);
					}
				}
			} else if(engine.field != null) {
				engine.field.reset();
			}
		}
		else if (engine.statc[0] == 1 && diamondPower[playerID] > 0)
			for (int x = 24; x < engine.nextPieceArraySize; x += 25)
				engine.nextPieceArrayObject[x].block[1].color = DIAMOND_COLOR;
		return false;
	}

	/*
	 * ゲーム開始時の処理
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		//engine.big = big[playerID];
		engine.enableSE = enableSE[playerID];
		if(playerID == 1) owner.bgmStatus.bgm = bgmno;
		//engine.colorClearSize = big[playerID] ? 8 : 2;
		engine.colorClearSize = 2;
		engine.ignoreHidden = false;

		engine.tspinAllowKick = false;
		engine.tspinEnable = false;
		engine.useAllSpinBonus = false;
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		// ステータス表示
		if(playerID == 0) {
			receiver.drawScoreFont(engine, playerID, -1, 0, "SPF VS", EventReceiver.COLOR_GREEN);

			receiver.drawScoreFont(engine, playerID, -1, 2, "OJAMA", EventReceiver.COLOR_PURPLE);
			receiver.drawScoreFont(engine, playerID, -1, 3, "1P:", EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID, 3, 3, String.valueOf(ojama[0]), (ojama[0] > 0));
			receiver.drawScoreFont(engine, playerID, -1, 4, "2P:", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 3, 4, String.valueOf(ojama[1]), (ojama[1] > 0));

			receiver.drawScoreFont(engine, playerID, -1, 6, "ATTACK", EventReceiver.COLOR_GREEN);
			receiver.drawScoreFont(engine, playerID, -1, 7, "1P: " + String.valueOf(ojamaSent[0]), EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID, -1, 8, "2P: " + String.valueOf(ojamaSent[1]), EventReceiver.COLOR_BLUE);

			receiver.drawScoreFont(engine, playerID, -1, 10, "SCORE", EventReceiver.COLOR_PURPLE);
			receiver.drawScoreFont(engine, playerID, -1, 11, "1P: " + String.valueOf(score[0]), EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID, -1, 12, "2P: " + String.valueOf(score[1]), EventReceiver.COLOR_BLUE);

			receiver.drawScoreFont(engine, playerID, -1, 14, "TIME", EventReceiver.COLOR_GREEN);
			receiver.drawScoreFont(engine, playerID, -1, 15, GeneralUtil.getTime(engine.statistics.time));
		}

		if (!owner.engine[playerID].gameActive)
			return;

		if(techBonusDisplay[playerID] > 0)
			receiver.drawMenuFont(engine, playerID, 0, 20, "TECH BONUS", EventReceiver.COLOR_YELLOW);
		if(zenKeshiDisplay[playerID] > 0)
			receiver.drawMenuFont(engine, playerID, 1, 21, "ZENKESHI!", EventReceiver.COLOR_YELLOW);

		Block b;
		int blockColor, textColor;
		if (engine.field != null)
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
						receiver.drawMenuFont(engine, playerID, x, y, String.valueOf(b.countdown), textColor);
					}
				}
	}

	@Override
	public boolean onMove (GameEngine engine, int playerID) {
		countdownDecremented[playerID] = false;
		return false;
	}

	@Override
	public void pieceLocked(GameEngine engine, int playerID, int avalanche) {
		if (engine.field == null)
			return;
		checkAll(engine, playerID);
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int avalanche) {
		if (engine.field == null)
			return;

		checkAll(engine, playerID);

		if (engine.field.canCascade())
			return;

		int enemyID = 0;
		if(playerID == 0) enemyID = 1;

		int width = engine.field.getWidth();
		int height = engine.field.getHeight();
		int hiddenHeight = engine.field.getHiddenHeight();

		int diamondBreakColor = Block.BLOCK_COLOR_INVALID;
		if (diamondPower[playerID] > 0)
			for (int y = (-1*hiddenHeight); y < height && diamondBreakColor == Block.BLOCK_COLOR_INVALID; y++)
				for (int x = 0; x < width && diamondBreakColor == Block.BLOCK_COLOR_INVALID; x++)
					if (engine.field.getBlockColor(x, y) == DIAMOND_COLOR)
					{
						receiver.blockBreak(engine, playerID, x, y, engine.field.getBlock(x, y));
						engine.field.setBlockColor(x, y, Block.BLOCK_COLOR_NONE);
						if (y+1 >= height)
						{
							techBonusDisplay[playerID] = 120;
							engine.statistics.score += 10000;
							score[playerID] += 10000;
						}
						else
							diamondBreakColor = engine.field.getBlockColor(x, y+1, true);
					}
		double pts = 0;
		double add, multiplier;
		Block b;
		//Clear blocks from diamond
		if (diamondBreakColor > Block.BLOCK_COLOR_NONE)
		{
			engine.field.allClearColor(diamondBreakColor, true, true);
			for (int y = (-1*hiddenHeight); y < height; y++)
			{
				multiplier = getRowValue(y);
				for (int x = 0; x < width; x++)
					if (engine.field.getBlockColor(x, y, true) == diamondBreakColor)
					{
						pts += multiplier * 7;
						receiver.blockBreak(engine, playerID, x, y, engine.field.getBlock(x, y));
						engine.field.setBlockColor(x, y, Block.BLOCK_COLOR_NONE);
					}
			}
		}
		if (diamondPower[playerID] == 1)
			pts *= 0.5;
		else if (diamondPower[playerID] == 2)
			pts *= 0.8;
		//TODO: Add diamond glitch
		//Clear blocks
		//engine.field.gemColorCheck(engine.colorClearSize, true, engine.garbageColorClear, engine.ignoreHidden);
		for (int y = (-1*hiddenHeight); y < height; y++)
		{
			multiplier = getRowValue(y);
			for (int x = 0; x < width; x++)
			{
				b = engine.field.getBlock(x, y);
				if (b == null)
					continue;
				if (!b.getAttribute(Block.BLOCK_ATTRIBUTE_ERASE) || b.isEmpty())
					continue;
				add = multiplier * 7;
				if (b.bonusValue > 1)
					add *= b.bonusValue;
				if (b.getAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE))
				{
					add /= 2.0;
					b.secondaryColor = 0;
				}
				receiver.blockBreak(engine, playerID, x, y, b);
				engine.field.setBlockColor(x, y, Block.BLOCK_COLOR_NONE);
				pts += add;
			}
		}
		if (engine.chain > 1)
			pts += (engine.chain-1)*20.0;
		engine.playSE("combo" + Math.min(engine.chain, 20));
		double ojamaNew = (int) (pts*attackMultiplier[playerID]/7.0);

		if (engine.field.isEmpty()) {
			engine.playSE("bravo");
			zenKeshiDisplay[playerID] = 120;
			ojamaNew += 12;
			engine.statistics.score += 1000;
			score[playerID] += 1000;
		}

		lastscore[playerID] = ((int) pts) * 10;
		scgettime[playerID] = 120;
		score[playerID] += lastscore[playerID];

		if (hurryupSeconds[playerID] > 0 && engine.statistics.time > hurryupSeconds[playerID])
			ojamaNew *= 1 << (engine.statistics.time / (hurryupSeconds[playerID] * 60));

		if (ojama[playerID] > 0 && ojamaNew > 0.0)
		{
			int delta = Math.min(ojama[playerID] << 1, (int) ojamaNew);
			ojama[playerID] -= delta >> 1;
			ojamaNew -= delta;
		}
		int ojamaSend = (int) (ojamaNew * defendMultiplier[enemyID]);
		if (ojamaSend > 0)
			ojama[enemyID] += ojamaSend;
	}

	public static double getRowValue(int row)
	{
		return ROW_VALUES[Math.min(Math.max(row, 0), ROW_VALUES.length-1)];
	}

	public void checkAll(GameEngine engine, int playerID) {
		boolean recheck = checkCountdown(engine, playerID);
		if (recheck)
			log.debug("Converted garbage blocks to regular blocks. Rechecking squares.");
		checkSquares(engine, playerID, recheck);
	}

	public boolean checkCountdown (GameEngine engine, int playerID) {
		if (countdownDecremented[playerID])
			return false;
		countdownDecremented[playerID] = true;
		boolean result = false;
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
					b.setAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE, false);
					b.color = b.secondaryColor;
					result = true;
				}
			}
		return result;
	}

	public void checkSquares (GameEngine engine, int playerID, boolean forceRecheck)
	{
		if (engine.field == null)
			return;
		if (engine.statistics.time == lastSquareCheck[playerID] && !forceRecheck)
			return;
		lastSquareCheck[playerID] = engine.statistics.time;

		log.debug("Checking squares.");

		int width = engine.field.getWidth();
		int height = engine.field.getHeight();
		int hiddenHeight = engine.field.getHiddenHeight();

		int color;
		Block b;
		int minX, minY, maxX, maxY;
		for (int x = 0; x < width; x++)
			for (int y = (-1*hiddenHeight); y < height; y++)
			{
				color = engine.field.getBlockColor(x, y);
				if (color < Block.BLOCK_COLOR_RED || color > Block.BLOCK_COLOR_PURPLE)
					continue;
				minX = x;
				minY = y;
				maxX = x;
				maxY = y;
				boolean expanded = false;
				b = engine.field.getBlock(x, y);
				if (!b.getAttribute(Block.BLOCK_ATTRIBUTE_BROKEN) &&
						b.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT) &&
						b.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN) &&
						!b.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP) &&
						!b.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT))
				{
					//Find boundaries of existing gem block
					maxX++;
					maxY++;
					Block test;
					while (maxX < width)
					{
						test = engine.field.getBlock(maxX, y);
						if (test == null)
						{
							maxX--;
							break;
						}
						if (test.color != color)
						{
							maxX--;
							break;
						}
						if (!test.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT))
							break;
						maxX++;
					}
					while (maxY < height)
					{
						test = engine.field.getBlock(x, maxY);
						if (test == null)
						{
							maxY--;
							break;
						}
						if (test.color != color)
						{
							maxY--;
							break;
						}
						if (!test.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))
							break;
						maxY++;
					}
					log.debug("Pre-existing square found: (" + minX + ", " + minY + ") to (" +
							 maxX + ", " + maxY + ")");
				}
				else if (b.getAttribute(Block.BLOCK_ATTRIBUTE_BROKEN) &&
						color == engine.field.getBlockColor(x+1, y) &&
						color == engine.field.getBlockColor(x, y+1) &&
						color == engine.field.getBlockColor(x+1, y+1))
				{
					Block bR = engine.field.getBlock(x+1, y);
					Block bD = engine.field.getBlock(x, y+1);
					Block bDR = engine.field.getBlock(x+1, y+1);
					if (
							bR.getAttribute(Block.BLOCK_ATTRIBUTE_BROKEN) &&
							bD.getAttribute(Block.BLOCK_ATTRIBUTE_BROKEN) &&
							bDR.getAttribute(Block.BLOCK_ATTRIBUTE_BROKEN))
					{
						//Form new gem block
						maxX = x+1;
						maxY = y+1;
						b.setAttribute(Block.BLOCK_ATTRIBUTE_BROKEN, false);
						bR.setAttribute(Block.BLOCK_ATTRIBUTE_BROKEN, false);
						bD.setAttribute(Block.BLOCK_ATTRIBUTE_BROKEN, false);
						bDR.setAttribute(Block.BLOCK_ATTRIBUTE_BROKEN, false);
						expanded = true;
					}
					log.debug("New square formed: (" + minX + ", " + minY + ") to (" +
							 maxX + ", " + maxY + ")");
				}
				if (maxX <= minX || maxY <= minY)
					continue; //No gem block, skip to next block
				boolean expandHere, done;
				int testX, testY;
				Block bTest;
				log.debug("Testing square for expansion. Coordinates before: (" + minX + ", " + minY + ") to (" +
						 maxX + ", " + maxY + ")");
				//Expand up
				for (testY = minY-1, done = false; testY >= (-1 * hiddenHeight) && !done; testY--)
				{
					log.debug("Testing to expand up. testY = " + testY);
					if (color != engine.field.getBlockColor(minX, testY) ||
							color != engine.field.getBlockColor(maxX, testY))
						break;
					if (engine.field.getBlock(minX, testY).getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT) ||
							engine.field.getBlock(maxX, testY).getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT))
						break;
					expandHere = true;
					for (testX = minX; testX <= maxX && !done; testX++)
					{
						if (engine.field.getBlockColor(testX, testY) != color)
						{
							done = true;
							expandHere = false;
						}
						else if (engine.field.getBlock(testX, testY).getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP))
							expandHere = false;
					}
					if (expandHere)
					{
						minY = testY;
						expanded = true;
					}
				}
				//Expand left
				for (testX = minX-1, done = false; testX >= 0 && !done; testX--)
				{
					if (color != engine.field.getBlockColor(testX, minY) ||
							color != engine.field.getBlockColor(testX, maxY))
						break;
					if (engine.field.getBlock(testX, minY).getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP) ||
							engine.field.getBlock(testX, maxY).getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))
						break;
					expandHere = true;
					for (testY = minY; testY <= maxY && !done; testY++)
					{
						if (engine.field.getBlockColor(testX, testY) != color)
						{
							done = true;
							expandHere = false;
						}
						else if (engine.field.getBlock(testX, testY).getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT))
							expandHere = false;
					}
					if (expandHere)
					{
						minX = testX;
						expanded = true;
					}
				}
				//Expand right
				for (testX = maxX+1, done = false; testX < width && !done; testX++)
				{
					if (color != engine.field.getBlockColor(testX, minY) ||
							color != engine.field.getBlockColor(testX, maxY))
						break;
					if (engine.field.getBlock(testX, minY).getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP) ||
							engine.field.getBlock(testX, maxY).getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))
						break;
					expandHere = true;
					for (testY = minY; testY <= maxY && !done; testY++)
					{
						if (engine.field.getBlockColor(testX, testY) != color)
						{
							done = true;
							expandHere = false;
						}
						else if (engine.field.getBlock(testX, testY).getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT))
							expandHere = false;
					}
					if (expandHere)
					{
						maxX = testX;
						expanded = true;
					}
				}
				//Expand down
				for (testY = maxY+1, done = false; testY < height && !done; testY++)
				{
					if (color != engine.field.getBlockColor(minX, testY) ||
							color != engine.field.getBlockColor(maxX, testY))
						break;
					if (engine.field.getBlock(minX, testY).getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT) ||
							engine.field.getBlock(maxX, testY).getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT))
						break;
					expandHere = true;
					for (testX = minX; testX <= maxX && !done; testX++)
					{
						if (engine.field.getBlockColor(testX, testY) != color)
						{
							done = true;
							expandHere = false;
						}
						else if (engine.field.getBlock(testX, testY).getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))
							expandHere = false;
					}
					if (expandHere)
					{
						maxY = testY;
						expanded = true;
					}
				}
				log.debug("expanded = " + expanded);
				if (expanded)
				{

					log.debug("Expanding square. Coordinates after: (" + minX + ", " + minY + ") to (" +
							 maxX + ", " + maxY + ")");
					int size = Math.min(maxX - minX + 1, maxY - minY + 1);
					for (testX = minX; testX <= maxX; testX++)
						for (testY = minY; testY <= maxY; testY++)
						{
							bTest = engine.field.getBlock(testX, testY);
							bTest.setAttribute(Block.BLOCK_ATTRIBUTE_BROKEN, false);
							bTest.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, testX != minX);
							bTest.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, testY != maxY);
							bTest.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, testY != minY);
							bTest.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, testX != maxX);
							bTest.bonusValue = size;
						}
					}
				}
	}

	public boolean lineClearEnd(GameEngine engine, int playerID) {
		if (engine.field == null)
			return false;

		int width = engine.field.getWidth();
		int height = engine.field.getHeight();
		int hiddenHeight = engine.field.getHiddenHeight();

		for (int y = (-1*hiddenHeight); y < height; y++)
			for (int x = 0; x < width; x++)
				if (engine.field.getBlockColor(x, y) == DIAMOND_COLOR)
				{
					calcScore(engine, playerID, 0);
					return true;
				}

		checkAll(engine, playerID);

		//Drop garbage if needed.
		if (ojama[playerID] > 0)
		{
			int enemyID = 0;
			if(playerID == 0) enemyID = 1;

			int dropRows = Math.min((ojama[playerID] + width - 1) / width, engine.field.getHighestBlockY(3));
			if (dropRows <= 0)
				return false;
			int drop = Math.min(ojama[playerID], width * dropRows);
			ojama[playerID] -= drop;
			//engine.field.garbageDrop(engine, drop, big[playerID], ojamaHard[playerID], 3);
			engine.field.garbageDrop(engine, drop, false, 0, ojamaCountdown[playerID], 3);
			engine.field.setAllSkin(engine.getSkin());
			int patternCol = 0;
			for (int x = 0; x < engine.field.getWidth(); x++)
			{
				if (patternCol >= dropPattern[enemyID].length)
					patternCol = 0;
				int patternRow = 0;
				for (int y = dropRows - hiddenHeight; y >= (-1 * hiddenHeight); y--)
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
		return false;
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		scgettime[playerID]++;
		if (zenKeshiDisplay[playerID] > 0)
			zenKeshiDisplay[playerID]--;
		int width = 1;
		if (engine.field != null)
			width = engine.field.getWidth();
		int blockHeight = receiver.getBlockGraphicsHeight(engine, playerID);
		// せり上がりMeter
		if(ojama[playerID] * blockHeight / width > engine.meterValue) {
			engine.meterValue++;
		} else if(ojama[playerID] * blockHeight / width < engine.meterValue) {
			engine.meterValue--;
		}
		if(ojama[playerID] > 30) engine.meterColor = GameEngine.METER_COLOR_RED;
		else if(ojama[playerID] > 10) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		else engine.meterColor = GameEngine.METER_COLOR_GREEN;

		// 決着
		if((playerID == 1) && (owner.engine[0].gameActive)) {
			if((owner.engine[0].stat == GameEngine.STAT_GAMEOVER) && (owner.engine[1].stat == GameEngine.STAT_GAMEOVER)) {
				// 引き分け
				winnerID = -1;
				owner.engine[0].gameActive = false;
				owner.engine[1].gameActive = false;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			} else if((owner.engine[0].stat != GameEngine.STAT_GAMEOVER) && (owner.engine[1].stat == GameEngine.STAT_GAMEOVER)) {
				// 1P勝利
				winnerID = 0;
				owner.engine[0].gameActive = false;
				owner.engine[1].gameActive = false;
				owner.engine[0].stat = GameEngine.STAT_EXCELLENT;
				owner.engine[0].resetStatc();
				owner.engine[0].statc[1] = 1;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			} else if((owner.engine[0].stat == GameEngine.STAT_GAMEOVER) && (owner.engine[1].stat != GameEngine.STAT_GAMEOVER)) {
				// 2P勝利
				winnerID = 1;
				owner.engine[0].gameActive = false;
				owner.engine[1].gameActive = false;
				owner.engine[1].stat = GameEngine.STAT_EXCELLENT;
				owner.engine[1].resetStatc();
				owner.engine[1].statc[1] = 1;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			}
		}
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID, 0, 1, "RESULT", EventReceiver.COLOR_ORANGE);
		if(winnerID == -1) {
			receiver.drawMenuFont(engine, playerID, 6, 2, "DRAW", EventReceiver.COLOR_GREEN);
		} else if(winnerID == playerID) {
			receiver.drawMenuFont(engine, playerID, 6, 2, "WIN!", EventReceiver.COLOR_YELLOW);
		} else {
			receiver.drawMenuFont(engine, playerID, 6, 2, "LOSE", EventReceiver.COLOR_WHITE);
		}

		float apm = (float)(ojamaSent[playerID] * 3600) / (float)(engine.statistics.time);
		drawResult(engine, playerID, receiver, 3, EventReceiver.COLOR_ORANGE,
				"ATTACK", String.format("%10d", ojamaSent[playerID]));
		drawResultStats(engine, playerID, receiver, 5, EventReceiver.COLOR_ORANGE,
				STAT_LINES, STAT_PIECE);
		drawResult(engine, playerID, receiver, 9, EventReceiver.COLOR_ORANGE,
				"ATTACK/MIN", String.format("%10g", apm));
		drawResultStats(engine, playerID, receiver, 11, EventReceiver.COLOR_ORANGE,
				STAT_LPM, STAT_PPS, STAT_TIME);
	}

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveOtherSetting(engine, owner.replayProp);
		savePreset(engine, owner.replayProp, -1 - playerID);

		if(useMap[playerID] && (fldBackup[playerID] != null)) {
			saveMap(fldBackup[playerID], owner.replayProp, playerID);
		}

		owner.replayProp.setProperty("spfvs.version", version);
	}
}
