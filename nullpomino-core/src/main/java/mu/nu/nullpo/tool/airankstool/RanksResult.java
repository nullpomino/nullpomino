package mu.nu.nullpo.tool.airankstool;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;

import org.jdesktop.swingworker.SwingWorker;

public class RanksResult extends JDialog implements ActionListener, PropertyChangeListener{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	//private JFrame parent;
	class SurfaceComparator implements Comparator<Integer>{

		public int compare(Integer o1, Integer o2) {

			if ((factorCompare*(ranks.getRankValue(o2.intValue())))>(factorCompare*ranks.getRankValue(o1.intValue()))){
				return 1;
			}
			else if ((factorCompare*(ranks.getRankValue(o2.intValue())))<(factorCompare*ranks.getRankValue(o1.intValue()))){
				return -1;
			}
			else {
				return 0;
			}
		}

	}
	class SurfaceRank implements Comparable<SurfaceRank>{
	       private int surface;
	       private int rank;

	       public int getSurface() {
			return surface;
		}
		public int getRank() {
			return rank;
		}

			public SurfaceRank(int surface,int rank2){
				this.surface=surface;
				this.rank=rank2;
			}
			public int compareTo(SurfaceRank o) {

				if ((factorCompare*((o).getRank()))>(factorCompare*rank)){
					return 1;
				}
				else if ((factorCompare*((o).getRank()))<(factorCompare*rank)){
					return -1;
				}
				else return 0;
			}

		}
	private SurfaceComponent surfaceComponent;
	private SurfaceComponent surfaceComponentMirrored;
	private JLabel labelScore;
	private JLabel labelScoreMirrored;
	private JButton buttonNext;
	private JButton buttonPrevious;
	private ProgressMonitor progressMonitor;

	private int currentSurface;
	private int currentSurfaceMirrored;
	private int indexSurface;
	private int bestNRanks;
	private int maxJump;
	private int stackWidth;

	private int factorCompare;

	private Ranks ranks;
	private Task task;

	private SurfaceRank [] surfaceRanksBests;
	private SurfaceRank [] surfaceRanksBestsMirrored;
	 class Task extends SwingWorker<Void, Void> {
	        @Override
	        public Void doInBackground() {

	            int progress = 0;
	            setProgress(0);
	            ArrayList<SurfaceRank> surfaceRankBestsList=new ArrayList<SurfaceRank>(bestNRanks+1);
	            for (int i=0;i<bestNRanks;i++){
	            	int rank=ranks.getRankValue(i);
	            	   surfaceRankBestsList.add(new SurfaceRank(i,rank));

	            }
	               int iMin=surfaceRankBestsList.indexOf(Collections.min(surfaceRankBestsList));
	               int iMax=surfaceRankBestsList.indexOf(Collections.max(surfaceRankBestsList));

	               for (int i=0;i<ranks.getSize();i++){
	            	   int rank=ranks.getRankValue(i);

	            	   SurfaceRank surfaceRank=new SurfaceRank(i,rank);

	            		   if (surfaceRank.compareTo(surfaceRankBestsList.get(iMax))<0){
	            			   surfaceRankBestsList.add(new SurfaceRank(i,rank));
	            		       surfaceRankBestsList.remove(iMax);
	            		       if (surfaceRank.compareTo(surfaceRankBestsList.get(iMin))<0)
	            			      iMin=surfaceRankBestsList.size()-1;

	            	           iMax=surfaceRankBestsList.indexOf(Collections.max(surfaceRankBestsList));
	            		   }

	            	   if (0==(i % (ranks.getSize()/100)) && i>=ranks.getSize()/100){

	        	        	  progress++;
	        	        	  setProgress(progress);

	        		   }

	               }
	               Collections.sort(surfaceRankBestsList);

	               surfaceRanksBests=new SurfaceRank[bestNRanks];
	               surfaceRanksBestsMirrored=new SurfaceRank[bestNRanks];

	               for (int i=0;i<bestNRanks;i++){
	            	   surfaceRanksBests[i]=surfaceRankBestsList.get(i);
	            	   int mirroredSurface=getMirroredSurface(surfaceRankBestsList.get(i).getSurface());
	                   surfaceRanksBestsMirrored[i]=new SurfaceRank(mirroredSurface,ranks.getRankValue(mirroredSurface));
	               }
	               ranks=null;

	            return null;
	        }

	        @Override
	        public void done() {

	        	setTitle(AIRanksTool.getUIText("Result_Title"));
        		initUI();
        		pack();
        		setVisible(true);
        		ranks=null;

	        }
	    }

	public RanksResult(JFrame parent,Ranks ranks,int bestNRanks,boolean ascendant){

		super(parent,true);
		//this.parent=parent;
		this.bestNRanks=bestNRanks;
		this.ranks=ranks;
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.factorCompare=ascendant?-1:1;
		this.maxJump=ranks.getMaxJump();
		this.stackWidth=ranks.getStackWidth();
		progressMonitor=new ProgressMonitor(parent,AIRanksTool.getUIText("Result_Progress_Message"),"",0,100);
		progressMonitor.setProgress(0);
		task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();

	}

