package mu.nu.nullpo.tool.airanksgenerator;

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
	//private JFrame parent;
	private JLabel progressLabel;
	private JProgressBar progressBar;
	private JButton cancelButton;
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
            	File ranksAIDir=new File(AIRanksGenerator.RANKSAI_DIR);
            	if (!ranksAIDir.exists()){
            		ranksAIDir.mkdirs();
            	}
                FileOutputStream fos=null;
                ObjectOutputStream out=null;
                fos=new FileOutputStream(AIRanksGenerator.RANKSAI_DIR+ outputFile);
                out = new ObjectOutputStream(fos);
                ranks.freeRanksFrom();
                out.writeObject(ranks);
                out.close();

            } catch(Exception e) {
                e.printStackTrace();
            }
            setProgress(100);
           dispose();

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

	super(parent,AIRanksGenerator.getUIText("Progress_Message"));
	this.outputFile=outputFile;
	//this.parent=parent;

	this.numIterations=numIterations;
	setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	progressLabel=new JLabel(String.format(AIRanksGenerator.getUIText("Progress_Note"), 1,0,numIterations,0));
    progressBar=new JProgressBar(0,100);
    cancelButton= new JButton(AIRanksGenerator.getUIText("Progress_Cancel_Button"));
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
           progressBar.setValue(progress);

           String message =
               String.format(AIRanksGenerator.getUIText("Progress_Note"), iteration+1,ranks.getCompletionPercentage(),numIterations,progress);
          progressLabel.setText(message);
          if (progress==100){

        	  ranks=null;
              ranksFrom=null;
          }
	 }

}
@Override
public void actionPerformed(ActionEvent arg0) {
	this.mySwingWorker.cancelTask();
}

}
