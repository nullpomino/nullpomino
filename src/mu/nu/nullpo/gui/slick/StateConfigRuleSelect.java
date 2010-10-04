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
import java.util.Arrays;
import java.util.LinkedList;

import mu.nu.nullpo.util.CustomProperties;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Rule selector state
 */
public class StateConfigRuleSelect extends DummyMenuScrollState {
	/** This state's ID */
	public static final int ID = 7;

	/** Number of rules shown at a time */
	public static final int PAGE_HEIGHT = 21;

	/** Player ID */
	public int player = 0;

	/** Game style ID */
	public int style = 0;

	/** Rule file list */
	private String[] strFileList;

	/** Current Rule File name */
	private String strCurrentFileName;

	/** Current Rule name */
	private String strCurrentRuleName;

	/** Rule entries */
	private LinkedList<RuleEntry> ruleEntries;

	/**
	 * Constructor
	 */
	public StateConfigRuleSelect() {
		pageHeight = PAGE_HEIGHT;
		nullError = "RULE DIRECTORY NOT FOUND";
		emptyError = "NO RULE FILE";
	}

	/*
	 * Fetch this state's ID
	 */
	@Override
	public int getID() {
		return ID;
	}

	/**
	 * Get rule file list
	 * @return Rule file list. null if directory doesn't exist.
	 */
	private String[] getRuleFileList() {
		File dir = new File("config/rule");

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir1, String name) {
				return name.endsWith(".rul");
			}
		};

		String[] list = dir.list(filter);

		if(!System.getProperty("os.name").startsWith("Windows")) {
			// Sort if not windows
			Arrays.sort(list);
		}

		return list;
	}

	/**
	 * Create rule entries
	 * @param filelist Rule file list
	 * @param currentStyle Current style
	 */
	private void createRuleEntries(String[] filelist, int currentStyle) {
		ruleEntries = new LinkedList<RuleEntry>();

		for(int i = 0; i < filelist.length; i++) {
			RuleEntry entry = new RuleEntry();

			File file = new File("config/rule/" + filelist[i]);
			entry.filename = filelist[i];
			entry.filepath = file.getPath();

			CustomProperties prop = new CustomProperties();
			try {
				FileInputStream in = new FileInputStream("config/rule/" + filelist[i]);
				prop.load(in);
				in.close();
				entry.rulename = prop.getProperty("0.ruleopt.strRuleName", "");
				entry.style = prop.getProperty("0.ruleopt.style", 0);
			} catch (Exception e) {
				entry.rulename = "";
				entry.style = -1;
			}

			if(entry.style == currentStyle) {
				ruleEntries.add(entry);
			}
		}
	}

	/**
	 * Get rule name list as String[]
	 * @return Rule name list
	 */
	private String[] extractRuleNameListFromRuleEntries() {
		String[] result = new String[ruleEntries.size()];

		for(int i = 0; i < ruleEntries.size(); i++) {
			result[i] = ruleEntries.get(i).rulename;
		}

		return result;
	}

	/*
	 * Called when entering this state
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		strFileList = getRuleFileList();
		createRuleEntries(strFileList, style);
		list = extractRuleNameListFromRuleEntries();
		maxCursor = list.length-1;

		if(style == 0) {
			strCurrentFileName = NullpoMinoSlick.propGlobal.getProperty(player + ".rulefile", "");
			strCurrentRuleName = NullpoMinoSlick.propGlobal.getProperty(player + ".rulename", "");
		} else {
			strCurrentFileName = NullpoMinoSlick.propGlobal.getProperty(player + ".rulefile." + style, "");
			strCurrentRuleName = NullpoMinoSlick.propGlobal.getProperty(player + ".rulename." + style, "");
		}

		cursor = 0;
		for(int i = 0; i < ruleEntries.size(); i++) {
			if(ruleEntries.get(i).filename.equals(strCurrentFileName)) {
				cursor = i;
			}
		}
	}

	/*
	 * State initialization
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
	}

	/*
	 * Draw the screen
	 */
	@Override
	protected void onRenderSuccess (GameContainer container, StateBasedGame game, Graphics graphics)  {
		String title = "SELECT " + (player + 1) + "P RULE (" + (cursor + 1) + "/" + (list.length) + ")";
		NormalFont.printFontGrid(1, 1, title, NormalFont.COLOR_ORANGE);

		NormalFont.printFontGrid(1, 25, "CURRENT:" + strCurrentRuleName.toUpperCase(), NormalFont.COLOR_BLUE);
		NormalFont.printFontGrid(9, 26, strCurrentFileName.toUpperCase(), NormalFont.COLOR_BLUE);

		NormalFont.printFontGrid(1, 28, "A:OK", NormalFont.COLOR_GREEN);
		NormalFont.printFontGrid(6, 28, "B:CANCEL", NormalFont.COLOR_GREEN);
	}

	/*
	 * Decide
	 */
	@Override
	protected boolean onDecide(GameContainer container, StateBasedGame game, int delta) {
		ResourceHolder.soundManager.play("decide");

		RuleEntry entry = ruleEntries.get(cursor);
		if(style == 0) {
			NullpoMinoSlick.propGlobal.setProperty(player + ".rule", entry.filepath);
			NullpoMinoSlick.propGlobal.setProperty(player + ".rulefile", entry.filename);
			NullpoMinoSlick.propGlobal.setProperty(player + ".rulename", entry.rulename);
		} else {
			NullpoMinoSlick.propGlobal.setProperty(player + ".rule." + style, entry.filepath);
			NullpoMinoSlick.propGlobal.setProperty(player + ".rulefile." + style, entry.filename);
			NullpoMinoSlick.propGlobal.setProperty(player + ".rulename." + style, entry.rulename);
		}

		NullpoMinoSlick.saveConfig();

		game.enterState(StateConfigRuleStyleSelect.ID);
		return true;
	}

	/*
	 * Cancel
	 */
	@Override
	protected boolean onCancel(GameContainer container, StateBasedGame game, int delta) {
		game.enterState(StateConfigRuleStyleSelect.ID);
		return true;
	}

	/**
	 * Rule entry
	 */
	private class RuleEntry {
		/** File name */
		public String filename;
		/** File path */
		public String filepath;
		/** Rule name */
		public String rulename;
		/** Game style */
		public int style;
	}
}
