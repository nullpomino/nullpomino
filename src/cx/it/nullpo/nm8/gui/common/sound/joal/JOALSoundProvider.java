package cx.it.nullpo.nm8.gui.common.sound.joal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALC;
import com.jogamp.openal.ALCcontext;
import com.jogamp.openal.ALCdevice;
import com.jogamp.openal.ALException;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;

import cx.it.nullpo.nm8.gui.framework.NFSound;
import cx.it.nullpo.nm8.gui.framework.NFSoundProvider;
import cx.it.nullpo.nm8.gui.framework.NFSystem;

/**
 * A NFSoundProvider that creates sound from OpenAL (Uses JOAL backend)
 */
public class JOALSoundProvider extends NFSoundProvider {
	static protected final long serialVersionUID = -7684279361068321760L;

	/** Log */
	private Log log = LogFactory.getLog(JOALSoundProvider.class);

	/** Number of sources to create */
	protected static final int MAX_SOURCE = 64;

	/** ALC: This object is used to access most of the OpenAL context functionality. */
	protected ALC alc;
	/** AL: This object is used to access most of the OpenAL functionality. */
	protected AL al;

	/** List of JOALSound that are created from this class */
	protected List<JOALSound> soundList = Collections.synchronizedList(new ArrayList<JOALSound>());

	/** Sources are points emitting sound. */
	protected List<Integer> sourceList = Collections.synchronizedList(new ArrayList<Integer>());

	/** Position of the source sounds. */
	protected float[] sourcePos = { 0.0f, 0.0f, 0.0f };
	/**  Velocity of the source sounds. */
	protected float[] sourceVel = { 0.0f, 0.0f, 0.0f };
	/**  Position of the listener. */
	protected float[] listenerPos = { 0.0f, 0.0f, 0.0f };
	/** Velocity of the listener. */
	protected float[] listenerVel = { 0.0f, 0.0f, 0.0f };
	/** Orientation of the listener. (first 3 elements are "at", second 3 are "up") */
	protected float[] listenerOri = { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f };

	/**
	 * Constructor
	 */
	public JOALSoundProvider() {
		initOpenAL();
	}

	/**
	 * Return the AL error as a String.
	 * @param err AL error code
	 * @return AL error as a String
	 */
	public static String getALErrorString(int err) {
		switch (err) {
		case AL.AL_NO_ERROR:
			return "AL_NO_ERROR";
		case AL.AL_INVALID_ENUM:
			return "AL_INVALID_ENUM";
		case AL.AL_INVALID_VALUE:
			return "AL_INVALID_VALUE";
		case AL.AL_INVALID_OPERATION:
			return "AL_INVALID_OPERATION";
		case AL.AL_OUT_OF_MEMORY:
			return "AL_OUT_OF_MEMORY";
		default:
			return "Unknown AL Error Code (" + err + ")";
		}
	}

	/**
	 * Return the ALC error as a String.
	 * @param err ALC error code
	 * @return ALC error as a String
	 */
	public static String getALCErrorString(int err) {
		switch (err) {
		case ALC.ALC_NO_ERROR:
			return "AL_NO_ERROR";
		case ALC.ALC_INVALID_DEVICE:
			return "ALC_INVALID_DEVICE";
		case ALC.ALC_INVALID_CONTEXT:
			return "ALC_INVALID_CONTEXT";
		case ALC.ALC_INVALID_ENUM:
			return "ALC_INVALID_ENUM";
		case ALC.ALC_INVALID_VALUE:
			return "ALC_INVALID_VALUE";
		case ALC.ALC_OUT_OF_MEMORY:
			return "ALC_OUT_OF_MEMORY";
		default:
			return "Unknown ALC Error Code (" + err + ")";
		}
	}

