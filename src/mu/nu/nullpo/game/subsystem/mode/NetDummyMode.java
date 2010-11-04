package mu.nu.nullpo.game.subsystem.mode;

import java.io.IOException;

import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.net.NetPlayerClient;
import mu.nu.nullpo.game.net.NetPlayerInfo;
import mu.nu.nullpo.game.net.NetRoomInfo;
import mu.nu.nullpo.game.net.NetUtil;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.game.subsystem.wallkick.Wallkick;
import mu.nu.nullpo.gui.net.NetLobbyFrame;
import mu.nu.nullpo.gui.net.NetLobbyListener;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * Special base class for netplay
 */
public class NetDummyMode extends DummyMode implements NetLobbyListener {
	/** Log */
	static Logger log = Logger.getLogger(NetDummyMode.class);

	/** NET: Lobby (Declared in NetDummyMode) */
	protected NetLobbyFrame netLobby;

	/** NET: GameManager (Declared in NetDummyMode) */
	protected GameManager owner;

	/** NET: true if netplay (Declared in NetDummyMode) */
	protected boolean netIsNetPlay;

	/** NET: true if watch mode (Declared in NetDummyMode) */
	protected boolean netIsWatch;

	/** NET: Current room info. Sometimes null. (Declared in NetDummyMode) */
	protected NetRoomInfo netCurrentRoomInfo;

	/** NET: Number of spectators (Declared in NetDummyMode) */
	protected int netNumSpectators;

	/** NET: Previous piece informations (Declared in NetDummyMode) */
	protected int netPrevPieceID, netPrevPieceX, netPrevPieceY, netPrevPieceDir;

	/** NET: The skin player using (Declared in NetDummyMode) */
	protected int netPlayerSkin;

	/** NET: Player name (Declared in NetDummyMode) */
	protected String netPlayerName;

	/** NET: Replay send status (0:Before Send 1:Sending 2:Sent) (Declared in NetDummyMode) */
	protected int netReplaySendStatus;

	/** NET: Current round's online ranking rank (Declared in NetDummyMode) */
	protected int netRankingRank;

	/** NET: True if new personal record (Declared in NetDummyMode) */
	protected boolean netIsPB;

	/** NET: True if net ranking display mode (Declared in NetDummyMode) */
	protected boolean netIsNetRankingDisplayMode;

	/** NET: Net ranking cursor position (Declared in NetDummyMode) */
	protected int netRankingCursor;

	/** NET: Net ranking player's current rank (Declared in NetDummyMode) */
	protected int netRankingMyRank;

	/** NET: Net Rankings' rank (Declared in NetDummyMode) */
	protected int[] netRankingPlace;

	/** NET: Net Rankings' names (Declared in NetDummyMode) */
	protected String[] netRankingName;

	/*
	 * NET: Mode name
	 */
	@Override
	public String getName() {
		return "NET-DUMMY";
	}

	/**
	 * NET: Netplay Initialization. NetDummyMode will set the lobby's current mode to this.
	 */
	@Override
	public void netplayInit(Object obj) {
		if(obj instanceof NetLobbyFrame) {
			netLobby = (NetLobbyFrame)obj;
			netLobby.setNetDummyMode(this);

			try {
				netLobby.ruleOptPlayer = new RuleOptions(owner.engine[0].ruleopt);
			} catch (NullPointerException e) {
				log.error("NPE on netplayInit; Most likely the mode is overriding 'owner' variable", e);
			}

			if((netLobby != null) && (netLobby.netPlayerClient != null) && (netLobby.netPlayerClient.getCurrentRoomInfo() != null)) {
				onJoin(netLobby, netLobby.netPlayerClient, netLobby.netPlayerClient.getCurrentRoomInfo());
			}
		}
	}

