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
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

import org.apache.log4j.Logger;

import sdljava.SDLException;
//import sdljava.event.MouseState;
//import sdljava.event.SDLEvent;
import sdljava.video.SDLSurface;

/**
 * リプレイ選択画面のステート
 */
public class StateReplaySelectSDL extends DummyMenuScrollStateSDL {
	/** Log */
	static Logger log = Logger.getLogger(StateReplaySelectSDL.class);

	/** 1画面に表示するMaximumファイルcount */
	public static final int PAGE_HEIGHT = 20;

	/** Mode  name */
	protected String[] modenameList;

	/** Rule name */
	protected String[] rulenameList;

	/** Scoreなどの情報 */
	protected Statistics[] statsList;

	public StateReplaySelectSDL () {
		pageHeight = PAGE_HEIGHT;
		nullError = "REPLAY DIRECTORY NOT FOUND";
		emptyError = "NO REPLAY FILE";
	}

	/*
	 * Called when entering this state
	 */
	@Override
	public void enter() throws SDLException {
		list = getReplayFileList();
		maxCursor = list.length-1;
		setReplayRuleAndModeList();
	}

	/**
	 * リプレイファイル一覧を取得
	 * @return リプレイファイルのFilenameの配列。ディレクトリがないならnull
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
		if(list == null) return;

		modenameList = new String[list.length];
		rulenameList = new String[list.length];
		statsList = new Statistics[list.length];

		for(int i = 0; i < list.length; i++) {
			CustomProperties prop = new CustomProperties();

			try {
				FileInputStream in = new FileInputStream(NullpoMinoSDL.propGlobal.getProperty("custom.replay.directory", "replay") + "/" + list[i]);
				prop.load(in);
				in.close();
			} catch (IOException e) {
				log.warn("Failed to load replay file from " + list[i], e);
			}

			modenameList[i] = prop.getProperty("name.mode", "");
			rulenameList[i] = prop.getProperty("name.rule", "");

			statsList[i] = new Statistics();
			statsList[i].readProperty(prop, 0);
		}
	}

	/*
	 * Draw the screen
	 */
	@Override
	protected void onRenderSuccess(SDLSurface screen) throws SDLException {
		String title = "SELECT REPLAY FILE";
		title += " (" + (cursor + 1) + "/" + (list.length) + ")";

		NormalFontSDL.printFontGrid(1, 1, title, NormalFontSDL.COLOR_ORANGE);

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

	@Override
	protected boolean onDecide() throws SDLException {
		ResourceHolderSDL.soundManager.play("decide");

		CustomProperties prop = new CustomProperties();

		try {
			FileInputStream in = new FileInputStream(NullpoMinoSDL.propGlobal.getProperty("custom.replay.directory", "replay") + "/" + list[cursor]);
			prop.load(in);
			in.close();
		} catch (IOException e) {
			log.error("Failed to load replay file from " + list[cursor], e);
			return true;
		}

		StateInGameSDL s = (StateInGameSDL)NullpoMinoSDL.gameStates[NullpoMinoSDL.STATE_INGAME];
		s.startReplayGame(prop);

		NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_INGAME);

		return false;
	}

	@Override
	protected boolean onCancel() throws SDLException {
		NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_TITLE);
		return false;
	}
}
