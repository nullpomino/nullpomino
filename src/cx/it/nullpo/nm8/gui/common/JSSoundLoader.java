package cx.it.nullpo.nm8.gui.common;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import cx.it.nullpo.nm8.game.util.NUtil;

/**
 * Load a RIFF wave file and create JSNFSound
 */
public class JSSoundLoader {
	/** Number of wave files loaded */
	static protected int numWavesLoaded = 0;

	/**
	 * Get number of wave files loaded
	 * @return Number of wave files loaded
	 */
	static public int getNumberOfWaveFilesLoaded() {
		return numWavesLoaded;
	}

	/**
	 * Load a sound effect by using Java Sound API.
	 * @param filename Filename
	 * @return JSNFSound
	 * @throws IOException When load fails
	 * @throws IllegalStateException If no more sound effects can be loaded
	 */
	static public JSNFSound load(String filename) throws IOException {
		return load(NUtil.getURL(filename));
	}

	/**
	 * Load a sound effect by using Java Sound API.
	 * @param url URL to load from
	 * @return JSNFSound
	 * @throws IOException When load fails
	 * @throws IllegalStateException If no more sound effects can be loaded
	 */
	static public JSNFSound load(URL url) throws IOException {
		JSNFSound s = null;

		try {
			AudioInputStream stream = AudioSystem.getAudioInputStream(url);
			AudioFormat format = stream.getFormat();

			if((format.getEncoding() == AudioFormat.Encoding.ULAW) || (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
				AudioFormat newFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
														format.getSampleRate(), format.getSampleSizeInBits() * 2,
														format.getChannels(), format.getFrameSize() * 2, format.getFrameRate(),
														true);
				stream = AudioSystem.getAudioInputStream(newFormat, stream);
				format = newFormat;
			}

			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			s = new JSNFSound(clip);
			stream.close();
			numWavesLoaded++;
		} catch (LineUnavailableException e) {
			throw new IllegalStateException(
					"LineUnavailableException is thrown. No more sound effects can be loaded. (" + numWavesLoaded + " files loaded until now)",
					e);
		} catch (UnsupportedAudioFileException e) {
			throw new IOException(url.getPath() + " is not a valid wav file (" + e.getMessage() + ")");
		} catch (IOException e) {
			throw e;
		}

		return s;
	}
}