	/**
	 * NET: Netplay Unload. NetDummyMode will set the lobby's current mode to null.
	 */
	@Override
	public void netplayUnload(Object obj) {
		if(netLobby != null) {
			netLobby.setNetDummyMode(null);
			netLobby = null;
		}
	}

	/**
	 * NET: Mode Initialization. NetDummyMode will set the "owner" variable.
	 */
	@Override
	public void modeInit(GameManager manager) {
		log.debug("modeInit() on NetDummyMode");
		owner = manager;
		netIsNetPlay = false;
		netIsWatch = false;
		netNumSpectators = 0;
		netPlayerName = "";
	}

	/**
	 * NET: Initialization for each player. NetDummyMode will stop and hide all players.
	 * Call netPlayerInit if you want to init NetPlay variables.
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		engine.stat = GameEngine.STAT_NOTHING;
		engine.isVisible = false;
	}

	/**
	 * NET: Initialize various NetPlay variables. Usually called from playerInit.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	protected void netPlayerInit(GameEngine engine, int playerID) {
		netPrevPieceID = Piece.PIECE_NONE;
		netPrevPieceX = 0;
		netPrevPieceY = 0;
		netPrevPieceDir = 0;
		netPlayerSkin = 0;
		netReplaySendStatus = 0;
		netRankingRank = -1;
		netIsPB = false;
		netIsNetRankingDisplayMode = false;

		if(netIsWatch) {
			engine.isNextVisible = false;
			engine.isHoldVisible = false;
		}
	}

	/**
	 * NET: When the pieces can move. NetDummyMode will send field/next/stats/piece movements.
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		// NET: Send field, next, and stats
		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false) &&
		   (netIsNetPlay) && (!netIsWatch) && (netNumSpectators > 0))
		{
			netSendField(engine);
			netSendStats(engine);
		}
		// NET: Send piece movement
		if((engine.ending == 0) && (netIsNetPlay) && (!netIsWatch) && (engine.nowPieceObject != null) && (netNumSpectators > 0))
		{
			if( ((engine.nowPieceObject == null) && (netPrevPieceID != Piece.PIECE_NONE)) || (engine.manualLock) )
			{
				netPrevPieceID = Piece.PIECE_NONE;
				netLobby.netPlayerClient.send("game\tpiece\t" + netPrevPieceID + "\t" + netPrevPieceX + "\t" + netPrevPieceY + "\t" +
						netPrevPieceDir + "\t" + 0 + "\t" + engine.getSkin() + "\n");
				netSendNextAndHold(engine);
			}
			else if((engine.nowPieceObject.id != netPrevPieceID) || (engine.nowPieceX != netPrevPieceX) ||
					(engine.nowPieceY != netPrevPieceY) || (engine.nowPieceObject.direction != netPrevPieceDir))
			{
				netPrevPieceID = engine.nowPieceObject.id;
				netPrevPieceX = engine.nowPieceX;
				netPrevPieceY = engine.nowPieceY;
				netPrevPieceDir = engine.nowPieceObject.direction;

				int x = netPrevPieceX + engine.nowPieceObject.dataOffsetX[netPrevPieceDir];
				int y = netPrevPieceY + engine.nowPieceObject.dataOffsetY[netPrevPieceDir];
				netLobby.netPlayerClient.send("game\tpiece\t" + netPrevPieceID + "\t" + x + "\t" + y + "\t" + netPrevPieceDir + "\t" +
								engine.nowPieceBottomY + "\t" + engine.ruleopt.pieceColor[netPrevPieceID] + "\t" + engine.getSkin() + "\n");
				netSendNextAndHold(engine);
			}
		}
		// NET: Stop game in watch mode
		if(netIsWatch) {
			return true;
		}

		return false;
	}

	/**
	 * NET: When the piece locked. NetDummyMode will send field and stats.
	 */
	@Override
	public void pieceLocked(GameEngine engine, int playerID, int lines) {
		// NET: Send field and stats
		if((engine.ending == 0) && (netIsNetPlay) && (!netIsWatch) && (netNumSpectators > 0)) {
			netSendField(engine);
			netSendStats(engine);
		}
	}

