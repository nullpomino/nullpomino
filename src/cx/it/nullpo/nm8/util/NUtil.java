package cx.it.nullpo.nm8.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * NullpoMino utility class (it was available as GeneralUtil and NetUtil in older versions)
 */
public class NUtil {
	/** OS name */
	private static String osName = System.getProperty("os.name").toLowerCase();
	private static boolean osWindows = osName.startsWith("win");
	private static boolean osMac = osName.startsWith("mac");
	private static boolean osLinux = osName.startsWith("linux");

	/**
	 * Get URL from a String
	 * @param str Filename
	 * @return URL
	 */
	public static URL getURL(String str) {
		URL url = null;

		try {
			char sep = File.separator.charAt(0);
			String file = str.replace(sep, '/');

			// Source (already dead):http://www.asahi-net.or.jp/~DP8T-ASM/java/tips/HowToMakeURL.html
			if(file.charAt(0) != '/') {
				String dir = System.getProperty("user.dir");
				dir = dir.replace(sep, '/') + '/';
				if(dir.charAt(0) != '/') {
					dir = "/" + dir;
				}
				file = dir + file;
			}
			url = new URL("file", "", file);
		} catch(MalformedURLException e) {
			return null;
		}

		return url;
	}

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
	 * @param t Play time (frames)
	 * @return String for play time
	 */
	public static String getTime(long t) {
		if(t < 0) return "--:--.--";
		return String.format("%02d:%02d.%02d", t / 3600, (t / 60) % 60, (t % 60) * 5 / 3);
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

	/**
	 * Get a List of String from an InputStream.
	 * @param in InputStream to read from
	 * @param charsetName Charset to use
	 * @return A List of String
	 * @throws IOException If something bad happens
	 */
	public static List<String> getStringListFromInputStreamE(InputStream in, String charsetName) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, charsetName));
		List<String> listString = new ArrayList<String>();
		String s;

		while((s = reader.readLine()) != null) {
			listString.add(s);
		}

		return listString;
	}

	/**
	 * Get a List of String from an InputStream. Returns null on failure.
	 * @param in InputStream to read from
	 * @param charsetName Charset to use
	 * @return A List of String, or null if fails
	 */
	public static List<String> getStringListFromInputStream(InputStream in, String charsetName) {
		try {
			return getStringListFromInputStreamE(in, charsetName);
		} catch (IOException e) {}
		return null;
	}

	/**
	 * Get a List of String from an InputStream. It uses UTF-8 charset.
	 * @param in InputStream to read from
	 * @return A List of String
	 * @throws IOException If something bad happens
	 */
	public static List<String> getStringListFromInputStreamE(InputStream in) throws IOException {
		return getStringListFromInputStreamE(in, "UTF-8");
	}

	/**
	 * Get a List of String from an InputStream. It uses UTF-8 charset. Returns null on failure.
	 * @param in InputStream to read from
	 * @return A List of String, or null if fails
	 */
	public static List<String> getStringListFromInputStream(InputStream in) {
		try {
			return getStringListFromInputStreamE(in);
		} catch (IOException e) {}
		return null;
	}

	/**
	 * Get a List of String from specified URL.
	 * @param url URL to read from
	 * @param charsetName Charset to use
	 * @return A List of String
	 * @throws IOException If something bad happens
	 */
	public static List<String> getStringListFromURLE(URL url, String charsetName) throws IOException {
		InputStream in = url.openStream();
		List<String> listString = null;

		try {
			listString = getStringListFromInputStreamE(in, charsetName);
		} finally {
			in.close();
		}

		return listString;
	}

	/**
	 * Get a List of String from specified URL. Returns null on failure.
	 * @param url URL to read from
	 * @param charsetName Charset to use
	 * @return A List of String, or null if fails
	 */
	public static List<String> getStringListFromURL(URL url, String charsetName) {
		try {
			return getStringListFromURLE(url, charsetName);
		} catch (IOException e) {}
		return null;
	}

	/**
	 * Get a List of String from specified URL. It uses UTF-8 charset.
	 * @param url URL to read from
	 * @return A List of String
	 * @throws IOException If something bad happens
	 */
	public static List<String> getStringListFromURLE(URL url) throws IOException {
		InputStream in = url.openStream();
		List<String> listString = null;

		try {
			listString = getStringListFromInputStreamE(in);
		} finally {
			in.close();
		}

		return listString;
	}

	/**
	 * Get a List of String from specified URL. It uses UTF-8 charset. Returns null on failure.
	 * @param url URL to read from
	 * @return A List of String, or null if fails
	 */
	public static List<String> getStringListFromURL(URL url) {
		try {
			return getStringListFromURLE(url);
		} catch (IOException e) {}
		return null;
	}

	/**
	 * @return true if the OS is Windows
	 */
	public static boolean isWindows() {
		return osWindows;
	}

	/**
	 * @return true if the OS is Mac OS
	 */
	public static boolean isMac() {
		return osMac;
	}

	/**
	 * @return true if the OS is Linux
	 */
	public static boolean isLinux() {
		return osLinux;
	}

	/**
	 * @return true if the OS is UN*X-like (non Windows, non Mac)
	 */
	public static boolean isUNIX() {
		return !osWindows && !osMac;
	}

	// *** The following code requires external library from org.cacas.java.gnu.tools.Crypt
	/**
	 * Create Tripcode
	 * @param tripkey Password
	 * @param maxlen Tripcode Length (Usually 10)
	 * @return String of Tripcode
	 */
	public static String createTripCode(String tripkey, int maxlen) {
		byte[] bTripKey = stringToShiftJIS(tripkey);
		byte[] bSaltTemp = new byte[bTripKey.length + 3];
		for(int i = 0; i < bTripKey.length; i++) {
			bSaltTemp[i] = bTripKey[i];
		}
		bSaltTemp[bTripKey.length + 0] = (byte)'H';
		bSaltTemp[bTripKey.length + 1] = (byte)'.';
		bSaltTemp[bTripKey.length + 2] = (byte)'.';
		byte[] bSalt = new byte[2];
		bSalt[0] = bSaltTemp[1];
		bSalt[1] = bSaltTemp[2];

		for(int i = 0; i < bSalt.length; i++) {
			if((bSalt[i] < (byte)'.') || (bSalt[i] > (byte)'z')) bSalt[i] = (byte)'.';
			if(bSalt[i] == (byte)':') bSalt[i] = (byte)'A';
			if(bSalt[i] == (byte)';') bSalt[i] = (byte)'B';
			if(bSalt[i] == (byte)'<') bSalt[i] = (byte)'C';
			if(bSalt[i] == (byte)'=') bSalt[i] = (byte)'D';
			if(bSalt[i] == (byte)'>') bSalt[i] = (byte)'E';
			if(bSalt[i] == (byte)'?') bSalt[i] = (byte)'F';
			if(bSalt[i] == (byte)'@') bSalt[i] = (byte)'G';
			if(bSalt[i] == (byte)'[') bSalt[i] = (byte)'a';
			if(bSalt[i] == (byte)'\\') bSalt[i] = (byte)'b';
			if(bSalt[i] == (byte)']') bSalt[i] = (byte)'c';
			if(bSalt[i] == (byte)'^') bSalt[i] = (byte)'d';
			if(bSalt[i] == (byte)'_') bSalt[i] = (byte)'e';
			if(bSalt[i] == (byte)'`') bSalt[i] = (byte)'f';
		}

		String strTripCode = org.cacas.java.gnu.tools.Crypt.crypt(bSalt, bTripKey);
		if(strTripCode.length() > maxlen) {
			strTripCode = strTripCode.substring(strTripCode.length() - maxlen);
		}

		return strTripCode;
	}

	// *** The following codes require external library from biz.source_code.base64Coder
	/**
	 * Compress a String then encode with Base64. The compression level is 9.
	 * @param input String you want to compress
	 * @return Compressed + Base64 encoded String
	 */
	public static String compressString(String input) {
		return compressString(input, Deflater.BEST_COMPRESSION);
	}

	/**
	 * Compress a String then encode with Base64.
	 * @param input String you want to compress
	 * @param level Compression level (0-9)
	 * @return Compressed + Base64 encoded String
	 */
	public static String compressString(String input, int level) {
		byte[] bCompressed = compressByteArray(stringToBytes(input), level);
		char[] cCompressed = biz.source_code.base64Coder.Base64Coder.encode(bCompressed);
		return new String(cCompressed);
	}

	/**
	 * Decompress a Base64 encoded String
	 * @param input Compressed + Base64 encoded String
	 * @return Raw String
	 */
	public static String decompressString(String input) {
		byte[] bCompressed = biz.source_code.base64Coder.Base64Coder.decode(input);
		byte[] bDecompressed = decompressByteArray(bCompressed);
		return bytesToString(bDecompressed);
	}
}
