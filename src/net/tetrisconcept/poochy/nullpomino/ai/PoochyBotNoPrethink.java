package net.tetrisconcept.poochy.nullpomino.ai;

import org.game_host.hebo.nullpomino.game.component.Controller;
import org.game_host.hebo.nullpomino.game.component.Piece;
import org.game_host.hebo.nullpomino.game.play.GameEngine;

public class PoochyBotNoPrethink extends PoochyBot {
	/*
	 * AI's name
	 */
	@Override
	public String getName() {
		return super.getName() + " (NO PRE-THINK)";
	}

	/*
	 * 新しいピース出現時の処理
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
	 * 各フレームの最初の処理
	 */
	public void onFirst(GameEngine engine, int playerID) {
		inARE = engine.stat == GameEngine.STAT_ARE || engine.stat == GameEngine.STAT_READY;
		inputARE = 0;
		if(inARE && delay >= engine.aiMoveDelay) {
			int input = 0;
			Piece nextPiece = engine.getNextObject(engine.nextPieceCount);
			if (bestHold && thinkComplete)
			{
				input |= Controller.BUTTON_BIT_D;
				if (engine.holdPieceObject == null)
					nextPiece = engine.getNextObject(engine.nextPieceCount+1);
				else
					nextPiece = engine.holdPieceObject;
			}
			if (nextPiece == null)
				return;
			nextPiece = checkOffset(nextPiece, engine);
			input |= calcIRS(nextPiece, engine);
			if (threadRunning && !thinking && (thinkCurrentPieceNo <= thinkLastPieceNo))
			{
				int spawnX = engine.getSpawnPosX(engine.field, nextPiece);
				if(bestX - spawnX > 1) {
					// left
					//setDAS = -1;
					input |= Controller.BUTTON_BIT_LEFT;
				} else if(spawnX - bestX > 1) {
					// right
					//setDAS = 1;
					input |= Controller.BUTTON_BIT_RIGHT;
				}
				else
					setDAS = 0;
				delay = 0;
			}
			debugOut("Currently in ARE. Next piece type = " + nextPiece.id + ", IRS = " + input);
			//engine.ctrl.setButtonBit(input);
			inputARE = input;
		}
	}
}
