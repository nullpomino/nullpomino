package net.omegaboshi.nullpomino.game.subsystem.randomizer;

import java.util.Random;

import org.game_host.hebo.nullpomino.game.component.Piece;

public abstract class Randomizer {
	
	protected Random r;
	public int[] pieces;
	
	public Randomizer(boolean[] pieceEnable, long seed) {
		r = new Random(seed);
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
	
	public abstract int next();
}
