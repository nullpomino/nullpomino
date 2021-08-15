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

import java.util.ArrayList;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.game.subsystem.mode.menu.AbstractMenuItem;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * Dummy implementation of game mode. Used as a base of most game modes.
 */
public abstract class AbstractMode implements GameMode {

	/** Total score */
	protected static enum Statistic { SCORE, LINES, TIME,
			LEVEL, LEVEL_MANIA, PIECE,
			MAXCOMBO, SPL, SPM, SPS,
			LPM, LPS, PPM, PPS,
			MAXCHAIN, LEVEL_ADD_DISP};

	/** GameManager that owns this mode */
	protected GameManager owner;

	/** Drawing and event handling EventReceiver */
	protected EventReceiver receiver;

	/** Current state of menu for drawMenu */
	protected int statcMenu, menuColor, menuY;

	protected ArrayList<AbstractMenuItem> menu;
	
	/** Name of mode in properties file */
	protected String propName;
	
	/** Position of cursor in menu */
	protected int menuCursor;
	
	/** Number of frames spent in menu */
	protected int menuTime;

	public AbstractMode() {
		statcMenu = 0;
		menuCursor = 0;
		menuTime = 0;
		menuColor = EventReceiver.COLOR_WHITE;
		menuY = 0;
		menu = new ArrayList<AbstractMenuItem>();
		propName = "dummy";
	}

	protected void loadSetting(CustomProperties prop) {
		for (AbstractMenuItem item : menu)
			item.load(-1, prop, propName);
	}

	protected void saveSetting(CustomProperties prop) {
		for (AbstractMenuItem item : menu)
			item.save(-1, prop, propName);
	}

	public void pieceLocked(GameEngine engine, int playerID, int lines) {
	}

