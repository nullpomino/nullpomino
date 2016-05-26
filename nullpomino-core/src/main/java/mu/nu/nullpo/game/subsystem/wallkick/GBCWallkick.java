package mu.nu.nullpo.game.subsystem.wallkick;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.component.WallkickResult;

/**
 * GBCWallkick
 */
public class GBCWallkick implements Wallkick {
	private static final int[][] KICKTABLE_L = {{ 1,-1}, { 1, 1}, {-1, 1}, {-1,-1}};
	private static final int[][] KICKTABLE_R = {{-1,-1}, { 1,-1}, { 1, 1}, {-1, 1}};

	public WallkickResult executeWallkick(int x, int y, int rtDir, int rtOld, int rtNew, boolean allowUpward, Piece piece, Field field, Controller ctrl)
	{
		if((piece.id != Piece.PIECE_I) && (piece.id != Piece.PIECE_I2) && (piece.id != Piece.PIECE_I3)) {
			int[][] kicktable = KICKTABLE_L;
			if(rtDir >= 0) kicktable = KICKTABLE_R;

			int x2 = kicktable[rtOld][0];
			int y2 = kicktable[rtOld][1];
			if(piece.big) {
				x2 *= 2;
				y2 *= 2;
			}

			if( ((y2 >= 0) || (allowUpward)) && (y + y2 > -2) ) {
				if(piece.checkCollision(x + x2, y + y2, rtNew, field) == false) {
					return new WallkickResult(x2, y2, rtNew);
				}
			}
		}

		return null;
	}
}
