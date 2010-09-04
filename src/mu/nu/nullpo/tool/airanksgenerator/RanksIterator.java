package mu.nu.nullpo.tool.airanksgenerator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;

import org.jdesktop.swingworker.SwingWorker;

public class RanksIterator extends ProgressMonitor implements PropertyChangeListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;



	private Ranks ranks;
	private Ranks ranksFrom;
	private String outputFile;
	private int numIterations;
	private int iteration;
	

	 private MySwingWorker mySwingWorker;

    class MySwingWorker extends SwingWorker<Void, String> {

    private int totalParts;
    private boolean cancelled;
    private RanksIteratorPart [] ranksIteratorPart;

        public MySwingWorker( int totalParts) {

        	this.totalParts=totalParts;

            setProgress(0);
            cancelled=false;

        }
        @Override

        public Void doInBackground() {

        	 ranksIteratorPart=new RanksIteratorPart[totalParts];

        	 for (int n=0;n<numIterations;n++){
        		 iteration=n;
        	   for (int i=0;i<totalParts;i++){
        	   ranksIteratorPart[i]=new RanksIteratorPart(this,ranks,i,totalParts);	;
        	   ranksIteratorPart[i].start();
        	   }
        	for (int i=0;i<totalParts;i++){
        		try {
					ranksIteratorPart[i].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	
        	if (cancelled){
        		System.out.println("cancelled !");
        		ranks=ranks.getRanksFrom();
        		return null;
        	}
        	ranks.scaleRanks();
        	//lastError=ranks.getErrorPercentage();
        	//lastErrorMax=ranks.getMaxError();
        	if (n!=numIterations-1){
        	  ranksFrom=ranks.getRanksFrom();
        	  ranksFrom.setRanksFrom(ranks);
        	  ranks=ranksFrom;
        	}
        	
        	}

            return null;
        }

        public void iterate(){



        	int totalCompletion=100*iteration+ranks.getCompletionPercentage();
        	if (ranks.completionPercentageIncrease()){

        	  this.setProgress(totalCompletion/numIterations);
        	}
        }


        @Override
        protected void done() {
        	System.out.println("done !");
            try {
            	File ranksAIDir=new File("res/ranksai/");
            	if (!ranksAIDir.exists()){
            		ranksAIDir.mkdirs();
            	}
                FileOutputStream fos=null;
                ObjectOutputStream out=null;
                fos=new FileOutputStream("res/ranksai/"+ outputFile);
                out = new ObjectOutputStream(fos);
                ranks.freeRanksFrom();
                out.writeObject(ranks);
                out.close();

            } catch(Exception e) {
                e.printStackTrace();
            }
            setProgress(100);



        	//new RanksResult(parent,ranks,100,false);

        }
        public void cancelTask(){
        	cancelled=true;
        	for (int i=0;i<totalParts;i++){
        		 ranksIteratorPart[i].interrupt();
        	}
        }
    }

public RanksIterator(JFrame parent,String inputFile,String outputFile, int numIterations){

	super(parent,AIRanksGenerator.getUIText("Progress_Message"),"",0,100);
	this.outputFile=outputFile;
	
	this.numIterations=numIterations;
	FileInputStream fis = null;
	ObjectInputStream in = null;
	if (inputFile.trim().length() == 0)
		ranksFrom=new Ranks(4,9);
	else {
		  try {
			fis = new FileInputStream(inputFile);
			   in = new ObjectInputStream(fis);
			   ranksFrom = (Ranks)in.readObject();
			   in.close();

		} catch (FileNotFoundException e) {
			ranksFrom=new Ranks(4,9);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	ranks=new Ranks(ranksFrom);

	//size=ranks.getSize();

	mySwingWorker =this.new MySwingWorker(4);
	mySwingWorker.addPropertyChangeListener(this);
	mySwingWorker.execute();



}
public void propertyChange(PropertyChangeEvent evt) {
	 if ("progress" == evt.getPropertyName() ) {
           int progress = (Integer) evt.getNewValue();
           setProgress(progress);


           String message =
               String.format(AIRanksGenerator.getUIText("Progress_Note"), iteration+1,ranks.getCompletionPercentage(),numIterations,progress);
          setNote(message);
          if (progress==100){

        	  ranks=null;
              ranksFrom=null;
          }
	 }

   if (isCanceled()) {

	                   this.mySwingWorker.cancelTask();



   }



}

}
