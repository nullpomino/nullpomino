package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class BagMinusTwoRandomizer extends BagRandomizer {

	int baglen;

	public BagMinusTwoRandomizer() {
		super();
	}

	public BagMinusTwoRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
		baglen = Math.max(1,pieces.length-2);
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
