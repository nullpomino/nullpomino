package cx.it.nullpo.nm8.game.subsystem.wallkick;

/**
 * Factory class of Wallkick
 */
public class WallkickFactory {
	/** ID of NoneWallkick */
	public static final int WALLKICK_NONE = 0;
	/** ID of StandardWallkick */
	public static final int WALLKICK_STANDARD = 1;

	/** Names of available wallkicks */
	public static final String[] WALLKICK_NAMETABLES =
	{
		"None",
		"Standard",
	};

	/**
	 * Create an Wallkick
	 * @param id Wallkick ID
	 * @return Wallkick
	 */
	public static Wallkick createWallkick(int id) {
		switch(id) {
		case WALLKICK_NONE:
			return new NoneWallkick();
		case WALLKICK_STANDARD:
			return new StandardWallkick();
		}

		// Invalid ID. Defaults to none.
		return new NoneWallkick();
	}
}
