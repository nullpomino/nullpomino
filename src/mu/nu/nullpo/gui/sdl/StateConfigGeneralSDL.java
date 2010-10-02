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

import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

import sdljava.SDLException;
import sdljava.video.SDLSurface;

/**
 * 全般の設定画面のステート
 */
public class StateConfigGeneralSDL extends BaseStateSDL {
	/** Cursor position */
	protected int cursor;

	/** フルスクリーン flag */
	protected boolean fullscreen;

	/** Sound effectsON/OFF */
	protected boolean se;

	/** BGMのON/OFF */
	protected boolean bgm;

	/** BGMの事前読み込み */
	protected boolean bgmpreload;

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

	/** サウンドバッファサイズ */
	protected int soundbuffer;

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

	/** 同時再生できるSound effectsのcount */
	protected int soundChannels;

	/** field右側にMeterを表示 */
	protected boolean showmeter;

	/** ghost ピースの上にNEXT表示 */
	protected boolean nextshadow;

	/** 枠線型ghost ピース */
	protected boolean outlineghost;

	/** Side piece preview */
	protected boolean sidenext;

	/**
	 * Constructor
	 */
	public StateConfigGeneralSDL() {
		cursor = 0;
		loadConfig(NullpoMinoSDL.propConfig);
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
		showbg = prop.getProperty("option.showbg", true);
		showfps = prop.getProperty("option.showfps", true);
		enableframestep = prop.getProperty("option.enableframestep", false);
		maxfps = prop.getProperty("option.maxfps", 60);
		showlineeffect = prop.getProperty("option.showlineeffect", true);
		soundbuffer = prop.getProperty("option.soundbuffer", 1024);
		heavyeffect = prop.getProperty("option.heavyeffect", false);
		fieldbgbright = prop.getProperty("option.fieldbgbright", 128);
		darknextarea = prop.getProperty("option.darknextarea", true);
		sevolume = prop.getProperty("option.sevolume", 128);
		bgmvolume = prop.getProperty("option.bgmvolume", 128);
		soundChannels = prop.getProperty("option.soundChannels", 15);
		showmeter = prop.getProperty("option.showmeter", true);
		nextshadow = prop.getProperty("option.nextshadow", false);
		outlineghost = prop.getProperty("option.outlineghost", false);
		sidenext = prop.getProperty("option.sidenext", false);
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
		prop.setProperty("option.showbg", showbg);
		prop.setProperty("option.showfps", showfps);
		prop.setProperty("option.enableframestep", enableframestep);
		prop.setProperty("option.maxfps", maxfps);
		prop.setProperty("option.showlineeffect", showlineeffect);
		prop.setProperty("option.soundbuffer", soundbuffer);
		prop.setProperty("option.heavyeffect", heavyeffect);
		prop.setProperty("option.fieldbgbright", fieldbgbright);
		prop.setProperty("option.darknextarea", darknextarea);
		prop.setProperty("option.sevolume", sevolume);
		prop.setProperty("option.bgmvolume", bgmvolume);
		prop.setProperty("option.soundChannels", soundChannels);
		prop.setProperty("option.showmeter", showmeter);
		prop.setProperty("option.nextshadow", nextshadow);
		prop.setProperty("option.outlineghost", outlineghost);
		prop.setProperty("option.sidenext", sidenext);
	}

