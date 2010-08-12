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

import mu.nu.nullpo.util.CustomProperties;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * ルール選択画面のステート
 */
public class StateConfigRuleSelect extends BasicGameState {
	/** このステートのID */
	public static final int ID = 7;

	/** 1画面に表示する最大ファイルcount */
	public static final int PAGE_HEIGHT = 20;

	/** プレイヤーID */
	public int player = 0;

	/** 初期設定Mode  */
	protected boolean firstSetupMode;

	/** ファイル名 */
	protected String[] strFileNameList;

	/** ファイルパス一覧 */
	protected String[] strFilePathList;

	/** Rule name一覧 */
	protected String[] strRuleNameList;

	/** Current ルールファイル */
	protected String strCurrentFileName;

	/** Current Rule name */
	protected String strCurrentRuleName;

	/** カーソル位置 */
	protected int cursor = 0;

	/** スクリーンショット撮影 flag */
	protected boolean ssflag = false;

	/** ID number of entry at top of currently displayed section */
	protected int minentry = 0;

	/*
	 * このステートのIDを取得
	 */
	@Override
	public int getID() {
		return ID;
	}

	/**
	 * ルールファイル一覧を取得
	 * @return ルールファイルのファイル名の配列。ディレクトリがないならnull
	 */
	protected String[] getRuleFileList() {
		File dir = new File("config/rule");

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir1, String name) {
				return name.endsWith(".rul");
			}
		};

		String[] list = dir.list(filter);

		return list;
	}

	/**
	 * 詳細情報を更新
	 */
	protected void updateDetails() {
		strFilePathList = new String[strFileNameList.length];
		strRuleNameList = new String[strFileNameList.length];

		for(int i = 0; i < strFileNameList.length; i++) {
			File file = new File("config/rule/" + strFileNameList[i]);
			strFilePathList[i] = file.getPath();

			CustomProperties prop = new CustomProperties();

			try {
				FileInputStream in = new FileInputStream("config/rule/" + strFileNameList[i]);
				prop.load(in);
				in.close();
				strRuleNameList[i] = prop.getProperty("0.ruleopt.strRuleName", "");
			} catch (Exception e) {
				strRuleNameList[i] = "";
			}
		}
	}

	/*
	 * このステートに入ったときの処理
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		firstSetupMode = NullpoMinoSlick.propConfig.getProperty("option.firstSetupMode", true);

		strFileNameList = getRuleFileList();
		updateDetails();

		strCurrentFileName = NullpoMinoSlick.propGlobal.getProperty(player + ".rulefile", "");
		strCurrentRuleName = NullpoMinoSlick.propGlobal.getProperty(player + ".rulename", "");
		for(int i = 0; i < strFileNameList.length; i++) {
			if(strCurrentFileName.equals(strFileNameList[i])) {
				cursor = i;
			}
		}
	}

	/*
	 * ステートのInitialization
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
	}

	/*
	 * 画面描画
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
		// 背景
		graphics.drawImage(ResourceHolder.imgMenu, 0, 0);

		// Menu
		if(strFileNameList == null) {
			NormalFont.printFontGrid(1, 1, "RULE DIRECTORY NOT FOUND", NormalFont.COLOR_RED);
		} else if(strFileNameList.length <= 0) {
			NormalFont.printFontGrid(1, 1, "NO RULE FILE", NormalFont.COLOR_RED);
		} else {
			if (cursor >= strFileNameList.length)
				cursor = 0;
			if (cursor < minentry)
				minentry = cursor;
			int maxentry = minentry + PAGE_HEIGHT - 1;
			if (cursor >= maxentry)
			{
				maxentry = cursor;
				minentry = maxentry - PAGE_HEIGHT + 1;
			}
			
			String title = "SELECT " + (player + 1) + "P RULE (" + (cursor + 1) + "/" + (strFileNameList.length) + ")";
			NormalFont.printFontGrid(1, 1, title, NormalFont.COLOR_ORANGE);

			NormalFont.printFontGrid(1, 26, "FILE:" + strFileNameList[cursor].toUpperCase(), NormalFont.COLOR_CYAN);
			NormalFont.printFontGrid(1, 27, "CURRENT:" + strCurrentRuleName.toUpperCase(), NormalFont.COLOR_BLUE);

			NormalFont.printFontGrid(1, 28, "A:OK", NormalFont.COLOR_GREEN);
			
			SlickUtil.drawMenuList(graphics, PAGE_HEIGHT, strFileNameList, cursor, minentry, maxentry);
		}

		if(firstSetupMode) {
			NormalFont.printFontGrid(6, 28, "D:USE DEFAULT RULE", NormalFont.COLOR_GREEN);
		} else {
			NormalFont.printFontGrid(6, 28, "B:CANCEL D:USE DEFAULT RULE", NormalFont.COLOR_GREEN);
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
		MouseInput.mouseInput.update(container.getInput());
		boolean clicked = MouseInput.mouseInput.isMouseClicked();
		boolean mouseConfirm = false;
		int x = MouseInput.mouseInput.getMouseX() >> 4;
		int y = MouseInput.mouseInput.getMouseY() >> 4;
		if (x == SlickUtil.SB_TEXT_X && (clicked || MouseInput.mouseInput.isMenuRepeatLeft()))
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
			else if (y == 2 + PAGE_HEIGHT && maxentry < strFileNameList.length-1)
			{
				//Down arrow
				minentry++;
				if (cursor < minentry)
					cursor = minentry;
			}
		}
		else if (clicked && x < SlickUtil.SB_TEXT_X-1 && y >= 3 && y <= 2 + PAGE_HEIGHT)
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

		if((strFileNameList != null) && (strFileNameList.length > 0)) {
			// カーソル移動
			//if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_UP)) {
			if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_NAV_UP)) {
				cursor--;
				if(cursor < 0) cursor = strFileNameList.length - 1;
				ResourceHolder.soundManager.play("cursor");
			}
			//if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_DOWN)) {
			if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_NAV_DOWN)) {
			    cursor++;
				if(cursor > strFileNameList.length - 1) cursor = 0;
				ResourceHolder.soundManager.play("cursor");
			}

			// 決定 button
			//if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_A)) {
			if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_NAV_SELECT) || mouseConfirm) {
				ResourceHolder.soundManager.play("decide");
				NullpoMinoSlick.propGlobal.setProperty(player + ".rule", strFilePathList[cursor]);
				NullpoMinoSlick.propGlobal.setProperty(player + ".rulefile", strFileNameList[cursor]);
				NullpoMinoSlick.propGlobal.setProperty(player + ".rulename", strRuleNameList[cursor]);
				NullpoMinoSlick.propConfig.setProperty("option.firstSetupMode", false);
				NullpoMinoSlick.saveConfig();

				if(!firstSetupMode) game.enterState(StateConfigMainMenu.ID);
				else game.enterState(StateTitle.ID);
				return;
			}
		}

		// デフォルトルールに設定
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_D)) {
			ResourceHolder.soundManager.play("decide");
			NullpoMinoSlick.propGlobal.setProperty(player + ".rule", "");
			NullpoMinoSlick.propGlobal.setProperty(player + ".rulefile", "");
			NullpoMinoSlick.propGlobal.setProperty(player + ".rulename", "");
			NullpoMinoSlick.propConfig.setProperty("option.firstSetupMode", false);
			NullpoMinoSlick.saveConfig();
			if(!firstSetupMode) game.enterState(StateConfigMainMenu.ID);
			else game.enterState(StateTitle.ID);
			return;
		}

		// Cancel button
		//if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_B) && !firstSetupMode) {
		if((GameKey.gamekey[0].isPushKey(GameKey.BUTTON_NAV_CANCEL) || MouseInput.mouseInput.isMouseRightClicked())
				&& !firstSetupMode) {
			game.enterState(StateConfigMainMenu.ID);
			return;
		}

		// スクリーンショット button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_SCREENSHOT)) ssflag = true;

		// 終了 button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_QUIT)) container.exit();

		if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}
}
