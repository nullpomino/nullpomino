package cx.it.nullpo.nm8.gui.framework;

import java.io.Serializable;

/**
 * NullpoMino Framework (NF) - NFMusic<br>
 * A single piece of music; Usually only one music can be played at a time
 */
public interface NFMusic extends Serializable {
	/**
	 * Play this music
	 */
	public void play();

	/**
	 * Loop this music
	 */
	public void loop();

	/**
	 * Stop the music being played
	 */
	public void stop();

	/**
	 * Check if the music is currently playing
	 * @return True if the music is playing
	 */
	public boolean isPlaying();

	/**
	 * Pause the music playback
	 */
	public void pause();

	/**
	 * Resume the music playback
	 */
	public void resume();

	/**
	 * Get the individual volume of the music
	 * @return The volume of this music. 0 - 1, 1 is Max
	 */
	public float getVolume();

	/**
	 * Set the volume of the music
	 * @param volume The volume to play music at. 0 - 1, 1 is Max
	 */
	public void setVolume(float volume);

	/**
	 * Seeks to a position in the music
	 * @param position Position
	 * @return True if the seek was successful
	 */
	public boolean setPosition(float position);

	/**
	 * Get the position in the music
	 * @return Position
	 */
	public float getPosition();
}