	public boolean lineClearEnd(GameEngine engine, int playerID) {
		return false;
	}

	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
	}

	public void afterSoftDropFall(GameEngine engine, int playerID, int fall) {
	}

	public void blockBreak(GameEngine engine, int playerID, int x, int y, Block blk) {
	}

	public void calcScore(GameEngine engine, int playerID, int lines) {
	}

	public void fieldEditExit(GameEngine engine, int playerID) {}

	public String getName() {
		return "DUMMY";
	}

	public int getPlayers() {
		return 1;
	}

	public int getGameStyle() {
		return GameEngine.GAMESTYLE_TETROMINO;
	}

	public void loadReplay(GameEngine engine, int playerID, CustomProperties prop) {
	}

	public void modeInit(GameManager manager) {
	}

	public boolean onARE(GameEngine engine, int playerID) {
		return false;
	}

	public boolean onCustom(GameEngine engine, int playerID) {
		return false;
	}

	public boolean onEndingStart(GameEngine engine, int playerID) {
		return false;
	}

	public boolean onExcellent(GameEngine engine, int playerID) {
		return false;
	}

	public void onFirst(GameEngine engine, int playerID) {
	}

	public boolean onGameOver(GameEngine engine, int playerID) {
		return false;
	}

	public void onLast(GameEngine engine, int playerID) {
	}

	public boolean onLineClear(GameEngine engine, int playerID) {
		return false;
	}

	public boolean onLockFlash(GameEngine engine, int playerID) {
		return false;
	}

	public boolean onMove(GameEngine engine, int playerID) {
		return false;
	}

	public boolean onReady(GameEngine engine, int playerID) {
		return false;
	}

	public boolean onResult(GameEngine engine, int playerID) {
		return false;
	}

	public boolean onSetting(GameEngine engine, int playerID) {
		return false;
	}

	public boolean onFieldEdit(GameEngine engine, int playerID) {
		return false;
	}

	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;
	}

	public void resetCounters() {
		menuTime = 0;
	}
	
	public void renderARE(GameEngine engine, int playerID) {
	}

	public void renderCustom(GameEngine engine, int playerID) {
	}

	public void renderEndingStart(GameEngine engine, int playerID) {
	}

	public void renderExcellent(GameEngine engine, int playerID) {
	}

	public void renderFirst(GameEngine engine, int playerID) {
	}

	public void renderGameOver(GameEngine engine, int playerID) {
	}

	public void renderLast(GameEngine engine, int playerID) {
	}

	public void renderLineClear(GameEngine engine, int playerID) {
	}

	public void renderLockFlash(GameEngine engine, int playerID) {
	}

	public void renderMove(GameEngine engine, int playerID) {
	}

	public void renderReady(GameEngine engine, int playerID) {
	}

	public void renderResult(GameEngine engine, int playerID) {
	}

	public void renderSetting(GameEngine engine, int playerID) {
		//TODO: Custom page breaks
		AbstractMenuItem menuItem;
		int pageNum = menuCursor / 10;
		int pageStart = pageNum * 10;
		int endPage = Math.min(menu.size(), pageStart+10);
		for (int i = pageStart; i < endPage; i++)
		{
			menuItem = menu.get(i);
			receiver.drawMenuFont(engine, playerID, 0, i << 1, menuItem.displayName, menuItem.color);
			if (menuCursor == i && !engine.owner.replayMode)
				receiver.drawMenuFont(engine, playerID, 0, (i << 1) + 1, "b" + menuItem.getValueString(), true);
			else 
				receiver.drawMenuFont(engine, playerID, 1, (i << 1) + 1, menuItem.getValueString());
		}
	}

	public void renderFieldEdit(GameEngine engine, int playerID) {
	}

	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
	}

	public void startGame(GameEngine engine, int playerID) {
	}

	public boolean isNetplayMode() {
		return false;
	}

	public boolean isVSMode() {
		return false;
	}

	public void netplayInit(Object obj) {
	}

	public void netplayUnload(Object obj){
	}

	public void netplayOnRetryKey(GameEngine engine, int playerID) {
	}

	/**
	 * Update menu cursor
	 * @param engine GameEngine
	 * @param maxCursor Max value of cursor position
	 * @return -1 if Left key is pressed, 1 if Right key is pressed, 0 otherwise
	 */
	protected int updateCursor(GameEngine engine, int maxCursor) {
		return updateCursor(engine, maxCursor, 0);
	}

	/**
	 * Update menu cursor
	 * @param engine GameEngine
	 * @param maxCursor Max value of cursor position
	 * @param playerID Player ID (unused)
	 * @return -1 if Left key is pressed, 1 if Right key is pressed, 0 otherwise
	 */
	protected int updateCursor (GameEngine engine, int maxCursor, int playerID) {
		// Up
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
			menuCursor--;
			if(menuCursor < 0) menuCursor = maxCursor;
			engine.playSE("cursor");
		}
		// Down
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
			menuCursor++;
			if(menuCursor > maxCursor) menuCursor = 0;
			engine.playSE("cursor");
		}

		// Configuration changes
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT)) return -1;
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) return 1;
		return 0;
	}

	protected void updateMenu(GameEngine engine) {
		// Configuration changes
		int change = updateCursor(engine, menu.size()-1);

		if(change != 0) {
			engine.playSE("change");
			int fast = 0;
			if (engine.ctrl.isPush(Controller.BUTTON_E)) fast++;
			if (engine.ctrl.isPush(Controller.BUTTON_F)) fast += 2;
			menu.get(menuCursor).change(change, fast);
		}
	}

	protected void initMenu (int y, int color, int statc) {
		menuY = y;
		menuColor = color;
		statcMenu = statc;
	}

	protected void initMenu (int color, int statc) {
		menuY = 0;
		statcMenu = statc;
		menuColor = color;
	}

	protected void drawMenu (GameEngine engine, int playerID, EventReceiver receiver, String... str) {
		for (int i = 0; i < str.length; i++)
		{
			if ((i&1) == 0)
				receiver.drawMenuFont(engine, playerID, 0, menuY, str[i], menuColor);
			else if (menuCursor == statcMenu && !engine.owner.replayMode)
			{
				receiver.drawMenuFont(engine, playerID, 0, menuY, "b" + str[i], true);
				statcMenu++;
			}
			else {
				receiver.drawMenuFont(engine, playerID, 1, menuY, str[i]);
				statcMenu++;
			}
			menuY++;
		}
	}

	protected void drawMenu (GameEngine engine, int playerID, EventReceiver receiver,
			int y, int color, int statc, String... str) {
		menuY = y;
		menuColor = color;
		statcMenu = statc;
		drawMenu(engine, playerID, receiver, str);
	}

	protected void drawMenuCompact (GameEngine engine, int playerID, EventReceiver receiver, String... str) {
		for (int i = 0; i < str.length-1; i+= 2)
		{
			receiver.drawMenuFont(engine, playerID, 1, menuY, str[i] + ":", menuColor);
			if (menuCursor == statcMenu && !engine.owner.replayMode)
			{
				receiver.drawMenuFont(engine, playerID, 0, menuY, "b", true);
				receiver.drawMenuFont(engine, playerID, str[i].length()+2, menuY, str[i+1], true);
			}
			else
				receiver.drawMenuFont(engine, playerID, str[i].length()+2, menuY, str[i+1]);
			statcMenu++;
			menuY++;
		}
	}

	protected void drawMenuCompact (GameEngine engine, int playerID, EventReceiver receiver,
			int y, int color, int statc, String... str) {
		menuY = y;
		menuColor = color;
		statcMenu = statc;
		drawMenuCompact(engine, playerID, receiver, str);
	}

	protected void drawResult (GameEngine engine, int playerID, EventReceiver receiver, int y, int color, String... str) {
		drawResultScale(engine, playerID, receiver, y, color, 1.0f, str);
	}
	protected void drawResultScale (GameEngine engine, int playerID, EventReceiver receiver, int y, int color, float scale, String... str) {
		for (int i = 0; i < str.length; i++)
			receiver.drawMenuFont(engine, playerID, 0, y+i, str[i], ((i&1) == 0) ? color : EventReceiver.COLOR_WHITE, scale);
	}
	protected void drawResultRank (GameEngine engine, int playerID, EventReceiver receiver, int y, int color, int rank) {
		drawResultRankScale(engine, playerID, receiver, y, color, 1.0f, rank);
	}
	protected void drawResultRankScale (GameEngine engine, int playerID, EventReceiver receiver, int y, int color, float scale, int rank) {
		if(rank != -1) {
			receiver.drawMenuFont(engine, playerID, 0, y, "RANK", color, scale);
			receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", rank + 1), scale);
		}
	}
	protected void drawResultNetRank (GameEngine engine, int playerID, EventReceiver receiver, int y, int color, int rank) {
		drawResultNetRankScale(engine, playerID, receiver, y, color, 1.0f, rank);
	}
	protected void drawResultNetRankScale (GameEngine engine, int playerID, EventReceiver receiver, int y, int color, float scale, int rank) {
		if(rank != -1) {
			receiver.drawMenuFont(engine, playerID, 0, y, "NET-RANK", color, scale);
			receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", rank + 1), scale);
		}
	}
	protected void drawResultNetRankDaily(GameEngine engine, int playerID, EventReceiver receiver, int y, int color, int rank) {
		drawResultNetRankDailyScale(engine, playerID, receiver, y, color, 1.0f, rank);
	}
	protected void drawResultNetRankDailyScale(GameEngine engine, int playerID, EventReceiver receiver, int y, int color, float scale, int rank) {
		if(rank != -1) {
			receiver.drawMenuFont(engine, playerID, 0, y, "DAILY-RANK", color, scale);
			receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", rank + 1), scale);
		}
	}
	protected void drawResultStats (GameEngine engine, int playerID, EventReceiver receiver, int y, int color, Statistic ... stats) {
		drawResultStatsScale(engine, playerID, receiver, y, color, 1.0f, stats);
	}
	protected void drawResultStatsScale (GameEngine engine, int playerID, EventReceiver receiver, int y, int color, float scale, Statistic ... stats) {
		for (int i = 0; i < stats.length; i++)
		{
			switch(stats[i]) {
				case SCORE:
					receiver.drawMenuFont(engine, playerID, 0, y, "SCORE", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.score), scale);
					break;
				case LINES:
					receiver.drawMenuFont(engine, playerID, 0, y, "LINES", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.lines), scale);
					break;
				case TIME:
					receiver.drawMenuFont(engine, playerID, 0, y, "TIME", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10s", GeneralUtil.getTime(engine.statistics.time)), scale);
					break;
				case LEVEL:
					receiver.drawMenuFont(engine, playerID, 0, y, "LEVEL", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.level + 1), scale);
					break;
				case LEVEL_MANIA:
					receiver.drawMenuFont(engine, playerID, 0, y, "LEVEL", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.level), scale);
					break;
				case PIECE:
					receiver.drawMenuFont(engine, playerID, 0, y, "PIECE", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.totalPieceLocked), scale);
					break;
				case MAXCOMBO:
					receiver.drawMenuFont(engine, playerID, 0, y, "MAX COMBO", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.maxCombo - 1), scale);
					break;
				case SPL:
					receiver.drawMenuFont(engine, playerID, 0, y, "SCORE/LINE", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10g", engine.statistics.spl), scale);
					break;
				case SPM:
					receiver.drawMenuFont(engine, playerID, 0, y, "SCORE/MIN", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10g", engine.statistics.spm), scale);
					break;
				case SPS:
					receiver.drawMenuFont(engine, playerID, 0, y, "SCORE/SEC", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10g", engine.statistics.sps), scale);
					break;
				case LPM:
					receiver.drawMenuFont(engine, playerID, 0, y, "LINE/MIN", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10g", engine.statistics.lpm), scale);
					break;
				case LPS:
					receiver.drawMenuFont(engine, playerID, 0, y, "LINE/SEC", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10g", engine.statistics.lps), scale);
					break;
				case PPM:
					receiver.drawMenuFont(engine, playerID, 0, y, "PIECE/MIN", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10g", engine.statistics.ppm), scale);
					break;
				case PPS:
					receiver.drawMenuFont(engine, playerID, 0, y, "PIECE/SEC", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10g", engine.statistics.pps), scale);
					break;
				case MAXCHAIN:
					receiver.drawMenuFont(engine, playerID, 0, y, "MAX CHAIN", color, scale);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.maxChain), scale);
					break;
				case LEVEL_ADD_DISP:
					receiver.drawMenuFont(engine, playerID, 0, y, "LEVEL", color, scale);
					receiver.drawMenuFont(engine, playerID,0,y+1,String.format("%10d",engine.statistics.level+engine.statistics.levelDispAdd),scale);
					break;
			}
			y += 2;
		}
	}

	/**
	 * Default method to render controller input display
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderInput(GameEngine engine, int playerID) {
		EventReceiver receiver = engine.owner.receiver;
		int y = 24;
		if (isVSMode() && !isNetplayMode()) {
			int color = EventReceiver.COLOR_BLUE;
			if (playerID == 0) {
				color = EventReceiver.COLOR_RED;
				y--;
			}
			receiver.drawScoreFont(engine, 0, -9, y, (playerID+1) + "P INPUT:", color);
		} else {
			receiver.drawScoreFont(engine, 0, -6, y, "INPUT:", EventReceiver.COLOR_BLUE);
		}
		Controller ctrl = engine.ctrl;
		if (ctrl.isPress(Controller.BUTTON_LEFT)) receiver.drawScoreFont(engine, 0, 0, y, "<");
		if (ctrl.isPress(Controller.BUTTON_DOWN)) receiver.drawScoreFont(engine, 0, 1, y, "n");
		if (ctrl.isPress(Controller.BUTTON_UP)) receiver.drawScoreFont(engine, 0, 2, y, "k");
		if (ctrl.isPress(Controller.BUTTON_RIGHT)) receiver.drawScoreFont(engine, 0, 3, y, ">");
		if (ctrl.isPress(Controller.BUTTON_A)) receiver.drawScoreFont(engine, 0, 4, y, "A");
		if (ctrl.isPress(Controller.BUTTON_B)) receiver.drawScoreFont(engine, 0, 5, y, "B");
		if (ctrl.isPress(Controller.BUTTON_C)) receiver.drawScoreFont(engine, 0, 6, y, "C");
		if (ctrl.isPress(Controller.BUTTON_D)) receiver.drawScoreFont(engine, 0, 7, y, "D");
		if (ctrl.isPress(Controller.BUTTON_E)) receiver.drawScoreFont(engine, 0, 8, y, "E");
		if (ctrl.isPress(Controller.BUTTON_F)) receiver.drawScoreFont(engine, 0, 9, y, "F");
	}
}
