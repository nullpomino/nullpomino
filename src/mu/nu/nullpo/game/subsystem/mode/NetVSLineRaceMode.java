package mu.nu.nullpo.game.subsystem.mode;

import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * NET-VS-LINE RACE mode
 */
public class NetVSLineRaceMode extends NetDummyVSMode {
	/** Number of lines required to win */
	private int goalLines;	// TODO: Add option to change this

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "NET-VS-LINE RACE";
	}

	/*
	 * Mode init
	 */
	@Override
	public void modeInit(GameManager manager) {
		super.modeInit(manager);
		goalLines = 40;
	}

	/*
	 * Player init
	 */
	@Override
	protected void netPlayerInit(GameEngine engine, int playerID) {
		super.netPlayerInit(engine, playerID);
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
	}

	/**
	 * Apply room settings, but ignore non-speed settings
	 */
	@Override
	protected void netvsApplyRoomSettings(GameEngine engine) {
		if(netCurrentRoomInfo != null) {
			engine.speed.gravity = netCurrentRoomInfo.gravity;
			engine.speed.denominator = netCurrentRoomInfo.denominator;
			engine.speed.are = netCurrentRoomInfo.are;
			engine.speed.areLine = netCurrentRoomInfo.areLine;
			engine.speed.lineDelay = netCurrentRoomInfo.lineDelay;
			engine.speed.lockDelay = netCurrentRoomInfo.lockDelay;
			engine.speed.das = netCurrentRoomInfo.das;
		}
	}

	/*
	 * Called at game start
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		super.startGame(engine, playerID);
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		engine.meterValue = owner.receiver.getMeterMax(engine);
	}

	/**
	 * Get player's place
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return Player's place
	 */
	private int getNowPlayerPlace(GameEngine engine, int playerID) {
		if(!netvsPlayerExist[playerID] || netvsPlayerDead[playerID]) return -1;

		int place = 0;
		int myLines = Math.min(engine.statistics.lines, goalLines);

		for(int i = 0; i < getPlayers(); i++) {
			if((i != playerID) && netvsPlayerExist[i] && !netvsPlayerDead[i]) {
				int enemyLines = Math.min(owner.engine[i].statistics.lines, goalLines);

				if(myLines < enemyLines) {
					place++;
				} else if((myLines == enemyLines) && (engine.statistics.pps < owner.engine[i].statistics.pps)) {
					place++;
				} else if((myLines == enemyLines) && (engine.statistics.pps == owner.engine[i].statistics.pps) &&
				          (engine.statistics.lpm < owner.engine[i].statistics.lpm))
				{
					place++;
				}
			}
		}

		return place;
	}

	/**
	 * Update progress meter
	 * @param engine GameEngine
	 */
	private void updateMeter(GameEngine engine) {
		if(goalLines > 0) {
			int remainLines = goalLines - engine.statistics.lines;
			engine.meterValue = (remainLines * owner.receiver.getMeterMax(engine)) / goalLines;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainLines <= 30) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainLines <= 20) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainLines <= 10) engine.meterColor = GameEngine.METER_COLOR_RED;
		}
	}

	/*
	 * Calculate Score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// Meter
		updateMeter(engine);

		// All clear
		if((lines >= 1) && (engine.field.isEmpty())) {
			engine.playSE("bravo");
		}

		// Game Completed
		if((engine.statistics.lines >= goalLines) && (playerID == 0)) {
			if(netvsIsPractice) {
				engine.stat = GameEngine.Status.EXCELLENT;
				engine.resetStatc();
			} else {
				// Send game end message
				int[] places = new int[NETVS_MAX_PLAYERS];
				int[] uidArray = new int[NETVS_MAX_PLAYERS];
				for(int i = 0; i < getPlayers(); i++) {
					places[i] = getNowPlayerPlace(owner.engine[i], i);
					uidArray[i] = -1;
				}
				for(int i = 0; i < getPlayers(); i++) {
					if((places[i] >= 0) && (places[i] < NETVS_MAX_PLAYERS)) {
						uidArray[places[i]] = netvsPlayerUID[i];
					}
				}

				String strMsg = "racewin";
				for(int i = 0; i < getPlayers(); i++) {
					if(uidArray[i] != -1) strMsg += "\t" + uidArray[i];
				}
				strMsg += "\n";
				netLobby.netPlayerClient.send(strMsg);

				// Wait until everyone dies
				engine.stat = GameEngine.Status.NOTHING;
				engine.resetStatc();
			}
		}
	}

	/*
	 * Drawing processing at the end of every frame
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		super.renderLast(engine, playerID);

		int x = owner.receiver.getFieldDisplayPositionX(engine, playerID);
		int y = owner.receiver.getFieldDisplayPositionY(engine, playerID);
		int fontColor = EventReceiver.COLOR_WHITE;

		if(netvsPlayerExist[playerID] && engine.isVisible) {
			if( ((netvsIsGameActive) || ((netvsIsPractice) && (playerID == 0))) && (engine.stat != GameEngine.Status.RESULT) ) {
				// Lines left
				int remainLines = Math.max(0, goalLines - engine.statistics.lines);
				fontColor = EventReceiver.COLOR_WHITE;
				if((remainLines <= 30) && (remainLines > 0)) fontColor = EventReceiver.COLOR_YELLOW;
				if((remainLines <= 20) && (remainLines > 0)) fontColor = EventReceiver.COLOR_ORANGE;
				if((remainLines <= 10) && (remainLines > 0)) fontColor = EventReceiver.COLOR_RED;

				String strLines = String.valueOf(remainLines);

				if(engine.displaysize != -1) {
					if(strLines.length() == 1) {
						owner.receiver.drawMenuFont(engine, playerID, 4, 21, strLines, fontColor, 2.0f);
					} else if(strLines.length() == 2) {
						owner.receiver.drawMenuFont(engine, playerID, 3, 21, strLines, fontColor, 2.0f);
					} else if(strLines.length() == 3) {
						owner.receiver.drawMenuFont(engine, playerID, 2, 21, strLines, fontColor, 2.0f);
					}
				} else {
					if(strLines.length() == 1) {
						owner.receiver.drawDirectFont(engine, playerID, x + 4 + 32, y + 168, strLines, fontColor, 1.0f);
					} else if(strLines.length() == 2) {
						owner.receiver.drawDirectFont(engine, playerID, x + 4 + 24, y + 168, strLines, fontColor, 1.0f);
					} else if(strLines.length() == 3) {
						owner.receiver.drawDirectFont(engine, playerID, x + 4 + 16, y + 168, strLines, fontColor, 1.0f);
					}
				}
			}

			if((netvsIsGameActive) && (engine.stat != GameEngine.Status.RESULT)) {
				// Place
				int place = getNowPlayerPlace(engine, playerID);
				if(netvsPlayerDead[playerID]) place = netvsPlayerPlace[playerID];

				if(engine.displaysize != -1) {
					if(place == 0) {
						owner.receiver.drawMenuFont(engine, playerID, -2, 22, "1ST", EventReceiver.COLOR_ORANGE);
					} else if(place == 1) {
						owner.receiver.drawMenuFont(engine, playerID, -2, 22, "2ND", EventReceiver.COLOR_WHITE);
					} else if(place == 2) {
						owner.receiver.drawMenuFont(engine, playerID, -2, 22, "3RD", EventReceiver.COLOR_RED);
					} else if(place == 3) {
						owner.receiver.drawMenuFont(engine, playerID, -2, 22, "4TH", EventReceiver.COLOR_GREEN);
					} else if(place == 4) {
						owner.receiver.drawMenuFont(engine, playerID, -2, 22, "5TH", EventReceiver.COLOR_BLUE);
					} else if(place == 5) {
						owner.receiver.drawMenuFont(engine, playerID, -2, 22, "6TH", EventReceiver.COLOR_PURPLE);
					}
				} else {
					if(place == 0) {
						owner.receiver.drawDirectFont(engine, playerID, x, y + 168, "1ST", EventReceiver.COLOR_ORANGE, 0.5f);
					} else if(place == 1) {
						owner.receiver.drawDirectFont(engine, playerID, x, y + 168, "2ND", EventReceiver.COLOR_WHITE, 0.5f);
					} else if(place == 2) {
						owner.receiver.drawDirectFont(engine, playerID, x, y + 168, "3RD", EventReceiver.COLOR_RED, 0.5f);
					} else if(place == 3) {
						owner.receiver.drawDirectFont(engine, playerID, x, y + 168, "4TH", EventReceiver.COLOR_GREEN, 0.5f);
					} else if(place == 4) {
						owner.receiver.drawDirectFont(engine, playerID, x, y + 168, "5TH", EventReceiver.COLOR_BLUE, 0.5f);
					} else if(place == 5) {
						owner.receiver.drawDirectFont(engine, playerID, x, y + 168, "6TH", EventReceiver.COLOR_PURPLE, 0.5f);
					}
				}
			}
			// Games count
			else if(!netvsIsPractice || (playerID != 0)) {
				String strTemp = netvsPlayerWinCount[playerID] + "/" + netvsPlayerPlayCount[playerID];

				if(engine.displaysize != -1) {
					int y2 = 21;
					if(engine.stat == GameEngine.Status.RESULT) y2 = 22;
					owner.receiver.drawMenuFont(engine, playerID, 0, y2, strTemp, EventReceiver.COLOR_WHITE);
				} else {
					owner.receiver.drawDirectFont(engine, playerID, x + 4, y + 168, strTemp, EventReceiver.COLOR_WHITE, 0.5f);
				}
			}
		}
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		super.renderResult(engine, playerID);

		float scale = 1.0f;
		if(engine.displaysize == -1) scale = 0.5f;

		drawResultScale(engine, playerID, owner.receiver, 2, EventReceiver.COLOR_ORANGE, scale,
				"LINE", String.format("%10d", engine.statistics.lines),
				"PIECE", String.format("%10d", engine.statistics.totalPieceLocked),
				"LINE/MIN", String.format("%10g", engine.statistics.lpm),
				"PIECE/SEC", String.format("%10g", engine.statistics.pps),
				"TIME", String.format("%10s", GeneralUtil.getTime(engine.statistics.time)));
	}

	/*
	 * Send stats
	 */
	@Override
	protected void netSendStats(GameEngine engine) {
		if((engine.playerID == 0) && !netvsIsPractice && !netvsIsWatch()) {
			String strMsg = "game\tstats\t" + engine.statistics.lines + "\t" + engine.statistics.pps + "\t" + engine.statistics.lpm + "\n";
			netLobby.netPlayerClient.send(strMsg);
		}
	}

	/*
	 * Receive stats
	 */
	@Override
	protected void netRecvStats(GameEngine engine, String[] message) {
		if(message.length > 4) engine.statistics.lines = Integer.parseInt(message[4]);
		if(message.length > 5) engine.statistics.pps = Float.parseFloat(message[5]);
		if(message.length > 6) engine.statistics.lpm = Float.parseFloat(message[6]);
		updateMeter(engine);
	}

	/*
	 * Send end-of-game stats
	 */
	@Override
	protected void netSendEndGameStats(GameEngine engine) {
		int playerID = engine.playerID;
		String msg = "gstat\t";
		msg += netvsPlayerPlace[playerID] + "\t";
		msg += 0 + "\t" + 0 + "\t" + 0 + "\t";
		msg += engine.statistics.lines + "\t" + engine.statistics.lpm + "\t";
		msg += engine.statistics.totalPieceLocked + "\t" + engine.statistics.pps + "\t";
		msg += netvsPlayTimer + "\t" + 0 + "\t" + netvsPlayerWinCount[playerID] + "\t" + netvsPlayerPlayCount[playerID];
		msg += "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/*
	 * Receive end-of-game stats
	 */
	@Override
	protected void netvsRecvEndGameStats(String[] message) {
		int seatID = Integer.parseInt(message[2]);
		int playerID = netvsGetPlayerIDbySeatID(seatID);

		if((playerID != 0) || (netvsIsWatch())) {
			GameEngine engine = owner.engine[playerID];

			engine.statistics.lines = Integer.parseInt(message[8]);
			engine.statistics.lpm = Float.parseFloat(message[9]);
			engine.statistics.totalPieceLocked = Integer.parseInt(message[10]);
			engine.statistics.pps = Float.parseFloat(message[11]);
			engine.statistics.time = Integer.parseInt(message[12]);

			netvsPlayerResultReceived[playerID] = true;
		}
	}
}
