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
 * Setting the rules of the game data
 */
public class RuleOptions implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = 5781310758989780350L;

	/** Lateral motion counterOrrotation counterExceeded the fixed timeTo disable the reset */
	public static final int LOCKRESET_LIMIT_OVER_NORESET = 0;

	/** Lateral motion counterOrrotation counterI fixed the excess is immediately */
	public static final int LOCKRESET_LIMIT_OVER_INSTANT = 1;

	/** Lateral motion counterOrrotation counterI exceeded theWallkickDisable */
	public static final int LOCKRESET_LIMIT_OVER_NOWALLKICK = 2;

	/** Of this ruleName */
	public String strRuleName;

	/** UseWallkickThe class name of the algorithm (If an empty stringWallkickNot) */
	public String strWallkick;

	/** The class name of the order of appearance correction algorithm to be used (If an empty string completely random) */
	public String strRandomizer;

	/** Game Style */
	public int style;

	/** BlockOf PeacerotationPatternX-coordinateCorrection (11Peace ×4Direction) */
	public int[][] pieceOffsetX;

	/** BlockOf PeacerotationPatternY-coordinateCorrection (11Peace ×4Direction) */
	public int[][] pieceOffsetY;

	/** BlockAppearance of the pieceX-coordinateCorrection (11Peace ×4Direction) */
	public int[][] pieceSpawnX;

	/** BlockAppearance of the pieceY-coordinateCorrection (11Peace ×4Direction) */
	public int[][] pieceSpawnY;

	/** BlockOf PeaceBigAppear whenX-coordinateCorrection (11Peace ×4Direction) */
	public int[][] pieceSpawnXBig;

	/** BlockOf PeaceBigAppear whenY-coordinateCorrection (11Peace ×4Direction) */
	public int[][] pieceSpawnYBig;

	/** BlockPeace color */
	public int[] pieceColor;

	/** BlockThe initial pieceDirection */
	public int[] pieceDefaultDirection;

	/** fieldEmerge from the above */
	public boolean pieceEnterAboveField;

	/** When the planned site appearance is buriedY-coordinateSlide on theMaximum count */
	public int pieceEnterMaxDistanceY;

	/** fieldThe width of the */
	public int fieldWidth;

	/** Field height */
	public int fieldHeight;

	/** fieldThe height of the invisible part of the above */
	public int fieldHiddenHeight;

	/** fieldPresence or absence of a ceiling of */
	public boolean fieldCeiling;

	/** fieldWhether you die did not put in the frame */
	public boolean fieldLockoutDeath;

	/** fieldWhether you die alone protruding into Attempts off target */
	public boolean fieldPartialLockoutDeath;

	/** NEXTOfcount */
	public int nextDisplay;

	/** Availability Hold */
	public boolean holdEnable;

	/** Hold preceding */
	public boolean holdInitial;

	/** Can not hold prior continuous use */
	public boolean holdInitialLimit;

	/** When using the holdBlockThe orientation of the piece back to its initial state */
	public boolean holdResetDirection;

	/** You can hold count (-1:Limitless) */
	public int holdLimit;

	/** Hard dropAvailability */
	public boolean harddropEnable;

	/** Hard dropImmediately fixed */
	public boolean harddropLock;

	/** Hard dropNot continuous use */
	public boolean harddropLimit;

	/** Soft dropAvailability */
	public boolean softdropEnable;

	/** Soft dropImmediately fixed */
	public boolean softdropLock;

	/** Soft dropNot continuous use */
	public boolean softdropLimit;

	/** In the ground stateSoft dropThen immediately fixed */
	public boolean softdropSurfaceLock;

	/** Soft dropSpeed (1.0f=1G, 0.5f=0.5G) */
	public float softdropSpeed;

	/** Soft dropSpeedCurrent × normal speednTo double */
	public boolean softdropMultiplyNativeSpeed;

	/** Use new soft drop codes */
	public boolean softdropGravitySpeedLimit;

	/** Precedingrotation */
	public boolean rotateInitial;

	/** PrecedingrotationNot continuous use */
	public boolean rotateInitialLimit;

	/** Wallkick */
	public boolean rotateWallkick;

	/** PrecedingrotationButWallkickMake */
	public boolean rotateInitialWallkick;

	/** TopDirectionToWallkickYou count (-1:Infinite) */
	public int rotateMaxUpwardWallkick;

	/** falseLeft is positive ifrotation, When true,Right is positiverotation */
	public boolean rotateButtonDefaultRight;

	/** ReverserotationAllow (falseIf positiverotationThe same as the) */
	public boolean rotateButtonAllowReverse;

	/** 180-degree rotationAllow (falseIf positiverotationThe same as the) */
	public boolean rotateButtonAllowDouble;

	/** In the fall fixing timeReset */
	public boolean lockresetFall;

	/** Move fixed timeReset */
	public boolean lockresetMove;

	/** rotationFixed at timeReset */
	public boolean lockresetRotate;

	/** Lock delay reset on wallkick */
	public boolean lockresetWallkick;

	/** Lateral motion countLimit (-1:Infinite) */
	public int lockresetLimitMove;

	/** rotation countLimit (-1:Infinite) */
	public int lockresetLimitRotate;

	/** Lateral motion counterAndrotation counterShare (Lateral motion counterI use only) */
	public boolean lockresetLimitShareCount;

	/** Lateral motion counterOrrotation counterHappens when you exceed the (LOCKRESET_LIMIT_OVER_Begins with a constantcountI use) */
	public int lockresetLimitOver;

	/** Shining moment fixed frame count */
	public int lockflash;

	/** BlockDedicated shines frame Put */
	public boolean lockflashOnlyFrame;

	/** Line clearBeforeBlockShine frame Put */
	public boolean lockflashBeforeLineClear;

	/** ARE cancel on move */
	public boolean areCancelMove;

	/** ARE cancel on rotate*/
	public boolean areCancelRotate;

	/** ARE cancel on hold*/
	public boolean areCancelHold;

	/** Minimum/MaximumARE (-1:Unspecified) */
	public int minARE, maxARE;

	/** Minimum/MaximumARE after line clear (-1:Unspecified) */
	public int minARELine, maxARELine;

	/** Minimum/MaximumLine clear time (-1:Unspecified) */
	public int minLineDelay, maxLineDelay;

	/** Minimum/MaximumFixation time (-1:Unspecified) */
	public int minLockDelay, maxLockDelay;

	/** Minimum/MaximumHorizontal reservoir time (-1:Unspecified) */
	public int minDAS, maxDAS;

	/** Lateral movement interval */
	public int dasDelay;

	public boolean shiftLockEnable;

	/** ReadyCan accumulate on the screen next to */
	public boolean dasInReady;

	/** First frame Can accumulate in the horizontal */
	public boolean dasInMoveFirstFrame;

	/** BlockPossible reservoir beside the moment it shines */
	public boolean dasInLockFlash;

	/** Line clearCan I accumulate in horizontal */
	public boolean dasInLineClear;

	/** ARECan I accumulate in horizontal */
	public boolean dasInARE;

	/** AREAt the end of the frame Can accumulate in the horizontal */
	public boolean dasInARELastFrame;

	/** EndingCan accumulate on the screen next to the inrush */
	public boolean dasInEndingStart;

	/** Charge DAS on blocked move */
	public boolean dasChargeOnBlockedMove;

	/** Leave DAS charge alone when left/right are not held -- useful with dasRedirectInDelay **/
   public boolean dasStoreChargeOnNeutral;

   /** Allow direction changes during delays without zeroing DAS charge **/
   public boolean dasRedirectInDelay;

	/** First frame Can be moved in */
	public boolean moveFirstFrame;

	/** Diagonal movement */
	public boolean moveDiagonal;

	/** Permit simultaneous push up and down */
	public boolean moveUpAndDown;

	/** Simultaneously pressing the left and right permit */
	public boolean moveLeftAndRightAllow;

	/** Before when I press the left and right simultaneously frame Of input DirectionGive priority to (Preferred to ignore the left and right while holding down the left and press the right) */
	public boolean moveLeftAndRightUsePreviousInput;

	/** Line clearOn afterBlockThe1View the animation step by step fall */
	public boolean lineFallAnim;

	/** Line delay cancel on move */
	public boolean lineCancelMove;

	/** Line delay cancel on rotate */
	public boolean lineCancelRotate;

	/** Line delay cancel on hold */
	public boolean lineCancelHold;

	/** BlockPicture of */
	public int skin;

	/** ghost Presence or absence of (falseIfMode At theghost A is enabledI hide, even if you) */
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

		style = 0;

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
		softdropGravitySpeedLimit = false;

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
		lockresetWallkick = false;
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
	 * OtherRuleParamCopy the contents of the
	 * @param r Copy sourceOfRuleParam
	 */
	public void copy(RuleOptions r) {
		strRuleName = r.strRuleName;
		strWallkick = r.strWallkick;
		strRandomizer = r.strRandomizer;

		style = r.style;

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
		softdropGravitySpeedLimit = r.softdropGravitySpeedLimit;

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
		lockresetWallkick = r.lockresetWallkick;
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
	 * Compared with other rules, If the sametrueReturns
	 * @param r Rules to compare
	 * @param ignoreGraphicsSetting trueIgnore the settings that do not affect the game itself and to
	 * @return If compared to the same rulestrue
	 */
	public boolean compare(RuleOptions r, boolean ignoreGraphicsSetting) {
		if((!ignoreGraphicsSetting) && (strRuleName != r.strRuleName)) return false;
		if(strWallkick != r.strWallkick) return false;
		if(strRandomizer != r.strRandomizer) return false;

		if(style != r.style) return false;

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
		if(softdropGravitySpeedLimit != r.softdropGravitySpeedLimit) return false;

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
		if(lockresetWallkick != r.lockresetWallkick) return false;
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
	 * Stored in the property set
	 * @param p Property Set
	 * @param id Player IDOrPresetID
	 */
	public void writeProperty(CustomProperties p, int id) {
		p.setProperty(id + ".ruleopt.strRuleName", strRuleName);
		p.setProperty(id + ".ruleopt.strWallkick", strWallkick);
		p.setProperty(id + ".ruleopt.strRandomizer", strRandomizer);

		p.setProperty(id + ".ruleopt.style", style);

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
		p.setProperty(id + ".ruleopt.softdropGravitySpeedLimit", softdropGravitySpeedLimit);

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
		p.setProperty(id + ".ruleopt.lockresetWallkick", lockresetWallkick);
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
	 * Read from the property set
	 * @param p Property Set
	 * @param id Player IDOrPresetID
	 */
	public void readProperty(CustomProperties p, int id) {
		strRuleName = p.getProperty(id + ".ruleopt.strRuleName", strRuleName);
		strWallkick = p.getProperty(id + ".ruleopt.strWallkick", strWallkick);
		strRandomizer = p.getProperty(id + ".ruleopt.strRandomizer", strRandomizer);

		style = p.getProperty(id + ".ruleopt.style", 0);

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
		softdropGravitySpeedLimit = p.getProperty(id + ".ruleopt.softdropGravitySpeedLimit", softdropGravitySpeedLimit);

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
		lockresetWallkick = p.getProperty(id + ".ruleopt.lockresetWallkick", lockresetWallkick);
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
