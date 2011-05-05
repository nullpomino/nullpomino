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

	/**
	 * Set the volume of this sound (1f equals 100%).<br>
	 * If this sound is already playing, the change may not be applied immediately.
	 * @param volume Volume of this sound (1f equals 100%)
	 */
	public void setVolume(float volume);

	/**
	 * Get the current volume of this sound (1f equals 100%).
	 * @return Volume of this sound (1f equals 100%)
	 */
	public float getVolume();

	/**
	 * Set the pitch of this sound.<br>
	 * If this sound is already playing, the change may not be applied immediately.<br>
	 * Some system does not have the support of pitch changing; in that case, this setting will be ignored.
	 * @param pitch Pitch of this sound
	 */
	public void setPitch(float pitch);

	/**
	 * Get the pitch of this sound.
	 * @return Pitch of this sound
	 */
	public float getPitch();
}
