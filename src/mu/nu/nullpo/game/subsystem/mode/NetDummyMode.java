package mu.nu.nullpo.game.subsystem.mode;

import java.io.IOException;
import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.net.NetPlayerClient;
import mu.nu.nullpo.game.net.NetRoomInfo;
import mu.nu.nullpo.game.net.NetUtil;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.net.NetLobbyFrame;
import mu.nu.nullpo.gui.net.NetLobbyListener;

/**
 * Special base class for netplay
 */
public class NetDummyMode extends DummyMode implements NetLobbyListener {
	/** Log */
	static Logger log = Logger.getLogger(NetDummyMode.class);

	/** Lobby (Declared in NetDummyMode) */
	protected NetLobbyFrame netLobby;

	/** GameManager (Declared in NetDummyMode) */
	protected GameManager owner;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "NET-DUMMY";
	}

	/**
	 * Netplay Initialization. NetDummyMode will set the lobby's current mode to this.
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
		}
	}

	/**
	 * Netplay Unload. NetDummyMode will set the lobby's current mode to null.
	 */
	@Override
	public void netplayUnload(Object obj) {
		if(netLobby != null) {
			netLobby.setNetDummyMode(null);
			netLobby = null;
		}
	}

	/**
	 * Mode Initialization. NetDummyMode will set the "owner" variable.
	 */
	@Override
	public void modeInit(GameManager manager) {
		owner = manager;
	}

	/**
	 * Initialization for each player. NetDummyMode will stop and hide all players.
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		engine.stat = GameEngine.STAT_NOTHING;
		engine.isVisible = false;
	}

	/**
	 * Render something such as HUD. NetDummyMode will render the number of players to bottom-right of the screen.
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		if(playerID == getPlayers() - 1) netDrawAllPlayersCount(engine);
	}

	/**
	 * Initialization Completed (Never called)
	 */
	public void netlobbyOnInit(NetLobbyFrame lobby) {
	}

	/**
	 * Login completed (Never called)
	 */
	public void netlobbyOnLoginOK(NetLobbyFrame lobby, NetPlayerClient client) {
	}

	/**
	 * When you enter a room (Never called)
	 */
	public void netlobbyOnRoomJoin(NetLobbyFrame lobby, NetPlayerClient client, NetRoomInfo roomInfo) {
	}

	/**
	 * When you returned to lobby (Never called)
	 */
	public void netlobbyOnRoomLeave(NetLobbyFrame lobby, NetPlayerClient client) {
	}

	/*
	 * When disconnected
	 */
	public void netlobbyOnDisconnect(NetLobbyFrame lobby, NetPlayerClient client, Throwable ex) {
	}

	/*
	 * Message received
	 */
	public void netlobbyOnMessage(NetLobbyFrame lobby, NetPlayerClient client, String[] message) throws IOException {
	}

	/*
	 * When the lobby window is closed
	 */
	public void netlobbyOnExit(NetLobbyFrame lobby) {
	}

	/**
	 * Draw number of players to bottom-right of screen.
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
	 * Send field to all spectators
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
	 * Send next and hold piece informations to all spectators
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
}
