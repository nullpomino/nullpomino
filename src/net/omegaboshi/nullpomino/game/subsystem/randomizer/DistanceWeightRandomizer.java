package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public abstract class DistanceWeightRandomizer extends Randomizer {
	
	int[] initWeights = {3, 3, 1, 0, 3, 3, 0, 2, 2, 2, 2};
	int[] weights;
	int[] cumulative;
	int sum;
	int id;
	
	public DistanceWeightRandomizer() {
		super();
	}
	
	public DistanceWeightRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
	}
	
	public void init() {
		weights = new int[pieces.length];
		for (int i = 0; i < pieces.length; i++) {
			weights[i] = initWeights[pieces[i]];
		}
		cumulative = new int[pieces.length];
	}
	
	public int next() {
		sum = 0;
		for (int i = 0; i < pieces.length; i++) {
			sum += getWeight(i);
			cumulative[i] = sum;
		}
		id = r.nextInt(sum);
		for (int i = 0; i < pieces.length; i++) {
			if (id < cumulative[i]) {
				id = i;
				break;
			}
		}
		weights[id] = 0;
		for (int i = 0; i < pieces.length; i++) {
			if (!isAtDistanceLimit(i)) {
				weights[i]++;
			}
		}
		return id;
	}
	
	protected abstract int getWeight(int i);
	
	protected abstract boolean isAtDistanceLimit(int i);

}
