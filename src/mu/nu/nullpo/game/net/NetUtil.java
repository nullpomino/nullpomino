/*
    Copyright (c) 2010, NullNoname
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * Neither the name of NullNoname nor the names of its
          contributors may be used to endorse or promote products derived from
          this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.
*/
package mu.nu.nullpo.game.net;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.cacas.java.gnu.tools.Crypt;

import biz.source_code.base64Coder.Base64Coder;

/**
 * Network utils
 */
public class NetUtil {
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

		String strTripCode = Crypt.crypt(bSalt, bTripKey);
		if(strTripCode.length() > maxlen) {
			strTripCode = strTripCode.substring(strTripCode.length() - maxlen);
		}

		return strTripCode;
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
			} catch (DataFormatException e) {}
		}

		// Get the decompressed data
		return bos.toByteArray();
	}

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
		char[] cCompressed = Base64Coder.encode(bCompressed);
		return new String(cCompressed);
	}

	/**
	 * Decompress a Base64 encoded String
	 * @param input Compressed + Base64 encoded String
	 * @return Raw String
	 */
	public static String decompressString(String input) {
		byte[] bCompressed = Base64Coder.decode(input);
		byte[] bDecompressed = decompressByteArray(bCompressed);
		return bytesToString(bDecompressed);
	}
}
