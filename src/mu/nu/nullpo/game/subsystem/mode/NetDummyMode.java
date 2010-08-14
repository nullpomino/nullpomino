package mu.nu.nullpo.game.subsystem.mode;

import java.io.IOException;

import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.net.NetPlayerClient;
import mu.nu.nullpo.game.net.NetRoomInfo;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.net.NetLobbyFrame;
import mu.nu.nullpo.gui.net.NetLobbyListener;

/**
 * Special base class for netplay
 */
public class NetDummyMode extends DummyMode implements NetLobbyListener {
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
			netLobby.ruleOpt = new RuleOptions(owner.engine[0].ruleopt);
			netLobby.setNetDummyMode(this);
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

	public void netlobbyOnInit(NetLobbyFrame lobby) {
	}

	public void netlobbyOnLoginOK(NetLobbyFrame lobby, NetPlayerClient client) {
	}

	public void netlobbyOnRoomJoin(NetLobbyFrame lobby, NetPlayerClient client, NetRoomInfo roomInfo) {
	}

	public void netlobbyOnRoomLeave(NetLobbyFrame lobby, NetPlayerClient client) {
	}

	public void netlobbyOnDisconnect(NetLobbyFrame lobby, NetPlayerClient client, Throwable ex) {
	}

	public void netlobbyOnMessage(NetLobbyFrame lobby, NetPlayerClient client, String[] message) throws IOException {
	}

	public void netlobbyOnExit(NetLobbyFrame lobby) {
	}
}
