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
package org.game_host.hebo.nullpomino.game.subsystem.mode;

import org.game_host.hebo.nullpomino.game.component.Block;
import org.game_host.hebo.nullpomino.game.component.Controller;
import org.game_host.hebo.nullpomino.game.event.EventReceiver;
import org.game_host.hebo.nullpomino.game.play.GameEngine;
import org.game_host.hebo.nullpomino.game.play.GameManager;
import org.game_host.hebo.nullpomino.util.CustomProperties;
import org.game_host.hebo.nullpomino.util.GeneralUtil;

/**
 * GARBAGE MANIAモード
 */
public class GarbageManiaMode extends DummyMode {
	/** 現在のバージョン */
	private static final int CURRENT_VERSION = 3;

	/** 落下速度テーブル */
	private static final int[] tableGravityValue =
	{
		4, 6, 8, 10, 12, 16, 32, 48, 64, 80, 96, 112, 128, 144, 4, 32, 64, 96, 128, 160, 192, 224, 256, 512, 768, 1024, 1280, 1024, 768, -1
	};

	/** 落下速度が変わるレベル */
	private static final int[] tableGravityChangeLevel =
	{
		30, 35, 40, 50, 60, 70, 80, 90, 100, 120, 140, 160, 170, 200, 220, 230, 233, 236, 239, 243, 247, 251, 300, 330, 360, 400, 420, 450, 500, 10000
	};

	/** BGMがフェードアウトするレベル */
	private static final int[] tableBGMFadeout = {495,695,880,-1};

	/** BGMが変わるレベル */
	private static final int[] tableBGMChange  = {500,700,900,-1};

	/** 裏段位の名前 */
	private static final String[] tableSecretGradeName =
	{
		 "9",  "8",  "7",  "6",  "5",  "4",  "3",  "2",  "1",	//  0～ 8
		"S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9",	//  9～17
		"GM"													// 18
	};

	/** LV999ロールの時間 */
	private static final int ROLLTIMELIMIT = 2024;

	/** ランキングに記録する数 */
	private static final int RANKING_MAX = 10;

	/** 最大セクション数 */
	private static final int SECTION_MAX = 10;

	/** デフォルトのセクションタイム */
	private static final int DEFAULT_SECTION_TIME = 5400;

	/** せり上がりパターン */
	private static final int[][] tableGarbagePattern =
	{
		{0,1,1,1,1,1,1,1,1,1},
		{0,1,1,1,1,1,1,1,1,1},
		{0,1,1,1,1,1,1,1,1,1},
		{0,1,1,1,1,1,1,1,1,1},
		{1,1,1,1,1,1,1,1,1,0},
		{1,1,1,1,1,1,1,1,1,0},
		{1,1,1,1,1,1,1,1,1,0},
		{1,1,1,1,1,1,1,1,1,0},
		{0,0,1,1,1,1,1,1,1,1},
		{0,1,1,1,1,1,1,1,1,1},
		{0,1,1,1,1,1,1,1,1,1},
		{1,1,1,1,1,1,1,1,0,0},
		{1,1,1,1,1,1,1,1,1,0},
		{1,1,1,1,1,1,1,1,1,0},
		{1,1,0,1,1,1,1,1,1,1},
		{1,0,0,1,1,1,1,1,1,1},
		{1,0,1,1,1,1,1,1,1,1},
		{1,1,1,1,1,1,1,0,1,1},
		{1,1,1,1,1,1,1,0,0,1},
		{1,1,1,1,1,1,1,1,0,1},
		{1,1,1,1,0,0,1,1,1,1},
		{1,1,1,1,0,0,1,1,1,1},
		{1,1,1,1,0,1,1,1,1,1},
		{1,1,1,0,0,0,1,1,1,1},
	};

	/** BIG用せり上がりパターン */
	private static final int[][] tableGarbagePatternBig =
	{
		{0,1,1,1,1},
		{0,1,1,1,1},
		{0,1,1,1,1},
		{0,1,1,1,1},
		{1,1,1,1,0},
		{1,1,1,1,0},
		{1,1,1,1,0},
		{1,1,1,1,0},
		{0,0,1,1,1},
		{0,1,1,1,1},
		{0,1,1,1,1},
		{1,1,1,0,0},
		{1,1,1,1,0},
		{1,1,1,1,0},
		{1,1,0,1,1},
		{1,0,0,1,1},
		{1,0,1,1,1},
		{1,1,0,1,1},
		{1,1,0,0,1},
		{1,1,1,0,1},
		{1,0,0,1,1},
		{1,0,0,1,1},
		{1,1,0,1,1},
		{1,0,0,0,1},
	};

