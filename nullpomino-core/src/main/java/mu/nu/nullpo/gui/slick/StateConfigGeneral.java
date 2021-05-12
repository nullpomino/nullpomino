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

import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * State of the general settings screen
 */
public class StateConfigGeneral extends BaseGameState {
	/** This state's ID */
	public static final int ID = 6;

	/** UI Text identifier Strings */
	protected static final String[] UI_TEXT = {
		"ConfigGeneral_SE",
		"ConfigGeneral_BGM",
		"ConfigGeneral_BGMPreload",
		"ConfigGeneral_SEVolume",
		"ConfigGeneral_BGMVolume",
		"ConfigGeneral_Background",
		"ConfigGeneral_UseBackgroundFade",
		"ConfigGeneral_ShowLineEffect",
		"ConfigGeneral_LineEffectSpeed",
		"ConfigGeneral_ShowMeter",
		"ConfigGeneral_DarkNextArea",
		"ConfigGeneral_NextShadow",
		"ConfigGeneral_NextType",
		"ConfigGeneral_OutlineGhost",
		"ConfigGeneral_FieldBGBright",
		"ConfigGeneral_ShowFieldBGGrid",
		"ConfigGeneral_ShowInput",
		"ConfigGeneral_Fullscreen",
		"ConfigGeneral_ShowFPS",
		"ConfigGeneral_MaxFPS",
		"ConfigGeneral_FrameStep",
		"ConfigGeneral_AlternateFPSPerfectMode",
		"ConfigGeneral_AlternateFPSPerfectYield",
		"ConfigGeneral_BGMStreaming",
		"ConfigGeneral_VSync",
		"ConfigGeneral_AlternateFPSTiming",
		"ConfigGeneral_AlternateFPSDynamicAdjust",
		"ConfigGeneral_ScreenSizeType",
	};

	/** Piece preview type options */
	protected static final String[] NEXTTYPE_OPTIONS = {"TOP", "SIDE(SMALL)", "SIDE(BIG)"};

	/** Screen size table */
	protected static final int[][] SCREENSIZE_TABLE =
	{
		{320,240}, {400,300}, {480,360}, {512,384}, {640,480}, {800,600}, {1024,768}, {1152,864}, {1280,960}, {1440,1080}, {1600,1200}, {2048,1536}, {2560,1920}, {2880,2160}
	};

	/** Cursor position */
	protected int cursor = 0;

	/** Full screen flag */
	protected boolean fullscreen;

	/** Sound effectsON/OFF */
	protected boolean se;

	/** BGMOfON/OFF */
	protected boolean bgm;

	/** BGMPreloading of */
	protected boolean bgmpreload;

	/** BGMStreamingON/OFF */
	protected boolean bgmstreaming;

	/** BackgroundDisplay */
	protected boolean showbg;

	/** FPSDisplay */
	protected boolean showfps;

	/**  frame Step is enabled */
	protected boolean enableframestep;

	/** MaximumFPS */
	protected int maxfps;

	/** Line clearDisplay Effects */
	protected boolean showlineeffect;

	/** Line clear effect speed */
	protected int lineeffectspeed;

	/** Heavy production use */
	protected boolean heavyeffect;

	/** fieldBackgroundThe brightness of the */
	protected int fieldbgbright;

	/** Show field BG grid */
	protected boolean showfieldbggrid;

	/** NEXTDarken the field */
	protected boolean darknextarea;

	/** Sound effects volume */
	protected int sevolume;

	/** BGM volume */
	protected int bgmvolume;

	/** fieldTo the rightMeterShow */
	protected boolean showmeter;

	/** Wait vsync */
	protected boolean vsync;

	/** ghost On top of the pieceNEXTDisplay */
	protected boolean nextshadow;

	/** Linear frameghost Peace */
	protected boolean outlineghost;

	/** Piece preview type (0=Top 1=Side small 2=Side big) */
	protected int nexttype;

	/** Timing of alternate FPS sleep (false=render true=update) */
	protected boolean alternateFPSTiming;

	/** Allow dynamic adjust of target FPS (as seen in Swing version) */
	protected boolean alternateFPSDynamicAdjust;

	/** Perfect FPS mode */
	protected boolean alternateFPSPerfectMode;

