package cx.it.nullpo.nm8.gui.framework;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

/**
 * NullpoMino Framework (NF) - NFSoundProvider<br>
 * Load a file and create NFSound
 */
public abstract class NFSoundProvider implements Serializable {
	private static final long serialVersionUID = 4929636545429557924L;

	/** Number of sound files loaded */
	protected int numSoundLoaded = 0;

	/**
	 * Get this sound provider's name
	 * @return This sound provider's name
	 */
	public String getName() {
		return "";
	}

	/**
	 * Get this sound provider's type
	 * @return This sound provider's type
	 */
	public int getSoundProviderType() {
		return -1;
	}

	/**
	 * Get number of sound files loaded
	 * @return Number of sound files loaded
	 */
	public int getNumberOfSoundLoaded() {
		return numSoundLoaded;
	}

	/**
	 * Load a sound
	 * @param filename Filename
	 * @return NFSound
	 * @throws IOException When load fails
	 * @throws UnsupportedOperationException If the format of sound effect is not supported
	 * @throws IllegalStateException If no more sound effects can be loaded
	 */
	public NFSound loadSound(String filename) throws IOException {
		return null;
	}

	/**
	 * Load a sound
	 * @param url URL
	 * @return NFSound
	 * @throws IOException When load fails
	 * @throws UnsupportedOperationException If the format of sound effect is not supported
	 * @throws IllegalStateException If no more sound effects can be loaded
	 */
	public NFSound loadSound(URL url) throws IOException {
		return null;
	}

	/**
	 * Dispose the sound system
	 */
	public void dispose() {
	}
}
