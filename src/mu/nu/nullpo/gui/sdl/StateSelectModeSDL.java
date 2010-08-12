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

import sdljava.SDLException;
//import sdljava.event.MouseState;
//import sdljava.event.SDLEvent;
import sdljava.video.SDLSurface;

/**
 * Mode 選択画面のステート
 */
public class StateSelectModeSDL extends BaseStateSDL {
	/** 1画面に表示する最大Mode count */
	public static final int PAGE_HEIGHT = 25;
	
	/** x-coordinate of scroll arrows */
	public static final int SCROLL_ARROW_X = 38;

	/** Mode  nameの配列 */
	protected String[] modenames;

	/** カーソル位置 */
	protected int cursor = 0;

	/** ID number of entry at top of currently displayed section */
	protected int minentry = 0;

	/**
	 * Constructor
	 */
	public StateSelectModeSDL() {
		String lastmode = NullpoMinoSDL.propGlobal.getProperty("name.mode", null);
		modenames = NullpoMinoSDL.modeManager.getModeNames(false);
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
	@Override
	public void render(SDLSurface screen) throws SDLException {
		ResourceHolderSDL.imgMenu.blitSurface(screen);

		if (cursor >= modenames.length)
			cursor = 0;
		
		if (cursor < minentry)
			minentry = cursor;
		int maxentry = minentry + PAGE_HEIGHT - 1;
		if (cursor >= maxentry)
		{
			maxentry = cursor;
			minentry = maxentry - PAGE_HEIGHT + 1;
		}
		if (maxentry >= modenames.length)
			maxentry = modenames.length-1;

		NormalFontSDL.printFontGrid(1, 1, "MODE SELECT (" + (cursor + 1) + "/" + modenames.length + ")",
									NormalFontSDL.COLOR_ORANGE);
		
		NormalFontSDL.printFontGrid(SCROLL_ARROW_X, 3, "k", NormalFontSDL.COLOR_BLUE);
		NormalFontSDL.printFontGrid(SCROLL_ARROW_X, 2 + PAGE_HEIGHT, "n", NormalFontSDL.COLOR_BLUE);

		for(int i = minentry, y = 0; i <= maxentry; i++, y++) {
			if(i < modenames.length) {
				NormalFontSDL.printFontGrid(2, 3 + y, modenames[i].toUpperCase(), (cursor == i));
				if(cursor == i) NormalFontSDL.printFontGrid(1, 3 + y, "b", NormalFontSDL.COLOR_RED);
			}
		}
	}

	/*
	 * Update game state
	 */
	@Override
	public void update() throws SDLException {
		// Mouse
		MouseInputSDL.mouseInput.update();
		boolean clicked = MouseInputSDL.mouseInput.isMouseClicked();
		boolean mouseConfirm = false;
		int x = MouseInputSDL.mouseInput.getMouseX() >> 4;
		int y = MouseInputSDL.mouseInput.getMouseY() >> 4;
		if (x == SCROLL_ARROW_X && (clicked || MouseInputSDL.mouseInput.isMenuRepeatLeft()))
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
		else if (clicked && x < SCROLL_ARROW_X-1 && y >= 3 && y <= 2 + PAGE_HEIGHT)
		{
			int newCursor = y - 3 + minentry;
			if (newCursor == cursor)
				mouseConfirm = true;
			else
			{
				ResourceHolderSDL.soundManager.play("cursor");
				cursor = newCursor;
			}
		}
		
		// カーソル移動
		// if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_UP)) {
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_NAV_UP)) {
			cursor--;
			if(cursor < 0) cursor = modenames.length - 1;
			ResourceHolderSDL.soundManager.play("cursor");
		}
		// if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_DOWN)) {
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_NAV_DOWN)) {
			cursor++;
			if(cursor > modenames.length - 1) cursor = 0;
			ResourceHolderSDL.soundManager.play("cursor");
		}

		// 決定 button
		// if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_A)) {
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_NAV_SELECT) || mouseConfirm) {
			ResourceHolderSDL.soundManager.play("decide");
			NullpoMinoSDL.propGlobal.setProperty("name.mode", modenames[cursor]);
			NullpoMinoSDL.saveConfig();

			StateInGameSDL s = (StateInGameSDL)NullpoMinoSDL.gameStates[NullpoMinoSDL.STATE_INGAME];
			s.startNewGame();

			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_INGAME);
		}

		// Cancel button
		// if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_B)) {
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_NAV_CANCEL) || MouseInputSDL.mouseInput.isMouseRightClicked()) {
			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_TITLE);
		}
	}
}
