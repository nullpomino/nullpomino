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

import mu.nu.nullpo.game.component.BGMStatus;

import org.apache.log4j.Logger;

import sdljava.SDLException;
import sdljava.image.SDLImage;
import sdljava.mixer.MixMusic;
import sdljava.mixer.SDLMixer;
import sdljava.ttf.SDLTTF;
import sdljava.ttf.SDLTrueTypeFont;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

/**
 * 画像や音声の管理をするクラス
 */
public class ResourceHolderSDL {
	/** Log */
	static Logger log = Logger.getLogger(ResourceHolderSDL.class);

	/** 背景のcount */
	public static final int BACKGROUND_MAX = 20;

	/** Line clear時に飛び散るBlockの画像count */
	public static final int BLOCK_BREAK_MAX = 8;

	/** Line clear時に飛び散るBlockの画像分割count */
	public static final int BLOCK_BREAK_SEGMENTS = 2;

	/** 宝石Block消去エフェクトのcount */
	public static final int PERASE_MAX = 7;

	/** Blockの画像 */
	public static SDLSurface imgBlock, imgBlockSmall, imgBlockBig;

	/** 特殊Blockの画像 - old */
	//public static SDLSurface imgSpBlock, imgSpBlockSmall, imgSpBlockBig;

	/** 通常のフォント */
	public static SDLSurface imgFont, imgFontSmall;

	/** 小物画像 */
	public static SDLSurface imgSprite;

	/** タイトル */
	public static SDLSurface imgTitle;

	/** メニュー背景 */
	public static SDLSurface imgMenu;

	/** フィールドの枠 */
	public static SDLSurface imgFrame;

	/** フィールドの背景 */
	public static SDLSurface imgFieldbg;

	/** 真っ黒画像と真っ白画像 */
	public static SDLSurface imgBlankBlack, imgBlankWhite;

	/** Line clear時に飛び散るBlock */
	public static SDLSurface[][] imgBreak;

	/** 宝石Block消去エフェクト */
	public static SDLSurface[] imgPErase;

	/** プレイ中の背景 */
	public static SDLSurface[] imgPlayBG;

	/** TTFフォント */
	public static SDLTrueTypeFont ttfFont;

	/** 効果音 */
	public static SoundManagerSDL soundManager;

	/** BGM */
	public static MixMusic[] bgm;

	/** Current BGM number */
	public static int bgmPlaying;

