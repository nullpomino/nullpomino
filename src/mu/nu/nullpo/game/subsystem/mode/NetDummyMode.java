package mu.nu.nullpo.game.subsystem.mode;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.zip.Adler32;

import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.component.Statistics;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.net.NetPlayerClient;
import mu.nu.nullpo.game.net.NetPlayerInfo;
import mu.nu.nullpo.game.net.NetRoomInfo;
import mu.nu.nullpo.game.net.NetSPRecord;
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

	/** NET: GameManager (Declared in NetDummyMode; Don't override it!) */
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
	protected int[] netRankingRank;

	/** NET: True if new personal record (Declared in NetDummyMode) */
	protected boolean netIsPB;

	/** NET: True if net ranking display mode (Declared in NetDummyMode) */
	protected boolean netIsNetRankingDisplayMode;

	/** NET: Net ranking cursor position (Declared in NetDummyMode) */
	protected int[] netRankingCursor;

	/** NET: Net ranking player's current rank (Declared in NetDummyMode) */
	protected int[] netRankingMyRank;

	/** NET: 0 if viewing all-time ranking, 1 if viewing daily ranking (Declared in NetDummyMode) */
	protected int netRankingView;

	/** NET: Net ranking type (Declared in NetDummyMode) */
	protected int netRankingType;

	/** NET: True if no data is present. [0] for all-time and [1] for daily. (Declared in NetDummyMode) */
	protected boolean[] netRankingNoDataFlag;

	/** NET: True if loading is complete. [0] for all-time and [1] for daily. (Declared in NetDummyMode) */
	protected boolean[] netRankingReady;

	/** NET: Net Rankings' rank (Declared in NetDummyMode) */
	protected LinkedList<Integer>[] netRankingPlace;

	/** NET: Net Rankings' names (Declared in NetDummyMode) */
	protected LinkedList<String>[] netRankingName;

	/** NET: Net Rankings' timestamps (Declared in NetDummyMode) */
	protected LinkedList<Calendar>[] netRankingDate;

	/** NET: Net Rankings' gamerates (Declared in NetDummyMode) */
	protected LinkedList<Float>[] netRankingGamerate;

	/** NET: Net Rankings' times (Declared in NetDummyMode) */
	protected LinkedList<Integer>[] netRankingTime;

	/** NET: Net Rankings' score (Declared in NetDummyMode) */
	protected LinkedList<Integer>[] netRankingScore;

	/** NET: Net Rankings' piece counts (Declared in NetDummyMode) */
	protected LinkedList<Integer>[] netRankingPiece;

	/** NET: Net Rankings' PPS values (Declared in NetDummyMode) */
	protected LinkedList<Float>[] netRankingPPS;

	/** NET: Net Rankings' line counts (Declared in NetDummyMode) */
	protected LinkedList<Integer>[] netRankingLines;

	/** NET: Net Rankings' score/line (Declared in NetDummyMode) */
	protected LinkedList<Double>[] netRankingSPL;

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
				netOnJoin(netLobby, netLobby.netPlayerClient, netLobby.netPlayerClient.getCurrentRoomInfo());
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
		netRankingCursor = new int[2];
		netRankingMyRank = new int[2];
		netRankingView = 0;
		netRankingNoDataFlag = new boolean[2];
		netRankingReady = new boolean[2];

		netRankingPlace = new LinkedList[2];
		netRankingName = new LinkedList[2];
		netRankingDate = new LinkedList[2];
		netRankingGamerate = new LinkedList[2];
		netRankingTime = new LinkedList[2];
		netRankingScore = new LinkedList[2];
		netRankingPiece = new LinkedList[2];
		netRankingPPS = new LinkedList[2];
		netRankingLines = new LinkedList[2];
		netRankingSPL = new LinkedList[2];
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
		netRankingRank = new int[2];
		netRankingRank[0] = -1;
		netRankingRank[1] = -1;
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
					// Send end-of-game messages
					if(netNumSpectators > 0) {
						netSendField(engine);
						netSendNextAndHold(engine);
						netSendStats(engine);
					}
					netSendEndGameStats(engine);
					netLobby.netPlayerClient.send("dead\t-1\n");
				} else if(engine.statc[0] >= engine.field.getHeight() + 1 + 180) {
					// To results screen
					netLobby.netPlayerClient.send("game\tresultsscreen\n");
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
	@Override
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
	 * NET: Update menu cursor. NetDummyMode will signal cursor movement to all spectators.
	 */
	@Override
	protected int updateCursor(GameEngine engine, int maxCursor, int playerID) {
		// NET: Don't execute in watch mode
		if(netIsWatch) return 0;

		int change = super.updateCursor(engine, maxCursor, playerID);

		// NET: Signal cursor change
		if((engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP) || engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) &&
			netIsNetPlay && (netNumSpectators > 0))
		{
			netLobby.netPlayerClient.send("game\tcursor\t" + engine.statc[2] + "\n");
		}

		return change;
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
				owner.engine[0].gameEnded();

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
			netRankingRank[0] = Integer.parseInt(message[1]);
			netIsPB = Boolean.parseBoolean(message[2]);
			netRankingRank[1] = Integer.parseInt(message[3]);
		}
		// Netplay Ranking
		if(message[0].equals("spranking")) {
			netRecvNetPlayRanking(owner.engine[0], message);
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
					engine.gameEnded();
					engine.stat = GameEngine.STAT_ENDINGSTART;
					engine.resetStatc();
				}
				// Retry
				if(message[3].equals("retry")) {
					engine.ending = 0;
					engine.gameEnded();
					engine.stat = GameEngine.STAT_SETTING;
					engine.resetStatc();
					engine.playSE("decide");
				}
				// Display results screen
				if(message[3].equals("resultsscreen")) {
					engine.field.reset();
					engine.stat = GameEngine.STAT_RESULT;
					engine.resetStatc();
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
	protected void netOnJoin(NetLobbyFrame lobby, NetPlayerClient client, NetRoomInfo roomInfo) {
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
			int d = netRankingView;

			if(!netRankingNoDataFlag[d] && netRankingReady[d] && (netRankingPlace != null) && (netRankingPlace[d] != null)) {
				// Up
				if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
					netRankingCursor[d]--;
					if(netRankingCursor[d] < 0) netRankingCursor[d] = netRankingPlace[d].size() - 1;
					engine.playSE("cursor");
				}
				// Down
				if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
					netRankingCursor[d]++;
					if(netRankingCursor[d] > netRankingPlace[d].size() - 1) netRankingCursor[d] = 0;
					engine.playSE("cursor");
				}
				// Download
				if(engine.ctrl.isPush(Controller.BUTTON_A)) {
					engine.playSE("decide");
					String strMsg = "spdownload\t" + NetUtil.urlEncode(netCurrentRoomInfo.ruleName) + "\t" +
									NetUtil.urlEncode(getName()) + "\t" + goaltype + "\t" +
									(netRankingView != 0) + "\t" + NetUtil.urlEncode(netRankingName[d].get(netRankingCursor[d])) + "\n";
					netLobby.netPlayerClient.send(strMsg);
					netIsNetRankingDisplayMode = false;
					owner.menuOnly = false;
				}
			}

			// Left/Right
			if(engine.ctrl.isPush(Controller.BUTTON_LEFT) || engine.ctrl.isPush(Controller.BUTTON_RIGHT)) {
				if(netRankingView == 0) netRankingView = 1;
				else netRankingView = 0;
				engine.playSE("change");
			}

			// Exit
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				netIsNetRankingDisplayMode = false;
				owner.menuOnly = false;
			}
		}
	}

	/**
	 * Render 1P NetPlay online ranking screen. Usually called from renderSetting(engine, playerID).
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param receiver EventReceiver
	 */
	protected void netOnRenderNetPlayRanking(GameEngine engine, int playerID, EventReceiver receiver) {
		if(netIsNetRankingDisplayMode) {
			int d = netRankingView;

			if(!netRankingNoDataFlag[d] && netRankingReady[d] && (netRankingPlace != null) && (netRankingPlace[d] != null)) {
				receiver.drawMenuFont(engine, playerID, 0, 1, "<<", EventReceiver.COLOR_ORANGE);
				receiver.drawMenuFont(engine, playerID, 38, 1, ">>", EventReceiver.COLOR_ORANGE);
				receiver.drawMenuFont(engine, playerID, 3, 1,
						((d != 0) ? "DAILY" : "ALL-TIME") + " RANKING (" + (netRankingCursor[d]+1) + "/" + netRankingPlace[d].size() + ")",
						EventReceiver.COLOR_GREEN);

				int startIndex = (netRankingCursor[d] / 20) * 20;
				int endIndex = startIndex + 20;
				if(endIndex > netRankingPlace[d].size()) endIndex = netRankingPlace[d].size();
				int c = 0;

				if(netRankingType == NetSPRecord.RANKINGTYPE_GENERIC_SCORE) {
					receiver.drawMenuFont(engine, playerID, 1, 3, "    SCORE   LINE TIME     NAME", EventReceiver.COLOR_BLUE);
				} else if(netRankingType == NetSPRecord.RANKINGTYPE_GENERIC_TIME) {
					receiver.drawMenuFont(engine, playerID, 1, 3, "    TIME     PIECE PPS    NAME", EventReceiver.COLOR_BLUE);
				} else if(netRankingType == NetSPRecord.RANKINGTYPE_SCORERACE) {
					receiver.drawMenuFont(engine, playerID, 1, 3, "    TIME     LINE SPL    NAME", EventReceiver.COLOR_BLUE);
				} else if(netRankingType == NetSPRecord.RANKINGTYPE_DIGRACE) {
					receiver.drawMenuFont(engine, playerID, 1, 3, "    TIME     LINE PIECE  NAME", EventReceiver.COLOR_BLUE);
				}

				for(int i = startIndex; i < endIndex; i++) {
					if(i == netRankingCursor[d]) {
						receiver.drawMenuFont(engine, playerID, 0, 4 + c, "b", EventReceiver.COLOR_RED);
					}

					int rankColor = (i == netRankingMyRank[d]) ? EventReceiver.COLOR_PINK : EventReceiver.COLOR_YELLOW;
					if(netRankingPlace[d].get(i) == -1) {
						receiver.drawMenuFont(engine, playerID, 1, 4 + c, "N/A", rankColor);
					} else {
						receiver.drawMenuFont(engine, playerID, 1, 4 + c, String.format("%3d", netRankingPlace[d].get(i)+1), rankColor);
					}

					if(netRankingType == NetSPRecord.RANKINGTYPE_GENERIC_SCORE) {
						receiver.drawMenuFont(engine, playerID, 5, 4 + c, "" + netRankingScore[d].get(i), (i == netRankingCursor[d]));
						receiver.drawMenuFont(engine, playerID, 13, 4 + c, "" + netRankingLines[d].get(i), (i == netRankingCursor[d]));
						receiver.drawMenuFont(engine, playerID, 18, 4 + c, GeneralUtil.getTime(netRankingTime[d].get(i)), (i == netRankingCursor[d]));
						receiver.drawTTFMenuFont(engine, playerID, 27, 4 + c, netRankingName[d].get(i), (i == netRankingCursor[d]));
					} else if(netRankingType == NetSPRecord.RANKINGTYPE_GENERIC_TIME) {
						receiver.drawMenuFont(engine, playerID, 5, 4 + c, GeneralUtil.getTime(netRankingTime[d].get(i)), (i == netRankingCursor[d]));
						receiver.drawMenuFont(engine, playerID, 14, 4 + c, "" + netRankingPiece[d].get(i), (i == netRankingCursor[d]));
						receiver.drawMenuFont(engine, playerID, 20, 4 + c, String.format("%.5g", netRankingPPS[d].get(i)), (i == netRankingCursor[d]));
						receiver.drawTTFMenuFont(engine, playerID, 27, 4 + c, netRankingName[d].get(i), (i == netRankingCursor[d]));
					} else if(netRankingType == NetSPRecord.RANKINGTYPE_SCORERACE) {
						receiver.drawMenuFont(engine, playerID, 5, 4 + c, GeneralUtil.getTime(netRankingTime[d].get(i)), (i == netRankingCursor[d]));
						receiver.drawMenuFont(engine, playerID, 14, 4 + c, "" + netRankingLines[d].get(i), (i == netRankingCursor[d]));
						receiver.drawMenuFont(engine, playerID, 19, 4 + c, String.format("%.5g", netRankingSPL[d].get(i)), (i == netRankingCursor[d]));
						receiver.drawTTFMenuFont(engine, playerID, 26, 4 + c, netRankingName[d].get(i), (i == netRankingCursor[d]));
					} else if(netRankingType == NetSPRecord.RANKINGTYPE_DIGRACE) {
						receiver.drawMenuFont(engine, playerID, 5, 4 + c, GeneralUtil.getTime(netRankingTime[d].get(i)), (i == netRankingCursor[d]));
						receiver.drawMenuFont(engine, playerID, 14, 4 + c, "" + netRankingLines[d].get(i), (i == netRankingCursor[d]));
						receiver.drawMenuFont(engine, playerID, 19, 4 + c, "" + netRankingPiece[d].get(i), (i == netRankingCursor[d]));
						receiver.drawTTFMenuFont(engine, playerID, 26, 4 + c, netRankingName[d].get(i), (i == netRankingCursor[d]));
					}

					c++;
				}

				if((netRankingCursor[d] >= 0) && (netRankingCursor[d] < netRankingDate[d].size())) {
					String strDate = "----/--/-- --:--:--";
					Calendar calendar = netRankingDate[d].get(netRankingCursor[d]);
					if(calendar != null) {
						strDate = GeneralUtil.getCalendarString(calendar, TimeZone.getDefault());
					}
					receiver.drawMenuFont(engine, playerID, 1, 25, "DATE:" + strDate, EventReceiver.COLOR_CYAN);

					float gamerate = netRankingGamerate[d].get(netRankingCursor[d]);
					receiver.drawMenuFont(engine, playerID, 1, 26, "GAMERATE:" + ((gamerate == 0f) ? "UNKNOWN" : (100*gamerate)+"%"),
							EventReceiver.COLOR_CYAN);
				}

				receiver.drawMenuFont(engine, playerID, 1, 28, "A:DOWNLOAD B:BACK LEFT/RIGHT:" + ((d == 0) ? "DAILY" : "ALL-TIME"),
						EventReceiver.COLOR_ORANGE);
			} else if(netRankingNoDataFlag[d]) {
				receiver.drawMenuFont(engine, playerID, 0, 1, "<<", EventReceiver.COLOR_ORANGE);
				receiver.drawMenuFont(engine, playerID, 38, 1, ">>", EventReceiver.COLOR_ORANGE);
				receiver.drawMenuFont(engine, playerID, 3, 1,
						((d != 0) ? "DAILY" : "ALL-TIME") + " RANKING",
						EventReceiver.COLOR_GREEN);

				receiver.drawMenuFont(engine, playerID, 1, 3, "NO DATA", EventReceiver.COLOR_DARKBLUE);

				receiver.drawMenuFont(engine, playerID, 1, 28, "B:BACK LEFT/RIGHT:" + ((d == 0) ? "DAILY" : "ALL-TIME"),
						EventReceiver.COLOR_ORANGE);
			} else if(!netRankingReady[d] && (netRankingPlace == null) || (netRankingPlace[d] == null)) {
				receiver.drawMenuFont(engine, playerID, 0, 1, "<<", EventReceiver.COLOR_ORANGE);
				receiver.drawMenuFont(engine, playerID, 38, 1, ">>", EventReceiver.COLOR_ORANGE);
				receiver.drawMenuFont(engine, playerID, 3, 1,
						((d != 0) ? "DAILY" : "ALL-TIME") + " RANKING",
						EventReceiver.COLOR_GREEN);

				receiver.drawMenuFont(engine, playerID, 1, 3, "LOADING...", EventReceiver.COLOR_CYAN);

				receiver.drawMenuFont(engine, playerID, 1, 28, "B:BACK LEFT/RIGHT:" + ((d == 0) ? "DAILY" : "ALL-TIME"),
						EventReceiver.COLOR_ORANGE);
			}
		}
	}

	/**
	 * Enter the netplay ranking screen
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param goaltype Game Type
	 */
	protected void netEnterNetPlayRankingScreen(GameEngine engine, int playerID, int goaltype) {
		if(netRankingPlace != null) {
			netRankingPlace[0] = null;
			netRankingPlace[1] = null;
		}
		netRankingCursor[0] = 0;
		netRankingCursor[1] = 0;
		netRankingMyRank[0] = -1;
		netRankingMyRank[1] = -1;
		netIsNetRankingDisplayMode = true;
		owner.menuOnly = true;
		netLobby.netPlayerClient.send("spranking\t" + NetUtil.urlEncode(netCurrentRoomInfo.ruleName) + "\t" +
				NetUtil.urlEncode(getName()) + "\t" + goaltype + "\t" + false + "\n");
		netLobby.netPlayerClient.send("spranking\t" + NetUtil.urlEncode(netCurrentRoomInfo.ruleName) + "\t" +
				NetUtil.urlEncode(getName()) + "\t" + goaltype + "\t" + true + "\n");
	}

	/**
	 * Receive 1P NetPlay ranking.
	 * @param engine GameEngine
	 * @param message Message array
	 */
	protected void netRecvNetPlayRanking(GameEngine engine, String[] message) {
		String strDebugTemp = "";
		for(int i = 0; i < message.length; i++) {
			strDebugTemp += message[i] + " ";
		}
		log.debug(strDebugTemp);

		if(message.length > 7) {
			boolean isDaily = Boolean.parseBoolean(message[4]);
			int d = isDaily ? 1 : 0;

			netRankingType = Integer.parseInt(message[5]);
			int maxRecords = Integer.parseInt(message[6]);
			String[] arrayRow = message[7].split(";");
			maxRecords = Math.min(maxRecords, arrayRow.length);

			netRankingNoDataFlag[d] = false;
			netRankingReady[d] = false;
			netRankingPlace[d] = new LinkedList<Integer>();
			netRankingName[d] = new LinkedList<String>();
			netRankingDate[d] = new LinkedList<Calendar>();
			netRankingGamerate[d] = new LinkedList<Float>();
			netRankingTime[d] = new LinkedList<Integer>();
			netRankingScore[d] = new LinkedList<Integer>();
			netRankingPiece[d] = new LinkedList<Integer>();
			netRankingPPS[d] = new LinkedList<Float>();
			netRankingLines[d] = new LinkedList<Integer>();
			netRankingSPL[d] = new LinkedList<Double>();

			for(int i = 0; i < maxRecords; i++) {
				String[] arrayData = arrayRow[i].split(",");
				netRankingPlace[d].add(Integer.parseInt(arrayData[0]));
				String pName = NetUtil.urlDecode(arrayData[1]);
				netRankingName[d].add(pName);
				netRankingDate[d].add(GeneralUtil.importCalendarString(arrayData[2]));
				netRankingGamerate[d].add(Float.parseFloat(arrayData[3]));

				if(netRankingType == NetSPRecord.RANKINGTYPE_GENERIC_SCORE) {
					netRankingScore[d].add(Integer.parseInt(arrayData[4]));
					netRankingLines[d].add(Integer.parseInt(arrayData[5]));
					netRankingTime[d].add(Integer.parseInt(arrayData[6]));
				} else if(netRankingType == NetSPRecord.RANKINGTYPE_GENERIC_TIME) {
					netRankingTime[d].add(Integer.parseInt(arrayData[4]));
					netRankingPiece[d].add(Integer.parseInt(arrayData[5]));
					netRankingPPS[d].add(Float.parseFloat(arrayData[6]));
				} else if(netRankingType == NetSPRecord.RANKINGTYPE_SCORERACE) {
					netRankingTime[d].add(Integer.parseInt(arrayData[4]));
					netRankingLines[d].add(Integer.parseInt(arrayData[5]));
					netRankingSPL[d].add(Double.parseDouble(arrayData[6]));
				} else if(netRankingType == NetSPRecord.RANKINGTYPE_DIGRACE) {
					netRankingTime[d].add(Integer.parseInt(arrayData[4]));
					netRankingLines[d].add(Integer.parseInt(arrayData[5]));
					netRankingPiece[d].add(Integer.parseInt(arrayData[6]));
				}

				if(pName.equals(netPlayerName)) {
					netRankingCursor[d] = i;
					netRankingMyRank[d] = i;
				}
			}

			netRankingReady[d] = true;
		} else if(message.length > 4) {
			boolean isDaily = Boolean.parseBoolean(message[4]);
			int d = isDaily ? 1 : 0;
			netRankingNoDataFlag[d] = true;
			netRankingReady[d] = false;
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
	 * Game modes should implement this. However, some basic codes are already implemented in NetDummyMode.
	 * @param engine GameEngine
	 */
	protected void netSendReplay(GameEngine engine) {
		NetSPRecord record = new NetSPRecord();
		record.setReplayProp(owner.replayProp);
		record.stats = new Statistics(engine.statistics);
		record.gameType = netGetGoalType();

		String strData = NetUtil.compressString(record.exportString());

		Adler32 checksumObj = new Adler32();
		checksumObj.update(NetUtil.stringToBytes(strData));
		long sChecksum = checksumObj.getValue();

		netLobby.netPlayerClient.send("spsend\t" + sChecksum + "\t" + strData + "\n");
	}

	/**
	 * NET: Get goal type (used from the default implementation of netSendReplay)<br>
	 * Game modes should implement this, unless there is only 1 goal type.
	 * @return Goal type (default implementation will return 0)
	 */
	protected int netGetGoalType() {
		return 0;
	}
}
