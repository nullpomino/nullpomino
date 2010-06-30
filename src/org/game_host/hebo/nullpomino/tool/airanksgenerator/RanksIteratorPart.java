package org.game_host.hebo.nullpomino.tool.airanksgenerator;

import javax.swing.SwingWorker;

import org.game_host.hebo.nullpomino.game.component.Piece;
import org.game_host.hebo.nullpomino.tool.airanksgenerator.RanksIterator.MySwingWorker;

public class RanksIteratorPart extends Thread {
	
private MySwingWorker mySwingWorker;
private Ranks ranks;
private int i;
private int totalParts;
private int sMin;
private int sMax;
private int size;
private int  [] surface;
private int [] surfaceDecodedWork;


	public RanksIteratorPart(MySwingWorker mySwingWorker, Ranks ranks, int i,
			int totalParts) {
		
	
      this.mySwingWorker=mySwingWorker;
      this.ranks=ranks;
      this.i=i;
      this.totalParts=totalParts;
     size=ranks.getSize();
		  sMin=i*size/totalParts;
		 sMax=(i==totalParts-1)?size:(i+1)*size/totalParts;
		surface=new int [ranks.getStackWidth()-1];
		ranks.decode(sMin,surface);
		 surfaceDecodedWork=new int [ranks.getStackWidth()-1];
		
	}
	
	public void run(){
		
		for (int s=sMin;s<sMax;s++){
			ranks.iterateSurface(surface,surfaceDecodedWork);
			
		   synchronized(this){
			 mySwingWorker.iterate();
		  }
		   if (Thread.interrupted()){
			   
			   ranks=null;
				break;
			
			  
		   }
		
	    }

   }
}
