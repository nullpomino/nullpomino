package mu.nu.nullpo.tool.airanksgenerator;
import java.io.Serializable;
import java.util.Arrays;

import mu.nu.nullpo.game.component.Piece;
public class Ranks implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
public static final float DAMPING_FACTOR=(float) 0.85;
private int [] ranks;




private int stackWidth;
private int size;
private Ranks ranksFrom;
private int maxJump;
private int error;
private int maxError;
public int getMaxError() {
	return maxError;
}


private int completion;
private int base;
private int surfaceWidth;

public int getCompletionPercentage(){
	long completionPercentage;
	completionPercentage= ((long)completion+1) *100;
	completionPercentage/=size;
	return (int)completionPercentage;
	
}
public boolean completionPercentageIncrease(){
	return (0==(completion % (size/100)) && completion!=0);
}
public float getErrorPercentage(){
	float errorLong= error/completion;
	  long maxErrorPossible=((long)Integer.MAX_VALUE-(long)Integer.MIN_VALUE);
	   float errorPercentage= ((float)errorLong/(float)maxErrorPossible);
	   errorPercentage*=100;
	return errorPercentage;
}
public  int getError() {
	return error;
}
public int getMaxJump() {
	return maxJump;
}
public int getStackWidth() {
	return stackWidth;
}
public Ranks(int maxJump,int stackWidth){
	 this.maxJump=maxJump;
	 base=2*maxJump+1;
	 this.stackWidth=stackWidth;
	 surfaceWidth=stackWidth-1;
	size=(int) Math.pow(base,surfaceWidth);
	ranks=new int[size];
	completion=0;
	error=0;
	maxError=0;
	Arrays.fill(ranks,Integer.MAX_VALUE/Piece.PIECE_STANDARD_COUNT);
	
}
public Ranks(Ranks rankFrom){
	this.ranksFrom=rankFrom;
	this.maxJump=ranksFrom.getMaxJump();
	base=2*maxJump+1;
	
	 this.stackWidth=ranksFrom.getStackWidth();
	 surfaceWidth=stackWidth-1;
	size=(int) Math.pow(2*maxJump+1,stackWidth-1);
	ranks=new int[size];
	completion=0;
	error=0;
	maxError=0;
	
	
}
public void setRanksFrom(Ranks ranksfrom){
	this.ranksFrom=ranksfrom;
	error=0;
	maxError=0;
    completion=0;
}
public Ranks getRanksFrom(){
	return ranksFrom;
}
public void freeRanksFrom(){
	ranksFrom=null;
}
public int getSize(){
	return size;
}
public int getRankValue(int surface){
	return ranks[surface];
}
public int encode(int [] surface){
	int surfaceNum=0;
	int factor=1;
	for (int i=0;i<surfaceWidth;i++){
		surfaceNum+=(surface[i]+maxJump)*factor;
		factor*=(base);
		}
		return surfaceNum;
}

public void setRank(int [] surface, int []surfaceDecodedWork){
	int currentSurfaceNum=encode(surface);
	ranks[currentSurfaceNum]=getRank(surface,surfaceDecodedWork);
	synchronized(this){
		completion++;
		int errorCurrent=Math.abs(ranks[currentSurfaceNum]-ranksFrom.getRankValue(currentSurfaceNum));
	    if (errorCurrent==0)
	    	errorCurrent=0;
		if (errorCurrent>maxError)
			maxError=errorCurrent;
		error+=errorCurrent;
	}
}

public void decode(int surfaceNum,int []surface ){
	
	int surfaceNumWork=surfaceNum;
	for (int i=0;i<surfaceWidth;i++){
		surface[i]=surfaceNumWork%base-maxJump;
		surfaceNumWork/=(base);
	}
	
	
}


public void iterateSurface(int [] surface,int []surfaceDecodedWork){
	
	int retenue=1;
	for (int i=0;i<surfaceWidth;i++){
		if (retenue==0)
			break;
		else {
		   if ((surface[i]<maxJump)){
			  surface[i]++;
			  surfaceDecodedWork[i]++;
			  retenue=0;
		   }
		   else {
			surface[i]=-maxJump;
			surfaceDecodedWork[i]=-maxJump;
			retenue=1;
		   }
		}
	}
	setRank(surface,surfaceDecodedWork);
	
}
private int getRank(int [] surface,int [] surfaceDecodedWork){
	int sum=0;
	
	
	for (int p=0;p<Piece.PIECE_STANDARD_COUNT;p++){
		sum+=(getRankPiece(surface,surfaceDecodedWork,p)/Piece.PIECE_STANDARD_COUNT);
	}
	int result=0;
	result=(int) ((1-DAMPING_FACTOR)*Integer.MAX_VALUE/Piece.PIECE_STANDARD_COUNT+DAMPING_FACTOR*(sum));
	if (result<0)
		result=0;
	return result;
}
private int getRankPiece(int [] surface, int [] surfaceDecodedWork ,int piece){
	int bestRank=0;
	for (int r=0;r<PIECES_NUM_ROTATIONS[piece];r++){
		int rank=getRankPieceRotation(surface,surfaceDecodedWork,piece,r);
		if (rank>bestRank){
			bestRank=rank;
		}
		
	}
	return bestRank;
}

