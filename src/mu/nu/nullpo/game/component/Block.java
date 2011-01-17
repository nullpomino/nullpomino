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

import mu.nu.nullpo.game.play.GameEngine;

/**
 * Block
 */
public class Block implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = -7126899262733374545L;

	/** Block colorの定count */
	public static final int BLOCK_COLOR_INVALID = -1,
							BLOCK_COLOR_NONE = 0,
							BLOCK_COLOR_GRAY = 1,
							BLOCK_COLOR_RED = 2,
							BLOCK_COLOR_ORANGE = 3,
							BLOCK_COLOR_YELLOW = 4,
							BLOCK_COLOR_GREEN = 5,
							BLOCK_COLOR_CYAN = 6,
							BLOCK_COLOR_BLUE = 7,
							BLOCK_COLOR_PURPLE = 8,
							BLOCK_COLOR_GEM_RED = 9,
							BLOCK_COLOR_GEM_ORANGE = 10,
							BLOCK_COLOR_GEM_YELLOW = 11,
							BLOCK_COLOR_GEM_GREEN = 12,
							BLOCK_COLOR_GEM_CYAN = 13,
							BLOCK_COLOR_GEM_BLUE = 14,
							BLOCK_COLOR_GEM_PURPLE = 15,
							BLOCK_COLOR_SQUARE_GOLD_1 = 16,
							BLOCK_COLOR_SQUARE_GOLD_2 = 17,
							BLOCK_COLOR_SQUARE_GOLD_3 = 18,
							BLOCK_COLOR_SQUARE_GOLD_4 = 19,
							BLOCK_COLOR_SQUARE_GOLD_5 = 20,
							BLOCK_COLOR_SQUARE_GOLD_6 = 21,
							BLOCK_COLOR_SQUARE_GOLD_7 = 22,
							BLOCK_COLOR_SQUARE_GOLD_8 = 23,
							BLOCK_COLOR_SQUARE_GOLD_9 = 24,
							BLOCK_COLOR_SQUARE_SILVER_1 = 25,
							BLOCK_COLOR_SQUARE_SILVER_2 = 26,
							BLOCK_COLOR_SQUARE_SILVER_3 = 27,
							BLOCK_COLOR_SQUARE_SILVER_4 = 28,
							BLOCK_COLOR_SQUARE_SILVER_5 = 29,
							BLOCK_COLOR_SQUARE_SILVER_6 = 30,
							BLOCK_COLOR_SQUARE_SILVER_7 = 31,
							BLOCK_COLOR_SQUARE_SILVER_8 = 32,
							BLOCK_COLOR_SQUARE_SILVER_9 = 33,
							BLOCK_COLOR_RAINBOW = 34,
							BLOCK_COLOR_GEM_RAINBOW = 35;

	/** アイテムの定count */
	public static final int BLOCK_ITEM_NONE = 0,
							BLOCK_ITEM_RANDOM = 1;

	public static final int MAX_ITEM = 1;

	/** 通常のBlock colorのMaximumcount */
	public static final int BLOCK_COLOR_COUNT = 9;

	/** 通常＋宝石Block colorのMaximumcount */
	public static final int BLOCK_COLOR_EXT_COUNT = 16;

	/** Block表示あり */
	public static final int BLOCK_ATTRIBUTE_VISIBLE = 1;

	/** 枠線表示あり */
	public static final int BLOCK_ATTRIBUTE_OUTLINE = 2;

	/** 骨Block */
	public static final int BLOCK_ATTRIBUTE_BONE = 4;

	/** 上のBlockと繋がっている */
	public static final int BLOCK_ATTRIBUTE_CONNECT_UP = 8;

	/** 下のBlockと繋がっている */
	public static final int BLOCK_ATTRIBUTE_CONNECT_DOWN = 16;

	/** 左のBlockと繋がっている */
	public static final int BLOCK_ATTRIBUTE_CONNECT_LEFT = 32;

	/** 右のBlockと繋がっている */
	public static final int BLOCK_ATTRIBUTE_CONNECT_RIGHT = 64;

	/** 自分で置いたBlock */
	public static final int BLOCK_ATTRIBUTE_SELFPLACED = 128;

	/** 壊れたピースの一部分 */
	public static final int BLOCK_ATTRIBUTE_BROKEN = 256;

	/** ojama block */
	public static final int BLOCK_ATTRIBUTE_GARBAGE = 512;

	/** 壁 */
	public static final int BLOCK_ATTRIBUTE_WALL = 1024;

	/** 消える予定のBlock */
	public static final int BLOCK_ATTRIBUTE_ERASE = 2048;

	/** Temporary mark for block linking check algorithm */
	public static final int BLOCK_ATTRIBUTE_TEMP_MARK = 4096;

	/** "Block has fallen" flag for cascade gravity */
	public static final int BLOCK_ATTRIBUTE_CASCADE_FALL = 8192;

	/** Anti-gravity flag (The block will not fall by gravity) */
	public static final int BLOCK_ATTRIBUTE_ANTIGRAVITY = 16384;

	/** Last commit flag -- block was part of last placement or cascade **/
	public static final int BLOCK_ATTRIBUTE_LAST_COMMIT = 32768;

	/** Ignore block connections (for Avalanche modes) */
	public static final int BLOCK_ATTRIBUTE_IGNORE_BLOCKLINK = 65536;

	/** Block color */
	public int color;

	/** Blockの絵柄 */
	public int skin;

	/** Blockの属性 */
	public int attribute;

	/** 固定してから経過した frame count */
	public int elapsedFrames;

	/** Blockの暗さ, または明るさ (0.03だったら3%暗く, -0.05だったら5%明るい) */
	public float darkness;

	/** 透明度 (1.0fで不透明, 0.0fで完全に透明) */
	public float alpha;

	/** ゲームが始まってから何番目に置いたBlockか (負countだったら初期配置やgarbage block) */
	public int pieceNum;

	/** アイテム number */
	public int item;

	/** Number of extra clears required before block is erased */
	public int hard;

	/** Counter for blocks that count down before some effect occurs */
	public int countdown;

	/** Color-shift phase for rainbow blocks */
	public static int rainbowPhase = 0;

	/** Color to turn into when garbage block turns into a regular block */
	public int secondaryColor;

	/** Bonus value awarded when cleared */
	public int bonusValue;

	/**
	 * Constructor
	 */
	public Block() {
		reset();
	}

	/**
	 * 色指定可能なConstructor
	 * @param color Block color
	 */
	public Block(int color) {
		reset();
		this.color = color;
	}

	/**
	 * 色と絵柄の指定が可能なConstructor
	 * @param color Block color
	 * @param skin Blockの絵柄
	 */
	public Block(int color, int skin) {
		reset();
		this.color = color;
		this.skin = skin;
	}

	/**
	 * 色と絵柄と属性の指定が可能なConstructor
	 * @param color Block color
	 * @param skin Blockの絵柄
	 * @param attribute Blockの属性
	 */
	public Block(int color, int skin, int attribute) {
		reset();
		this.color = color;
		this.skin = skin;
		this.attribute = attribute;
	}

	/**
	 * Copy constructor
	 * @param b Copy source
	 */
	public Block(Block b) {
		copy(b);
	}

	/**
	 * 設定をReset to defaults
	 */
	public void reset() {
		color = BLOCK_COLOR_NONE;
		skin = 0;
		attribute = 0;
		elapsedFrames = 0;
		darkness = 0f;
		alpha = 1f;
		pieceNum = -1;
		item = 0;
		hard = 0;
		countdown = 0;
		secondaryColor = 0;
		bonusValue = 0;
	}

	/**
	 * 設定を他のBlockからコピー
	 * @param b Copy source
	 */
	public void copy(Block b) {
		color = b.color;
		skin = b.skin;
		attribute = b.attribute;
		elapsedFrames = b.elapsedFrames;
		darkness = b.darkness;
		alpha = b.alpha;
		pieceNum = b.pieceNum;
		item = b.item;
		hard = b.hard;
		countdown = b.countdown;
		secondaryColor = b.secondaryColor;
		bonusValue = b.bonusValue;
	}

	/**
	 * 指定した属性 stateを調べる
	 * @param attr 調べたい属性
	 * @return 指定した属性がすべてセットされている場合はtrue
	 */
	public boolean getAttribute(int attr) {
		return ((attribute & attr) != 0);
	}

	/**
	 * 属性を変更する
	 * @param attr 変更したい属性
	 * @param status 変更後 state
	 */
	public void setAttribute(int attr, boolean status) {
		if(status) attribute |= attr;
		else attribute &= ~attr;
	}

	/**
	 * このBlockが空白かどうか判定
	 * @return このBlockが空白だったらtrue
	 */
	public boolean isEmpty() {
		return (color < BLOCK_COLOR_GRAY);
	}

	/**
	 * このBlockが宝石Blockかどうか判定
	 * @return このBlockが宝石Blockだったらtrue
	 */
	public boolean isGemBlock() {
		return ((color >= BLOCK_COLOR_GEM_RED) && (color <= BLOCK_COLOR_GEM_PURPLE)) ||
				(color == BLOCK_COLOR_GEM_RAINBOW);
	}

	/**
	 * Checks to see if <code>this</code> is a gold square block
	 * @return <code>true</code> if the block is a gold square block
	 */
	public boolean isGoldSquareBlock() {
		return (color >= BLOCK_COLOR_SQUARE_GOLD_1) && (color <= BLOCK_COLOR_SQUARE_GOLD_9);
	}

	/**
	 * Checks to see if <code>this</code> is a silver square block
	 * @return <code>true</code> if the block is a silver square block
	 */
	public boolean isSilverSquareBlock() {
		return (color >= BLOCK_COLOR_SQUARE_SILVER_1) && (color <= BLOCK_COLOR_SQUARE_SILVER_9);
	}

	/**
	 * Checks to see if <code>this</code> is a normal block (gray to purple)
	 * @return <code>true</code> if the block is a normal block
	 */
	public boolean isNormalBlock() {
		return (color >= BLOCK_COLOR_GRAY) && (color <= BLOCK_COLOR_PURPLE);
	}

	public int getDrawColor() {
		if (color == BLOCK_COLOR_GEM_RAINBOW)
			return BLOCK_COLOR_GEM_RED + (rainbowPhase/3);
		else if (color == BLOCK_COLOR_RAINBOW)
			return BLOCK_COLOR_RED + (rainbowPhase/3);
		else
			return color;
	}

	/**
	 * @return the character representing the color of this block
	 */
	public char blockToChar(){
		//'0'-'9','A'-'Z' represent colors 0-35.
		//Colors beyond that would follow the ASCII table starting at '['.
		if(color >= 10) {
			return (char)('A' + (color - 10));
		}
		return (char)('0' + Math.max(0, color));
	}

	@Override
	public String toString(){
		return ""+blockToChar();
	}

	/**
	 * @param c A character representing a block
	 * @return The int representing the block's color
	 */
	public static int charToBlockColor(char c){
		int blkColor = 0;

		//With a radix of 36, the digits encompass '0'-'9','A'-'Z'.
		//With a radix higher than 36, we can also have characters 'a'-'z' represent digits.
		blkColor = Character.digit(c, 36);

		//Given the current implementation of other functions, I assumed that
		//if we needed additional BLOCK_COLOR values, it would follow from 'Z'->'['
		//in the ASCII chart.
		if(blkColor == -1) {
			blkColor = (c - '[') + 36;
		}
		return blkColor;
	}

	public static void updateRainbowPhase(int time) {
		rainbowPhase = time%21;
	}

	public static void updateRainbowPhase(GameEngine engine) {
		if (engine != null && engine.timerActive)
			updateRainbowPhase(engine.statistics.time);
		else
		{
			rainbowPhase++;
			if (rainbowPhase >= 21)
				rainbowPhase = 0;
		}
	}

	public static int gemToNormalColor(int color)
	{
		if ((color >= BLOCK_COLOR_GEM_RED) && (color <= BLOCK_COLOR_GEM_PURPLE))
			return color - 7;
		else if (color == BLOCK_COLOR_GEM_RAINBOW)
			return BLOCK_COLOR_RAINBOW;
		else
			return color;
	}
}
