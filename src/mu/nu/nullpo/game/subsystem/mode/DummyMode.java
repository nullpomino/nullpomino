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
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * Dummy implementation of game mode. Used as a base of most game modes.
 */
public class DummyMode implements GameMode {

	/** Total score */
	protected static final int STAT_SCORE = 1, STAT_LINES = 2, STAT_TIME = 3,
			STAT_LEVEL = 4, STAT_LEVEL_MANIA = 5, STAT_PIECE = 6,
			STAT_MAXCOMBO = 7, STAT_SPL = 8, STAT_SPM = 9, STAT_SPS = 10,
			STAT_LPM = 11, STAT_LPS = 12, STAT_PPM = 13, STAT_PPS = 14,
			STAT_MAXCHAIN = 15, STAT_LEVEL_ADD_DISP = 16;
	
	/** Current state of menu for drawMenu */
	protected int statcMenu, menuColor, menuY;
	
	public DummyMode() {
		statcMenu = 0;
		menuColor = EventReceiver.COLOR_WHITE;
		menuY = 0;
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
			engine.statc[2]--;
			if(engine.statc[2] < 0) engine.statc[2] = maxCursor;
			engine.playSE("cursor");
		}
		// Down
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
			engine.statc[2]++;
			if(engine.statc[2] > maxCursor) engine.statc[2] = 0;
			engine.playSE("cursor");
		}

		// Configuration changes
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT)) return -1;
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) return 1;
		return 0;
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
			else if (engine.statc[2] == statcMenu && !engine.owner.replayMode)
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
		for (int i = 0; i < str.length; i++)
		{
			if ((i&1) == 0)
				receiver.drawMenuFont(engine, playerID, 0, y+i, str[i], color);
			else if (engine.statc[2] == statc && !engine.owner.replayMode)
			{
				receiver.drawMenuFont(engine, playerID, 0, y+i, "b" + str[i], true);
				statc++;
			}
			else {
				receiver.drawMenuFont(engine, playerID, 1, y+i, str[i]);
				statc++;
			}
		}
	}

	protected void drawResult (GameEngine engine, int playerID, EventReceiver receiver, int y, int color, String... str) {
		for (int i = 0; i < str.length; i++)
			receiver.drawMenuFont(engine, playerID, 0, y+i, str[i], ((i&1) == 0) ? color : EventReceiver.COLOR_WHITE);
	}
	protected void drawResultRank (GameEngine engine, int playerID, EventReceiver receiver, int y, int color, int rank) {
		if(rank != -1) {
			receiver.drawMenuFont(engine, playerID, 0, y, "RANK", color);
			receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", rank + 1));
		}
	}
	protected void drawResultNetRank (GameEngine engine, int playerID, EventReceiver receiver, int y, int color, int rank) {
		if(rank != -1) {
			receiver.drawMenuFont(engine, playerID, 0, y, "NET-RANK", color);
			receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", rank + 1));
		}
	}
	protected void drawResultNetRankDaily(GameEngine engine, int playerID, EventReceiver receiver, int y, int color, int rank) {
		if(rank != -1) {
			receiver.drawMenuFont(engine, playerID, 0, y, "DAILY-RANK", color);
			receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", rank + 1));
		}
	}
	protected void drawResultStats (GameEngine engine, int playerID, EventReceiver receiver, int y, int color, int... stats) {
		for (int i = 0; i < stats.length; i++)
		{
			switch(stats[i]) {
				case STAT_SCORE:
					receiver.drawMenuFont(engine, playerID, 0, y, "SCORE", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.score));
					break;
				case STAT_LINES:
					receiver.drawMenuFont(engine, playerID, 0, y, "LINES", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.lines));
					break;
				case STAT_TIME:
					receiver.drawMenuFont(engine, playerID, 0, y, "TIME", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10s", GeneralUtil.getTime(engine.statistics.time)));
					break;
				case STAT_LEVEL:
					receiver.drawMenuFont(engine, playerID, 0, y, "LEVEL", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.level + 1));
					break;
				case STAT_LEVEL_MANIA:
					receiver.drawMenuFont(engine, playerID, 0, y, "LEVEL", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.level));
					break;
				case STAT_PIECE:
					receiver.drawMenuFont(engine, playerID, 0, y, "PIECE", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.totalPieceLocked));
					break;
				case STAT_MAXCOMBO:
					receiver.drawMenuFont(engine, playerID, 0, y, "MAX COMBO", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.maxCombo - 1));
					break;
				case STAT_SPL:
					receiver.drawMenuFont(engine, playerID, 0, y, "SCORE/LINE", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10g", engine.statistics.spl));
					break;
				case STAT_SPM:
					receiver.drawMenuFont(engine, playerID, 0, y, "SCORE/MIN", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10g", engine.statistics.spm));
					break;
				case STAT_SPS:
					receiver.drawMenuFont(engine, playerID, 0, y, "SCORE/SEC", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10g", engine.statistics.sps));
					break;
				case STAT_LPM:
					receiver.drawMenuFont(engine, playerID, 0, y, "LINE/MIN", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10g", engine.statistics.lpm));
					break;
				case STAT_LPS:
					receiver.drawMenuFont(engine, playerID, 0, y, "LINE/SEC", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10g", engine.statistics.lps));
					break;
				case STAT_PPM:
					receiver.drawMenuFont(engine, playerID, 0, y, "PIECE/MIN", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10g", engine.statistics.ppm));
					break;
				case STAT_PPS:
					receiver.drawMenuFont(engine, playerID, 0, y, "PIECE/SEC", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10g", engine.statistics.pps));
					break;
				case STAT_MAXCHAIN:
					receiver.drawMenuFont(engine, playerID, 0, y, "MAX CHAIN", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.maxChain));
					break;
				case STAT_LEVEL_ADD_DISP:
					receiver.drawMenuFont(engine, playerID, 0, y, "LEVEL", color);
					receiver.drawMenuFont(engine, playerID, 0, y+1, String.format("%10d", engine.statistics.level + engine.statistics.levelDispAdd));
					break;
			}
			y += 2;
		}
	}
}
