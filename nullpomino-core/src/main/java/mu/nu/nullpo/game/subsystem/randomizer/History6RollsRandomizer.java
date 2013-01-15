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
package mu.nu.nullpo.game.subsystem.randomizer;

import java.util.Random;

import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * History randomizer (4 rolls)
 * @deprecated No longer used. The current one is net.omegaboshi.nullpomino.game.subsystem.randomizer.History6RollsRandomizer.
 */
public class History6RollsRandomizer implements Randomizer {
	/*
	 * Create NEXT sequence
	 */
	public int[] createPieceSequence(boolean[] pieceEnable, Random random, int arrayMax) {
		int[] pieceArray = new int[arrayMax];
		int history[] = new int[4];
		int id = 0;
		boolean szoOnly = GeneralUtil.isPieceSZOOnly(pieceEnable);

		// HistoryZ S Z SI am filled with
		history[0] = Piece.PIECE_Z;
		history[1] = Piece.PIECE_S;
		history[2] = Piece.PIECE_Z;
		history[3] = Piece.PIECE_S;

		// Pat generation
		do {
			id = random.nextInt(Piece.PIECE_COUNT);
		} while( (!pieceEnable[id]) || ((!szoOnly) && ((id == Piece.PIECE_Z) || (id == Piece.PIECE_O) || (id == Piece.PIECE_S))) );

		// Add to NEXT list
		pieceArray[0] = id;

		// Slide the history
		for(int j = 0; j < 3; j++) {
			history[3 - j] = history[3 - (j + 1)];
		}

		// A new historyBlockPut
		history[0] = id;

		// Create draws
		for(int i = 1; i < arrayMax; i++) {
			// Draw
			do {
				id = random.nextInt(Piece.PIECE_COUNT);
			} while(!pieceEnable[id]);

			// Was drawn to the history TsumoMaximum6Redraw times
			if((id == history[0]) || (id == history[1]) || (id == history[2]) || (id == history[3])) {
				for(int j = 0; j < 6; j++) {
					do {
						id = random.nextInt(Piece.PIECE_COUNT);
					} while(!pieceEnable[id]);

					// 4If you do not have to leave in place the history of one
					if((id != history[0]) && (id != history[1]) && (id != history[2]) && (id != history[3])) break;
				}
			}

			// Slide the history
			for(int j = 0; j < 3; j++) {
				history[3 - j] = history[3 - (j + 1)];
			}

			// A new historyBlockPut
			history[0] = id;

			// Add to NEXT list
			pieceArray[i] = id;
		}

		return pieceArray;
	}
}
