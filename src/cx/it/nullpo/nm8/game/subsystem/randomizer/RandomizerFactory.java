package cx.it.nullpo.nm8.game.subsystem.randomizer;

/**
 * Factory class of Randomizer
 */
public class RandomizerFactory {
	/** ID of MemorylessRandomizer */
	public static final int RANDOMIZER_MEMORYLESS = 0;
	/** ID of BagRandomizer */
	public static final int RANDOMIZER_BAG = 1;

	/**
	 * Create a randomizer
	 * @param id Randomizer ID
	 * @param pieceEnable Piece enable flag
	 * @param seed Random seed
	 * @return Randomizer
	 */
	public static Randomizer createRandomizer(int id, boolean[] pieceEnable, long seed) {
		switch(id) {
		case RANDOMIZER_MEMORYLESS:
			return new MemorylessRandomizer(pieceEnable, seed);
		case RANDOMIZER_BAG:
			return new BagRandomizer(pieceEnable, seed);
		}

		// Invalid ID. Default to memoryless
		return new MemorylessRandomizer(pieceEnable, seed);
	}
}
