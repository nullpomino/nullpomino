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
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * 全般の設定画面のステート
 */
public class StateConfigGeneral extends BasicGameState {
	/** This state's ID */
	public static final int ID = 6;

	/** Joystick input 検出法の表示名 */
	protected static final String[] JOYSTICK_METHOD_STRINGS = {"NONE", "SLICK DEFAULT", "SLICK ALTERNATE", "LWJGL"};

	/** Screenshot撮影 flag */
	protected boolean ssflag = false;

	/** Cursor position */
	protected int cursor = 0;

	/** フルスクリーン flag */
	protected boolean fullscreen;

	/** Sound effectsON/OFF */
	protected boolean se;

	/** BGMのON/OFF */
	protected boolean bgm;

	/** BGMの事前読み込み */
	protected boolean bgmpreload;

	/** BGMストリーミングのON/OFF */
	protected boolean bgmstreaming;

	/** Background表示 */
	protected boolean showbg;

	/** FPS表示 */
	protected boolean showfps;

	/**  frame ステップ is enabled */
	protected boolean enableframestep;

	/** MaximumFPS */
	protected int maxfps;

	/** Line clearエフェクト表示 */
	protected boolean showlineeffect;

	/** 重い演出を使う */
	protected boolean heavyeffect;

	/** fieldBackgroundの明るさ */
	protected int fieldbgbright;

	/** NEXT欄を暗くする */
	protected boolean darknextarea;

	/** Sound effects volume */
	protected int sevolume;

	/** BGM volume */
	protected int bgmvolume;

	/** Joystick input の検出法 */
	protected int joyMethod;

	/** field右側にMeterを表示 */
	protected boolean showmeter;

	/** 垂直同期を待つ */
	protected boolean vsync;

	/** ghost ピースの上にNEXT表示 */
	protected boolean nextshadow;

	/** 枠線型ghost ピース */
	protected boolean outlineghost;

	/** Side piece preview */
	protected boolean sidenext;

	/** Timing of alternate FPS sleep (false=render true=update) */
	protected boolean alternateFPSTiming;

	/** Allow dynamic adjust of target FPS (as seen in Swing version) */
	protected boolean alternateFPSDynamicAdjust;

