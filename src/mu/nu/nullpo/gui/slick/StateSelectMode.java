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

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Mode select screen
 */
public class StateSelectMode extends DummyMenuScrollState {
	/** Logger */
	static Logger log = Logger.getLogger(StateSelectMode.class);

	/** This state's ID */
	public static final int ID = 3;

	/** Number of game modes in one page */
	public static final int PAGE_HEIGHT = 24;

	/** true if top-level folder */
	public static boolean isTopLevel;

	/** Current folder name */
	protected String strCurrentFolder;

	/**
	 * Constructor
	 */
	public StateSelectMode() {
		super();
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
	}

	/**
	 * Prepare mode list
	 */
	protected void prepareModeList() {
		strCurrentFolder = StateSelectModeFolder.strCurrentFolder;

		// Get mode list
		LinkedList<String> listMode = null;
		if(isTopLevel) {
			listMode = StateSelectModeFolder.listTopLevelModes;
			list = new String[listMode.size() + 1];
			for(int i = 0; i < listMode.size(); i++) {
				list[i] = listMode.get(i);
			}
			list[list.length - 1] = "[MORE...]";
		} else {
			listMode = StateSelectModeFolder.mapFolder.get(strCurrentFolder);
			if(listMode != null) {
				list = new String[listMode.size()];
				for(int i = 0; i < list.length; i++) {
					list[i] = listMode.get(i);
				}
			} else {
				list = NullpoMinoSlick.modeManager.getModeNames(false);
			}
		}
		maxCursor = list.length - 1;

		// Set cursor postion
		String lastmode = null;
		if(isTopLevel) {
			lastmode = NullpoMinoSlick.propGlobal.getProperty("name.mode.toplevel", null);
		} else if(strCurrentFolder.length() > 0) {
			lastmode = NullpoMinoSlick.propGlobal.getProperty("name.mode." + strCurrentFolder, null);
		} else {
			lastmode = NullpoMinoSlick.propGlobal.getProperty("name.mode", null);
		}
		cursor = getIDbyName(lastmode);
		if(cursor < 0) cursor = 0;
		if(cursor > list.length - 1) cursor = list.length - 1;
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

	/**
	 * Get game mode description
	 * @param str Mode name
	 * @return Description
	 */
	protected String getModeDesc(String str) {
		String str2 = str.replace(' ', '_');
		str2 = str2.replace('(', 'l');
		str2 = str2.replace(')', 'r');
		String result = NullpoMinoSlick.propModeDesc.getProperty(str2);
		if(result == null) {
			result = NullpoMinoSlick.propDefaultModeDesc.getProperty(str2, str2);
		}
		return result;
	}

	/*
	 * Enter
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		prepareModeList();
	}

	/*
	 * Render screen
	 */
	@Override
	public void onRenderSuccess(GameContainer container, StateBasedGame game, Graphics graphics) {
		if(!isTopLevel && (strCurrentFolder.length() > 0)) {
			NormalFont.printFontGrid(1, 1, strCurrentFolder + " (" + (cursor + 1) + "/" + list.length + ")",
					NormalFont.COLOR_ORANGE);
		} else {
			NormalFont.printFontGrid(1, 1, "MODE SELECT (" + (cursor + 1) + "/" + list.length + ")",
					NormalFont.COLOR_ORANGE);
		}

		NormalFont.printTTFFont(16, 440, getModeDesc(list[cursor]));
	}

	/*
	 * Decide
	 */
	@Override
	protected boolean onDecide(GameContainer container, StateBasedGame game, int delta) {
		ResourceHolder.soundManager.play("decide");

		if(isTopLevel && (cursor == list.length - 1)) {
			// More...
			NullpoMinoSlick.propGlobal.setProperty("name.mode.toplevel", list[cursor]);
			game.enterState(StateSelectModeFolder.ID);
		} else {
			// Go to rule selector
			if(isTopLevel) {
				NullpoMinoSlick.propGlobal.setProperty("name.mode.toplevel", list[cursor]);
			}
			if(strCurrentFolder.length() > 0) {
				NullpoMinoSlick.propGlobal.setProperty("name.mode." + strCurrentFolder, list[cursor]);
			}
			NullpoMinoSlick.propGlobal.setProperty("name.mode", list[cursor]);
			NullpoMinoSlick.saveConfig();
			game.enterState(StateSelectRuleFromList.ID);
		}

		return false;
	}

	/*
	 * Cancel
	 */
	@Override
	protected boolean onCancel(GameContainer container, StateBasedGame game, int delta) {
		if(isTopLevel) {
			game.enterState(StateTitle.ID);
		} else {
			game.enterState(StateSelectModeFolder.ID);
		}
		return false;
	}
}