	/** このモードを所有するGameManager */
	private GameManager owner;

	/** 描画などのイベント処理 */
	private EventReceiver receiver;

	/** 現在の落下速度の番号（tableGravityChangeLevelのレベルに到達するたびに1つ増える） */
	private int gravityindex;

	/** 次のセクションのレベル（これ-1のときにレベルストップする） */
	private int nextseclv;

	/** レベルが増えたフラグ */
	private boolean lvupflag;

	/** ハードドロップした段数 */
	private int harddropBonus;

	/** コンボボーナス */
	private int comboValue;

	/** 直前に手に入れたスコア */
	private int lastscore;

	/** 獲得スコア表示がされる残り時間 */
	private int scgettime;

	/** ロール経過時間 */
	private int rolltime;

	/** 裏段位 */
	private int secretGrade;

	/** 現在のBGM */
	private int bgmlv;

	/** セクションタイム */
	private int[] sectiontime;

	/** 新記録が出たセクションはtrue */
	private boolean[] sectionIsNewRecord;

	/** どこかのセクションで新記録を出すとtrue */
	private boolean sectionAnyNewRecord;

	/** クリアしたセクション数 */
	private int sectionscomp;

	/** 平均セクションタイム */
	private int sectionavgtime;

	/** せり上がりパターン番号 */
	private int garbagePos;

	/** せり上がり用カウンタ（ラインを消さないと+1） */
	private int garbageCount;

	/** せり上がりした回数 */
	private int garbageTotal;

	/** セクションタイム記録表示中ならtrue */
	private boolean isShowBestSectionTime;

	/** 開始時のレベル */
	private int startlevel;

	/** trueなら常にゴーストON */
	private boolean alwaysghost;

	/** trueなら常に20G */
	private boolean always20g;

	/** trueならレベルストップ音有効 */
	private boolean lvstopse;

	/** ビッグモード */
	private boolean big;

	/** trueならセクションタイム表示有効 */
	private boolean showsectiontime;

	/** バージョン */
	private int version;

	/** 今回のプレイのランキングでのランク */
	private int rankingRank;

	/** ランキングのレベル */
	private int[] rankingLevel;

	/** ランキングのタイム */
	private int[] rankingTime;

	/** セクションタイム記録 */
	private int[] bestSectionTime;

	/*
	 * モード名
	 */
	@Override
	public String getName() {
		return "GARBAGE MANIA";
	}

	/*
	 * 初期化
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;

		gravityindex = 0;
		nextseclv = 0;
		lvupflag = true;
		harddropBonus = 0;
		comboValue = 0;
		lastscore = 0;
		scgettime = 0;
		rolltime = 0;
		secretGrade = 0;
		bgmlv = 0;
		sectiontime = new int[SECTION_MAX];
		sectionIsNewRecord = new boolean[SECTION_MAX];
		sectionAnyNewRecord = false;
		sectionscomp = 0;
		sectionavgtime = 0;
		garbagePos = 0;
		garbageCount = 0;
		garbageTotal = 0;
		isShowBestSectionTime = false;
		startlevel = 0;
		alwaysghost = false;
		always20g = false;
		lvstopse = false;
		big = false;

		rankingRank = -1;
		rankingLevel = new int[RANKING_MAX];
		rankingTime = new int[RANKING_MAX];
		bestSectionTime = new int[SECTION_MAX];

		engine.speed.are = 23;
		engine.speed.areLine = 23;
		engine.speed.lineDelay = 40;
		engine.speed.lockDelay = 31;
		engine.speed.das = 15;

		engine.tspinEnable = false;
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DOUBLE;
		engine.bighalf = true;
		engine.bigmove = true;
		engine.staffrollEnable = true;
		engine.staffrollNoDeath = true;

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
			version = owner.replayProp.getProperty("garbagemania.version", 0);
		}

		owner.backgroundStatus.bg = startlevel;
	}

	/**
	 * プロパティファイルから設定を読み込み
	 * @param prop プロパティファイル
	 */
	private void loadSetting(CustomProperties prop) {
		startlevel = prop.getProperty("garbagemania.startlevel", 0);
		alwaysghost = prop.getProperty("garbagemania.alwaysghost", false);
		always20g = prop.getProperty("garbagemania.always20g", false);
		lvstopse = prop.getProperty("garbagemania.lvstopse", false);
		showsectiontime = prop.getProperty("garbagemania.showsectiontime", false);
		big = prop.getProperty("garbagemania.big", false);
	}

