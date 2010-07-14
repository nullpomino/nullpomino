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
package org.game_host.hebo.nullpomino.game.component;

import java.io.Serializable;

import org.game_host.hebo.nullpomino.util.CustomProperties;

/**
 * ゲームフィールド
 */
public class Field implements Serializable {

	/** シリアルバージョンID */
	private static final long serialVersionUID = 7745183278794213487L;

	/** デフォルトの幅 */
	public static final int DEFAULT_WIDTH = 10;

	/** デフォルトの高さ */
	public static final int DEFAULT_HEIGHT = 20;

	/** デフォルトの見えない部分の高さ */
	public static final int DEFAULT_HIDDEN_HEIGHT = 3;

	/** 座標の属性（通常） */
	public static final int COORD_NORMAL = 0;

	/** 座標の属性（見えない部分） */
	public static final int COORD_HIDDEN = 1;

	/** 座標の属性（置いたブロックが消える） */
	public static final int COORD_VANISH = 2;

	/** 座標の属性（壁） */
	public static final int COORD_WALL = 3;

	/** フィールドの幅 */
	protected int width;

	/** フィールドの高さ */
	protected int height;

	/** フィールドより上の見えない部分の高さ */
	protected int hidden_height;

	/** フィールドのブロック */
	protected Block[][] block_field;

	/** フィールド上の見えない部分のブロック */
	protected Block[][] block_hidden;

	/** ライン消去フラグ */
	protected boolean[] lineflag_field;

	/** 見えない部分のライン消去フラグ */
	protected boolean[] lineflag_hidden;

	/** HURRY UP地面の数 */
	protected int hurryupFloorLines;

	/** 天井の有無 */
	public boolean ceiling;

	/**
	 * パラメータ付きコンストラクタ
	 * @param w フィールドの幅
	 * @param h フィールドの高さ
	 * @param hh フィールドより上の見えない部分の高さ
	 */
	public Field(int w, int h, int hh) {
		width = w;
		height = h;
		hidden_height = hh;

		ceiling = false;
		reset();
	}

	/**
	 * パラメータ付きコンストラクタ
	 * @param w フィールドの幅
	 * @param h フィールドの高さ
	 * @param hh フィールドより上の見えない部分の高さ
	 * @param c 天井の有無
	 */
	public Field(int w, int h, int hh, boolean c) {
		this(w, h, hh);
		ceiling = c;
	}

