package mu.nu.nullpo.game.subsystem.mode;

import java.io.IOException;

import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
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

	/** NET: Current room info (Declared in NetDummyMode) */
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
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		engine.stat = GameEngine.STAT_NOTHING;
		engine.isVisible = false;
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
	 * NET: Render something such as HUD. NetDummyMode will render the number of players to bottom-right of the screen.
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		if(playerID == getPlayers() - 1) netDrawAllPlayersCount(engine);
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

		if((netCurrentRoomInfo.roomID != -1) && (netLobby != null)) {
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
	 * NET: Send replay data<br>
	 * Game modes should implement this.
	 * @param engine GameEngine
	 */
	protected void netSendReplay(GameEngine engine) {
	}
}