	/**
	 * NET: Line clear. NetDummyMode will send field and stats.
	 */
	@Override
	public boolean onLineClear(GameEngine engine, int playerID) {
		// NET: Send field and stats
		if((engine.statc[0] == 1) && (engine.ending == 0) && (netIsNetPlay) && (!netIsWatch) && (netNumSpectators > 0)) {
			netSendField(engine);
			netSendStats(engine);
		}
		return false;
	}

	/**
	 * NET: ARE. NetDummyMode will send field, next and stats.
	 */
	@Override
	public boolean onARE(GameEngine engine, int playerID) {
		// NET: Send field, next, and stats
		if((engine.statc[0] == 0) && (engine.ending == 0) && (netIsNetPlay) && (!netIsWatch) && (netNumSpectators > 0)) {
			netSendField(engine);
			netSendNextAndHold(engine);
			netSendStats(engine);
		}
		return false;
	}

	/**
	 * NET: Game Over
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		// NET: Send messages / Wait for messages
		if(netIsNetPlay){
			if(!netIsWatch) {
				if(engine.statc[0] == 0) {
					if(netNumSpectators > 0) {
						netSendField(engine);
						netSendNextAndHold(engine);
						netSendStats(engine);
					}
					netSendEndGameStats(engine);
					netLobby.netPlayerClient.send("dead\t-1\n");
				}
			} else {
				if(engine.statc[0] < engine.field.getHeight() + 1 + 180) {
					return false;
				} else {
					engine.field.reset();
					engine.stat = GameEngine.STAT_RESULT;
					engine.resetStatc();
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * NET: Results screen
	 */
	public boolean onResult(GameEngine engine, int playerID) {
		// NET: Retry
		if(netIsNetPlay) {
			engine.allowTextRenderByReceiver = false;

			// Replay Send
			if(netIsWatch || owner.replayMode) {
				netReplaySendStatus = 2;
			} else if(netReplaySendStatus == 0) {
				netReplaySendStatus = 1;
				netSendReplay(engine);
			}

			// Retry
			if(engine.ctrl.isPush(Controller.BUTTON_A) && !netIsWatch && (netReplaySendStatus == 2)) {
				engine.playSE("decide");
				if(netNumSpectators > 0) {
					netLobby.netPlayerClient.send("game\tretry\n");
					netSendOptions(engine);
				}
				owner.reset();
			}

			return true;
		}

		return false;
	}

	/**
	 * NET: Render something such as HUD. NetDummyMode will render the number of players to bottom-right of the screen.
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		if(playerID == getPlayers() - 1) netDrawAllPlayersCount(engine);
	}

	/**
	 * NET: Retry key
	 */
	@Override
	public void netplayOnRetryKey(GameEngine engine, int playerID) {
		if(netIsNetPlay && !netIsWatch) {
			owner.reset();
			netLobby.netPlayerClient.send("reset1p\n");
			netSendOptions(engine);
		}
	}

	/**
	 * NET: Initialization Completed (Never called)
	 */
	public void netlobbyOnInit(NetLobbyFrame lobby) {
	}

	/**
	 * NET: Login completed (Never called)
	 */
	public void netlobbyOnLoginOK(NetLobbyFrame lobby, NetPlayerClient client) {
	}

	/**
	 * NET: When you enter a room (Never called)
	 */
	public void netlobbyOnRoomJoin(NetLobbyFrame lobby, NetPlayerClient client, NetRoomInfo roomInfo) {
	}

	/**
	 * NET: When you returned to lobby (Never called)
	 */
	public void netlobbyOnRoomLeave(NetLobbyFrame lobby, NetPlayerClient client) {
	}

	/*
	 * NET: When disconnected
	 */
	public void netlobbyOnDisconnect(NetLobbyFrame lobby, NetPlayerClient client, Throwable ex) {
	}

