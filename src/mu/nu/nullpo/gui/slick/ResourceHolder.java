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

import java.awt.Color;

import mu.nu.nullpo.game.component.BGMStatus;

import org.apache.log4j.Logger;
import org.newdawn.slick.BigImage;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.ShadowEffect;
import org.newdawn.slick.openal.SoundStore;

/**
 * 画像や音声の管理をするクラス
 */
public class ResourceHolder {
	/** Log */
	static Logger log = Logger.getLogger(ResourceHolder.class);

	/** Backgroundのcount */
	public static final int BACKGROUND_MAX = 20;

	/** Number of images for block spatter animation during line clears */
	public static final int BLOCK_BREAK_MAX = 8;

	/** Number of image splits for block spatter animation during line clears */
	public static final int BLOCK_BREAK_SEGMENTS = 2;

	/** Number of gem block clear effects */
	public static final int PERASE_MAX = 7;

	/** Block images */
	public static Image imgBlock, imgBlockSmall, imgBlockBig;

	/** 特殊Block images - old */
	//public static Image imgSpBlock, imgSpBlockSmall, imgSpBlockBig;

	/** Regular font */
	public static Image imgFont, imgFontSmall;

	/** 小物画像 */
	//public static Image imgSprite;

	/** Title */
	public static Image imgTitle;

	/** Menu Background */
	public static Image imgMenu;

	/** Field frame */
	public static Image imgFrame;

	/** Field background */
	//public static Image imgFieldbg;

	/** Block spatter animation during line clears */
	public static Image[][] imgBreak;

	/** Effects for clearing gem blocks */
	public static Image[] imgPErase;

	/** プレイ中のBackground */
	public static Image[] imgPlayBG;

	/** TTF font */
	public static UnicodeFont ttfFont;

	/** Sound effects */
	public static SoundManager soundManager;

	/** BGM */
	public static Music[] bgm;

	/** Current BGM number */
	public static int bgmPlaying;

