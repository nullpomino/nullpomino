package mu.nu.nullpo.tool.airanksgenerator;

import java.util.Random;

import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.subsystem.ai.RanksAI;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.History4RollsRandomizer;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

public class AIRanksTester {
	private Randomizer randomizer;
	private int numTries;
	private RanksAI ranksAI;
	private int [] pieces;
	private int totalPieces;
	public AIRanksTester(int numTries){
		this.numTries=numTries;

		this.totalPieces=0;
		this.ranksAI=new RanksAI();
	}
	public static void main(String[] args) {

		AIRanksTester tester=new AIRanksTester(100 );
		tester.test();
	}
	private void init(){
		boolean [] pieceEnable= new boolean[Piece.PIECE_COUNT];
		for (int i=0;i<Piece.PIECE_STANDARD_COUNT;i++){
			pieceEnable[i]=true;
		}
		long seed=new Random().nextLong();

		randomizer =new History4RollsRandomizer(pieceEnable,seed);
		randomizer.init();

		 pieces=new int[6];
		for (int i=0;i<6;i++){
			pieces[i]=randomizer.next();
		}

		ranksAI.initRanks();
	}
	private void incrementPieces(){

		for (int i=0;i<5;i++){
			pieces[i]=pieces[i+1];
		}
		pieces[5]=randomizer.next();

	}
	public void test(){
		for (int i=0;i<numTries;i++){
			int tempTotalPieces=totalPieces;
			playGame();
			System.out.println("Game : " +i+ " Pieces : "+(totalPieces-tempTotalPieces)+ " Cumulated average : "+totalPieces/(i+1) );
		}
	}
	private void playGame(){
		init();
		/*if ((pieces[0]==Piece.PIECE_S) || (pieces[0]==Piece.PIECE_Z) || (pieces[0]==Piece.PIECE_O)){
			//System.out.println("�chec !!");
		}*/
		int [] heights=new int[9];
		int [] holdPiece={-1};
		boolean [] holdOK ={true};
		while (!ranksAI.isGameOver()){
			totalPieces++;
			//System.out.println(Arrays.toString(heights));
			ranksAI.playFictitiousMove(heights,pieces,holdPiece,holdOK);
			incrementPieces();

		}
	}
}

