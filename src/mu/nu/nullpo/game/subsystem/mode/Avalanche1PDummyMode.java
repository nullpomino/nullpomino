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

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * AVALANCHE DUMMY Mode
 */
public abstract class Avalanche1PDummyMode extends DummyMode {
	/** Enabled piece types */
	public static final int[] PIECE_ENABLE = {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0};

	/** Enabled piece types */
	public static final int[] CHAIN_POWERS_FEVERTYPE =
	{
		4, 12, 24, 32, 48, 96, 160, 240, 320, 400, 500, 600, 700, 800, 900, 999
	};

	public int[] tableSpeedChangeLevel =
	{
		80, 90, 96, 97, Integer.MAX_VALUE
	};

	public int[] tableSpeedValue =
	{
		30, 45, 120, 480, -1
	};

	/** Block colors */
	public static final int[] BLOCK_COLORS =
	{
		Block.BLOCK_COLOR_RED,
		Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_BLUE,
		Block.BLOCK_COLOR_YELLOW,
		Block.BLOCK_COLOR_PURPLE
	};

	/** Fever map files list */
	public static final String[] FEVER_MAPS =
	{
		"Fever", "15th", "15thDS", "7", "Poochy7"
	};

	public static final int DAS = 10;

	/** GameManager object (Manages entire game status) */
	protected GameManager owner;

	/** EventReceiver object (This receives many game events, can also be used for drawing the fonts.) */
	protected EventReceiver receiver;

	/** Amount of points earned from most recent clear */
	protected int lastscore, lastmultiplier;

	/** Elapsed time from last line clear (lastscore is displayed to screen until this reaches to 120) */
	protected int scgettime;

	/** Outline type */
	protected int outlinetype;

	/** Flag for all clear */
	protected boolean zenKeshi;

	/** Amount of garbage sent */
	protected int garbageSent, garbageAdd;

	/** Number of colors to use */
	protected int numColors;

	/** Time to display last engine.chain */
	protected int chainDisplay;

	/** Number of all clears */
	protected int zenKeshiCount;

	/** Score before adding zenkeshi bonus and max engine.chain bonus */
	protected int scoreBeforeBonus;

	/** Zenkeshi bonus and max engine.chain bonus amounts */
	protected int zenKeshiBonus, maxChainBonus;

	/** Blocks cleared */
	protected int blocksCleared;

	/** Current level */
	protected int level;

	/** Maximum level */
	protected int maxLevel;

	/** Blocks cleared needed to reach next level */
	protected int toNextLevel;

	/** Blocks cleared needed to reach next level */
	protected int blocksPerLevel;

	/** True to use slower falling animations, false to use faster */
	protected boolean cascadeSlow;

	/** True to use big field display */
	protected boolean bigDisplay;

	/** 1 ojama is generated per this many points. */
	protected int ojamaRate;

	/** Index of current speed value in table */
	protected int speedIndex;