	/*
	 * NET: Message received
	 */
	public void netlobbyOnMessage(NetLobbyFrame lobby, NetPlayerClient client, String[] message) throws IOException {
		// Player status update
		if(message[0].equals("playerupdate")) {
			netUpdatePlayerExist();
		}
		// When someone logout
		if(message[0].equals("playerlogout")) {
			NetPlayerInfo pInfo = new NetPlayerInfo(message[1]);

			if((netCurrentRoomInfo != null) && (pInfo.roomID == netCurrentRoomInfo.roomID)) {
				netUpdatePlayerExist();
			}
		}
		// Game started
		if(message[0].equals("start")) {
			log.debug("NET: Game started");

			if(netIsWatch) {
				owner.reset();
				owner.engine[0].stat = GameEngine.STAT_READY;
				owner.engine[0].resetStatc();
			}
		}
		// Dead
		if(message[0].equals("dead")) {
			log.debug("NET: Dead");

			if(netIsWatch) {
				owner.engine[0].gameActive = false;
				owner.engine[0].timerActive = false;

				if((owner.engine[0].stat != GameEngine.STAT_GAMEOVER) && (owner.engine[0].stat != GameEngine.STAT_RESULT)) {
					owner.engine[0].stat = GameEngine.STAT_GAMEOVER;
					owner.engine[0].resetStatc();
				}
			}
		}
		// Replay send fail
		if(message[0].equals("spsendng")) {
			netReplaySendStatus = 1;
			netSendReplay(owner.engine[0]);
		}
		// Replay send complete
		if(message[0].equals("spsendok")) {
			netReplaySendStatus = 2;
			netRankingRank = Integer.parseInt(message[1]);
			netIsPB = Boolean.parseBoolean(message[2]);
		}
		// Reset
		if(message[0].equals("reset1p")) {
			if(netIsWatch) {
				owner.reset();
			}
		}
		// Game messages
		if(message[0].equals("game")) {
			if(netIsWatch) {
				GameEngine engine = owner.engine[0];
				if(engine.field == null) {
					engine.field = new Field();
				}

				// Move cursor
				if(message[3].equals("cursor")) {
					if(engine.stat == GameEngine.STAT_SETTING) {
						engine.statc[2] = Integer.parseInt(message[4]);
						engine.playSE("cursor");
					}
				}
				// Change game options
				if(message[3].equals("option")) {
					netRecvOptions(engine, message);
				}
				// Field
				if(message[3].equals("field")) {
					netRecvField(engine, message);
				}
				// Stats
				if(message[3].equals("stats")) {
					netRecvStats(engine, message);
				}
				// Current Piece
				if(message[3].equals("piece")) {
					int id = Integer.parseInt(message[4]);

					if(id >= 0) {
						int pieceX = Integer.parseInt(message[5]);
						int pieceY = Integer.parseInt(message[6]);
						int pieceDir = Integer.parseInt(message[7]);
						//int pieceBottomY = Integer.parseInt(message[8]);
						int pieceColor = Integer.parseInt(message[9]);
						int pieceSkin = Integer.parseInt(message[10]);

						engine.nowPieceObject = new Piece(id);
						engine.nowPieceObject.direction = pieceDir;
						engine.nowPieceObject.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
						engine.nowPieceObject.setColor(pieceColor);
						engine.nowPieceObject.setSkin(pieceSkin);
						engine.nowPieceX = pieceX;
						engine.nowPieceY = pieceY;
						//engine.nowPieceBottomY = pieceBottomY;
						engine.nowPieceObject.updateConnectData();
						engine.nowPieceBottomY =
							engine.nowPieceObject.getBottom(pieceX, pieceY, engine.field);

						if((engine.stat != GameEngine.STAT_EXCELLENT) && (engine.stat != GameEngine.STAT_GAMEOVER) &&
						   (engine.stat != GameEngine.STAT_RESULT))
						{
							engine.gameActive = true;
							engine.timerActive = true;
							engine.stat = GameEngine.STAT_MOVE;
							engine.statc[0] = 2;
						}

						netPlayerSkin = pieceSkin;
					} else {
						engine.nowPieceObject = null;
					}
				}
				// Next and Hold
				if(message[3].equals("next")) {
					netRecvNextAndHold(engine, message);
				}
				// Ending
				if(message[3].equals("ending")) {
					engine.ending = 1;
					engine.timerActive = false;
					engine.gameActive = false;
					engine.stat = GameEngine.STAT_ENDINGSTART;
					engine.resetStatc();
				}
				// Retry
				if(message[3].equals("retry")) {
					engine.ending = 0;
					engine.timerActive = false;
					engine.gameActive = false;
					engine.stat = GameEngine.STAT_SETTING;
					engine.resetStatc();
					engine.playSE("decide");
				}
			}
		}
	}

