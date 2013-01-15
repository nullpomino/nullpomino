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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import mu.nu.nullpo.game.subsystem.mode.GameMode;

import org.apache.log4j.Logger;

/**
 * Mode Management class
 */
public class ModeManager {
	/** Log */
	static Logger log = Logger.getLogger(ModeManager.class);

	/** Mode Dynamic array of */
	public ArrayList<GameMode> modelist = new ArrayList<GameMode>();

	/**
	 * Constructor
	 */
	public ModeManager() {
	}

	/**
	 * Copy constructor
	 * @param m Copy source
	 */
	public ModeManager(ModeManager m) {
		modelist.addAll(m.modelist);
	}

	/**
	 * Mode OfcountGet the(Usually+All net play)
	 * @return ModeOfcount(Usually+All net play)
	 */
	public int getSize() {
		return modelist.size();
	}

	/**
	 * Mode OfcountGet the
	 * @param netplay falseIf normalMode Only, When true,For net playMode OnlycountObtained
	 * @return ModeOfcount
	 */
	public int getNumberOfModes(boolean netplay) {
		int count = 0;

		for(int i = 0; i < modelist.size(); i++) {
			GameMode mode = modelist.get(i);

			if((mode != null) && (mode.isNetplayMode() == netplay))
				count++;
		}

		return count;
	}

	/**
	 * All that has been readMode nameGet the
	 * @return Mode nameAn array of
	 */
	public String[] getAllModeNames() {
		String[] strings = new String[getSize()];

		for(int i = 0; i < strings.length; i++) {
			strings[i] = getName(i);
		}

		return strings;
	}

	/**
	 * Are loadedMode nameGet the
	 * @param netplay falseIf normalMode Only, When true,For net playMode Only obtained
	 * @return Mode nameAn array of
	 */
	public String[] getModeNames(boolean netplay) {
		int num = getNumberOfModes(netplay);
		String[] strings = new String[num];
		int j = 0;

		for(int i = 0; i < modelist.size(); i++) {
			GameMode mode = modelist.get(i);

			if((mode != null) && (mode.isNetplayMode() == netplay)) {
				strings[j] = mode.getName();
				j++;
			}
		}

		return strings;
	}

	/**
	 * Mode  nameGet the
	 * @param id ModeID
	 * @return Mode name (idIf the incorrect &quot;*INVALID MODE*&quot;)
	 */
	public String getName(int id) {
		try {
			return modelist.get(id).getName();
		} catch(Exception e) {
			return "*INVALID MODE*";
		}
	}

	/**
	 * Mode  nameFromIDGet the
	 * @param name Mode name
	 * @return ModeID (If it is not found-1)
	 */
	public int getIDbyName(String name) {
		if(name == null) return -1;

		for(int i = 0; i < modelist.size(); i++) {
			if(name.compareTo(modelist.get(i).getName()) == 0) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Mode Gets an object
	 * @param id ModeID
	 * @return ModeObject (idIf the incorrectnull)
	 */
	public GameMode getMode(int id) {
		try {
			return modelist.get(id);
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * Mode Gets an object
	 * @param name Mode name
	 * @return ModeObject (Not foundnull)
	 */
	public GameMode getMode(String name) {
		try {
			return modelist.get(getIDbyName(name));
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * Property fileGame from the list that was written toMode Read
	 * @param prop Property file
	 */
	public void loadGameModes(CustomProperties prop) {
		int count = 0;

		while(true) {
			// Read the name of a class
			String name = prop.getProperty(String.valueOf(count), null);
			if(name == null) return;

			Class<?> modeClass;
			GameMode modeObject;

			try {
				modeClass = Class.forName(name);
				modeObject = (GameMode) modeClass.newInstance();
				modelist.add(modeObject);
			} catch(ClassNotFoundException e) {
				log.warn("Mode class " + name + " not found", e);
			} catch(Exception e) {
				log.warn("Mode class " + name + " load failed", e);
			}

			count++;
		}
	}

	/**
	 * Game from the list that was written to a text fileMode Read
	 * @param bf I read a text fileBufferedReader
	 */
	public void loadGameModes(BufferedReader bf) {
		while(true) {
			// Read the name of a class
			String name = null;
			try {
				name = bf.readLine();
			} catch (IOException e) {
				log.warn("IOException on readLine()", e);
				return;
			}
			if(name == null) return;
			if(name.length() == 0) return;

			if(!name.startsWith("#")) {
				Class<?> modeClass;
				GameMode modeObject;

				try {
					modeClass = Class.forName(name);
					modeObject = (GameMode) modeClass.newInstance();
					modelist.add(modeObject);
				} catch(ClassNotFoundException e) {
					log.warn("Mode class " + name + " not found", e);
				} catch(Exception e) {
					log.warn("Mode class " + name + " load failed", e);
				}
			}
		}
	}
}
