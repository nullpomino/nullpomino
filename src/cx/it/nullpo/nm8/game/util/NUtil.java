package cx.it.nullpo.nm8.game.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * NullpoMino utility class (it was available as GeneralUtil and NetUtil in older versions)
 */
public class NUtil {
	/**
	 * Convert frames (1 frame = 1/60 seconds) to Milliseconds (ms)
	 * @param f Frames
	 * @return Milliseconds
	 */
	public static long framesToMS(int f) {
		return Math.round(f * 16.6667f);
	}

	/**
	 * Convert Milliseconds (ms) to frames (1 frame = 1/60 seconds)
	 * @param ms Milliseconds
	 * @return Frames
	 */
	public static int msToFrames(long ms) {
		return Math.round(ms / 16.6667f);
	}

	/**
	 * Converts play time into a String
	 * @param t Play time (ms)
	 * @return String for play time
	 */
	public static String getTime(long t) {
		if(t < 0) return "--:--.---";

		int minute = 0;
		int second = 0;

		long time = t;
		while(time >= 1000 * 60) {
			minute++;
			time -= 1000 * 60;
		}
		while(time >= 1000) {
			second++;
			time -= 1000;
		}

		return String.format("%02d:%02d.%03d", minute, second, time);
	}

	/**
	 * Returns ON if b is true, OFF if b is false
	 * @param b Boolean variable to be checked
	 * @return ON if b is true, OFF if b is false
	 */
	public static String getONorOFF(boolean b) {
		if(b == true) return "ON";
		return "OFF";
	}