	/**
	 * Init OpenAL
	 */
	protected void initOpenAL() {
		alc = ALFactory.getALC();
		al = ALFactory.getAL();

		// Get handle to default device.
		ALCdevice device = alc.alcOpenDevice(null);
		if(device == null) {
			throw new ALException("OpenAL device is not available");
		}

		String deviceSpecifier = alc.alcGetString(device, ALC.ALC_DEVICE_SPECIFIER);
		if(deviceSpecifier == null) {
			log.warn("Can't get specifier for default OpenAL device");
		} else {
			log.trace("Using device " + deviceSpecifier);
		}

		// Create audio context.
		ALCcontext context = alc.alcCreateContext(device, null);
		if(context == null) {
			throw new ALException("Error creating OpenAL context");
		}

		// Set active context.
		alc.alcMakeContextCurrent(context);

		// Create sources
		createSources();
	}

	/**
	 * Generate sources
	 */
	protected void createSources() {
		synchronized (sourceList) {
			try {
				while(sourceList.size() < MAX_SOURCE) {
					int[] source = new int[1];
					al.alGenSources(1, source, 0);

					if(al.alGetError() != AL.AL_NO_ERROR) {
						break;
					}

					al.alSourcef(source[0], AL.AL_PITCH, 1.0f);
					al.alSourcef(source[0], AL.AL_GAIN, 1.0f);
					al.alSourcefv(source[0], AL.AL_POSITION, sourcePos, 0);
					al.alSourcefv(source[0], AL.AL_VELOCITY, sourceVel, 0);

					sourceList.add(source[0]);
				}
			} catch (Throwable e) {}

			log.info(sourceList.size() + " OpenAL sources created");
		}

		al.alListenerfv(AL.AL_POSITION, listenerPos, 0);
		al.alListenerfv(AL.AL_VELOCITY, listenerVel, 0);
		al.alListenerfv(AL.AL_ORIENTATION, listenerOri, 0);
	}

	/**
	 * Get a free (stopped) source
	 * @return A free (stopped) source, or -1 if all of them are currently used
	 */
	protected int getFreeSource() {
		synchronized (sourceList) {
			Iterator<Integer> it = sourceList.iterator();

			while(it.hasNext()) {
				Integer source = it.next();
				int[] state = new int[1];
				al.alGetSourcei(source, AL.AL_SOURCE_STATE, state, 0);

				if((state[0] == AL.AL_INITIAL) || (state[0] == AL.AL_STOPPED)) {
					return source;
				}
			}
		}

		return -1;
	}

	/**
	 * Get a list of sources by using buffer
	 * @param buffer Buffer
	 * @return A list of sources
	 */
	protected List<Integer> getSourceListByBuffer(int buffer) {
		List<Integer> list = new ArrayList<Integer>();

		synchronized (sourceList) {
			Iterator<Integer> it = sourceList.iterator();

			while(it.hasNext()) {
				Integer source = it.next();
				int[] state = new int[1];
				al.alGetSourcei(source, AL.AL_SOURCE_STATE, state, 0);

				if((state[0] == AL.AL_PLAYING) || (state[0] == AL.AL_PAUSED)) {
					int[] bufferID = new int[1];
					al.alGetSourcei(source, AL.AL_BUFFER, bufferID, 0);

					if(bufferID[0] == buffer) {
						list.add(source);
					}
				}
			}
		}

		return list;
	}

	/**
	 * Attempt to play sound
	 * @param sound Sound to play
	 * @param loop true to loop forever
	 */
	public void playSound(JOALSound sound, boolean loop) {
		int source = getFreeSource();
		if(source == -1) return;
		int buffer = sound.getBuffer();

		al.alSourcei(source, AL.AL_BUFFER, buffer);
		al.alSourcef(source, AL.AL_PITCH, sound.getPitch());
		al.alSourcef(source, AL.AL_GAIN, sound.getVolume());
		al.alSourcei(source, AL.AL_LOOPING, (loop ? AL.AL_TRUE : AL.AL_FALSE));

		al.alSourcePlay(source);
	}

	/**
	 * Stop sound
	 * @param sound Sound to stop
	 */
	public void stopSound(JOALSound sound) {
		List<Integer> list = getSourceListByBuffer(sound.getBuffer());

		Iterator<Integer> it = list.iterator();
		while(it.hasNext()) {
			Integer source = it.next();
			al.alSourceStop(source);
		}
	}

