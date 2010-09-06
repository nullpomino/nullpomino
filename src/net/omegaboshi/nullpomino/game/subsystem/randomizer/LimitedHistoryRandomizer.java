package net.omegaboshi.nullpomino.game.subsystem.randomizer;

import mu.nu.nullpo.game.component.Piece;

public abstract class LimitedHistoryRandomizer extends Randomizer {

	int[] history;
	int id;
	int numrolls;

	boolean firstPiece;

	public LimitedHistoryRandomizer() {
		super();
	}

	public LimitedHistoryRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);

	}

	public void init() {
		firstPiece = true;
	}

	public int next() {
		if (firstPiece && !isPieceSZOOnly()) {
			do {
				id = r.nextInt(pieces.length);
			} while (pieces[id] == Piece.PIECE_O || pieces[id] == Piece.PIECE_Z || pieces[id] == Piece.PIECE_S);
			firstPiece = false;
		} else {
			for (int i = 0; i < numrolls; i++) {
				id = r.nextInt(pieces.length);
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