	/**
	 * 画像や音声を読み込み
	 * @throws SlickException Failed to load
	 */
	public static void load() throws SlickException {
		String skindir = NullpoMinoSlick.propConfig.getProperty("custom.skin.directory", "res");

		// 画像
		log.info("Loading Image");

		imgBlock = loadImage(skindir + "/graphics/block.png");
		imgBlockSmall = loadImage(skindir + "/graphics/block_small.png");
		imgBlockBig = loadImage(skindir + "/graphics/block_big.png");
		/* old special blocks
		 * imgSpBlock = loadImage(skindir + "/graphics/block_sp.png");
		 * imgSpBlockSmall = loadImage(skindir + "/graphics/block_sp_small.png");
		 * imgSpBlockBig = loadImage(skindir + "/graphics/block_sp_big.png");
		 */
		imgFont = loadImage(skindir + "/graphics/font.png");
		imgFontSmall = loadImage(skindir + "/graphics/font_small.png");
		imgTitle = loadImage(skindir + "/graphics/title.png");
		imgMenu = loadImage(skindir + "/graphics/menu.png");
		imgFrame = loadImage(skindir + "/graphics/frame.png");

		if(NullpoMinoSlick.propConfig.getProperty("option.showlineeffect", true) == true) {
			loadLineClearEffectImages();
		}
		if(NullpoMinoSlick.propConfig.getProperty("option.showbg", true) == true) {
			loadBackgroundImages();
		}

		// Font
		try {
			ttfFont = new UnicodeFont(skindir + "/font/font.ttf", 16, false, false);
			ttfFont.getEffects().add(new ShadowEffect(Color.black, 1, 1, 1));
			ttfFont.getEffects().add(new ColorEffect(java.awt.Color.white));
		} catch (Throwable e) {
			log.error("TTF Font load failed", e);
			ttfFont = null;
		}

		// Sound effects
		soundManager = new SoundManager();
		if(NullpoMinoSlick.propConfig.getProperty("option.se", true) == true) {
			try {
				SoundStore.get().init();
			} catch (Throwable e) {
				log.warn("Sound init failed", e);
			}

			log.info("Loading Sound Effect");
			soundManager.load("cursor", skindir + "/se/cursor.wav");
			soundManager.load("decide", skindir + "/se/decide.wav");
			soundManager.load("erase1", skindir + "/se/erase1.wav");
			soundManager.load("erase2", skindir + "/se/erase2.wav");
			soundManager.load("erase3", skindir + "/se/erase3.wav");
			soundManager.load("erase4", skindir + "/se/erase4.wav");
			soundManager.load("died", skindir + "/se/died.wav");
			soundManager.load("gameover", skindir + "/se/gameover.wav");
			soundManager.load("hold", skindir + "/se/hold.wav");
			soundManager.load("holdfail", skindir + "/se/holdfail.wav");
			soundManager.load("initialhold", skindir + "/se/initialhold.wav");
			soundManager.load("initialrotate", skindir + "/se/initialrotate.wav");
			soundManager.load("levelup", skindir + "/se/levelup.wav");
			soundManager.load("linefall", skindir + "/se/linefall.wav");
			soundManager.load("lock", skindir + "/se/lock.wav");
			soundManager.load("move", skindir + "/se/move.wav");
			soundManager.load("pause", skindir + "/se/pause.wav");
			soundManager.load("rotate", skindir + "/se/rotate.wav");
			soundManager.load("step", skindir + "/se/step.wav");
			soundManager.load("piece0", skindir + "/se/piece0.wav");
			soundManager.load("piece1", skindir + "/se/piece1.wav");
			soundManager.load("piece2", skindir + "/se/piece2.wav");
			soundManager.load("piece3", skindir + "/se/piece3.wav");
			soundManager.load("piece4", skindir + "/se/piece4.wav");
			soundManager.load("piece5", skindir + "/se/piece5.wav");
			soundManager.load("piece6", skindir + "/se/piece6.wav");
			soundManager.load("piece7", skindir + "/se/piece7.wav");
			soundManager.load("piece8", skindir + "/se/piece8.wav");
			soundManager.load("piece9", skindir + "/se/piece9.wav");
			soundManager.load("piece10", skindir + "/se/piece10.wav");
			soundManager.load("harddrop", skindir + "/se/harddrop.wav");
			soundManager.load("softdrop", skindir + "/se/softdrop.wav");
			soundManager.load("levelstop", skindir + "/se/levelstop.wav");
			soundManager.load("endingstart", skindir + "/se/endingstart.wav");
			soundManager.load("excellent", skindir + "/se/excellent.wav");
			soundManager.load("b2b_start", skindir + "/se/b2b_start.wav");
			soundManager.load("b2b_continue", skindir + "/se/b2b_continue.wav");
			soundManager.load("b2b_end", skindir + "/se/b2b_end.wav");
			soundManager.load("gradeup", skindir + "/se/gradeup.wav");
			soundManager.load("countdown", skindir + "/se/countdown.wav");
			soundManager.load("tspin0", skindir + "/se/tspin0.wav");
			soundManager.load("tspin1", skindir + "/se/tspin1.wav");
			soundManager.load("tspin2", skindir + "/se/tspin2.wav");
			soundManager.load("tspin3", skindir + "/se/tspin3.wav");
			soundManager.load("ready", skindir + "/se/ready.wav");
			soundManager.load("go", skindir + "/se/go.wav");
			soundManager.load("movefail", skindir + "/se/movefail.wav");
			soundManager.load("rotfail", skindir + "/se/rotfail.wav");
			soundManager.load("medal", skindir + "/se/medal.wav");
			soundManager.load("change", skindir + "/se/change.wav");
			soundManager.load("bravo", skindir + "/se/bravo.wav");
			soundManager.load("cool", skindir + "/se/cool.wav");
			soundManager.load("regret", skindir + "/se/regret.wav");
			soundManager.load("garbage", skindir + "/se/garbage.wav");
			soundManager.load("stageclear", skindir + "/se/stageclear.wav");
			soundManager.load("stagefail", skindir + "/se/stagefail.wav");
			soundManager.load("gem", skindir + "/se/gem.wav");
			soundManager.load("danger", skindir + "/se/danger.wav");
			soundManager.load("matchend", skindir + "/se/matchend.wav");
			soundManager.load("hurryup", skindir + "/se/hurryup.wav");
			soundManager.load("square_s", skindir + "/se/square_s.wav");
			soundManager.load("square_g", skindir + "/se/square_g.wav");

			for(int i = 0; i < 20; i++) {
				soundManager.load("combo" + (i + 1), skindir + "/se/combo" + (i + 1) + ".wav");
			}
		}

		// 音楽
		bgm = new Music[BGMStatus.BGM_COUNT];
		bgmPlaying = -1;

		if(NullpoMinoSlick.propConfig.getProperty("option.bgmpreload", false) == true) {
			for(int i = 0; i < BGMStatus.BGM_COUNT; i++) {
				bgmLoad(i, false);
			}
		}
	}

	/**
	 * Load background images.
	 */
	public static void loadBackgroundImages() {
		if(imgPlayBG == null) {
			imgPlayBG = new Image[BACKGROUND_MAX];

			String skindir = NullpoMinoSlick.propConfig.getProperty("custom.skin.directory", "res");
			for(int i = 0; i < imgPlayBG.length; i++)
				imgPlayBG[i] = loadImage(skindir + "/graphics/back" + i + ".png");
		}
	}