	/**
	 * プロパティファイルに設定を保存
	 * @param prop プロパティファイル
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("garbagemania.startlevel", startlevel);
		prop.setProperty("garbagemania.alwaysghost", alwaysghost);
		prop.setProperty("garbagemania.always20g", always20g);
		prop.setProperty("garbagemania.lvstopse", lvstopse);
		prop.setProperty("garbagemania.showsectiontime", showsectiontime);
		prop.setProperty("garbagemania.big", big);
	}

	/**
	 * ゲーム開始時のBGMを設定
	 * @param engine GameEngine
	 */
	private void setStartBgmlv(GameEngine engine) {
		bgmlv = 0;
		while((tableBGMChange[bgmlv] != -1) && (engine.statistics.level >= tableBGMChange[bgmlv])) bgmlv++;
	}

	/**
	 * 落下速度を更新
	 * @param engine GameEngine
	 */
	private void setSpeed(GameEngine engine) {
		if(always20g == true) {
			engine.speed.gravity = -1;
		} else {
			while(engine.statistics.level >= tableGravityChangeLevel[gravityindex]) gravityindex++;
			engine.speed.gravity = tableGravityValue[gravityindex];
		}
	}

	/**
	 * 平均セクションタイムを更新
	 */
	private void setAverageSectionTime() {
		if(sectionscomp > 0) {
			int temp = 0;
			for(int i = startlevel; i < startlevel + sectionscomp; i++) {
				if((i >= 0) && (i < sectiontime.length)) temp += sectiontime[i];
			}
			sectionavgtime = temp / sectionscomp;
		} else {
			sectionavgtime = 0;
		}
	}

	/**
	 * セクションタイム更新処理
	 * @param sectionNumber セクション番号
	 */
	private void stNewRecordCheck(int sectionNumber) {
		if((sectiontime[sectionNumber] < bestSectionTime[sectionNumber]) && (!owner.replayMode)) {
			sectionIsNewRecord[sectionNumber] = true;
			sectionAnyNewRecord = true;
		}
	}

