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

import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.net.UpdateChecker;

import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * タイトル画面のステート
 */
public class StateTitle extends BasicGameState {
	/** このステートのID */
	public static final int ID = 1;

	/** Log */
	static Logger log = Logger.getLogger(StateTitle.class);

	/** カーソル位置 */
	protected int cursor = 0;

	/** スクリーンショット撮影 flag */
	protected boolean ssflag = false;

	/** 新Versionの check 済みならtrue */
	protected boolean isNewVersionChecked = false;

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
		// オブザーバー開始
		NullpoMinoSlick.startObserverClient();
		// GC呼び出し
		System.gc();

		// タイトルバー更新
		if(container instanceof AppGameContainer) {
			((AppGameContainer) container).setTitle("NullpoMino version" + GameManager.getVersionString());
			((AppGameContainer) container).setUpdateOnlyWhenVisible(true);
		}

		// 新Version check 
		if(!isNewVersionChecked && NullpoMinoSlick.propGlobal.getProperty("updatechecker.enable", true)) {
			isNewVersionChecked = true;

			int startupCount = NullpoMinoSlick.propGlobal.getProperty("updatechecker.startupCount", 0);
			int startupMax = NullpoMinoSlick.propGlobal.getProperty("updatechecker.startupMax", 5);

			if(startupCount >= startupMax) {
				String strURL = NullpoMinoSlick.propGlobal.getProperty("updatechecker.url", "");
				UpdateChecker.startCheckForUpdates(strURL);
				startupCount = 0;
			} else {
				startupCount++;
			}

			if(startupMax >= 1) {
				NullpoMinoSlick.propGlobal.setProperty("updatechecker.startupCount", startupCount);
				NullpoMinoSlick.saveConfig();
			}
		}
	}

	/*
	 * 画面描画
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// 背景
		g.drawImage(ResourceHolder.imgTitle, 0, 0);

		// Menu
		NormalFont.printFontGrid(1, 1, "NULLPOMINO", NormalFont.COLOR_ORANGE);
		NormalFont.printFontGrid(1, 2, "VERSION " + GameManager.getVersionString(), NormalFont.COLOR_ORANGE);
		NormalFont.printFontGrid(1, 4 + cursor, "b", NormalFont.COLOR_RED);

		NormalFont.printFontGrid(2, 4, "START", (cursor == 0));
		NormalFont.printFontGrid(2, 5, "REPLAY", (cursor == 1));
		NormalFont.printFontGrid(2, 6, "NETPLAY", (cursor == 2));
		NormalFont.printFontGrid(2, 7, "CONFIG", (cursor == 3));
		NormalFont.printFontGrid(2, 8, "EXIT", (cursor == 4));
		
		
		
        
        if(cursor == 0) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("Title_Start"));
		if(cursor == 1) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("Title_Replay"));
		if(cursor == 2) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("Title_NetPlay"));
		if(cursor == 3) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("Title_Config"));
		if(cursor == 4) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("Title_Exit"));

		if(UpdateChecker.isNewVersionAvailable(GameManager.getVersionMajor(), GameManager.getVersionMinor())) {
			String strTemp = String.format(NullpoMinoSlick.getUIText("Title_NewVersion"),
					UpdateChecker.getLatestVersionFullString(), UpdateChecker.getStrReleaseDate());
			NormalFont.printTTFFont(16, 416, strTemp);
		}
        
		
		// FPS
		NullpoMinoSlick.drawFPS(container);
		// オブザーバー
		NullpoMinoSlick.drawObserverClient();
		// スクリーンショット
		if(ssflag) {
			NullpoMinoSlick.saveScreenShot(container, g);
			ssflag = false;
		}

		if(!NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}

	/*
	 * ゲームの内部状態の更新
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(!container.hasFocus()) {
			if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
			return;
		}

		// TTFフォント描画
		if(ResourceHolder.ttfFont != null) ResourceHolder.ttfFont.loadGlyphs();

		// キー入力状態を更新
		GameKey.gamekey[0].update(container.getInput());

		// Mouse
		boolean mouseConfirm = false;
		MouseInput.mouseInput.update(container.getInput());
		if (MouseInput.mouseInput.isMouseClicked())
		{
			int y = MouseInput.mouseInput.getMouseY() >> 4;
			int newCursor = y - 4;
			if (newCursor == cursor)
				mouseConfirm = true;
			else if (newCursor >= 0 && newCursor <= 4)
			{
				ResourceHolder.soundManager.play("cursor");
				cursor = newCursor;
			}
		}
		
		// カーソル移動
		//if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_UP)) {
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_NAV_UP)) {
			
			cursor--;
			if(cursor < 0) cursor = 4;
			ResourceHolder.soundManager.play("cursor");
		}
		//if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_DOWN)) {
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_NAV_DOWN)) {
		    cursor++;
			if(cursor > 4) cursor = 0;
			ResourceHolder.soundManager.play("cursor");
		}

		// 決定 button
		//if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_A)) {
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_NAV_SELECT) || mouseConfirm) {
			ResourceHolder.soundManager.play("decide");

			switch(cursor) {
			case 0:
				game.enterState(StateSelectMode.ID);
				break;
			case 1:
				game.enterState(StateReplaySelect.ID);
				break;
			case 2:
				game.enterState(StateNetGame.ID);
				break;
			case 3:
				game.enterState(StateConfigMainMenu.ID);
				break;
			case 4:
				container.exit();
				break;
			}
		}

		// スクリーンショット button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_SCREENSHOT)) ssflag = true;

		// 終了 button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_QUIT)) container.exit();

		if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}
}
