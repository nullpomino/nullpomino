package org.game_host.hebo.nullpomino.tool.airanksgenerator;

import org.game_host.hebo.nullpomino.game.component.Field;
import org.game_host.hebo.nullpomino.game.component.Piece;

public class SurfaceEncoder {
	public static final int[] PIECES_NUM_ROTATIONS = {
		2,	// I
		4,	// L
		1,	// O
		2,	// Z
		4,	// T
		4,	// J
		2,	// S
		1,	// I1
		2,	// I2
		2,	// I3
		4	// L3
	};

	public static final int[][] PIECES_LEFTMOSTS={
		{0,2,0,1},	// I
		{0,1,0,0},	// L
		{0,0,0,0},	// O
		{0,1,0,0},	// Z
		{0,1,0,0},	// T
		{0,1,0,0},	// J
		{0,1,0,0},	// S
		{0,0,0,0},	// I1
		{0,1,0,0},	// I2
		{0,1,0,1},	// I3
		{0,0,0,0}	// L3
	};
	public static final int[][] PIECES_RIGHTMOSTS={
		{3,2,3,1}, //I
		{2,2,2,1}, //L
		{1,1,1,1}, //O
		{2,2,2,1}, //Z
		{2,2,2,1}, //T
		{2,2,2,1}, //J
		{2,2,2,1}, //S
		{0,0,0,0}, //I1
		{1,1,1,0}, //I2
		{2,1,2,1}, //I3
		{1,1,1,1}, //L3

	};

	public static final int[][] PIECES_WIDTHS={
		{4,1,4,1}, //I
		{3,2,3,2}, //L
		{2,2,2,2}, //O
		{3,2,3,2}, //Z
		{3,2,3,2}, //T
		{3,2,3,2}, //J
		{3,2,3,2}, //S
		{1,1,1,1}, //I1
		{2,1,2,1}, //I2
		{3,1,3,1}, //I3
		{2,2,2,2}  //L3
	};

	public static final int[][][] PIECES_HEIGHTS={
		{{1,1,1,1},{4},{1,1,1,1},{4}}, //I
		{{1,1,2},{3,1},{2,1,1},{1,3}}, //L
		{{2,2},{2,2},{2,2},{2,2}},     //O
		{{1,2,1},{2,2},{1,2,1},{2,2}}, //Z
		{{1,2,1},{3,1},{1,2,1},{1,3}}, //T
		{{2,1,1},{3,1},{1,1,2},{1,3}}, //J
		{{1,2,1},{2,2},{1,2,1},{2,2}}, //S
		{{1},{1},{1},{1}},            //I1
		{{1,1},{2},{1,1},{2}},        //I2
		{{1,1,1},{3},{1,1,1},{3}},    //I3
		{{2,1},{2,1},{1,2},{1,2}}     //L3
	};
	public static final int[][][] PIECES_LOWESTS={
		{{1,1,1,1},{3},{2,2,2,2},{3}}, //I
		{{1,1,1},{2,2},{2,1,1},{0,2}}, //L
		{{1,1},{1,1},{1,1},{1,1}},     //O
		{{0,1,1},{2,1},{1,2,2},{2,1}}, //Z
		{{1,1,1},{2,1},{1,2,1},{1,2}}, //T
		{{1,1,1},{2,0},{1,1,2},{2,2}}, //J
		{{1,1,0},{1,2},{2,2,1},{1,2}}, //S
		{{0},{0},{0},{0}},             //I1
		{{0,0},{1},{1,1},{1}},         //I2
		{{1,1,1},{2},{1,1,1},{2}},     //I3
		{{1,1},{1,0},{0,1},{1,1}}      //L3
	};


	private int stackWidth;
	private int maxJumps;
	private int base;
	private int bigFactor;
	private int [][][][] surfaceFits;
	private int [][][][] surfaceAdds;

	public SurfaceEncoder(int stackWidth,int maxJumps){

		this.stackWidth=stackWidth;
		this.maxJumps=maxJumps;
		this.base=2*this.maxJumps+1;
		int leftPartSize=(stackWidth-1)/2;
		this.bigFactor=(int) Math.pow(base, leftPartSize);

		generateSurfaceFits();
		generateSurfaceAdds();

	}


	public void generateSurfaceFits(){
         surfaceFits=new int[Piece.PIECE_COUNT][][][];

		for (int p=0;p<Piece.PIECE_COUNT;p++){
			surfaceFits[p]=new int[PIECES_NUM_ROTATIONS[p]][][];
		
	         
			for (int r=0;r<PIECES_NUM_ROTATIONS[p];r++){
				surfaceFits[p][r]=new int[stackWidth-PIECES_WIDTHS[p][r]+1][];
				int [] pieceHeights=new int[stackWidth];

				for (int x=0;x<stackWidth-PIECES_WIDTHS[p][r]+1;x++){
					surfaceFits[p][r][x]= new int [stackWidth-1];
					int middle=(stackWidth-1)/2;


					for (int x1=0;x1<x;x1++){
						pieceHeights[x1]=-1;
					}
					for (int x1=x;x1<x+PIECES_WIDTHS[p][r];x1++){
						pieceHeights[x1]=3-PIECES_LOWESTS[p][r][x1-x];
					}
					for (int x1=PIECES_WIDTHS[p][r];x1<stackWidth;x1++){
						pieceHeights[x1]=-1;
					}
					
					int index=0;
					for (int i=middle;i>=1;i--){
						if( pieceHeights[i-1]!=-1 && pieceHeights[i]!=-1){
						 int step=pieceHeights[i-1]-pieceHeights[i];
					      surfaceFits[p][r][x][index]=step+maxJumps;
						}
						else {
							surfaceFits[p][r][x][index]=-1;
						}
					
					
						index++;
					}
					for (int i=middle;i<stackWidth-1;i++){
						if( pieceHeights[i+1]!=-1 && pieceHeights[i]!=-1){
							 int step=pieceHeights[i+1]-pieceHeights[i];
						      surfaceFits[p][r][x][index]=step+maxJumps;
							}
							else {
								surfaceFits[p][r][x][index]=-1;
							}
						
						
							index++;
					    
					
					}
				

					   
				}
			}
		}
	}
	
