package net.omegaboshi.nullpomino.game.subsystem.randomizer;

public class NintendoRandomizer extends Randomizer {

	int prev;
	int roll = pieces.length+1;
	
	public NintendoRandomizer(boolean[] pieceEnable, long seed) {
		super(pieceEnable, seed);
		prev = pieces.length;
	}
	
	public int next() {
		int id = r.nextInt(roll);
		if (id == prev || id == pieces.length) {
			id = r.nextInt(pieces.length);
		}
		prev = id;
		return pieces[id];
	}

}
