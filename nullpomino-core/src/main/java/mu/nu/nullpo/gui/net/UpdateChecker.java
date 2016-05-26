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
package mu.nu.nullpo.gui.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * NewVersionChecker
 */
public class UpdateChecker implements Runnable {
	/** Log */
	static Logger log = Logger.getLogger(UpdateChecker.class);

	/**  default のXMLのURL */
	/*
	 * TODO: Find an actual place to put the NullpoUpdate.xml file, possible
	 * on github pages.  For now, just use the v7.5.0 file as a classpath
	 * resource.
	 */
	public static final String DEFAULT_XML_URL = UpdateChecker.class.getResource("NullpoUpdate.xml").toString();

	/** Constant statecount */
	public static final int STATUS_INACTIVE = 0,
							STATUS_LOADING = 1,
							STATUS_ERROR = 2,
							STATUS_COMPLETE = 3;

	/** Current State */
	private static volatile int status = 0;

	/**  event Listener */
	private static LinkedList<UpdateCheckerListener> listeners = null;

	/** Update information has been writtenXMLOfURL */
	private static String strURLofXML = null;

	/** The latest version ofVersion number */
	private static String strLatestVersion = null;

	/** Release Date */
	private static String strReleaseDate = null;

	/** DownloadURL */
	private static String strDownloadURL = null;

	/** Installer for Windows URL */
	private static String strWindowsInstallerURL = null;

	/** Update check Thread for */
	private static Thread thread = null;

	/**
	 * XMLDownload theVersion numberAcquisition and
	 * @return true if successful
	 */
	private static boolean checkUpdate() {
		try {
			URL url = new URL(strURLofXML);
			URLConnection httpCon = url.openConnection();
			BufferedReader httpIn = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));

			String str;
			while((str = httpIn.readLine()) != null) {
				Pattern pat = Pattern.compile("<Version>.*</Version>");
				Matcher matcher = pat.matcher(str);
				if(matcher.find()) {
					String tempStr = matcher.group();
					tempStr = tempStr.replace("<Version>", "");
					tempStr = tempStr.replace("</Version>", "");
					strLatestVersion = tempStr;
					log.debug("Latest Version:" + strLatestVersion);
				}

				pat = Pattern.compile("<Date>.*</Date>");
				matcher = pat.matcher(str);
				if(matcher.find()) {
					String tempStr = matcher.group();
					tempStr = tempStr.replace("<Date>", "");
					tempStr = tempStr.replace("</Date>", "");
					strReleaseDate = tempStr;
					log.debug("Release Date:" + strReleaseDate);
				}

				pat = Pattern.compile("<DownloadURL>.*</DownloadURL>");
				matcher = pat.matcher(str);
				if(matcher.find()) {
					String tempStr = matcher.group();
					tempStr = tempStr.replace("<DownloadURL>", "");
					tempStr = tempStr.replace("</DownloadURL>", "");
					strDownloadURL = tempStr;
					log.debug("Download URL:" + strDownloadURL);
				}

				pat = Pattern.compile("<WindowsInstallerURL>.*</WindowsInstallerURL>");
				matcher = pat.matcher(str);
				if(matcher.find()) {
					String tempStr = matcher.group();
					tempStr = tempStr.replace("<WindowsInstallerURL>", "");
					tempStr = tempStr.replace("</WindowsInstallerURL>", "");
					strWindowsInstallerURL = tempStr;
					log.debug("Windows Installer URL:" + strWindowsInstallerURL);
				}
			}

