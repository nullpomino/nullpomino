package cx.it.nullpo.nm8.game.component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

/**
 * A Random wrapper that supports instance copying.
 */
public class NRandom implements Serializable {
	private static final long serialVersionUID = -7407847810638532397L;

	/** Random */
	protected Random random;

	/** Initial seed */
	protected long seed;

	/**
	 * Constructor with random generated seed
	 */
	public NRandom() {
		Random temprand = new Random();
		seed = temprand.nextLong();
		random = new Random(seed);
	}

	/**
	 * Constructor with specific seed
	 * @param seed Initial seed
	 */
	public NRandom(long seed) {
		this.seed = seed;
		random = new Random(seed);
	}

	/**
	 * Copy constructor
	 * @param r NRandom to copy from
	 */
	public NRandom(NRandom r) {
		copy(r);
	}

	/**
	 * Copy from another NRandom
	 * @param r NRandom to copy from
	 */
	public void copy(NRandom r) {
		importRandom(r.exportRandom());
		seed = r.getSeed();
	}

	/**
	 * Get Random that this NRandom uses
	 * @return Random
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * Get initial seed value
	 * @return Initial seed
	 */
	public long getSeed() {
		return seed;
	}

	/**
	 * Export Random to a byte[]
	 * @return byte[] of Random
	 */
	public byte[] exportRandom() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(random);
			return baos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("Failed to export Random", e);
		}
	}

	/**
	 * Import Random from a byte[]
	 * @param b byte[] of Random
	 */
	public void importRandom(byte[] b) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			ObjectInputStream ois = new ObjectInputStream(bais);
			random = (Random)ois.readObject();
		} catch (Exception e) {
			throw new RuntimeException("Failed to import Random", e);
		}
	}

	synchronized public void setSeed(long seed) {
		this.seed = seed;
		random.setSeed(seed);
	}
	public void nextBytes(byte[] bytes) {
		random.nextBytes(bytes);
	}
	public int nextInt(){
		return random.nextInt();
	}
	public int nextInt(int n) {
		return random.nextInt(n);
	}
	public long nextLong() {
		return random.nextLong();
	}
	public boolean nextBoolean() {
		return random.nextBoolean();
	}
	public float nextFloat() {
		return random.nextFloat();
	}
	public double nextDouble() {
		return random.nextDouble();
	}
	synchronized public double nextGaussian() {
		return random.nextGaussian();
	}
}
