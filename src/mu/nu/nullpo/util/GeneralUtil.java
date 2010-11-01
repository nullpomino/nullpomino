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

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.subsystem.ai.DummyAI;
import mu.nu.nullpo.game.subsystem.wallkick.Wallkick;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

/**
 * Generic static utils
 */
public class GeneralUtil {
	/** Log */
	static Logger log = Logger.getLogger(GeneralUtil.class);

	/**
	 * Converts play time into a String
	 * @param t Play time
	 * @return String for play time
	 */
	public static String getTime(int t) {
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
	 * Returns ○ if b is true, × if b is false
	 * @param b Boolean variable to be checked
	 * @return ○ if b is true, × if b is false
	 */
	public static String getOorX(boolean b) {
		if(b == true) return "c";
		return "e";
	}

	/**
	 * Fetches the filename for a replay
	 * @return Replay's filename
	 */
	public static String getReplayFilename() {
		GregorianCalendar currentTime = new GregorianCalendar();
		int month = currentTime.get(Calendar.MONTH) + 1;

		String filename = String.format("%04d_%02d_%02d_%02d_%02d_%02d.rep", currentTime.get(Calendar.YEAR), month, currentTime
				.get(Calendar.DATE), currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), currentTime.get(Calendar.SECOND));

		return filename;
	}

	/**
	 * Get date and time from a Calendar
	 * @param c Calendar
	 * @return Date and Time String
	 */
	public static String getCalendarString(Calendar c) {
		return  String.format("%04d-%02d-%02d %02d:%02d:%02d",
				c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE),
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
	}

	/**
	 * Get date from a Calendar
	 * @param c Calendar
	 * @return Date String
	 */
	public static String getCalendarStringDate(Calendar c) {
		return  String.format("%04d-%02d-%02d",
				c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE));
	}

	/**
	 * Get time from a Calendar
	 * @param c Calendar
	 * @return Time String
	 */
	public static String getCalendarStringTime(Calendar c) {
		return  String.format("%02d:%02d:%02d",
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
	}

	/**
	 * Get the number of piece types can appear
	 * @param pieceEnable Piece enable flags
	 * @return Number of piece types can appear (In the normal Tetromino games, it returns 7)
	 */
	public static int getNumberOfPiecesCanAppear(boolean[] pieceEnable) {
		if(pieceEnable == null) return Piece.PIECE_COUNT;

		int count = 0;

		for(int i = 0; i < pieceEnable.length; i++) {
			if(pieceEnable[i]) count++;
		}

		return count;
	}

	/**
	 * Returns true if enabled piece types are S,Z,O only.
	 * @param pieceEnable Piece enable flags
	 * @return <code>true</code> if enabled piece types are S,Z,O only.
	 */
	public static boolean isPieceSZOOnly(boolean[] pieceEnable) {
		if(pieceEnable == null) return false;

		for(int i = 0; i < pieceEnable.length; i++) {
			if((pieceEnable[i] == true) && (i != Piece.PIECE_S) && (i != Piece.PIECE_Z) && (i != Piece.PIECE_O))
				return false;
		}

		return true;
	}

	/**
	 * Create piece ID array from a String
	 * @param strSrc String
	 * @return Piece ID array
	 */
	public static int[] createNextPieceArrayFromNumberString(String strSrc) {
		int len = strSrc.length();
		if(len < 1) return null;

		int[] nextArray = new int[len];
		for(int i = 0; i < len; i++) {
			int pieceID = Piece.PIECE_I;

			try {
				pieceID = Integer.parseInt(strSrc.substring(i, i + 1));
			} catch (NumberFormatException e) {}

			if((pieceID < 0) || (pieceID >= Piece.PIECE_COUNT)) pieceID = Piece.PIECE_I;

			nextArray[i] = pieceID;
		}

		return nextArray;
	}

	/**
	 * Load rule file
	 * @param filename Filename
	 * @return RuleOptions
	 */
	public static RuleOptions loadRule(String filename) {
		CustomProperties prop = new CustomProperties();

		try {
			FileInputStream in = new FileInputStream(filename);
			prop.load(in);
			in.close();
		} catch (Exception e) {
			log.warn("Failed to load rule from " + filename, e);
		}

		RuleOptions ruleopt = new RuleOptions();
		ruleopt.readProperty(prop, 0);

		return ruleopt;
	}

	/**
	 * Load Randomizer
	 * @param filename Classpath of the randomizer
	 * @return Randomizer (null if something fails)
	 */
	public static Randomizer loadRandomizer(String filename) {
		Class<?> randomizerClass = null;
		Randomizer randomizerObject = null;

		try {
			randomizerClass = Class.forName(filename);
			randomizerObject = (Randomizer) randomizerClass.newInstance();
		} catch (Exception e) {
			log.warn("Failed to load Randomizer from " + filename, e);
		}

		return randomizerObject;
	}

	/**
	 * Load Wallkick
	 * @param filename Classpath of the wallkick
	 * @return Wallkick (null if something fails)
	 */
	public static Wallkick loadWallkick(String filename) {
		Class<?> wallkickClass = null;
		Wallkick wallkickObject = null;

		try {
			wallkickClass = Class.forName(filename);
			wallkickObject = (Wallkick) wallkickClass.newInstance();
		} catch (Exception e) {
			log.warn("Failed to load Wallkick from " + filename, e);
		}

		return wallkickObject;
	}

	/**
	 * Load AI
	 * @param filename Classpath of the AI
	 * @return The instance of AI (null if something fails)
	 */
	public static DummyAI loadAIPlayer(String filename) {
		Class<?> aiClass = null;
		DummyAI aiObject = null;

		try {
			aiClass = Class.forName(filename);
			aiObject = (DummyAI) aiClass.newInstance();
		} catch (Exception e) {
			log.warn("Failed to load AIPlayer from " + filename, e);
		}

		return aiObject;
	}
}