			httpIn.close();
		} catch (Exception e) {
			log.error("Failed to get latest version data", e);
			return false;
		}
		return true;
	}

	/**
	 * Major latestVersionGet the
	 * @return Major latestVersion(floatType)
	 */
	public static float getLatestMajorVersionAsFloat() {
		float resultVersion = 0f;
		if((strLatestVersion != null) && (strLatestVersion.length() > 0)) {
			String strDot = strLatestVersion.contains("_") ? "_" : ".";
			String[] strSplit = strLatestVersion.split(strDot);

			if(strSplit.length >= 2) {
				String strTemp = strSplit[0] + "." + strSplit[1];
				try {
					resultVersion = Float.parseFloat(strTemp);
				} catch (NumberFormatException e) {}
			}
		}
		return resultVersion;
	}

	/**
	 * Minor version of the latestVersionGet the
	 * @return Minor version of the latestVersion(intType)
	 */
	public static int getLatestMinorVersionAsInt() {
		int resultVersion = 0;
		if((strLatestVersion != null) && (strLatestVersion.length() > 0)) {
			String strDot = strLatestVersion.contains("_") ? "_" : ".";
			String[] strSplit = strLatestVersion.split(strDot);

			if(strSplit.length >= 1) {
				String strTemp = strSplit[strSplit.length - 1];
				try {
					resultVersion = Integer.parseInt(strTemp);
				} catch (NumberFormatException e) {}
			}
		}
		return resultVersion;
	}

	/**
	 * The latest version ofVersion numberOfStringGets the type representation
	 * @return The latest version ofVersion numberOfStringType representation("7.0.0"Such as)
	 */
	public static String getLatestVersionFullString() {
		return getLatestMajorVersionAsFloat() + "." + getLatestMinorVersionAsInt();
	}

	/**
	 * Current versionThan the latest version ofVersionWho will determine whether the new
	 * @param nowMajor Current MajorVersion
	 * @param nowMinor Current MinorVersion
	 * @return The latest edition of the new and bettertrue
	 */
	public static boolean isNewVersionAvailable(float nowMajor, int nowMinor) {
		if(!isCompleted()) return false;

		float latestMajor = getLatestMajorVersionAsFloat();
		int latestMinor = getLatestMinorVersionAsInt();

		if(latestMajor > nowMajor) return true;
		if((latestMajor == nowMajor) && (latestMinor > nowMinor)) return true;

		return false;
	}

	/**
	 * Version check
	 * @param strURL Latest information entersXMLIn the fileURL(nullWhen I or an empty string default Using the value)
	 */
	public static void startCheckForUpdates(String strURL) {
		if((strURL == null) || (strURL.length() <= 0)) {
			strURLofXML = DEFAULT_XML_URL;
		} else {
			strURLofXML = strURL;
		}
		thread = new Thread(new UpdateChecker());
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * @return Thread is running(Loading)Iftrue
	 */
	public static boolean isRunning() {
		return (status == STATUS_LOADING);
	}

	/**
	 * @return Completed readingtrue
	 */
	public static boolean isCompleted() {
		return (status == STATUS_COMPLETE);
	}

	/**
	 * Current Gets the state
	 * @return Current State
	 */
	public static int getStatus() {
		return status;
	}

	/**
	 * XMLOfURLGet the
	 * @return XMLOfURL
	 */
	public static String getStrURLofXML() {
		return strURLofXML;
	}

	/**
	 * The latest version ofVersion number(Unformatted)Get the(7_0_0_0Such as)
	 * @return The latest version ofVersion number(Unformatted)
	 */
	public static String getStrLatestVersion() {
		return strLatestVersion;
	}

	/**
	 * Gets the date on which the latest version has been released
	 * @return Sun has released the latest version
	 */
	public static String getStrReleaseDate() {
		return strReleaseDate;
	}

	/**
	 * Where to download the latest versionURLGet the
	 * @return Where to download the latest versionURL
	 */
	public static String getStrDownloadURL() {
		return strDownloadURL;
	}

	/**
	 * Get the URL of Installer (*.exe) for Windows
	 * @return URL of Installer (*.exe) for Windows
	 */
	public static String getStrWindowsInstallerURL() {
		return strWindowsInstallerURL;
	}

	/**
	 *  event Adds a listener(Nothing happens and another has been added)
	 * @param l Add event Listener
	 */
	public static void addListener(UpdateCheckerListener l) {
		if(listeners == null) {
			listeners = new LinkedList<UpdateCheckerListener>();
		}
		if(listeners.contains(l)) {
			return;
		}
		listeners.add(l);
	}

	/**
	 *  event Removes a listener
	 * @param l Remove event Listener
	 * @return Has been deletedtrue, It has not been registered from the beginningfalse
	 */
	public static boolean removeListener(UpdateCheckerListener l) {
		if(listeners == null) {
			return false;
		}
		return listeners.remove(l);
	}

	/*
	 * Update check Processing of the thread
	 */
	public void run() {
		// Start
		status = STATUS_LOADING;
		if(listeners != null) {
			for(UpdateCheckerListener l : listeners) {
				l.onUpdateCheckerStart();
			}
		}

		// Update check
		if(checkUpdate() == true) {
			status = STATUS_COMPLETE;
		} else {
			status = STATUS_ERROR;
		}

		// End
		if(listeners != null) {
			for(UpdateCheckerListener l : listeners) {
				l.onUpdateCheckerEnd(status);
			}
		}
	}
}
