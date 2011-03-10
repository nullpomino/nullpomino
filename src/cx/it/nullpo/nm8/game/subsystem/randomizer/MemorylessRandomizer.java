package cx.it.nullpo.nm8.game.subsystem.randomizer;

/**
 * MemorylessRandomizer: Pure rand()%7 randomizer
 */
public class MemorylessRandomizer extends Randomizer {
	/** Serial version ID */
	private static final long serialVersionUID = 1311808200848290027L;

	public MemorylessRandomizer() {
		super();
	}

	public MemorylessRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
	}

	@Override
	public int next() {
		return pieces[r.nextInt(pieces.length)];
	}
}
