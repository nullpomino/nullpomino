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
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 新Versionチェッカー
 */
public class UpdateChecker implements Runnable {
	/** Log */
	static Logger log = Logger.getLogger(UpdateChecker.class);

	/**  default のXMLのURL */
	public static final String DEFAULT_XML_URL = "http://dl.dropbox.com/u/6118422/NullpoUpdate.xml";

	/** 状態の定count */
	public static final int STATUS_INACTIVE = 0,
							STATUS_LOADING = 1,
							STATUS_ERROR = 2,
							STATUS_COMPLETE = 3;

	/** Current 状態 */
	private static volatile int status = 0;

	/**  event リスナー */
	private static LinkedList<UpdateCheckerListener> listeners = null;

	/** アップデート情報が書かれたXMLのURL */
	private static String strURLofXML = null;

	/** 最新版のVersion number */
	private static String strLatestVersion = null;

	/** リリース日 */
	private static String strReleaseDate = null;

	/** ダウンロードURL */
	private static String strDownloadURL = null;

	/** 更新 check 用スレッド */
	private static Thread thread = null;

	/**
	 * XMLをダウンロードしてVersion numberなどを取得
	 * @return true if successful
	 */
	private static boolean checkUpdate() {
		try {
			URL url = new URL(strURLofXML);
			HttpURLConnection httpCon = (HttpURLConnection)url.openConnection();
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
			}

			httpIn.close();
			httpCon.disconnect();
		} catch (Exception e) {
			log.error("Failed to get latest version data", e);
			return false;
		}
		return true;
	}

	/**
	 * 最新版のメジャーVersionを取得
	 * @return 最新版のメジャーVersion(float型)
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
	 * 最新版のマイナーVersionを取得
	 * @return 最新版のマイナーVersion(int型)
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
	 * 最新版のVersion numberのString型表現を取得
	 * @return 最新版のVersion numberのString型表現("7.0.0"など)
	 */
	public static String getLatestVersionFullString() {
		return getLatestMajorVersionAsFloat() + "." + getLatestMinorVersionAsInt();
	}

	/**
	 * Current versionよりも最新版のVersionの方が新しいか判定
	 * @param nowMajor Current メジャーVersion
	 * @param nowMinor Current マイナーVersion
	 * @return 最新版の方が新しいとtrue
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
	 * @param strURL 最新版の情報が入ったXMLファイルのURL(nullまたは空文字列にすると default 値を使う)
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
	 * @return スレッドが動作中(読み込み中)ならtrue
	 */
	public static boolean isRunning() {
		return (status == STATUS_LOADING);
	}

	/**
	 * @return 読み込み完了したらtrue
	 */
	public static boolean isCompleted() {
		return (status == STATUS_COMPLETE);
	}

	/**
	 * Current 状態を取得
	 * @return Current 状態
	 */
	public static int getStatus() {
		return status;
	}

	/**
	 * XMLのURLを取得
	 * @return XMLのURL
	 */
	public static String getStrURLofXML() {
		return strURLofXML;
	}

	/**
	 * 最新版のVersion number(未整形)を取得(7_0_0_0など)
	 * @return 最新版のVersion number(未整形)
	 */
	public static String getStrLatestVersion() {
		return strLatestVersion;
	}

	/**
	 * 最新版がリリースされた日を取得
	 * @return 最新版がリリースされた日
	 */
	public static String getStrReleaseDate() {
		return strReleaseDate;
	}

	/**
	 * 最新版のダウンロード先URLを取得
	 * @return 最新版のダウンロード先URL
	 */
	public static String getStrDownloadURL() {
		return strDownloadURL;
	}

	/**
	 *  event リスナーを追加(もう追加されていると何も起こりません)
	 * @param l 追加する event リスナー
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
	 *  event リスナーを削除
	 * @param l 削除する event リスナー
	 * @return 削除されたらtrue, 最初から登録されていなかったらfalse
	 */
	public static boolean removeListener(UpdateCheckerListener l) {
		if(listeners == null) {
			return false;
		}
		return listeners.remove(l);
	}

	/*
	 * 更新 check スレッドの処理
	 */
	public void run() {
		// 開始
		status = STATUS_LOADING;
		if(listeners != null) {
			for(UpdateCheckerListener l : listeners) {
				l.onUpdateCheckerStart();
			}
		}

		// 更新 check
		if(checkUpdate() == true) {
			status = STATUS_COMPLETE;
		} else {
			status = STATUS_ERROR;
		}

		// 終了
		if(listeners != null) {
			for(UpdateCheckerListener l : listeners) {
				l.onUpdateCheckerEnd(status);
			}
		}
	}
}
