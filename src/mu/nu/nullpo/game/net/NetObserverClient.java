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

import java.io.IOException;

import mu.nu.nullpo.game.play.GameManager;

import org.apache.log4j.Logger;

/**
 * クライアント(Observer用)
 */
public class NetObserverClient extends NetBaseClient {
	/** Log */
	static final Logger log = Logger.getLogger(NetObserverClient.class);

	/** サーバーVersion */
	protected volatile float serverVersion = 0f;

	/** Number of players */
	protected volatile int playerCount = 0;

	/** Observercount */
	protected volatile int observerCount = 0;

	/**
	 * Default constructor
	 */
	public NetObserverClient() {
		super();
	}

	/**
	 * Constructor
	 * @param host 接続先ホスト
	 */
	public NetObserverClient(String host) {
		super(host);
	}

	/**
	 * Constructor
	 * @param host 接続先ホスト
	 * @param port 接続先ポート number
	 */
	public NetObserverClient(String host, int port) {
		super(host, port);
	}

	/*
	 * 受信したメッセージに応じていろいろ処理をする
	 */
	@Override
	protected void processPacket(String fullMessage) throws IOException {
		String[] message = fullMessage.split("\t");	// タブ区切り

		// 接続完了
		if(message[0].equals("welcome")) {
			//welcome\t[VERSION]\t[PLAYERS]\t[OBSERVERS]
			serverVersion = Float.parseFloat(message[1]);
			playerCount = Integer.parseInt(message[2]);
			observerCount = Integer.parseInt(message[3]);
			send("observerlogin\t" + GameManager.getVersionMajor() + "\n");
		}
		// 人count更新
		if(message[0].equals("observerupdate")) {
			//observerupdate\t[PLAYERS]\t[OBSERVERS]
			playerCount = Integer.parseInt(message[1]);
			observerCount = Integer.parseInt(message[2]);
		}

		super.processPacket(fullMessage);
	}

	public float getServerVersion() {
		return serverVersion;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public int getObserverCount() {
		return observerCount;
	}
}
