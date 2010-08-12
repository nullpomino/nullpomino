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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import mu.nu.nullpo.game.component.Statistics;
import mu.nu.nullpo.gui.slick.NormalFont;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

import org.apache.log4j.Logger;

import sdljava.SDLException;
import sdljava.event.MouseState;
import sdljava.event.SDLEvent;
import sdljava.video.SDLSurface;

/**
 * リプレイ選択画面のステート
 */
public class StateReplaySelectSDL extends BaseStateSDL {
	/** Log */
	static Logger log = Logger.getLogger(StateReplaySelectSDL.class);

	/** 1画面に表示する最大ファイルcount */
	public static final int MAX_FILE_IN_ONE_PAGE = 20;

	/** リプレイ一覧 */
	protected String[] replaylist;

	/** Mode  name */
	protected String[] modenameList;

	/** Rule name */
	protected String[] rulenameList;

	/** Scoreなどの情報 */
	protected Statistics[] statsList;

	/** カーソル位置 */
	protected int cursor = 0;

	/** ID number of file at top of currently displayed section */
	protected int minfile = 0;

	/*
	 * このステートに入ったときの処理
	 */
	@Override
	public void enter() throws SDLException {
		replaylist = getReplayFileList();
		setReplayRuleAndModeList();
	}

	/**
	 * リプレイファイル一覧を取得
	 * @return リプレイファイルのファイル名の配列。ディレクトリがないならnull
	 */
	protected String[] getReplayFileList() {
		File dir = new File(NullpoMinoSDL.propGlobal.getProperty("custom.replay.directory", "replay"));

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir1, String name) {
				return name.endsWith(".rep");
			}
		};

		String[] list = dir.list(filter);

		return list;
	}

	/**
	 * リプレイの詳細を設定
	 */
	protected void setReplayRuleAndModeList() {
		if(replaylist == null) return;

		modenameList = new String[replaylist.length];
		rulenameList = new String[replaylist.length];
		statsList = new Statistics[replaylist.length];

		for(int i = 0; i < replaylist.length; i++) {
			CustomProperties prop = new CustomProperties();

			try {
				FileInputStream in = new FileInputStream(NullpoMinoSDL.propGlobal.getProperty("custom.replay.directory", "replay") + "/" + replaylist[i]);
				prop.load(in);
				in.close();
			} catch (IOException e) {
				log.warn("Failed to load replay file from " + replaylist[i], e);
			}

			modenameList[i] = prop.getProperty("name.mode", "");
			rulenameList[i] = prop.getProperty("name.rule", "");

			statsList[i] = new Statistics();
			statsList[i].readProperty(prop, 0);
		}
	}

	/*
	 * 画面描画
	 */
	@Override
	public void render(SDLSurface screen) throws SDLException {
		ResourceHolderSDL.imgMenu.blitSurface(screen);

		if(replaylist == null) {
			NormalFontSDL.printFontGrid(1, 1, "REPLAY DIRECTORY NOT FOUND", NormalFontSDL.COLOR_RED);
		} else if(replaylist.length == 0) {
			NormalFontSDL.printFontGrid(1, 1, "NO REPLAY FILE", NormalFontSDL.COLOR_RED);
		} else {
			if (cursor >= replaylist.length)
				cursor = 0;
			
			if (cursor < minfile)
				minfile = cursor;
			int maxfile = minfile + MAX_FILE_IN_ONE_PAGE - 1;
			if (cursor >= maxfile)
			{
				maxfile = cursor;
				minfile = maxfile - MAX_FILE_IN_ONE_PAGE + 1;
			}
			if (maxfile >= replaylist.length)
				maxfile = replaylist.length-1;
				
			String title = "SELECT REPLAY FILE";
			title += " (" + (cursor + 1) + "/" + (replaylist.length) + ")";

			NormalFontSDL.printFontGrid(1, 1, title, NormalFontSDL.COLOR_ORANGE);

			for(int i = minfile, y = 0; i <= maxfile; i++, y++) {
				if(i < replaylist.length) {
					NormalFontSDL.printFontGrid(2, 3 + y, replaylist[i].toUpperCase(), (cursor == i));
					if(cursor == i) NormalFontSDL.printFontGrid(1, 3 + y, "b", NormalFontSDL.COLOR_RED);
				}
			}

			NormalFontSDL.printFontGrid(1, 24, "MODE:" + modenameList[cursor] + " RULE:" + rulenameList[cursor], NormalFontSDL.COLOR_CYAN);
			NormalFontSDL.printFontGrid(1, 25,
										"SCORE:" + statsList[cursor].score + " LINE:" + statsList[cursor].lines
										, NormalFontSDL.COLOR_CYAN);
			NormalFontSDL.printFontGrid(1, 26,
										"LEVEL:" + (statsList[cursor].level + statsList[cursor].levelDispAdd) +
										" TIME:" + GeneralUtil.getTime(statsList[cursor].time)
										, NormalFontSDL.COLOR_CYAN);
			/*
			NormalFontSDL.printFontGrid(1, 27,
										"GAME RATE:" + ( (statsList[cursor].gamerate == 0f) ? "UNKNOWN" : ((100*statsList[cursor].gamerate) + "%") )
										, NormalFontSDL.COLOR_CYAN);
			*/
		}
	}

	/*
	 * Update game state
	 */
	@Override
	public void update() throws SDLException {
		if((replaylist != null) && (replaylist.length > 0)) {
			// Mouse
			int mouseOldY = MouseInputSDL.mouseInput.getMouseY();
			
			MouseInputSDL.mouseInput.isMouseClicked();
			
			if (mouseOldY != MouseInputSDL.mouseInput.getMouseY()) {
				int oldcursor=cursor;
				if (cursor<MAX_FILE_IN_ONE_PAGE) {
					if ((MouseInputSDL.mouseInput.getMouseY()>=48) && (MouseInputSDL.mouseInput.getMouseY()<64+(Math.min(MAX_FILE_IN_ONE_PAGE+1,replaylist.length-1)*16))) {
						cursor=(MouseInputSDL.mouseInput.getMouseY()-48)/16;
					}
				} else {
					if (MouseInputSDL.mouseInput.getMouseY()<48) {
						cursor=MAX_FILE_IN_ONE_PAGE-1;
					}
					else if ((MouseInputSDL.mouseInput.getMouseY()>=48) && (MouseInputSDL.mouseInput.getMouseY()<64+(replaylist.length-MAX_FILE_IN_ONE_PAGE-1)*16)) {
						cursor=MAX_FILE_IN_ONE_PAGE+(MouseInputSDL.mouseInput.getMouseY()-48)/16;
					}
				}
				if (cursor!=oldcursor) ResourceHolderSDL.soundManager.play("cursor");
			}
			
			// カーソル移動
			// if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_UP)) {
			if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_NAV_UP)) {
				cursor--;
				if(cursor < 0) cursor = replaylist.length - 1;
				ResourceHolderSDL.soundManager.play("cursor");
			}
			// if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_DOWN)) {
			if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_NAV_DOWN)) {
				cursor++;
				if(cursor > replaylist.length - 1) cursor = 0;
				ResourceHolderSDL.soundManager.play("cursor");
			}

			// 決定 button
			// if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_A)) {
			if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_NAV_SELECT) || MouseInputSDL.mouseInput.isMouseClicked()) {
				ResourceHolderSDL.soundManager.play("decide");

				CustomProperties prop = new CustomProperties();

				try {
					FileInputStream in = new FileInputStream(NullpoMinoSDL.propGlobal.getProperty("custom.replay.directory", "replay") + "/" + replaylist[cursor]);
					prop.load(in);
					in.close();
				} catch (IOException e) {
					log.error("Failed to load replay file from " + replaylist[cursor], e);
					return;
				}

				StateInGameSDL s = (StateInGameSDL)NullpoMinoSDL.gameStates[NullpoMinoSDL.STATE_INGAME];
				s.startReplayGame(prop);

				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_INGAME);
			}
		}

		// Cancel button
		// if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_B)) NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_TITLE);
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_NAV_CANCEL) || MouseInputSDL.mouseInput.isMouseRightClicked()) {
			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_TITLE);
		}
	}
}