	/*
	 * NET: When the lobby window is closed
	 */
	public void netlobbyOnExit(NetLobbyFrame lobby) {
	}

	/**
	 * NET: When you join the room
	 * @param lobby NetLobbyFrame
	 * @param client NetPlayerClient
	 * @param roomInfo NetRoomInfo
	 */
	protected void onJoin(NetLobbyFrame lobby, NetPlayerClient client, NetRoomInfo roomInfo) {
		log.debug("onJoin on NetDummyMode");

		netCurrentRoomInfo = roomInfo;
		netIsNetPlay = true;
		netIsWatch = (netLobby.netPlayerClient.getYourPlayerInfo().seatID == -1);
		netNumSpectators = 0;
		netUpdatePlayerExist();

		if(netIsWatch) {
			owner.engine[0].isNextVisible = false;
			owner.engine[0].isHoldVisible = false;
		}

		if(roomInfo != null) {
			// Set to locked rule
			if((roomInfo.ruleLock) && (netLobby != null) && (netLobby.ruleOptLock != null)) {
				log.info("Set locked rule");
				Randomizer randomizer = GeneralUtil.loadRandomizer(netLobby.ruleOptLock.strRandomizer);
				Wallkick wallkick = GeneralUtil.loadWallkick(netLobby.ruleOptLock.strWallkick);
				owner.engine[0].ruleopt.copy(netLobby.ruleOptLock);
				owner.engine[0].randomizer = randomizer;
				owner.engine[0].wallkick = wallkick;
				//loadRanking(owner.modeConfig, owner.engine[0].ruleopt.strRuleName);
			}
		}
	}

	/**
	 * NET: Update player count
	 */
	protected void netUpdatePlayerExist() {
		netNumSpectators = 0;
		netPlayerName = "";

		if((netCurrentRoomInfo != null) && (netCurrentRoomInfo.roomID != -1) && (netLobby != null)) {
			for(NetPlayerInfo pInfo: netLobby.updateSameRoomPlayerInfoList()) {
				if(pInfo.roomID == netCurrentRoomInfo.roomID) {
					if(pInfo.seatID == 0) {
						netPlayerName = pInfo.strName;
					} else if(pInfo.seatID == -1) {
						netNumSpectators++;
					}
				}
			}
		}
	}

	/**
	 * NET: Draw number of players to bottom-right of screen.
	 * This subroutine uses "netLobby" and "owner" variables.
	 * @param engine GameEngine
	 */
	protected void netDrawAllPlayersCount(GameEngine engine) {
		if((netLobby != null) && (netLobby.netPlayerClient != null) && (netLobby.netPlayerClient.isConnected())) {
			int fontcolor = EventReceiver.COLOR_BLUE;
			if(netLobby.netPlayerClient.getObserverCount() > 0) fontcolor = EventReceiver.COLOR_GREEN;
			if(netLobby.netPlayerClient.getPlayerCount() > 1) fontcolor = EventReceiver.COLOR_RED;
			String strObserverInfo = String.format("%d/%d", netLobby.netPlayerClient.getObserverCount(), netLobby.netPlayerClient.getPlayerCount());
			String strObserverString = String.format("%40s", strObserverInfo);
			owner.receiver.drawDirectFont(engine, 0, 0, 480-16, strObserverString, fontcolor);
		}
	}

