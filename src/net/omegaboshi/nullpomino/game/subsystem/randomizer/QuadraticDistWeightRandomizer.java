package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class QuadraticDistWeightRandomizer extends Randomizer {
	
	int[] initWeights = {3, 3, 1, 0, 3, 3, 0, 2, 2, 2, 2};
	int[] weights;
	int[] cumulative;
	int sum;
	int id;
	
	public QuadraticDistWeightRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
		weights = new int[pieces.length];
		for (int i = 0; i < pieces.length; i++) {
			weights[i] = initWeights[pieces[i]];
		}
		cumulative = new int[pieces.length];
	}
	
	public int next() {
		sum = 0;
		for (int i = 0; i < pieces.length; i++) {
			sum += weights[i]*weights[i];
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
			weights[i]++;
		}
		return id;
	}

}
