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
package mu.nu.nullpo.gui.swing;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * 画像や音声の管理をするクラス
 */
public class ResourceHolderSwing {
	/** Backgroundのcount */
	public static final int BACKGROUND_MAX = 20;

	/** Log */
	static Logger log = Logger.getLogger(ResourceHolderSwing.class);

	/** Block images */
	public static Image imgBlock, imgBlockSmall, imgBlockBig;

	/** 特殊Block images - old */
	//public static Image imgSpBlock, imgSpBlockSmall, imgSpBlockBig;

	/** Regular font */
	public static Image imgFont, imgFontSmall;

	/** Field frame */
	public static Image imgFrame;

	/** Field background */
	public static Image imgFieldbg;

	/** プレイ中のBackground */
	public static Image[] imgPlayBG;

	/** 音声ファイル管理 */
	public static WaveEngine soundManager;

	/**
	 * 画像や音声を読み込み
	 */
	public static void load() {
		String skindir = NullpoMinoSwing.propConfig.getProperty("custom.skin.directory", "res");

		// 画像
		imgBlock = loadImage(getURL(skindir + "/graphics/block.png"));
		imgBlockSmall = loadImage(getURL(skindir + "/graphics/block_small.png"));
		imgBlockBig = loadImage(getURL(skindir + "/graphics/block_big.png"));
		/* old special blocks
		 * imgSpBlock = loadImage(getURL(skindir + "/graphics/block_sp.png"));
		 * imgSpBlockSmall = loadImage(getURL(skindir + "/graphics/block_sp_small.png"));
		 * imgSpBlockBig = loadImage(getURL(skindir + "/graphics/block_sp_big.png"));
		 */
		imgFont = loadImage(getURL(skindir + "/graphics/font.png"));
		imgFontSmall = loadImage(getURL(skindir + "/graphics/font_small.png"));
		imgFrame = loadImage(getURL(skindir + "/graphics/frame.png"));
		imgFieldbg = loadImage(getURL(skindir + "/graphics/fieldbg.png"));

		if(NullpoMinoSwing.propConfig.getProperty("option.showbg", true) == true) {
			loadBackgroundImages();
		}

		// Sound effects
		soundManager = new WaveEngine();
		if(NullpoMinoSwing.propConfig.getProperty("option.se", true) == true) {
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

			soundManager.setVolume(NullpoMinoSwing.propConfig.getProperty("option.sevolume", 0.5));
		}
	}

	/**
	 * Load background images.
	 */
	public static void loadBackgroundImages() {
		if(imgPlayBG == null) {
			imgPlayBG = new Image[BACKGROUND_MAX];

			String skindir = NullpoMinoSwing.propConfig.getProperty("custom.skin.directory", "res");
			for(int i = 0; i < BACKGROUND_MAX; i++) {
				imgPlayBG[i] = loadImage(getURL(skindir + "/graphics/back" + i + ".png"));
			}
		}
	}

	/**
	 * 画像を読み込み
	 * @param url 画像ファイルのURL
	 * @return 画像ファイル (失敗するとnull）
	 */
	public static BufferedImage loadImage(URL url) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(url);
		} catch (Throwable e) {
			log.error("Failed to load image " + url, e);
			try {
				img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
			} catch (Throwable e2) {}
		}

		return img;
	}

	/**
	 * リソースファイルのURLを返す
	 * @param str Filename
	 * @return リソースファイルのURL
	 */
	public static URL getURL(String str) {
		URL url = null;

		try {
			char sep = File.separator.charAt(0);
			String file = str.replace(sep, '/');

			// 参考(消滅)：http://www.asahi-net.or.jp/~DP8T-ASM/java/tips/HowToMakeURL.html
			if(file.charAt(0) != '/') {
				String dir = System.getProperty("user.dir");
				dir = dir.replace(sep, '/') + '/';
				if(dir.charAt(0) != '/') {
					dir = "/" + dir;
				}
				file = dir + file;
			}
			url = new URL("file", "", file);
		} catch(MalformedURLException e) {
			log.warn("Invalid URL:" + str, e);
			return null;
		}

		return url;
	}
}
