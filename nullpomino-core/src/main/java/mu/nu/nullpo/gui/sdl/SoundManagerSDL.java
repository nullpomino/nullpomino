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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import sdljava.SDLException;
import sdljava.mixer.MixChunk;
import sdljava.mixer.SDLMixer;

/**
 * Sound effectsManager
 */
public class SoundManagerSDL {
	/** Log */
	static Logger log = Logger.getLogger(SoundManagerSDL.class);

	/** You can registerWAVE file OfMaximumcount */
	protected int maxClips;

	/** WAVE file  data (Name-> dataBody) */
	protected HashMap<String, MixChunk> clipMap;

	/** Channel data (Name->Channel number) */
	protected HashMap<String, Integer> channelMap;

	/** Was registeredWAVE file count */
	protected int counter = 0;

	/**
	 * Constructor
	 */
	public SoundManagerSDL() {
		this(128);
	}

	/**
	 * Constructor
	 * @param maxClips You can registerWAVE file OfMaximumcount
	 */
	public SoundManagerSDL(int maxClips) {
		this.maxClips = maxClips;
		clipMap = new HashMap<String, MixChunk>(maxClips);
		channelMap = new HashMap<String, Integer>(maxClips);
	}

	/**
	 * Load WAVE file
	 * @param name Registered name
	 * @param filename Filename (String)
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
	 * @param name Registered name
	 * @param fileurl Filename (URL)
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
	 * Playback
	 * @param name Registered name
	 */
	public void play(String name) {
		// NameGet the clip corresponding to the
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
	 * Stop
	 * @param name Registered name
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

	/**
	 * Change sound volume
	 * @param volume New volume
	 */
	public void changeVolume(int volume) {
		Collection<MixChunk> sounds = clipMap.values();
		Iterator<MixChunk> it = sounds.iterator();

		while(it.hasNext()) {
			MixChunk clip = it.next();
			try {
				SDLMixer.volumeChunk(clip, volume);
			} catch (SDLException e) {
				log.debug("Failed to change volume", e);
			}
		}
	}
}
