package net.tetrisconcept.poochy.nullpomino.ai;

import mu.nu.nullpo.game.play.GameEngine;

public class NohohoNoPrethink extends Nohoho {
	/*
	 * AI's name
	 */
	@Override
	public String getName() {
		return super.getName() + " (NO PRE-THINK)";
	}

	/*
	 * Called whenever a new piece is spawned
	 */
	@Override
	public void newPiece(GameEngine engine, int playerID) {
		if(!engine.aiUseThread) {
			thinkBestPosition(engine, playerID);
		} else {
			thinkRequest = true;
			thinkCurrentPieceNo++;
		}
	}

	/*
	 * Called at the start of each frame
	 */
	@Override
	public void onFirst(GameEngine engine, int playerID) {
	}
}
