package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class MemorylessRandomizer extends Randomizer {

	public MemorylessRandomizer() {
		super();
	}

	public MemorylessRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
	}

	public int next() {
		return pieces[r.nextInt(pieces.length)];
	}

}