	/*
	 * Draw the game screen
	 */
	@Override
	public void render(SDLSurface screen) throws SDLException {
		ResourceHolderSDL.imgMenu.blitSurface(screen);

		NormalFontSDL.printFontGrid(1, 1, "GENERAL OPTIONS", NormalFontSDL.COLOR_ORANGE);

		NormalFontSDL.printFontGrid(1, 3 + cursor, "b", NormalFontSDL.COLOR_RED);

		NormalFontSDL.printFontGrid(2, 3, "FULLSCREEN:" + GeneralUtil.getOorX(fullscreen), (cursor == 0));
		NormalFontSDL.printFontGrid(2, 4, "SE:" + GeneralUtil.getOorX(se), (cursor == 1));
		NormalFontSDL.printFontGrid(2, 5, "BGM:" + GeneralUtil.getOorX(bgm), (cursor == 2));
		NormalFontSDL.printFontGrid(2, 6, "BGM PRELOAD:" + GeneralUtil.getOorX(bgmpreload), (cursor == 3));
		NormalFontSDL.printFontGrid(2, 7, "SHOW BACKGROUND:" + GeneralUtil.getOorX(showbg), (cursor == 4));
		NormalFontSDL.printFontGrid(2, 8, "SHOW FPS:" + GeneralUtil.getOorX(showfps), (cursor == 5));
		NormalFontSDL.printFontGrid(2, 9, "FRAME STEP:" + GeneralUtil.getOorX(enableframestep), (cursor == 6));
		NormalFontSDL.printFontGrid(2, 10, "MAX FPS:" + maxfps, (cursor == 7));
		NormalFontSDL.printFontGrid(2, 11, "SHOW LINE EFFECT:" + GeneralUtil.getOorX(showlineeffect), (cursor == 8));
		NormalFontSDL.printFontGrid(2, 12, "SOUND BUFFER SIZE:" + soundbuffer, (cursor == 9));
		NormalFontSDL.printFontGrid(2, 13, "USE BACKGROUND FADE:" + GeneralUtil.getOorX(heavyeffect), (cursor == 10));
		NormalFontSDL.printFontGrid(2, 14, "FIELD BG BRIGHT:" + fieldbgbright, (cursor == 11));
		NormalFontSDL.printFontGrid(2, 15, "DARK NEXT AREA:" + GeneralUtil.getOorX(darknextarea), (cursor == 12));
		NormalFontSDL.printFontGrid(2, 16, "SE VOLUME:" + sevolume, (cursor == 13));
		NormalFontSDL.printFontGrid(2, 17, "BGM VOLUME:" + bgmvolume, (cursor == 14));
		NormalFontSDL.printFontGrid(2, 18, "MAX SOUND CHANNELS:" + soundChannels, (cursor == 15));
		NormalFontSDL.printFontGrid(2, 19, "SHOW METER:" + GeneralUtil.getOorX(showmeter), (cursor == 16));
		NormalFontSDL.printFontGrid(2, 20, "SHOW NEXT ABOVE SHADOW:" + GeneralUtil.getOorX(nextshadow), (cursor == 17));
		NormalFontSDL.printFontGrid(2, 21, "OUTLINE GHOST PIECE:" + GeneralUtil.getOorX(outlineghost), (cursor == 18));
		NormalFontSDL.printFontGrid(2, 22, "SHOW NEXT ON SIDE:" + GeneralUtil.getOorX(sidenext), (cursor == 19));

		if(cursor == 0) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_Fullscreen"));
		if(cursor == 1) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_SE"));
		if(cursor == 2) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_BGM"));
		if(cursor == 3) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_BGMPreload"));
		if(cursor == 4) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_Background"));
		if(cursor == 5) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_ShowFPS"));
		if(cursor == 6) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_FrameStep"));
		if(cursor == 7) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_MaxFPS"));
		if(cursor == 8) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_ShowLineEffect"));
		if(cursor == 9) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_SoundBufferSize"));
		if(cursor == 10) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_UseBackgroundFade"));
		if(cursor == 11) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_FieldBGBright"));
		if(cursor == 12) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_DarkNextArea"));
		if(cursor == 13) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_SEVolume"));
		if(cursor == 14) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_BGMVolume"));
		if(cursor == 15) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_SoundChannels"));
		if(cursor == 16) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_ShowMeter"));
		if(cursor == 17) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_NextShadow"));
		if(cursor == 18) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_OutlineGhost"));
		if(cursor == 19) NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText("ConfigGeneral_SideNext"));
	}

	/*
	 * Update game state
	 */
	@Override
	public void update() throws SDLException {
		// Cursor movement
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_UP)) {
			cursor--;
			if(cursor < 0) cursor = 19;
			ResourceHolderSDL.soundManager.play("cursor");
		}
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_DOWN)) {
			cursor++;
			if(cursor > 19) cursor = 0;
			ResourceHolderSDL.soundManager.play("cursor");
		}

		// Configuration changes
		int change = 0;
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_LEFT)) change = -1;
		if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_RIGHT)) change = 1;

		if(change != 0) {
			ResourceHolderSDL.soundManager.play("change");

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
					showbg = !showbg;
					break;
				case 5:
					showfps = !showfps;
					break;
				case 6:
					enableframestep = !enableframestep;
					break;
				case 7:
					maxfps += change;
					if(maxfps < 0) maxfps = 99;
					if(maxfps > 99) maxfps = 0;
					break;
				case 8:
					showlineeffect = !showlineeffect;
					break;
				case 9:
					soundbuffer += change * 256;
					if(soundbuffer < 0) soundbuffer = 65535;
					if(soundbuffer > 65535) soundbuffer = 0;
					break;
				case 10:
					heavyeffect = !heavyeffect;
					break;
				case 11:
					fieldbgbright += change;
					if(fieldbgbright < 0) fieldbgbright = 255;
					if(fieldbgbright > 255) fieldbgbright = 0;
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
					soundChannels += change;
					if(soundChannels < 0) soundChannels = 50;
					if(soundChannels > 50) soundChannels = 0;
					break;
				case 16:
					showmeter = !showmeter;
					break;
				case 17:
					nextshadow = !nextshadow;
					break;
				case 18:
					outlineghost = !outlineghost;
					break;
				case 19:
					sidenext = !sidenext;
					break;
			}
		}

		// 決定 button
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_A)) {
			ResourceHolderSDL.soundManager.play("decide");

			saveConfig(NullpoMinoSDL.propConfig);
			NullpoMinoSDL.saveConfig();

			NullpoMinoSDL.showfps = showfps;
			NullpoMinoSDL.maxFPS = maxfps;

			if(showlineeffect) ResourceHolderSDL.loadLineClearEffectImages();
			if(showbg) ResourceHolderSDL.loadBackgroundImages();

			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
		}

		// Cancel button
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_B)) {
			loadConfig(NullpoMinoSDL.propConfig);
			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
		}
	}
}
