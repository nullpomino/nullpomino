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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import mu.nu.nullpo.game.component.Statistics;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * リプレイ選択画面のステート
 */
public class StateReplaySelect extends BasicGameState {
	/** このステートのID */
	public static final int ID = 4;

	/** 1画面に表示する最大ファイルcount */
	public static final int PAGE_HEIGHT = 20;

	/** Log */
	static Logger log = Logger.getLogger(StateReplaySelect.class);

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

	/** スクリーンショット撮影 flag */
	protected boolean ssflag = false;

	/** ID number of file at top of currently displayed section */
	protected int minentry = 0;

	/*
	 * このステートのIDを取得
	 */
	@Override
	public int getID() {
		return ID;
	}

	/*
	 * ステートのInitialization
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
	}

	/*
	 * このステートに入ったときの処理
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		replaylist = getReplayFileList();
		setReplayRuleAndModeList();
	}

	/**
	 * リプレイファイル一覧を取得
	 * @return リプレイファイルのファイル名の配列。ディレクトリがないならnull
	 */
	protected String[] getReplayFileList() {
		File dir = new File(NullpoMinoSlick.propGlobal.getProperty("custom.replay.directory", "replay"));

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
				FileInputStream in = new FileInputStream(NullpoMinoSlick.propGlobal.getProperty("custom.replay.directory", "replay") + "/" + replaylist[i]);
				prop.load(in);
				in.close();
			} catch (IOException e) {
				log.error("Failed to load replay file (" + replaylist[i] + ")", e);
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
	public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
		// 背景
		graphics.drawImage(ResourceHolder.imgMenu, 0, 0);

		// Menu
		if(replaylist == null) {
			NormalFont.printFontGrid(1, 1, "REPLAY DIRECTORY NOT FOUND", NormalFont.COLOR_RED);
		} else if(replaylist.length == 0) {
			NormalFont.printFontGrid(1, 1, "NO REPLAY FILE", NormalFont.COLOR_RED);
		} else {
			if (cursor >= replaylist.length)
				cursor = 0;
			if (cursor < minentry)
				minentry = cursor;
			int maxentry = minentry + PAGE_HEIGHT - 1;
			if (cursor >= maxentry)
			{
				maxentry = cursor;
				minentry = maxentry - PAGE_HEIGHT + 1;
			}
			
			String title = "SELECT REPLAY FILE";
			title += " (" + (cursor + 1) + "/" + (replaylist.length) + ")";
			NormalFont.printFontGrid(1, 1, title, NormalFont.COLOR_ORANGE);

			NormalFont.printFontGrid(1, 24, "MODE:" + modenameList[cursor] + " RULE:" + rulenameList[cursor], NormalFont.COLOR_CYAN);
			NormalFont.printFontGrid(1, 25,
										"SCORE:" + statsList[cursor].score + " LINE:" + statsList[cursor].lines
										, NormalFont.COLOR_CYAN);
			NormalFont.printFontGrid(1, 26,
										"LEVEL:" + (statsList[cursor].level + statsList[cursor].levelDispAdd) +
										" TIME:" + GeneralUtil.getTime(statsList[cursor].time)
										, NormalFont.COLOR_CYAN);
			/*
			NormalFont.printFontGrid(1, 27,
										"GAME RATE:" + ( (statsList[cursor].gamerate == 0f) ? "UNKNOWN" : ((100*statsList[cursor].gamerate) + "%") )
										, NormalFont.COLOR_CYAN);
			*/
			
			SlickUtil.drawMenuList(graphics, PAGE_HEIGHT, replaylist, cursor, minentry, maxentry);
		}

		// FPS
		NullpoMinoSlick.drawFPS(container);
		// オブザーバー
		NullpoMinoSlick.drawObserverClient();
		// スクリーンショット
		if(ssflag) {
			NullpoMinoSlick.saveScreenShot(container, graphics);
			ssflag = false;
		}

		if(!NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}

	/*
	 * Update game state
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(!container.hasFocus()) {
			if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
			return;
		}

		// キー入力状態を更新
		GameKey.gamekey[0].update(container.getInput());

		// Mouse
		boolean mouseConfirm = false;
		MouseInput.mouseInput.update(container.getInput());
		if (MouseInput.mouseInput.isMouseClicked())
		{
			int x = MouseInput.mouseInput.getMouseX() >> 4;
			int y = MouseInput.mouseInput.getMouseY() >> 4;
			if (x < SlickUtil.SB_TEXT_X-1 && y >= 3 && y <= 2 + PAGE_HEIGHT)
			{
				int newCursor = y - 3 + minentry;
				if (newCursor == cursor)
					mouseConfirm = true;
				else
				{
					ResourceHolder.soundManager.play("cursor");
					cursor = newCursor;
				}
			}
			else if (x == SlickUtil.SB_TEXT_X)
			{
				int maxentry = minentry + PAGE_HEIGHT - 1;
				if (y == 3 && minentry > 0)
				{
					//Scroll up
					minentry--;
					maxentry--;
					if (cursor > maxentry)
						cursor = maxentry;
				}
				else if (y == 2 + PAGE_HEIGHT && maxentry < replaylist.length-1)
				{
					//Down arrow
					minentry++;
					if (cursor < minentry)
						cursor = minentry;
				}
			}
		}
		
		if((replaylist != null) && (replaylist.length > 0)) {
			// カーソル移動
			//if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_UP)) {
			if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_NAV_UP)) {
				cursor--;
				if(cursor < 0) cursor = replaylist.length - 1;
				ResourceHolder.soundManager.play("cursor");
			}
			//if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_DOWN)) {
			if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_NAV_DOWN)) {
				cursor++;
				if(cursor > replaylist.length - 1) cursor = 0;
				ResourceHolder.soundManager.play("cursor");
			}

			// 決定 button
			//if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_A)) {
			if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_NAV_SELECT) || mouseConfirm) {
				ResourceHolder.soundManager.play("decide");

				CustomProperties prop = new CustomProperties();

				try {
					FileInputStream in = new FileInputStream(NullpoMinoSlick.propGlobal.getProperty("custom.replay.directory", "replay") + "/" + replaylist[cursor]);
					prop.load(in);
					in.close();
				} catch (IOException e) {
					log.error("Failed to load replay file from " + replaylist[cursor], e);
					return;
				}

				NullpoMinoSlick.stateInGame.startReplayGame(prop);

				game.enterState(StateInGame.ID);
			}
		}

		// Cancel button
		//if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_B)) game.enterState(StateTitle.ID);
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_NAV_CANCEL) || MouseInput.mouseInput.isMouseRightClicked())
			game.enterState(StateTitle.ID);

		// スクリーンショット button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_SCREENSHOT)) ssflag = true;

		// 終了 button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_QUIT)) container.exit();

		if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}
}
