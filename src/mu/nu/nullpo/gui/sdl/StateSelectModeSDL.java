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
public class StateSelectModeSDL extends DummyMenuScrollStateSDL {
	/** 1画面に表示するMaximumMode count */
	public static final int PAGE_HEIGHT = 25;

	/**
	 * Constructor
	 */
	public StateSelectModeSDL() {
		pageHeight = PAGE_HEIGHT;
		String lastmode = NullpoMinoSDL.propGlobal.getProperty("name.mode", null);
		list = NullpoMinoSDL.modeManager.getModeNames(false);
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

	/*
	 * Draw the screen
	 */
	@Override
	public void onRenderSuccess(SDLSurface screen) throws SDLException {
		NormalFontSDL.printFontGrid(1, 1, "MODE SELECT (" + (cursor + 1) + "/" + list.length + ")",
									NormalFontSDL.COLOR_ORANGE);
	}

	@Override
	protected boolean onDecide() throws SDLException {
		ResourceHolderSDL.soundManager.play("decide");
		NullpoMinoSDL.propGlobal.setProperty("name.mode", list[cursor]);
		NullpoMinoSDL.saveConfig();

		StateInGameSDL s = (StateInGameSDL)NullpoMinoSDL.gameStates[NullpoMinoSDL.STATE_INGAME];
		s.startNewGame();

		NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_INGAME);

		return false;
	}

	@Override
	protected boolean onCancel() throws SDLException {
		NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_TITLE);
		return false;
	}
}
