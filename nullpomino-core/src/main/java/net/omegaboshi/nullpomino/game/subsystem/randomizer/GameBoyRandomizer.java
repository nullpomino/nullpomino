package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class GameBoyRandomizer extends Randomizer {

	int id;
	int roll;

	public GameBoyRandomizer() {
		super();
	}

	public GameBoyRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
	}

	public void init() {
		id = r.nextInt(pieces.length);
		roll = 6 * pieces.length - 3;
	}

	public int next() {
		id = (id + (r.nextInt(roll)/5) + 1) % pieces.length;
		return pieces[id];
	}

}
