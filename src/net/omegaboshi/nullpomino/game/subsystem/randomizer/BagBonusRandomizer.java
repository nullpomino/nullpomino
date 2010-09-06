package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class BagBonusRandomizer extends Randomizer {

	int[] bag;
	int baglen;
	int pt;
	int bonus;

	public BagBonusRandomizer() {
		super();
	}

	public BagBonusRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
	}

	public void init() {
		baglen = pieces.length+1;
		bag = new int[baglen];
		pt = 0;
		bonus = pieces.length;
		for (int i = 0; i < pieces.length; i++) {
			bag[i] = pieces[i];
		}
		shuffle();
	}

	public void shuffle() {
		bag[bonus] = r.nextInt(pieces.length);
		for (int i = baglen; i > 1; i--) {
			int j = r.nextInt(i);
			int temp = bag[i-1];
			bag[i-1] = bag[j];
			bag[j] = temp;
			if (bonus == i-1) {
				bonus = j;
			} else if(bonus == j) {
				bonus = i-1;
			}
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
