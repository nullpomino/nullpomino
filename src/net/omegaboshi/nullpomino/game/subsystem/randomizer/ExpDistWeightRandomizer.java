package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class ExpDistWeightRandomizer extends DistanceWeightRandomizer {

	public ExpDistWeightRandomizer() {
		super();
	}

	public ExpDistWeightRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
	}

	public int getWeight(int i) {
		if (weights[i] == 0) {
			return 0;
		} else {
			return 1 << (weights[i] - 1);
		}
	}

	public boolean isAtDistanceLimit(int i) {
		return weights[i] > 25;
	}
}
