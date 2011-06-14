package cx.it.nullpo.nm8.game.subsystem.wallkick;

import cx.it.nullpo.nm8.game.component.Controller;
import cx.it.nullpo.nm8.game.component.Field;
import cx.it.nullpo.nm8.game.component.Piece;
import cx.it.nullpo.nm8.game.component.WallkickResult;

/**
 * Dummy implementation of Wallkick interface. No wallkicks here.
 */
public class NoneWallkick implements Wallkick {
	public WallkickResult executeWallkick(int x, int y, int rtDir, int rtOld,
			int rtNew, boolean allowUpward, Piece piece, Field field,
			Controller ctrl) {
		return null;
	}
}
