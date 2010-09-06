package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class LinearDistWeightRandomizer extends DistanceWeightRandomizer {

	public LinearDistWeightRandomizer() {
		super();
	}

	public LinearDistWeightRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
	}

	public int getWeight(int i) {
		return weights[i];
	}

	public boolean isAtDistanceLimit(int i) {
		return false;
	}

}
