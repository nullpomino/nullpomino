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

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Mode 選択画面のステート
 */
public class StateSelectMode extends BasicGameState {
	/** このステートのID */
	public static final int ID = 3;

	/** 1画面に表示する最大Mode count */
	public static final int PAGE_HEIGHT = 25;

	/** Mode  nameの配列 */
	protected String[] modenames;

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

	/*
	 * ステートのInitialization
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		String lastmode = NullpoMinoSlick.propGlobal.getProperty("name.mode", null);
		modenames = NullpoMinoSlick.modeManager.getModeNames(false);
		cursor = getIDbyName(lastmode);
		if(cursor < 0) cursor = 0;
		if(cursor > modenames.length - 1) cursor = 0;
	}

	/**
	 * Get mode ID (not including netplay modes)
	 * @param name Name of mode
	 * @return ID (-1 if not found)
	 */
	protected int getIDbyName(String name) {
		if((name == null) || (modenames == null)) return -1;

		for(int i = 0; i < modenames.length; i++) {
			if(name.equals(modenames[i])) {
				return i;
			}
		}

		return -1;
	}

	/*
	 * 画面描画
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
		// 背景
		graphics.drawImage(ResourceHolder.imgMenu, 0, 0);

		// Menu
		if (cursor >= modenames.length)
			cursor = 0;
		if (cursor < minentry)
			minentry = cursor;
		int maxentry = minentry + PAGE_HEIGHT - 1;
		if (cursor >= minentry + PAGE_HEIGHT - 1)
		{
			maxentry = cursor;
			minentry = cursor - PAGE_HEIGHT + 1;
		}
		
		NormalFont.printFontGrid(1, 1, "MODE SELECT (" + (cursor + 1) + "/" + modenames.length + ")",
				NormalFont.COLOR_ORANGE);

		SlickUtil.drawMenuList(graphics, PAGE_HEIGHT, modenames, cursor, minentry, maxentry);
		
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
				else if (y == 2 + PAGE_HEIGHT && maxentry < modenames.length-1)
				{
					//Down arrow
					minentry++;
					if (cursor < minentry)
						cursor = minentry;
				}
			}
		}
		
		// カーソル移動
		//if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_UP)) {
	    if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_NAV_UP)) {
			cursor--;
			if(cursor < 0) cursor = modenames.length - 1;
			ResourceHolder.soundManager.play("cursor");
		}
		//if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_DOWN)) {
	    if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_NAV_DOWN)) {	
	        cursor++;
			if(cursor > modenames.length - 1) cursor = 0;
			ResourceHolder.soundManager.play("cursor");
		}

		// 決定 button
		//if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_A)) {
	    if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_NAV_SELECT) || mouseConfirm) {
			ResourceHolder.soundManager.play("decide");
			NullpoMinoSlick.propGlobal.setProperty("name.mode", modenames[cursor]);
			NullpoMinoSlick.saveConfig();
			NullpoMinoSlick.stateInGame.startNewGame();
			game.enterState(StateInGame.ID);
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
