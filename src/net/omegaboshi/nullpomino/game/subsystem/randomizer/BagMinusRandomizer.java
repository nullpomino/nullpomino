package net.omegaboshi.nullpomino.game.subsystem.randomizer;


public class BagMinusRandomizer extends BagRandomizer {
	
	public BagMinusRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
	}

	public int next() {
		int id = bag[pt];
		pt++;
		if (pt == pieces.length-1) {
			pt = 0;
			shuffle();
		}
		return id;
	}

}