	/**
	 * NET: Send field to all spectators
	 * @param engine GameEngine
	 */
	protected void netSendField(GameEngine engine) {
		String strSrcFieldData = engine.field.fieldToString();
		int nocompSize = strSrcFieldData.length();

		String strCompFieldData = NetUtil.compressString(strSrcFieldData);
		int compSize = strCompFieldData.length();

		String strFieldData = strSrcFieldData;
		boolean isCompressed = false;
		if(compSize < nocompSize) {
			strFieldData = strCompFieldData;
			isCompressed = true;
		}

		String msg = "game\tfield\t";
		msg += engine.getSkin() + "\t";
		msg += engine.field.getHeightWithoutHurryupFloor() + "\t";
		msg += strFieldData + "\t" + isCompressed + "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/**
	 * NET: Receive field message
	 * @param engine GameEngine
	 * @param message Message array
	 */
	protected void netRecvField(GameEngine engine, String[] message) {
		if(message.length > 5) {
			engine.nowPieceObject = null;
			engine.holdDisable = false;
			if(engine.stat == GameEngine.STAT_SETTING) engine.stat = GameEngine.STAT_MOVE;
			int skin = Integer.parseInt(message[4]);
			int highestWallY = Integer.parseInt(message[5]);
			netPlayerSkin = skin;
			if(message.length > 7) {
				String strFieldData = message[6];
				boolean isCompressed = Boolean.parseBoolean(message[7]);
				if(isCompressed) {
					strFieldData = NetUtil.decompressString(strFieldData);
				}
				engine.field.stringToField(strFieldData, skin, highestWallY, highestWallY);
			} else {
				engine.field.reset();
			}
		}
	}

	/**
	 * NET: Send next and hold piece informations to all spectators
	 * @param engine GameEngine
	 */
	protected void netSendNextAndHold(GameEngine engine) {
		int holdID = Piece.PIECE_NONE;
		int holdDirection = Piece.DIRECTION_UP;
		int holdColor = Block.BLOCK_COLOR_GRAY;
		if(engine.holdPieceObject != null) {
			holdID = engine.holdPieceObject.id;
			holdDirection = engine.holdPieceObject.direction;
			holdColor = engine.ruleopt.pieceColor[engine.holdPieceObject.id];
		}

		String msg = "game\tnext\t" + engine.ruleopt.nextDisplay + "\t" + engine.holdDisable + "\t";

		for(int i = -1; i < engine.ruleopt.nextDisplay; i++) {
			if(i < 0) {
				msg += holdID + ";" + holdDirection + ";" + holdColor;
			} else {
				Piece nextObj = engine.getNextObject(engine.nextPieceCount + i);
				msg += nextObj.id + ";" + nextObj.direction + ";" + engine.ruleopt.pieceColor[nextObj.id];
			}

			if(i < engine.ruleopt.nextDisplay - 1) msg += "\t";
		}

		msg += "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/**
	 * NET: Receive next and hold piece informations
	 * @param engine GameEngine
	 * @param message Message array
	 */
	protected void netRecvNextAndHold(GameEngine engine, String[] message) {
		int maxNext = Integer.parseInt(message[4]);
		engine.ruleopt.nextDisplay = maxNext;
		engine.holdDisable = Boolean.parseBoolean(message[5]);

		for(int i = 0; i < maxNext + 1; i++) {
			if(i + 6 < message.length) {
				String[] strPieceData = message[i + 6].split(";");
				int pieceID = Integer.parseInt(strPieceData[0]);
				int pieceDirection = Integer.parseInt(strPieceData[1]);
				int pieceColor = Integer.parseInt(strPieceData[2]);

				if(i == 0) {
					if(pieceID == Piece.PIECE_NONE) {
						engine.holdPieceObject = null;
					} else {
						engine.holdPieceObject = new Piece(pieceID);
						engine.holdPieceObject.direction = pieceDirection;
						engine.holdPieceObject.setColor(pieceColor);
						engine.holdPieceObject.setSkin(netPlayerSkin);
					}
				} else {
					if((engine.nextPieceArrayObject == null) || (engine.nextPieceArrayObject.length < maxNext)) {
						engine.nextPieceArrayObject = new Piece[maxNext];
					}
					engine.nextPieceArrayObject[i - 1] = new Piece(pieceID);
					engine.nextPieceArrayObject[i - 1].direction = pieceDirection;
					engine.nextPieceArrayObject[i - 1].setColor(pieceColor);
					engine.nextPieceArrayObject[i - 1].setSkin(netPlayerSkin);
				}
			}
		}

		engine.isNextVisible = true;
		engine.isHoldVisible = true;
	}

	/**
	 * Menu routine for 1P NetPlay online ranking screen. Usually called from onSetting(engine, playerID).
	 * @param engine GameEngine
	 * @param goaltype Goal Type
	 */
	protected void netOnUpdateNetPlayRanking(GameEngine engine, int goaltype) {
		if(netIsNetRankingDisplayMode) {
			if((netRankingName != null) && (netRankingName.length > 0)) {
				// Up
				if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
					netRankingCursor--;
					if(netRankingCursor < 0) netRankingCursor = netRankingPlace.length - 1;
					engine.playSE("cursor");
				}
				// Down
				if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
					netRankingCursor++;
					if(netRankingCursor > netRankingPlace.length - 1) netRankingCursor = 0;
					engine.playSE("cursor");
				}
				// Download
				if(engine.ctrl.isPush(Controller.BUTTON_A)) {
					engine.playSE("decide");
					String strMsg = "spdownload\t" + netCurrentRoomInfo.ruleName + "\t" + getName() + "\t" + goaltype + "\t"
									+ NetUtil.urlEncode(netRankingName[netRankingCursor]) + "\n";
					netLobby.netPlayerClient.send(strMsg);
					netIsNetRankingDisplayMode = false;
					owner.menuOnly = false;
				}
			}

			// Exit
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				netIsNetRankingDisplayMode = false;
				owner.menuOnly = false;
			}
		}
	}

	/**
	 * NET: Send various in-game stats (as well as goaltype)<br>
	 * Game modes should implement this.
	 * @param engine GameEngine
	 */
	protected void netSendStats(GameEngine engine) {
	}

	/**
	 * NET: Receive various in-game stats (as well as goaltype)<br>
	 * Game modes should implement this.
	 * @param engine GameEngine
	 * @param message Message array
	 */
	protected void netRecvStats(GameEngine engine, String[] message) {
	}

	/**
	 * NET: Send end-of-game stats<br>
	 * Game modes should implement this.
	 * @param engine GameEngine
	 */
	protected void netSendEndGameStats(GameEngine engine) {
	}

	/**
	 * NET: Send game options to all spectators<br>
	 * Game modes should implement this.
	 * @param engine GameEngine
	 */
	protected void netSendOptions(GameEngine engine) {
	}

	/**
	 * NET: Receive game options.<br>
	 * Game modes should implement this.
	 * @param engine GameEngine
	 * @param message Message array
	 */
	protected void netRecvOptions(GameEngine engine, String[] message) {
	}

	/**
	 * NET: Send replay data<br>
	 * Game modes should implement this.
	 * @param engine GameEngine
	 */
	protected void netSendReplay(GameEngine engine) {
	}
}
