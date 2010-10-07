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
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

/**
 * クライアント(基本部分)
 */
public class NetBaseClient extends Thread {
	/** Log */
	static final Logger log = Logger.getLogger(NetBaseClient.class);

	/**  default のポート number */
	public static final int DEFAULT_PORT = 9200;

	/** 読み込みバッファのサイズ */
	public static final int BUF_SIZE = 2048;

	/** pingを打つ間隔(1000=1秒) */
	public static final int PING_INTERVAL = 10 * 1000;

	/** この countだけpingを打っても反応がない場合は自動切断 */
	public static final int PING_AUTO_DISCONNECT_COUNT = 3;

	/** trueの間スレッドが動く */
	public volatile boolean threadRunning;

	/** 正 always 接続している間true */
	public volatile boolean connectedFlag;

	/** 接続用ソケット */
	protected Socket socket;

	/** 接続先ホスト */
	protected String host;

	/** 接続先ポート number */
	protected int port;

	/** 前回の不完全パケット */
	protected StringBuilder notCompletePacketBuffer;

	/** メッセージ受け取りインターフェース */
	protected LinkedList<NetMessageListener> listeners = new LinkedList<NetMessageListener>();

	/** ping打った count(サーバーからpongメッセージを受信するとリセット) */
	protected int pingCount;

	/** 自動ping打ちTimer */
	protected Timer timerPing;

	/**
	 * Default constructor
	 */
	public NetBaseClient() {
		super();
		this.host = null;
		this.port = DEFAULT_PORT;
	}

	/**
	 * Constructor
	 * @param host 接続先ホスト
	 */
	public NetBaseClient(String host) {
		super();
		this.host = host;
		this.port = DEFAULT_PORT;
	}

	/**
	 * Constructor
	 * @param host 接続先ホスト
	 * @param port 接続先ポート number
	 */
	public NetBaseClient(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	/*
	 * スレッドの処理
	 */
	@Override
	public void run() {
		threadRunning = true;
		connectedFlag = false;
		log.info("Connecting to " + host + ":" + port);

		Throwable exDisconnectReason = null;

		try {
			// 接続
			socket = new Socket(host, port);
			connectedFlag = true;

			// ping打ちTimer準備
			pingCount = 0;
			TimerTask taskPing = new TimerTask() {
				@Override
				public void run() {
					if(isConnected()) {
						if(pingCount >= PING_AUTO_DISCONNECT_COUNT) {
							log.error("Ping timeout");
							threadRunning = false;
							connectedFlag = false;
							timerPing.cancel();
						} else {
							send("ping\n");
							pingCount++;

							if(pingCount >= 2) {
								log.debug("Ping " + pingCount + "/" + PING_AUTO_DISCONNECT_COUNT);
							}
						}
					} else {
						log.info("Ping Timer Cancelled");
						timerPing.cancel();
					}
				}
			};
			timerPing = new Timer(true);
			timerPing.schedule(taskPing, PING_INTERVAL, PING_INTERVAL);

			// メッセージ受信
			byte[] buf = new byte[BUF_SIZE];
			int size;

			while( (threadRunning) && ((size = socket.getInputStream().read(buf)) > 0) ) {
				String message = new String(buf, 0, size, "UTF-8");
				//log.debug(message);

				// 受信したメッセージに応じていろいろ処理をする
				StringBuilder packetBuffer = new StringBuilder();
				if(notCompletePacketBuffer != null) packetBuffer.append(notCompletePacketBuffer);
				packetBuffer.append(message);

				int index;
				while((index = packetBuffer.indexOf("\n")) != -1) {
					String msgNow = packetBuffer.substring(0, index);
					processPacket(msgNow);
					packetBuffer = packetBuffer.replace(0, index+1, "");
				}

				// 不完全パケットがある場合
				if(packetBuffer.length() > 0) {
					notCompletePacketBuffer = packetBuffer;
				} else {
					notCompletePacketBuffer = null;
				}
			}
		} catch (UnsupportedEncodingException e) {
			throw new Error("UTF-8 Not Supported", e);
		} catch (Exception e) {
			log.info("Socket disconnected", e);
			exDisconnectReason = e;
		}

		if(timerPing != null) timerPing.cancel();
		connectedFlag = false;
		threadRunning = false;

		// Listener
		for(int i = 0; i < listeners.size(); i++) {
			try {
				listeners.get(i).netOnDisconnect(this, exDisconnectReason);
			} catch (Exception e2) {
				log.debug("Uncaught Exception on NetMessageListener #" + i + " (disconnect event)", e2);
			}
		}
	}

	/**
	 * 受信したメッセージに応じていろいろ処理をする
	 * @param fullMessage 受信したメッセージ
	 * @throws IOException 何かエラーがあったとき
	 */
	protected void processPacket(String fullMessage) throws IOException {
		String[] message = fullMessage.split("\t");	// タブ区切り

		// ping返答
		if(message[0].equals("pong")) {
			if(pingCount >= 2) {
				log.debug("pong " + pingCount);
			}
			pingCount = 0;
		}

		// Listener呼び出し
		for(int i = 0; i < listeners.size(); i++) {
			try {
				listeners.get(i).netOnMessage(this, message);
			} catch (Exception e) {
				log.error("Uncaught Exception on NetMessageListener #" + i + " (message event)", e);
			}
		}
	}

	/**
	 * サーバーにメッセージを送信
	 * @param bytes 送信するメッセージ
	 * @return true if successful
	 */
	public boolean send(byte[] bytes) {
		try {
			socket.getOutputStream().write(bytes);
		} catch (Exception e) {
			log.error("Failed to send message", e);
			return false;
		}
		return true;
	}

	/**
	 * サーバーにメッセージを送信
	 * @param msg 送信するメッセージ
	 * @return true if successful
	 */
	public boolean send(String msg) {
		try {
			socket.getOutputStream().write(NetUtil.stringToBytes(msg));
		} catch (Exception e) {
			log.error("Failed to send message (" + msg + ")", e);
			return false;
		}
		return true;
	}

	/**
	 * @return 正 always 接続されているとtrue
	 */
	public boolean isConnected() {
		return (socket == null) ? false : (socket.isConnected() && connectedFlag);
	}

	/**
	 * @return 接続先ホスト
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return 接続先ポート number
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 新しいNetMessageListenerを追加
	 * @param l 追加するNetMessageListener
	 */
	public void addListener(NetMessageListener l) {
		if(!listeners.contains(l)) listeners.add(l);
	}

	/**
	 * 指定したNetMessageListenerを削除
	 * @param l 削除するNetMessageListener
	 * @return 実際に削除されたらtrue, もともと追加されてなかったらfalse
	 */
	public boolean removeListener(NetMessageListener l) {
		return listeners.remove(l);
	}
}