	public void generateSurfaceAdds(){
		surfaceAdds=new int[Piece.PIECE_COUNT][][][];
		for (int p=0;p<Piece.PIECE_COUNT;p++){
			surfaceAdds[p]=new int[PIECES_NUM_ROTATIONS[p]][][];
			for (int r=0;r<PIECES_NUM_ROTATIONS[p];r++){
				surfaceFits[p][r]=new int[stackWidth-PIECES_WIDTHS[p][r]+1][];
				for (int x=0;x<stackWidth-PIECES_WIDTHS[p][r]+1;x++){
					surfaceFits[p][r][x]= new int [stackWidth-1];
					int middle=(stackWidth-1)/2;
					
					int [] pieceHeights=new int[stackWidth];
					for (int x1=0;x1<x-1;x1++){
						pieceHeights[x1]=-1;
					}
					if (x>=1)
						pieceHeights[x-1]=0;
					for (int x1=x;x1<x+PIECES_WIDTHS[p][r];x1++){
						pieceHeights[x1]=PIECES_HEIGHTS[p][r][x1-x];
					}
					if (x+PIECES_WIDTHS[p][r]<stackWidth)
						pieceHeights[x+PIECES_WIDTHS[p][r]]=0;
					for (int x1=x+PIECES_WIDTHS[p][r];x1<stackWidth;x1++){
						pieceHeights[x1]=-1;
					}
				
					int index=0;
					for (int i=middle;i>=1;i--){
						if( pieceHeights[i-1]!=-1 && pieceHeights[i]!=-1){
						 int step=pieceHeights[i-1]-pieceHeights[i];
					      surfaceAdds[p][r][x][index]=step;
						}
						else {
							surfaceAdds[p][r][x][index]=-1;
						}
					
					
						index++;
					}
					for (int i=middle;i<stackWidth-1;i++){
						if( pieceHeights[i+1]!=-1 && pieceHeights[i]!=-1){
							 int step=pieceHeights[i+1]-pieceHeights[i];
						      surfaceAdds[p][r][x][index]=step;
							}
							else {
								surfaceAdds[p][r][x][index]=-1;
							}
						
						
							index++;
					    
					
					}
				}
			}
		}
	}


	public int encode(Field field){

		int [] heights= new int [stackWidth];
		for (int i=0;i<stackWidth;i++){
			heights[i]=field.getHighestBlockY(i);
		}

		return encode (heights);
	}
	public int encode (int [] heights ){
		int middle=(stackWidth-1)/2;
		int factor=1;
		int surfaceNum=0;
		for (int i=middle;i>=1;i--){
			int step=heights[i-1]-heights[i];
			if (step>maxJumps)
				step=maxJumps;
			if (step<maxJumps)
				step=maxJumps;
			surfaceNum+=(step+maxJumps)*factor;
			factor*=this.base;
		}
		for (int i=middle;i<stackWidth-1;i++){
			int step=heights[i+1]-heights[i];
			if (step>maxJumps)
				step=maxJumps;
			if (step<maxJumps)
				step=maxJumps;
			surfaceNum+=(step+maxJumps)*factor;
			factor*=9;
		}
		return surfaceNum;
	}
	public int  getStoredSurface(int surfaceNum){
		int leftPart=surfaceNum % bigFactor;
		int rightPart=surfaceNum / bigFactor;

		if (leftPart>rightPart){
			int tempPart=leftPart;
			leftPart=rightPart;
			rightPart=tempPart;
		}
		return leftPart+bigFactor*rightPart;
	}

	public int getSizeOfStoredSurfaces(){

		return (int) ((Math.pow(base, stackWidth-1)+Math.pow(base, (stackWidth-1)/2))/2);
	}
	
	
	
	public boolean surfaceFitsPiece(int surfaceNum,int piece, int rotation, int x){
		int surfaceNumChecked=surfaceNum;
		for (int i=0;i<stackWidth-1;i++){
			int step=surfaceNumChecked%base;
		  if (surfaceFits[piece][rotation][x][i]!=-1 && surfaceFits[piece][rotation][x][i]!=step)
			  return false;
			  
		surfaceNumChecked/=9;
		}
		return true;
	}

	public boolean addPossible(int surfaceNum,int piece,int rotation, int x){
		int surfaceNumChecked=surfaceNum;
		for (int i=0;i<stackWidth-1;i++){
			int step=surfaceNumChecked%base;
		  if (surfaceFits[piece][rotation][x][i]!=-1 && Math.abs(surfaceAdds[piece][rotation][x][i]+step)>4)
			  return false;
			  
		surfaceNumChecked/=9;
		}
		return true;
	}

}





