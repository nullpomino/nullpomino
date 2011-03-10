package cx.it.nullpo.nm8.game.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * A Properties that can store/load non-String values
 */
public class CustomProperties extends Properties {
	/** Serial version */
	private static final long serialVersionUID = 3L;

	/**
	 * Set byte property
	 * @param key Key
	 * @param value Value
	 * @return Previous value of key, or null if there were none
	 */
	public synchronized Object setProperty(String key, byte value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * Set short property
	 * @param key Key
	 * @param value Value
	 * @return Previous value of key, or null if there were none
	 */
	public synchronized Object setProperty(String key, short value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * Set int property
	 * @param key Key
	 * @param value Value
	 * @return Previous value of key, or null if there were none
	 */
	public synchronized Object setProperty(String key, int value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * Set long property
	 * @param key Key
	 * @param value Value
	 * @return Previous value of key, or null if there were none
	 */
	public synchronized Object setProperty(String key, long value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * Set float property
	 * @param key Key
	 * @param value Value
	 * @return Previous value of key, or null if there were none
	 */
	public synchronized Object setProperty(String key, float value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * Set double property
	 * @param key Key
	 * @param value Value
	 * @return Previous value of key, or null if there were none
	 */
	public synchronized Object setProperty(String key, double value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * Set char property
	 * @param key Key
	 * @param value Value
	 * @return Previous value of key, or null if there were none
	 */
	public synchronized Object setProperty(String key, char value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * Set boolean property
	 * @param key Key
	 * @param value Value
	 * @return Previous value of key, or null if there were none
	 */
	public synchronized Object setProperty(String key, boolean value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * Get byte property
	 * @param key Key
	 * @param defaultValue Default value (used if key is not found)
	 * @return The value of key (or defaultValue if not found)
	 */
	public byte getProperty(String key, byte defaultValue) {
		String str = getProperty(key, String.valueOf(defaultValue));

		byte result;
		try {
			result = Byte.parseByte(str);
		} catch(NumberFormatException e) {
			result = defaultValue;
		}

		return result;
	}

	/**
	 * Get short property
	 * @param key Key
	 * @param defaultValue Default value (used if key is not found)
	 * @return The value of key (or defaultValue if not found)
	 */
	public short getProperty(String key, short defaultValue) {
		String str = getProperty(key, String.valueOf(defaultValue));

		short result;
		try {
			result = Short.parseShort(str);
		} catch(NumberFormatException e) {
			result = defaultValue;
		}

		return result;
	}

	/**
	 * Get int property
	 * @param key Key
	 * @param defaultValue Default value (used if key is not found)
	 * @return The value of key (or defaultValue if not found)
	 */
	public int getProperty(String key, int defaultValue) {
		String str = getProperty(key, String.valueOf(defaultValue));

		int result;
		try {
			result = Integer.parseInt(str);
		} catch(NumberFormatException e) {
			result = defaultValue;
		}

		return result;
	}

	/**
	 * Get long property
	 * @param key Key
	 * @param defaultValue Default value (used if key is not found)
	 * @return The value of key (or defaultValue if not found)
	 */
	public long getProperty(String key, long defaultValue) {
		String str = getProperty(key, String.valueOf(defaultValue));

		long result;
		try {
			result = Long.parseLong(str);
		} catch(NumberFormatException e) {
			result = defaultValue;
		}

		return result;
	}

	/**
	 * Get float property
	 * @param key Key
	 * @param defaultValue Default value (used if key is not found)
	 * @return The value of key (or defaultValue if not found)
	 */
	public float getProperty(String key, float defaultValue) {
		String str = getProperty(key, String.valueOf(defaultValue));

		float result;
		try {
			result = Float.parseFloat(str);
		} catch(NumberFormatException e) {
			result = defaultValue;
		}

		return result;
	}

	/**
	 * Get double property
	 * @param key Key
	 * @param defaultValue Default value (used if key is not found)
	 * @return The value of key (or defaultValue if not found)
	 */
	public double getProperty(String key, double defaultValue) {
		String str = getProperty(key, String.valueOf(defaultValue));

		double result;
		try {
			result = Double.parseDouble(str);
		} catch(NumberFormatException e) {
			result = defaultValue;
		}

		return result;
	}

	/**
	 * Get char property
	 * @param key Key
	 * @param defaultValue Default value (used if key is not found)
	 * @return The value of key (or defaultValue if not found)
	 */
	public char getProperty(String key, char defaultValue) {
		String str = getProperty(key, String.valueOf(defaultValue));

		char result;
		try {
			result = str.charAt(0);
		} catch(Exception e) {
			result = defaultValue;
		}

		return result;
	}

	/**
	 * Get boolean property
	 * @param key Key
	 * @param defaultValue Default value (used if key is not found)
	 * @return The value of key (or defaultValue if not found)
	 */
	public boolean getProperty(String key, boolean defaultValue) {
		String str = getProperty(key, Boolean.toString(defaultValue));
		return Boolean.valueOf(str);
	}

	/**
	 * Encode this CustomProperties to a UTF-8 String
	 * @param comments Comment (can be null)
	 * @return The encoded String of CustomProperties
	 */
	public String encode(String comments) {
		String result = null;

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			store(out, comments);
			result = URLEncoder.encode(out.toString("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error("UTF-8 not supported", e);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Decode an encoded String of CustomProperties
	 * @param source The encoded String of CustomProperties
	 * @return true if success
	 */
	public boolean decode(String source) {
		try {
			String decodedString = URLDecoder.decode(source, "UTF-8");
			ByteArrayInputStream in = new ByteArrayInputStream(decodedString.getBytes("UTF-8"));
			load(in);
		} catch (UnsupportedEncodingException e) {
			throw new Error("UTF-8 not supported", e);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
