/*
    Copyright (c) 2010, NullNoname
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * Neither the name of NullNoname nor the names of its
          contributors may be used to endorse or promote products derived from
          this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.
*/
package mu.nu.nullpo.game.subsystem.wallkick;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;

/**
 * SRS with symmetric I piece kicks
 */
public class StandardSymmetricWallkick extends BaseStandardWallkick {
	// Wallkick data
	private static final int WALLKICK_NORMAL_L[][][] =
	{
		{{ 1, 0},{ 1,-1},{ 0, 2},{ 1, 2}},	// 0>>3
		{{ 1, 0},{ 1, 1},{ 0,-2},{ 1,-2}},	// 1>>0
		{{-1, 0},{-1,-1},{ 0, 2},{-1, 2}},	// 2>>1
		{{-1, 0},{-1, 1},{ 0,-2},{-1,-2}},	// 3>>2
	};
	private static final int WALLKICK_NORMAL_R[][][] =
	{
		{{-1, 0},{-1,-1},{ 0, 2},{-1, 2}},	// 0>>1
		{{ 1, 0},{ 1, 1},{ 0,-2},{ 1,-2}},	// 1>>2
		{{ 1, 0},{ 1,-1},{ 0, 2},{ 1, 2}},	// 2>>3
		{{-1, 0},{-1, 1},{ 0,-2},{-1,-2}},	// 3>>0
	};
	private static final int WALLKICK_I_L[][][] =
	{
		{{ 2, 0},{-1, 0},{-1,-2},{ 2, 1}},	// 0>>3
		{{ 2, 0},{-1, 0},{ 2,-1},{-1, 2}},	// 1>>0
		{{-2, 0},{ 1, 0},{-2,-1},{ 1, 1}},	// 2>>1
		{{ 1, 0},{-2, 0},{ 1,-2},{-2, 1}},	// 3>>2
	};
	private static final int WALLKICK_I_R[][][] =
	{
		{{-2, 0},{ 1, 0},{ 1,-2},{-2, 1}},	// 0>>1
		{{-1, 0},{ 2, 0},{-1,-2},{ 2, 1}},	// 1>>2
		{{ 2, 0},{-1, 0},{ 2,-1},{-1, 1}},	// 2>>3
		{{-2, 0},{ 1, 0},{-2,-1},{ 1, 2}},	// 3>>0
	};
	private static final int WALLKICK_I2_L[][][] =
	{
		{{ 1, 0},{ 0,-1},{ 1,-2}},			// 0>>3
		{{ 0, 1},{ 1, 0},{ 1, 1}},			// 1>>0
		{{-1, 0},{ 0, 1},{-1, 0}},			// 2>>1
		{{ 0,-1},{-1, 0},{-1, 1}},			// 3>>2
	};
	private static final int WALLKICK_I2_R[][][] =
	{
		{{ 0,-1},{-1, 0},{-1,-1}},			// 0>>1
		{{ 1, 0},{ 0,-1},{ 1, 0}},			// 1>>2
		{{ 0, 1},{ 1, 0},{ 1,-1}},			// 2>>3
		{{-1, 0},{ 0, 1},{-1, 2}},			// 3>>0
	};
	private static final int WALLKICK_I3_L[][][] =
	{
		{{ 1, 0},{-1, 0},{ 0, 0},{ 0, 0}},	// 0>>3
		{{-1, 0},{ 1, 0},{ 0,-1},{ 0, 1}},	// 1>>0
		{{-1, 0},{ 1, 0},{ 0, 2},{ 0,-2}},	// 2>>1
		{{ 1, 0},{-1, 0},{ 0,-1},{ 0, 1}},	// 3>>2
	};
	private static final int WALLKICK_I3_R[][][] =
	{
		{{ 1, 0},{-1, 0},{ 0, 1},{ 0,-1}},	// 0>>1
		{{ 1, 0},{-1, 0},{ 0,-2},{ 0, 2}},	// 1>>2
		{{-1, 0},{ 1, 0},{ 0, 1},{ 0,-1}},	// 2>>3
		{{-1, 0},{ 1, 0},{ 0, 0},{ 0, 0}},	// 3>>0
	};
	private static final int WALLKICK_L3_L[][][] =
	{
		{{ 0,-1},{ 0, 1}},					// 0>>3
		{{ 1, 0},{-1, 0}},					// 1>>0
		{{ 0, 1},{ 0,-1}},					// 2>>1
		{{-1, 0},{ 1, 0}},					// 3>>2
	};
	private static final int WALLKICK_L3_R[][][] =
	{
		{{-1, 0},{ 1, 0}},					// 0>>1
		{{ 0,-1},{ 0, 1}},					// 1>>2
		{{ 1, 0},{-1, 0}},					// 2>>3
		{{ 0, 1},{ 0,-1}},					// 3>>0
	};

