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

import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.newdawn.slick.Sound;

/**
 * 効果音マネージャ
 */
public class SoundManager {
	/** Log */
	static Logger log = Logger.getLogger(SoundManager.class);

	/** 登録できるWAVEファイルのMaximumcount */
	protected int maxClips;

	/** WAVEファイル data (名前-> data本体) */
	protected HashMap<String, Sound> clipMap;

	/** 登録されたWAVEファイルcount */
	protected int counter = 0;

	/**
	 * Constructor
	 */
	public SoundManager() {
		this(128);
	}

	/**
	 * Constructor
	 * @param maxClips 登録できるWAVEファイルのMaximumcount
	 */
	public SoundManager(int maxClips) {
		this.maxClips = maxClips;
		clipMap = new HashMap<String, Sound>(maxClips);
	}

	/**
	 * WAVEファイルをロード
	 * @param name 登録名
	 * @param filename ファイル名 (String）
	 * @return 成功したらtrue、失敗したらfalse
	 */
	public boolean load(String name, String filename) {
		if(counter >= maxClips) {
			log.error("No more wav files can be loaded (" + maxClips + ")");
			return false;
		}

		try {
			Sound clip = new Sound(filename);
			clipMap.put(name, clip);
		} catch(Throwable e) {
			log.error("Failed to load wav file", e);
			return false;
		}

		return true;
	}

	/**
	 * WAVEファイルをロード
	 * @param name 登録名
	 * @param fileurl ファイル名 (URL）
	 * @return 成功したらtrue、失敗したらfalse
	 */
	public boolean load(String name, URL fileurl) {
		if(counter >= maxClips) {
			log.error("No more wav files can be loaded (" + maxClips + ")");
			return false;
		}

		try {
			Sound clip = new Sound(fileurl);
			clipMap.put(name, clip);
		} catch(Throwable e) {
			log.error("Failed to load wav file", e);
			return false;
		}

		return true;
	}

	/**
	 * 再生
	 * @param name 登録名
	 */
	public void play(String name) {
		// 名前に対応するクリップを取得
		Sound clip = clipMap.get(name);

		if(clip != null) {
			clip.play();
		}
	}

	/**
	 * 停止
	 * @param name 登録名
	 */
	public void stop(String name) {
		Sound clip = clipMap.get(name);

		if(clip != null) {
			clip.stop();
		}
	}
}
