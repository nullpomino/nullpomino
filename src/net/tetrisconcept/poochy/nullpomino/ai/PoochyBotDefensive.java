package net.tetrisconcept.poochy.nullpomino.ai;

import org.game_host.hebo.nullpomino.game.component.Field;
import org.game_host.hebo.nullpomino.game.component.Piece;

public class PoochyBotDefensive extends PoochyBot {
	/*
	 * AI's name
	 */
	@Override
	public String getName() {
		return super.getName() + " (Defensive)";
	}

	/**
	 * 思考ルーチン
	 * @param x X座標
	 * @param y Y座標
	 * @param rt 方向
	 * @param rtOld 回転前の方向（-1：なし）
	 * @param fld フィールド（どんなに弄っても問題なし）
	 * @param piece ピース
	 * @param depth 妥協レベル（0からgetMaxThinkDepth()-1まで）
	 * @return 評価得点
	 */
	@Override
	public int thinkMain(int x, int y, int rt, int rtOld, Field fld, Piece piece, int depth) {
		int pts = 0;

		// 他のブロックに隣接していると加点
		if(piece.checkCollision(x - 1, y, fld)) pts += 1;
		if(piece.checkCollision(x + 1, y, fld)) pts += 1;
		if(piece.checkCollision(x, y - 1, fld)) pts += 1000;

		int width = fld.getWidth();
		//int height = fld.getHeight();

		int xMin = piece.getMinimumBlockX()+x;
		int xMax = piece.getMaximumBlockX()+x;

		// 穴の数とI型が必要な谷の数（設置前）
		int holeBefore = fld.getHowManyHoles();
		//int lidBefore = fld.getHowManyLidAboveHoles();
		
		//Fetch depths.
		int[] depthsBefore = getColumnDepths(fld);
		int deepestY = -1;
		//int deepestX = -1;
		for (int i = 0; i < width-1; i++)
			if (depthsBefore[i] > deepestY)
			{
				deepestY = depthsBefore[i];
				//deepestX = i;
			}

		//Find valleys that need an I, J, or L.
		int needIValleyBefore = 0, needJValleyBefore = 0, needLValleyBefore = 0;
		if (depthsBefore[0] > depthsBefore[1])
			needIValleyBefore = (depthsBefore[0]-depthsBefore[1])/3;
		if (depthsBefore[width-1] > depthsBefore[width-2])
			needIValleyBefore = (depthsBefore[width-1]-depthsBefore[width-2])/3;
		for (int i = 1; i < width-1; i++)
		{
			int left = depthsBefore[i-1], right = depthsBefore[i+1];
			int lowerSide = Math.max(left, right);
			int diff = depthsBefore[i] - lowerSide;
			if (diff >= 3)
				needIValleyBefore += diff/3;
			if (left == right)
			{
				if (left == depthsBefore[i]+2)
				{
					needIValleyBefore++;
					needLValleyBefore--;
					needJValleyBefore--;
				}
				else if (left == depthsBefore[i]+1)
				{
					needLValleyBefore++;
					needJValleyBefore++;
				}
			}
			if (diff%4 == 2)
			{
				if (left > right)
					needLValleyBefore+=2;
				else if (left < right)
					needJValleyBefore+=2;
				else
				{
					needJValleyBefore++;
					needLValleyBefore++;
				}
			}
		}
		if ((depthsBefore[0] - depthsBefore[1])%4 == 2)
			needJValleyBefore += 2;
		if ((depthsBefore[width-1] - depthsBefore[width-2])%4 == 2)
			needJValleyBefore += 2;
		
		needJValleyBefore >>= 1;
		needLValleyBefore >>= 1;

		// フィールドの高さ（設置前）
		int heightBefore = fld.getHighestBlockY();
		// T-Spinフラグ
		boolean tspin = false;
		if((piece.id == Piece.PIECE_T) && (rtOld != -1) && (fld.isTSpinSpot(x, y, piece.big))) {
			tspin = true;
		}

		//Does move fill in valley with an I piece?
		int valley = 0;
		if(piece.id == Piece.PIECE_I) {
			if (xMin == xMax && 0 <= xMin && xMin < width)
			{
				//debugOut("actualX = " + xMin);
				int xDepth = depthsBefore[xMin];
				int sideDepth = -1;
				if (xMin > 0)
					sideDepth = depthsBefore[xMin-1];
				if (xMin < width-1)
					sideDepth = Math.max(sideDepth, depthsBefore[xMin+1]);
				valley = xDepth - sideDepth;
				//debugOut("valley = " + valley);
			}
		}

		// ピースを置く
		if(!piece.placeToField(x, y, rt, fld)) {
			debugOut("End of thinkMain(" + x + ", " + y + ", " + rt + ", " + rtOld +
					", fld, piece " + piece.id + ", " + depth + "). pts = 0 (Cannot place piece)");
			return Integer.MIN_VALUE;
		}

		// ライン消去
		int lines = fld.checkLine();
		if(lines > 0) {
			fld.clearLine();
			fld.downFloatingBlocks();
		}

		// 全消し
		boolean allclear = fld.isEmpty();
		if(allclear) pts += 500000;

		// フィールドの高さ（消去後）
		int heightAfter = fld.getHighestBlockY();

		int[] depthsAfter = getColumnDepths(fld);

		// 危険フラグ
		//boolean danger = (heightBefore <= 8);
		//Flag for really dangerously high stacks
		boolean peril = (heightBefore <= 4);

		// 下に置くほど加点
		pts += y * 20;

		int holeAfter = fld.getHowManyHoles();

		//Bonus points for filling in valley with an I piece
		int valleyBonus = 0;
		if (valley == 3 && xMax < width-1)
			valleyBonus = 40000;
		else if (valley >= 4)
			valleyBonus = 400000;
		if (xMax == 0)
			valleyBonus *= 2;
		if (valley > 0)
			debugOut("I piece xMax = " + xMax + ", valley depth = " + valley +
					", valley bonus = " + valleyBonus);
		pts += valleyBonus;
		
		//Points for line clears
		if (peril) {
			if(lines == 1) pts += 500000;
			if(lines == 2) pts += 1000000;
			if(lines == 3) pts += 30000000;
			if(lines >= 4) pts += 100000000;
		}
		else {
			if(lines == 1) pts += 50000;
			if(lines == 2) pts += 100000;
			if(lines == 3) pts += 300000;
			if(lines >= 4) pts += 1000000;
		}

		if( (lines < 4) && (!allclear) ) {
			// 穴の数とI型が必要な谷の数（設置後）
			//int lidAfter = fld.getHowManyLidAboveHoles();

			//Find valleys that need an I, J, or L.
			int needIValleyAfter = 0, needJValleyAfter = 0, needLValleyAfter = 0;
			if (depthsAfter[0] > depthsAfter[1])
				needIValleyAfter = (depthsAfter[0]-depthsAfter[1])/3;
			if (depthsAfter[width-1] > depthsAfter[width-2])
				needIValleyAfter = (depthsAfter[width-1]-depthsAfter[width-2])/3;
			for (int i = 1; i < width-1; i++)
			{
				int left = depthsAfter[i-1], right = depthsAfter[i+1];
				int lowerSide = Math.max(left, right);
				int diff = depthsAfter[i] - lowerSide;
				if (diff >= 3)
					needIValleyAfter += diff/3;
				if (left == right)
				{
					if (left == depthsAfter[i]+2)
					{
						needIValleyAfter++;
						needLValleyAfter--;
						needJValleyAfter--;
					}
					else if (left == depthsAfter[i]+1)
					{
						needLValleyAfter++;
						needJValleyAfter++;
					}
				}
				if (diff%4 == 2)
				{
					if (left > right)
						needLValleyAfter+=2;
					else if (left < right)
						needJValleyAfter+=2;
					else
					{
						needJValleyAfter++;
						needLValleyAfter++;
					}
				}
			}
			if ((depthsAfter[0] - depthsAfter[1])%4 == 2)
				needJValleyAfter += 2;
			if ((depthsAfter[width-1] - depthsAfter[width-2])%4 == 2)
				needJValleyAfter += 2;

			needJValleyAfter >>= 1;
			needLValleyAfter >>= 1;

			if(holeAfter > holeBefore) {
				// 新たに穴ができると減点
				if(depth == 0) return Integer.MIN_VALUE;
				pts -= (holeAfter - holeBefore) * 400;
			} else if(holeAfter < holeBefore) {
				// 穴を減らすと加点
				pts += (holeBefore - holeAfter) * 400 + 10000;
			}

			/*
			if(lidAfter < lidBefore) {
				// 穴の上に乗っているブロックを減らすと加点
				pts += (lidAfter - lidBefore) * 500;
			}
			*/

			if((tspin) && (lines >= 1)) {
				// T-Spin Bonus - retained from Basic AI, but should never actually trigger
				pts += 100000 * lines;
			}

			//Bonuses and penalties for valleys that need I, J, or L.
			int needIValleyDiffScore = 0;
			if (needIValleyBefore > 0)
				needIValleyDiffScore = 1 << needIValleyBefore;
			if (needIValleyAfter > 0)
				needIValleyDiffScore -= 1 << needIValleyAfter;

			int needLJValleyDiffScore = 0;

			if (needJValleyBefore > 1)
				needLJValleyDiffScore += 1 << needJValleyBefore;
			if (needJValleyAfter > 1)
				needLJValleyDiffScore -= 1 << needJValleyAfter;
			if (needLValleyBefore > 1)
				needLJValleyDiffScore += 1 << needLValleyBefore;
			if (needLValleyAfter > 1)
				needLJValleyDiffScore -= 1 << needLValleyAfter;

			if(needIValleyDiffScore < 0 && holeAfter >= holeBefore) {
				if(depth == 0) return Integer.MIN_VALUE;
				pts += needIValleyDiffScore * 200;
			} else if(needIValleyDiffScore > 0) {
				pts += needIValleyDiffScore * 200;
			}
			if(needLJValleyDiffScore < 0 && holeAfter >= holeBefore) {
				if(depth == 0) return Integer.MIN_VALUE;
				pts += needLJValleyDiffScore * 40;
			} else if(needLJValleyDiffScore > 0) {
				pts += needLJValleyDiffScore * 40;
			}

			//Bonus for pyramidal stack
			int mid = width/2-1;
			int d;
			for (int i = 0; i < mid-1; i++)
			{
				d = depthsAfter[i] - depthsAfter[i+1];
				if (d >= 0)
					pts += 10;
				else
					pts += d;
			}
			for (int i = mid+2; i < width; i++)
			{
				d = depthsAfter[i] - depthsAfter[i-1];
				if (d >= 0)
					pts += 10;
				else
					pts += d;
			}
			d = depthsAfter[mid-1] - depthsAfter[mid];
			if (d >= 0)
				pts += 5;
			else
				pts += d;
			d = depthsAfter[mid+1] - depthsAfter[mid];
			if (d >= 0)
				pts += 5;
			else
				pts += d;

			// 高さを抑えると加点
			if(heightBefore < heightAfter)
				pts += (heightAfter - heightBefore) * 20;
			// 高くすると減点
			else if(heightBefore > heightAfter)
				pts -= (heightBefore - heightAfter) * 4;
			
			//Penalty for dangerous placements
			if (heightAfter < 2)
			{
				int spawnMinX = width/2 - 2;
				int spawnMaxX = width/2 + 1;
				for (int i = spawnMinX; i <= spawnMaxX; i++)
					if (depthsAfter[i] < 2 && depthsAfter[i] < depthsBefore[i])
						pts -= 2000000 * (depthsBefore[i] - depthsAfter[i]);
				if (heightBefore >= 2 && depth == 0)
					pts -= 2000000 * (heightBefore - heightAfter);
			}
		}
		debugOut("End of thinkMain(" + x + ", " + y + ", " + rt + ", " + rtOld +
				", fld, piece " + piece.id + ", " + depth + "). pts = " + pts);
		return pts;
	}
}