	/**
	 * Check if specified sound is playing
	 * @param sound Sound
	 * @return true if the sound is playing
	 */
	public boolean isPlaying(JOALSound sound) {
		synchronized (sourceList) {
			Iterator<Integer> it = sourceList.iterator();

			while(it.hasNext()) {
				Integer source = it.next();
				int[] state = new int[1];
				al.alGetSourcei(source, AL.AL_SOURCE_STATE, state, 0);

				if((state[0] == AL.AL_PLAYING) || (state[0] == AL.AL_PAUSED)) {
					int[] bufferID = new int[1];
					al.alGetSourcei(source, AL.AL_BUFFER, bufferID, 0);

					if(bufferID[0] == sound.getBuffer()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public NFSound loadSound(String filename) throws IOException {
		// Variables to load into.
		int[] format = new int[1];
		int[] size = new int[1];
		ByteBuffer[] data = new ByteBuffer[1];
		int[] freq = new int[1];
		int[] loop = new int[1];
		int[] buffer = new int[1];

		// Load wav data from a file
		try {
			ALut.alutLoadWAVFile(filename, format, data, size, freq, loop);
		} catch (ALException e) {
			throw new IOException("Failed to load wave file from " + filename, e);
		}

		// Load wav data into buffers.
		al.alGenBuffers(1, buffer, 0);
		if(al.alGetError() != AL.AL_NO_ERROR) {
			throw new ALException("Cannot create buffer to load wave data (" + getALErrorString(al.alGetError()) + ")");
		}
		al.alBufferData(buffer[0], format[0], data[0], size[0], freq[0]);

		return new JOALSound(this, buffer[0]);
	}

	public NFSound loadSound(URL url) throws IOException {
		// Variables to load into.
		int[] format = new int[1];
		int[] size = new int[1];
		ByteBuffer[] data = new ByteBuffer[1];
		int[] freq = new int[1];
		int[] loop = new int[1];
		int[] buffer = new int[1];

		// Load wav data from a URL (using InputStream)
		InputStream in = url.openStream();
		try {
			ALut.alutLoadWAVFile(in, format, data, size, freq, loop);
		} catch (ALException e) {
			throw new IOException("Failed to load wave file from " + url, e);
		}
		in.close();

		// Load wav data into buffers.
		al.alGenBuffers(1, buffer, 0);
		if(al.alGetError() != AL.AL_NO_ERROR) {
			throw new ALException("Cannot create buffer to load wave data (" + getALErrorString(al.alGetError()) + ")");
		}
		al.alBufferData(buffer[0], format[0], data[0], size[0], freq[0]);

		return new JOALSound(this, buffer[0]);
	}

	@Override
	public String getName() {
		return "JOAL-OpenAL";
	}

	@Override
	public int getSoundProviderType() {
		return NFSystem.SOUND_PROVIDER_OPENAL_LWJGL;
	}

	public int getNumberOfSoundLoaded() {
		return soundList.size();
	}

	public void dispose() {
		// Release all buffer data
		synchronized (soundList) {
			Iterator<JOALSound> it = soundList.iterator();
			while(it.hasNext()) {
				it.next().dispose();
				it.remove();
			}
		}

		// Release all source data
		synchronized (sourceList) {
			Iterator<Integer> it = sourceList.iterator();
			while(it.hasNext()) {
				al.alDeleteSources(1, new int[] {it.next().intValue()}, 0);
				it.remove();
			}
		}

		// Close the OpenAL
		ALCcontext curContext;
		ALCdevice curDevice;

		// Get the current context.
		curContext = alc.alcGetCurrentContext();

		// Get the device used by that context.
		curDevice = alc.alcGetContextsDevice(curContext);

		// Reset the current context to NULL.
		alc.alcMakeContextCurrent(null);

		// Release the context and the device.
		alc.alcDestroyContext(curContext);
		alc.alcCloseDevice(curDevice);

		log.trace("OpenAL (JOAL) disposed");
	}
}
