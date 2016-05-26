package mu.nu.nullpo.game.subsystem.wallkick;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.component.WallkickResult;

/**
 * Base class for all Standard (SRS) wallkicks
 */
public class BaseStandardWallkick implements Wallkick {
	/**
	 * Get wallkick table. Used from executeWallkick.
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param rtDir Rotation button used (-1: left rotation, 1: right rotation, 2: 180-degree rotation)
	 * @param rtOld Direction before rotation
	 * @param rtNew Direction after rotation
	 * @param allowUpward If true, upward wallkicks are allowed.
	 * @param piece Current piece
	 * @param field Current field
	 * @param ctrl Button input status (it may be null, when controlled by an AI)
	 * @return Wallkick Table. You may return null if you don't want to execute a kick.
	 */
	protected int[][][] getKickTable(int x, int y, int rtDir, int rtOld, int rtNew, boolean allowUpward, Piece piece, Field field, Controller ctrl) {
		return null;
	}

	/*
	 * Wallkick
	 */
	public WallkickResult executeWallkick(int x, int y, int rtDir, int rtOld, int rtNew, boolean allowUpward, Piece piece, Field field, Controller ctrl) {
		int[][][] kicktable = getKickTable(x, y, rtDir, rtOld, rtNew, allowUpward, piece, field, ctrl);

		if(kicktable != null) {
			for(int i = 0; i < kicktable[rtOld].length; i++) {
				int x2 = kicktable[rtOld][i][0];
				int y2 = kicktable[rtOld][i][1];

				if(piece.big == true) {
					x2 *= 2;
					y2 *= 2;
				}

				if((y2 >= 0) || (allowUpward)) {
					if(piece.checkCollision(x + x2, y + y2, rtNew, field) == false) {
						return new WallkickResult(x2, y2, rtNew);
					}
				}
			}
		}

		return null;
	}
}
