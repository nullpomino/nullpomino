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

import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.net.UpdateChecker;

import sdljava.SDLException;
//import sdljava.event.MouseState;
//import sdljava.event.SDLEvent;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

/**
 * タイトル画面のステート
 */
public class StateTitleSDL extends BaseStateSDL {
	/** カーソル位置 */
	protected int cursor = 0;

	/** 新Versionの check 済みならtrue */
	protected boolean isNewVersionChecked = false;

	/*
	 * このステートに入ったときの処理
	 */
	@Override
	public void enter() throws SDLException {
		// タイトルバー変更
		SDLVideo.wmSetCaption("NullpoMino version" + GameManager.getVersionString(), null);
		// オブザーバー開始
		NullpoMinoSDL.startObserverClient();
		// GC呼び出し
		System.gc();

		// 新Version check 
		if(!isNewVersionChecked && NullpoMinoSDL.propGlobal.getProperty("updatechecker.enable", true)) {
			isNewVersionChecked = true;

			int startupCount = NullpoMinoSDL.propGlobal.getProperty("updatechecker.startupCount", 0);
			int startupMax = NullpoMinoSDL.propGlobal.getProperty("updatechecker.startupMax", 5);

			if(startupCount >= startupMax) {
				String strURL = NullpoMinoSDL.propGlobal.getProperty("updatechecker.url", "");
				UpdateChecker.startCheckForUpdates(strURL);
				startupCount = 0;
			} else {
				startupCount++;
			}

			if(startupMax >= 1) {
				NullpoMinoSDL.propGlobal.setProperty("updatechecker.startupCount", startupCount);
				NullpoMinoSDL.saveConfig();
			}
		}
	}

	/*
	 * ゲーム画面の描画
	 */
	@Override
	public void render(SDLSurface screen) throws SDLException {
		ResourceHolderSDL.imgTitle.blitSurface(screen);

		NormalFontSDL.printFontGrid(1, 1, "NULLPOMINO", NormalFontSDL.COLOR_ORANGE);
		NormalFontSDL.printFontGrid(1, 2, "VERSION " + GameManager.getVersionString(), NormalFontSDL.COLOR_ORANGE);

		NormalFontSDL.printFontGrid(1, 4 + cursor, "b", NormalFontSDL.COLOR_RED);

		NormalFontSDL.printFontGrid(2, 4, "START", (cursor == 0));
		NormalFontSDL.printFontGrid(2, 5, "REPLAY", (cursor == 1));
		NormalFontSDL.printFontGrid(2, 6, "NETPLAY", (cursor == 2));
		NormalFontSDL.printFontGrid(2, 7, "CONFIG", (cursor == 3));
		NormalFontSDL.printFontGrid(2, 8, "EXIT", (cursor == 4));

		if(cursor == 0) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("Title_Start"));
		if(cursor == 1) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("Title_Replay"));
		if(cursor == 2) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("Title_NetPlay"));
		if(cursor == 3) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("Title_Config"));
		if(cursor == 4) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("Title_Exit"));

		if(UpdateChecker.isNewVersionAvailable(GameManager.getVersionMajor(), GameManager.getVersionMinor())) {
			String strTemp = String.format(NullpoMinoSDL.getUIText("Title_NewVersion"),
					UpdateChecker.getLatestVersionFullString(), UpdateChecker.getStrReleaseDate());
			NormalFontSDL.printTTFFont(16, 416, strTemp);
		}
	}

	/*
	 * Update game state
	 */
	@Override
	public void update() throws SDLException {
		// Mouse
		int mouseOldY = MouseInputSDL.mouseInput.getMouseY();
		
		MouseInputSDL.mouseInput.update();
		
		if (mouseOldY != MouseInputSDL.mouseInput.getMouseY() && MouseInputSDL.mouseInput.getMouseY() >= 64 &&
				MouseInputSDL.mouseInput.getMouseY() < 64+5*16) {
			int oldcursor=cursor;
			cursor=(MouseInputSDL.mouseInput.getMouseY()-64)/16;
			if (cursor!=oldcursor) ResourceHolderSDL.soundManager.play("cursor");
		}
		
		// カーソル移動
		// if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_UP)) {
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_NAV_UP)) {
			cursor--;
			if(cursor < 0) cursor = 4;
			ResourceHolderSDL.soundManager.play("cursor");
		}
		// if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_DOWN)) {
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_NAV_DOWN)) {
			cursor++;
			if(cursor > 4) cursor = 0;
			ResourceHolderSDL.soundManager.play("cursor");
		}

		// 決定 button
		// if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_A)) {
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_NAV_SELECT) || MouseInputSDL.mouseInput.isMouseClicked()) {
			ResourceHolderSDL.soundManager.play("decide");

			switch(cursor) {
			case 0:
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_SELECTMODE);
				break;
			case 1:
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_REPLAYSELECT);
				break;
			case 2:
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_NETGAME);
				break;
			case 3:
				NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
				break;
			case 4:
				NullpoMinoSDL.enterState(-1);
				break;
			}
		}
	}
}
