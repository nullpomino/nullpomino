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

import java.io.Serializable;

import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.play.GameEngine;

/**
 * Player information
 */
public class NetPlayerInfo implements Serializable {
	/** Serial version */
	private static final long serialVersionUID = 1L;

	/** Default rating for multiplayer games */
	public static final int DEFAULT_MULTIPLAYER_RATING = 1500;

	/** Name */
	public String strName = "";

	/** Country code */
	public String strCountry = "";

	/** Host */
	public String strHost = "";

	/** Team name */
	public String strTeam = "";

	/** Rules in use */
	public RuleOptions ruleOpt = null;

	/** Multiplayer rating */
	public int[] rating = new int[GameEngine.MAX_GAMESTYLE];

	/** Rating backup (internal use) */
	public int[] ratingBefore = new int[GameEngine.MAX_GAMESTYLE];

	/** Number of rated multiplayer games played */
	public int playCount = 0;

	/** Number of rated multiplayer games win */
	public int winCount = 0;

	/** User ID */
	public int uid = -1;

	/** Current room ID */
	public int roomID = -1;

	/** Game seat number (-1 if spectator) */
	public int seatID = -1;

	/** Join queue number (-1 if not in queue) */
	public int queueID = -1;

	/** true if "Ready" sign */
	public boolean ready = false;

	/** true if playing now */
	public boolean playing = false;

	/** true if connected */
	public boolean connected = false;

	/** true if this player is using tripcode */
	public boolean isTripUse = false;

	/**
	 * Constructor
	 */
	public NetPlayerInfo() {
	}

	/**
	 * Copy constructor
	 * @param n Copy source
	 */
	public NetPlayerInfo(NetPlayerInfo n) {
		copy(n);
	}

	/**
	 * String array constructor (Uses importStringArray)
	 * @param pdata String array (String[12])
	 */
	public NetPlayerInfo(String[] pdata) {
		importStringArray(pdata);
	}

	/**
	 * String constructor (Uses importString)
	 * @param str String(Divided by ;)
	 */
	public NetPlayerInfo(String str) {
		importString(str);
	}

	/**
	 * Copy from other NetPlayerInfo
	 * @param n Copy source
	 */
	public void copy(NetPlayerInfo n) {
		strName = n.strName;
		strCountry = n.strCountry;
		strHost = n.strHost;
		strTeam = n.strTeam;

		if(n.ruleOpt != null) {
			ruleOpt = new RuleOptions(n.ruleOpt);
		} else {
			ruleOpt = null;
		}

		for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
			rating[i] = n.rating[i];
			ratingBefore[i] = n.ratingBefore[i];
		}
		playCount = n.playCount;
		winCount = n.winCount;

		uid = n.uid;
		roomID = n.roomID;
		seatID = n.seatID;
		queueID = n.queueID;
		ready = n.ready;
		playing = n.playing;
		connected = n.connected;
		isTripUse = n.isTripUse;
	}

	/**
	 * Import from String array
	 * @param pdata String array (String[18])
	 */
	public void importStringArray(String[] pdata) {
		strName = NetUtil.urlDecode(pdata[0]);
		strCountry = NetUtil.urlDecode(pdata[1]);
		strHost = NetUtil.urlDecode(pdata[2]);
		strTeam = NetUtil.urlDecode(pdata[3]);
		playCount = Integer.parseInt(pdata[4]);
		winCount = Integer.parseInt(pdata[5]);
		roomID = Integer.parseInt(pdata[6]);
		uid = Integer.parseInt(pdata[7]);
		seatID = Integer.parseInt(pdata[8]);
		queueID = Integer.parseInt(pdata[9]);
		ready = Boolean.parseBoolean(pdata[10]);
		playing = Boolean.parseBoolean(pdata[11]);
		connected = Boolean.parseBoolean(pdata[12]);
		isTripUse = Boolean.parseBoolean(pdata[13]);
		rating[0] = Integer.parseInt(pdata[14]);
		rating[1] = Integer.parseInt(pdata[15]);
		rating[2] = Integer.parseInt(pdata[16]);
		rating[3] = Integer.parseInt(pdata[17]);
	}

	/**
	 * Import from String (Divided by ;)
	 * @param str String
	 */
	public void importString(String str) {
		importStringArray(str.split(";"));
	}

	/**
	 * Export to String array
	 * @return String array (String[18])
	 */
	public String[] exportStringArray() {
		String[] pdata = new String[18];
		pdata[0] = NetUtil.urlEncode(strName);
		pdata[1] = NetUtil.urlEncode(strCountry);
		pdata[2] = NetUtil.urlEncode(strHost);
		pdata[3] = NetUtil.urlEncode(strTeam);
		pdata[4] = Integer.toString(playCount);
		pdata[5] = Integer.toString(winCount);
		pdata[6] = Integer.toString(roomID);
		pdata[7] = Integer.toString(uid);
		pdata[8] = Integer.toString(seatID);
		pdata[9] = Integer.toString(queueID);
		pdata[10] = Boolean.toString(ready);
		pdata[11] = Boolean.toString(playing);
		pdata[12] = Boolean.toString(connected);
		pdata[13] = Boolean.toString(isTripUse);
		pdata[14] = Integer.toString(rating[0]);
		pdata[15] = Integer.toString(rating[1]);
		pdata[16] = Integer.toString(rating[2]);
		pdata[17] = Integer.toString(rating[3]);
		return pdata;
	}

	/**
	 * Export to String (Divided by ;)
	 * @return String
	 */
	public String exportString() {
		String[] data = exportStringArray();
		String strResult = "";

		for(int i = 0; i < data.length; i++) {
			strResult += data[i];
			if(i < data.length - 1) strResult += ";";
		}

		return strResult;
	}

	/**
	 * Reset play flags
	 */
	public void resetPlayState() {
		ready = false;
		playing = false;
	}

	/**
	 * Delete this player
	 */
	public void delete() {
		ruleOpt = null;
	}
}
