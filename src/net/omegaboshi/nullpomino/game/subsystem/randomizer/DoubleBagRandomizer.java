package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class DoubleBagRandomizer extends Randomizer {
	
	int[] bag;
	int baglen;
	int pt;
	
	public DoubleBagRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
		baglen = pieces.length*2;
		bag = new int[baglen];
		pt = 0;
		for (int i = 0; i < baglen; i++) {
			bag[i] = pieces[i%pieces.length];
		}
		shuffle();
	}
	
	public void shuffle() {
		for (int i = baglen; i > 1; i--) {
			int j = r.nextInt(i);
			int temp = bag[i-1];
			bag[i-1] = bag[j];
			bag[j] = temp;
		}
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
