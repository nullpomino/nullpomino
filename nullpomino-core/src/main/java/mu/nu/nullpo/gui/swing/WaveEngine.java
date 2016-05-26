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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;

/**
 * Sound engine
 * <a href="http://javagame.skr.jp/index.php?%A5%B5%A5%A6%A5%F3%A5%C9%A5%A8%A5%F3%A5%B8%A5%F3">Reprint yuan</a>
 */
public class WaveEngine implements LineListener {
	/** Log */
	static Logger log = Logger.getLogger(WaveEngine.class);

	/** You can registerWAVE file OfMaximumcount */
	private int maxClips;

	/** WAVE file  data (Name-> dataBody) */
	private HashMap<String, Clip> clipMap;

	/** Was registeredWAVE file count */
	private int counter = 0;

	/** Volume */
	private double volume = 1.0;

	/**
	 * Constructor
	 */
	public WaveEngine() {
		this(128);
	}

	/**
	 * Constructor
	 * @param maxClips You can registerWAVE file OfMaximumcount
	 */
	public WaveEngine(int maxClips) {
		this.maxClips = maxClips;
		clipMap = new HashMap<String, Clip>(maxClips);
	}

	/**
	 * Current Get the volume setting
	 * @return Current Volume setting (1.0The default )
	 */
	public double getVolume() {
		return volume;
	}

	/**
	 * Set the volume
	 * @param vol New configuration volume (1.0The default )
	 */
	public void setVolume(double vol) {
		volume = vol;

		Set<String> set = clipMap.keySet();
		for(String name: set) {
			try {
				Clip clip = clipMap.get(name);
				FloatControl ctrl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
				ctrl.setValue((float)Math.log10(volume) * 20);
			} catch (Exception e) {}
		}
	}

	/**
	 * WAVE file Read
	 * @param name Registered name
	 * @param filename Filename
	 */
	public void load(String name, String filename) {
		load(name, ResourceHolderSwing.getURL(filename));
	}

	/**
	 * WAVE file Read
	 * @param name Registered name
	 * @param url URL
	 */
	public void load(String name, URL url) {
		if(counter >= maxClips) {
			log.warn(name + " : No more files can be loaded (Max:" + maxClips + ")");
			return;
		}

		try {
			// Open the audio stream
			AudioInputStream stream = AudioSystem.getAudioInputStream(url);

			// Obtains the audio format
			AudioFormat format = stream.getFormat();

			// ULAW/ALAWIf the format isPCMChange the format
			if((format.getEncoding() == AudioFormat.Encoding.ULAW) || (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
				AudioFormat newFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
														format.getSampleRate(), format.getSampleSizeInBits() * 2,
														format.getChannels(), format.getFrameSize() * 2, format.getFrameRate(),
														true);
				stream = AudioSystem.getAudioInputStream(newFormat, stream);
				format = newFormat;
			}

			// LinesGet information
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			// Create an empty clip
			Clip clip = (Clip) AudioSystem.getLine(info);
			// Clip event Monitoring
			clip.addLineListener(this);
			// Opened as a clip the audio stream
			clip.open(stream);
			// Submit a clip
			clipMap.put(name, clip);

			// Close the stream
			stream.close();
		} catch (LineUnavailableException e) {
			log.warn(name + " : Failed to open line", e);
		} catch (UnsupportedAudioFileException e) {
			log.warn(name + " : This is not a wave file", e);
		} catch (IOException e) {
			log.warn(name + " : Load failed", e);
		}
	}

	/**
	 * Playback
	 * @param name Registered name
	 */
	public void play(String name) {
		Clip clip = clipMap.get(name);

		if(clip != null) {
			// Stop
			clip.stop();
			// Playback position back to the beginning
			clip.setFramePosition(0);
			// Playback
			clip.start();
		}
	}

	/**
	 * Stop
	 * @param name Registered name
	 */
	public void stop(String name) {
		Clip clip = clipMap.get(name);

		if(clip != null) {
			clip.stop();
		}
	}

	/*
	 * Lines stateChange
	 */
	public void update(LineEvent event) {
		// If you stop playback or to the end
		if(event.getType() == LineEvent.Type.STOP) {
			Clip clip = (Clip) event.getSource();
			clip.stop();
			clip.setFramePosition(0); // Playback position back to the beginning
		}
	}
}
