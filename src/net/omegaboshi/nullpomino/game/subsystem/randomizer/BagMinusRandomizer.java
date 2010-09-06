package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class BagMinusRandomizer extends BagRandomizer {

	int baglen;

	public BagMinusRandomizer() {
		super();
	}

	public BagMinusRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
		baglen = Math.max(1,pieces.length-1);
	}

	public int next() {
		int id = bag[pt];
		pt++;
		if (pt == baglen) {
			pt = 0;
			shuffle();
		}
		return id;
	}

}
