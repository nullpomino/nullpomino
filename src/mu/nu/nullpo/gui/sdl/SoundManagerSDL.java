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

import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;

import sdljava.mixer.MixChunk;
import sdljava.mixer.SDLMixer;

/**
 * Sound effectsマネージャ
 */
public class SoundManagerSDL {
	/** Log */
	static Logger log = Logger.getLogger(SoundManagerSDL.class);

	/** 登録できるWAVE file のMaximumcount */
	protected int maxClips;

	/** WAVE file  data (Name-> data本体) */
	protected HashMap<String, MixChunk> clipMap;

	/** チャンネル data (Name->チャンネル number) */
	protected HashMap<String, Integer> channelMap;

	/** 登録されたWAVE file count */
	protected int counter = 0;

	/**
	 * Constructor
	 */
	public SoundManagerSDL() {
		this(128);
	}

	/**
	 * Constructor
	 * @param maxClips 登録できるWAVE file のMaximumcount
	 */
	public SoundManagerSDL(int maxClips) {
		this.maxClips = maxClips;
		clipMap = new HashMap<String, MixChunk>(maxClips);
		channelMap = new HashMap<String, Integer>(maxClips);
	}

	/**
	 * Load WAVE file
	 * @param name 登録名
	 * @param filename Filename (String）
	 * @return true if successful, false if failed
	 */
	public boolean load(String name, String filename) {
		if(counter >= maxClips) {
			log.warn("No more wav files can be loaded (Max:" + maxClips + ")");
			return false;
		}

		try {
			MixChunk clip = SDLMixer.loadWAV(filename);
			int volume = NullpoMinoSDL.propConfig.getProperty("option.sevolume", 128);
			SDLMixer.volumeChunk(clip, volume);
			clipMap.put(name, clip);
		} catch(Throwable e) {
			log.warn("Failed to load wav file from" + filename, e);
			return false;
		}

		return true;
	}

	/**
	 * Load WAVE file
	 * @param name 登録名
	 * @param fileurl Filename (URL）
	 * @return true if successful, false if failed
	 */
	public boolean load(String name, URL fileurl) {
		if(counter >= maxClips) {
			log.warn("No more wav files can be loaded (Max:" + maxClips + ")");
			return false;
		}

		try {
			MixChunk clip = SDLMixer.loadWAV(fileurl);
			clipMap.put(name, clip);
		} catch(Throwable e) {
			log.warn("Failed to load wav file from" + fileurl, e);
			return false;
		}

		return true;
	}

	/**
	 * 再生
	 * @param name 登録名
	 */
	public void play(String name) {
		// Nameに対応するクリップを取得
		MixChunk clip = clipMap.get(name);

		if(clip != null) {
			try {
				int ch = SDLMixer.playChannel(-1, clip, 0);
				channelMap.put(name, ch);
			} catch (Exception e) {
			}
		} else {
			log.debug("Unknown sound played:" + name);
		}
	}

	/**
	 * 停止
	 * @param name 登録名
	 */
	public void stop(String name) {
		Integer ch = channelMap.get(name);

		if(ch != null) {
			try {
				SDLMixer.haltChannel(ch);
				channelMap.remove(name);
			} catch (Exception e) {
				log.debug("Failed to stop sound:" + name, e);
			}
		}
	}
}