	/**
	 * Convert byte[] to String (with UTF-8 encoding)
	 * @param bytes Byte array (byte[])
	 * @return String
	 */
	public static String bytesToString(byte[] bytes) {
		try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error("UTF-8 Not Supported", e);
		}
	}

	/**
	 * Convert String to byte[] (with UTF-8 encoding)
	 * @param str String
	 * @return Byte array (byte[])
	 */
	public static byte[] stringToBytes(String str) {
		try {
			return str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error("UTF-8 Not Supported", e);
		}
	}

	/**
	 * Encode non-URL-safe characters with using URLEncoder
	 * @param str String
	 * @return URLEncoder-encoded String
	 */
	public static String urlEncode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error("UTF-8 Not Supported", e);
		}
	}

	/**
	 * Decode URL-safe characters with using URLDecoder
	 * @param str URLEncoder-encoded String
	 * @return Decoded String
	 */
	public static String urlDecode(String str) {
		try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error("UTF-8 Not Supported", e);
		}
	}

	/**
	 * Convert String to byte[] with Shift_JIS encoding
	 * @param s UTF-8 String
	 * @return Shift_JIS encoded byte array (byte[])
	 */
	public static byte[] stringToShiftJIS(String s) {
		byte[] b = null;
		try {
			b = s.getBytes("Shift_JIS");
		} catch (UnsupportedEncodingException e) {
			b = s.getBytes();
		}
		return b;
	}

	/**
	 * Convert Shift_JIS byte array (byte[]) to String
	 * @param b Shift_JIS encoded byte array (byte[])
	 * @return UTF-8 String
	 */
	public static String shiftJIStoString(byte[] b) {
		String s = null;
		try {
			s = new String(b, "Shift_JIS");
		} catch (UnsupportedEncodingException e) {
			s = new String(b);
		}
		return s;
	}

	/**
	 * Combine array of strings
	 * @param strings Array of strings
	 * @param separator Separator used for combine
	 * @param startIndex First element which will be combined
	 * @return Combined string
	 */
	public static String stringCombine(String[] strings, String separator, int startIndex) {
		String res = "";
		for (int i = startIndex; i<strings.length; i++) {
			res+= strings[i];
			if (i != strings.length-1)
				res+= separator;
		}

		return res;
	}

	/**
	 * Compress a byte array (byte[]). The compression level is 9.<br>
	 * <a href="http://www.exampledepot.com/egs/java.util.zip/CompArray.html">Source</a>
	 * @param input Raw byte array (byte[])
	 * @return Compressed byte array (byte[])
	 */
	public static byte[] compressByteArray(byte[] input) {
		return compressByteArray(input, Deflater.BEST_COMPRESSION);
	}

	/**
	 * Compress a byte array (byte[]).<br>
	 * <a href="http://www.exampledepot.com/egs/java.util.zip/CompArray.html">Source</a>
	 * @param input Raw byte array (byte[])
	 * @param level Compression level (0-9)
	 * @return Compressed byte array (byte[])
	 */
	public static byte[] compressByteArray(byte[] input, int level) {
		// Create the compressor with highest level of compression
		Deflater compressor = new Deflater(level);

		// Give the compressor the data to compress
		compressor.setInput(input);
		compressor.finish();

		// Create an expandable byte array to hold the compressed data.
		// You cannot use an array that's the same size as the orginal because
		// there is no guarantee that the compressed data will be smaller than
		// the uncompressed data.
		ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

		// Compress the data
		byte[] buf = new byte[1024];
		while (!compressor.finished()) {
			int count = compressor.deflate(buf);
			bos.write(buf, 0, count);
		}

		// Get the compressed data
		return bos.toByteArray();
	}

	/**
	 * Decompress a byte array (byte[])
	 * @param compressedData Compressed byte array (byte[])
	 * @return Raw byte array (byte[])
	 */
	public static byte[] decompressByteArray(byte[] compressedData) {
		// Create the decompressor and give it the data to compress
		Inflater decompressor = new Inflater();
		decompressor.setInput(compressedData);

		// Create an expandable byte array to hold the decompressed data
		ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);

		// Decompress the data
		byte[] buf = new byte[1024];
		while (!decompressor.finished()) {
			try {
				int count = decompressor.inflate(buf);
				bos.write(buf, 0, count);
			} catch (DataFormatException e) {
				throw new RuntimeException("This byte array is not a valid compressed data", e);
			}
		}

		// Get the decompressed data
		return bos.toByteArray();
	}

	/**
	 * Fetches the filename for a replay
	 * @return Replay's filename
	 */
	public static String getReplayFilename() {
		Calendar c = Calendar.getInstance();
		DateFormat dfm = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String filename = dfm.format(c.getTime()) + ".rep";
		return filename;
	}

	/**
	 * Get date and time from a Calendar
	 * @param c Calendar
	 * @return Date and Time String
	 */
	public static String getCalendarString(Calendar c) {
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dfm.format(c.getTime());
	}

	/**
	 * Get date and time from a Calendar with specific TimeZone
	 * @param c Calendar
	 * @param z TimeZone
	 * @return Date and Time String
	 */
	public static String getCalendarString(Calendar c, TimeZone z) {
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dfm.setTimeZone(z);
		return dfm.format(c.getTime());
	}

	/**
	 * Get date from a Calendar
	 * @param c Calendar
	 * @return Date String
	 */
	public static String getCalendarStringDate(Calendar c) {
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		return dfm.format(c.getTime());
	}

	/**
	 * Get date from a Calendar with specific TimeZone
	 * @param c Calendar
	 * @param z TimeZone
	 * @return Date String
	 */
	public static String getCalendarStringDate(Calendar c, TimeZone z) {
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		dfm.setTimeZone(z);
		return dfm.format(c.getTime());
	}

	/**
	 * Get time from a Calendar
	 * @param c Calendar
	 * @return Time String
	 */
	public static String getCalendarStringTime(Calendar c) {
		DateFormat dfm = new SimpleDateFormat("HH:mm:ss");
		return dfm.format(c.getTime());
	}

	/**
	 * Get time from a Calendar with specific TimeZone
	 * @param c Calendar
	 * @param z TimeZone
	 * @return Time String
	 */
	public static String getCalendarStringTime(Calendar c, TimeZone z) {
		DateFormat dfm = new SimpleDateFormat("HH:mm:ss");
		dfm.setTimeZone(z);
		return dfm.format(c.getTime());
	}

	/**
	 * Export a Calendar to a String for saving/sending. TimeZone is always GMT. Time is based on current time.
	 * @return Calendar String (Each field is separated with a hyphen '-')
	 */
	public static String exportCalendarString() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		return exportCalendarString(c);
	}

	/**
	 * Export a Calendar to a String for saving/sending. TimeZone is always GMT.
	 * @param c Calendar
	 * @return Calendar String (Each field is separated with a hyphen '-')
	 */
	public static String exportCalendarString(Calendar c) {
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dfm.format(c.getTime());
	}

	/**
	 * Create a Calendar by using a String that came from exportCalendarString. TimeZone is always GMT.
	 * @param s String (Each field is separated with a hyphen '-')
	 * @return Calendar (null if fails)
	 */
	public static Calendar importCalendarString(String s) {
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		dfm.setTimeZone(TimeZone.getTimeZone("GMT"));

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

		try {
			Date date = dfm.parse(s);
			c.setTime(date);
		} catch (Exception e) {
			return null;
		}

		return c;
	}
}
