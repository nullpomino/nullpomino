package mu.nu.nullpo.tool.airankstool;

import mu.nu.nullpo.tool.airankstool.RanksIterator.OneIteration;

public class RanksIteratorPart extends Thread {

private OneIteration oneIteration;
private Ranks ranks;

private int sMin;
private int sMax;
private int size;
private int  [] surface;
private int [] surfaceDecodedWork;

	public RanksIteratorPart(OneIteration oneIteration, Ranks ranks, int i,
			int totalParts) {

      this.oneIteration=oneIteration;
      this.ranks=ranks;

     size=this.ranks.getSize();
		  sMin=i*size/totalParts;
		 sMax=(i==totalParts-1)?size:(i+1)*size/totalParts;
		surface=new int [this.ranks.getStackWidth()-1];
		this.ranks.decode(sMin,surface);
		 surfaceDecodedWork=new int [ranks.getStackWidth()-1];
		 this.ranks.decode(sMin,surfaceDecodedWork);
		 this.setPriority(MIN_PRIORITY);
	}

	@Override
	public void run(){

		for (int s=sMin;s<sMax;s++){
			ranks.iterateSurface(surface,surfaceDecodedWork);

		   synchronized(this){
			 oneIteration.iterate();
		  }
		   if (Thread.interrupted()){

			   ranks=null;
				break;

		   }

	    }

   }
}
