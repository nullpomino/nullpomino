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

/**
 * 通常のランダムな出現順
 *  (engine.randomizerがnullの場合にGameEngineから使われます）
 */
public class NintendoRandomizer implements Randomizer {
	/*
	 * Create NEXT sequence
	 */
	public int[] createPieceSequence(boolean[] pieceEnable, Random random, int arrayMax) {
		int[] pieceArray = new int[arrayMax];
		int prev = Piece.PIECE_COUNT;

		for(int i = 0; i < pieceArray.length; i++) {
			int id = 0;

			do {
				id = random.nextInt(Piece.PIECE_COUNT+1);
			} while(id != Piece.PIECE_COUNT && pieceEnable[id] == false);
			
			if (id == prev || id == Piece.PIECE_COUNT) {
				do {
					id = random.nextInt(Piece.PIECE_COUNT);
				} while(pieceEnable[id] == false);
			}

			pieceArray[i] = id;
			prev = id;
		}

		return pieceArray;
	}
}
