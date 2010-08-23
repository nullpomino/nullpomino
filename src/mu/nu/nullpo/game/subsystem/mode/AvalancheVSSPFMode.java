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

/**
 * AVALANCHE-SPF VS-BATTLE mode (Release Candidate 1)
 */
public class AvalancheVSSPFMode extends DummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 0;

	/** Enabled piece types */
	private static final int[] PIECE_ENABLE = {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0};

	/** Block colors */
	private static final int[] BLOCK_COLORS =
	{
		Block.BLOCK_COLOR_RED,
		Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_BLUE,
		Block.BLOCK_COLOR_YELLOW
	};

	/** Fever map files list */
	private static final String[] FEVER_MAPS =
	{
		"Fever", "15th", "15thDS", "7", "Compendium"
	};

	/** Chain multipliers */
	private static final int[] CHAIN_POWERS = {
		4, 12, 24, 33, 50, 101, 169, 254, 341, 428, 538, 648, 763, 876, 990, 999 //Arle
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

	/** Number of players */
	private static final int MAX_PLAYERS = 2;

	/** Ojama counter setting constants */
	private static final int OJAMA_COUNTER_OFF = 0, OJAMA_COUNTER_ON = 1, OJAMA_COUNTER_FEVER = 2;

	/** Names of ojama counter settings */
	private static final String[] OJAMA_COUNTER_STRING = {"OFF", "ON", "FEVER"};

	/** Zenkeshi setting constants */
	private static final int /*ZENKESHI_MODE_OFF = 0,*/ ZENKESHI_MODE_ON = 1, ZENKESHI_MODE_FEVER = 2;

	/** Names of zenkeshi settings */
	private static final String[] ZENKESHI_TYPE_NAMES = {"OFF", "ON", "FEVER"};

	/** Names of outline settings */
	private static final String[] OUTLINE_TYPE_NAMES = {"NORMAL", "COLOR", "NONE"};

	/** Names of chain display settings */
	private static final String[] CHAIN_DISPLAY_NAMES = {"OFF", "YELLOW", "PLAYER", "SIZE"};

	/** Constants for chain display settings */
	private static final int CHAIN_DISPLAY_NONE = 0, /*CHAIN_DISPLAY_YELLOW = 1,*/
		CHAIN_DISPLAY_PLAYER = 2, CHAIN_DISPLAY_SIZE = 3;

	/** Each player's frame color */
	private static final int[] PLAYER_COLOR_FRAME = {GameEngine.FRAME_COLOR_RED, GameEngine.FRAME_COLOR_BLUE};

	/** GameManager that owns this mode */
	private GameManager owner;

	/** Drawing and event handling EventReceiver */
	private EventReceiver receiver;

	/** Rule settings for countering ojama not yet dropped */
	private int[] ojamaCounterMode;

	/** 溜まっている邪魔Blockのcount */
	private int[] ojama;

	/** 送った邪魔Blockのcount */
	private int[] ojamaSent;

	/** Time to display the most recent increase in score */
	private int[] scgettime;

	/** 使用するBGM */
	private int bgmno;

	/** Big */
	private boolean[] big;

	/** 効果音ON/OFF */
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

	/** 最大マップ number */
	private int[] mapMaxNo;

	/** バックアップ用フィールド（マップをリプレイに保存するときに使用） */
	private Field[] fldBackup;

	/** マップ選択用乱count */
	private Random randMap;

	/** Version */
	private int version;

	/** Flag for all clear */
	private boolean[] zenKeshi;

	/** Amount of points earned from most recent clear */
	private int[] lastscore, lastmultiplier;

	/** Amount of ojama added in current chain */
	private int[] ojamaAdd;

	/** Score */
	private int[] score;

	/** Max amount of ojama dropped at once */
	private int[] maxAttack;

	/** Minimum chain count needed to send ojama */
	private int[] rensaShibari;

	/** Denominator for score-to-ojama conversion */
	private int[] ojamaRate;

	/** Hurryup開始までの秒count(0でHurryupなし) */
	private int[] hurryupSeconds;

	/** Set to true when last drop resulted in a clear */
	private boolean[] cleared;

	/** Set to true when dropping ojama blocks */
	private boolean[] ojamaDrop;

	/** Selected fever map set file */
	private int[] feverMapSet;

	/** Selected fever map set file's subset list */
	private String[][] feverMapSubsets;

	/** Time to display "ZENKESHI!" */
	private int[] zenKeshiDisplay;

	/** Zenkeshi reward type */
	private int[] zenKeshiType;

	/** Fever map CustomProperties */
	private CustomProperties[] propFeverMap;

	/** Selected outline type */
	private int[] outlineType;

	/** If true, both columns 3 and 4 are danger columns */
	private boolean[] dangerColumnDouble;

	/** If true, red X's appear at tops of danger columns */
	private boolean[] dangerColumnShowX;

	/** Last chain hit number */
	private int[] chain;

	/** Time to display last chain */
	private int[] chainDisplay;

	/** Type of chain display */
	private int[] chainDisplayType;
	
	/** True to use new (Fever) chain powers */
	private boolean[] newChainPower;

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
	
	/** True to use slower falling animations, false to use faster */
	private boolean[] cascadeSlow;

	/*
	 * Mode  name
	 */
	@Override
	public String getName() {
		return "AVALANCHE-SPF VS-BATTLE (BETA)";
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

		ojamaCounterMode = new int[MAX_PLAYERS];
		ojama = new int[MAX_PLAYERS];
		ojamaSent = new int[MAX_PLAYERS];

		scgettime = new int[MAX_PLAYERS];
		bgmno = 0;
		big = new boolean[MAX_PLAYERS];
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

		zenKeshi = new boolean[MAX_PLAYERS];
		lastscore = new int[MAX_PLAYERS];
		lastmultiplier = new int[MAX_PLAYERS];
		ojamaAdd = new int[MAX_PLAYERS];
		score = new int[MAX_PLAYERS];
		maxAttack = new int[MAX_PLAYERS];
		rensaShibari = new int[MAX_PLAYERS];
		ojamaRate = new int[MAX_PLAYERS];
		ojamaCountdown = new int[MAX_PLAYERS];

		cleared = new boolean[MAX_PLAYERS];
		ojamaDrop = new boolean[MAX_PLAYERS];
		feverMapSet = new int[MAX_PLAYERS];
		zenKeshiDisplay = new int[MAX_PLAYERS];
		zenKeshiType = new int[MAX_PLAYERS];
		propFeverMap = new CustomProperties[MAX_PLAYERS];
		feverMapSubsets = new String[MAX_PLAYERS][];
		outlineType = new int[MAX_PLAYERS];
		dangerColumnDouble = new boolean[MAX_PLAYERS];
		dangerColumnShowX = new boolean[MAX_PLAYERS];
		chain = new int[MAX_PLAYERS];
		chainDisplay = new int[MAX_PLAYERS];
		chainDisplayType = new int[MAX_PLAYERS];
		newChainPower = new boolean[MAX_PLAYERS];

		dropSet = new int[MAX_PLAYERS];
		dropMap = new int[MAX_PLAYERS];
		dropPattern = new int[MAX_PLAYERS][][];
		attackMultiplier = new double[MAX_PLAYERS];
		defendMultiplier = new double[MAX_PLAYERS];
		countdownDecremented = new boolean[MAX_PLAYERS];
		cascadeSlow = new boolean[MAX_PLAYERS];

		winnerID = -1;
	}

	/**
	 * Read speed presets
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 * @param preset Preset number
	 */
	private void loadPreset(GameEngine engine, CustomProperties prop, int preset) {
		engine.speed.gravity = prop.getProperty("avalanchevsspf.gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("avalanchevsspf.denominator." + preset, 256);
		engine.speed.are = prop.getProperty("avalanchevsspf.are." + preset, 30);
		engine.speed.areLine = prop.getProperty("avalanchevsspf.areLine." + preset, 30);
		engine.speed.lineDelay = prop.getProperty("avalanchevsspf.lineDelay." + preset, 10);
		engine.speed.lockDelay = prop.getProperty("avalanchevsspf.lockDelay." + preset, 60);
		engine.speed.das = prop.getProperty("avalanchevsspf.das." + preset, 14);
		engine.cascadeDelay = prop.getProperty("avalanchevsspf.fallDelay." + preset, 1);
		engine.cascadeClearDelay = prop.getProperty("avalanchevsspf.clearDelay." + preset, 10);
	}

	/**
	 * Save speed presets
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 * @param preset Preset number
	 */
	private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
		prop.setProperty("avalanchevsspf.gravity." + preset, engine.speed.gravity);
		prop.setProperty("avalanchevsspf.denominator." + preset, engine.speed.denominator);
		prop.setProperty("avalanchevsspf.are." + preset, engine.speed.are);
		prop.setProperty("avalanchevsspf.areLine." + preset, engine.speed.areLine);
		prop.setProperty("avalanchevsspf.lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("avalanchevsspf.lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("avalanchevsspf.das." + preset, engine.speed.das);
		prop.setProperty("avalanchevsspf.fallDelay." + preset, engine.cascadeDelay);
		prop.setProperty("avalanchevsspf.clearDelay." + preset, engine.cascadeClearDelay);
	}

	/**
	 * スピード以外の設定を読み込み
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 */
	private void loadOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		bgmno = prop.getProperty("avalanchevsspf.bgmno", 0);
		ojamaCounterMode[playerID] = prop.getProperty("avalanchevsspf.ojamaCounterMode", OJAMA_COUNTER_ON);
		big[playerID] = prop.getProperty("avalanchevsspf.big.p" + playerID, false);
		enableSE[playerID] = prop.getProperty("avalanchevsspf.enableSE.p" + playerID, true);
		hurryupSeconds[playerID] = prop.getProperty("avalanchevsspf.hurryupSeconds.p" + playerID, 192);
		useMap[playerID] = prop.getProperty("avalanchevsspf.useMap.p" + playerID, false);
		mapSet[playerID] = prop.getProperty("avalanchevsspf.mapSet.p" + playerID, 0);
		mapNumber[playerID] = prop.getProperty("avalanchevsspf.mapNumber.p" + playerID, -1);
		presetNumber[playerID] = prop.getProperty("avalanchevsspf.presetNumber.p" + playerID, 0);
		maxAttack[playerID] = prop.getProperty("avalanchevsspf.maxAttack.p" + playerID, 30);
		rensaShibari[playerID] = prop.getProperty("avalanchevsspf.rensaShibari.p" + playerID, 1);
		ojamaRate[playerID] = prop.getProperty("avalanchevsspf.ojamaRate.p" + playerID, 120);
		feverMapSet[playerID] = prop.getProperty("avalanchevsspf.feverMapSet.p" + playerID, 0);
		zenKeshiType[playerID] = prop.getProperty("avalanchevsspf.zenKeshiType.p" + playerID, 1);
		outlineType[playerID] = prop.getProperty("avalanchevsspf.outlineType.p" + playerID, 1);
		dangerColumnDouble[playerID] = prop.getProperty("avalanchevsspf.dangerColumnDouble.p" + playerID, false);
		dangerColumnShowX[playerID] = prop.getProperty("avalanchevsspf.dangerColumnShowX.p" + playerID, false);
		chainDisplayType[playerID] = prop.getProperty("avalanchevsspf.chainDisplayType.p" + playerID, 1);
		newChainPower[playerID] = prop.getProperty("avalanchevsspf.newChainPower.p" + playerID, false);
		ojamaCountdown[playerID] = prop.getProperty("avalanchevsspf.ojamaCountdown.p" + playerID, 3);
		dropSet[playerID] = prop.getProperty("avalanchevsspf.dropSet.p" + playerID, 4);
		dropMap[playerID] = prop.getProperty("avalanchevsspf.dropMap.p" + playerID, 0);
		cascadeSlow[playerID] = prop.getProperty("avalanchevsspf.cascadeSlow.p" + playerID, false);
	}

	/**
	 * スピード以外の設定を保存
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 */
	private void saveOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		prop.setProperty("avalanchevsspf.bgmno", bgmno);
		prop.setProperty("avalanchevsspf.ojamaCounterMode", ojamaCounterMode[playerID]);
		prop.setProperty("avalanchevsspf.big.p" + playerID, big[playerID]);
		prop.setProperty("avalanchevsspf.enableSE.p" + playerID, enableSE[playerID]);
		prop.setProperty("avalanchevsspf.hurryupSeconds.p" + playerID, hurryupSeconds[playerID]);
		prop.setProperty("avalanchevsspf.useMap.p" + playerID, useMap[playerID]);
		prop.setProperty("avalanchevsspf.mapSet.p" + playerID, mapSet[playerID]);
		prop.setProperty("avalanchevsspf.mapNumber.p" + playerID, mapNumber[playerID]);
		prop.setProperty("avalanchevsspf.presetNumber.p" + playerID, presetNumber[playerID]);
		prop.setProperty("avalanchevsspf.maxAttack.p" + playerID, maxAttack[playerID]);
		prop.setProperty("avalanchevsspf.rensaShibari.p" + playerID, rensaShibari[playerID]);
		prop.setProperty("avalanchevsspf.ojamaRate.p" + playerID, ojamaRate[playerID]);
		prop.setProperty("avalanchevsspf.feverMapSet.p" + playerID, feverMapSet[playerID]);
		prop.setProperty("avalanchevsspf.zenKeshiType.p" + playerID, zenKeshiType[playerID]);
		prop.setProperty("avalanchevsspf.outlineType.p" + playerID, outlineType[playerID]);
		prop.setProperty("avalanchevsspf.dangerColumnDouble.p" + playerID, dangerColumnDouble[playerID]);
		prop.setProperty("avalanchevsspf.dangerColumnShowX.p" + playerID, dangerColumnShowX[playerID]);
		prop.setProperty("avalanchevsspf.chainDisplayType.p" + playerID, chainDisplayType[playerID]);
		prop.setProperty("avalanchevsspf.newChainPower.p" + playerID, newChainPower[playerID]);
		prop.setProperty("avalanchevsspf.ojamaCountdown.p" + playerID, ojamaCountdown[playerID]);
		prop.setProperty("avalanchevsspf.dropSet.p" + playerID, dropSet[playerID]);
		prop.setProperty("avalanchevsspf.dropMap.p" + playerID, dropMap[playerID]);
		prop.setProperty("avalanchevsspf.cascadeSlow.p" + playerID, cascadeSlow[playerID]);
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

	/**
	 * プレビュー用にマップを読み込み
	 * @param engine GameEngine
	 * @param playerID プレイヤー number
	 * @param id マップID
	 * @param forceReload trueにするとマップファイルを強制再読み込み
	 */
	private void loadMapPreview(GameEngine engine, int playerID, int id, boolean forceReload) {
		if((propMap[playerID] == null) || (forceReload)) {
			mapMaxNo[playerID] = 0;
			propMap[playerID] = receiver.loadProperties("config/map/avalanche/" + mapSet[playerID] + ".map");
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
		engine.clearMode = GameEngine.CLEAR_COLOR;
		engine.garbageColorClear = true;
		engine.lineGravityType = GameEngine.LINE_GRAVITY_CASCADE;
		for(int i = 0; i < Piece.PIECE_COUNT; i++)
			engine.nextPieceEnable[i] = (PIECE_ENABLE[i] == 1);
		engine.blockColors = BLOCK_COLORS;
		engine.randomBlockColor = true;
		engine.connectBlocks = false;

		ojama[playerID] = 0;
		ojamaAdd[playerID] = 0;
		ojamaSent[playerID] = 0;
		score[playerID] = 0;
		zenKeshi[playerID] = false;
		scgettime[playerID] = 0;
		cleared[playerID] = false;
		ojamaDrop[playerID] = false;
		zenKeshiDisplay[playerID] = 0;
		chain[playerID] = 0;
		chainDisplay[playerID] = 0;
		countdownDecremented[playerID] = true;

		if(engine.owner.replayMode == false) {
			loadOtherSetting(engine, engine.owner.modeConfig);
			loadPreset(engine, engine.owner.modeConfig, -1 - playerID);
			version = CURRENT_VERSION;
		} else {
			loadOtherSetting(engine, engine.owner.replayProp);
			loadPreset(engine, engine.owner.replayProp, -1 - playerID);
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
					engine.statc[2] = 32;
					loadDropMapPreview(engine, playerID, DROP_PATTERNS[dropSet[playerID]][dropMap[playerID]]);
				}
				else if (engine.statc[2] == 30)
					engine.field = null;
				engine.playSE("cursor");
			}
			// Down
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
				engine.statc[2]++;
				if(engine.statc[2] > 32) {
					engine.statc[2] = 0;
					engine.field = null;
				}
				else if (engine.statc[2] == 31)
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
					if (m >= 10) ojamaRate[playerID] += change*100;
					else ojamaRate[playerID] += change*10;
					if(ojamaRate[playerID] < 10) ojamaRate[playerID] = 1000;
					if(ojamaRate[playerID] > 1000) ojamaRate[playerID] = 10;
					break;
				case 13:
					if (m > 10) hurryupSeconds[playerID] += change*m/10;
					else hurryupSeconds[playerID] += change;
					if(hurryupSeconds[playerID] < 0) hurryupSeconds[playerID] = 300;
					if(hurryupSeconds[playerID] > 300) hurryupSeconds[playerID] = 0;
					break;
				case 14:
					dangerColumnDouble[playerID] = !dangerColumnDouble[playerID];
					break;
				case 15:
					dangerColumnShowX[playerID] = !dangerColumnShowX[playerID];
					break;
				case 16:
					ojamaCountdown[playerID] += change;
					if(ojamaCountdown[playerID] < 1) ojamaCountdown[playerID] = 9;
					if(ojamaCountdown[playerID] > 9) ojamaCountdown[playerID] = 1;
					break;
				case 17:
					zenKeshiType[playerID] += change;
					if(zenKeshiType[playerID] < 0) zenKeshiType[playerID] = 2;
					if(zenKeshiType[playerID] > 2) zenKeshiType[playerID] = 0;
					break;
				case 18:
					feverMapSet[playerID] += change;
					if(feverMapSet[playerID] < 0) feverMapSet[playerID] = FEVER_MAPS.length-1;
					if(feverMapSet[playerID] >= FEVER_MAPS.length) feverMapSet[playerID] = 0;
					break;
				case 19:
					outlineType[playerID] += change;
					if(outlineType[playerID] < 0) outlineType[playerID] = 2;
					if(outlineType[playerID] > 2) outlineType[playerID] = 0;
					break;
				case 20:
					chainDisplayType[playerID] += change;
					if(chainDisplayType[playerID] < 0) chainDisplayType[playerID] = 3;
					if(chainDisplayType[playerID] > 3) chainDisplayType[playerID] = 0;
					break;
				case 21:
					cascadeSlow[playerID] = !cascadeSlow[playerID];
					break;
				case 22:
					newChainPower[playerID] = !newChainPower[playerID];
					break;
				case 23:
					useMap[playerID] = !useMap[playerID];
					if(!useMap[playerID]) {
						if(engine.field != null) engine.field.reset();
					} else {
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 24:
					mapSet[playerID] += change;
					if(mapSet[playerID] < 0) mapSet[playerID] = 99;
					if(mapSet[playerID] > 99) mapSet[playerID] = 0;
					if(useMap[playerID]) {
						mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 25:
					if(useMap[playerID]) {
						mapNumber[playerID] += change;
						if(mapNumber[playerID] < -1) mapNumber[playerID] = mapMaxNo[playerID] - 1;
						if(mapNumber[playerID] > mapMaxNo[playerID] - 1) mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					} else {
						mapNumber[playerID] = -1;
					}
					break;
				case 26:
					//big[playerID] = !big[playerID];
					big[playerID] = false;
					break;
				case 27:
					bgmno += change;
					if(bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
					if(bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
					break;
				case 28:
					enableSE[playerID] = !enableSE[playerID];
					break;
				case 29:
				case 30:
					presetNumber[playerID] += change;
					if(presetNumber[playerID] < 0) presetNumber[playerID] = 99;
					if(presetNumber[playerID] > 99) presetNumber[playerID] = 0;
					break;
				case 31:
					dropSet[playerID] += change;
					if(dropSet[playerID] < 0) dropSet[playerID] = DROP_PATTERNS.length-1;
					if(dropSet[playerID] >= DROP_PATTERNS.length) dropSet[playerID] = 0;
					if(dropMap[playerID] >= DROP_PATTERNS[dropSet[playerID]].length) dropMap[playerID] = 0;
					loadDropMapPreview(engine, playerID, DROP_PATTERNS[dropSet[playerID]][dropMap[playerID]]);
					break;
				case 32:
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

				if(engine.statc[2] == 36) {
					loadPreset(engine, owner.modeConfig, presetNumber[playerID]);
				} else if(engine.statc[2] == 37) {
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

			if(engine.statc[3] >= 300)
				engine.statc[4] = 1;
			else if(engine.statc[3] > 240)
				engine.statc[2] = 17;
			else if (engine.statc[3] == 240)
			{
				engine.statc[2] = 32;
				loadDropMapPreview(engine, playerID, DROP_PATTERNS[dropSet[playerID]][dropMap[playerID]]);
			}
			else if(engine.statc[3] >= 180)
				engine.statc[2] = 24;
			else if(engine.statc[3] >= 120)
				engine.statc[2] = 18;
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

	private void loadMapSetFever(GameEngine engine, int playerID, int id, boolean forceReload) {
		if((propFeverMap[playerID] == null) || (forceReload)) {
			propFeverMap[playerID] = receiver.loadProperties("config/map/avalanche/" +
					FEVER_MAPS[id] + ".map");
			String subsets = propFeverMap[playerID].getProperty("sets");
			feverMapSubsets[playerID] = subsets.split(",");
		}
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
			} else if(engine.statc[2] < 16) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_CYAN, 9,
						"COUNTER", OJAMA_COUNTER_STRING[ojamaCounterMode[playerID]],
						"MAX ATTACK", String.valueOf(maxAttack[playerID]),
						"MIN CHAIN", String.valueOf(rensaShibari[playerID]),
						"OJAMA RATE", String.valueOf(ojamaRate[playerID]),
						"HURRYUP", (hurryupSeconds[playerID] == 0) ? "NONE" : hurryupSeconds[playerID]+"SEC",
						"X COLUMN", dangerColumnDouble[playerID] ? "3 AND 4" : "3 ONLY",
						"X SHOW", GeneralUtil.getONorOFF(dangerColumnShowX[playerID]));
				
				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 2/5", EventReceiver.COLOR_YELLOW);
			} else if(engine.statc[2] < 23) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_CYAN, 16,
						"COUNTDOWN", String.valueOf(ojamaCountdown[playerID]),
						"ZENKESHI", ZENKESHI_TYPE_NAMES[zenKeshiType[playerID]]);
				drawMenu(engine, playerID, receiver, 4, zenKeshiType[playerID] == ZENKESHI_MODE_FEVER ? 
							EventReceiver.COLOR_PURPLE : EventReceiver.COLOR_WHITE, 18,
						"F-MAP SET", FEVER_MAPS[feverMapSet[playerID]].toUpperCase());
				drawMenu(engine, playerID, receiver, 6, EventReceiver.COLOR_DARKBLUE, 19,
						"OUTLINE", OUTLINE_TYPE_NAMES[outlineType[playerID]],
						"SHOW CHAIN", CHAIN_DISPLAY_NAMES[chainDisplayType[playerID]],
						"FALL ANIM", cascadeSlow[playerID] ? "FEVER" : "CLASSIC");
				drawMenu(engine, playerID, receiver, 12, EventReceiver.COLOR_CYAN, 22,
						"CHAINPOWER", newChainPower[playerID] ? "FEVER" : "CLASSIC");
				
				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 3/5", EventReceiver.COLOR_YELLOW);
			} else if(engine.statc[2] < 31) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_PINK, 23,
						"USE MAP", GeneralUtil.getONorOFF(useMap[playerID]),
						"MAP SET", String.valueOf(mapSet[playerID]),
						"MAP NO.", (mapNumber[playerID] < 0) ? "RANDOM" : mapNumber[playerID]+"/"+(mapMaxNo[playerID]-1),
						"BIG", GeneralUtil.getONorOFF(big[playerID]));
				drawMenu(engine, playerID, receiver, 8, EventReceiver.COLOR_DARKBLUE, 27,
						"BGM", String.valueOf(bgmno),
						"SE", GeneralUtil.getONorOFF(enableSE[playerID]));
				drawMenu(engine, playerID, receiver, 12, EventReceiver.COLOR_GREEN, 29,
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
				
				drawMenu(engine, playerID, receiver, 14, EventReceiver.COLOR_CYAN, 31,
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
	 * Readyの時のInitialization処理（Initialization前）
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			engine.numColors = 4;
			engine.lineGravityType = cascadeSlow[playerID] ? GameEngine.LINE_GRAVITY_CASCADE_SLOW : GameEngine.LINE_GRAVITY_CASCADE;

			if(outlineType[playerID] == 0) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NORMAL;
			if(outlineType[playerID] == 1) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_SAMECOLOR;
			if(outlineType[playerID] == 2) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NONE;
			
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
						propMap[playerID] = receiver.loadProperties("config/map/avalanche/" + mapSet[playerID] + ".map");
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
			loadMapSetFever(engine, playerID, feverMapSet[playerID], true);
		}

		return false;
	}

	/*
	 * ゲーム開始時の処理
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		engine.big = big[playerID];
		engine.enableSE = enableSE[playerID];
		if(playerID == 1) owner.bgmStatus.bgm = bgmno;
		engine.colorClearSize = big[playerID] ? 12 : 4;
		engine.ignoreHidden = true;

		engine.tspinAllowKick = false;
		engine.tspinEnable = false;
		engine.useAllSpinBonus = false;
	}

	@Override
	public void renderLast(GameEngine engine, int playerID) {
		// ステータス表示
		if(playerID == 0) {
			receiver.drawScoreFont(engine, playerID, -1,  0, "AVALANCHE VS", EventReceiver.COLOR_GREEN);

			receiver.drawScoreFont(engine, playerID, -1,  2, "OJAMA", EventReceiver.COLOR_PURPLE);
			String ojamaFeverStr1P = String.valueOf(ojama[0]);
			if (ojamaAdd[0] > 0)
				ojamaFeverStr1P = ojamaFeverStr1P + "(+" + String.valueOf(ojamaAdd[0]) + ")";
			String ojamaFeverStr2P = String.valueOf(ojama[1]);
			if (ojamaAdd[1] > 0)
				ojamaFeverStr2P = ojamaFeverStr2P + "(+" + String.valueOf(ojamaAdd[1]) + ")";
			receiver.drawScoreFont(engine, playerID, -1,  3, "1P:", EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID,  3,  3, ojamaFeverStr1P, (ojama[0] > 0));
			receiver.drawScoreFont(engine, playerID, -1,  4, "2P:", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID,  3,  4, ojamaFeverStr2P, (ojama[1] > 0));

			receiver.drawScoreFont(engine, playerID, -1,  6, "ATTACK", EventReceiver.COLOR_GREEN);
			receiver.drawScoreFont(engine, playerID, -1,  7, "1P: " + String.valueOf(ojamaSent[0]), EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID, -1,  8, "2P: " + String.valueOf(ojamaSent[1]), EventReceiver.COLOR_BLUE);

			receiver.drawScoreFont(engine, playerID, -1, 10, "SCORE", EventReceiver.COLOR_PURPLE);
			receiver.drawScoreFont(engine, playerID, -1, 11, "1P: ", EventReceiver.COLOR_RED);
			if (scgettime[0] > 0 && lastscore[0] > 0 && lastmultiplier[0] > 0)
				receiver.drawScoreFont(engine, playerID, 3, 11, "+" + lastscore[0] + "X" + lastmultiplier[0], EventReceiver.COLOR_RED);
			else
				receiver.drawScoreFont(engine, playerID, 3, 11, String.valueOf(score[0]), EventReceiver.COLOR_RED);

			receiver.drawScoreFont(engine, playerID, -1, 12, "2P: ", EventReceiver.COLOR_BLUE);
			if (scgettime[1] > 0 && lastscore[1] > 0 && lastmultiplier[1] > 0)
				receiver.drawScoreFont(engine, playerID, 3, 12, "+" + lastscore[1] + "X" + lastmultiplier[1], EventReceiver.COLOR_BLUE);
			else
				receiver.drawScoreFont(engine, playerID, 3, 12, String.valueOf(score[1]), EventReceiver.COLOR_BLUE);

			receiver.drawScoreFont(engine, playerID, -1, 14, "TIME", EventReceiver.COLOR_GREEN);
			receiver.drawScoreFont(engine, playerID, -1, 15, GeneralUtil.getTime(engine.statistics.time));
		}

		if (!owner.engine[playerID].gameActive)
			return;
		int playerColor = (playerID == 0) ? EventReceiver.COLOR_RED : EventReceiver.COLOR_BLUE;
		if (dangerColumnShowX[playerID])
			receiver.drawMenuFont(engine, playerID, 2, 0, dangerColumnDouble[playerID] ? "XX" : "X", EventReceiver.COLOR_RED);

		int textHeight = 13;
		if (engine.field != null)
			textHeight = engine.field.getHeight()+1;
		if (chain[playerID] > 0 && chainDisplay[playerID] > 0 && chainDisplayType[playerID] != CHAIN_DISPLAY_NONE)
		{
			int color = EventReceiver.COLOR_YELLOW;
			if (chainDisplayType[playerID] == CHAIN_DISPLAY_PLAYER)
				color = playerColor;
			else if (chainDisplayType[playerID] == CHAIN_DISPLAY_SIZE)
				color = chain[playerID] >= rensaShibari[playerID] ? EventReceiver.COLOR_GREEN : EventReceiver.COLOR_RED;
			receiver.drawMenuFont(engine, playerID, chain[playerID] > 9 ? 0 : 1, textHeight, chain[playerID] + " CHAIN!", color);
		}
		if(zenKeshi[playerID] || zenKeshiDisplay[playerID] > 0)
			receiver.drawMenuFont(engine, playerID, 0, textHeight+1, "ZENKESHI!", EventReceiver.COLOR_YELLOW);
		
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
	/*
	 * Hard dropしたときの処理
	 */
	@Override
	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
		engine.statistics.score += fall;
	}

	/*
	 * Hard dropしたときの処理
	 */
	@Override
	public void afterSoftDropFall(GameEngine engine, int playerID, int fall) {
		engine.statistics.score += fall;
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int avalanche) {
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;

		
		if (big[playerID])
			avalanche >>= 2;
		// Line clear bonus
		int pts = avalanche*10;
		int ojamaNew = 0;
		if (avalanche > 0) {
			cleared[playerID] = true;
			if (zenKeshi[playerID] && zenKeshiType[playerID] == ZENKESHI_MODE_ON)
				ojamaNew += 30;
			if (engine.field.isEmpty()) {
				engine.playSE("bravo");
				zenKeshi[playerID] = true;
				engine.statistics.score += 2100;
				score[playerID] += 2100;
			}
			else
				zenKeshi[playerID] = false;

			chain[playerID] = engine.chain;
			chainDisplay[playerID] = 60;
			engine.playSE("combo" + Math.min(chain[playerID], 20));
			int multiplier = engine.field.colorClearExtraCount;
			if (big[playerID])
				multiplier >>= 2;
			if (engine.field.colorsCleared > 1)
				multiplier += (engine.field.colorsCleared-1)*2;
			/*
			if (multiplier < 0)
				multiplier = 0;
			if (chain == 0)
				firstExtra = avalanche > engine.colorClearSize;
			*/
			if (!newChainPower[playerID])
			{
				if (chain[playerID] == 2)
					multiplier += 8;
				else if (chain[playerID] == 3)
					multiplier += 16;
				else if (chain[playerID] >= 4)
					multiplier += 32*(chain[playerID]-3);
			}
			else
			{
				if (chain[playerID] > CHAIN_POWERS.length)
					multiplier += CHAIN_POWERS[CHAIN_POWERS.length-1];
				else
					multiplier += CHAIN_POWERS[chain[playerID]-1];
			}
			/*
			if (firstExtra)
				multiplier++;
			*/

			if (multiplier > 999)
				multiplier = 999;
			if (multiplier < 1)
				multiplier = 1;

			lastscore[playerID] = pts;
			lastmultiplier[playerID] = multiplier;
			scgettime[playerID] = 25;
			int ptsTotal = pts*multiplier;
			score[playerID] += ptsTotal;

			if (chain[playerID] >= rensaShibari[playerID])
			{
				//Add ojama
				int rate = ojamaRate[playerID];
				if (hurryupSeconds[playerID] > 0 && engine.statistics.time > hurryupSeconds[playerID])
					rate >>= engine.statistics.time / (hurryupSeconds[playerID] * 60);
				if (rate <= 0)
					rate = 1;
				ojamaNew += ((int) (ptsTotal * attackMultiplier[playerID] * defendMultiplier[enemyID])+rate-1)/rate;
				ojamaSent[playerID] += ojamaNew;

				if (ojamaCounterMode[playerID] != OJAMA_COUNTER_OFF)
				{
					if (ojama[playerID] > 0 && ojamaNew > 0)
					{
						int delta = Math.min(ojama[playerID], ojamaNew);
						ojama[playerID] -= delta;
						ojamaNew -= delta;
					}
					if (ojamaAdd[playerID] > 0 && ojamaNew > 0)
					{
						int delta = Math.min(ojamaAdd[playerID], ojamaNew);
						ojamaAdd[playerID] -= delta;
						ojamaNew -= delta;
					}
				}
				if (ojamaNew > 0)
					ojamaAdd[enemyID] += ojamaNew;
			}
		}
		else if (!engine.field.canCascade())
			cleared[playerID] = false;
	}

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
		
		//Turn cleared ojama into normal blocks
		boolean result = false;
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
					result = true;
				}
			}
		if (result)
			return true;
		//Decrement countdowns
		if (!countdownDecremented[playerID])
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

	private void loadFeverMap(GameEngine engine, int playerID, int chain) {
		engine.createFieldIfNeeded();
		engine.field.reset();
		engine.field.stringToField(propFeverMap[playerID].getProperty(
				feverMapSubsets[playerID][engine.random.nextInt(feverMapSubsets[playerID].length)] +
				"." + 4 + "colors." + chain + "chain"));
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_ANTIGRAVITY, false);
		engine.field.setAllSkin(engine.getSkin());
		engine.field.shuffleColors(BLOCK_COLORS, 4, engine.random);
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		if (scgettime[playerID] > 0)
			scgettime[playerID]--;
		if (zenKeshiDisplay[playerID] > 0)
			zenKeshiDisplay[playerID]--;
		if (chainDisplay[playerID] > 0)
			chainDisplay[playerID]--;

		int width = 6;
		if (engine.field != null)
			width = engine.field.getWidth()*6;
		int blockHeight = receiver.getBlockGraphicsHeight(engine, playerID);
		// せり上がりMeter
		int value = ojama[playerID] * blockHeight / width;
		if(ojama[playerID] >= 5*width) engine.meterColor = GameEngine.METER_COLOR_RED;
		else if(ojama[playerID] >= width) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		else if(ojama[playerID] >= 1) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		else engine.meterColor = GameEngine.METER_COLOR_GREEN;
		if (value > engine.meterValue)
			engine.meterValue++;
		else if (value < engine.meterValue)
			engine.meterValue--;

		// 決着
		if((playerID == 1) && (owner.engine[0].gameActive)) {
			boolean p1Lose = (owner.engine[0].stat == GameEngine.STAT_GAMEOVER);
			boolean p2Lose = (owner.engine[1].stat == GameEngine.STAT_GAMEOVER);
			if(p1Lose && p2Lose) {
				// 引き分け
				winnerID = -1;
				owner.engine[0].stat = GameEngine.STAT_GAMEOVER;
				owner.engine[1].stat = GameEngine.STAT_GAMEOVER;
			} else if(p2Lose && !p1Lose) {
				// 1P勝利
				winnerID = 0;
				owner.engine[0].stat = GameEngine.STAT_EXCELLENT;
				owner.engine[1].stat = GameEngine.STAT_GAMEOVER;
			} else if(p1Lose && !p2Lose) {
				// 2P勝利
				winnerID = 1;
				owner.engine[0].stat = GameEngine.STAT_GAMEOVER;
				owner.engine[1].stat = GameEngine.STAT_EXCELLENT;
			}
			if (p1Lose || p2Lose) {
				owner.engine[0].gameActive = false;
				owner.engine[1].gameActive = false;
				owner.engine[0].resetStatc();
				owner.engine[1].resetStatc();
				owner.engine[0].statc[1] = 1;
				owner.engine[1].statc[1] = 1;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			}
		}
	}

	@Override
	public boolean onMove (GameEngine engine, int playerID) {
		cleared[playerID] = false;
		ojamaDrop[playerID] = false;
		countdownDecremented[playerID] = false;
		return false;
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

		receiver.drawMenuFont(engine, playerID, 0, 3, "ATTACK", EventReceiver.COLOR_ORANGE);
		String strScore = String.format("%10d", ojamaSent[playerID]);
		receiver.drawMenuFont(engine, playerID, 0, 4, strScore);

		receiver.drawMenuFont(engine, playerID, 0, 5, "LINE", EventReceiver.COLOR_ORANGE);
		String strLines = String.format("%10d", engine.statistics.lines);
		receiver.drawMenuFont(engine, playerID, 0, 6, strLines);

		receiver.drawMenuFont(engine, playerID, 0, 7, "PIECE", EventReceiver.COLOR_ORANGE);
		String strPiece = String.format("%10d", engine.statistics.totalPieceLocked);
		receiver.drawMenuFont(engine, playerID, 0, 8, strPiece);

		receiver.drawMenuFont(engine, playerID, 0, 9, "ATTACK/MIN", EventReceiver.COLOR_ORANGE);
		float apm = (float)(ojamaSent[playerID] * 3600) / (float)(engine.statistics.time);
		String strAPM = String.format("%10g", apm);
		receiver.drawMenuFont(engine, playerID, 0, 10, strAPM);

		receiver.drawMenuFont(engine, playerID, 0, 11, "LINE/MIN", EventReceiver.COLOR_ORANGE);
		String strLPM = String.format("%10g", engine.statistics.lpm);
		receiver.drawMenuFont(engine, playerID, 0, 12, strLPM);

		receiver.drawMenuFont(engine, playerID, 0, 13, "PIECE/SEC", EventReceiver.COLOR_ORANGE);
		String strPPS = String.format("%10g", engine.statistics.pps);
		receiver.drawMenuFont(engine, playerID, 0, 14, strPPS);

		receiver.drawMenuFont(engine, playerID, 0, 15, "TIME", EventReceiver.COLOR_ORANGE);
		String strTime = String.format("%10s", GeneralUtil.getTime(owner.engine[0].statistics.time));
		receiver.drawMenuFont(engine, playerID, 0, 16, strTime);
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

		owner.replayProp.setProperty("avalanchevs.version", version);
	}
}
