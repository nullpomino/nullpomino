package cx.it.nullpo.nm8.gui.common.sound.lwjglal;

import java.nio.IntBuffer;

import org.lwjgl.openal.AL10;

import cx.it.nullpo.nm8.gui.framework.NFSound;

/**
 * OpenAL (LWJGL backend) implementation of NFSound.
 * Please note this class does NOT handle most actions.
 * Most actions are handled by LWJGLALSoundProvider, the owner of this class.
 */
public class LWJGLALSound implements NFSound {
	private static final long serialVersionUID = -3178886688240479610L;

	/** Owner of this class */
	protected LWJGLALSoundProvider owner = null;

	/** true if disposed */
	protected boolean isDisposed = false;

	/** Buffers hold sound data */
	protected IntBuffer buffer = null;

	/** Current Volume */
	protected float currentVolume = 1f;

	/** Current Pitch */
	protected float currentPitch = 1f;

	/**
	 * Constructor
	 * @param owner LWJGLALSoundProvider
	 * @param buffer Buffer
	 */
	public LWJGLALSound(LWJGLALSoundProvider owner, IntBuffer buffer) {
		this.owner = owner;
		this.buffer = buffer;
	}

	public void play() {
		owner.playSound(this, false);
	}

	public void loop() {
		owner.playSound(this, true);
	}

	public void stop() {
		owner.stopSound(this);
	}

	public boolean isPlaying() {
		return owner.isPlaying(this);
	}

	public void setVolume(float volume) {
		currentVolume = volume;
	}

	public float getVolume() {
		return currentVolume;
	}

	public void setPitch(float pitch) {
		currentPitch = pitch;
	}

	public float getPitch() {
		return currentPitch;
	}

	public void dispose() {
		if(!isDisposed) {
			AL10.alDeleteBuffers(buffer);
			isDisposed = true;
		}
	}

	public IntBuffer getBuffer() {
		return buffer;
	}
}
