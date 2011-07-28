package cx.it.nullpo.nm8.neuro.nlt;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.nlt.util.NDimension;


public abstract class NComponent {

	public void update(NFGraphics g) { }
	
	public NDimension getSize() { 
		return null; 
	}
	
	public void setSize(NDimension nd) { }
}
