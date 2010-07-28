package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class ExpDistWeightRandomizer extends Randomizer {
	
	int[] initWeights = {3, 4, 2, 1, 3, 4, 1, 2, 2, 2, 2};
	int[] weights;
	int[] cumulative;
	int sum;
	int id;
	
	public ExpDistWeightRandomizer(boolean[] pieceEnable, long seed) {
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
			sum += 1 << (weights[i] - 1);
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
			if (weights[i] < 26) {
				weights[i]++;
			}
		}
		return id;
	}

}
