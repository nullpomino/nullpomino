package net.omegaboshi.nullpomino.game.subsystem.randomizer;

import org.game_host.hebo.nullpomino.game.component.Piece;

public class History6RollsRandomizer extends Randomizer {

	int[] history;
	int id;
	
	boolean firstPiece;
	
	public History6RollsRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
		history = new int[] {Piece.PIECE_S, Piece.PIECE_Z, Piece.PIECE_S, Piece.PIECE_Z};
		firstPiece = true;
	}
	
	public int next() {
		if (firstPiece) {
			do {
				id = r.nextInt(pieces.length);
			} while (pieces[id] != Piece.PIECE_O && pieces[id] != Piece.PIECE_Z && pieces[id] != Piece.PIECE_S);
			firstPiece = false;
		} else {
			for (int i = 0; i < 6; i++) {
				id = r.nextInt(7);
				if (!(pieces[id] == history[0] || pieces[id] == history[1] || pieces[id] == history[2] || pieces[id] == history[3])) {
					break;
				}
			}
		}
		for (int i = 3; i > 0; i--) {
			history[i] = history[i-1];
		}
		history[0] = pieces[id];
		return pieces[id];
	}
}