	/**
	 * Load line clear effect images.
	 */
	public static void loadLineClearEffectImages() {
		String skindir = NullpoMinoSlick.propConfig.getProperty("custom.skin.directory", "res");

		if(imgBreak == null) {
			imgBreak = new Image[BLOCK_BREAK_MAX][BLOCK_BREAK_SEGMENTS];

			for(int i = 0; i < BLOCK_BREAK_MAX; i++) {
				for(int j = 0; j < BLOCK_BREAK_SEGMENTS; j++) {
					imgBreak[i][j] = loadImage(skindir + "/graphics/break" + i + "_" + j + ".png");
				}
			}
		}
		if(imgPErase == null) {
			imgPErase = new Image[PERASE_MAX];

			for(int i = 0; i < imgPErase.length; i++) {
				imgPErase[i] = loadImage(skindir + "/graphics/perase" + i + ".png");
			}
		}
	}

	/**
	 * 画像読み込み
	 * @param filename Filename
	 * @return 画像 data
	 */
	public static Image loadImage(String filename) {
		log.debug("Loading image from " + filename);

		Image img = null;
		try {
			img = new Image(filename);
		} catch (Throwable e) {
			log.error("Failed to load image from " + filename, e);
			try {
				img = new Image(256, 256);
			} catch (Throwable e2) {}
		}

		return img;
	}

	/**
	 * 巨大画像を読み込み
	 * @param filename Filename
	 * @return 画像 data
	 */
	public static BigImage loadBigImage(String filename) {
		log.debug("Loading big image from " + filename);

		BigImage bigImg = null;
		try {
			bigImg = new BigImage(filename);
		} catch (Throwable e) {
			log.error("Failed to load big image from " + filename, e);
		}

		return bigImg;
	}

	/**
	 * 指定した numberのBGMをメモリ上に読み込み
	 * @param no BGM number
	 * @param showerr 例外が発生したときにコンソールに表示する
	 */
	public static void bgmLoad(int no, boolean showerr) {
		if(NullpoMinoSlick.propConfig.getProperty("option.bgm", false) == false) return;

		if(bgm[no] == null) {
			if(showerr) log.info("Loading BGM" + no);

			try {
				String filename = NullpoMinoSlick.propMusic.getProperty("music.filename." + no, null);
				if((filename == null) || (filename.length() < 1)) {
					if(showerr) log.info("BGM" + no + " not available");
					return;
				}

				boolean streaming = NullpoMinoSlick.propConfig.getProperty("option.bgmstreaming", true);

				bgm[no] = new Music(filename, streaming);

				if(!showerr) log.info("Loaded BGM" + no);
			} catch(Throwable e) {
				if(showerr)
					log.error("BGM " + no + " load failed", e);
				else
					log.warn("BGM " + no + " load failed");
			}
		}
	}

	/**
	 * 指定した numberのBGMを再生
	 * @param no BGM number
	 */
	public static void bgmStart(int no) {
		if(NullpoMinoSlick.propConfig.getProperty("option.bgm", false) == false) return;

		bgmStop();

		int bgmvolume = NullpoMinoSlick.propConfig.getProperty("option.bgmvolume", 128);
		NullpoMinoSlick.appGameContainer.setMusicVolume(bgmvolume / (float)128);

		if(no >= 0) {
			if(bgm[no] == null) {
				bgmLoad(no, true);
			}

			if(bgm[no] != null) {
				try {
					if(NullpoMinoSlick.propMusic.getProperty("music.noloop." + no, false) == true)
						bgm[no].play();
					else
						bgm[no].loop();
				} catch(Throwable e) {
					log.error("Failed to play music " + no, e);
				}
			}

			bgmPlaying = no;
		} else {
			bgmPlaying = -1;
		}
	}

	/**
	 * Current BGMを一時停止
	 */
	public static void bgmPause() {
		if(bgmPlaying >= 0) {
			if(bgm[bgmPlaying] != null) {
				bgm[bgmPlaying].pause();
			}
		}
	}

	/**
	 * 一時停止中のBGMを再開
	 */
	public static void bgmResume() {
		if(bgmPlaying >= 0) {
			if(bgm[bgmPlaying] != null) {
				bgm[bgmPlaying].resume();
			}
		}
	}

	/**
	 * BGM再生中かどうか
	 * @return 再生中ならtrue
	 */
	public static boolean bgmIsPlaying() {
		if(bgmPlaying >= 0) {
			if(bgm[bgmPlaying] != null) {
				return bgm[bgmPlaying].playing();
			}
		}

		return false;
	}

	/**
	 * BGMを停止
	 */
	public static void bgmStop() {
		for(int i = 0; i < BGMStatus.BGM_COUNT; i++) {
			if(bgm[i] != null) {
				bgm[i].pause();
				bgm[i].stop();
			}
		}
	}

	/**
	 * 全てのBGMをメモリから解放
	 */
	public static void bgmUnloadAll() {
		for(int i = 0; i < BGMStatus.BGM_COUNT; i++) {
			if(bgm[i] != null) {
				bgm[i].stop();
				if(NullpoMinoSlick.propConfig.getProperty("option.bgmpreload", false) == false)
					bgm[i] = null;
			}
		}
	}
}