	/** Perfect FPS mode */
	protected boolean alternateFPSPerfectMode;

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
		heavyeffect = prop.getProperty("option.heavyeffect", false);
		fieldbgbright = prop.getProperty("option.fieldbgbright", 64);
		darknextarea = prop.getProperty("option.darknextarea", true);
		sevolume = prop.getProperty("option.sevolume", 128);
		bgmvolume = prop.getProperty("option.bgmvolume", 128);
		joyMethod = prop.getProperty("option.joymethod", ControllerManager.CONTROLLER_METHOD_SLICK_DEFAULT);
		showmeter = prop.getProperty("option.showmeter", true);
		vsync = prop.getProperty("option.vsync", false);
		nextshadow = prop.getProperty("option.nextshadow", false);
		outlineghost = prop.getProperty("option.outlineghost", false);
		sidenext = prop.getProperty("option.sidenext", false);
		alternateFPSTiming = prop.getProperty("option.alternateFPSTiming", true);
		alternateFPSDynamicAdjust = prop.getProperty("option.alternateFPSDynamicAdjust", false);
		alternateFPSPerfectMode = prop.getProperty("option.alternateFPSPerfectMode", false);
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
		prop.setProperty("option.heavyeffect", heavyeffect);
		prop.setProperty("option.fieldbgbright", fieldbgbright);
		prop.setProperty("option.darknextarea", darknextarea);
		prop.setProperty("option.sevolume", sevolume);
		prop.setProperty("option.bgmvolume", bgmvolume);
		prop.setProperty("option.joymethod", joyMethod);
		prop.setProperty("option.showmeter", showmeter);
		prop.setProperty("option.vsync", vsync);
		prop.setProperty("option.nextshadow", nextshadow);
		prop.setProperty("option.outlineghost", outlineghost);
		prop.setProperty("option.sidenext", sidenext);
		prop.setProperty("option.alternateFPSTiming", alternateFPSTiming);
		prop.setProperty("option.alternateFPSDynamicAdjust", alternateFPSDynamicAdjust);
		prop.setProperty("option.alternateFPSPerfectMode", alternateFPSPerfectMode);
	}

	/*
	 * Draw the screen
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// Background
		g.drawImage(ResourceHolder.imgMenu, 0, 0);

		// Menu
		NormalFont.printFontGrid(1, 1, "GENERAL OPTIONS", NormalFont.COLOR_ORANGE);

		NormalFont.printFontGrid(1, 3 + cursor, "b", NormalFont.COLOR_RED);

		NormalFont.printFontGrid(2,  3, "FULLSCREEN:" + GeneralUtil.getOorX(fullscreen), (cursor == 0));
		NormalFont.printFontGrid(2,  4, "SE:" + GeneralUtil.getOorX(se), (cursor == 1));
		NormalFont.printFontGrid(2,  5, "BGM:" + GeneralUtil.getOorX(bgm), (cursor == 2));
		NormalFont.printFontGrid(2,  6, "BGM PRELOAD:" + GeneralUtil.getOorX(bgmpreload), (cursor == 3));
		NormalFont.printFontGrid(2,  7, "BGM STREAMING:" + GeneralUtil.getOorX(bgmstreaming), (cursor == 4));
		NormalFont.printFontGrid(2,  8, "SHOW BACKGROUND:" + GeneralUtil.getOorX(showbg), (cursor == 5));
		NormalFont.printFontGrid(2,  9, "SHOW FPS:" + GeneralUtil.getOorX(showfps), (cursor == 6));
		NormalFont.printFontGrid(2, 10, "FRAME STEP:" + GeneralUtil.getOorX(enableframestep), (cursor == 7));
		NormalFont.printFontGrid(2, 11, "MAX FPS:" + maxfps, (cursor == 8));
		NormalFont.printFontGrid(2, 12, "SHOW LINE EFFECT:" + GeneralUtil.getOorX(showlineeffect), (cursor == 9));
		NormalFont.printFontGrid(2, 13, "USE BACKGROUND FADE:" + GeneralUtil.getOorX(heavyeffect), (cursor == 10));
		NormalFont.printFontGrid(2, 14, "FIELD BG BRIGHT:" + fieldbgbright, (cursor == 11));
		NormalFont.printFontGrid(2, 15, "DARK NEXT AREA:" + GeneralUtil.getOorX(darknextarea), (cursor == 12));
		NormalFont.printFontGrid(2, 16, "SE VOLUME:" + sevolume, (cursor == 13));
		NormalFont.printFontGrid(2, 17, "BGM VOLUME:" + bgmvolume, (cursor == 14));
		NormalFont.printFontGrid(2, 18, "JOYSTICK METHOD:" + JOYSTICK_METHOD_STRINGS[joyMethod], (cursor == 15));
		NormalFont.printFontGrid(2, 19, "SHOW METER:" + GeneralUtil.getOorX(showmeter), (cursor == 16));
		NormalFont.printFontGrid(2, 20, "VSYNC:" + GeneralUtil.getOorX(vsync), (cursor == 17));
		NormalFont.printFontGrid(2, 21, "SHOW NEXT ABOVE SHADOW:" + GeneralUtil.getOorX(nextshadow), (cursor == 18));
		NormalFont.printFontGrid(2, 22, "OUTLINE GHOST PIECE:" + GeneralUtil.getOorX(outlineghost), (cursor == 19));
		NormalFont.printFontGrid(2, 23, "SHOW NEXT ON SIDE:" + GeneralUtil.getOorX(sidenext), (cursor == 20));
		NormalFont.printFontGrid(2, 24, "FPS SLEEP TIMING:" + (alternateFPSTiming ? "UPDATE" : "RENDER"), (cursor == 21));
		NormalFont.printFontGrid(2, 25, "FPS DYNAMIC ADJUST:" + GeneralUtil.getOorX(alternateFPSDynamicAdjust), (cursor == 22));
		NormalFont.printFontGrid(2, 26, "FPS PERFECT MODE:" + GeneralUtil.getOorX(alternateFPSPerfectMode), (cursor == 23));

		if(cursor == 0) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_Fullscreen"));
		if(cursor == 1) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_SE"));
		if(cursor == 2) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_BGM"));
		if(cursor == 3) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_BGMPreload"));
		if(cursor == 4) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_BGMStreaming"));
		if(cursor == 5) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_Background"));
		if(cursor == 6) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_ShowFPS"));
		if(cursor == 7) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_FrameStep"));
		if(cursor == 8) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_MaxFPS"));
		if(cursor == 9) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_ShowLineEffect"));
		if(cursor == 10) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_UseBackgroundFade"));
		if(cursor == 11) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_FieldBGBright"));
		if(cursor == 12) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_DarkNextArea"));
		if(cursor == 13) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_SEVolume"));
		if(cursor == 14) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_BGMVolume"));
		if(cursor == 15) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_JoyMethod"));
		if(cursor == 16) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_ShowMeter"));
		if(cursor == 17) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_VSync"));
		if(cursor == 18) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_NextShadow"));
		if(cursor == 19) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_OutlineGhost"));
		if(cursor == 20) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_SideNext"));
		if(cursor == 21) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_AlternateFPSTiming"));
		if(cursor == 22) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_AlternateFPSDynamicAdjust"));
		if(cursor == 23) NormalFont.printTTFFont(16, 432, NullpoMinoSlick.getUIText("ConfigGeneral_AlternateFPSPerfectMode"));

		// FPS
		NullpoMinoSlick.drawFPS(container);
		// Observer
		NullpoMinoSlick.drawObserverClient();
		// Screenshot
		if(ssflag) {
			NullpoMinoSlick.saveScreenShot(container, g);
			ssflag = false;
		}

		if(!NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}

	/*
	 * Update game state
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(!container.hasFocus()) {
			GameKey.gamekey[0].clear();
			if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
			return;
		}

		// TTF font 描画
		if(ResourceHolder.ttfFont != null) ResourceHolder.ttfFont.loadGlyphs();

		// Update key input states
		GameKey.gamekey[0].update(container.getInput());

		// Cursor movement
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_UP)) {
		    cursor--;
			if(cursor < 0) cursor = 23;
			ResourceHolder.soundManager.play("cursor");
		}
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_DOWN)) {
			cursor++;
			if(cursor > 23) cursor = 0;
			ResourceHolder.soundManager.play("cursor");
		}

		// Configuration changes
		int change = 0;
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_LEFT)) change = -1;
		if(GameKey.gamekey[0].isMenuRepeatKey(GameKey.BUTTON_RIGHT)) change = 1;

		if(change != 0) {
			ResourceHolder.soundManager.play("change");

			switch(cursor) {
				case 0:
					fullscreen = !fullscreen;
					break;
				case 1:
					se = !se;
					break;
				case 2:
					bgm = !bgm;
					break;
				case 3:
					bgmpreload = !bgmpreload;
					break;
				case 4:
					bgmstreaming = !bgmstreaming;
					break;
				case 5:
					showbg = !showbg;
					break;
				case 6:
					showfps = !showfps;
					break;
				case 7:
					enableframestep = !enableframestep;
					break;
				case 8:
					maxfps += change;
					if(maxfps < 0) maxfps = 99;
					if(maxfps > 99) maxfps = 0;
					break;
				case 9:
					showlineeffect = !showlineeffect;
					break;
				case 10:
					heavyeffect = !heavyeffect;
					break;
				case 11:
					fieldbgbright += change;
					if(fieldbgbright < 0) fieldbgbright = 128;
					if(fieldbgbright > 128) fieldbgbright = 0;
					break;
				case 12:
					darknextarea = !darknextarea;
					break;
				case 13:
					sevolume += change;
					if(sevolume < 0) sevolume = 128;
					if(sevolume > 128) sevolume = 0;
					break;
				case 14:
					bgmvolume += change;
					if(bgmvolume < 0) bgmvolume = 128;
					if(bgmvolume > 128) bgmvolume = 0;
					break;
				case 15:
					joyMethod += change;
					if(joyMethod < 0) joyMethod = ControllerManager.CONTROLLER_METHOD_MAX - 1;
					if(joyMethod > ControllerManager.CONTROLLER_METHOD_MAX - 1) joyMethod = 0;
					break;
				case 16:
					showmeter = !showmeter;
					break;
				case 17:
					vsync = !vsync;
					break;
				case 18:
					nextshadow = !nextshadow;
					break;
				case 19:
					outlineghost = !outlineghost;
					break;
				case 20:
					sidenext = !sidenext;
					break;
				case 21:
					alternateFPSTiming = !alternateFPSTiming;
					break;
				case 22:
					alternateFPSDynamicAdjust = !alternateFPSDynamicAdjust;
					break;
				case 23:
					alternateFPSPerfectMode = !alternateFPSPerfectMode;
					break;
			}
		}

		// 決定 button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_A)) {
			ResourceHolder.soundManager.play("decide");
			saveConfig(NullpoMinoSlick.propConfig);
			NullpoMinoSlick.saveConfig();
			NullpoMinoSlick.setGeneralConfig();
			if(showlineeffect) ResourceHolder.loadLineClearEffectImages();
			if(showbg) ResourceHolder.loadBackgroundImages();
			game.enterState(StateConfigMainMenu.ID);
		}

		// Cancel button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_B)) {
		    loadConfig(NullpoMinoSlick.propConfig);
			game.enterState(StateConfigMainMenu.ID);
		}

		// Screenshot button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_SCREENSHOT)) ssflag = true;

		// Exit button
		if(GameKey.gamekey[0].isPushKey(GameKey.BUTTON_QUIT)) container.exit();

		if(NullpoMinoSlick.alternateFPSTiming) NullpoMinoSlick.alternateFPSSleep();
	}
}
