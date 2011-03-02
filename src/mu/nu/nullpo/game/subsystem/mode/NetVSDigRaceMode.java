package mu.nu.nullpo.game.subsystem.mode;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * NET-VS-DIG RACE mode
 */
public class NetVSDigRaceMode extends NetDummyVSMode {
	/** Number of garbage lines to clear */
	private int goalLines;	// TODO: Add option to change this

	/** Number of garbage lines left */
	private int[] playerRemainLines;

	/** Number of gems available at the start of the game (for map game) */
	private int[] playerStartGems;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "NET-VS-DIG RACE";
	}

	/*
	 * Mode init
	 */
	@Override
	public void modeInit(GameManager manager) {
		super.modeInit(manager);
		goalLines = 18;
		playerRemainLines = new int[NETVS_MAX_PLAYERS];
		playerStartGems = new int[NETVS_MAX_PLAYERS];
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

	/**
	 * Fill the playfield with garbage
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	private void fillGarbage(GameEngine engine, int playerID) {
		int w = engine.field.getWidth();
		int h = engine.field.getHeight();
		int hole = -1;
		int skin = engine.getSkin();
		if((playerID != 0) || netvsIsWatch()) skin = netvsPlayerSkin[playerID];
		if(skin < 0) skin = 0;

		for(int y = h - 1; y >= h - goalLines; y--) {
			if((hole == -1) || (engine.random.nextInt(100) < netCurrentRoomInfo.garbagePercent)) {
				int newhole = -1;
				do {
					newhole = engine.random.nextInt(w);
				} while(newhole == hole);
				hole = newhole;
			}

			int prevColor = -1;
			for(int x = 0; x < w; x++) {
				if(x != hole) {
					int color = Block.BLOCK_COLOR_GRAY;
					if(y == h - 1) {
						do {
							color = Block.BLOCK_COLOR_GEM_RED + engine.random.nextInt(7);
						} while(color == prevColor);
						prevColor = color;
					}
					engine.field.setBlock(x,y,new Block(color,skin,Block.BLOCK_ATTRIBUTE_VISIBLE | Block.BLOCK_ATTRIBUTE_GARBAGE));
				}
			}

			// Set connections
			if(y != h - 1) {
				for(int x = 0; x < w; x++) {
					if(x != hole) {
						Block blk = engine.field.getBlock(x, y);
						if(blk != null) {
							if(!engine.field.getBlockEmpty(x-1, y)) blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, true);
							if(!engine.field.getBlockEmpty(x+1, y)) blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, true);
						}
					}
				}
			}
		}
	}

	/**
	 * Get number of garbage lines left
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return Number of garbage lines left
	 */
	private int getRemainGarbageLines(GameEngine engine, int playerID) {
		if((engine == null) || (engine.field == null)) return -1;

		int w = engine.field.getWidth();
		int h = engine.field.getHeight();
		int lines = 0;
		boolean hasGemBlock = false;

		for(int y = h - 1; y >= h - goalLines; y--) {
			if(!engine.field.getLineFlag(y)) {
				for(int x = 0; x < w; x++) {
					Block blk = engine.field.getBlock(x, y);

					if((blk != null) && (blk.isGemBlock())) {
						hasGemBlock = true;
					}
					if((blk != null) && (blk.getAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE))) {
						lines++;
						break;
					}
				}
			}
		}

		if(!hasGemBlock) return 0;

		return lines;
	}

	/**
	 * Turn all normal blocks to gem (for map game)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	private void turnAllBlocksToGem(GameEngine engine, int playerID) {
		int w = engine.field.getWidth();
		int h = engine.field.getHeight();

		for(int y = engine.field.getHighestBlockY(); y < h; y++) {
			for(int x = 0; x < w; x++) {
				Block blk = engine.field.getBlock(x, y);
				if((blk != null) && (blk.color >= Block.BLOCK_COLOR_RED) && (blk.color <= Block.BLOCK_COLOR_PURPLE)) {
					blk.color = Block.BLOCK_COLOR_GEM_RED + (blk.color - 2);
				}
			}
		}
	}

	/*
	 * Ready
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		super.onReady(engine, playerID);

		if((engine.statc[0] == 0) && netvsPlayerExist[playerID]) {
			if((netCurrentRoomInfo == null) || !netCurrentRoomInfo.useMap) {
				// Fill the field with garbage
				engine.createFieldIfNeeded();
				fillGarbage(engine, playerID);

				// Update meter
				int remainLines = getRemainGarbageLines(engine, playerID);
				playerRemainLines[playerID] = remainLines;
				engine.meterValue = remainLines * owner.receiver.getBlockGraphicsHeight(engine, playerID);
				engine.meterColor = GameEngine.METER_COLOR_GREEN;
			} else {
				// Map game
				engine.createFieldIfNeeded();
				turnAllBlocksToGem(engine, playerID);
				playerStartGems[playerID] = engine.field.getHowManyGems();
				playerRemainLines[playerID] = playerStartGems[playerID];
				engine.meterValue = owner.receiver.getMeterMax(engine);
				engine.meterColor = GameEngine.METER_COLOR_GREEN;
			}
		}
		return false;
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

		for(int i = 0; i < getPlayers(); i++) {
			if((i != playerID) && netvsPlayerExist[i] && !netvsPlayerDead[i] && (owner.engine[i].field != null)) {
				if(playerRemainLines[playerID] > playerRemainLines[i]) {
					place++;
				} else if( (playerRemainLines[playerID] == playerRemainLines[i]) &&
						   (engine.field.getHighestBlockY() < owner.engine[i].field.getHighestBlockY()) )
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
		int playerID = engine.playerID;
		int remainLines = 0;

		if((netCurrentRoomInfo == null) || !netCurrentRoomInfo.useMap) {
			// Normal game
			remainLines = playerRemainLines[playerID];
			engine.meterValue = remainLines * owner.receiver.getBlockGraphicsHeight(engine, playerID);
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainLines <= 14) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainLines <= 8) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainLines <= 4) engine.meterColor = GameEngine.METER_COLOR_RED;
		} else if((engine.field != null) && (playerStartGems[playerID] > 0)) {
			// Map game
			remainLines = engine.field.getHowManyGems() - engine.field.getHowManyGemClears();
			engine.meterValue = (remainLines * owner.receiver.getMeterMax(engine)) / playerStartGems[playerID];
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainLines <= playerStartGems[playerID] / 2) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainLines <= playerStartGems[playerID] / 3) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainLines <= playerStartGems[playerID] / 4) engine.meterColor = GameEngine.METER_COLOR_RED;
		}
	}

	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		if((lines > 0) && (playerID == 0)) {
			if((netCurrentRoomInfo == null) || !netCurrentRoomInfo.useMap) {
				playerRemainLines[playerID] = getRemainGarbageLines(engine, playerID);
			} else if(engine.field != null) {
				playerRemainLines[playerID] = engine.field.getHowManyGems() - engine.field.getHowManyGemClears();
			}
			updateMeter(engine);

			// Game Completed
			if(playerRemainLines[playerID] <= 0) {
				if(netvsIsPractice) {
					engine.stat = GameEngine.STAT_EXCELLENT;
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
					engine.stat = GameEngine.STAT_NOTHING;
					engine.resetStatc();
				}
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
			if( ((netvsIsGameActive) || ((netvsIsPractice) && (playerID == 0))) && (engine.stat != GameEngine.STAT_RESULT) ) {
				// Lines left
				int remainLines = Math.max(0, playerRemainLines[playerID]);
				fontColor = EventReceiver.COLOR_WHITE;
				if((remainLines <= 14) && (remainLines > 0)) fontColor = EventReceiver.COLOR_YELLOW;
				if((remainLines <=  8) && (remainLines > 0)) fontColor = EventReceiver.COLOR_ORANGE;
				if((remainLines <=  4) && (remainLines > 0)) fontColor = EventReceiver.COLOR_RED;

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

			if((netvsIsGameActive) && (engine.stat != GameEngine.STAT_RESULT)) {
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
					if(engine.stat == GameEngine.STAT_RESULT) y2 = 22;
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
		int playerID = engine.playerID;

		if((playerID == 0) && !netvsIsPractice && !netvsIsWatch()) {
			int remainLines = playerRemainLines[playerID];
			String strMsg = "game\tstats\t" + remainLines + "\n";
			netLobby.netPlayerClient.send(strMsg);
		}
	}

	/*
	 * Receive stats
	 */
	@Override
	protected void netRecvStats(GameEngine engine, String[] message) {
		int playerID = engine.playerID;
		if(message.length > 4) playerRemainLines[playerID] = Integer.parseInt(message[4]);
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
