package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class QuadraticDistWeightRandomizer extends DistanceWeightRandomizer {

	public QuadraticDistWeightRandomizer() {
		super();
	}

	public QuadraticDistWeightRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
	}

	public int getWeight(int i) {
		return weights[i]*weights[i];
	}

	public boolean isAtDistanceLimit(int i) {
		return false;
	}

}