	public Avalanche1PDummyMode()
	{
		blocksPerLevel = 15;
		maxLevel = 99;
		ojamaRate = 120;
	}

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "AVALANCHE DUMMY";
	}

	/*
	 * Game style
	 */
	@Override
	public int getGameStyle() {
		return GameEngine.GAMESTYLE_AVALANCHE;
	}

	/*
	 * Initialization
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;
		lastscore = 0;
		lastmultiplier = 0;
		scgettime = 0;

		speedIndex = 0;
		outlinetype = 0;

		zenKeshi = false;
		garbageSent = 0;
		garbageAdd = 0;
		//firstExtra = false;

		zenKeshiCount = 0;
		engine.statistics.maxChain = 0;
		scoreBeforeBonus = 0;
		zenKeshiBonus = 0;
		maxChainBonus = 0;
		blocksCleared = 0;

		engine.chain = 0;
		chainDisplay = 0;
		level = 5;
		toNextLevel = blocksPerLevel;

		engine.framecolor = GameEngine.FRAME_COLOR_PURPLE;
		engine.clearMode = GameEngine.CLEAR_COLOR;
		engine.garbageColorClear = true;
		engine.colorClearSize = 4;
		engine.ignoreHidden = true;
		for(int i = 0; i < Piece.PIECE_COUNT; i++)
			engine.nextPieceEnable[i] = (PIECE_ENABLE[i] == 1);
		engine.randomBlockColor = true;
		engine.blockColors = BLOCK_COLORS;
		engine.connectBlocks = false;
		engine.cascadeDelay = 1;
		engine.cascadeClearDelay = 10;
		engine.dominoQuickTurn = true;
		/*
		engine.fieldWidth = 6;
		engine.fieldHeight = 12;
		engine.fieldHiddenHeight = 2;
		*/
	}

	/**
	 * Set the gravity rate
	 * @param engine GameEngine
	 */
	public void setSpeed(GameEngine engine) {
		if (level <= 40) {
			engine.speed.gravity = 1;
			engine.speed.denominator = Math.max(43 - level - ((level % 10) << 1), 2);
		} else {
			engine.speed.denominator = 60;
			while (level >= tableSpeedChangeLevel[speedIndex])
				speedIndex++;
			engine.speed.gravity = tableSpeedValue[speedIndex];
		}
	}

	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0)
			return readyInit(engine, playerID);
		return false;
	}

	protected boolean readyInit(GameEngine engine, int playerID) {
		engine.numColors = numColors;
		engine.lineGravityType = cascadeSlow ? GameEngine.LINE_GRAVITY_CASCADE_SLOW : GameEngine.LINE_GRAVITY_CASCADE;
		engine.displaysize = bigDisplay ? 1 : 0;

		if(outlinetype == 0) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NORMAL;
		else if(outlinetype == 1) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_SAMECOLOR;
		else if(outlinetype == 2) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NONE;

		if (numColors == 3) level = 1;
		else if (numColors == 4) level = 5;
		else if (numColors == 5) level = 10;
		toNextLevel = blocksPerLevel;

		zenKeshiCount = 0;
		engine.statistics.maxChain = 0;
		scoreBeforeBonus = 0;
		zenKeshiBonus = 0;
		maxChainBonus = 0;
		blocksCleared = 0;

		return false;
	}

	/*
	 * Called for initialization during "Ready" screen
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;

		engine.tspinEnable = false;
		engine.useAllSpinBonus = false;
		engine.tspinAllowKick = false;

		engine.speed.are = 30;
		engine.speed.areLine = 30;
		engine.speed.das = DAS;
		engine.speed.lockDelay = 60;

		setSpeed(engine);
	}

	@Override
	public boolean onARE(GameEngine engine, int playerID) {
		if (engine.dasCount > 0 && engine.dasCount < DAS)
			engine.dasCount = DAS;
		return false;
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		if (scgettime > 0)
			scgettime--;
		if (chainDisplay > 0)
			chainDisplay--;
	}

	/*
	 * game over
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0)
			addBonus(engine, playerID);
		return false;
	}

	protected void addBonus (GameEngine engine, int playerID) {
		scoreBeforeBonus = engine.statistics.score;
		if (numColors >= 5)
			zenKeshiBonus = zenKeshiCount*zenKeshiCount*1000;
		else if (numColors == 4)
			zenKeshiBonus = zenKeshiCount*(zenKeshiCount+1)*500;
		else
			zenKeshiBonus = zenKeshiCount*(zenKeshiCount+3)*250;
		maxChainBonus = engine.statistics.maxChain*engine.statistics.maxChain*2000;
		engine.statistics.score += zenKeshiBonus + maxChainBonus;
	}

	/*
	 * Called when hard drop used
	 */
	@Override
	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
		engine.statistics.score += fall;
	}

	/*
	 * Called when soft drop used
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

		if (avalanche > 0) {
			if (zenKeshi)
				garbageAdd += 30;
			if (engine.field.isEmpty()) {
				engine.playSE("bravo");
				zenKeshi = true;
				zenKeshiCount++;
				//engine.statistics.score += 2100;
			}
			else
				zenKeshi = false;

			onClear(engine, playerID);
			engine.playSE("combo" + Math.min(engine.chain, 20));

			int pts = calcPts(avalanche);

			int multiplier = engine.field.colorClearExtraCount;
			if (engine.field.colorsCleared > 1)
				multiplier += (engine.field.colorsCleared-1)*2;

			multiplier += calcChainMultiplier(engine.chain);

			if (multiplier > 999)
				multiplier = 999;
			if (multiplier < 1)
				multiplier = 1;

			blocksCleared += avalanche;
			toNextLevel -= avalanche;
			if (toNextLevel <= 0 && level < maxLevel)
			{
				toNextLevel = blocksPerLevel;
				level++;
			}

			lastscore = pts;
			lastmultiplier = multiplier;
			scgettime = 120;
			int score = pts*multiplier;
			engine.statistics.scoreFromLineClear += score;
			engine.statistics.score += score;

			garbageAdd += calcOjama(score, avalanche, pts, multiplier);

			setSpeed(engine);
		}
	}

	protected int calcOjama(int score, int avalanche, int pts, int multiplier)
	{
		return (score+ojamaRate-1)/ojamaRate;
	}

	protected int calcPts (int avalanche) {
		return avalanche*10;
	}

	protected int calcChainMultiplier(int chain) {
		if (chain == 2)
			return 8;
		else if (chain == 3)
			return 16;
		else if (chain >= 4)
			return 32*(chain-3);
		else
			return 0;
	}

	protected void onClear (GameEngine engine, int playerID) {
		chainDisplay = 60;
	}

	public boolean lineClearEnd(GameEngine engine, int playerID) {
		if (garbageAdd > 0)
		{
			garbageSent += garbageAdd;
			garbageAdd = 0;
		}
		return false;
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID,  0, 1, "PLAY DATA", EventReceiver.COLOR_ORANGE);

		receiver.drawMenuFont(engine, playerID,  0, 3, "SCORE", EventReceiver.COLOR_BLUE);
		String strScoreBefore = String.format("%10d", scoreBeforeBonus);
		receiver.drawMenuFont(engine, playerID,  0, 4, strScoreBefore, EventReceiver.COLOR_GREEN);

		receiver.drawMenuFont(engine, playerID,  0, 5, "ZENKESHI", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID,  0, 6, String.format("%10d", zenKeshiCount));
		String strZenKeshiBonus = "+" + zenKeshiBonus;
		receiver.drawMenuFont(engine, playerID, 10-strZenKeshiBonus.length(), 7, strZenKeshiBonus, EventReceiver.COLOR_GREEN);

		receiver.drawMenuFont(engine, playerID,  0, 8, "MAX CHAIN", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID,  0, 9, String.format("%10d", engine.statistics.maxChain));
		String strMaxChainBonus = "+" + maxChainBonus;
		receiver.drawMenuFont(engine, playerID, 10-strMaxChainBonus.length(), 10, strMaxChainBonus, EventReceiver.COLOR_GREEN);

		receiver.drawMenuFont(engine, playerID,  0, 11, "TOTAL", EventReceiver.COLOR_BLUE);
		String strScore = String.format("%10d", engine.statistics.score);
		receiver.drawMenuFont(engine, playerID,  0, 12, strScore, EventReceiver.COLOR_RED);

		receiver.drawMenuFont(engine, playerID,  0, 13, "TIME", EventReceiver.COLOR_BLUE);
		String strTime = String.format("%10s", GeneralUtil.getTime(engine.statistics.time));
		receiver.drawMenuFont(engine, playerID,  0, 14, strTime);
	}
}
