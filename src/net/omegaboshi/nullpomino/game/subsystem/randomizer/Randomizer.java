package net.omegaboshi.nullpomino.game.subsystem.randomizer;

import java.util.Random;

import org.game_host.hebo.nullpomino.game.component.Piece;

public abstract class Randomizer {
	
	protected Random r;
	public int[] pieces;
	
	public Randomizer() {
		r = new Random(0);
		pieces = new int[0];
	}
	
	public Randomizer(boolean[] pieceEnable, long seed) {
		setPieceEnable(pieceEnable);
		reseed(seed);
	}
	
	public abstract int next();
	
	public void setPieceEnable(boolean[] pieceEnable) {
		int piece = 0;
		for (int i = 0; i < Piece.PIECE_COUNT; i++) {
			if  (pieceEnable[i]) piece++;
		}
		pieces = new int[piece];
		piece = 0;
		for (int i = 0; i < Piece.PIECE_COUNT; i++) {
			if (pieceEnable[i]) {
				pieces[piece] = i;
				piece++;
			}
		}
	}
	
	public void reseed(long seed) {
		r = new Random(seed);
	}
}