public boolean surfaceFitsPiece(int [] surface,int piece, int rotation, int x){
	boolean fits=true;

    for (int x1=0;x1<(PIECES_WIDTHS[piece][rotation]-1);x1++){
	     
	   if (surface[x+x1]!=PIECES_LOWESTS[piece][rotation][x1]-PIECES_LOWESTS[piece][rotation][x1+1]){
		   fits=false;
         break;
         }
    }
	return fits;
}

public boolean surfaceAddPossible(int []surfaceDecodedWork, int piece, int rotation, int x){
	  boolean addPossible=true;
	  if ( x>0){
		   surfaceDecodedWork[x-1]+=PIECES_HEIGHTS[piece][rotation][0];
		  if (surfaceDecodedWork[x-1]>maxJump){
			  
			  surfaceDecodedWork[x-1]=maxJump;
			  addPossible=false;
		  }
		  else if (surfaceDecodedWork[x-1]<-maxJump){
			  surfaceDecodedWork[x-1]=-maxJump;
			  addPossible=false;
			  
		  }
		  
		  
		  
	  }
	  if (addPossible){
	  
		  for (int x1=0;x1<PIECES_WIDTHS[piece][rotation]-1;x1++){
			  surfaceDecodedWork[x+x1]+=PIECES_HEIGHTS[piece][rotation][x1+1]-PIECES_HEIGHTS[piece][rotation][x1];
			  if (surfaceDecodedWork[x+x1]>maxJump){
				  surfaceDecodedWork[x+x1]=maxJump;
				  
				  addPossible=false;
				  break;
				  
			  }
			  else if(surfaceDecodedWork[x+x1]<-maxJump) {
				  surfaceDecodedWork[x+x1]=-maxJump;
				  
				  addPossible=false;
				  break;
				  
			  }
		  }
	  }
	  
	  if ( addPossible && x<(surfaceWidth-(PIECES_WIDTHS[piece][rotation]-1))){
		  surfaceDecodedWork[x+(PIECES_WIDTHS[piece][rotation]-1)]-=PIECES_HEIGHTS[piece][rotation][PIECES_WIDTHS[piece][rotation]-1];
		  if (surfaceDecodedWork[x+(PIECES_WIDTHS[piece][rotation]-1)]>maxJump){
			  surfaceDecodedWork[x+(PIECES_WIDTHS[piece][rotation]-1)]=maxJump;
			 
			  addPossible=false;
			  
		  }
		  else if ( surfaceDecodedWork[x+(PIECES_WIDTHS[piece][rotation]-1)]<-maxJump){
			  surfaceDecodedWork[x+(PIECES_WIDTHS[piece][rotation]-1)]=-maxJump;
			  addPossible=false;
			  
		  }
	  }
	  return addPossible;
}

public void addToHeights(int [] heights,int piece,int rotation,int x){
	for (int x1=0;x1<PIECES_WIDTHS[piece][rotation];x1++){
		heights[x+x1]+=PIECES_HEIGHTS[piece][rotation][x1];
	}
	
}

public int [] heightsToSurface(int [] heights){
	
	int [] surface= new int[stackWidth-1];
	for (int i=0;i<stackWidth-1;i++){
        int diff=heights[i+1]-heights[i];
        if (diff>maxJump){
                
                       
                 diff=maxJump;
        }
        if (diff<-maxJump){
                
                diff=-maxJump;
        }
        surface[i]=diff;
	}
	return surface;
	
}

private int getRankPieceRotation( int [] surface,int [] surfaceDecodedWork,int piece, int rotation){
	
	int bestRank=0;	
	
	for (int x=0;x<(stackWidth-(PIECES_WIDTHS[piece][rotation]-1));x++){
		boolean fits=surfaceFitsPiece(surfaceDecodedWork,piece,rotation,x);
	     
	      if (fits){
	    	  boolean addPossible=surfaceAddPossible(surfaceDecodedWork,piece,rotation,x);
	    	  if (addPossible){
	    		  int newSurface=0;
	    		  int factor=1;
	    		  for (int i=0;i<surfaceWidth;i++){
	    			  newSurface+=(surfaceDecodedWork[i]+maxJump)*factor;
	    			  factor*=base;
	    		  }
	    		  int rank=ranksFrom.getRankValue(newSurface);
	    		  if (rank>bestRank){
	    			  bestRank=rank;
	    		  }
	    		
	    		
	    		
	    	  }
	    		  
	      }
	      //Reinit work surface
	  	if (x>0){
			surfaceDecodedWork[x-1]=surface[x-1];
		
		}
		for (int x1=0;x1<PIECES_WIDTHS[piece][rotation]-1;x1++){
			surfaceDecodedWork[x+x1]=surface[x+x1];
		}
		if (x<(surfaceWidth-(PIECES_WIDTHS[piece][rotation]-1))){
			surfaceDecodedWork[x+(PIECES_WIDTHS[piece][rotation]-1)]=surface[x+(PIECES_WIDTHS[piece][rotation]-1)];
		}
	
     }
	
		
	

return bestRank;
}
}
