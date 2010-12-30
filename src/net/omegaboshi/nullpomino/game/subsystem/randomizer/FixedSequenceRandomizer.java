package net.omegaboshi.nullpomino.game.subsystem.randomizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import mu.nu.nullpo.game.component.Piece;
public class FixedSequenceRandomizer extends Randomizer {

	private int[] sequenceTranslated;
	private int id=-1;
	
	public FixedSequenceRandomizer(){
		super();
	}
	public FixedSequenceRandomizer(boolean[] pieceEnable,long seed){
		super(pieceEnable,seed);
		
	}
	public void init(){
		

				StringBuffer sequence;
		        File file = new File("sequence.txt");
		        sequence = new StringBuffer();
		        BufferedReader reader = null;

		        try
		        {
		            reader = new BufferedReader(new FileReader(file));
		            String text = null;

		            // repeat until all lines is read
		            while ((text = reader.readLine()) != null)
		            {
		                sequence.append(text);
		                    
		            }
		        } catch (FileNotFoundException e)
		        {
		            e.printStackTrace();
		        } catch (IOException e)
		        {
		            e.printStackTrace();
		        } finally
		        {
		            try
		            {
		                if (reader != null)
		                {
		                    reader.close();
		                }
		            } catch (IOException e)
		            {
		                e.printStackTrace();
		            }
		        }
		        
		        sequenceTranslated=new int[sequence.toString().length()];
		        for (int i=0;i<sequenceTranslated.length;i++){
		        	sequenceTranslated[i]=pieceCharToId(sequence.toString().charAt(i));
		        }
		        System.out.println(Arrays.toString(sequenceTranslated));
	

	}
	
	private int pieceCharToId(char c){
		int i=0;
		for (i=0;i<Piece.PIECE_STANDARD_COUNT;i++){
			if (c==Piece.PIECE_NAMES[i].charAt(0)){
				break;
			}
		}
		return i;
	}

	@Override
	public int next() {
		id=id+1;
		return sequenceTranslated[id%sequenceTranslated.length];
	}

}
