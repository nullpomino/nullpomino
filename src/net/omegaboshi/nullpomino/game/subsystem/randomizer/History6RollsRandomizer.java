package net.omegaboshi.nullpomino.game.subsystem.randomizer;

import org.game_host.hebo.nullpomino.game.component.Piece;

public class History6RollsRandomizer extends LimitedHistoryRandomizer {
	
	public History6RollsRandomizer() {
		super();
	}
	
	public History6RollsRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
	}
	
	public void init() {
		super.init();
		history = new int[] {Piece.PIECE_S, Piece.PIECE_Z, Piece.PIECE_S, Piece.PIECE_Z};
		numrolls = 6;
	}
}
