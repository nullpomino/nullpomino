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
package mu.nu.nullpo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * StringSet of properties that can be stored in non-
 */
public class CustomProperties extends Properties {
	/**
	 * Serial version
	 */
	private static final long serialVersionUID = 2L;

	/**
	 * byteSet the properties of the type
	 * @param key Key
	 * @param value keyVariable that corresponds to thecount
	 * @return The previous value of the specified key in the property list. If you do not have it null
	 */
	public synchronized Object setProperty(String key, byte value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * shortSet the properties of the type
	 * @param key Key
	 * @param value keyVariable that corresponds to thecount
	 * @return The previous value of the specified key in the property list. If you do not have it null
	 */
	public synchronized Object setProperty(String key, short value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * intSet the properties of the type
	 * @param key Key
	 * @param value keyVariable that corresponds to thecount
	 * @return The previous value of the specified key in the property list. If you do not have it null
	 */
	public synchronized Object setProperty(String key, int value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * longSet the properties of the type
	 * @param key Key
	 * @param value keyVariable that corresponds to thecount
	 * @return The previous value of the specified key in the property list. If you do not have it null
	 */
	public synchronized Object setProperty(String key, long value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * floatSet the properties of the type
	 * @param key Key
	 * @param value keyVariable that corresponds to thecount
	 * @return The previous value of the specified key in the property list. If you do not have it null
	 */
	public synchronized Object setProperty(String key, float value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * doubleSet the properties of the type
	 * @param key Key
	 * @param value keyVariable that corresponds to thecount
	 * @return The previous value of the specified key in the property list. If you do not have it null
	 */
	public synchronized Object setProperty(String key, double value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * charSet the properties of the type
	 * @param key Key
	 * @param value keyVariable that corresponds to thecount
	 * @return The previous value of the specified key in the property list. If you do not have it null
	 */
	public synchronized Object setProperty(String key, char value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * booleanSet the properties of the type
	 * @param key Key
	 * @param value keyVariable that corresponds to thecount
	 * @return The previous value of the specified key in the property list. If you do not have it null
	 */
	public synchronized Object setProperty(String key, boolean value) {
		return setProperty(key, String.valueOf(value));
	}

	/**
	 * byteGets a property of type
	 * @param key Key
	 * @param defaultValue keyStrange that I return if it can not find thecount
	 * @return Integer that corresponds to the specified keycount (Not founddefaultValue)
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
	 * shortGets a property of type
	 * @param key Key
	 * @param defaultValue keyStrange that I return if it can not find thecount
	 * @return Integer that corresponds to the specified keycount (Not founddefaultValue)
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
	 * intGets a property of type
	 * @param key Key
	 * @param defaultValue keyStrange that I return if it can not find thecount
	 * @return Integer that corresponds to the specified keycount (Not founddefaultValue)
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
	 * longGets a property of type
	 * @param key Key
	 * @param defaultValue keyStrange that I return if it can not find thecount
	 * @return Integer that corresponds to the specified keycount (Not founddefaultValue)
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
	 * floatGets a property of type
	 * @param key Key
	 * @param defaultValue keyStrange that I return if it can not find thecount
	 * @return Integer that corresponds to the specified keycount (Not founddefaultValue)
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
	 * doubleGets a property of type
	 * @param key Key
	 * @param defaultValue keyStrange that I return if it can not find thecount
	 * @return Integer that corresponds to the specified keycount (Not founddefaultValue)
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
	 * charGets a property of type
	 * @param key Key
	 * @param defaultValue keyStrange that I return if it can not find thecount
	 * @return Integer that corresponds to the specified keycount (Not founddefaultValue)
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
	 * booleanGets a property of type
	 * @param key Key
	 * @param defaultValue keyStrange that I return if it can not find thecount
	 * @return Corresponding to the specified keybooleanType variablecount (Not founddefaultValue)
	 */
	public boolean getProperty(String key, boolean defaultValue) {
		String str = getProperty(key, Boolean.toString(defaultValue));
		return Boolean.valueOf(str);
	}

	/**
	 * Converted to a string this property set(URLEncoderEncoded)
	 * @param comments Identifying comment
	 * @return URLEncoderProperty string sets that are encoded in
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
	 * encode(String)I encodedStringRestore the property set from
	 * @param source encode(String)I encodedString
	 * @return The successtrue
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
