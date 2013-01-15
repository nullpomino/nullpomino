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
package mu.nu.nullpo.game.component;

import java.io.Serializable;

/**
 * WallkickThe resulting class
 */
public class WallkickResult implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = -7985029240622355609L;

	/** X-coordinateCorrection amount */
	public int offsetX;

	/** Y-coordinateCorrection amount */
	public int offsetY;

	/** rotationPiece afterDirection */
	public int direction;

	/**
	 * Constructor
	 */
	public WallkickResult() {
		reset();
	}

	/**
	 * With parametersConstructor
	 * @param offsetX X-coordinateCorrection amount
	 * @param offsetY Y-coordinateCorrection amount
	 * @param direction rotationOf Tetoramino afterDirection
	 */
	public WallkickResult(int offsetX, int offsetY, int direction) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.direction = direction;
	}

	/**
	 * Copy constructor
	 * @param w Copy source
	 */
	public WallkickResult(WallkickResult w) {
		copy(w);
	}

	/**
	 * Reset to defaults
	 */
	public void reset() {
		offsetX = 0;
		offsetY = 0;
		direction = 0;
	}

	/**
	 * AnotherWallkickResultCopied from the
	 * @param w Copy source
	 */
	public void copy(WallkickResult w) {
		this.offsetX = w.offsetX;
		this.offsetY = w.offsetY;
		this.direction = w.direction;
	}

	/**
	 * TopDirectionToWallkickDetermine whether
	 * @return TopDirectionToWallkickWhen (offsetY < 0To), in which case thetrue
	 */
	public boolean isUpward() {
		return (offsetY < 0);
	}
}