	/*
	 * 設定画面の処理
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// メニュー
		if(engine.owner.replayMode == false) {
			// 上
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
				engine.statc[2]--;
				if(engine.statc[2] < 0) engine.statc[2] = 5;
				engine.playSE("cursor");
			}
			// 下
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
				engine.statc[2]++;
				if(engine.statc[2] > 5) engine.statc[2] = 0;
				engine.playSE("cursor");
			}

			// 設定変更
			int change = 0;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT)) change = -1;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) change = 1;

			if(change != 0) {
				engine.playSE("change");

				switch(engine.statc[2]) {
				case 0:
					startlevel += change;
					if(startlevel < 0) startlevel = 9;
					if(startlevel > 9) startlevel = 0;
					owner.backgroundStatus.bg = startlevel;
					break;
				case 1:
					alwaysghost = !alwaysghost;
					break;
				case 2:
					always20g = !always20g;
					break;
				case 3:
					lvstopse = !lvstopse;
					break;
				case 4:
					showsectiontime = !showsectiontime;
					break;
				case 5:
					big = !big;
					break;
				}
			}

			// セクションタイム表示切替
			if(engine.ctrl.isPush(Controller.BUTTON_F) && (engine.statc[3] >= 5)) {
				engine.playSE("change");
				isShowBestSectionTime = !isShowBestSectionTime;
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");
				saveSetting(owner.modeConfig);
				receiver.saveModeConfig(owner.modeConfig);
				isShowBestSectionTime = false;
				sectionscomp = 0;
				return false;
			}

			// キャンセル
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				engine.quitflag = true;
			}

			engine.statc[3]++;
		} else {
			engine.statc[3]++;
			engine.statc[2] = -1;

			if(engine.statc[3] >= 60) {
				return false;
			}
		}

		return true;
	}

	/*
	 * 設定画面の描画処理
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if(engine.owner.replayMode == false) {
			receiver.drawMenuFont(engine, playerID, 0, (engine.statc[2] * 2) + 1, "b", EventReceiver.COLOR_RED);
		}

		receiver.drawMenuFont(engine, playerID, 0, 0, "LEVEL", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 1, String.valueOf(startlevel * 100), (engine.statc[2] == 0));
		receiver.drawMenuFont(engine, playerID, 0, 2, "FULL GHOST", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 3, GeneralUtil.getONorOFF(alwaysghost), (engine.statc[2] == 1));
		receiver.drawMenuFont(engine, playerID, 0, 4, "20G MODE", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 5, GeneralUtil.getONorOFF(always20g), (engine.statc[2] == 2));
		receiver.drawMenuFont(engine, playerID, 0, 6, "LVSTOPSE", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 7, GeneralUtil.getONorOFF(lvstopse), (engine.statc[2] == 3));
		receiver.drawMenuFont(engine, playerID, 0, 8, "SHOW STIME", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 9, GeneralUtil.getONorOFF(showsectiontime), (engine.statc[2] == 4));
		receiver.drawMenuFont(engine, playerID, 0, 10, "BIG", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 11, GeneralUtil.getONorOFF(big), (engine.statc[2] == 5));
	}

	/*
	 * ゲーム開始時の処理
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.statistics.level = startlevel * 100;

		nextseclv = engine.statistics.level + 100;
		if(engine.statistics.level < 0) nextseclv = 100;
		if(engine.statistics.level >= 900) nextseclv = 999;

		owner.backgroundStatus.bg = engine.statistics.level / 100;

		engine.big = big;

		setSpeed(engine);
		setStartBgmlv(engine);
		owner.bgmStatus.bgm = bgmlv;
	}

	/*
	 * スコア表示
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "GARBAGE MANIA", EventReceiver.COLOR_CYAN);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (startlevel == 0) && (big == false) && (always20g == false) && (engine.ai == null)) {
				if(!isShowBestSectionTime) {
					// ランキング
					receiver.drawScoreFont(engine, playerID, 3, 2, "LEVEL TIME", EventReceiver.COLOR_BLUE);

					for(int i = 0; i < RANKING_MAX; i++) {
						receiver.drawScoreFont(engine, playerID, 0, 3 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
						receiver.drawScoreFont(engine, playerID, 3, 3 + i, String.valueOf(rankingLevel[i]), (i == rankingRank));
						receiver.drawScoreFont(engine, playerID, 9, 3 + i, GeneralUtil.getTime(rankingTime[i]), (i == rankingRank));
					}

					receiver.drawScoreFont(engine, playerID, 0, 17, "F:VIEW SECTION TIME", EventReceiver.COLOR_GREEN);
				} else {
					// セクションタイム
					receiver.drawScoreFont(engine, playerID, 0, 2, "SECTION TIME", EventReceiver.COLOR_BLUE);

					int totalTime = 0;
					for(int i = 0; i < SECTION_MAX; i++) {
						int temp = Math.min(i * 100, 999);
						int temp2 = Math.min(((i + 1) * 100) - 1, 999);

						String strSectionTime;
						strSectionTime = String.format("%3d-%3d %s", temp, temp2, GeneralUtil.getTime(bestSectionTime[i]));

						receiver.drawScoreFont(engine, playerID, 0, 3 + i, strSectionTime, sectionIsNewRecord[i]);

						totalTime += bestSectionTime[i];
					}

					receiver.drawScoreFont(engine, playerID, 0, 14, "TOTAL", EventReceiver.COLOR_BLUE);
					receiver.drawScoreFont(engine, playerID, 0, 15, GeneralUtil.getTime(totalTime));
					receiver.drawScoreFont(engine, playerID, 9, 14, "AVERAGE", EventReceiver.COLOR_BLUE);
					receiver.drawScoreFont(engine, playerID, 9, 15, GeneralUtil.getTime(totalTime / SECTION_MAX));

					receiver.drawScoreFont(engine, playerID, 0, 17, "F:VIEW RANKING", EventReceiver.COLOR_GREEN);
				}
			}
		} else {
			//receiver.drawScoreFont(engine, playerID, 0, 2, "GARBAGE", EventReceiver.COLOR_BLUE);
			//receiver.drawScoreFont(engine, playerID, 0, 3, "" + garbageCount + ":" + garbagePos);

			// スコア
			receiver.drawScoreFont(engine, playerID, 0, 5, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore;
			if((lastscore == 0) || (scgettime <= 0)) {
				strScore = String.valueOf(engine.statistics.score);
			} else {
				strScore = String.valueOf(engine.statistics.score) + "\n(+" + String.valueOf(lastscore) + ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 6, strScore);

			// レベル
			receiver.drawScoreFont(engine, playerID, 0, 9, "LEVEL", EventReceiver.COLOR_BLUE);
			int tempLevel = engine.statistics.level;
			if(tempLevel < 0) tempLevel = 0;
			String strLevel = String.format("%3d", tempLevel);
			receiver.drawScoreFont(engine, playerID, 0, 10, strLevel);

			int speed = engine.speed.gravity / 128;
			if(engine.speed.gravity < 0) speed = 40;
			receiver.drawSpeedMeter(engine, playerID, 0, 11, speed);

			receiver.drawScoreFont(engine, playerID, 0, 12, String.format("%3d", nextseclv));

			// タイム
			receiver.drawScoreFont(engine, playerID, 0, 14, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 15, GeneralUtil.getTime(engine.statistics.time));

			// ロール残り時間
			if((engine.gameActive) && (engine.ending == 2)) {
				int time = ROLLTIMELIMIT - rolltime;
				if(time < 0) time = 0;
				receiver.drawScoreFont(engine, playerID, 0, 17, "ROLL TIME", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 18, GeneralUtil.getTime(time), ((time > 0) && (time < 10 * 60)));
			}

			// セクションタイム
			if((showsectiontime == true) && (sectiontime != null)) {
				receiver.drawScoreFont(engine, playerID, 12, 2, "SECTION TIME", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < sectiontime.length; i++) {
					if(sectiontime[i] > 0) {
						int temp = i * 100;
						if(temp > 999) temp = 999;

						int section = engine.statistics.level / 100;
						String strSeparator = " ";
						if((i == section) && (engine.ending == 0)) strSeparator = "b";

						String strSectionTime;
						strSectionTime = String.format("%3d%s%s", temp, strSeparator, GeneralUtil.getTime(sectiontime[i]));

						receiver.drawScoreFont(engine, playerID, 12, 3 + i, strSectionTime, sectionIsNewRecord[i]);
					}
				}

				if(sectionavgtime > 0) {
					receiver.drawScoreFont(engine, playerID, 12, 14, "AVERAGE", EventReceiver.COLOR_BLUE);
					receiver.drawScoreFont(engine, playerID, 12, 15, GeneralUtil.getTime(sectionavgtime));
				}
			}
		}
	}

	/*
	 * 移動中の処理
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		// 新規ピース出現時
		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false) && (!lvupflag)) {
			// レベルアップ
			if(engine.statistics.level < nextseclv - 1) {
				engine.statistics.level++;
				if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) engine.playSE("levelstop");
			}
			levelUp(engine);

			// ハードドロップボーナス初期化
			harddropBonus = 0;
		}
		if( (engine.ending == 0) && (engine.statc[0] > 0) && ((version >= 2) || (engine.holdDisable == false)) ) {
			lvupflag = false;
		}

		return false;
	}

	/*
	 * ARE中の処理
	 */
	@Override
	public boolean onARE(GameEngine engine, int playerID) {
		// 最後のフレーム
		if((engine.ending == 0) && (engine.statc[0] >= engine.statc[1] - 1) && (!lvupflag)) {
			if(engine.statistics.level < nextseclv - 1) {
				engine.statistics.level++;
				if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) engine.playSE("levelstop");
			}
			levelUp(engine);
			lvupflag = true;
		}

