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
package mu.nu.nullpo.game.subsystem.ai;

import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.play.GameEngine;

/**
 * T-SpinしてくるAI (WIP)
 */
public class TSpinAI extends BasicAI {
	@Override
	public String getName() {
		return "T-SPIN";
	}

	@Override
	public int thinkMain(GameEngine engine, int x, int y, int rt, int rtOld, Field fld, Piece piece, Piece nextpiece, Piece holdpiece, int depth) {
		int pts = 0;

		// Add points for being adjacent to other blocks
		if(piece.checkCollision(x - 1, y, fld)) pts += 1;
		if(piece.checkCollision(x + 1, y, fld)) pts += 1;
		if(piece.checkCollision(x, y - 1, fld)) pts += 100;

		// Number of holes and valleys needing an I piece (before placement)
		int holeBefore = fld.getHowManyHoles();
		int lidBefore = fld.getHowManyLidAboveHoles();
		int needIValleyBefore = fld.getTotalValleyNeedIPiece();
		// Field height (before placement)
		int heightBefore = fld.getHighestBlockY();
		// T-Spin flag
		boolean tspin = false;
		if((piece.id == Piece.PIECE_T) && (rtOld != -1) && (fld.isTSpinSpot(x, y, piece.big))) {
			tspin = true;
		}
		// T-Spin穴のcount (before placement)
		int tslotBefore = 0;
		//if( (nextpiece.id == Piece.PIECE_T) || ((holdpiece != null) && (holdpiece.id == Piece.PIECE_T)) ) {
			tslotBefore = fld.getTSlotLineClearAll(false);
		//}

		// ピースを置く
		if(!piece.placeToField(x, y, rt, fld)) {
			return 0;
		}

		// Line clear
		int lines = fld.checkLine();
		if(lines > 0) {
			fld.clearLine();
			fld.downFloatingBlocks();
		}

		// All clear
		boolean allclear = fld.isEmpty();
		if(allclear) pts += 500000;

		// Field height (after clears)
		int heightAfter = fld.getHighestBlockY();

		// Danger flag
		boolean danger = (heightAfter <= 12);

		// Additional points for lower placements
		if((!danger) && (depth == 0))
			pts += y * 10;
		else
			pts += y * 20;

		// Linescountで加点
		if((lines == 1) && (!danger) && (depth == 0) && (heightAfter >= 16) && (holeBefore < 3) && (!tspin) && (engine.combo < 1)) {
			return 0;
		}
		if((!danger) && (depth == 0)) {
			if(lines == 1) pts += 10;
			if(lines == 2) pts += 50;
			if(lines == 3) pts += 100;
			if(lines >= 4) pts += 100000;
		} else {
			if(lines == 1) pts += 5000;
			if(lines == 2) pts += 10000;
			if(lines == 3) pts += 30000;
			if(lines >= 4) pts += 100000;
		}

		if( (lines < 4) && (!allclear) ) {
			// Number of holes and valleys needing an I piece (after placement)
			int holeAfter = fld.getHowManyHoles();
			int lidAfter = fld.getHowManyLidAboveHoles();
			int needIValleyAfter = fld.getTotalValleyNeedIPiece();
			// T-Spin穴のcount (after placement)
			int tslotAfter = 0;
			//if( (nextpiece.id == Piece.PIECE_T) || ((holdpiece != null) && (holdpiece.id == Piece.PIECE_T)) ) {
				tslotAfter = fld.getTSlotLineClearAll(false);
			//}
			boolean newtslot = false;

			if((!danger) && (tslotAfter > tslotBefore) && (tslotAfter == 1) && (holeAfter == holeBefore + 1)) {
				// 新たにT-Spin穴ができると加点
				pts += 100000;
				newtslot = true;

				// ホールドのTを必ず出す
				if((nextpiece.id != Piece.PIECE_T) && (holdpiece != null) && (holdpiece.id == Piece.PIECE_T)) {
					forceHold = true;
				}
			} else if((tslotAfter < tslotBefore) && (!tspin) && (!danger)) {
				// T-Spin穴壊すとNG
				return 0;
			} else if(holeAfter > holeBefore) {
				// Demerits for new holes
				pts -= (holeAfter - holeBefore) * 10;
				if(depth == 0) return 0;
			} else if(holeAfter < holeBefore) {
				// Add points for reduction in number of holes
				if(!danger)
					pts += (holeBefore - holeAfter) * 5;
				else
					pts += (holeBefore - holeAfter) * 10;
			}

			if((lidAfter > lidBefore) && (!newtslot)) {
				// 穴の上に乗っているBlockを増やすと減点
				if(!danger)
					pts -= (lidAfter - lidBefore) * 10;
				else
					pts -= (lidAfter - lidBefore) * 20;
			} else if(lidAfter < lidBefore) {
				// Add points for reduction in number blocks above holes
				if(!danger)
					pts += (lidBefore - lidAfter) * 10;
				else
					pts += (lidBefore - lidAfter) * 20;
			}

			if((tspin) && (lines >= 1) && (holeAfter < holeBefore)) {
				// T-Spin bonus
				pts += 100000 * lines;
			}

			if((needIValleyAfter > needIValleyBefore) && (needIValleyAfter >= 2)) {
				// 2つ以上I型が必要な穴を作ると減点
				pts -= (needIValleyAfter - needIValleyBefore) * 10;
				if(depth == 0) return 0;
			} else if(needIValleyAfter < needIValleyBefore) {
				// Add points for reduction in number of holes
				if((depth == 0) && (!danger))
					pts += (needIValleyBefore - needIValleyAfter) * 10;
				else
					pts += (needIValleyBefore - needIValleyAfter) * 20;
			}

			if(heightBefore < heightAfter) {
				// Add points for reducing the height
				if((depth == 0) && (!danger))
					pts += (heightAfter - heightBefore) * 10;
				else
					pts += (heightAfter - heightBefore) * 20;
			} else if(heightBefore > heightAfter) {
				// Demerits for increase in height
				if((depth > 0) || (danger))
					pts -= (heightBefore - heightAfter) * 4;
			}

			// Combo bonus
			if((lines >= 1) && (engine.comboType != GameEngine.COMBO_TYPE_DISABLE)) {
				pts += lines * engine.combo * 50;
			}
		}

		return pts;
	}
}
