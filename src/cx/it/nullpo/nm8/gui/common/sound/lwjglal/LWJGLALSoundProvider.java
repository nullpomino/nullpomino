package cx.it.nullpo.nm8.gui.common.sound.lwjglal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.util.WaveData;

import cx.it.nullpo.nm8.gui.framework.NFSound;
import cx.it.nullpo.nm8.gui.framework.NFSoundProvider;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.util.NUtil;

/**
 * A NFSoundProvider that creates sound from OpenAL (Uses LWJGL backend)
 */
public class LWJGLALSoundProvider extends NFSoundProvider {
	private static final long serialVersionUID = -7131810609916337503L;

	/** Log */
	private Log log = LogFactory.getLog(LWJGLALSoundProvider.class);

	/** Number of sources to create */
	protected static final int MAX_SOURCE = 64;

	/** List of LWJGLALSound that are created from this class */
	protected List<LWJGLALSound> soundList = Collections.synchronizedList(new ArrayList<LWJGLALSound>());

	/** Sources are points emitting sound. */
	protected List<IntBuffer> sourceList = Collections.synchronizedList(new ArrayList<IntBuffer>());

	/** Position of the source sound. */
	protected FloatBuffer sourcePos = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });

	/** Velocity of the source sound. */
	protected FloatBuffer sourceVel = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });

	/** Position of the listener. */
	protected FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });

	/** Velocity of the listener. */
	protected FloatBuffer listenerVel = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });

	/** Orientation of the listener. (first 3 elements are "at", second 3 are "up") */
	protected FloatBuffer listenerOri = BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f });

	public LWJGLALSoundProvider() {
		// CRUCIAL!
		// any buffer that has data added, must be flipped to establish its position and limits
		sourcePos.flip();
		sourceVel.flip();
		listenerPos.flip();
		listenerVel.flip();
		listenerOri.flip();

		// Init
		initOpenAL();
	}

	/**
	 * Return the AL10 error as a String.
	 * @param err AL10 error code
	 * @return AL10 error as a String
	 */
	public static String getALErrorString(int err) {
		switch (err) {
		case AL10.AL_NO_ERROR:
			return "AL_NO_ERROR";
		case AL10.AL_INVALID_ENUM:
			return "AL_INVALID_ENUM";
		case AL10.AL_INVALID_VALUE:
			return "AL_INVALID_VALUE";
		case AL10.AL_INVALID_OPERATION:
			return "AL_INVALID_OPERATION";
		case AL10.AL_OUT_OF_MEMORY:
			return "AL_OUT_OF_MEMORY";
		default:
			return "Unknown AL Error Code (" + err + ")";
		}
	}

	/**
	 * Return the ALC10 error as a String.
	 * @param err ALC10 error code
	 * @return ALC10 error as a String
	 */
	public static String getALCErrorString(int err) {
		switch (err) {
		case ALC10.ALC_NO_ERROR:
			return "AL_NO_ERROR";
		case ALC10.ALC_INVALID_DEVICE:
			return "ALC_INVALID_DEVICE";
		case ALC10.ALC_INVALID_CONTEXT:
			return "ALC_INVALID_CONTEXT";
		case ALC10.ALC_INVALID_ENUM:
			return "ALC_INVALID_ENUM";
		case ALC10.ALC_INVALID_VALUE:
			return "ALC_INVALID_VALUE";
		case ALC10.ALC_OUT_OF_MEMORY:
			return "ALC_OUT_OF_MEMORY";
		default:
			return "Unknown ALC Error Code (" + err + ")";
		}
	}

	/**
	 * Init OpenAL
	 */
	protected void initOpenAL() {
		try {
			AL.create();
		} catch (Throwable e) {
			throw new RuntimeException("Failed to init OpenAL (" + e.toString() + ")", e);
		}
		AL10.alGetError();	// Reset error number
		createSources();
	}

	/**
	 * Generate sources
	 */
	protected void createSources() {
		synchronized (sourceList) {
			try {
				while(sourceList.size() < MAX_SOURCE) {
					IntBuffer source = BufferUtils.createIntBuffer(1);

					AL10.alGenSources(source);
					if(AL10.alGetError() != AL10.AL_NO_ERROR) break;

					AL10.alSourcef(source.get(0), AL10.AL_PITCH, 1.0f);
					AL10.alSourcef(source.get(0), AL10.AL_GAIN, 1.0f);
					AL10.alSource(source.get(0), AL10.AL_POSITION, sourcePos);
					AL10.alSource(source.get(0), AL10.AL_VELOCITY, sourceVel);

					sourceList.add(source);
				}
			} catch (Throwable e) {}

			log.info(sourceList.size() + " OpenAL sources created");
		}

		AL10.alListener(AL10.AL_POSITION, listenerPos);
		AL10.alListener(AL10.AL_VELOCITY, listenerVel);
		AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
	}

	/**
	 * Get a free (stopped) source
	 * @return A free (stopped) source, or null if all of them are currently used
	 */
	protected IntBuffer getFreeSource() {
		synchronized (sourceList) {
			Iterator<IntBuffer> it = sourceList.iterator();

			while(it.hasNext()) {
				IntBuffer source = it.next();
				int state = AL10.alGetSourcei(source.get(0), AL10.AL_SOURCE_STATE);

				if((state == AL10.AL_INITIAL) || (state == AL10.AL_STOPPED)) {
					return source;
				}
			}
		}

		return null;
	}

	/**
	 * Get a list of sources by using buffer
	 * @param buffer Buffer
	 * @return A list of sources
	 */
	protected List<IntBuffer> getSourceListByBuffer(IntBuffer buffer) {
		List<IntBuffer> list = new ArrayList<IntBuffer>();

		synchronized (sourceList) {
			Iterator<IntBuffer> it = sourceList.iterator();

			while(it.hasNext()) {
				IntBuffer source = it.next();
				int state = AL10.alGetSourcei(source.get(0), AL10.AL_SOURCE_STATE);

				if((state == AL10.AL_PLAYING) || (state == AL10.AL_PAUSED)) {
					int bufferID = AL10.alGetSourcei(source.get(0), AL10.AL_BUFFER);

					if(bufferID == buffer.get(0)) {
						list.add(source);
					}
				}
			}
		}

		return list;
	}

	/**
	 * Create a new LWJGLALSound
	 * @param wave WaveData
	 * @return LWJGLALSound
	 */
	protected LWJGLALSound createSound(WaveData waveFile) {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);

		try {
			AL10.alGenBuffers(buffer);
		} catch (UnsatisfiedLinkError e) {
			throw new RuntimeException("UnsatisfiedLinkError on AL10.alGenBuffers." +
					"Maybe the native libraries are missing, or init of OpenAL has failed?", e);
		} catch (Throwable e) {
			throw new RuntimeException("Cannot create buffer to load wave data (" + e.toString() + ")", e);
		}

		int err = AL10.alGetError();
		if(err != AL10.AL_NO_ERROR) {
			throw new RuntimeException("Cannot create buffer to load wave data (" + getALErrorString(err) + ")");
		}

		AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);

		LWJGLALSound snd = new LWJGLALSound(this, buffer);
		soundList.add(snd);
		return snd;
	}

	/**
	 * Attempt to play sound
	 * @param sound Sound to play
	 * @param loop true to loop forever
	 */
	public void playSound(LWJGLALSound sound, boolean loop) {
		IntBuffer source = getFreeSource();
		if(source == null) return;
		IntBuffer buffer = sound.getBuffer();

		AL10.alSourcei(source.get(0), AL10.AL_BUFFER, buffer.get(0));
		AL10.alSourcef(source.get(0), AL10.AL_PITCH, sound.getPitch());
		AL10.alSourcef(source.get(0), AL10.AL_GAIN, sound.getVolume());
		AL10.alSourcei(source.get(0), AL10.AL_LOOPING, (loop ? AL10.AL_TRUE : AL10.AL_FALSE));

		AL10.alSourcePlay(source);
	}

	/**
	 * Stop sound
	 * @param sound Sound to stop
	 */
	public void stopSound(LWJGLALSound sound) {
		List<IntBuffer> list = getSourceListByBuffer(sound.getBuffer());

		Iterator<IntBuffer> it = list.iterator();
		while(it.hasNext()) {
			IntBuffer source = it.next();
			AL10.alSourceStop(source);
		}
	}

	/**
	 * Check if specified sound is playing
	 * @param sound Sound
	 * @return true if the sound is playing
	 */
	public boolean isPlaying(LWJGLALSound sound) {
		IntBuffer buffer = sound.getBuffer();

		synchronized (sourceList) {
			Iterator<IntBuffer> it = sourceList.iterator();

			while(it.hasNext()) {
				IntBuffer source = it.next();
				int state = AL10.alGetSourcei(source.get(0), AL10.AL_SOURCE_STATE);

				if((state == AL10.AL_PLAYING) || (state == AL10.AL_PAUSED)) {
					int bufferID = AL10.alGetSourcei(source.get(0), AL10.AL_BUFFER);

					if(bufferID == buffer.get(0)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public String getName() {
		return "LWJGL-OpenAL";
	}

	@Override
	public int getSoundProviderType() {
		return NFSystem.SOUND_PROVIDER_OPENAL;
	}

	@Override
	public NFSound loadSound(String filename) throws IOException {
		WaveData waveFile = WaveData.create(filename);

		// Oh no, waveFile is null! Let's try again with URL.
		if(waveFile == null) {
			return loadSound(NUtil.getURL(filename));
		}

		NFSound snd = createSound(waveFile);
		waveFile.dispose();
		numSoundLoaded++;

		return snd;
	}

	@Override
	public NFSound loadSound(URL url) throws IOException {
		WaveData waveFile = WaveData.create(url);

		// Oh no, waveFile is null! What happened?
		if(waveFile == null) {
			try {
				File file = new File(url.toURI());

				if(file.canRead()) {
					throw new IOException("Unknown file loading error (URL:" + url.toString() + ")");
				} else {
					throw new FileNotFoundException("URL " + url.toString() + " does not exist");
				}
			} catch (URISyntaxException e) {
				throw new FileNotFoundException("Bad URL (" + url.toString() + ")");
			}
		}

		NFSound snd = createSound(waveFile);
		waveFile.dispose();
		numSoundLoaded++;

		return snd;
	}

	@Override
	public void dispose() {
		// Release all buffer data
		synchronized (soundList) {
			Iterator<LWJGLALSound> it = soundList.iterator();
			while(it.hasNext()) {
				it.next().dispose();
				it.remove();
			}
		}

		// Release all source data
		synchronized (sourceList) {
			Iterator<IntBuffer> it = sourceList.iterator();
			while(it.hasNext()) {
				IntBuffer source = it.next();
				AL10.alDeleteSources(source);
				it.remove();
			}
		}
	}
}