	/**
	 * 画像や音声を読み込み
	 * @throws SDLException 読み込みに失敗
	 */
	public static void load() throws SDLException {
		String skindir = NullpoMinoSDL.propConfig.getProperty("custom.skin.directory", "res");

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
		imgSprite = loadImage(skindir + "/graphics/sprite.png");
		imgTitle = loadImage(skindir + "/graphics/title.png");
		imgMenu = loadImage(skindir + "/graphics/menu.png");
		imgFrame = loadImage(skindir + "/graphics/frame.png");
		imgFieldbg = loadImage(skindir + "/graphics/fieldbg.png");
		imgBlankBlack = loadImage(skindir + "/graphics/blank_black.png");
		imgBlankWhite = loadImage(skindir + "/graphics/blank_white.png");

		if(NullpoMinoSDL.propConfig.getProperty("option.showlineeffect", true) == true)
			loadLineClearEffectImages();
		if(NullpoMinoSDL.propConfig.getProperty("option.showbg", true) == true)
			loadBackgroundImages();

		// フォント
		try {
			ttfFont = SDLTTF.openFont(skindir + "/font/font.ttf", 16);
		} catch (Throwable e) {
			log.warn("TTF Font load failed", e);
			ttfFont = null;
		}

		// 効果音
		SDLMixer.allocateChannels(NullpoMinoSDL.propConfig.getProperty("option.soundChannels", 15));

		soundManager = new SoundManagerSDL();
		if(NullpoMinoSDL.propConfig.getProperty("option.se", true) == true) {
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
		bgm = new MixMusic[BGMStatus.BGM_COUNT];
		bgmPlaying = -1;

		if(NullpoMinoSDL.propConfig.getProperty("option.bgmpreload", false) == true) {
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
			imgPlayBG = new SDLSurface[BACKGROUND_MAX];

			String skindir = NullpoMinoSDL.propConfig.getProperty("custom.skin.directory", "res");
			for(int i = 0; i < imgPlayBG.length; i++) {
				imgPlayBG[i] = loadImage(skindir + "/graphics/back" + i + ".png");
			}
		}
	}

	/**
	 * Load line-clear effect images.
	 */
	public static void loadLineClearEffectImages() {
		String skindir = NullpoMinoSDL.propConfig.getProperty("custom.skin.directory", "res");

		if(imgBreak == null) {
			imgBreak = new SDLSurface[BLOCK_BREAK_MAX][BLOCK_BREAK_SEGMENTS];

			for(int i = 0; i < BLOCK_BREAK_MAX; i++) {
				for(int j = 0; j < BLOCK_BREAK_SEGMENTS; j++) {
					imgBreak[i][j] = loadImage(skindir + "/graphics/break" + i + "_" + j + ".png");
				}
			}
		}
		if(imgPErase == null) {
			imgPErase = new SDLSurface[PERASE_MAX];

			for(int i = 0; i < imgPErase.length; i++) {
				imgPErase[i] = loadImage(skindir + "/graphics/perase" + i + ".png");
			}
		}
	}

	/**
	 * 画像読み込み
	 * @param filename ファイル名
	 * @return 画像 data
	 */
	public static SDLSurface loadImage(String filename) {
		SDLSurface img = null;

		try {
			img = SDLImage.load(filename);
		} catch (Throwable e) {
			log.error("Failed to load image from " + filename, e);
			try {
				img = SDLVideo.createRGBSurface(SDLVideo.SDL_SWSURFACE, 256, 256, 4, 0, 0, 0, 0);
			} catch (Throwable e2) {}
		}

		return img;
	}

	/**
	 * 指定した numberのBGMをメモリ上に読み込み
	 * @param no BGM number
	 * @param showerr 例外が発生したときにコンソールに表示する
	 */
	public static void bgmLoad(int no, boolean showerr) {
		if(NullpoMinoSDL.propConfig.getProperty("option.bgm", false) == false) return;

		if(bgm[no] == null) {
			if(showerr) {
				log.info("Loading BGM " + no);
			}

			try {
				String filename = NullpoMinoSDL.propMusic.getProperty("music.filename." + no, null);
				if((filename == null) || (filename.length() < 1)) {
					if(showerr) log.info("BGM" + no + " not available");
					return;
				}

				bgm[no] = SDLMixer.loadMUS(filename);

				if(!showerr) {
					log.info("Loaded BGM " + no);
				}
			} catch(Throwable e) {
				if(showerr) {
					log.warn("BGM " + no + " load failed", e);
				} else {
					log.warn("BGM " + no + " load failed");
				}
			}
		}
	}

	/**
	 * 指定した numberのBGMを再生
	 * @param no BGM number
	 */
	public static void bgmStart(int no) {
		if(NullpoMinoSDL.propConfig.getProperty("option.bgm", false) == false) return;

		bgmStop();
		bgmPlaying = no;

		if(no < 0) return;

		bgmLoad(no, true);

		if(bgm[no] != null) {
			try {
				if(NullpoMinoSDL.propMusic.getProperty("music.noloop." + no, false) == true)
					SDLMixer.playMusic(bgm[no], 1);
				else
					SDLMixer.playMusic(bgm[no], -1);

				SDLMixer.volumeMusic(NullpoMinoSDL.propConfig.getProperty("option.bgmvolume", 128));
			} catch (Exception e) {
				log.warn("BGM " + no + " start failed", e);
			}
		}
	}

	/**
	 * Current BGMを一時停止
	 */
	public static void bgmPause() {
		if(bgmIsPlaying()) {
			SDLMixer.pauseMusic();
		}
	}

	/**
	 * 一時停止中のBGMを再開
	 */
	public static void bgmResume() {
		if(bgmIsPlaying()) {
			SDLMixer.resumeMusic();
		}
	}

	/**
	 * BGM再生中かどうか
	 * @return 再生中ならtrue
	 */
	public static boolean bgmIsPlaying() {
		return SDLMixer.playingMusic();
	}

	/**
	 * BGMを停止
	 */
	public static void bgmStop() {
		try {
			if(bgmIsPlaying()) {
				SDLMixer.haltMusic();
				bgmPlaying = -1;
			}
		} catch (SDLException e) {
			log.debug("BGM stop failed", e);
		}
	}
}