	/**
	 * デフォルトコンストラクタ
	 */
	public Field() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_HIDDEN_HEIGHT);
	}

	/**
	 * コピーコンストラクタ
	 * @param f コピー元
	 */
	public Field(Field f) {
		copy(f);
	}

	/**
	 * 初期化処理
	 */
	public void reset() {
		block_field = new Block[width][height];
		block_hidden = new Block[width][hidden_height];
		lineflag_field = new boolean[height];
		lineflag_hidden = new boolean[hidden_height];
		hurryupFloorLines = 0;

		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				block_field[i][j] = new Block();
			}
			for(int j = 0; j < hidden_height; j++) {
				block_hidden[i][j] = new Block();
			}
		}
	}

	/**
	 * 別のFieldからコピー
	 * @param f コピー元
	 */
	public void copy(Field f) {
		width = f.width;
		height = f.height;
		hidden_height = f.hidden_height;
		ceiling = f.ceiling;

		block_field = new Block[width][height];
		block_hidden = new Block[width][hidden_height];
		lineflag_field = new boolean[height];
		lineflag_hidden = new boolean[hidden_height];
		hurryupFloorLines = f.hurryupFloorLines;

		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				block_field[i][j] = new Block(f.getBlock(i, j));
			}
			for(int j = 0; j < hidden_height; j++) {
				block_hidden[i][j] = new Block(f.getBlock(i, -j-1));
			}
		}
	}

	/**
	 * プロパティセットに保存
	 * @param p プロパティセット
	 * @param id 適当なID
	 */
	public void writeProperty(CustomProperties p, int id) {
		for(int i = 0; i < height; i++) {
			String mapStr = "";

			for(int j = 0; j < width; j++) {
				mapStr += String.valueOf(getBlockColor(j, i));
				if(j < width - 1) mapStr += ",";
			}

			p.setProperty(id + ".field.map." + i, mapStr);
		}
	}

	/**
	 * プロパティセットから読み込み
	 * @param p プロパティセット
	 * @param id 適当なID
	 */
	public void readProperty(CustomProperties p, int id) {
		for(int i = 0; i < height; i++) {
			String mapStr = p.getProperty(id + ".field.map." + i, "");
			String[] mapArray = mapStr.split(",");

			for(int j = 0; j < mapArray.length; j++) {
				int blkColor = Block.BLOCK_COLOR_NONE;

				try {
					blkColor = Integer.parseInt(mapArray[j]);
				} catch (NumberFormatException e) {}

				setBlockColor(j, i, blkColor);

				if(getBlock(j, i) != null) {
					getBlock(j, i).elapsedFrames = -1;
				}
			}
		}
	}

	/**
	 * フィールドの幅を取得
	 * @return フィールドの幅
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * フィールドの高さを取得
	 * @return フィールドの高さ
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * フィールドより上の見えない部分の高さを取得
	 * @return フィールドより上の見えない部分の高さ
	 */
	public int getHiddenHeight() {
		return hidden_height;
	}

	/**
	 * 指定された座標の属性を取得
	 * @param x X座標
	 * @param y Y座標
	 * @return 座標の属性
	 */
	public int getCoordAttribute(int x, int y) {
		// 天井
		if((y < 0) && (ceiling)) return COORD_WALL;

		// 壁
		if((x < 0) || (x >= width) || (y >= height)) return COORD_WALL;

		// 通常
		if((y >= 0) && (y < height)) return COORD_NORMAL;

		// 見えない部分
		int y2 = (y * -1) - 1;
		if(y2 < hidden_height) return COORD_HIDDEN;

		// 置いたブロックが消える
		return COORD_VANISH;
	}

	/**
	 * 指定した座標にあるブロックを取得
	 * @param x X座標
	 * @param y Y座標
	 * @return 成功したら指定した座標にあるBlockオブジェクト、失敗したらnull
	 */
	public Block getBlock(int x, int y) {
		// フィールド内
		if(y >= 0) {
			try {
				return block_field[x][y];
			} catch(ArrayIndexOutOfBoundsException e) {
				return null;
			}
		}
		// フィールド外
		int y2 = (y * -1) - 1;

		try {
			return block_hidden[x][y2];
		} catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * 指定した座標にあるブロックを取得（失敗したら例外送出）
	 * @param x X座標
	 * @param y Y座標
	 * @return 指定した座標にあるBlockオブジェクト
	 * @throws ArrayIndexOutOfBoundsException 指定した座標が範囲外
	 */
	public Block getBlockE(int x, int y) throws ArrayIndexOutOfBoundsException {
		// フィールド内
		if(y >= 0) {
			try {
				return block_field[x][y];
			} catch(ArrayIndexOutOfBoundsException e) {
				throw e;
			}
		}
		// フィールド外
		try {
			int y2 = (y * -1) - 1;
			return block_hidden[x][y2];
		} catch(ArrayIndexOutOfBoundsException e) {
			throw e;
		}
	}

	/**
	 * 指定した座標にあるブロックを設定
	 * @param x X座標
	 * @param y Y座標
	 * @param blk ブロック
	 * @return 成功したらtrue、失敗したらfalse
	 */
	public boolean setBlock(int x, int y, Block blk) {
		// フィールド内
		if(y >= 0) {
			try {
				block_field[x][y] = blk;
			} catch(ArrayIndexOutOfBoundsException e) {
				return false;
			}
		}
		// フィールド外
		else {
			int y2 = (y * -1) - 1;

			try {
				block_hidden[x][y2] = blk;
			} catch(ArrayIndexOutOfBoundsException e) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 指定した座標にあるブロックを設定（失敗したら例外送出）
	 * @param x X座標
	 * @param y Y座標
	 * @param blk ブロック
	 * @throws ArrayIndexOutOfBoundsException 指定した座標が範囲外
	 */
	public void setBlockE(int x, int y, Block blk) throws ArrayIndexOutOfBoundsException {
		// フィールド内
		if(y >= 0) {
			try {
				block_field[x][y] = blk;
			} catch(ArrayIndexOutOfBoundsException e) {
				throw e;
			}
		}
		// フィールド外
		else {
			try {
				int y2 = (y * -1) - 1;
				block_hidden[x][y2] = blk;
			} catch(ArrayIndexOutOfBoundsException e) {
				throw e;
			}
		}
	}

	/**
	 * 指定した座標にあるブロックの色を取得
	 * @param x X座標
	 * @param y Y座標
	 * @return 指定した座標にあるブロックの色（失敗したらBLOCK_COLOR_INVALID）
	 */
	public int getBlockColor(int x, int y) {
		// フィールド内
		if(y >= 0) {
			try {
				return block_field[x][y].color;
			} catch(ArrayIndexOutOfBoundsException e) {
				return Block.BLOCK_COLOR_INVALID;
			}
		}

		// フィールド外
		int y2 = (y * -1) - 1;

		try {
			return block_hidden[x][y2].color;
		} catch(ArrayIndexOutOfBoundsException e) {
			return Block.BLOCK_COLOR_INVALID;
		}
	}

	/**
	 * 指定した座標にあるブロックの色を取得（失敗したら例外送出）
	 * @param x X座標
	 * @param y Y座標
	 * @return 指定した座標にあるブロックの色
	 * @throws ArrayIndexOutOfBoundsException 指定した座標が範囲外
	 */
	public int getBlockColorE(int x, int y) throws ArrayIndexOutOfBoundsException {
		// フィールド内
		if(y >= 0) {
			try {
				return block_field[x][y].color;
			} catch(ArrayIndexOutOfBoundsException e) {
				throw e;
			}
		}

		// フィールド外
		try {
			int y2 = (y * -1) - 1;
			return block_hidden[x][y2].color;
		} catch(ArrayIndexOutOfBoundsException e) {
			throw e;
		}
	}

	/**
	 * 指定した座標にあるブロックの色を変更
	 * @param x X座標
	 * @param y Y座標
	 * @param c 色
	 * @return 成功したらtrue、失敗したらfalse
	 */
	public boolean setBlockColor(int x, int y, int c) {
		// フィールド内
		if(y >= 0) {
			try {
				block_field[x][y].color = c;
			} catch(ArrayIndexOutOfBoundsException e) {
				return false;
			}
		}
		// フィールド外
		else {
			int y2 = (y * -1) - 1;

			try {
				block_hidden[x][y2].color = c;
			} catch(ArrayIndexOutOfBoundsException e) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 指定した座標にあるブロックの色を変更（失敗したら例外送出）
	 * @param x X座標
	 * @param y Y座標
	 * @param c 色
	 * @throws ArrayIndexOutOfBoundsException 指定した座標が範囲外
	 */
	public void setBlockColorE(int x, int y, int c) throws ArrayIndexOutOfBoundsException {
		// フィールド内
		if(y >= 0) {
			try {
				block_field[x][y].color = c;
			} catch(ArrayIndexOutOfBoundsException e) {
				throw e;
			}
		}
		// フィールド外
		else {
			try {
				int y2 = (y * -1) - 1;
				block_hidden[x][y2].color = c;
			} catch(ArrayIndexOutOfBoundsException e) {
				throw e;
			}
		}
	}

	/**
	 * ライン消去フラグを取得
	 * @param y Y座標
	 * @return 消える列ならtrue、そうでないなら（もしくは座標が範囲外なら）false
	 */
	public boolean getLineFlag(int y) {
		// フィールド内
		if(y >= 0) {
			try {
				return lineflag_field[y];
			} catch(ArrayIndexOutOfBoundsException e) {
				return false;
			}
		}

		// フィールド外
		int y2 = (y * -1) - 1;

		try {
			return lineflag_hidden[y2];
		} catch(ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * 指定した座標にあるブロックが空白かどうか判定
	 * @param x X座標
	 * @param y Y座標
	 * @return 指定した座標にあるブロックが空白ならtrue（指定した座標が範囲外の場合もtrue）
	 */
	public boolean getBlockEmpty(int x, int y) {
		// フィールド内
		if(y >= 0) {
			try {
				return block_field[x][y].isEmpty();
			} catch(ArrayIndexOutOfBoundsException e) {
				return true;
			}
		}

		// フィールド外
		int y2 = (y * -1) - 1;

		try {
			return block_hidden[x][y2].isEmpty();
		} catch(ArrayIndexOutOfBoundsException e) {
			return true;
		}
	}

	/**
	 * 指定した座標にあるブロックが空白かどうか判定（指定した座標が範囲外の場合はfalse）
	 * @param x X座標
	 * @param y Y座標
	 * @return 指定した座標にあるブロックが空白ならtrue（指定した座標が範囲外の場合はfalse）
	 */
	public boolean getBlockEmptyF(int x, int y) {
		// フィールド内
		if(y >= 0) {
			try {
				return block_field[x][y].isEmpty();
			} catch(ArrayIndexOutOfBoundsException e) {
				return false;
			}
		}

		// フィールド外
		int y2 = (y * -1) - 1;

		try {
			return block_hidden[x][y2].isEmpty();
		} catch(ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * 指定した座標にあるブロックが空白かどうか判定（失敗したら例外送出）
	 * @param x X座標
	 * @param y Y座標
	 * @return 指定した座標にあるブロックが空白ならtrue
	 * @throws ArrayIndexOutOfBoundsException 指定した座標が範囲外
	 */
	public boolean getBlockEmptyE(int x, int y) throws ArrayIndexOutOfBoundsException {
		// フィールド内
		if(y >= 0) {
			try {
				return block_field[x][y].isEmpty();
			} catch(ArrayIndexOutOfBoundsException e) {
				throw e;
			}
		}

		// フィールド外
		try {
			int y2 = (y * -1) - 1;
			return block_hidden[x][y2].isEmpty();
		} catch(ArrayIndexOutOfBoundsException e) {
			throw e;
		}
	}

	/**
	 * ライン消去フラグを取得（失敗したら例外送出）
	 * @param y Y座標
	 * @return 消える列ならtrue、そうでないならfalse
	 * @throws ArrayIndexOutOfBoundsException 指定した座標が範囲外
	 */
	public boolean getLineFlagE(int y) throws ArrayIndexOutOfBoundsException {
		// フィールド内
		if(y >= 0) {
			try {
				return lineflag_field[y];
			} catch(ArrayIndexOutOfBoundsException e) {
				throw e;
			}
		}

		// フィールド外
		try {
			int y2 = (y * -1) - 1;
			return lineflag_hidden[y2];
		} catch(ArrayIndexOutOfBoundsException e) {
			throw e;
		}
	}

	/**
	 * ライン消去フラグを設定
	 * @param y Y座標
	 * @param flag 設定するライン消去フラグ
	 * @return 成功したらtrue、失敗したらfalse
	 */
	public boolean setLineFlag(int y, boolean flag) {
		// フィールド内
		if(y >= 0) {
			try {
				lineflag_field[y] = flag;
			} catch(ArrayIndexOutOfBoundsException e) {
				return false;
			}
		}
		// フィールド外
		else {
			int y2 = (y * -1) - 1;

			try {
				lineflag_hidden[y2] = flag;
			} catch(ArrayIndexOutOfBoundsException e) {
				return false;
			}
		}

		return true;
	}

	/**
	 * ライン消去フラグを設定（失敗したら例外送出）
	 * @param y Y座標
	 * @param flag 設定するライン消去フラグ
	 * @throws ArrayIndexOutOfBoundsException 指定した座標が範囲外
	 */
	public void setLineFlagE(int y, boolean flag) throws ArrayIndexOutOfBoundsException {
		// フィールド内
		if(y >= 0) {
			try {
				lineflag_field[y] = flag;
			} catch(ArrayIndexOutOfBoundsException e) {
				throw e;
			}
		}
		// フィールド外
		else {
			try {
				int y2 = (y * -1) - 1;
				lineflag_hidden[y2] = flag;
			} catch(ArrayIndexOutOfBoundsException e) {
				throw e;
			}
		}
	}

	/**
	 * ライン消去チェック
	 * @return 消えるライン数
	 */
	public int checkLine() {
		int lines = 0;

		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			boolean flag = true;

			for(int j = 0; j < width; j++) {
				if((getBlockEmpty(j, i) == true) || (getBlock(j, i).getAttribute(Block.BLOCK_ATTRIBUTE_WALL) == true)) {
					flag = false;
					break;
				}
			}

			setLineFlag(i, flag);

			if(flag) {
				lines++;

				for(int j = 0; j < width; j++) {
					getBlock(j, i).setAttribute(Block.BLOCK_ATTRIBUTE_ERASE, true);
				}
			}
		}

		return lines;
	}

	/**
	 * ライン消去チェック（消去フラグの設定とかはしない）
	 * @return 消えるライン数
	 */
	public int checkLineNoFlag() {
		int lines = 0;

		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			boolean flag = true;

			for(int j = 0; j < width; j++) {
				if((getBlockEmpty(j, i) == true) || (getBlock(j, i).getAttribute(Block.BLOCK_ATTRIBUTE_WALL) == true)) {
					flag = false;
					break;
				}
			}

			if(flag) {
				lines++;
			}
		}

		return lines;
	}

	/**
	 * ラインを消す
	 * @return 消えたライン数
	 */
	public int clearLine() {
		int lines = 0;

		// フィールド内
		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			if(getLineFlag(i)) {
				lines++;

				for(int j = 0; j < width; j++) {
					setBlockColor(j, i, Block.BLOCK_COLOR_NONE);
				}
			}
		}

		// 消えたラインの上下のブロックの結合を解除
		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			if(getLineFlag(i - 1)) {
				for(int j = 0; j < width; j++) {
					Block blk = getBlock(j, i);

					if(blk != null && blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, false);
						setBlockLinkBroken(j, i);
					}
				}
			}
			if(getLineFlag(i + 1)) {
				for(int j = 0; j < width; j++) {
					Block blk = getBlock(j, i);

					if(blk != null && blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, false);
						setBlockLinkBroken(j, i);
					}
				}
			}
		}

		return lines;
	}

	/**
	 * 上にあったブロックをすべて下まで下ろす
	 * @return 消えていたライン数
	 */
	public int downFloatingBlocks() {
		int lines = 0;
		int y = getHeightWithoutHurryupFloor() - 1;

		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			if(getLineFlag(y)) {
				lines++;

				// ブロックを1段上からコピー
				for(int k = y; k > (hidden_height * -1); k--) {
					for(int l = 0; l < width; l++) {
						Block blk = getBlock(l, k - 1);
						if(blk == null) blk = new Block();
						setBlock(l, k, blk);
						setLineFlag(k, getLineFlag(k - 1));
					}
				}

				// 一番上を空白にする
				for(int l = 0; l < width; l++) {
					Block blk = new Block();
					setBlock(l, (hidden_height * -1), blk);
				}
				setLineFlag((hidden_height * -1), false);
			} else {
				y--;
			}
		}

		return lines;
	}

	/**
	 * 上にあったブロックを1段だけ下ろす
	 */
	public void downFloatingBlocksSingleLine() {
		int y = getHeightWithoutHurryupFloor() - 1;

		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			if(getLineFlag(y)) {
				// ブロックを1段上からコピー
				for(int k = y; k > (hidden_height * -1); k--) {
					for(int l = 0; l < width; l++) {
						Block blk = getBlock(l, k - 1);
						if(blk == null) blk = new Block();
						setBlock(l, k, blk);
						setLineFlag(k, getLineFlag(k - 1));
					}
				}

				// 一番上を空白にする
				for(int l = 0; l < width; l++) {
					Block blk = new Block();
					setBlock(l, (hidden_height * -1), blk);
				}
				setLineFlag((hidden_height * -1), false);
				return;
			} else {
				y--;
			}
		}
	}

	/**
	 * 消えるライン数を数える
	 * @return ライン数
	 */
	public int getLines() {
		int lines = 0;

		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			if(getLineFlag(i)) lines++;
		}

		return lines;
	}

	/**
	 * 全消しだったらtrue
	 * @return 全消しだったらtrue
	 */
	public boolean isEmpty() {
		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			if(getLineFlag(i) == false) {
				for(int j = 0; j < width; j++) {
					if(getBlockEmpty(j, i) == false) {
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * T-Spinになる地形だったらtrue
	 * @param x X座標
	 * @param y Y座標
	 * @param big ビッグかどうか
	 * @return T-Spinになる地形だったらtrue
	 */
	public boolean isTSpinSpot(int x, int y, boolean big) {
		// 判定用相対座標を設定
		int[] tx = new int[4];
		int[] ty = new int[4];

		if(big == true) {
			tx[0] = 1;
			ty[0] = 1;
			tx[1] = 4;
			ty[1] = 1;
			tx[2] = 1;
			ty[2] = 4;
			tx[3] = 4;
			ty[3] = 4;
		} else {
			tx[0] = 0;
			ty[0] = 0;
			tx[1] = 2;
			ty[1] = 0;
			tx[2] = 0;
			ty[2] = 2;
			tx[3] = 2;
			ty[3] = 2;
		}

		// 判定
		int count = 0;

		for(int i = 0; i < tx.length; i++) {
			if(getBlockColor(x + tx[i], y + ty[i]) != Block.BLOCK_COLOR_NONE) count++;
		}

		if(count >= 3) return true;

		return false;
	}

	/**
	 * T-Spinできそうな穴だったらtrue
	 * @param x X座標
	 * @param y Y座標
	 * @param big ビッグかどうか
	 * @return T-Spinできそうな穴だったらtrue
	 */
	public boolean isTSlot(int x, int y, boolean big) {
		// 中央が埋まってると無理
		if(big == true) {
			if(!getBlockEmptyF(x + 2, y + 2)) {
				return false;
			}
		} else {
			//□□□※※※□□□□
			//□□□★※□□□□□
			//□□□※※※□□□□
			//□□□○※○□□□□

			if(!getBlockEmptyF(x + 1, y + 0)) return false;
			if(!getBlockEmptyF(x + 1, y + 1)) return false;
			if(!getBlockEmptyF(x + 1, y + 2)) return false;

			if(!getBlockEmptyF(x + 0, y + 1)) return false;
			if(!getBlockEmptyF(x + 2, y + 1)) return false;

			if(!getBlockEmptyF(x + 1, y - 1)) return false;
		}

		// 判定用相対座標を設定
		int[] tx = new int[4];
		int[] ty = new int[4];

		if(big == true) {
			tx[0] = 1;
			ty[0] = 1;
			tx[1] = 4;
			ty[1] = 1;
			tx[2] = 1;
			ty[2] = 4;
			tx[3] = 4;
			ty[3] = 4;
		} else {
			tx[0] = 0;
			ty[0] = 0;
			tx[1] = 2;
			ty[1] = 0;
			tx[2] = 0;
			ty[2] = 2;
			tx[3] = 2;
			ty[3] = 2;
		}

		// 判定
		int count = 0;

		for(int i = 0; i < tx.length; i++) {
			if(getBlockColor(x + tx[i], y + ty[i]) != Block.BLOCK_COLOR_NONE) count++;
		}

		if(count == 3) return true;

		return false;
	}

	/**
	 * T-Spinできそうな穴が何個あるか調べる
	 * @param big ビッグだったらtrue
	 * @return T-Spinできそうな穴の数
	 */
	public int getHowManyTSlot(boolean big) {
		int result = 0;

		for(int j = 0; j < width; j++) {
			for(int i = 0; i < getHeightWithoutHurryupFloor() - 2; i++) {
				if(getLineFlag(i) == false) {
					if(isTSlot(j, i, big)) {
						result++;
					}
				}
			}
		}

		return result;
	}


	public int getHowManyBlocksCovered() {
		int blocksCovered = 0;


		for(int j = 0; j < width; j++) {

            int highestBlockY=getHighestBlockY(j);
			for(int i = highestBlockY; i < getHeightWithoutHurryupFloor(); i++) {
				if(getLineFlag(i) == false) {

					if( getBlockEmpty(j, i)) {
					blocksCovered++;
					}

				}
			}
		}

		return blocksCovered;
	}
	/**
	 * T-Spinで消えるライン数を返す
	 * @param x X座標
	 * @param y Y座標
	 * @param big ビッグかどうか(未対応)
	 * @return T-Spinで消えるライン数(T-Spinじゃない場合などは0)
	 */
	public int getTSlotLineClear(int x, int y, boolean big) {
		if(!isTSlot(x, y, big)) return 0;

		boolean[] lineflag = new boolean[2];
		lineflag[0] = lineflag[1] = true;

		for(int j = 0; j < width; j++) {
			for(int i = 0; i < 2; i++) {
				//■■■★※■■■■■
				//□□□※※※□□□□
				//□□□○※○□□□□
				if((j < x) || (j >= x + 3)) {
					if(getBlockEmptyF(j, y + 1 + i) == true) {
						lineflag[i] = false;
					}
				}
			}
		}

		int lines = 0;
		for(int i = 0; i < lineflag.length; i++) {
			if(lineflag[i]) lines++;
		}

		return lines;
	}

	/**
	 * T-Spinで消えるライン数を返す(フィールド全体)
	 * @param big ビッグかどうか(未対応)
	 * @return T-Spinで消えるライン数(T-Spinじゃない場合などは0)
	 */
	public int getTSlotLineClearAll(boolean big) {
		int result = 0;

		for(int j = 0; j < width; j++) {
			for(int i = 0; i < getHeightWithoutHurryupFloor() - 2; i++) {
				if(getLineFlag(i) == false) {
					result += getTSlotLineClear(j, i, big);
				}
			}
		}

		return result;
	}

	/**
	 * T-Spinで消えるライン数を返す(フィールド全体)
	 * @param big ビッグかどうか(未対応)
	 * @param minimum 最低ライン数(2にするとT-Spin Doubleにだけ反応)
	 * @return T-Spinで消えるライン数(T-Spinじゃない場合やminimumに満たないラインなどは0)
	 */
	public int getTSlotLineClearAll(boolean big, int minimum) {
		int result = 0;

		for(int j = 0; j < width; j++) {
			for(int i = 0; i < getHeightWithoutHurryupFloor() - 2; i++) {
				if(getLineFlag(i) == false) {
					int temp = getTSlotLineClear(j, i, big);

					if(temp >= minimum)
						result += temp;
				}
			}
		}

		return result;
	}

	/**
	 * フィールド内に何個のブロックがあるか調べる
	 * @return フィールド内にあるブロックの数
	 */
	public int getHowManyBlocks() {
		int count = 0;

		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			if(getLineFlag(i) == false) {
				for(int j = 0; j < width; j++) {
					if(!getBlockEmpty(j, i)) {
						count++;
					}
				}
			}
		}

		return count;
	}

	/**
	 * 左から何個のブロックが並んでいるか調べる
	 * @return 左から並んでいるブロックの総数
	 */
	public int getHowManyBlocksFromLeft() {
		int count = 0;

		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			if(getLineFlag(i) == false) {
				for(int j = 0; j < width; j++) {
					if(!getBlockEmpty(j, i)) {
						count++;
					} else {
						break;
					}
				}
			}
		}

		return count;
	}

	/**
	 * 右から何個のブロックが並んでいるか調べる
	 * @return 右から並んでいるブロックの総数
	 */
	public int getHowManyBlocksFromRight() {
		int count = 0;

		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			if(getLineFlag(i) == false) {
				for(int j = width - 1; j > 0; j--) {
					if(!getBlockEmpty(j, i)) {
						count++;
					} else {
						break;
					}
				}
			}
		}

		return count;
	}


	/**
	 * 一番上にあるブロックのY座標を取得
	 * @return 一番上にあるブロックのY座標
	 */
	public int getHighestBlockY() {
		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			if(getLineFlag(i) == false) {
				for(int j = 0; j < width; j++) {
					if(!getBlockEmpty(j, i)) return i;
				}
			}
		}

		return height;
	}

	/**
	 * 一番上にあるブロックのY座標を取得（X座標を指定できるバージョン）
	 * @param x X座標
	 * @return 一番上にあるブロックのY座標
	 */
	public int getHighestBlockY(int x) {
		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			if(getLineFlag(i) == false) {
				if(!getBlockEmpty(x, i)) return i;
			}
		}

		return height;
	}

	/**
	 * 邪魔ブロックが最初に現れるY座標を取得
	 * @return 邪魔ブロックが最初に現れるY座標
	 */
	public int getHighestGarbageBlockY() {
		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			if(getLineFlag(i) == false) {
				for(int j = 0; j < width; j++) {
					if(!getBlockEmpty(j, i) && getBlock(j, i).getAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE))
						return i;
				}
			}
		}

		return height;
	}

	/**
	 * 指定した座標の下に隙間があるか調べる
	 * @param x X座標
	 * @param y Y座標
	 * @return 指定した座標の下に隙間があればtrue
	 */
	public boolean isHoleBelow(int x, int y) {
		if(!getBlockEmpty(x, y) && getBlockEmpty(x, y + 1)) return true;
		return false;
	}

	/**
	 * フィールド内の隙間の数を調べる
	 * @return フィールド内の隙間の数
	 */
	public int getHowManyHoles() {
		int hole = 0;
		boolean samehole = false;

		for(int j = 0; j < width; j++) {
			samehole = false;

			for(int i = getHighestBlockY(); i < getHeightWithoutHurryupFloor(); i++) {
				if(getLineFlag(i) == false) {
					if(isHoleBelow(j, i)) {
						samehole = true;
					} else if(samehole && getBlockEmpty(j, i)) {
						hole++;
					} else {
						samehole = false;
					}
				}
			}
		}

		return hole;
	}

	/**
	 * 隙間の上に何個ブロックが積み重なっているか調べる
	 * @return 積み重なっているブロックの数
	 */
	public int getHowManyLidAbobeHoles() {
		int blocks = 0;

		for(int j = 0; j < width; j++) {
			int count = 0;

			for(int i = getHighestBlockY(); i < getHeightWithoutHurryupFloor() - 1; i++) {
				if(getLineFlag(i) == false) {
					if(isHoleBelow(j, i)) {
						count++;
						blocks += count;
						count = 0;
					} else if(!getBlockEmpty(j, i)) {
						count++;
					}
				}
			}
		}

		return blocks;
	}

	/**
	 * 全ての谷（■　■になっている地形）の深さを合計したものを返す（谷が多くて深いほど戻り値も大きくなる）
	 * @return 全ての谷の深さを合計したもの
	 */
	public int getTotalValleyDepth() {
		int depth = 0;

		for(int j = 0; j < width; j++) {
			int d = getValleyDepth(j);
			if(d >= 2) depth += d;
		}

		return depth;
	}

	/**
	 * I型が必要な谷（深さ3以上）の数を返す
	 * @return I型が必要な谷の数
	 */
	public int getTotalValleyNeedIPiece() {
		int count = 0;

		for(int j = 0; j < width; j++) {
			if(getValleyDepth(j) >= 3) count++;
		}

		return count;
	}

	/**
	 * 谷（■　■になっている地形）の深さを調べる
	 * @param x 調べるX座標
	 * @return 谷の深さ（無かったら0）
	 */
	public int getValleyDepth(int x) {
		int depth = 0;

		int highest = getHighestBlockY(x - 1);
		highest = Math.min(highest, getHighestBlockY(x));
		highest = Math.min(highest, getHighestBlockY(x + 1));

		for(int i = highest; i < getHeightWithoutHurryupFloor(); i++) {
			if(getLineFlag(i) == false) {
				if( (!getBlockEmptyF(x - 1, i) || (x <= 0)) && getBlockEmptyF(x, i) && (!getBlockEmptyF(x + 1, i) || (x >= width - 1)) )
					depth++;
			}
		}

		return depth;
	}

	/**
	 * フィールド全体を上にずらす
	 * @param lines ずらす段数
	 */
	public void pushUp(int lines) {
		for(int k = 0; k < lines; k++) {
			for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor() - 1; i++) {
				// ブロックを1段下からコピー
				for(int j = 0; j < width; j++) {
					Block blk = getBlock(j, i + 1);
					if(blk == null) blk = new Block();
					setBlock(j, i, blk);
					setLineFlag(i, getLineFlag(i + 1));
				}
			}

			// 一番下を空白にする
			for(int j = 0; j < width; j++) {
				int y = getHeightWithoutHurryupFloor() - 1;
				setBlock(j, y, new Block());
				setLineFlag(y, false);
			}
		}
	}

	/**
	 * フィールド全体を1段上にずらす
	 */
	public void pushUp() {
		pushUp(1);
	}

	/**
	 * フィールド全体を下にずらす
	 * @param lines ずらす段数
	 */
	public void pushDown(int lines) {
		for(int k = 0; k < lines; k++) {
			for(int i = getHeightWithoutHurryupFloor() - 1; i > (hidden_height * -1); i--) {
				// ブロックを1段上からコピー
				for(int j = 0; j < width; j++) {
					Block blk = getBlock(j, i - 1);
					if(blk == null) blk = new Block();
					setBlock(j, i, blk);
					setLineFlag(i, getLineFlag(i + 1));
				}
			}

			// 一番上を空白にする
			for(int j = 0; j < width; j++) {
				setBlock(j, (hidden_height * -1), new Block());
				setLineFlag((hidden_height * -1), false);
			}
		}
	}

	/**
	 * フィールド全体を1段下にずらす
	 */
	public void pushDown() {
		pushDown(1);
	}

	/**
	 * 穴が1箇所だけ開いた邪魔ブロックを一番下に追加
	 * @param hole 穴の位置（-1なら穴なし）
	 * @param color 邪魔ブロックの色
	 * @param skin 邪魔ブロックの絵柄
	 * @param attribute 邪魔ブロックの属性
	 * @param lines 追加する邪魔ブロックのライン数
	 */
	public void addSingleHoleGarbage(int hole, int color, int skin, int attribute, int lines) {
		for(int k = 0; k < lines; k++) {
			pushUp(1);

			for(int j = 0; j < width; j++) {
				if(j != hole) {
					Block blk = new Block();
					blk.color = color;
					blk.skin = skin;
					blk.attribute = attribute;
					setBlock(j, getHeightWithoutHurryupFloor() - 1, blk);
				}
			}
		}
	}

	/**
	 * 一番下のラインの形をコピーした邪魔ブロックを一番下に追加
	 * @param color 邪魔ブロックの色
	 * @param skin 邪魔ブロックの絵柄
	 * @param attribute 邪魔ブロックの属性
	 * @param lines 追加する邪魔ブロックのライン数
	 */
	public void addBottomCopyGarbage(int color, int skin, int attribute, int lines) {
		for(int k = 0; k < lines; k++) {
			pushUp(1);

			for(int j = 0; j < width; j++) {
				boolean empty = getBlockEmpty(j, height - 2);

				if(!empty) {
					Block blk = new Block();
					blk.color = color;
					blk.skin = skin;
					blk.attribute = attribute;
					setBlock(j, getHeightWithoutHurryupFloor() - 1, blk);
				}
			}
		}
	}

	/**
	 * 裏段位を取得
	 * (from NullpoMino Unofficial Expansion build 091309)
	 * @author Zircean
	 * @return 裏段位
	 */
	public int getSecretGrade() {
		int holeLoc;
		int rows = 0;
		boolean rowCheck;

		for(int i = height - 1; i > 0; i--) {
			holeLoc = -Math.abs(i - (height / 2)) + (height / 2) - 1;
			if(getBlockEmpty(holeLoc, i) && !getBlockEmpty(holeLoc, i - 1)) {
				rowCheck = true;
				for(int j = 0; j < width; j++) {
					if(j != holeLoc && getBlockEmpty(j, i)) {
						rowCheck = false;
						break;
					}
				}
				if(rowCheck) {
					rows++;
				} else {
					break;
				}
			} else {
				break;
			}
		}

		return rows;
	}

	/**
	 * 全てのブロックの属性を変更
	 * @param attr 変更したい属性
	 * @param status 変更後の状態
	 */
	public void setAllAttribute(int attr, boolean status) {
		for(int i = (hidden_height * -1); i < height; i++) {
			for(int j = 0; j < width; j++) {
				Block blk = getBlock(j, i);

				if(blk != null) {
					blk.setAttribute(attr, status);
				}
			}
		}
	}

	/**
	 * 全てのブロックの絵柄を変更
	 * @param skin 絵柄
	 */
	public void setAllSkin(int skin) {
		for(int i = (hidden_height * -1); i < height; i++) {
			for(int j = 0; j < width; j++) {
				Block blk = getBlock(j, i);

				if(blk != null) {
					blk.skin = skin;
				}
			}
		}
	}

	/**
	 * 宝石ブロックの数を取得
	 * @return 宝石ブロックの数
	 */
	public int getHowManyGems() {
		int gems = 0;
		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			for(int j = 0; j < width; j++) {
				Block blk = getBlock(j, i);

				if((blk != null) && (blk.isGemBlock())) {
					gems++;
				}
			}
		}
		return gems;
	}

	/**
	 * 宝石ブロックがいくつ消えるか取得
	 * @return 消える宝石ブロックの数
	 */
	public int getHowManyGemClears() {
		int gems = 0;

		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			if(getLineFlag(i)) {
				for(int j = 0; j < width; j++) {
					Block blk = getBlock(j, i);

					if((blk != null) && (blk.isGemBlock())) {
						gems++;
					}
				}
			}
		}

		return gems;
	}

	/**
	 * Checks for 4x4 square formations and converts blocks to square blocks if needed.
	 */
	public void checkForSquares() {
		// Check for gold squares
		for (int i = (hidden_height * -1); i < (getHeightWithoutHurryupFloor() - 3); i++) {
			for (int j = 0; j < (width - 3); j++) {
				// rootBlk is the upper-left square
				Block rootBlk = getBlock(j, i);
				boolean squareCheck = false;

				/*
				 * id is the color of the top-left square: if it is a monosquare, every block in the
				 * 4x4 area will have this color.
				 */
				int id = Block.BLOCK_COLOR_NONE;
				if (!(rootBlk == null || rootBlk.isEmpty())) {
					id = rootBlk.color;
				}

				// This can't be a square if rootBlk doesn't exist or is part of another square.
				if (!(rootBlk == null || rootBlk.isEmpty() || rootBlk.isGoldSquareBlock() || rootBlk.isSilverSquareBlock())) {
					// A square is innocent until proven guilty.
					squareCheck = true;
					for (int k = 0; k < 4; k++) {
						for (int l = 0; l < 4; l++) {
							// blk is the current block
							Block blk = getBlock(j+l, i+k);
							/*
							 * Reasons why the entire area would not be a monosquare: this block does not exist,
							 * it is part of another square, it has been broken by line clears, is a garbage
							 * block, is not the same color as id, or has connections outside the area.
							 */
							if (blk == null || blk.isEmpty() || blk.isGoldSquareBlock() || blk.isSilverSquareBlock() ||
									blk.getAttribute(Block.BLOCK_ATTRIBUTE_BROKEN) ||
									blk.getAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE) || blk.color != id ||
									(l == 0 && blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT)) ||
									(l == 3 && blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT)) ||
									(k == 0 && blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP)) ||
									(k == 3 && blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))){
								squareCheck = false;
								break;
							}
						}
						if (!squareCheck) {
							break;
						}
					}
				}
				// We found a square! Set all the blocks equal to gold blocks.
				if (squareCheck) {
					int[] squareX = new int[] {0, 1, 1, 2};
					int[] squareY = new int[] {0, 3, 3, 6};
					for (int k = 0; k < 4; k++) {
						for (int l = 0; l < 4; l++) {
							Block blk = getBlock(j+l, i+k);
							blk.color = Block.BLOCK_COLOR_SQUARE_GOLD_1 + squareX[l] + squareY[k];
							// For stylistic concerns, we attach all blocks in the square together.
							if (k > 0) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, true);
							}
							if (k < 3) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, true);
							}
							if (l > 0) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, true);
							}
							if (l < 3) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, true);
							}
						}
					}
				}
			}
		}
		// Check for silver squares
		for (int i = (hidden_height * -1); i < (getHeightWithoutHurryupFloor() - 3); i++) {
			for (int j = 0; j < (width - 3); j++) {
				Block rootBlk = getBlock(j, i);
				boolean squareCheck = false;
				// We don't have to check colors because this loop checks for multisquares.
				if (!(rootBlk == null || rootBlk.isEmpty() || rootBlk.isGoldSquareBlock() || rootBlk.isSilverSquareBlock())) {
					// A square is innocent until proven guilty
					squareCheck = true;
					for (int k = 0; k < 4; k++) {
						for (int l = 0; l < 4; l++) {
							Block blk = getBlock(j+l, i+k);
							// See above, but without the color checking.
							if (blk == null || blk.isEmpty() || blk.isGoldSquareBlock() || blk.isSilverSquareBlock() ||
									blk.getAttribute(Block.BLOCK_ATTRIBUTE_BROKEN) ||
									blk.getAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE) ||
									(l == 0 && blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT)) ||
									(l == 3 && blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT)) ||
									(k == 0 && blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP)) ||
									(k == 3 && blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))){
								squareCheck = false;
								break;
							}
						}
						if (!squareCheck) {
							break;
						}
					}
				}
				// We found a square! Set all the blocks equal to silver blocks.
				if (squareCheck) {
					int[] squareX = new int[] {0, 1, 1, 2};
					int[] squareY = new int[] {0, 3, 3, 6};
					for (int k = 0; k < 4; k++) {
						for (int l = 0; l < 4; l++) {
							Block blk = getBlock(j+l, i+k);
							blk.color = Block.BLOCK_COLOR_SQUARE_SILVER_1 + squareX[l] + squareY[k];
							if (k > 0) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, true);
							}
							if (k < 3) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, true);
							}
							if (l > 0) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, true);
							}
							if (l < 3) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, true);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Checks the lines that are currently being cleared to see how many strips of squares are present in them.
	 * @return +1 for every 1x4 strip of silver, +2 for every strip of gold
	 */
	public int getHowManySquareClears() {
		int squares = 0;
		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			// Check the lines we are clearing.
			if (getLineFlag(i)) {
				for(int j = 0; j < width; j++) {
					Block blk = getBlock(j, i);

					// Silver blocks are worth 1, gold are worth 2, but not if they are garbage (avalanche)
					if (blk != null && !blk.getAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE)) {
						if (blk.isGoldSquareBlock()) {
							squares += 2;
						} else if (blk.isSilverSquareBlock()) {
							squares++;
						}
					}
				}
			}
		}
		// We have to divide the amount by 4 because it's based on 1x4 strips, not single blocks.
		return squares/4;
	}

	/**
	 * Converts all pieces below the cleared lines to single blocks and breaks all of their connections.
	 * Then, causes them to fall down under their own gravity as in cascade.
	 * Currently, it is rather buggy...
	 */
	@Deprecated
	public void doAvalanche() {
		// This sets the highest line that will be affected by the avalanche.
		int topLine = hidden_height * -1;
		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			if (getLineFlag(i)) {
				topLine = i + 1;
			}
		}

		for (int i = (getHeightWithoutHurryupFloor() - 1); i >= topLine; i--) {
			// There can be lines cleared underneath, in case of a spin hurdle or such.
			if (!getLineFlag(i)) {
				for (int j = 0; j < width; j++) {
					Block blk = getBlock(j, i);

					// Change each affected block to broken and garbage, and break connections.
					blk.setAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE, true);
					blk.setAttribute(Block.BLOCK_ATTRIBUTE_BROKEN, true);
					blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, false);
					blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, false);
					blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, false);
					blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, false);
					// Force all blocks down if there is room
					int newY = i;
					Block downBlock;
					do {
						newY--;
						downBlock = getBlock(j, newY);
					} while (downBlock == null || downBlock.isEmpty());
					if (newY != i) {
						setBlock(j, newY, blk);
						setBlock(j, i, new Block());
					}
				}
			}
		}
	}

	/**
	 * Clear line colors of sufficient size.
	 * @param size Minimum length of line for a clear
	 * @param diagonals <code>true</code> to check diagonals, <code>false</code> to check only vertical and horizontal
	 * @return Total number of blocks that would be cleared.
	 */
	public int clearLineColor (int size, boolean diagonals)
	{
		int total = checkLineColor(size, true, diagonals);
		if (total > 0)
			for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++)
				for(int j = 0; j < width; j++)
				{
					Block b = getBlock(j, i);
					if (b == null)
						continue;
					if (b.getAttribute(Block.BLOCK_ATTRIBUTE_ERASE))
						setBlockColor(j, i, Block.BLOCK_COLOR_NONE);
				}
		return total;
	}
	/**
	 * Check for line color clears of sufficient size.
	 * @param size Minimum length of line for a clear
	 * @param flag <code>true</code> to set BLOCK_ATTRIBUTE_ERASE to true on blocks to be cleared.
	 * @param diagonals <code>true</code> to check diagonals, <code>false</code> to check only vertical and horizontal
	 * @return Total number of blocks that would be cleared.
	 */
	public int checkLineColor (int size, boolean flag, boolean diagonals)
	{
		if (size < 1)
			return 0;
		int total = 0;
		int maxHeight = getHeightWithoutHurryupFloor();
		int x, y, count, blockColor, lineColor;
		//Check all vertical lines
		for(int i = (hidden_height * -1); i < maxHeight+1-size; i++) {
			for(int j = 0; j < width; j++) {
				x = j;
				y = i;
				count = 0;
				blockColor = getBlockColor(x, y);
				if (blockColor == Block.BLOCK_COLOR_NONE || blockColor == Block.BLOCK_COLOR_INVALID)
					continue;
				lineColor = blockColor;
				while (lineColor == blockColor)
				{
					count++;
					y++;
					blockColor = getBlockColor(x, y);
				}
				if (count < size)
					continue;
				total += count;
				if (!flag)
					continue;
				x = j;
				y = i;
				blockColor = lineColor;
				while (lineColor == blockColor)
				{
					getBlock(x, y).setAttribute(Block.BLOCK_ATTRIBUTE_ERASE, true);
					y++;
					blockColor = getBlockColor(x, y);
				}
			}
		}
		//Check all horizontal lines
		for(int i = (hidden_height * -1); i < maxHeight; i++) {
			for(int j = 0; j < width+1-size; j++) {
				x = j;
				y = i;
				count = 0;
				blockColor = getBlockColor(x, y);
				if (blockColor == Block.BLOCK_COLOR_NONE || blockColor == Block.BLOCK_COLOR_INVALID)
					continue;
				lineColor = blockColor;
				while (lineColor == blockColor)
				{
					x++;
					blockColor = getBlockColor(x, y);
					count++;
				}
				if (count < size)
					continue;
				total += count;
				if (!flag)
					continue;
				x = j;
				y = i;
				blockColor = lineColor;
				while (lineColor == blockColor)
				{
					getBlock(x, y).setAttribute(Block.BLOCK_ATTRIBUTE_ERASE, true);
					x++;
					blockColor = getBlockColor(x, y);
				}
			}
		}
		
		if (!diagonals)
			return total;
		//Check all diagonal lines
		for(int i = (hidden_height * -1); i < maxHeight+1-size; i++) {
			for(int j = 0; j < width+1-size; j++) {
				x = j;
				y = i;
				count = 0;
				blockColor = getBlockColor(x, y);
				if (blockColor == Block.BLOCK_COLOR_NONE || blockColor == Block.BLOCK_COLOR_INVALID)
					continue;
				lineColor = blockColor;
				while (lineColor == blockColor)
				{
					x++;
					y++;
					blockColor = getBlockColor(x, y);
					count++;
				}
				if (count < size)
					continue;
				total += count;
				if (!flag)
					continue;
				x = j;
				y = i;
				blockColor = lineColor;
				while (lineColor == blockColor)
				{
					getBlock(x, y).setAttribute(Block.BLOCK_ATTRIBUTE_ERASE, true);
					x++;
					y++;
					blockColor = getBlockColor(x, y);
				}
			}
		}
		return total;
	}
	/**
	 * Performs all color clears of sufficient size.
	 * @param size Minimum size of cluster for a clear
	 * @param garbageClear <code>true</code> to clear garbage blocks adjacent to cleared clusters
	 * @return Total number of blocks cleared.
	 */
	public int clearColor (int size, boolean garbageClear)
	{
		Field temp = new Field(this);
		int total = 0;

		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			for(int j = 0; j < width; j++) {
				int clear = temp.clearColor(j, i, false, garbageClear);
				if (clear >= size)
				{
					total += clear;
					clearColor(j, i, true, garbageClear);
				}
			}
		}
		return total;
	}
	/**
	 * Clears the block at the given position as well as all adjacent blocks of
	 * the same color, and any garbage blocks adjacent to the group if garbageClear is true.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param flag <code>true</code> to set BLOCK_ATTRIBUTE_ERASE to true on cleared blocks.
	 * @param garbageClear <code>true</code> to clear garbage blocks adjacent to cleared clusters
	 * @return The number of blocks cleared.
	 */
	public int clearColor (int x, int y, boolean flag, boolean garbageClear)
	{
		int blockColor = getBlockColor(x, y);
		if (blockColor == Block.BLOCK_COLOR_NONE || blockColor == Block.BLOCK_COLOR_INVALID)
			return 0;
		else
			return clearColor(x, y, blockColor, flag, garbageClear);
	}
	/**
	 * Note: This method is private because calling it with a targetColor parameter
	 *       of BLOCK_COLOR_NONE or BLOCK_COLOR_INVALID may cause an infinite loop
	 *       and crash the game. This check is handled by the above public method
	 *       so as to avoid redundant checks.
	 */
	private int clearColor (int x, int y, int targetColor, boolean flag, boolean garbageClear)
	{
		int blockColor = getBlockColor(x, y);
		if (blockColor == Block.BLOCK_COLOR_INVALID)
			return 0;
		Block b = getBlock(x, y);
		if (garbageClear && b.getAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE)
				 && !b.getAttribute(Block.BLOCK_ATTRIBUTE_WALL))
		{
			if (flag)
				b.setAttribute(Block.BLOCK_ATTRIBUTE_ERASE, true);
			setBlockColor(x, y, Block.BLOCK_COLOR_NONE);
		}
		if (blockColor != targetColor)
			return 0;
		if (flag)
			b.setAttribute(Block.BLOCK_ATTRIBUTE_ERASE, true);
		setBlockColor(x, y, Block.BLOCK_COLOR_NONE);
		return 1 + clearColor(x+1, y, targetColor, flag, garbageClear) + clearColor(x-1, y, targetColor, flag, garbageClear)
				 + clearColor(x, y+1, targetColor, flag, garbageClear) + clearColor(x, y-1, targetColor, flag, garbageClear);
	}

	/**
	 * Main routine for cascade gravity.
	 * @return <code>true</code> if something falls. <code>false</code> if nothing falls.
	 */
	public boolean doCascadeGravity() {
		boolean result = false;

		setAllAttribute(Block.BLOCK_ATTRIBUTE_CASCADE_FALL, false);

		for(int i = (getHeightWithoutHurryupFloor() - 1); i >= (hidden_height * -1); i--) {
			for(int j = 0; j < width; j++) {
				Block blk = getBlock(j, i);

				if((blk != null) && !blk.isEmpty()) {
					boolean fall = true;
					checkBlockLink(j, i);

					for(int k = (getHeightWithoutHurryupFloor() - 1); k >= (hidden_height * -1); k--) {
						for(int l = 0; l < width; l++) {
							Block bTemp = getBlock(l, k);

							if( (bTemp != null) && !bTemp.isEmpty() &&
								bTemp.getAttribute(Block.BLOCK_ATTRIBUTE_TEMP_MARK) && !bTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CASCADE_FALL) )
							{
								Block bBelow = getBlock(l, k + 1);

								if( (getCoordAttribute(l, k + 1) == COORD_WALL) ||
									((bBelow != null) && !bBelow.isEmpty() && !bBelow.getAttribute(Block.BLOCK_ATTRIBUTE_TEMP_MARK)) )
								{
									fall = false;
								}
							}
						}
					}

					if(fall) {
						result = true;
						for(int k = (getHeightWithoutHurryupFloor() - 1); k >= (hidden_height * -1); k--) {
							for(int l = 0; l < width; l++) {
								Block bTemp = getBlock(l, k);
								Block bBelow = getBlock(l, k + 1);

								if( (getCoordAttribute(l, k + 1) != COORD_WALL) &&
								    (bTemp != null) && !bTemp.isEmpty() && (bBelow != null) && bBelow.isEmpty() &&
								    bTemp.getAttribute(Block.BLOCK_ATTRIBUTE_TEMP_MARK) && !bTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CASCADE_FALL) )
								{
									bTemp.setAttribute(Block.BLOCK_ATTRIBUTE_TEMP_MARK, false);
									bTemp.setAttribute(Block.BLOCK_ATTRIBUTE_CASCADE_FALL, true);
									setBlock(l, k + 1, bTemp);
									setBlock(l, k, new Block());
								}
							}
						}
					}
				}
			}
		}

		setAllAttribute(Block.BLOCK_ATTRIBUTE_TEMP_MARK, false);
		setAllAttribute(Block.BLOCK_ATTRIBUTE_CASCADE_FALL, false);
		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			setLineFlag(i, false);
		}

		return result;
	}

	/**
	 * Checks the connection of blocks and set "mark" to each block.
	 * @param x X coord
	 * @param y Y coord
	 */
	public void checkBlockLink(int x, int y) {
		setAllAttribute(Block.BLOCK_ATTRIBUTE_TEMP_MARK, false);
		checkBlockLinkSub(x, y);
	}

	/**
	 * Subroutine for checkBlockLink.
	 * @param x X coord
	 * @param y Y coord
	 */
	protected void checkBlockLinkSub(int x, int y) {
		Block blk = getBlock(x, y);
		if((blk != null) && !blk.isEmpty() && !blk.getAttribute(Block.BLOCK_ATTRIBUTE_TEMP_MARK)) {
			blk.setAttribute(Block.BLOCK_ATTRIBUTE_TEMP_MARK, true);
			if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP)) checkBlockLinkSub(x, y - 1);
			if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) checkBlockLinkSub(x, y + 1);
			if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT)) checkBlockLinkSub(x - 1, y);
			if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT)) checkBlockLinkSub(x + 1, y);
		}
	}

	/**
	 * Checks the connection of blocks and set the "broken" flag to each block.
	 * It only affects to normal blocks. (ex. not square or gems)
	 * @param x X coord
	 * @param y Y coord
	 */
	public void setBlockLinkBroken(int x, int y) {
		setAllAttribute(Block.BLOCK_ATTRIBUTE_TEMP_MARK, false);
		setBlockLinkBrokenSub(x, y);
	}

	/**
	 * Subroutine for setBlockLinkBrokenSub.
	 * @param x X coord
	 * @param y Y coord
	 */
	protected void setBlockLinkBrokenSub(int x, int y) {
		Block blk = getBlock(x, y);
		if((blk != null) && !blk.isEmpty() && !blk.getAttribute(Block.BLOCK_ATTRIBUTE_TEMP_MARK) && blk.isNormalBlock()) {
			blk.setAttribute(Block.BLOCK_ATTRIBUTE_TEMP_MARK, true);
			blk.setAttribute(Block.BLOCK_ATTRIBUTE_BROKEN, true);
			if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP)) setBlockLinkBrokenSub(x, y - 1);
			if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) setBlockLinkBrokenSub(x, y + 1);
			if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT)) setBlockLinkBrokenSub(x - 1, y);
			if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT)) setBlockLinkBrokenSub(x + 1, y);
		}
	}

	/**
	 * HURRY UPの地面を一番下に追加
	 * @param lines 上げるライン数
	 * @param skin 地面の絵柄
	 */
	public void addHurryupFloor(int lines, int skin) {
		for(int k = 0; k < lines; k++) {
			pushUp(1);

			for(int j = 0; j < width; j++) {
				Block blk = new Block();
				blk.color = Block.BLOCK_COLOR_GRAY;
				blk.skin = skin;
				blk.attribute = Block.BLOCK_ATTRIBUTE_WALL | Block.BLOCK_ATTRIBUTE_GARBAGE | Block.BLOCK_ATTRIBUTE_VISIBLE;
				setBlock(j, getHeightWithoutHurryupFloor() - 1, blk);
			}

			hurryupFloorLines++;
		}
	}

	/**
	 * HURRY UPの地面が何ラインあるか調べる
	 * @return HURRY UPの地面ラインの数
	 */
	public int getHurryupFloorLines() {
		return hurryupFloorLines;
	}

	/**
	 * HURRY UPの地面を除いたフィールドの高さを返す
	 * @return HURRY UPの地面を除いたフィールドの高さ
	 */
	public int getHeightWithoutHurryupFloor() {
		return height - hurryupFloorLines;
	}

	/**
	 * フィールドを文字列に変換
	 * @return 文字列に変換されたフィールド
	 */
	public String fieldToString() {
		String strResult = "";

		for(int i = getHeight() - 1; i >= Math.max(-1, getHighestBlockY()); i--) {
			for(int j = 0; j < getWidth(); j++) {
				int blkColor = getBlockColor(j, i);
				if(blkColor < 0) blkColor = Block.BLOCK_COLOR_NONE;

				if(blkColor >= 10) {
					char c = (char)(0x41 + (blkColor - 10));
					strResult += c;
				} else {
					strResult += Integer.toString(blkColor);
				}
			}
		}

		// 終わりの0を取り除く
		while(strResult.endsWith("0")) {
			strResult = strResult.substring(0, strResult.length() - 1);
		}

		return strResult;
	}

	/**
	 * 文字列を元にフィールドを変更
	 * @param str 文字列
	 */
	public void stringToField(String str) {
		for(int i = -1; i < getHeight(); i++) {
			for(int j = 0; j < getWidth(); j++) {
				int index = (getHeight() - 1 - i) * getWidth() + j;
				int blkColor = Block.BLOCK_COLOR_NONE;

				try {
					char c = str.charAt(index);
					blkColor = Character.digit(c, 10);

					if(blkColor == -1) {
						blkColor = (c - 0x41) + 10;
					}
				} catch (Exception e) {}

				Block blk = new Block();
				blk.color = blkColor;
				blk.elapsedFrames = -1;
				blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
				blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);

				setBlock(j, i, blk);
			}
		}
	}

	/**
	 * 文字列を元にフィールドを変更
	 * @param str 文字列
	 * @param skin ブロックの絵柄
	 * @param highestGarbageY 最も高い邪魔ブロックの位置
	 * @param highestWallY 最も高いHurryupブロックの位置
	 */
	public void stringToField(String str, int skin, int highestGarbageY, int highestWallY) {
		for(int i = -1; i < getHeight(); i++) {
			for(int j = 0; j < getWidth(); j++) {
				int index = (getHeight() - 1 - i) * getWidth() + j;
				int blkColor = Block.BLOCK_COLOR_NONE;

				try {
					char c = str.charAt(index);
					blkColor = Character.digit(c, 10);

					if(blkColor == -1) {
						blkColor = (c - 0x41) + 10;
					}
				} catch (Exception e) {}

				Block blk = new Block();
				blk.color = blkColor;
				blk.skin = skin;
				blk.elapsedFrames = -1;
				blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
				blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);

				if(i >= highestGarbageY) {
					blk.setAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE, true);
				}
				if(i >= highestWallY) {
					blk.setAttribute(Block.BLOCK_ATTRIBUTE_WALL, true);
				}

				setBlock(j, i, blk);
			}
		}
	}

	/**
	 * フィールドの文字列表現を取得
	 */
	@Override
	public String toString() {
		String str = getClass().getName() + "@" + Integer.toHexString(hashCode()) + "\n";

		for(int i = (hidden_height * -1); i < height; i++) {
			str += String.format("%3d:", i);

			for(int j = 0; j < width; j++) {
				int color = getBlockColor(j, i);

				if(color < 0) {
					str += "*";
				} else if(color >= 10) {
					str += "+";
				} else {
					str += Integer.toString(color);
				}
			}

			str += "\n";
		}

		return str;
	}

	public int checkColor(int size, boolean flag, boolean garbageClear) {
		Field temp = new Field(this);
		int total = 0;

		for(int i = (hidden_height * -1); i < getHeightWithoutHurryupFloor(); i++) {
			for(int j = 0; j < width; j++) {
				int clear = temp.clearColor(j, i, false, garbageClear);
				if (clear >= size)
				{
					total += clear;
					if (flag)
						clearColor(j, i, true, garbageClear);
				}
			}
		}
		return total;
	}
}
