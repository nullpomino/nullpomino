package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class BagBonusBagRandomizer extends Randomizer {

	int[] bag;
	int[] bonusbag;
	int baglen;
	int pt;
	int bonuspt;
	int bonus;

	public BagBonusBagRandomizer() {
		super();
	}

	public BagBonusBagRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
	}

	public void init() {
		baglen = pieces.length+1;
		bag = new int[baglen];
		bonusbag = new int[pieces.length];
		pt = 0;
		bonuspt = 0;
		bonus = pieces.length;
		for (int i = 0; i < pieces.length; i++) {
			bag[i] = pieces[i];
			bonusbag[i] = pieces[i];
		}
		shuffleBonus();
		shuffle();
	}

	public void shuffle() {
		bag[bonus] = bonusbag[bonuspt];
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

	public void shuffleBonus() {
		for (int i = pieces.length; i > 1; i--) {
			int j = r.nextInt(i);
			int temp = bonusbag[i-1];
			bonusbag[i-1] = bonusbag[j];
			bonusbag[j] = temp;
		}
	}

	public int next() {
		int id = bag[pt];
		pt++;
		if (pt == baglen) {
			pt = 0;
			bonuspt++;
			if (bonuspt == pieces.length) {
				bonuspt = 0;
				shuffleBonus();
			}
			shuffle();
		}
		return id;
	}
}
