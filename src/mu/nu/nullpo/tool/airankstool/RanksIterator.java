package mu.nu.nullpo.tool.airankstool;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.jdesktop.swingworker.SwingWorker;

public class RanksIterator extends JDialog implements PropertyChangeListener,ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Ranks ranks;
	private Ranks ranksFrom;
	private String outputFile;
	private int numIterations;
	private int iteration;

	private JLabel progressLabel;
	private JProgressBar progressBar;
	private JButton cancelButton;
	 private AllIterations allIterations;
	 private OneIteration oneIteration;
	  class OneIteration extends SwingWorker<Void, String>{
		  private int totalParts;
		  private RanksIteratorPart [] ranksIteratorPart;
		  private boolean cancelled;
		  private Ranks ranks;

		  public OneIteration(int totalParts, Ranks ranks){
			  this.ranks=ranks;
			  this.totalParts=totalParts;
			  cancelled=false;

		  }
		  public void iterate(){


	        	if (ranks.completionPercentageIncrease()){

	        	  this.setProgress(ranks.getCompletionPercentage());
	        	}
	        }
		@Override
		protected Void doInBackground() throws Exception {
			ranksIteratorPart=new RanksIteratorPart[totalParts];

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
        		//System.out.println("cancelled !");
        		ranks=ranks.getRanksFrom();
        		allIterations.cancelTask();

        	}
        	setProgress(100);
			return null;
		}
	    public void cancelTask(){
        	cancelled=true;
        	for (int i=0;i<totalParts;i++){
        		 ranksIteratorPart[i].interrupt();
        	}
        }

	  }
    class AllIterations extends SwingWorker<Void, String> {

    private int totalParts;
    private RanksIterator ranksIterator;

    private String inputFile;
    boolean cancelled;

        public AllIterations( int totalParts, RanksIterator ranksIterator, String inputFile) {

        	this.totalParts=totalParts;
        	this.ranksIterator=ranksIterator;
        	this.inputFile=inputFile;
        	cancelled=false;
            setProgress(0);


        }

        @Override

        public Void doInBackground() {
        	progressLabel.setText(AIRanksTool.getUIText("Progress_Note_Load_File"));

        	FileInputStream fis = null;
        	ObjectInputStream in = null;
        	if (inputFile.trim().length() == 0)
        		ranksFrom=new Ranks(4,9);
        	else {
        		  try {
        			fis = new FileInputStream(AIRanksTool.RANKSAI_DIR+inputFile);
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



        	 for (int n=0;n<numIterations;n++){
        		 iteration=n;


        		 oneIteration=new OneIteration(totalParts,ranks);
        		 oneIteration.addPropertyChangeListener(ranksIterator);
        		 oneIteration.execute();
        		 try {
					oneIteration.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (cancelled){
	        		//System.out.println("cancelled !");
	        		//ranks=ranks.getRanksFrom();
	        		//allIterations.cancelTask();
	        		break;
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
         	//System.out.println("save file !");
       	 progressLabel.setText(AIRanksTool.getUIText("Progress_Note_Save_File"));

           try {
           	File ranksAIDir=new File(AIRanksTool.RANKSAI_DIR);
           	if (!ranksAIDir.exists()){
           		ranksAIDir.mkdirs();
           	}
               FileOutputStream fos=null;
               ObjectOutputStream out=null;
               fos=new FileOutputStream(AIRanksTool.RANKSAI_DIR+ outputFile);
               out = new ObjectOutputStream(fos);
               ranks.freeRanksFrom();
               out.writeObject(ranks);
               out.close();

           } catch(Exception e) {
               e.printStackTrace();
           }
           ranks=null;
           ranksFrom=null;
           setProgress(100);

            return null;
        }

        public void cancelTask(){
        	cancelled=true;

        }

        @Override
        protected void done() {

           dispose();

        	//new RanksResult(parent,ranks,100,false);

        }

    }

public RanksIterator(JFrame parent,String inputFile,String outputFile, int numIterations){

	super(parent,AIRanksTool.getUIText("Progress_Message"));
	this.outputFile=outputFile;
	;

	this.numIterations=numIterations;
	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

	progressLabel=new JLabel(String.format(AIRanksTool.getUIText("Progress_Note"), 1,0,numIterations,0));
	String message=String.format(AIRanksTool.getUIText("Progress_Note"), 100,100,100,100);
    progressLabel.setText(message);

	progressBar=new JProgressBar(0,100);
    cancelButton= new JButton(AIRanksTool.getUIText("Progress_Cancel_Button"));
    cancelButton.setActionCommand("cancel");
    cancelButton.addActionListener(this);
	JPanel mainPane=new JPanel(new BorderLayout() );
    JPanel pane = new JPanel(new GridLayout(0,1));
    mainPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

	pane.add(progressLabel);
	pane.add(progressBar);
	pane.add(cancelButton);
	mainPane.add(pane,BorderLayout.CENTER);
	add(mainPane);
	pack();
	setVisible(true);


	//size=ranks.getSize();
	int numProcessors=Runtime.getRuntime().availableProcessors();
	//System.out.println(numProcessors);

	allIterations =this.new AllIterations(numProcessors,this,inputFile);
	//allIterations.addPropertyChangeListener(this);
	allIterations.execute();

}
public void propertyChange(PropertyChangeEvent evt) {
	 if ("progress" == evt.getPropertyName() ) {
		 ;
           int totalCompletion=(100*iteration+ranks.getCompletionPercentage())/numIterations;
           progressBar.setValue(totalCompletion);

           String message =
               String.format(AIRanksTool.getUIText("Progress_Note"), iteration+1,ranks.getCompletionPercentage(),numIterations,totalCompletion);
          progressLabel.setText(message);

	 }

}
public void actionPerformed(ActionEvent arg0) {
	this.oneIteration.cancelTask();
}

}