	// 180-degree rotation wallkick data
	private static final int WALLKICK_NORMAL_180[][][] =
	{
		{{ 1, 0},{ 2, 0},{ 1, 1},{ 2, 1},{-1, 0},{-2, 0},{-1, 1},{-2, 1},{ 0,-1},{ 3, 0},{-3, 0}},	// 0>>2─ ┐
		{{ 0, 1},{ 0, 2},{-1, 1},{-1, 2},{ 0,-1},{ 0,-2},{-1,-1},{-1,-2},{ 1, 0},{ 0, 3},{ 0,-3}},	// 1>>3─ ┼ ┐
		{{-1, 0},{-2, 0},{-1,-1},{-2,-1},{ 1, 0},{ 2, 0},{ 1,-1},{ 2,-1},{ 0, 1},{-3, 0},{ 3, 0}},	// 2>>0─ ┘ │
		{{ 0, 1},{ 0, 2},{ 1, 1},{ 1, 2},{ 0,-1},{ 0,-2},{ 1,-1},{ 1,-2},{-1, 0},{ 0, 3},{ 0,-3}},	// 3>>1─ ─ ┘
	};
	private static final int WALLKICK_I_180[][][] =
	{
		{{-1, 0},{-2, 0},{ 1, 0},{ 2, 0},{ 0, 1}},													// 0>>2─ ┐
		{{ 0, 1},{ 0, 2},{ 0,-1},{ 0,-2},{-1, 0}},													// 1>>3─ ┼ ┐
		{{ 1, 0},{ 2, 0},{-1, 0},{-2, 0},{ 0,-1}},													// 2>>0─ ┘ │
		{{ 0, 1},{ 0, 2},{ 0,-1},{ 0,-2},{ 1, 0}},													// 3>>1─ ─ ┘
	};

	/*
	 * Get kick table
	 */
	@Override
	protected int[][][] getKickTable(int x, int y, int rtDir, int rtOld, int rtNew, boolean allowUpward, Piece piece, Field field, Controller ctrl) {
		int[][][] kicktable = null;

		if(rtDir == 2) {
			// 180-degree rotation
			switch(piece.id) {
			case Piece.PIECE_I:
				kicktable = WALLKICK_I_180;
				break;
			default:
				kicktable = WALLKICK_NORMAL_180;
				break;
			}
		} else if(rtDir == -1) {
			// Left rotation
			switch(piece.id) {
			case Piece.PIECE_I:
				kicktable = WALLKICK_I_L;
				break;
			case Piece.PIECE_I2:
				kicktable = WALLKICK_I2_L;
				break;
			case Piece.PIECE_I3:
				kicktable = WALLKICK_I3_L;
				break;
			case Piece.PIECE_L3:
				kicktable = WALLKICK_L3_L;
				break;
			default:
				kicktable = WALLKICK_NORMAL_L;
				break;
			}
		} else if(rtDir == 1) {
			// Right rotation
			switch(piece.id) {
			case Piece.PIECE_I:
				kicktable = WALLKICK_I_R;
				break;
			case Piece.PIECE_I2:
				kicktable = WALLKICK_I2_R;
				break;
			case Piece.PIECE_I3:
				kicktable = WALLKICK_I3_R;
				break;
			case Piece.PIECE_L3:
				kicktable = WALLKICK_L3_R;
				break;
			default:
				kicktable = WALLKICK_NORMAL_R;
				break;
			}
		}

		return kicktable;
	}
}
