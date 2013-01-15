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
 * BlockPeace
 */
public class Piece implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = 1204901746632931186L;

	/** BlockOf PeaceIDConstantcount */
	public static final int PIECE_NONE = -1,
							PIECE_I = 0,
							PIECE_L = 1,
							PIECE_O = 2,
							PIECE_Z = 3,
							PIECE_T = 4,
							PIECE_J = 5,
							PIECE_S = 6,
							PIECE_I1 = 7,
							PIECE_I2 = 8,
							PIECE_I3 = 9,
							PIECE_L3 = 10;

	/** BlockOf PeaceName */
	public static final String[] PIECE_NAMES = {"I","L","O","Z","T","J","S","I1","I2","I3","L3"};

	/** NormalBlockOf PeaceIDOfMaximumcount */
	public static final int PIECE_STANDARD_COUNT = 7;

	/** BlockOf PeaceIDOfMaximumcount */
	public static final int PIECE_COUNT = 11;

	/**  default OfBlockOf Peace data (X-coordinate) */
	public static final int[][][] DEFAULT_PIECE_DATA_X = {
		{{0,1,2,3},{2,2,2,2},{3,2,1,0},{1,1,1,1}},	// I
		{{2,2,1,0},{2,1,1,1},{0,0,1,2},{0,1,1,1}},	// L
		{{0,1,1,0},{1,1,0,0},{1,0,0,1},{0,0,1,1}},	// O
		{{0,1,1,2},{2,2,1,1},{2,1,1,0},{0,0,1,1}},	// Z
		{{1,0,1,2},{2,1,1,1},{1,2,1,0},{0,1,1,1}},	// T
		{{0,0,1,2},{2,1,1,1},{2,2,1,0},{0,1,1,1}},	// J
		{{2,1,1,0},{2,2,1,1},{0,1,1,2},{0,0,1,1}},	// S
		{{0      },{0      },{0      },{0      }},	// I1
		{{0,1    },{1,1    },{1,0    },{0,0    }},	// I2
		{{0,1,2  },{1,1,1  },{2,1,0  },{1,1,1  }},	// I3
		{{1,0,0  },{0,0,1  },{0,1,1  },{1,1,0  }},	// L3
	};

	/**  default OfBlockOf Peace data (Y-coordinate) */
	public static final int[][][] DEFAULT_PIECE_DATA_Y = {
		{{1,1,1,1},{0,1,2,3},{2,2,2,2},{3,2,1,0}},	// I
		{{0,1,1,1},{2,2,1,0},{2,1,1,1},{0,0,1,2}},	// L
		{{0,0,1,1},{0,1,1,0},{1,1,0,0},{1,0,0,1}},	// O
		{{0,0,1,1},{0,1,1,2},{2,2,1,1},{2,1,1,0}},	// Z
		{{0,1,1,1},{1,0,1,2},{2,1,1,1},{1,2,1,0}},	// T
		{{0,1,1,1},{0,0,1,2},{2,1,1,1},{2,2,1,0}},	// J
		{{0,0,1,1},{2,1,1,0},{2,2,1,1},{0,1,1,2}},	// S
		{{0      },{0      },{0      },{0      }},	// I1
		{{0,0    },{0,1    },{1,1    },{1,0    }},	// I2
		{{1,1,1  },{0,1,2  },{1,1,1  },{2,1,0  }},	// I3
		{{1,1,0  },{1,0,0  },{0,0,1  },{0,1,1  }},	// L3
	};

	/** New Spin bonusUse coordinate dataA(X-coordinate) */
	public static final int[][][] SPINBONUSDATA_HIGH_X = {
		{{1,2,2,1},{1,3,1,3},{1,2,2,1},{0,2,0,2}},	// I
		{{1,0    },{2,2    },{1,2    },{0,0    }},	// L
		{{       },{       },{       },{       }},	// O
		{{2,0    },{2,1    },{0,2    },{0,1    }},	// Z
		{{0,2    },{2,2    },{0,2    },{0,0    }},	// T
		{{1,2    },{2,2    },{1,0    },{0,0    }},	// J
		{{0,2    },{1,2    },{2,0    },{1,0    }},	// S
		{{       },{       },{       },{       }},	// I1
		{{       },{       },{       },{       }},	// I2
		{{       },{       },{       },{       }},	// I3
		{{       },{       },{       },{       }},	// L3
	};

	/** New Spin bonusUse coordinate dataA(Y-coordinate) */
	public static final int[][][] SPINBONUSDATA_HIGH_Y = {
		{{0,2,0,2},{1,2,2,1},{1,3,1,3},{1,2,2,1}},	// I
		{{0,0    },{1,0    },{2,2    },{1,2    }},	// L
		{{       },{       },{       },{       }},	// O
		{{0,1    },{2,0    },{2,1    },{0,2    }},	// Z
		{{0,0    },{0,2    },{2,2    },{0,2    }},	// T
		{{0,0    },{1,2    },{2,2    },{1,0    }},	// J
		{{0,1    },{2,0    },{2,1    },{0,2    }},	// S
		{{       },{       },{       },{       }},	// I1
		{{       },{       },{       },{       }},	// I2
		{{       },{       },{       },{       }},	// I3
		{{       },{       },{       },{       }},	// L3
	};

	/** New Spin bonusUse coordinate dataB(X-coordinate) */
	public static final int[][][] SPINBONUSDATA_LOW_X = {
		{{-1,4,-1,4},{2,2,2,2},{-1,4,-1,4},{1,1,1,1}},	// I
		{{2,0    },{0,0    },{0,2    },{2,2    }},	// L
		{{       },{       },{       },{       }},	// O
		{{-1,3   },{2,1    },{3,-1   },{0,1    }},	// Z
		{{0,2    },{0,0    },{0,2    },{2,2    }},	// T
		{{0,2    },{0,0    },{2,0    },{2,2    }},	// J
		{{3,-1   },{1,2    },{-1,3   },{1,0    }},	// S
		{{       },{       },{       },{       }},	// I1
		{{       },{       },{       },{       }},	// I2
		{{       },{       },{       },{       }},	// I3
		{{       },{       },{       },{       }},	// L3
	};

	/** New Spin bonusUse coordinate dataB(Y-coordinate) */
	public static final int[][][] SPINBONUSDATA_LOW_Y = {
		{{1,1,1,1},{-1,4,-1,4},{2,2,2,2},{-1,4,-1,4}},	// I
		{{2,2    },{2,0    },{0,0    },{0,3    }},	// L
		{{       },{       },{       },{       }},	// O
		{{0,1    },{-1,3   },{2,1    },{3,-1   }},	// Z
		{{2,2    },{0,2    },{0,0    },{0,2    }},	// T
		{{2,2    },{0,2    },{0,0    },{2,0    }},	// J
		{{0,1    },{-1,3   },{2,1    },{3,-1   }},	// S
		{{       },{       },{       },{       }},	// I1
		{{       },{       },{       },{       }},	// I2
		{{       },{       },{       },{       }},	// I3
		{{       },{       },{       },{       }},	// L3
	};

	/** DirectionConstantcount */
	public static final int DIRECTION_UP = 0, DIRECTION_RIGHT = 1, DIRECTION_DOWN = 2, DIRECTION_LEFT = 3, DIRECTION_RANDOM = 4;

	/** DirectionOfMaximumcount */
	public static final int DIRECTION_COUNT = 4;

	/** RelativeXPosition (4Direction×nBlock) */
	public int[][] dataX;

	/** RelativeYPosition (4Direction×nBlock) */
	public int[][] dataY;

	/** Configure the pieceBlock (nBlock) */
	public Block[] block;

	/** ID */
	public int id;

	/** Direction */
	public int direction;

	/** BigBlock */
	public boolean big;

	/** RelativeXPosition and relativeYOriginal position stateIt has been shifted from thetrue */
	public boolean offsetApplied;

	/** RelativeXWidth misalignment */
	public int[] dataOffsetX;

	/** RelativeYWidth misalignment */
	public int[] dataOffsetY;

	/** Connect blocks in this piece? */
	public boolean connectBlocks;

	/**
	 * Gets the name of the piece
	 * @param id PeaceID
	 * @return Piece name(If an invalid?Returns)
	 */
	public static String getPieceName(int id) {
		if((id >= 0) && (id < PIECE_NAMES.length)) {
			return PIECE_NAMES[id];
		}
		return "?";
	}


	/**
	 * Constructor
	 */
	public Piece() {
		initPiece(0);
	}

	/**
	 * Copy constructor
	 * @param p Copy source
	 */
	public Piece(Piece p) {
		copy(p);
	}

	/**
	 * BlockOf PeaceIDWe can specifyConstructor
	 * @param id BlockOf PeaceID
	 */
	public Piece(int id) {
		initPiece(id);
	}

	/**
	 * BlockOf PeaceInitialization
	 * @param pieceID BlockOf PeaceID
	 */
	public void initPiece(int pieceID) {
		this.id = pieceID;
		this.direction = DIRECTION_UP;
		this.big = false;
		this.offsetApplied = false;
		this.connectBlocks = true;

		int maxBlock = getMaxBlock();
		dataX = new int[DIRECTION_COUNT][maxBlock];
		dataY = new int[DIRECTION_COUNT][maxBlock];
		block = new Block[maxBlock];
		for(int i = 0; i < block.length; i++) block[i] = new Block();
		dataOffsetX = new int[DIRECTION_COUNT];
		dataOffsetY = new int[DIRECTION_COUNT];

		resetOffsetArray();
	}

	/**
	 * BlockOf Peace dataOtherPieceCopied from the
	 * @param p Copy source
	 */
	public void copy(Piece p) {
		id = p.id;
		direction = p.direction;
		big = p.big;
		offsetApplied = p.offsetApplied;
		connectBlocks = p.connectBlocks;

		int maxBlock = p.getMaxBlock();
		dataX = new int[DIRECTION_COUNT][maxBlock];
		dataY = new int[DIRECTION_COUNT][maxBlock];
		block = new Block[maxBlock];
		for(int i = 0; i < maxBlock; i++) block[i] = new Block(p.block[i]);
		dataOffsetX = new int[DIRECTION_COUNT];
		dataOffsetY = new int[DIRECTION_COUNT];

		for(int i = 0; i < DIRECTION_COUNT; i++) {
			for(int j = 0; j < maxBlock; j++) {
				dataX[i][j] = p.dataX[i][j];
				dataY[i][j] = p.dataY[i][j];
			}
			dataOffsetX[i] = p.dataOffsetX[i];
			dataOffsetY[i] = p.dataOffsetY[i];
		}
	}

	/**
	 * 1Are included in one piece ofBlockOfcountGet the
	 * @return 1Are included in one piece ofBlockOfcount
	 */
	public int getMaxBlock() {
		return DEFAULT_PIECE_DATA_X[id][direction].length;
	}

	/**
	 * AllBlock stateAbSet the same as the
	 * @param b SetBlock
	 */
	public void setBlock(Block b) {
		for(int i = 0; i < block.length; i++) block[i].copy(b);
	}

	/**
	 * AllBlock colorChange
	 * @param color Color
	 */
	public void setColor(int color) {
		for(int i = 0; i < block.length; i++) {
			block[i].color = color;
		}
	}

	/**
	 * Changes the colors of the blocks individually; allows one piece to have
	 * blocks of multiple colors
	 * @param color Array with each cell specifying a color of a block
	 */
	public void setColor(int[] color) {
		int length = Math.min(block.length, color.length);
		for(int i = 0; i < length; i++) {
			block[i].color = color[i];
		}
	}

	/**
	 * Sets all blocks to an item block
	 * @param item ID number of the item
	 */
	public void setItem(int item) {
		for(int i = 0; i < block.length; i++) {
			block[i].item = item;
		}
	}

	/**
	 * Sets the items of the blocks individually; allows one piece to have
	 * different item settings for each block
	 * @param item Array with each element specifying a color of a block
	 */
	public void setItem(int[] item) {
		int length = Math.min(block.length, item.length);
		for(int i = 0; i < length; i++) {
			block[i].item = item[i];
		}
	}

	/**
	 * Sets all blocks' hard count
	 * @param hard Hard count
	 */
	public void setHard(int hard) {
		for(int i = 0; i < block.length; i++) {
			block[i].hard = hard;
		}
	}

	/**
	 * Sets the hard counts of the blocks individually; allows one piece to have
	 * different hard count settings for each block
	 * @param hard Array with each element specifying a hard count of a block
	 */
	public void setHard(int[] hard) {
		int length = Math.min(block.length, hard.length);
		for(int i = 0; i < length; i++) {
			block[i].hard = hard[i];
		}
	}

	/**
	 * Fetches the colors of the blocks in the piece
	 * @return An int array containing the color of each block
	 */
	public int[] getColors() {
		int[] result = new int[block.length];
		for(int i = 0; i < block.length; i++)
			result[i] = block[i].color;
		return result;
	}

	/**
	 * AllBlockChange the pattern of
	 * @param skin Pattern
	 */
	public void setSkin(int skin) {
		for(int i = 0; i < block.length; i++) {
			block[i].skin = skin;
		}
	}

	/**
	 * AllBlockOf course frame Change
	 * @param elapsedFrames I have elapsed since a fixed frame count
	 */
	public void setElapsedFrames(int elapsedFrames) {
		for(int i = 0; i < block.length; i++) {
			block[i].elapsedFrames = elapsedFrames;
		}
	}

	/**
	 * AllBlockChange the darkness or lightness of
	 * @param darkness It is the darkness or lightness (0.03If it&#39;s the case3%Darkly, -0.05If it&#39;s the case5%Bright)
	 */
	public void setDarkness(float darkness) {
		for(int i = 0; i < block.length; i++) {
			block[i].darkness = darkness;
		}
	}

	/**
	 * AllBlockChange the transparency of
	 * @param alpha Transparency (1.0fOpacity in, 0.0fCompletely transparent in)
	 */
	public void setAlpha(float alpha) {
		for(int i = 0; i < block.length; i++) {
			block[i].alpha = alpha;
		}
	}

	/**
	 * AllBlockSet the attributes of the
	 * @param attr I want to change the attributes
	 * @param status After the change state
	 */
	public void setAttribute(int attr, boolean status) {
		for(int i = 0; i < block.length; i++) block[i].setAttribute(attr, status);
	}

	/**
	 * RelativeXPosition and relativeYPosition is shifted
	 * @param offsetX XArray of position correction amount (int[DIRECTION_COUNT])
	 * @param offsetY YArray of position correction amount (int[DIRECTION_COUNT])
	 */
	public void applyOffsetArray(int[] offsetX, int[] offsetY) {
		applyOffsetArrayX(offsetX);
		applyOffsetArrayY(offsetY);
	}

	/**
	 * RelativeXPosition is shifted
	 * @param offsetX XArray of position correction amount (int[DIRECTION_COUNT])
	 */
	public void applyOffsetArrayX(int[] offsetX) {
		offsetApplied = true;

		for(int i = 0; i < DIRECTION_COUNT; i++) {
			for(int j = 0; j < getMaxBlock(); j++) {
				dataX[i][j] += offsetX[i];
			}
			dataOffsetX[i] = offsetX[i];
		}
	}

	/**
	 * RelativeYPosition is shifted
	 * @param offsetY YArray of position correction amount (int[DIRECTION_COUNT])
	 */
	public void applyOffsetArrayY(int[] offsetY) {
		offsetApplied = true;

		for(int i = 0; i < DIRECTION_COUNT; i++) {
			for(int j = 0; j < getMaxBlock(); j++) {
				dataY[i][j] += offsetY[i];
			}
			dataOffsetY[i] = offsetY[i];
		}
	}

	/**
	 * RelativeXPosition and relativeYPosition back to the initial state
	 */
	public void resetOffsetArray() {
		for(int i = 0; i < DIRECTION_COUNT; i++) {
			for(int j = 0; j < getMaxBlock(); j++) {
				dataX[i][j] = DEFAULT_PIECE_DATA_X[id][i][j];
				dataY[i][j] = DEFAULT_PIECE_DATA_Y[id][i][j];
			}
			dataOffsetX[i] = 0;
			dataOffsetY[i] = 0;
		}
		offsetApplied = false;
	}

	/**
	 * BlockTies dataUpdate
	 */
	public void updateConnectData() {
		for(int j = 0; j < getMaxBlock(); j++) {
			// RelativeXPosition and relativeYPosition
			int bx = dataX[direction][j];
			int by = dataY[direction][j];

			block[j].setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, false);
			block[j].setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, false);
			block[j].setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, false);
			block[j].setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, false);

			if (connectBlocks)
			{
				block[j].setAttribute(Block.BLOCK_ATTRIBUTE_BROKEN, false);
				// Other3Of oneBlockExamine the relationship between the
				for(int k = 0; k < getMaxBlock(); k++) {
					if(k != j) {
						int bx2 = dataX[direction][k];
						int by2 = dataY[direction][k];

						if((bx == bx2) && (by - 1 == by2)) block[j].setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, true);		// Up
						if((bx == bx2) && (by + 1 == by2)) block[j].setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, true);	// Down
						if((by == by2) && (bx - 1 == bx2)) block[j].setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, true);	// Left
						if((by == by2) && (bx + 1 == bx2)) block[j].setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, true);	// Right
					}
				}
			}
			else
				block[j].setAttribute(Block.BLOCK_ATTRIBUTE_BROKEN, true);
		}
	}

	/**
	 * 1One or moreBlockThefieldBe placed to determine whether Attempts off target
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param rt Direction
	 * @param fld field
	 * @return 1One or moreBlockThefieldAttempts off target would be placed intrue, Otherwisefalse
	 */
	public boolean isPartialLockOut(int x, int y, int rt, Field fld) {
		// BigThe only treatment
		if(big == true) return isPartialLockOutBig(x, y, rt, fld);

		boolean placed = false;

		for(int i = 0; i < getMaxBlock(); i++) {
			int y2 = y + dataY[rt][i];
			if(y2 < 0) placed = true;
		}

		return placed;
	}

	/**
	 * 1One or moreBlockThefieldBe placed to determine whether Attempts off target(BigUse)
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param rt Direction
	 * @param fld field
	 * @return 1One or moreBlockThefieldAttempts off target would be placed intrue, Otherwisefalse
	 */
	protected boolean isPartialLockOutBig(int x, int y, int rt, Field fld) {
		boolean placed = false;

		for(int i = 0; i < getMaxBlock(); i++) {
			int y2 = (y + dataY[rt][i] * 2);

			// 4BlockMinute put
			for(int k = 0; k < 2; k++)for(int l = 0; l < 2; l++) {
				int y3 = y2 + l;
				if(y3 < 0) placed = true;
			}
		}

		return placed;
	}

	/**
	 * 1One or moreBlockThefieldBe placed to determine whether Attempts off target
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param fld field
	 * @return 1One or moreBlockThefieldAttempts off target would be placed intrue, Otherwisefalse
	 */
	public boolean isPartialLockOut(int x, int y, Field fld) {
		return isPartialLockOut(x, y, direction, fld);
	}

	/**
	 * 1One or moreBlockAfieldDecision whether to put in the frame(fieldBut make no changes to the)
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param rt Direction
	 * @param fld field
	 * @return 1One or moreBlockAfieldI put in the frametrue, Otherwisefalse
	 */
	public boolean canPlaceToVisibleField(int x, int y, int rt, Field fld) {
		// BigThe only treatment
		if(big == true) return canPlaceToVisibleFieldBig(x, y, rt, fld);

		boolean placed = false;

		for(int i = 0; i < getMaxBlock(); i++) {
			int y2 = y + dataY[rt][i];
			if(y2 >= 0) placed = true;
		}

		return placed;
	}

	/**
	 * 1One or moreBlockAfieldDecision whether to put in the frame(fieldBut make no changes to the.BigUse)
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param rt Direction
	 * @param fld field
	 * @return 1One or moreBlockAfieldI put in the frametrue, Otherwisefalse
	 */
	protected boolean canPlaceToVisibleFieldBig(int x, int y, int rt, Field fld) {
		boolean placed = false;

		for(int i = 0; i < getMaxBlock(); i++) {
			int y2 = (y + dataY[rt][i] * 2);

			// 4BlockMinute put
			for(int k = 0; k < 2; k++)for(int l = 0; l < 2; l++) {
				int y3 = y2 + l;
				if(y3 >= 0) placed = true;
			}
		}

		return placed;
	}

	/**
	 * 1One or moreBlockAfieldDecision whether to put in the frame(fieldBut make no changes to the)
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param fld field
	 * @return 1One or moreBlockAfieldI put in the frametrue, Otherwisefalse
	 */
	public boolean canPlaceToVisibleField(int x, int y, Field fld) {
		return canPlaceToVisibleField(x, y, direction, fld);
	}

	/**
	 * fieldPlace the piece to
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param rt Direction
	 * @param fld field
	 * @return 1One or moreBlockAfieldI was put in the frametrue, Otherwisefalse
	 */
	public boolean placeToField(int x, int y, int rt, Field fld) {
		updateConnectData();

		//On a Big piece, double its size.
		int size = 1;
		if(big == true) size = 2;

		boolean placed = false;

		for(int i = 0; i < getMaxBlock(); i++) {
			int x2 = x + dataX[rt][i] * size; //Multiply co-ordinate offset by piece size.
			int y2 = y + dataY[rt][i] * size;

			fld.setAllAttribute(Block.BLOCK_ATTRIBUTE_LAST_COMMIT, false);
			block[i].setAttribute(Block.BLOCK_ATTRIBUTE_LAST_COMMIT, true);

			/*
			 * Loop through width/height of the block, setting cells in the field.
			 * If the piece is normal (size == 1), a standard, 1x1 space is allotted per block.
			 * If the piece is big (size == 2), a 2x2 space is allotted per block.
			 */
			for(int k = 0; k < size; k++){
				for(int l = 0; l < size; l++){
					int x3 = x2 + k;
					int y3 = y2 + l;
					Block blk = new Block(block[i]);

					// Set Big block connections
					if(big) {
						if(block[i].getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT) && block[i].getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT)) {
							// Top
							if(l == 0) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, true);
							}
							// Bottom
							if(l == 1) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, true);
							}
						}
						else if(block[i].getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT)) {
							// Top
							if(l == 0) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, true);
							}
							// Bottom
							if(l == 1) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, true);
							}
							// Left
							if(k == 0) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, true);
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, true);
							}
							// Right
							if(k == 1) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, true);
							}
						}
						else if(block[i].getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT)) {
							// Top
							if(l == 0) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, true);
							}
							// Bottom
							if(l == 1) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, true);
							}
							// Left
							if(k == 0) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, true);
							}
							// Right
							if(k == 1) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, true);
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, true);
							}
						}

						if(block[i].getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP) && block[i].getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
							// Left
							if(k == 0) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, true);
							}
							// Right
							if(k == 1) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, true);
							}
						}
						else if(block[i].getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
							// Left
							if(k == 0) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, true);
							}
							// Right
							if(k == 1) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, true);
							}
							// Top
							if(l == 0) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, true);
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, true);
							}
							// Bottom
							if(l == 1) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, true);
							}
						}
						else if(block[i].getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
							// Left
							if(k == 0) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, true);
							}
							// Right
							if(k == 1) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, true);
							}
							// Top
							if(l == 0) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, true);
							}
							// Bottom
							if(l == 1) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, true);
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, true);
							}
						}
					}

					fld.setBlock(x3, y3, blk);
					if(y3 >= 0) placed = true;
				}
			}
		}

		return placed;
	}

	/**
	 * fieldPlace the piece to
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param fld field
	 * @return 1One or moreBlockAfieldI was put in the frametrue, Otherwisefalse
	 */
	public boolean placeToField(int x, int y, Field fld) {
		return placeToField(x, y, direction, fld);
	}

	/**
	 * Collision detection of Peace
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param fld field
	 * @return BlockI was overlaptrue, They do not overlapfalse
	 */
	public boolean checkCollision(int x, int y, Field fld) {
		return checkCollision(x, y, direction, fld);
	}

	/**
	 * Collision detection of Peace
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param rt Direction
	 * @param fld field
	 * @return BlockI was overlaptrue, They do not overlapfalse
	 */
	public boolean checkCollision(int x, int y, int rt, Field fld) {
		// BigThe only treatment
		if(big == true) return checkCollisionBig(x, y, rt, fld);

		for(int i = 0; i < getMaxBlock(); i++) {
			int x2 = x + dataX[rt][i];
			int y2 = y + dataY[rt][i];

			if(x2 >= fld.getWidth()) {
				return true;
			}
			if(y2 >= fld.getHeight()) {
				return true;
			}
			if(fld.getCoordAttribute(x2, y2) == Field.COORD_WALL) {
				return true;
			}
			if((fld.getCoordAttribute(x2, y2) != Field.COORD_VANISH) && (fld.getBlockColor(x2, y2) != Block.BLOCK_COLOR_NONE)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Collision detection of Peace (BigFor)
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param rt Direction
	 * @param fld field
	 * @return BlockI was overlaptrue, They do not overlapfalse
	 */
	protected boolean checkCollisionBig(int x, int y, int rt, Field fld) {
		for(int i = 0; i < getMaxBlock(); i++) {
			int x2 = (x + dataX[rt][i] * 2);
			int y2 = (y + dataY[rt][i] * 2);

			// 4BlockMinutes to examine
			for(int k = 0; k < 2; k++)for(int l = 0; l < 2; l++) {
				int x3 = x2 + k;
				int y3 = y2 + l;

				if(x3 >= fld.getWidth()) {
					return true;
				}
				if(y3 >= fld.getHeight()) {
					return true;
				}
				if(fld.getCoordAttribute(x3, y3) == Field.COORD_WALL) {
					return true;
				}
				if((fld.getCoordAttribute(x3, y3) != Field.COORD_VANISH) && (fld.getBlockColor(x3, y3) != Block.BLOCK_COLOR_NONE)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * When I dropped the piece as it isY-coordinateGet the
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param rt Direction
	 * @param fld field
	 * @return When I dropped the piece as it isY-coordinate
	 */
	public int getBottom(int x, int y, int rt, Field fld) {
		int y2 = y;

		while(checkCollision(x, y2, rt, fld) == false) {
			y2++;
		}

		return y2 - 1;
	}

	/**
	 * When I dropped the piece as it isY-coordinateGet the
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param fld field
	 * @return When I dropped the piece as it isY-coordinate
	 */
	public int getBottom(int x, int y, Field fld) {
		return getBottom(x, y, direction, fld);
	}

	/**
	 * Gets the width of the piece
	 * @return The width of the piece
	 */
	public int getWidth() {
		int max = dataX[direction][0];
		int min = dataX[direction][0];

		for(int j = 1; j < getMaxBlock(); j++) {
			int bx = dataX[direction][j];

			max = Math.max(bx, max);
			min = Math.min(bx, min);
		}

		int wide = 1;
		if(big == true) wide = 2;

		return (max - min) * wide;
	}

	/**
	 * Gets the height of the piece
	 * @return The height of the piece
	 */
	public int getHeight() {
		int max = dataY[direction][0];
		int min = dataY[direction][0];

		for(int j = 1; j < getMaxBlock(); j++) {
			int by = dataY[direction][j];

			max = Math.max(by, max);
			min = Math.min(by, min);
		}

		int wide = 1;
		if(big == true) wide = 2;

		return (max - min) * wide;
	}

	/**
	 * Highest TetoraminoBlockOfX-coordinateGet the
	 * @return Highest TetoraminoBlockOfX-coordinate
	 */
	public int getMinimumBlockX() {
		int min = dataX[direction][0];

		for(int j = 1; j < getMaxBlock(); j++) {
			int by = dataX[direction][j];

			min = Math.min(by, min);
		}

		int wide = 1;
		if(big == true) wide = 2;

		return min * wide;
	}

	/**
	 * Lowest TetoraminoBlockOfX-coordinateGet the
	 * @return Lowest TetoraminoBlockOfX-coordinate
	 */
	public int getMaximumBlockX() {
		int max = dataX[direction][0];

		for(int j = 1; j < getMaxBlock(); j++) {
			int by = dataX[direction][j];

			max = Math.max(by, max);
		}

		int wide = 1;
		if(big == true) wide = 2;

		return max * wide;
	}

	/**
	 * Highest TetoraminoBlockOfY-coordinateGet the
	 * @return Highest TetoraminoBlockOfY-coordinate
	 */
	public int getMinimumBlockY() {
		int min = dataY[direction][0];

		for(int j = 1; j < getMaxBlock(); j++) {
			int by = dataY[direction][j];

			min = Math.min(by, min);
		}

		int wide = 1;
		if(big == true) wide = 2;

		return min * wide;
	}

	/**
	 * Lowest TetoraminoBlockOfY-coordinateGet the
	 * @return Lowest TetoraminoBlockOfY-coordinate
	 */
	public int getMaximumBlockY() {
		int max = dataY[direction][0];

		for(int j = 1; j < getMaxBlock(); j++) {
			int by = dataY[direction][j];

			max = Math.max(by, max);
		}

		int wide = 1;
		if(big == true) wide = 2;

		return max * wide;
	}

	/**
	 * Determines how far we can move to the left or from the current position
	 * @param nowX CurrentXPosition
	 * @param nowY CurrentYPosition
	 * @param rt Of PeaceDirection
	 * @param fld field
	 * @return Left-most position of a movable
	 */
	public int getMostMovableLeft(int nowX, int nowY, int rt, Field fld) {
		int x = nowX;
		while(!checkCollision(x - 1, nowY, rt, fld)) x--;
		return x;
	}

	/**
	 * Determines how far you can move from the current position to the right
	 * @param nowX CurrentXPosition
	 * @param nowY CurrentYPosition
	 * @param rt Of PeaceDirection
	 * @param fld field
	 * @return Rightmost position of movable
	 */
	public int getMostMovableRight(int nowX, int nowY, int rt, Field fld) {
		int x = nowX;
		while(!checkCollision(x + 1, nowY, rt, fld)) x++;
		return x;
	}

	/**
	 * rotation buttonPiece after pressing theDirectionGet the
	 * @param move rotationDirection (-1:Left 1:Right 2:180Degrees)
	 * @return rotation buttonPiece after pressing theDirection
	 */
	public int getRotateDirection(int move) {
		int rt = direction + move;

		if(move == 2) {
			if(rt > 3) rt -= 4;
			if(rt < 0) rt += 4;
		} else {
			if(rt > 3) rt = 0;
			if(rt < 0) rt = 3;
		}

		return rt;
	}

	/**
	 * rotation buttonPiece after pressing theDirectionGet the
	 * @param move rotationDirection (-1:Left 1:Right 2:180Degrees)
	 * @param dir OriginalDirection
	 * @return rotation buttonPiece after pressing theDirection
	 */
	public int getRotateDirection(int move, int dir) {
		int rt = dir + move;

		if(move == 2) {
			if(rt > 3) rt -= 4;
			if(rt < 0) rt += 4;
		} else {
			if(rt > 3) rt = 0;
			if(rt < 0) rt = 3;
		}

		return rt;
	}
}
