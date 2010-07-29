package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class ExpDistWeightRandomizer extends DistanceWeightRandomizer {
	
	int[] initWeights = {3, 4, 2, 1, 3, 4, 1, 2, 2, 2, 2};
	
	public ExpDistWeightRandomizer() {
		super();
	}
	
	public ExpDistWeightRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
	}
	
	public int getWeight(int i) {
		return 1 << (weights[i] - 1);
	}
	
	public boolean isAtDistanceLimit(int i) {
		return i > 25;
	}

}
