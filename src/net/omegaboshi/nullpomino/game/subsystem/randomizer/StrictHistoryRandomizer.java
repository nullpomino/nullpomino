package net.omegaboshi.nullpomino.game.subsystem.randomizer;

import org.game_host.hebo.nullpomino.game.component.Piece;

public class StrictHistoryRandomizer extends Randomizer {

	int[] history;
	int id;
	
	boolean[] curHist;
	int numDistinctCurHist;
	int[] notHist;
	int notHistPos;
	
	public StrictHistoryRandomizer() {
		super();
	}
	
	public StrictHistoryRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
	}
	
	public void init() {
		history = new int[] {Piece.PIECE_S, Piece.PIECE_Z, Piece.PIECE_O, Piece.PIECE_O};
		curHist = new boolean[pieces.length];
		notHist = new int[pieces.length];
	}
	
	public int next() {
		for (int i = 0; i < pieces.length; i++) {
			curHist[i] = false;
		}
		numDistinctCurHist = 0;
		for (int i = 0; i < 4; i++) {
			if (!curHist[history[i]]) {
				curHist[history[i]] = true;
				numDistinctCurHist++;
			}
		}
		notHistPos = 0;
		for (int i = 0; i < pieces.length; i++) {
			if (!curHist[i]) {
				notHist[notHistPos] = i;
				notHistPos++;
			}
		}
		id = notHist[r.nextInt(notHistPos)];
		for (int i = 3; i > 0; i--) {
			history[i] = history[i-1];
		}
		history[0] = pieces[id];
		return pieces[id];
	}
}