	/** Execute Thread.yield() during Perfect FPS mode */
	protected boolean alternateFPSPerfectYield;

	/** Screen size type */
	protected int screenSizeType;

	/** Show player input */
	protected boolean showInput;

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
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		loadConfig(NullpoMinoSlick.propConfig);
	}

	/**
	 * Load settings
	 * @param prop Property file to read from
	 */
	protected void loadConfig(CustomProperties prop) {
		fullscreen = prop.getProperty("option.fullscreen", false);
		se = prop.getProperty("option.se", true);
		bgm = prop.getProperty("option.bgm", false);
		bgmpreload = prop.getProperty("option.bgmpreload", false);
		bgmstreaming = prop.getProperty("option.bgmstreaming", true);
		showbg = prop.getProperty("option.showbg", true);
		showfps = prop.getProperty("option.showfps", true);
		enableframestep = prop.getProperty("option.enableframestep", false);
		maxfps = prop.getProperty("option.maxfps", 60);
		showlineeffect = prop.getProperty("option.showlineeffect", true);
		lineeffectspeed = prop.getProperty("option.lineeffectspeed", 0);
		heavyeffect = prop.getProperty("option.heavyeffect", false);
		if(prop.getProperty("option.fieldbgbright2") != null) {
			fieldbgbright = prop.getProperty("option.fieldbgbright2", 128);
		} else {
			fieldbgbright = prop.getProperty("option.fieldbgbright", 64) * 2;
			if(fieldbgbright > 255) fieldbgbright = 255;
		}
		showfieldbggrid = prop.getProperty("option.showfieldbggrid", true);
		darknextarea = prop.getProperty("option.darknextarea", true);
		sevolume = prop.getProperty("option.sevolume", 128);
		bgmvolume = prop.getProperty("option.bgmvolume", 128);
		showmeter = prop.getProperty("option.showmeter", true);
		vsync = prop.getProperty("option.vsync", true);
		nextshadow = prop.getProperty("option.nextshadow", false);
		outlineghost = prop.getProperty("option.outlineghost", false);
		showInput = prop.getProperty("option.showInput", false);
		nexttype = 0;
		if((prop.getProperty("option.sidenext", false) == true) && (prop.getProperty("option.bigsidenext", false) == false)) {
			nexttype = 1;
		} else if((prop.getProperty("option.sidenext", false) == true) && (prop.getProperty("option.bigsidenext", false) == true)) {
			nexttype = 2;
		}
		alternateFPSTiming = prop.getProperty("option.alternateFPSTiming", false);
		alternateFPSDynamicAdjust = prop.getProperty("option.alternateFPSDynamicAdjust", false);
		alternateFPSPerfectMode = prop.getProperty("option.alternateFPSPerfectMode", false);
		alternateFPSPerfectYield = prop.getProperty("option.alternateFPSPerfectYield", false);

		screenSizeType = 4;	// Default to 640x480
		int sWidth = prop.getProperty("option.screenwidth", -1);
		int sHeight = prop.getProperty("option.screenheight", -1);
		for(int i = 0; i < SCREENSIZE_TABLE.length; i++) {
			if((sWidth == SCREENSIZE_TABLE[i][0]) && (sHeight == SCREENSIZE_TABLE[i][1])) {
				screenSizeType = i;
				break;
			}
		}
	}

	/**
	 * Save settings
	 * @param prop Property file to save to
	 */
	protected void saveConfig(CustomProperties prop) {
		prop.setProperty("option.fullscreen", fullscreen);
		prop.setProperty("option.se", se);
		prop.setProperty("option.bgm", bgm);
		prop.setProperty("option.bgmpreload", bgmpreload);
		prop.setProperty("option.bgmstreaming", bgmstreaming);
		prop.setProperty("option.showbg", showbg);
		prop.setProperty("option.showfps", showfps);
		prop.setProperty("option.enableframestep", enableframestep);
		prop.setProperty("option.maxfps", maxfps);
		prop.setProperty("option.showlineeffect", showlineeffect);
		prop.setProperty("option.lineeffectspeed", lineeffectspeed);
		prop.setProperty("option.heavyeffect", heavyeffect);
		prop.setProperty("option.fieldbgbright2", fieldbgbright);
		prop.setProperty("option.showfieldbggrid", showfieldbggrid);
		prop.setProperty("option.darknextarea", darknextarea);
		prop.setProperty("option.sevolume", sevolume);
		prop.setProperty("option.bgmvolume", bgmvolume);
		prop.setProperty("option.showmeter", showmeter);
		prop.setProperty("option.vsync", vsync);
		prop.setProperty("option.nextshadow", nextshadow);
		prop.setProperty("option.outlineghost", outlineghost);
		prop.setProperty("option.showInput", showInput);
		if(nexttype == 0) {
			prop.setProperty("option.sidenext", false);
			prop.setProperty("option.bigsidenext", false);
		} else if(nexttype == 1) {
			prop.setProperty("option.sidenext", true);
			prop.setProperty("option.bigsidenext", false);
		} else if(nexttype == 2) {
			prop.setProperty("option.sidenext", true);
			prop.setProperty("option.bigsidenext", true);
		}
		prop.setProperty("option.alternateFPSTiming", alternateFPSTiming);
		prop.setProperty("option.alternateFPSDynamicAdjust", alternateFPSDynamicAdjust);
		prop.setProperty("option.alternateFPSPerfectMode", alternateFPSPerfectMode);
		prop.setProperty("option.alternateFPSPerfectYield", alternateFPSPerfectYield);

		if((screenSizeType >= 0) && (screenSizeType < SCREENSIZE_TABLE.length)) {
			prop.setProperty("option.screenwidth", SCREENSIZE_TABLE[screenSizeType][0]);
			prop.setProperty("option.screenheight", SCREENSIZE_TABLE[screenSizeType][1]);
		}
	}

	/*
	 * Draw the screen
	 */
	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// Background
		g.drawImage(ResourceHolderSlick.imgMenu, 0, 0);

		// Basic Options
		if(cursor < 17) {
			NormalFontSlick.printFontGrid(1, 1, "GENERAL OPTIONS: BASIC (1/3)", NormalFontSlick.COLOR_ORANGE);
			NormalFontSlick.printFontGrid(1, 3 + cursor, "b", NormalFontSlick.COLOR_RED);

			NormalFontSlick.printFontGrid(2,  3, "SE:" + GeneralUtil.getOorX(se), (cursor == 0));
			NormalFontSlick.printFontGrid(2,  4, "BGM:" + GeneralUtil.getOorX(bgm), (cursor == 1));
			NormalFontSlick.printFontGrid(2,  5, "BGM PRELOAD:" + GeneralUtil.getOorX(bgmpreload), (cursor == 2));
			NormalFontSlick.printFontGrid(2,  6, "SE VOLUME:" + sevolume + "("+ (sevolume * 100 / 128) + "%)", (cursor == 3));
			NormalFontSlick.printFontGrid(2,  7, "BGM VOLUME:" + bgmvolume + "(" + (bgmvolume * 100 / 128) + "%)", (cursor == 4));
			NormalFontSlick.printFontGrid(2,  8, "SHOW BACKGROUND:" + GeneralUtil.getOorX(showbg), (cursor == 5));
			NormalFontSlick.printFontGrid(2,  9, "USE BACKGROUND FADE:" + GeneralUtil.getOorX(heavyeffect), (cursor == 6));
			NormalFontSlick.printFontGrid(2, 10, "SHOW LINE EFFECT:" + GeneralUtil.getOorX(showlineeffect), (cursor == 7));
			NormalFontSlick.printFontGrid(2, 11, "LINE EFFECT SPEED:" + "X " + (lineeffectspeed+1), (cursor == 8));
			NormalFontSlick.printFontGrid(2, 12, "SHOW METER:" + GeneralUtil.getOorX(showmeter), (cursor == 9));
			NormalFontSlick.printFontGrid(2, 13, "DARK NEXT AREA:" + GeneralUtil.getOorX(darknextarea), (cursor == 10));
			NormalFontSlick.printFontGrid(2, 14, "SHOW NEXT ABOVE SHADOW:" + GeneralUtil.getOorX(nextshadow), (cursor == 11));
			NormalFontSlick.printFontGrid(2, 15, "NEXT DISPLAY TYPE:" + NEXTTYPE_OPTIONS[nexttype], (cursor == 12));
			NormalFontSlick.printFontGrid(2, 16, "OUTLINE GHOST PIECE:" + GeneralUtil.getOorX(outlineghost), (cursor == 13));
			NormalFontSlick.printFontGrid(2, 17, "FIELD BG BRIGHT:" + fieldbgbright + "(" + (fieldbgbright * 100 / 255) + "%)", (cursor == 14));
			NormalFontSlick.printFontGrid(2, 18, "SHOW FIELD BG GRID:" + GeneralUtil.getOorX(showfieldbggrid), (cursor == 15));
			NormalFontSlick.printFontGrid(2, 19, "SHOW CONTROLLER INPUT:" + GeneralUtil.getOorX(showInput), (cursor == 16));
		}
		// Advanced Options
		else if(cursor < 23) {
			NormalFontSlick.printFontGrid(1, 1, "GENERAL OPTIONS: ADVANCED (2/3)", NormalFontSlick.COLOR_ORANGE);
			NormalFontSlick.printFontGrid(1, 3 + (cursor - 17), "b", NormalFontSlick.COLOR_RED);

			NormalFontSlick.printFontGrid(2,  3, "FULLSCREEN:" + GeneralUtil.getOorX(fullscreen), (cursor == 17));
			NormalFontSlick.printFontGrid(2,  4, "SHOW FPS:" + GeneralUtil.getOorX(showfps), (cursor == 18));
			NormalFontSlick.printFontGrid(2,  5, "MAX FPS:" + maxfps, (cursor == 19));
			NormalFontSlick.printFontGrid(2,  6, "FRAME STEP:" + GeneralUtil.getOorX(enableframestep), (cursor == 20));
			NormalFontSlick.printFontGrid(2,  7, "FPS PERFECT MODE:" + GeneralUtil.getOorX(alternateFPSPerfectMode), (cursor == 21));
			NormalFontSlick.printFontGrid(2,  8, "FPS PERFECT YIELD:" + GeneralUtil.getOorX(alternateFPSPerfectYield), (cursor == 22));
		}
		// Slick Options
		else {
			NormalFontSlick.printFontGrid(1, 1, "GENERAL OPTIONS: SLICK (3/3)", NormalFontSlick.COLOR_ORANGE);
			NormalFontSlick.printFontGrid(1, 3 + (cursor - 23), "b", NormalFontSlick.COLOR_RED);

			NormalFontSlick.printFontGrid(2,  3, "BGM STREAMING:" + GeneralUtil.getOorX(bgmstreaming), (cursor == 23));
			NormalFontSlick.printFontGrid(2,  4, "VSYNC:" + GeneralUtil.getOorX(vsync), (cursor == 24));
			NormalFontSlick.printFontGrid(2,  5, "FPS SLEEP TIMING:" + (alternateFPSTiming ? "UPDATE" : "RENDER"), (cursor == 25));
			NormalFontSlick.printFontGrid(2,  6, "FPS DYNAMIC ADJUST:" + GeneralUtil.getOorX(alternateFPSDynamicAdjust), (cursor == 26));
			NormalFontSlick.printFontGrid(2,  7, "SCREEN SIZE:" + SCREENSIZE_TABLE[screenSizeType][0] + "e" + SCREENSIZE_TABLE[screenSizeType][1],
									 (cursor == 27));
		}

		if((cursor >= 0) && (cursor < UI_TEXT.length)) NormalFontSlick.printTTFFont(16, 432, NullpoMinoSlick.getUIText(UI_TEXT[cursor]));
	}

	/*
	 * Update game state
	 */
	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		// TTF font
		if(ResourceHolderSlick.ttfFont != null) ResourceHolderSlick.ttfFont.loadGlyphs();

		// Update key input states
		GameKeySlick.gamekey[0].update(container.getInput());

		// Cursor movement
		if(GameKeySlick.gamekey[0].isMenuRepeatKey(GameKeySlick.BUTTON_UP)) {
		    cursor--;
			if(cursor < 0) cursor = 27;
			ResourceHolderSlick.soundManager.play("cursor");
		}
		if(GameKeySlick.gamekey[0].isMenuRepeatKey(GameKeySlick.BUTTON_DOWN)) {
			cursor++;
			if(cursor > 27) cursor = 0;
			ResourceHolderSlick.soundManager.play("cursor");
		}

		// Configuration changes
		int change = 0;
		if(GameKeySlick.gamekey[0].isMenuRepeatKey(GameKeySlick.BUTTON_LEFT)) change = -1;
		if(GameKeySlick.gamekey[0].isMenuRepeatKey(GameKeySlick.BUTTON_RIGHT)) change = 1;

		if(change != 0) {
			ResourceHolderSlick.soundManager.play("change");

			switch(cursor) {
			case 0:
				se = !se;
				break;
			case 1:
				bgm = !bgm;
				break;
			case 2:
				bgmpreload = !bgmpreload;
				break;
			case 3:
				sevolume += change;
				if(sevolume < 0) sevolume = 128;
				if(sevolume > 128) sevolume = 0;
				break;
			case 4:
				bgmvolume += change;
				if(bgmvolume < 0) bgmvolume = 128;
				if(bgmvolume > 128) bgmvolume = 0;
				break;
			case 5:
				showbg = !showbg;
				break;
			case 6:
				heavyeffect = !heavyeffect;
				break;
			case 7:
				showlineeffect = !showlineeffect;
				break;
			case 8:
				lineeffectspeed += change;
				if(lineeffectspeed < 0) lineeffectspeed = 9;
				if(lineeffectspeed > 9) lineeffectspeed = 0;
				break;
			case 9:
				showmeter = !showmeter;
				break;
			case 10:
				darknextarea = !darknextarea;
				break;
			case 11:
				nextshadow = !nextshadow;
				break;
			case 12:
				nexttype += change;
				if(nexttype < 0) nexttype = 2;
				if(nexttype > 2) nexttype = 0;
				break;
			case 13:
				outlineghost = !outlineghost;
				break;
			case 14:
				fieldbgbright += change;
				if(fieldbgbright < 0) fieldbgbright = 255;
				if(fieldbgbright > 255) fieldbgbright = 0;
				break;
			case 15:
				showfieldbggrid = !showfieldbggrid;
				break;
			case 16:
				showInput = !showInput;
				break;
			case 17:
				fullscreen = !fullscreen;
				break;
			case 18:
				showfps = !showfps;
				break;
			case 19:
				maxfps += change;
				if(maxfps < 0) maxfps = 99;
				if(maxfps > 99) maxfps = 0;
				break;
			case 20:
				enableframestep = !enableframestep;
				break;
			case 21:
				alternateFPSPerfectMode = !alternateFPSPerfectMode;
				break;
			case 22:
				alternateFPSPerfectYield = !alternateFPSPerfectYield;
				break;
			case 23:
				bgmstreaming = !bgmstreaming;
				break;
			case 24:
				vsync = !vsync;
				break;
			case 25:
				alternateFPSTiming = !alternateFPSTiming;
				break;
			case 26:
				alternateFPSDynamicAdjust = !alternateFPSDynamicAdjust;
				break;
			case 27:
				screenSizeType += change;
				if(screenSizeType < 0) screenSizeType = SCREENSIZE_TABLE.length - 1;
				if(screenSizeType > SCREENSIZE_TABLE.length - 1) screenSizeType = 0;
				break;
			}
		}

		// Confirm button
		if(GameKeySlick.gamekey[0].isPushKey(GameKeySlick.BUTTON_A)) {
			ResourceHolderSlick.soundManager.play("decide");
			saveConfig(NullpoMinoSlick.propConfig);
			NullpoMinoSlick.saveConfig();
			NullpoMinoSlick.setGeneralConfig();
			if(showlineeffect) ResourceHolderSlick.loadLineClearEffectImages();
			if(showbg) ResourceHolderSlick.loadBackgroundImages();
			game.enterState(StateConfigMainMenu.ID);
		}

		// Cancel button
		if(GameKeySlick.gamekey[0].isPushKey(GameKeySlick.BUTTON_B)) {
		    loadConfig(NullpoMinoSlick.propConfig);
			game.enterState(StateConfigMainMenu.ID);
		}
	}
}
