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

import mu.nu.nullpo.util.CustomProperties;

/**
 * ゲームルールの設定 data
 */
public class RuleOptions implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = 5781310758989780350L;

	/** 横移動カウンタかrotationカウンタが超過したら固定 timeリセットを無効にする */
	public static final int LOCKRESET_LIMIT_OVER_NORESET = 0;

	/** 横移動カウンタかrotationカウンタが超過したら即座に固定する */
	public static final int LOCKRESET_LIMIT_OVER_INSTANT = 1;

	/** 横移動カウンタかrotationカウンタが超過したらWallkick無効にする */
	public static final int LOCKRESET_LIMIT_OVER_NOWALLKICK = 2;

	/** このルールの名前 */
	public String strRuleName;

	/** 使用するWallkickアルゴリズムのクラス名 (空文字列ならWallkickしない) */
	public String strWallkick;

	/** 使用する出現順補正アルゴリズムのクラス名 (空文字列なら完全ランダム) */
	public String strRandomizer;

	/** BlockピースのrotationパターンのX-coordinate補正 (11ピース×4Direction) */
	public int[][] pieceOffsetX;

	/** BlockピースのrotationパターンのY-coordinate補正 (11ピース×4Direction) */
	public int[][] pieceOffsetY;

	/** Blockピースの出現X-coordinate補正 (11ピース×4Direction) */
	public int[][] pieceSpawnX;

	/** Blockピースの出現Y-coordinate補正 (11ピース×4Direction) */
	public int[][] pieceSpawnY;

	/** BlockピースのBig時の出現X-coordinate補正 (11ピース×4Direction) */
	public int[][] pieceSpawnXBig;

	/** BlockピースのBig時の出現Y-coordinate補正 (11ピース×4Direction) */
	public int[][] pieceSpawnYBig;

	/** Blockピースの色 */
	public int[] pieceColor;

	/** Blockピースの初期Direction */
	public int[] pieceDefaultDirection;

	/** フィールドより上から出現 */
	public boolean pieceEnterAboveField;

	/** 出現予定地が埋まっているときにY-coordinateを上にずらすMaximum count */
	public int pieceEnterMaxDistanceY;

	/** フィールドの幅 */
	public int fieldWidth;

	/** Field height */
	public int fieldHeight;

	/** フィールドより上の見えない部分の高さ */
	public int fieldHiddenHeight;

	/** フィールドの天井の有無 */
	public boolean fieldCeiling;

	/** フィールド枠内に置けなかったら死ぬかどうか */
	public boolean fieldLockoutDeath;

	/** フィールド枠外にはみ出しただけで死ぬかどうか */
	public boolean fieldPartialLockoutDeath;

	/** NEXTのcount */
	public int nextDisplay;

	/** ホールド使用可否 */
	public boolean holdEnable;

	/** 先行ホールド */
	public boolean holdInitial;

	/** 先行ホールド連続使用不可 */
	public boolean holdInitialLimit;

	/** ホールドを使ったときにBlockピースの向きを初期状態に戻す */
	public boolean holdResetDirection;

	/** ホールドできる count (-1:無制限) */
	public int holdLimit;

	/** Hard drop使用可否 */
	public boolean harddropEnable;

	/** Hard drop即固定 */
	public boolean harddropLock;

	/** Hard drop連続使用不可 */
	public boolean harddropLimit;

	/** Soft drop使用可否 */
	public boolean softdropEnable;

	/** Soft drop即固定 */
	public boolean softdropLock;

	/** Soft drop連続使用不可 */
	public boolean softdropLimit;

	/** 接地状態でSoft dropすると即固定 */
	public boolean softdropSurfaceLock;

	/** Soft drop速度 (1.0f=1G, 0.5f=0.5G) */
	public float softdropSpeed;

	/** Soft drop速度をCurrent 通常速度×n倍にする */
	public boolean softdropMultiplyNativeSpeed;

	/** 先行rotation */
	public boolean rotateInitial;

	/** 先行rotation連続使用不可 */
	public boolean rotateInitialLimit;

	/** Wallkick */
	public boolean rotateWallkick;

	/** 先行rotationでもWallkickする */
	public boolean rotateInitialWallkick;

	/** 上DirectionへのWallkickができる count (-1:無限) */
	public int rotateMaxUpwardWallkick;

	/** falseなら左が正rotation, trueなら右が正rotation */
	public boolean rotateButtonDefaultRight;

	/** 逆rotationを許可 (falseなら正rotationと同じ) */
	public boolean rotateButtonAllowReverse;

	/** 180-degree rotationを許可 (falseなら正rotationと同じ) */
	public boolean rotateButtonAllowDouble;

	/** 落下で固定 timeリセット */
	public boolean lockresetFall;

	/** 移動で固定 timeリセット */
	public boolean lockresetMove;

	/** rotationで固定 timeリセット */
	public boolean lockresetRotate;

	/** 横移動 count制限 (-1:無限) */
	public int lockresetLimitMove;

	/** rotation count制限 (-1:無限) */
	public int lockresetLimitRotate;

	/** 横移動カウンタとrotationカウンタを共有 (横移動カウンタだけ使う) */
	public boolean lockresetLimitShareCount;

	/** 横移動カウンタかrotationカウンタが超過したときの処理 (LOCKRESET_LIMIT_OVER_で始まる定countを使う) */
	public int lockresetLimitOver;

	/** 固定した瞬間光る frame count */
	public int lockflash;

	/** Blockが光る専用 frame を入れる */
	public boolean lockflashOnlyFrame;

	/** Line clear前にBlockが光る frame を入れる */
	public boolean lockflashBeforeLineClear;

	/** ARE cancel on move */
	public boolean areCancelMove;

	/** ARE cancel on rotate*/
	public boolean areCancelRotate;

	/** ARE cancel on hold*/
	public boolean areCancelHold;

	/** 最小/MaximumARE (-1:指定なし) */
	public int minARE, maxARE;

	/** 最小/MaximumARE after line clear (-1:指定なし) */
	public int minARELine, maxARELine;

	/** 最小/MaximumLine clear time (-1:指定なし) */
	public int minLineDelay, maxLineDelay;

	/** 最小/Maximum固定 time (-1:指定なし) */
	public int minLockDelay, maxLockDelay;

	/** 最小/Maximum横溜め time (-1:指定なし) */
	public int minDAS, maxDAS;

	/** 横移動間隔 */
	public int dasDelay;

	public boolean shiftLockEnable;

	/** Ready画面で横溜め可能 */
	public boolean dasInReady;

	/** 最初の frame で横溜め可能 */
	public boolean dasInMoveFirstFrame;

	/** Blockが光った瞬間に横溜め可能 */
	public boolean dasInLockFlash;

	/** Line clear中に横溜め可能 */
	public boolean dasInLineClear;

	/** ARE中に横溜め可能 */
	public boolean dasInARE;

	/** AREの最後の frame で横溜め可能 */
	public boolean dasInARELastFrame;

	/** Ending突入画面で横溜め可能 */
	public boolean dasInEndingStart;

	/** Charge DAS on blocked move */
	public boolean dasChargeOnBlockedMove;

	/** Leave DAS charge alone when left/right are not held -- useful with dasRedirectInDelay **/
   public boolean dasStoreChargeOnNeutral;

   /** Allow direction changes during delays without zeroing DAS charge **/
   public boolean dasRedirectInDelay;

	/** 最初の frame で移動可能 */
	public boolean moveFirstFrame;

	/** 斜め移動 */
	public boolean moveDiagonal;

	/** 上下同時押し許可 */
	public boolean moveUpAndDown;

	/** 左右同時押し許可 */
	public boolean moveLeftAndRightAllow;

	/** 左右同時押ししたときに前の frame の input Directionを優先する (左を押しながら右を押すと右を無視して左を優先) */
	public boolean moveLeftAndRightUsePreviousInput;

	/** Line clear後に上のBlockが1段ずつ落ちるアニメーションを表示 */
	public boolean lineFallAnim;

	/** Line delay cancel on move */
	public boolean lineCancelMove;

	/** Line delay cancel on rotate */
	public boolean lineCancelRotate;

	/** Line delay cancel on hold */
	public boolean lineCancelHold;

	/** Blockの絵柄 */
	public int skin;

	/** ゴーストの有無 (falseならMode 側でゴーストを有効にしていても非表示) */
	public boolean ghost;

	/**
	 * Constructor
	 */
	public RuleOptions() {
		reset();
	}

	/**
	 * Copy constructor
	 * @param r Copy source
	 */
	public RuleOptions(RuleOptions r) {
		copy(r);
	}

	/**
	 * Initialization
	 */
	public void reset() {
		strRuleName = "";
		strWallkick = "";
		strRandomizer = "";

		pieceOffsetX = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceOffsetY = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnX = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnY = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnXBig = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnYBig = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];

		pieceColor = new int[Piece.PIECE_COUNT];
		pieceColor[Piece.PIECE_I] = Block.BLOCK_COLOR_GRAY;
		pieceColor[Piece.PIECE_L] = Block.BLOCK_COLOR_GRAY;
		pieceColor[Piece.PIECE_O] = Block.BLOCK_COLOR_GRAY;
		pieceColor[Piece.PIECE_Z] = Block.BLOCK_COLOR_GRAY;
		pieceColor[Piece.PIECE_T] = Block.BLOCK_COLOR_GRAY;
		pieceColor[Piece.PIECE_J] = Block.BLOCK_COLOR_GRAY;
		pieceColor[Piece.PIECE_S] = Block.BLOCK_COLOR_GRAY;
		pieceColor[Piece.PIECE_I1] = Block.BLOCK_COLOR_PURPLE;
		pieceColor[Piece.PIECE_I2] = Block.BLOCK_COLOR_BLUE;
		pieceColor[Piece.PIECE_I3] = Block.BLOCK_COLOR_GREEN;
		pieceColor[Piece.PIECE_L3] = Block.BLOCK_COLOR_ORANGE;

		pieceDefaultDirection = new int[Piece.PIECE_COUNT];
		pieceEnterAboveField = true;
		pieceEnterMaxDistanceY = 0;

		fieldWidth = Field.DEFAULT_WIDTH;
		fieldHeight = Field.DEFAULT_HEIGHT;
		fieldHiddenHeight = Field.DEFAULT_HIDDEN_HEIGHT;
		fieldCeiling = false;
		fieldLockoutDeath = true;
		fieldPartialLockoutDeath = false;

		nextDisplay = 3;

		holdEnable = true;
		holdInitial = true;
		holdInitialLimit = false;
		holdResetDirection = true;
		holdLimit = -1;

		harddropEnable = true;
		harddropLock = true;
		harddropLimit = true;

		softdropEnable = true;
		softdropLock = false;
		softdropLimit = false;
		softdropSurfaceLock = false;
		softdropSpeed = 0.5f;
		softdropMultiplyNativeSpeed = false;

		rotateInitial = true;
		rotateInitialLimit = false;
		rotateWallkick = true;
		rotateInitialWallkick = true;
		rotateMaxUpwardWallkick = -1;
		rotateButtonDefaultRight = true;
		rotateButtonAllowReverse = true;
		rotateButtonAllowDouble = true;

		lockresetFall = true;
		lockresetMove = true;
		lockresetRotate = true;
		lockresetLimitMove = 15;
		lockresetLimitRotate = 15;
		lockresetLimitShareCount = true;
		lockresetLimitOver = LOCKRESET_LIMIT_OVER_INSTANT;

		lockflash = 2;
		lockflashOnlyFrame = true;
		lockflashBeforeLineClear = false;
		areCancelMove = false;
		areCancelRotate = false;
		areCancelHold = false;

		minARE = -1;
		maxARE = -1;
		minARELine = -1;
		maxARELine = -1;
		minLineDelay = -1;
		maxLineDelay = -1;
		minLockDelay = -1;
		maxLockDelay = -1;
		minDAS = -1;
		maxDAS = -1;

		dasDelay = 0;

		shiftLockEnable = false;

		dasInReady = true;
		dasInMoveFirstFrame = true;
		dasInLockFlash = true;
		dasInLineClear = true;
		dasInARE = true;
		dasInARELastFrame = true;
		dasInEndingStart = true;
		dasChargeOnBlockedMove = false;
	   dasStoreChargeOnNeutral = false;
	   dasRedirectInDelay = false;

		moveFirstFrame = true;
		moveDiagonal = true;
		moveUpAndDown = true;
		moveLeftAndRightAllow = true;
		moveLeftAndRightUsePreviousInput = false;

		lineFallAnim = true;
		lineCancelMove = false;
		lineCancelRotate = false;
		lineCancelHold = false;

		skin = 0;
		ghost = true;
	}

	/**
	 * 他のRuleParamの内容をコピー
	 * @param r Copy sourceのRuleParam
	 */
	public void copy(RuleOptions r) {
		strRuleName = r.strRuleName;
		strWallkick = r.strWallkick;
		strRandomizer = r.strRandomizer;

		pieceOffsetX = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceOffsetY = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnX = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnY = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnXBig = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnYBig = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceColor = new int[Piece.PIECE_COUNT];
		pieceDefaultDirection = new int[Piece.PIECE_COUNT];
		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			for(int j = 0; j < Piece.DIRECTION_COUNT; j++) {
				pieceOffsetX[i][j] = r.pieceOffsetX[i][j];
				pieceOffsetY[i][j] = r.pieceOffsetY[i][j];
				pieceSpawnX[i][j] = r.pieceSpawnX[i][j];
				pieceSpawnY[i][j] = r.pieceSpawnY[i][j];
				pieceSpawnXBig[i][j] = r.pieceSpawnXBig[i][j];
				pieceSpawnYBig[i][j] = r.pieceSpawnYBig[i][j];
			}
			pieceColor[i] = r.pieceColor[i];
			pieceDefaultDirection[i] = r.pieceDefaultDirection[i];
		}
		pieceEnterAboveField = r.pieceEnterAboveField;
		pieceEnterMaxDistanceY = r.pieceEnterMaxDistanceY;

		fieldWidth = r.fieldWidth;
		fieldHeight = r.fieldHeight;
		fieldHiddenHeight = r.fieldHiddenHeight;
		fieldCeiling = r.fieldCeiling;
		fieldLockoutDeath = r.fieldLockoutDeath;
		fieldPartialLockoutDeath = r.fieldPartialLockoutDeath;

		nextDisplay = r.nextDisplay;

		holdEnable = r.holdEnable;
		holdInitial = r.holdInitial;
		holdInitialLimit = r.holdInitialLimit;
		holdResetDirection = r.holdResetDirection;
		holdLimit = r.holdLimit;

		harddropEnable = r.harddropEnable;
		harddropLock = r.harddropLock;
		harddropLimit = r.harddropLimit;

		softdropEnable = r.softdropEnable;
		softdropLock = r.softdropLock;
		softdropLimit = r.softdropLimit;
		softdropSurfaceLock = r.softdropSurfaceLock;
		softdropSpeed = r.softdropSpeed;
		softdropMultiplyNativeSpeed = r.softdropMultiplyNativeSpeed;

		rotateInitial = r.rotateInitial;
		rotateInitialLimit = r.rotateInitialLimit;
		rotateWallkick = r.rotateWallkick;
		rotateInitialWallkick = r.rotateInitialWallkick;
		rotateMaxUpwardWallkick = r.rotateMaxUpwardWallkick;
		rotateButtonDefaultRight = r.rotateButtonDefaultRight;
		rotateButtonAllowReverse = r.rotateButtonAllowReverse;
		rotateButtonAllowDouble = r.rotateButtonAllowDouble;

		lockresetFall = r.lockresetFall;
		lockresetMove = r.lockresetMove;
		lockresetRotate = r.lockresetRotate;
		lockresetLimitMove = r.lockresetLimitMove;
		lockresetLimitRotate = r.lockresetLimitRotate;
		lockresetLimitShareCount = r.lockresetLimitShareCount;
		lockresetLimitOver = r.lockresetLimitOver;

		lockflash = r.lockflash;
		lockflashOnlyFrame = r.lockflashOnlyFrame;
		lockflashBeforeLineClear = r.lockflashBeforeLineClear;
		areCancelMove = r.areCancelMove;
		areCancelRotate = r.areCancelRotate;
		areCancelHold = r.areCancelHold;

		minARE = r.minARE;
		maxARE = r.maxARE;
		minARELine = r.minARELine;
		maxARELine = r.maxARELine;
		minLineDelay = r.minLineDelay;
		maxLineDelay = r.maxLineDelay;
		minLockDelay = r.minLockDelay;
		maxLockDelay = r.maxLockDelay;
		minDAS = r.minDAS;
		maxDAS = r.maxDAS;

		dasDelay = r.dasDelay;

		shiftLockEnable = r.shiftLockEnable;

		dasInReady = r.dasInReady;
		dasInMoveFirstFrame = r.dasInMoveFirstFrame;
		dasInLockFlash = r.dasInLockFlash;
		dasInLineClear = r.dasInLineClear;
		dasInARE = r.dasInARE;
		dasInARELastFrame = r.dasInARELastFrame;
		dasInEndingStart = r.dasInEndingStart;
		dasChargeOnBlockedMove = r.dasChargeOnBlockedMove;
		dasStoreChargeOnNeutral = r.dasStoreChargeOnNeutral;
      dasRedirectInDelay = r.dasRedirectInDelay;

		moveFirstFrame = r.moveFirstFrame;
		moveDiagonal = r.moveDiagonal;
		moveUpAndDown = r.moveUpAndDown;
		moveLeftAndRightAllow = r.moveLeftAndRightAllow;
		moveLeftAndRightUsePreviousInput = r.moveLeftAndRightUsePreviousInput;

		lineFallAnim = r.lineFallAnim;
		lineCancelMove = r.lineCancelMove;
		lineCancelRotate = r.lineCancelRotate;
		lineCancelHold = r.lineCancelHold;

		skin = r.skin;
		ghost = r.ghost;
	}

	/**
	 * 他のルールと比較し, 同じならtrueを返す
	 * @param r 比較するルール
	 * @param ignoreGraphicsSetting trueにするとゲーム自体に影響しない設定を無視
	 * @return 比較したルールと同じならtrue
	 */
	public boolean compare(RuleOptions r, boolean ignoreGraphicsSetting) {
		if((!ignoreGraphicsSetting) && (strRuleName != r.strRuleName)) return false;
		if(strWallkick != r.strWallkick) return false;
		if(strRandomizer != r.strRandomizer) return false;

		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			for(int j = 0; j < Piece.DIRECTION_COUNT; j++) {
				if(pieceOffsetX[i][j] != r.pieceOffsetX[i][j]) return false;
				if(pieceOffsetY[i][j] != r.pieceOffsetY[i][j]) return false;
				if(pieceSpawnX[i][j] != r.pieceSpawnX[i][j]) return false;
				if(pieceSpawnY[i][j] != r.pieceSpawnY[i][j]) return false;
				if(pieceSpawnXBig[i][j] != r.pieceSpawnXBig[i][j]) return false;
				if(pieceSpawnYBig[i][j] != r.pieceSpawnYBig[i][j]) return false;
			}
			if((!ignoreGraphicsSetting) && (pieceColor[i] != r.pieceColor[i])) return false;
			if(pieceDefaultDirection[i] != r.pieceDefaultDirection[i]) return false;
		}
		if(pieceEnterAboveField != r.pieceEnterAboveField) return false;
		if(pieceEnterMaxDistanceY != r.pieceEnterMaxDistanceY) return false;

		if(fieldWidth != r.fieldWidth) return false;
		if(fieldHeight != r.fieldHeight) return false;
		if(fieldHiddenHeight != r.fieldHiddenHeight) return false;
		if(fieldCeiling != r.fieldCeiling) return false;
		if(fieldLockoutDeath != r.fieldLockoutDeath) return false;
		if(fieldPartialLockoutDeath != r.fieldPartialLockoutDeath) return false;

		if(nextDisplay != r.nextDisplay) return false;

		if(holdEnable != r.holdEnable) return false;
		if(holdInitial != r.holdInitial) return false;
		if(holdInitialLimit != r.holdInitialLimit) return false;
		if(holdResetDirection != r.holdResetDirection) return false;
		if(holdLimit != r.holdLimit) return false;

		if(harddropEnable != r.harddropEnable) return false;
		if(harddropLock != r.harddropLock) return false;
		if(harddropLimit != r.harddropLimit) return false;

		if(softdropEnable != r.softdropEnable) return false;
		if(softdropLock != r.softdropLock) return false;
		if(softdropLimit != r.softdropLimit) return false;
		if(softdropSurfaceLock != r.softdropSurfaceLock) return false;
		if(softdropSpeed != r.softdropSpeed) return false;
		if(softdropMultiplyNativeSpeed != r.softdropMultiplyNativeSpeed) return false;

		if(rotateInitial != r.rotateInitial) return false;
		if(rotateInitialLimit != r.rotateInitialLimit) return false;
		if(rotateWallkick != r.rotateWallkick) return false;
		if(rotateInitialWallkick != r.rotateInitialWallkick) return false;
		if(rotateMaxUpwardWallkick != r.rotateMaxUpwardWallkick) return false;
		if(rotateButtonDefaultRight != r.rotateButtonDefaultRight) return false;
		if(rotateButtonAllowReverse != r.rotateButtonAllowReverse) return false;
		if(rotateButtonAllowDouble != r.rotateButtonAllowDouble) return false;

		if(lockresetFall != r.lockresetFall) return false;
		if(lockresetMove != r.lockresetMove) return false;
		if(lockresetRotate != r.lockresetRotate) return false;
		if(lockresetLimitMove != r.lockresetLimitMove) return false;
		if(lockresetLimitRotate != r.lockresetLimitRotate) return false;
		if(lockresetLimitShareCount != r.lockresetLimitShareCount) return false;
		if(lockresetLimitOver != r.lockresetLimitOver) return false;

		if(lockflash != r.lockflash) return false;
		if(lockflashOnlyFrame != r.lockflashOnlyFrame) return false;
		if(lockflashBeforeLineClear != r.lockflashBeforeLineClear) return false;
		if(areCancelMove != r.areCancelMove) return false;
		if(areCancelRotate != r.areCancelRotate) return false;
		if(areCancelHold != r.areCancelHold) return false;

		if(minARE != r.minARE) return false;
		if(maxARE != r.maxARE) return false;
		if(minARELine != r.minARELine) return false;
		if(maxARELine != r.maxARELine) return false;
		if(minLineDelay != r.minLineDelay) return false;
		if(maxLineDelay != r.maxLineDelay) return false;
		if(minLockDelay != r.minLockDelay) return false;
		if(maxLockDelay != r.maxLockDelay) return false;
		if(minDAS != r.minDAS) return false;
		if(maxDAS != r.maxDAS) return false;

		if(dasDelay != r.dasDelay) return false;

		if(shiftLockEnable != r.shiftLockEnable) return false;

		if(dasInReady != r.dasInReady) return false;
		if(dasInMoveFirstFrame != r.dasInMoveFirstFrame) return false;
		if(dasInLockFlash != r.dasInLockFlash) return false;
		if(dasInLineClear != r.dasInLineClear) return false;
		if(dasInARE != r.dasInARE) return false;
		if(dasInARELastFrame != r.dasInARELastFrame) return false;
		if(dasInEndingStart != r.dasInEndingStart) return false;
		if(dasChargeOnBlockedMove != r.dasChargeOnBlockedMove) return false;
		if(dasStoreChargeOnNeutral != r.dasStoreChargeOnNeutral) return false;
		if(dasRedirectInDelay != r.dasRedirectInDelay) return false;

		if(moveFirstFrame != r.moveFirstFrame) return false;
		if(moveDiagonal != r.moveDiagonal) return false;
		if(moveUpAndDown != r.moveUpAndDown) return false;
		if(moveLeftAndRightAllow != r.moveLeftAndRightAllow) return false;
		if(moveLeftAndRightUsePreviousInput != r.moveLeftAndRightUsePreviousInput) return false;

		if((ignoreGraphicsSetting) && (lineFallAnim != r.lineFallAnim)) return false;
		if(lineCancelMove != r.lineCancelMove) return false;
		if(lineCancelRotate != r.lineCancelRotate) return false;
		if(lineCancelHold != r.lineCancelHold) return false;

		if((ignoreGraphicsSetting) && (skin != r.skin)) return false;
		if(ghost != r.ghost) return false;

		return true;
	}

	/**
	 * プロパティセットに保存
	 * @param p プロパティセット
	 * @param id Player IDまたはPresetID
	 */
	public void writeProperty(CustomProperties p, int id) {
		p.setProperty(id + ".ruleopt.strRuleName", strRuleName);
		p.setProperty(id + ".ruleopt.strWallkick", strWallkick);
		p.setProperty(id + ".ruleopt.strRandomizer", strRandomizer);

		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			for(int j = 0; j < Piece.DIRECTION_COUNT; j++) {
				p.setProperty(id + ".ruleopt.pieceOffsetX." + i + "." + j, pieceOffsetX[i][j]);
				p.setProperty(id + ".ruleopt.pieceOffsetY." + i + "." + j, pieceOffsetY[i][j]);
				p.setProperty(id + ".ruleopt.pieceSpawnX." + i + "." + j, pieceSpawnX[i][j]);
				p.setProperty(id + ".ruleopt.pieceSpawnY." + i + "." + j, pieceSpawnY[i][j]);
				p.setProperty(id + ".ruleopt.pieceSpawnXBig." + i + "." + j, pieceSpawnXBig[i][j]);
				p.setProperty(id + ".ruleopt.pieceSpawnYBig." + i + "." + j, pieceSpawnYBig[i][j]);
			}
			p.setProperty(id + ".ruleopt.pieceColor." + i, pieceColor[i]);
			p.setProperty(id + ".ruleopt.pieceDefaultDirection." + i, pieceDefaultDirection[i]);
		}
		p.setProperty(id + ".ruleopt.pieceEnterAboveField", pieceEnterAboveField);
		p.setProperty(id + ".ruleopt.pieceEnterMaxDistanceY", pieceEnterMaxDistanceY);

		p.setProperty(id + ".ruleopt.fieldWidth", fieldWidth);
		p.setProperty(id + ".ruleopt.fieldHeight", fieldHeight);
		p.setProperty(id + ".ruleopt.fieldHiddenHeight", fieldHiddenHeight);
		p.setProperty(id + ".ruleopt.fieldCeiling", fieldCeiling);
		p.setProperty(id + ".ruleopt.fieldLockoutDeath", fieldLockoutDeath);
		p.setProperty(id + ".ruleopt.fieldPartialLockoutDeath", fieldPartialLockoutDeath);

		p.setProperty(id + ".ruleopt.nextDisplay", nextDisplay);

		p.setProperty(id + ".ruleopt.holdEnable", holdEnable);
		p.setProperty(id + ".ruleopt.holdInitial", holdInitial);
		p.setProperty(id + ".ruleopt.holdInitialLimit", holdInitialLimit);
		p.setProperty(id + ".ruleopt.holdResetDirection", holdResetDirection);
		p.setProperty(id + ".ruleopt.holdLimit", holdLimit);

		p.setProperty(id + ".ruleopt.harddropEnable", harddropEnable);
		p.setProperty(id + ".ruleopt.harddropLock", harddropLock);
		p.setProperty(id + ".ruleopt.harddropLimit", harddropLimit);

		p.setProperty(id + ".ruleopt.softdropEnable", softdropEnable);
		p.setProperty(id + ".ruleopt.softdropLock", softdropLock);
		p.setProperty(id + ".ruleopt.softdropLimit", softdropLimit);
		p.setProperty(id + ".ruleopt.softdropSurfaceLock", softdropSurfaceLock);
		p.setProperty(id + ".ruleopt.softdropSpeed", softdropSpeed);
		p.setProperty(id + ".ruleopt.softdropMultiplyNativeSpeed", softdropMultiplyNativeSpeed);

		p.setProperty(id + ".ruleopt.rotateInitial", rotateInitial);
		p.setProperty(id + ".ruleopt.rotateInitialLimit", rotateInitialLimit);
		p.setProperty(id + ".ruleopt.rotateWallkick", rotateWallkick);
		p.setProperty(id + ".ruleopt.rotateInitialWallkick", rotateInitialWallkick);
		p.setProperty(id + ".ruleopt.rotateMaxUpwardWallkick", rotateMaxUpwardWallkick);
		p.setProperty(id + ".ruleopt.rotateButtonDefaultRight", rotateButtonDefaultRight);
		p.setProperty(id + ".ruleopt.rotateButtonAllowReverse", rotateButtonAllowReverse);
		p.setProperty(id + ".ruleopt.rotateButtonAllowDouble", rotateButtonAllowDouble);

		p.setProperty(id + ".ruleopt.lockresetFall", lockresetFall);
		p.setProperty(id + ".ruleopt.lockresetMove", lockresetMove);
		p.setProperty(id + ".ruleopt.lockresetRotate", lockresetRotate);
		p.setProperty(id + ".ruleopt.lockresetLimitMove", lockresetLimitMove);
		p.setProperty(id + ".ruleopt.lockresetLimitRotate", lockresetLimitRotate);
		p.setProperty(id + ".ruleopt.lockresetLimitShareCount", lockresetLimitShareCount);
		p.setProperty(id + ".ruleopt.lockresetLimitOver", lockresetLimitOver);

		p.setProperty(id + ".ruleopt.lockflash", lockflash);
		p.setProperty(id + ".ruleopt.lockflashOnlyFrame", lockflashOnlyFrame);
		p.setProperty(id + ".ruleopt.lockflashBeforeLineClear", lockflashBeforeLineClear);
		p.setProperty(id + ".ruleopt.areCancelMove", areCancelMove);
		p.setProperty(id + ".ruleopt.areCancelRotate", areCancelRotate);
		p.setProperty(id + ".ruleopt.areCancelHold", areCancelHold);

		p.setProperty(id + ".ruleopt.minARE", minARE);
		p.setProperty(id + ".ruleopt.maxARE", maxARE);
		p.setProperty(id + ".ruleopt.minARELine", minARELine);
		p.setProperty(id + ".ruleopt.maxARELine", maxARELine);
		p.setProperty(id + ".ruleopt.minLineDelay", minLineDelay);
		p.setProperty(id + ".ruleopt.maxLineDelay", maxLineDelay);
		p.setProperty(id + ".ruleopt.minLockDelay", minLockDelay);
		p.setProperty(id + ".ruleopt.maxLockDelay", maxLockDelay);
		p.setProperty(id + ".ruleopt.minDAS", minDAS);
		p.setProperty(id + ".ruleopt.maxDAS", maxDAS);

		p.setProperty(id + ".ruleopt.dasDelay", dasDelay);

		p.setProperty(id + ".ruleopt.shiftLockEnable", shiftLockEnable);

		p.setProperty(id + ".ruleopt.dasInReady", dasInReady);
		p.setProperty(id + ".ruleopt.dasInMoveFirstFrame", dasInMoveFirstFrame);
		p.setProperty(id + ".ruleopt.dasInLockFlash", dasInLockFlash);
		p.setProperty(id + ".ruleopt.dasInLineClear", dasInLineClear);
		p.setProperty(id + ".ruleopt.dasInARE", dasInARE);
		p.setProperty(id + ".ruleopt.dasInARELastFrame", dasInARELastFrame);
		p.setProperty(id + ".ruleopt.dasInEndingStart", dasInEndingStart);
		p.setProperty(id + ".ruleopt.dasOnBlockedMove", dasChargeOnBlockedMove);
		p.setProperty(id + ".ruleopt.dasStoreChargeOnNeutral", dasStoreChargeOnNeutral);
		p.setProperty(id + ".ruleopt.dasRedirectInARE", dasRedirectInDelay);

		p.setProperty(id + ".ruleopt.moveFirstFrame", moveFirstFrame);
		p.setProperty(id + ".ruleopt.moveDiagonal", moveDiagonal);
		p.setProperty(id + ".ruleopt.moveUpAndDown", moveUpAndDown);
		p.setProperty(id + ".ruleopt.moveLeftAndRightAllow", moveLeftAndRightAllow);
		p.setProperty(id + ".ruleopt.moveLeftAndRightUsePreviousInput", moveLeftAndRightUsePreviousInput);

		p.setProperty(id + ".ruleopt.lineFallAnim", lineFallAnim);
		p.setProperty(id + ".ruleopt.lineCancelMove", lineCancelMove);
		p.setProperty(id + ".ruleopt.lineCancelRotate", lineCancelRotate);
		p.setProperty(id + ".ruleopt.lineCancelHold", lineCancelHold);

		p.setProperty(id + ".ruleopt.skin", skin);
		p.setProperty(id + ".ruleopt.ghost", ghost);
	}

	/**
	 * プロパティセットから読み込み
	 * @param p プロパティセット
	 * @param id Player IDまたはPresetID
	 */
	public void readProperty(CustomProperties p, int id) {
		strRuleName = p.getProperty(id + ".ruleopt.strRuleName", strRuleName);
		strWallkick = p.getProperty(id + ".ruleopt.strWallkick", strWallkick);
		strRandomizer = p.getProperty(id + ".ruleopt.strRandomizer", strRandomizer);

		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			for(int j = 0; j < Piece.DIRECTION_COUNT; j++) {
				pieceOffsetX[i][j] = p.getProperty(id + ".ruleopt.pieceOffsetX." + i + "." + j, pieceOffsetX[i][j]);
				pieceOffsetY[i][j] = p.getProperty(id + ".ruleopt.pieceOffsetY." + i + "." + j, pieceOffsetY[i][j]);
				pieceSpawnX[i][j] = p.getProperty(id + ".ruleopt.pieceSpawnX." + i + "." + j, pieceSpawnX[i][j]);
				pieceSpawnY[i][j] = p.getProperty(id + ".ruleopt.pieceSpawnY." + i + "." + j, pieceSpawnY[i][j]);
				pieceSpawnXBig[i][j] = p.getProperty(id + ".ruleopt.pieceSpawnXBig." + i + "." + j, pieceSpawnXBig[i][j]);
				pieceSpawnYBig[i][j] = p.getProperty(id + ".ruleopt.pieceSpawnYBig." + i + "." + j, pieceSpawnYBig[i][j]);
			}
			pieceColor[i] = p.getProperty(id + ".ruleopt.pieceColor." + i, pieceColor[i]);
			pieceDefaultDirection[i] = p.getProperty(id + ".ruleopt.pieceDefaultDirection." + i, pieceDefaultDirection[i]);
		}
		pieceEnterAboveField = p.getProperty(id + ".ruleopt.pieceEnterAboveField", pieceEnterAboveField);
		pieceEnterMaxDistanceY = p.getProperty(id + ".ruleopt.pieceEnterMaxDistanceY", pieceEnterMaxDistanceY);

		fieldWidth = p.getProperty(id + ".ruleopt.fieldWidth", fieldWidth);
		fieldHeight = p.getProperty(id + ".ruleopt.fieldHeight", fieldHeight);
		fieldHiddenHeight = p.getProperty(id + ".ruleopt.fieldHiddenHeight", fieldHiddenHeight);
		fieldCeiling = p.getProperty(id + ".ruleopt.fieldCeiling", fieldCeiling);
		fieldLockoutDeath = p.getProperty(id + ".ruleopt.fieldLockoutDeath", fieldLockoutDeath);
		fieldPartialLockoutDeath = p.getProperty(id + ".ruleopt.fieldPartialLockoutDeath", fieldPartialLockoutDeath);

		nextDisplay = p.getProperty(id + ".ruleopt.nextDisplay", nextDisplay);

		holdEnable = p.getProperty(id + ".ruleopt.holdEnable", holdEnable);
		holdInitial = p.getProperty(id + ".ruleopt.holdInitial", holdInitial);
		holdInitialLimit = p.getProperty(id + ".ruleopt.holdInitialLimit", holdInitialLimit);
		holdResetDirection = p.getProperty(id + ".ruleopt.holdResetDirection", holdResetDirection);
		holdLimit = p.getProperty(id + ".ruleopt.holdLimit", holdLimit);

		harddropEnable = p.getProperty(id + ".ruleopt.harddropEnable", harddropEnable);
		harddropLock = p.getProperty(id + ".ruleopt.harddropLock", harddropLock);
		harddropLimit = p.getProperty(id + ".ruleopt.harddropLimit", harddropLimit);

		softdropEnable = p.getProperty(id + ".ruleopt.softdropEnable", softdropEnable);
		softdropLock = p.getProperty(id + ".ruleopt.softdropLock", softdropLock);
		softdropLimit = p.getProperty(id + ".ruleopt.softdropLimit", softdropLimit);
		softdropSurfaceLock = p.getProperty(id + ".ruleopt.softdropSurfaceLock", softdropSurfaceLock);
		softdropSpeed = p.getProperty(id + ".ruleopt.softdropSpeed", softdropSpeed);
		softdropMultiplyNativeSpeed = p.getProperty(id + ".ruleopt.softdropMultiplyNativeSpeed", softdropMultiplyNativeSpeed);

		rotateInitial = p.getProperty(id + ".ruleopt.rotateInitial", rotateInitial);
		rotateInitialLimit = p.getProperty(id + ".ruleopt.rotateInitialLimit", rotateInitialLimit);
		rotateWallkick = p.getProperty(id + ".ruleopt.rotateWallkick", rotateWallkick);
		rotateInitialWallkick = p.getProperty(id + ".ruleopt.rotateInitialWallkick", rotateInitialWallkick);
		rotateMaxUpwardWallkick = p.getProperty(id + ".ruleopt.rotateMaxUpwardWallkick", rotateMaxUpwardWallkick);
		rotateButtonDefaultRight = p.getProperty(id + ".ruleopt.rotateButtonDefaultRight", rotateButtonDefaultRight);
		rotateButtonAllowReverse = p.getProperty(id + ".ruleopt.rotateButtonAllowReverse", rotateButtonAllowReverse);
		rotateButtonAllowDouble = p.getProperty(id + ".ruleopt.rotateButtonAllowDouble", rotateButtonAllowDouble);

		lockresetFall = p.getProperty(id + ".ruleopt.lockresetFall", lockresetFall);
		lockresetMove = p.getProperty(id + ".ruleopt.lockresetMove", lockresetMove);
		lockresetRotate = p.getProperty(id + ".ruleopt.lockresetRotate", lockresetRotate);
		lockresetLimitMove = p.getProperty(id + ".ruleopt.lockresetLimitMove", lockresetLimitMove);
		lockresetLimitRotate = p.getProperty(id + ".ruleopt.lockresetLimitRotate", lockresetLimitRotate);
		lockresetLimitShareCount = p.getProperty(id + ".ruleopt.lockresetLimitShareCount", lockresetLimitShareCount);
		lockresetLimitOver = p.getProperty(id + ".ruleopt.lockresetLimitOver", lockresetLimitOver);

		lockflash = p.getProperty(id + ".ruleopt.lockflash", lockflash);
		lockflashOnlyFrame = p.getProperty(id + ".ruleopt.lockflashOnlyFrame", lockflashOnlyFrame);
		lockflashBeforeLineClear = p.getProperty(id + ".ruleopt.lockflashBeforeLineClear", lockflashBeforeLineClear);
		areCancelMove = p.getProperty(id + ".ruleopt.areCancelMove", areCancelMove);
		areCancelRotate = p.getProperty(id + ".ruleopt.areCancelRotate", areCancelRotate);
		areCancelHold = p.getProperty(id + ".ruleopt.areCancelHold", areCancelHold);

		minARE = p.getProperty(id + ".ruleopt.minARE", minARE);
		maxARE = p.getProperty(id + ".ruleopt.maxARE", maxARE);
		minARELine = p.getProperty(id + ".ruleopt.minARELine", minARELine);
		maxARELine = p.getProperty(id + ".ruleopt.maxARELine", maxARELine);
		minLineDelay = p.getProperty(id + ".ruleopt.minLineDelay", minLineDelay);
		maxLineDelay = p.getProperty(id + ".ruleopt.maxLineDelay", maxLineDelay);
		minLockDelay = p.getProperty(id + ".ruleopt.minLockDelay", minLockDelay);
		maxLockDelay = p.getProperty(id + ".ruleopt.maxLockDelay", maxLockDelay);
		minDAS = p.getProperty(id + ".ruleopt.minDAS", minDAS);
		maxDAS = p.getProperty(id + ".ruleopt.maxDAS", maxDAS);

		dasDelay = p.getProperty(id + ".ruleopt.dasDelay", dasDelay);
		shiftLockEnable = p.getProperty(id + ".ruleopt.shiftLockEnable", shiftLockEnable);

		dasInReady = p.getProperty(id + ".ruleopt.dasInReady", dasInReady);
		dasInMoveFirstFrame = p.getProperty(id + ".ruleopt.dasInMoveFirstFrame", dasInMoveFirstFrame);
		dasInLockFlash = p.getProperty(id + ".ruleopt.dasInLockFlash", dasInLockFlash);
		dasInLineClear = p.getProperty(id + ".ruleopt.dasInLineClear", dasInLineClear);
		dasInARE = p.getProperty(id + ".ruleopt.dasInARE", dasInARE);
		dasInARELastFrame = p.getProperty(id + ".ruleopt.dasInARELastFrame", dasInARELastFrame);
		dasInEndingStart = p.getProperty(id + ".ruleopt.dasInEndingStart", dasInEndingStart);
		dasChargeOnBlockedMove = p.getProperty(id + ".ruleopt.dasOnBlockedMove", dasChargeOnBlockedMove);
		dasStoreChargeOnNeutral = p.getProperty(id + ".ruleopt.dasStoreChargeOnNeutral", dasStoreChargeOnNeutral);
		dasRedirectInDelay = p.getProperty(id + ".ruleopt.dasRedirectInARE", dasRedirectInDelay);

		moveFirstFrame = p.getProperty(id + ".ruleopt.moveFirstFrame", moveFirstFrame);
		moveDiagonal = p.getProperty(id + ".ruleopt.moveDiagonal", moveDiagonal);
		moveUpAndDown = p.getProperty(id + ".ruleopt.moveUpAndDown", moveUpAndDown);
		moveLeftAndRightAllow = p.getProperty(id + ".ruleopt.moveLeftAndRightAllow", moveLeftAndRightAllow);
		moveLeftAndRightUsePreviousInput = p.getProperty(id + ".ruleopt.moveLeftAndRightUsePreviousInput", moveLeftAndRightUsePreviousInput);

		lineFallAnim = p.getProperty(id + ".ruleopt.lineFallAnim", lineFallAnim);
		lineCancelMove = p.getProperty(id + ".ruleopt.lineCancelMove", lineCancelMove);
		lineCancelRotate = p.getProperty(id + ".ruleopt.lineCancelRotate", lineCancelRotate);
		lineCancelHold = p.getProperty(id + ".ruleopt.lineCancelHold", lineCancelHold);

		skin = p.getProperty(id + ".ruleopt.skin", skin);
		ghost = p.getProperty(id + ".ruleopt.ghost", ghost);
	}
}
