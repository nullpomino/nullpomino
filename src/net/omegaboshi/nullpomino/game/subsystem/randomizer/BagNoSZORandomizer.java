package net.omegaboshi.nullpomino.game.subsystem.randomizer;

import mu.nu.nullpo.game.component.Piece;

public class BagNoSZORandomizer extends BagRandomizer {

	boolean firstBag = true;

	public BagNoSZORandomizer() {
		super();
	}

	public BagNoSZORandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
	}

	public void shuffle() {
		if (firstBag && !isPieceSZOOnly()) {
			do {
				super.shuffle();
			} while (bag[0] == Piece.PIECE_O || bag[0] == Piece.PIECE_Z || bag[0] == Piece.PIECE_S);
			firstBag = false;
		} else {
			super.shuffle();
		}
	}

}
