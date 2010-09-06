package net.omegaboshi.nullpomino.game.subsystem.randomizer;

import java.util.Random;

import mu.nu.nullpo.game.component.Piece;

public abstract class Randomizer {

	protected Random r;
	public int[] pieces;

	public Randomizer() {}

	public Randomizer(boolean[] pieceEnable, long seed) {
		setState(pieceEnable, seed);
	}

	public void init() {}

	public abstract int next();

	public void setState(boolean[] pieceEnable, long seed) {
		setPieceEnable(pieceEnable);
		reseed(seed);
		init();
	}

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

	protected boolean isPieceSZOOnly()
	{
		for (int i=0; i<pieces.length; i++) {
			if (pieces[i] != Piece.PIECE_O && pieces[i] != Piece.PIECE_Z && pieces[i] != Piece.PIECE_S)
				return false;
		}

		return true;
	}
}
