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

import cx.it.nullpo.nm8.gui.framework.NFSound;
import cx.it.nullpo.nm8.gui.framework.NFSoundProvider;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.util.NUtil;

/**
 * Load a RIFF wave file and create JSNFSound
 */
public class JSSoundProvider extends NFSoundProvider {
	private static final long serialVersionUID = 843506484369573907L;

	@Override
	public String getName() {
		return "Java Sound";
	}

	@Override
	public int getSoundProviderType() {
		return NFSystem.SOUND_PROVIDER_JAVASOUND;
	}

	@Override
	public NFSound loadSound(String filename) throws IOException {
		return loadSound(NUtil.getURL(filename));
	}

	@Override
	public NFSound loadSound(URL url) throws IOException {
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
		} catch (LineUnavailableException e) {
			throw new IllegalStateException(
					"LineUnavailableException is thrown. No more sound effects can be loaded. (" + numSoundLoaded + " files loaded until now)",
					e);
		} catch (UnsupportedAudioFileException e) {
			throw new UnsupportedOperationException(url.getPath() + " is not a valid wav file (" + e.getMessage() + ")", e);
		} catch (IOException e) {
			throw e;
		}

		numSoundLoaded++;
		return s;
	}
}
