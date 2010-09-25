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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.Arrays;

import mu.nu.nullpo.util.CustomProperties;
import sdljava.SDLException;
//import sdljava.event.MouseState;
//import sdljava.event.SDLEvent;
import sdljava.video.SDLSurface;

/**
 * ルール選択画面のステート
 */
public class StateConfigRuleSelectSDL extends DummyMenuScrollStateSDL {
	/** Number of rules shown at a time */
	public static final int PAGE_HEIGHT = 21;

	/** Player ID */
	public int player = 0;

	/** Game style */
	public int style = 0;

	/** 初期設定Mode */
	protected boolean firstSetupMode;

	/** ファイルパス一覧 */
	protected String[] strFilePathList;

	/** Rule name一覧 */
	protected String[] strRuleNameList;

	/** Current ルールファイル */
	protected String strCurrentFileName;

	/** Current Rule name */
	protected String strCurrentRuleName;

	public StateConfigRuleSelectSDL() {
		pageHeight = PAGE_HEIGHT;
		nullError = "RULE DIRECTORY NOT FOUND";
		emptyError = "NO RULE FILE";
	}

	/**
	 * ルールファイル一覧を取得
	 * @return ルールファイルのFilenameの配列。ディレクトリがないならnull
	 */
	protected String[] getRuleFileList() {
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
	 * 詳細情報を更新
	 */
	protected void updateDetails() {
		strFilePathList = new String[list.length];
		strRuleNameList = new String[list.length];

		for(int i = 0; i < list.length; i++) {
			File file = new File("config/rule/" + list[i]);
			strFilePathList[i] = file.getPath();

			CustomProperties prop = new CustomProperties();

			try {
				FileInputStream in = new FileInputStream("config/rule/" + list[i]);
				prop.load(in);
				in.close();
				strRuleNameList[i] = prop.getProperty("0.ruleopt.strRuleName", "");
			} catch (Exception e) {
				strRuleNameList[i] = "";
			}
		}
	}

	/*
	 * Called when entering this state
	 */
	@Override
	public void enter() throws SDLException {
		firstSetupMode = NullpoMinoSDL.propConfig.getProperty("option.firstSetupMode", true);

		list = getRuleFileList();
		maxCursor = list.length-1;
		updateDetails();

		if(style == 0) {
			strCurrentFileName = NullpoMinoSDL.propGlobal.getProperty(player + ".rulefile", "");
			strCurrentRuleName = NullpoMinoSDL.propGlobal.getProperty(player + ".rulename", "");
		} else {
			strCurrentFileName = NullpoMinoSDL.propGlobal.getProperty(player + ".rulefile." + style, "");
			strCurrentRuleName = NullpoMinoSDL.propGlobal.getProperty(player + ".rulename." + style, "");
		}

		cursor = 0;
		for(int i = 0; i < list.length; i++) {
			if(strCurrentFileName.equals(list[i])) {
				cursor = i;
			}
		}
	}

	/*
	 * 描画
	 */
	@Override
	protected void onRenderSuccess(SDLSurface screen) throws SDLException {
		String title = "SELECT " + (player + 1) + "P RULE (" + (cursor + 1) + "/" + (list.length) + ")";
		NormalFontSDL.printFontGrid(1, 1, title, NormalFontSDL.COLOR_ORANGE);

		NormalFontSDL.printFontGrid(1, 25, "CURRENT:" + strCurrentRuleName.toUpperCase(), NormalFontSDL.COLOR_BLUE);
		NormalFontSDL.printFontGrid(9, 26, strCurrentFileName.toUpperCase(), NormalFontSDL.COLOR_BLUE);

		NormalFontSDL.printFontGrid(1, 28, "A:OK", NormalFontSDL.COLOR_GREEN);
		if(!firstSetupMode)
			NormalFontSDL.printFontGrid(6, 28, "B:CANCEL", NormalFontSDL.COLOR_GREEN);
	}

	@Override
	protected boolean onDecide() throws SDLException {
		ResourceHolderSDL.soundManager.play("decide");
		NullpoMinoSDL.propConfig.setProperty("option.firstSetupMode", false);
		if(style == 0) {
			NullpoMinoSDL.propGlobal.setProperty(player + ".rule", strFilePathList[cursor]);
			NullpoMinoSDL.propGlobal.setProperty(player + ".rulefile", list[cursor]);
			NullpoMinoSDL.propGlobal.setProperty(player + ".rulename", strRuleNameList[cursor]);
		} else {
			NullpoMinoSDL.propGlobal.setProperty(player + ".rule." + style, strFilePathList[cursor]);
			NullpoMinoSDL.propGlobal.setProperty(player + ".rulefile." + style, list[cursor]);
			NullpoMinoSDL.propGlobal.setProperty(player + ".rulename." + style, strRuleNameList[cursor]);
		}
		NullpoMinoSDL.saveConfig();
		if(!firstSetupMode) NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_RULESTYLESELECT);
		else NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_TITLE);
		return true;
	}

	@Override
	protected boolean onCancel() throws SDLException {
		NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_RULESTYLESELECT);
		return true;
	}
}
