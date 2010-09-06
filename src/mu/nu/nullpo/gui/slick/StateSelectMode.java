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
import org.newdawn.slick.state.StateBasedGame;

/**
 * Mode 選択画面のステート
 */
public class StateSelectMode extends DummyMenuScrollState {
	/** This state's ID */
	public static final int ID = 3;

	/** 1画面に表示するMaximumMode count */
	public static final int PAGE_HEIGHT = 25;

	public StateSelectMode() {
		pageHeight = PAGE_HEIGHT;
	}

	/*
	 * Fetch this state's ID
	 */
	@Override
	public int getID() {
		return ID;
	}

	/*
	 * State initialization
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		String lastmode = NullpoMinoSlick.propGlobal.getProperty("name.mode", null);
		list = NullpoMinoSlick.modeManager.getModeNames(false);
		maxCursor = list.length-1;
		cursor = getIDbyName(lastmode);
		if(cursor < 0) cursor = 0;
		if(cursor > list.length - 1) cursor = 0;
	}

	/**
	 * Get mode ID (not including netplay modes)
	 * @param name Name of mode
	 * @return ID (-1 if not found)
	 */
	protected int getIDbyName(String name) {
		if((name == null) || (list == null)) return -1;

		for(int i = 0; i < list.length; i++) {
			if(name.equals(list[i])) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public void onRenderSuccess(GameContainer container, StateBasedGame game, Graphics graphics) {
		NormalFont.printFontGrid(1, 1, "MODE SELECT (" + (cursor + 1) + "/" + list.length + ")",
				NormalFont.COLOR_ORANGE);
	}

	@Override
	protected boolean onDecide(GameContainer container, StateBasedGame game, int delta) {
		ResourceHolder.soundManager.play("decide");
		NullpoMinoSlick.propGlobal.setProperty("name.mode", list[cursor]);
		NullpoMinoSlick.saveConfig();
		NullpoMinoSlick.stateInGame.startNewGame();
		game.enterState(StateInGame.ID);
		return false;
	}

	@Override
	protected boolean onCancel(GameContainer container, StateBasedGame game, int delta) {
		game.enterState(StateTitle.ID);
		return false;
	}
}