		return false;
	}

	/**
	 * レベルが上がったときの共通処理
	 */
	private void levelUp(GameEngine engine) {
		// メーター
		engine.meterValue = ((engine.statistics.level % 100) * receiver.getMeterMax(engine)) / 99;
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		if(engine.statistics.level % 100 >= 50) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(engine.statistics.level % 100 >= 80) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(engine.statistics.level == nextseclv - 1) engine.meterColor = GameEngine.METER_COLOR_RED;

		// 速度変更
		setSpeed(engine);

		// LV100到達でゴーストを消す
		if((engine.statistics.level >= 100) && (!alwaysghost)) engine.ghost = false;

		// BGMフェードアウト
		if((tableBGMFadeout[bgmlv] != -1) && (engine.statistics.level >= tableBGMFadeout[bgmlv]))
			owner.bgmStatus.fadesw  = true;
	}

	/*
	 * スコア計算
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// コンボ
		if(lines == 0) {
			comboValue = 1;
		} else {
			comboValue = comboValue + (2 * lines) - 2;
			if(comboValue < 1) comboValue = 1;
		}

		if(lines == 0) {
			// せり上がり
			garbageCount++;

			if(garbageCount >= 13 - (engine.statistics.level / 100)) {
				engine.playSE("garbage");

				if((big) && (version >= 3)) {
					engine.field.pushUp(2);

					for(int i = 0; i < tableGarbagePatternBig[garbagePos].length; i++) {
						if(tableGarbagePatternBig[garbagePos][i] != 0) {
							for(int j = 0; j < 2; j++) {
								for(int k = 0; k < 2; k++) {
									Block blk = new Block();
									blk.color = Block.BLOCK_COLOR_GRAY;
									blk.skin = engine.getSkin();
									blk.attribute = Block.BLOCK_ATTRIBUTE_GARBAGE | Block.BLOCK_ATTRIBUTE_VISIBLE | Block.BLOCK_ATTRIBUTE_OUTLINE;
									engine.field.setBlock(i * 2 + k, engine.field.getHeight() - 1 - j, blk);
								}
							}
						}
					}
				} else {
					engine.field.pushUp();

					for(int i = 0; i < tableGarbagePattern[garbagePos].length; i++) {
						if(tableGarbagePattern[garbagePos][i] != 0) {
							Block blk = new Block();
							blk.color = Block.BLOCK_COLOR_GRAY;
							blk.skin = engine.getSkin();
							blk.attribute = Block.BLOCK_ATTRIBUTE_GARBAGE | Block.BLOCK_ATTRIBUTE_VISIBLE | Block.BLOCK_ATTRIBUTE_OUTLINE;
							engine.field.setBlock(i, engine.field.getHeight() - 1, blk);
						}
					}
				}

				garbageTotal++;

				garbagePos++;
				if((garbagePos > tableGarbagePatternBig.length - 1) && (big) && (version >= 3)) garbagePos = 0;
				else if(garbagePos > tableGarbagePattern.length - 1) garbagePos = 0;

				garbageCount = 0;
			}
		}

		if((lines >= 1) && (engine.ending == 0)) {
			// レベルアップ
			int levelb = engine.statistics.level;
			engine.statistics.level += lines;
			levelUp(engine);

			if(engine.statistics.level >= 999) {
				// エンディング
				engine.playSE("endingstart");
				engine.statistics.level = 999;
				engine.timerActive = false;
				engine.ending = 2;

				sectionscomp++;
				setAverageSectionTime();
				stNewRecordCheck(sectionscomp - 1);
			} else if(engine.statistics.level >= nextseclv) {
				// 次のセクション
				engine.playSE("levelup");

				sectionscomp++;
				setAverageSectionTime();
				stNewRecordCheck(sectionscomp - 1);

				// 背景切り替え
				owner.backgroundStatus.fadesw = true;
				owner.backgroundStatus.fadecount = 0;
				owner.backgroundStatus.fadebg = nextseclv / 100;

				// BGM切り替え
				if((tableBGMChange[bgmlv] != -1) && (engine.statistics.level >= tableBGMChange[bgmlv])) {
					bgmlv++;
					owner.bgmStatus.fadesw = false;
					owner.bgmStatus.bgm = bgmlv;
				}

				// 次のセクションレベルを更新
				nextseclv += 100;
				if(nextseclv > 999) nextseclv = 999;
			} else if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) {
				engine.playSE("levelstop");
			}

			// スコア計算
			int manuallock = 0;
			if(engine.manualLock == true) manuallock = 1;

			int bravo = 1;
			if(engine.field.isEmpty()) {
				engine.playSE("bravo");
				bravo = 4;
			}

			int speedBonus = engine.getLockDelay() - engine.statc[0];
			if(speedBonus < 0) speedBonus = 0;

			lastscore = ((levelb + lines)/4 + engine.softdropFall + manuallock + harddropBonus) * lines * comboValue * bravo +
						(engine.statistics.level / 2) + (speedBonus * 7);
			engine.statistics.score += lastscore;
			scgettime = 120;
		}
	}

	/*
	 * ハードドロップしたときの処理
	 */
	@Override
	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
		if(fall * 2 > harddropBonus) harddropBonus = fall * 2;
	}

	/*
	 * 各フレームの終わりの処理
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		// 獲得スコア表示
		if(scgettime > 0) scgettime--;

		// セクションタイム増加
		if((engine.timerActive) && (engine.ending == 0)) {
			int section = engine.statistics.level / 100;

			if((section >= 0) && (section < sectiontime.length)) {
				sectiontime[section]++;
			}
		}

		// エンディング
		if((engine.gameActive) && (engine.ending == 2)) {
			if((version >= 1) && (engine.ctrl.isPress(Controller.BUTTON_F)))
				rolltime += 5;
			else
				rolltime += 1;

			// 時間メーター
			int remainRollTime = ROLLTIMELIMIT - rolltime;
			engine.meterValue = (remainRollTime * receiver.getMeterMax(engine)) / ROLLTIMELIMIT;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainRollTime <= 30*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainRollTime <= 20*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainRollTime <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;

			// ロール終了
			if(rolltime >= ROLLTIMELIMIT) {
				engine.gameActive = false;
				engine.resetStatc();
				engine.stat = GameEngine.STAT_EXCELLENT;
			}
		}
	}

	/*
	 * ゲームオーバー時の処理
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			secretGrade = engine.field.getSecretGrade();
		}
		return false;
	}

	/*
	 * 結果画面
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID, 0, 0, "kn PAGE" + (engine.statc[1] + 1) + "/3", EventReceiver.COLOR_RED);

		if(engine.statc[1] == 0) {
			receiver.drawMenuFont(engine, playerID, 0, 2, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore = String.format("%10d", engine.statistics.score);
			receiver.drawMenuFont(engine, playerID, 0, 3, strScore);

			receiver.drawMenuFont(engine, playerID, 0, 4, "LINE", EventReceiver.COLOR_BLUE);
			String strLines = String.format("%10d", engine.statistics.lines);
			receiver.drawMenuFont(engine, playerID, 0, 5, strLines);

			receiver.drawMenuFont(engine, playerID, 0, 6, "LEVEL", EventReceiver.COLOR_BLUE);
			String strLevel = String.format("%10d", engine.statistics.level);
			receiver.drawMenuFont(engine, playerID, 0, 7, strLevel);

			receiver.drawMenuFont(engine, playerID, 0, 8, "TIME", EventReceiver.COLOR_BLUE);
			String strTime = String.format("%10s", GeneralUtil.getTime(engine.statistics.time));
			receiver.drawMenuFont(engine, playerID, 0, 9, strTime);

			receiver.drawMenuFont(engine, playerID, 0, 10, "GARBAGE", EventReceiver.COLOR_BLUE);
			String strGarbage = String.format("%10d", garbageTotal);
			receiver.drawMenuFont(engine, playerID, 0, 11, strGarbage);

			if(rankingRank != -1) {
				receiver.drawMenuFont(engine, playerID, 0, 12, "RANK", EventReceiver.COLOR_BLUE);
				String strRank = String.format("%10d", rankingRank + 1);
				receiver.drawMenuFont(engine, playerID, 0, 13, strRank);
			}

			if(secretGrade > 4) {
				receiver.drawMenuFont(engine, playerID, 0, 14, "S. GRADE", EventReceiver.COLOR_BLUE);
				String strRank = String.format("%10s", tableSecretGradeName[secretGrade-1]);
				receiver.drawMenuFont(engine, playerID, 0, 15, strRank);
			}
		} else if(engine.statc[1] == 1) {
			receiver.drawMenuFont(engine, playerID, 0, 2, "SECTION", EventReceiver.COLOR_BLUE);

			for(int i = 0; i < sectiontime.length; i++) {
				if(sectiontime[i] > 0) {
					receiver.drawMenuFont(engine, playerID, 2, 3 + i, GeneralUtil.getTime(sectiontime[i]), sectionIsNewRecord[i]);
				}
			}

			if(sectionavgtime > 0) {
				receiver.drawMenuFont(engine, playerID, 0, 14, "AVERAGE", EventReceiver.COLOR_BLUE);
				receiver.drawMenuFont(engine, playerID, 2, 15, GeneralUtil.getTime(sectionavgtime));
			}
		} else if(engine.statc[1] == 2) {
			receiver.drawMenuFont(engine, playerID, 0, 2, "LINE/MIN", EventReceiver.COLOR_BLUE);
			String strLPM = String.format("%10g", engine.statistics.lpm);
			receiver.drawMenuFont(engine, playerID, 0, 3, strLPM);

			receiver.drawMenuFont(engine, playerID, 0, 4, "SCORE/MIN", EventReceiver.COLOR_BLUE);
			String strSPM = String.format("%10g", engine.statistics.spm);
			receiver.drawMenuFont(engine, playerID, 0, 5, strSPM);

			receiver.drawMenuFont(engine, playerID, 0, 6, "PIECE", EventReceiver.COLOR_BLUE);
			String strPiece = String.format("%10d", engine.statistics.totalPieceLocked);
			receiver.drawMenuFont(engine, playerID, 0, 7, strPiece);

			receiver.drawMenuFont(engine, playerID, 0, 8, "PIECE/SEC", EventReceiver.COLOR_BLUE);
			String strPPS = String.format("%10g", engine.statistics.pps);
			receiver.drawMenuFont(engine, playerID, 0, 9, strPPS);
		}
	}

	/*
	 * 結果画面の処理
	 */
	@Override
	public boolean onResult(GameEngine engine, int playerID) {
		// ページ切り替え
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
			engine.statc[1]--;
			if(engine.statc[1] < 0) engine.statc[1] = 2;
			engine.playSE("change");
		}
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
			engine.statc[1]++;
			if(engine.statc[1] > 2) engine.statc[1] = 0;
			engine.playSE("change");
		}
		// セクションタイム表示切替
		if(engine.ctrl.isPush(Controller.BUTTON_F)) {
			engine.playSE("change");
			isShowBestSectionTime = !isShowBestSectionTime;
		}

		return false;
	}

	/*
	 * リプレイ保存
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(owner.replayProp);
		owner.replayProp.setProperty("garbagemania.version", version);

		// ランキング更新
		if((owner.replayMode == false) && (startlevel == 0) && (always20g == false) && (big == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.level, engine.statistics.time);
			if(sectionAnyNewRecord) updateBestSectionTime();

			if((rankingRank != -1) || (sectionAnyNewRecord)) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * プロパティファイルからランキングを読み込み
	 * @param prop プロパティファイル
	 * @param ruleName ルール名
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			rankingLevel[i] = prop.getProperty("garbagemania.ranking." + ruleName + ".level." + i, 0);
			rankingTime[i] = prop.getProperty("garbagemania.ranking." + ruleName + ".time." + i, 0);
		}
		for(int i = 0; i < SECTION_MAX; i++) {
			bestSectionTime[i] = prop.getProperty("garbagemania.bestSectionTime." + ruleName + "." + i, DEFAULT_SECTION_TIME);
		}
	}

	/**
	 * プロパティファイルにランキングを保存
	 * @param prop プロパティファイル
	 * @param ruleName ルール名
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			prop.setProperty("garbagemania.ranking." + ruleName + ".level." + i, rankingLevel[i]);
			prop.setProperty("garbagemania.ranking." + ruleName + ".time." + i, rankingTime[i]);
		}
		for(int i = 0; i < SECTION_MAX; i++) {
			prop.setProperty("garbagemania.bestSectionTime." + ruleName + "." + i, bestSectionTime[i]);
		}
	}

	/**
	 * ランキングを更新
	 * @param gr 段位
	 * @param lv レベル
	 * @param time タイム
	 */
	private void updateRanking(int lv, int time) {
		rankingRank = checkRanking(lv, time);

		if(rankingRank != -1) {
			// ランキングをずらす
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingLevel[i] = rankingLevel[i - 1];
				rankingTime[i] = rankingTime[i - 1];
			}

			// 新しいデータを登録
			rankingLevel[rankingRank] = lv;
			rankingTime[rankingRank] = time;
		}
	}

	/**
	 * ランキングの順位を取得
	 * @param gr 段位
	 * @param lv レベル
	 * @param time タイム
	 * @return 順位(ランク外なら-1)
	 */
	private int checkRanking(int lv, int time) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(lv > rankingLevel[i]) {
				return i;
			} else if((lv == rankingLevel[i]) && (time < rankingTime[i])) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * ベストセクションタイム更新
	 */
	private void updateBestSectionTime() {
		for(int i = 0; i < SECTION_MAX; i++) {
			if(sectionIsNewRecord[i]) {
				bestSectionTime[i] = sectiontime[i];
			}
		}
	}
}
