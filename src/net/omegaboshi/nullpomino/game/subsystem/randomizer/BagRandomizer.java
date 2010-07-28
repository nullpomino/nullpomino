package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class BagRandomizer extends Randomizer {
	
	int[] bag;
	int pt;
	
	public BagRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
		bag = new int[pieces.length];
		pt = 0;
		for (int i = 0; i < pieces.length; i++) {
			bag[i] = pieces[i];
		}
		shuffle();
	}
	
	public void shuffle() {
		for (int i = pieces.length; i > 1; i--) {
			int j = r.nextInt(i);
			int temp = bag[i-1];
			bag[i-1] = bag[j];
			bag[j] = temp;
		}
	}

	public int next() {
		int id = bag[pt];
		pt++;
		if (pt == pieces.length) {
			pt = 0;
			shuffle();
		}
		return id;
	}
}
