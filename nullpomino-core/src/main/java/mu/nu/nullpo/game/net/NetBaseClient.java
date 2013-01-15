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
 * Client(Basic part)
 */
public class NetBaseClient extends Thread {
	/** Log */
	static final Logger log = Logger.getLogger(NetBaseClient.class);

	/**  default Port of number */
	public static final int DEFAULT_PORT = 9200;

	/** The size of the read buffer */
	public static final int BUF_SIZE = 2048;

	/** Default ping interval (1000=1s) */
	public static final int PING_INTERVAL = 5 * 1000;

	/** This countOnlypingIf there is no reaction even hit the automatic disconnection */
	public static final int PING_AUTO_DISCONNECT_COUNT = 6;

	/** trueThread moves between */
	public volatile boolean threadRunning;

	/** Regular always While you are connectedtrue */
	public volatile boolean connectedFlag;

	/** Socket for connection */
	protected Socket socket;

	/** Destination host */
	protected String host;

	/** Destination port number */
	protected int port;

	/** IP address */
	protected String ip;

	/** Previous incomplete packet */
	protected StringBuilder notCompletePacketBuffer;

	/** Interface receiving messages */
	protected LinkedList<NetMessageListener> listeners = new LinkedList<NetMessageListener>();

	/** pingHit count(From serverpongReset When a message is received) */
	protected int pingCount;

	/** Ping task */
	protected TimerTask taskPing;

	/** AutomaticpingHitTimer */
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
	 * @param host Destination host
	 */
	public NetBaseClient(String host) {
		super("NET_"+host);
		this.host = host;
		this.port = DEFAULT_PORT;
	}

	/**
	 * Constructor
	 * @param host Destination host
	 * @param port Destination port number
	 */
	public NetBaseClient(String host, int port) {
		super("NET_"+host+":"+port);
		this.host = host;
		this.port = port;
	}

	/*
	 * Processing of the thread
	 */
	@Override
	public void run() {
		threadRunning = true;
		connectedFlag = false;
		log.info("Connecting to " + host + ":" + port);

		Throwable exDisconnectReason = null;

		try {
			// Connection
			socket = new Socket(host, port);
			connectedFlag = true;
			ip = socket.getInetAddress().getHostAddress();

			// pingHitTimerPreparation
			startPingTask();

			// Message reception
			byte[] buf = new byte[BUF_SIZE];
			int size;

			while( (threadRunning) && ((size = socket.getInputStream().read(buf)) > 0) ) {
				String message = new String(buf, 0, size, "UTF-8");
				//log.debug(message);

				// The various processing depending on the received message
				StringBuilder packetBuffer = new StringBuilder();
				if(notCompletePacketBuffer != null) packetBuffer.append(notCompletePacketBuffer);
				packetBuffer.append(message);

				int index;
				while((index = packetBuffer.indexOf("\n")) != -1) {
					String msgNow = packetBuffer.substring(0, index);
					processPacket(msgNow);
					packetBuffer = packetBuffer.replace(0, index+1, "");
				}

				// If there is an incomplete packet
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
	 * The various processing depending on the received message
	 * @param fullMessage Received Messages
	 * @throws IOException If there are any errors
	 */
	protected void processPacket(String fullMessage) throws IOException {
		String[] message = fullMessage.split("\t");	// Tab delimited

		// pingReply
		if(message[0].equals("pong")) {
			if(pingCount >= (PING_AUTO_DISCONNECT_COUNT / 2)) {
				log.debug("pong " + pingCount);
			}
			pingCount = 0;
		}

		// ListenerCall
		for(int i = 0; i < listeners.size(); i++) {
			try {
				listeners.get(i).netOnMessage(this, message);
			} catch (Exception e) {
				log.error("Uncaught Exception on NetMessageListener #" + i + " (message event)", e);
			}
		}
	}

	/**
	 * Send a message to the server
	 * @param bytes Message to be sent
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
	 * Send a message to the server
	 * @param msg Message to be sent
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
	 * @return Regular always And are connectedtrue
	 */
	public boolean isConnected() {
		return (socket == null) ? false : (socket.isConnected() && connectedFlag);
	}

	/**
	 * @return Destination host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return Destination port number
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return Server's IP address
	 */
	public String getIP() {
		return ip;
	}

	/**
	 * NewNetMessageListenerAdd
	 * @param l AddNetMessageListener
	 */
	public void addListener(NetMessageListener l) {
		if(!listeners.contains(l)) listeners.add(l);
	}

	/**
	 * SpecifiedNetMessageListenerDelete the
	 * @param l RemoveNetMessageListener
	 * @return Actually been removedtrue, I has not been added originallyfalse
	 */
	public boolean removeListener(NetMessageListener l) {
		return listeners.remove(l);
	}

	/**
	 * Start Ping timer task
	 */
	public void startPingTask() {
		startPingTask(PING_INTERVAL);
	}

	/**
	 * Start Ping timer task
	 * @param interval Interval
	 */
	public void startPingTask(long interval) {
		log.debug("Ping interval:" + interval);
		if(timerPing != null) timerPing.cancel();
		if(interval <= 0) return;
		pingCount = 0;
		taskPing = new PingTask();
		timerPing = new Timer(true);
		timerPing.schedule(taskPing, interval, interval);
	}

	/**
	 * Stop the Ping timer task
	 */
	public void stopPingTask() {
		if(timerPing != null) timerPing.cancel();
	}

	/**
	 * Ping task
	 */
	protected class PingTask extends TimerTask {
		@Override
		public void run() {
			try {
				if(isConnected()) {
					if(pingCount >= PING_AUTO_DISCONNECT_COUNT) {
						log.error("Ping timeout");
						threadRunning = false;
						connectedFlag = false;
						if(timerPing != null) timerPing.cancel();
					} else {
						send("ping\n");
						pingCount++;

						if(pingCount >= (PING_AUTO_DISCONNECT_COUNT / 2)) {
							log.debug("Ping " + pingCount + "/" + PING_AUTO_DISCONNECT_COUNT);
						}
					}
				} else {
					log.info("Ping Timer Cancelled");
					if(timerPing != null) timerPing.cancel();
				}
			} catch (Exception e) {
				log.error("Exception in Ping Timer. Stopping the task.", e);
				if(timerPing != null) timerPing.cancel();
			}
		}
	}
}
