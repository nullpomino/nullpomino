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

/**
 * Player情報
 */
public class NetPlayerInfo implements Serializable {
	/** Serial version */
	private static final long serialVersionUID = 1L;

	/** Name */
	public String strName = "";

	/** Country code */
	public String strCountry = "";

	/** 接続元ホスト */
	public String strHost = "";

	/** Team name */
	public String strTeam = "";

	/** Rules in use */
	public RuleOptions ruleOpt = null;

	/** 識別用ID */
	public int uid = -1;

	/** 今いるルームのID */
	public int roomID = -1;

	/** ゲーム席 number(いないなら-1) */
	public int seatID = -1;

	/** 順番待ち number(いないなら-1) */
	public int queueID = -1;

	/** プレイ開始準備完了ならtrue */
	public boolean ready = false;

	/** プレイ中ならtrue */
	public boolean playing = false;

	/** 正 always 接続されているならtrue */
	public boolean connected = false;

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
	 * Stringの配列から dataを読み込むConstructor
	 * @param pdata Stringの配列(String[10])
	 */
	public NetPlayerInfo(String[] pdata) {
		importStringArray(pdata);
	}

	/**
	 * Stringら dataを読み込むConstructor
	 * @param str String(;で区切り)
	 */
	public NetPlayerInfo(String str) {
		importString(str);
	}

	/**
	 * 他のNetPlayerInfoからコピー
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

		uid = n.uid;
		roomID = n.roomID;
		seatID = n.seatID;
		queueID = n.queueID;
		ready = n.ready;
		playing = n.playing;
		connected = n.connected;
	}

	/**
	 * Stringの配列から data代入
	 * @param pdata Stringの配列(String[11])
	 */
	public void importStringArray(String[] pdata) {
		strName = NetUtil.urlDecode(pdata[0]);
		strCountry = NetUtil.urlDecode(pdata[1]);
		strHost = NetUtil.urlDecode(pdata[2]);
		strTeam = NetUtil.urlDecode(pdata[3]);
		roomID = Integer.parseInt(pdata[4]);
		uid = Integer.parseInt(pdata[5]);
		seatID = Integer.parseInt(pdata[6]);
		queueID = Integer.parseInt(pdata[7]);
		ready = Boolean.parseBoolean(pdata[8]);
		playing = Boolean.parseBoolean(pdata[9]);
		connected = Boolean.parseBoolean(pdata[10]);
	}

	/**
	 * String(;で区切り)から data代入
	 * @param str String
	 */
	public void importString(String str) {
		importStringArray(str.split(";"));
	}

	/**
	 * Stringの配列に変換
	 * @return Stringの配列(String[11])
	 */
	public String[] exportStringArray() {
		String[] pdata = new String[11];
		pdata[0] = NetUtil.urlEncode(strName);
		pdata[1] = NetUtil.urlEncode(strCountry);
		pdata[2] = NetUtil.urlEncode(strHost);
		pdata[3] = NetUtil.urlEncode(strTeam);
		pdata[4] = Integer.toString(roomID);
		pdata[5] = Integer.toString(uid);
		pdata[6] = Integer.toString(seatID);
		pdata[7] = Integer.toString(queueID);
		pdata[8] = Boolean.toString(ready);
		pdata[9] = Boolean.toString(playing);
		pdata[10] = Boolean.toString(connected);
		return pdata;
	}

	/**
	 * Stringに変換(;で区切り)
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
	 * プレイ中 flagをリセット
	 */
	public void resetPlayState() {
		ready = false;
		playing = false;
	}

	/**
	 * Player消去時の処理
	 */
	public void delete() {
		ruleOpt = null;
	}
}
