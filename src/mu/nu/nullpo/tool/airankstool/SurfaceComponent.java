package mu.nu.nullpo.tool.airankstool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

public class SurfaceComponent extends JComponent {

	private static final long serialVersionUID = 1L;

	private int [] surfaceDecoded;
	private int minHeight;
	private int maxHeight;
	private int componentHeight;
	private int stackWidth;
	private int maxJump;
	private int baseSizeX;
	private int baseSizeY;
    public SurfaceComponent(int maxJump,int stackWidth, int surface){
    	super();
    
    	this.surfaceDecoded=new int[stackWidth-1];
    
    
    	baseSizeX=10;
    	baseSizeY=10;
    	this.stackWidth=stackWidth;
    	this.maxJump=maxJump;
    	componentHeight=maxJump*(stackWidth-1);
    	setPreferredSize(getPreferredSize());
    	setSurface(surface);
    
    
    }
    public Dimension getPreferredSize(){
        return new Dimension(baseSizeX*(stackWidth+1)+2*baseSizeX,componentHeight*baseSizeY+2*baseSizeY);

    }

    public void setSurface(int surface){
    	int height=0;
    	minHeight=0;
    	maxHeight=0;
    	int workSurface=surface;
    	for (int i=0;i<stackWidth-1;i++){
    		surfaceDecoded[i]=(workSurface % (2*maxJump+1))-maxJump;
    		height+=surfaceDecoded[i];
    		if (height>maxHeight){
    			maxHeight=height;
    		}
    		if (height<minHeight){
    			minHeight=height;
    		}
    		workSurface/=(2*maxJump+1);
    	}
    	repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Rectangle bounds=g.getClipBounds();
        g.setColor(Color.BLACK);
        baseSizeX=bounds.width/(stackWidth+3);
        baseSizeY=bounds.height/(componentHeight+2);
        g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
        g.setColor(Color.WHITE);
        int posX=bounds.x+baseSizeX;
        int posY=bounds.y+baseSizeY+maxHeight*baseSizeY;
        for (int x=0;x<stackWidth-1;x++){
        	g.drawLine(posX, posY, posX+baseSizeX, posY);
        	posX+=baseSizeX;
        	g.drawLine(posX,posY, posX,posY-surfaceDecoded[x]*baseSizeY);
        	posY-=surfaceDecoded[x]*baseSizeY;
        }
        g.drawLine(posX, posY, posX+baseSizeX, posY);

    }

}
