package cx.it.nullpo.nm8.game.subsystem.randomizer;

import cx.it.nullpo.nm8.game.component.PieceManager;

/**
 * Factory class of Randomizer
 */
public class RandomizerFactory {
	/** ID of MemorylessRandomizer */
	public static final int RANDOMIZER_MEMORYLESS = 0;
	/** ID of BagRandomizer */
	public static final int RANDOMIZER_BAG = 1;

	/** Names of available randomizers */
	public static final String[] RANDOMIZER_NAMETABLES =
	{
		"Memoryless",
		"Bag",
	};

	/**
	 * Create a randomizer
	 * @param id Randomizer ID
	 * @param pieceEnable Piece enable flag (it can be null, if you use only standard pieces)
	 * @param seed Random seed
	 * @return Randomizer
	 */
	public static Randomizer createRandomizer(int id, boolean[] pieceEnable, long seed) {
		// If pieceEnable array is null, create a new one
		if(pieceEnable == null) {
			pieceEnable = new boolean[PieceManager.PIECE_COUNT];
			for(int i = 0; i < PieceManager.PIECE_STANDARD_COUNT; i++) pieceEnable[i] = true;
		}

		// If no piece can appear, modify it
		boolean allDisable = true;
		for(int i = 0; i < pieceEnable.length; i++) {
			if(pieceEnable[i]) {
				allDisable = false;
				break;
			}
		}
		if(allDisable) {
			pieceEnable = new boolean[PieceManager.PIECE_COUNT];
			for(int i = 0; i < PieceManager.PIECE_STANDARD_COUNT; i++) pieceEnable[i] = true;
		}

		// Create a new randomizer
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
