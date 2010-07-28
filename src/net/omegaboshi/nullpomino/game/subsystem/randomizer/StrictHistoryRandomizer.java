package net.omegaboshi.nullpomino.game.subsystem.randomizer;

import org.game_host.hebo.nullpomino.game.component.Piece;

public class StrictHistoryRandomizer extends Randomizer {

	int[] history;
	int id;
	
	boolean[] curHist;
	int[] notHist;
	int notHistPos;
	
	public StrictHistoryRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
		history = new int[] {Piece.PIECE_S, Piece.PIECE_Z, Piece.PIECE_O, Piece.PIECE_T};
		curHist = new boolean[] {false, false, true, true, true, false, true, false, false, false, false};
		notHist = new int[3];
	}
	
	public int next() {
		notHistPos = 0;
		for (int i = 0; i < pieces.length; i++) {
			if (!curHist[i]) {
				notHist[notHistPos] = i;
				notHistPos++;
			}
		}
		id = notHist[r.nextInt(notHistPos)];
		
		curHist[pieces[id]] = true;
		curHist[history[3]] = false;
		
		for (int i = 3; i > 0; i--) {
			history[i] = history[i-1];
		}
		history[0] = pieces[id];
		return pieces[id];
	}
}
