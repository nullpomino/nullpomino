package cx.it.nullpo.nm8.game.subsystem.randomizer;

import java.io.Serializable;

import cx.it.nullpo.nm8.game.component.NRandom;
import cx.it.nullpo.nm8.game.component.Piece;

/**
 * Randomizer: Creates the sequence of pieces
 */
public abstract class Randomizer implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = -1392962792115922732L;

	/** NRandom to make randomness */
	protected NRandom r;

	/** Piece IDs that can appear */
	public int[] pieces;

	/**
	 * Constructor
	 */
	public Randomizer() {}

	/**
	 * Constructor
	 * @param pieceEnable Piece enabled flag
	 * @param seed Random Seed
	 */
	public Randomizer(boolean[] pieceEnable, long seed) {
		setState(pieceEnable, seed);
	}

	/**
	 * Do any initialize here
	 */
	public void init() {}

	/**
	 * Get the next piece ID
	 * @return Piece ID
	 */
	public abstract int next();

	/**
	 * Init this Randomizer
	 * @param pieceEnable Piece enabled flag
	 * @param seed Random Seed
	 */
	public void setState(boolean[] pieceEnable, long seed) {
		setPieceEnable(pieceEnable);
		reseed(seed);
		init();
	}

	/**
	 * Set pieces array
	 * @param pieceEnable Piece enabled flag
	 */
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

	/**
	 * Renew the Random with a new seed
	 * @param seed Random Seed
	 */
	public void reseed(long seed) {
		r = new NRandom(seed);
	}

	/**
	 * @return true if only S,Z, and/or O piece can appear
	 */
	protected boolean isPieceSZOOnly() {
		for (int i=0; i<pieces.length; i++) {
			if (pieces[i] != Piece.PIECE_O && pieces[i] != Piece.PIECE_Z && pieces[i] != Piece.PIECE_S)
				return false;
		}

		return true;
	}
}
