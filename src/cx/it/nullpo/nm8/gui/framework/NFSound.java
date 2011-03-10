package cx.it.nullpo.nm8.gui.framework;

import java.io.Serializable;

/**
 * NullpoMino Framework (NF) - NFSound<br>
 * A single sound effect
 */
public interface NFSound extends Serializable {
	/**
	 * Play this sound effect
	 */
	public void play();

	/**
	 * Loop this sound effect
	 */
	public void loop();

	/**
	 * Stop the sound being played
	 */
	public void stop();

	/**
	 * Check if the sound is currently playing
	 * @return True if the sound is playing
	 */
	public boolean isPlaying();
}