  private int getMirroredSurface(int surface){

		int surfaceWork=surface;
		int surfaceMirrored=0;

		int factorD=(int ) Math.pow(2*maxJump+1,stackWidth-2);
		for (int i=0;i<stackWidth-1;i++){
			int val=surfaceWork % (2*maxJump+1);
			surfaceMirrored+=factorD*(2*maxJump-val);
			surfaceWork/=(2*maxJump+1);
			factorD/=9;
		}
		return surfaceMirrored;

  }

	private void initUI(){

		indexSurface=0;
		currentSurface=surfaceRanksBests[indexSurface].getSurface();

		surfaceComponent=new SurfaceComponent (maxJump,stackWidth,currentSurface);
		labelScore=new JLabel(AIRanksTool.getUIText("Result_Score")+surfaceRanksBests[indexSurface].getRank());

		currentSurfaceMirrored=surfaceRanksBestsMirrored[indexSurface].getSurface();
		surfaceComponentMirrored=new SurfaceComponent(maxJump,stackWidth,currentSurfaceMirrored);
	    labelScoreMirrored=new JLabel(AIRanksTool.getUIText("Result_Score")+surfaceRanksBestsMirrored[indexSurface].getRank());

		buttonNext=new JButton(AIRanksTool.getUIText("Result_Next"));
		buttonNext.setActionCommand("next");
		buttonNext.addActionListener( this);
		buttonNext.setMnemonic('N');
		buttonPrevious=new JButton(AIRanksTool.getUIText("Result_Previous"));
		buttonPrevious.setActionCommand("previous ");
		buttonPrevious.setEnabled(false);
		buttonPrevious.addActionListener( this);
		buttonPrevious.setMnemonic('P');

		JPanel pane=new JPanel(new BorderLayout());
		JPanel surfacePane=new JPanel(new BorderLayout());
		surfacePane.add(surfaceComponent,BorderLayout.CENTER);
		surfacePane.add(labelScore,BorderLayout.SOUTH);
		JPanel surfacePaneMirrored=new JPanel(new BorderLayout());
		surfacePaneMirrored.add(surfaceComponentMirrored,BorderLayout.CENTER);
		surfacePaneMirrored.add(labelScoreMirrored,BorderLayout.SOUTH);
		JPanel highPane=new JPanel();
		highPane.add(surfacePane);
		highPane.add(surfacePaneMirrored);
		JPanel buttonsPane=new JPanel();

		buttonsPane.add(buttonPrevious);
		buttonsPane.add(buttonNext);
		pane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		pane.add(highPane,BorderLayout.CENTER);
		pane.add(buttonsPane,BorderLayout.SOUTH);
		getContentPane().add(pane);

		//getContentPane().add(surfaceComponent);

	}

	public void actionPerformed(ActionEvent e) {

		if ("next".equals(e.getActionCommand())) {
			if (indexSurface<bestNRanks-1){
	           indexSurface++;
	          currentSurface=surfaceRanksBests[indexSurface].getSurface();
	          surfaceComponent.setSurface(currentSurface);
	          labelScore.setText(AIRanksTool.getUIText("Result_Score")+ surfaceRanksBests[indexSurface].getRank());

	          currentSurfaceMirrored=surfaceRanksBestsMirrored[indexSurface].getSurface();
	  		surfaceComponentMirrored.setSurface(currentSurfaceMirrored);
	  	    labelScoreMirrored.setText(AIRanksTool.getUIText("Result_Score")+surfaceRanksBestsMirrored[indexSurface].getRank());

	          if (indexSurface>0){
	        	  buttonPrevious.setEnabled(true);
	          }
	          if (indexSurface==bestNRanks-1){
	        	  buttonNext.setEnabled(false);
	          }

			}

	    } else {
	    	if (indexSurface>0){
		           indexSurface--;
		          currentSurface=surfaceRanksBests[indexSurface].getSurface();
		          surfaceComponent.setSurface(currentSurface);
		          labelScore.setText(AIRanksTool.getUIText("Result_Score")+ surfaceRanksBests[indexSurface].getRank());

		          currentSurfaceMirrored=getMirroredSurface(currentSurface);
		  		surfaceComponentMirrored.setSurface(currentSurfaceMirrored);
		  	    labelScoreMirrored.setText(AIRanksTool.getUIText("Result_Score")+surfaceRanksBestsMirrored[indexSurface].getRank());;

		          if (indexSurface<bestNRanks-1){
		        	  buttonNext.setEnabled(true);
		          }
		          if (indexSurface==0){
		        	  buttonPrevious.setEnabled(false);
		          }

				}
	    }

	}

	public void propertyChange(PropertyChangeEvent evt) {
		 if ("progress" == evt.getPropertyName() ) {
	            int progress = (Integer) evt.getNewValue();
	            progressMonitor.setProgress(progress);
	            String message =
	                String.format(AIRanksTool.getUIText("Result_Progress_Note"), progress);
	            progressMonitor.setNote(message);
		 }

	    if (progressMonitor.isCanceled()) {
	                    task.cancel(true);

	    }

	}

}
