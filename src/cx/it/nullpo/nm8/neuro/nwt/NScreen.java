package cx.it.nullpo.nm8.neuro.nwt;

import java.util.LinkedList;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.nwt.util.NBounds;

public class NScreen {

	/** Width of this Screen. */
	private int width;
	/** Height of this Screen. */
	private int height;
	/** Components list */
	private LinkedList<NComponentWrapper> components;
	
	public void addComponent(NComponent nc, float x, float y) {
		NBounds nb = new NBounds(x,y,nc.getWidth(),nc.getHeight());
		components.add(new NComponentWrapper(nc,nb));
	}
	
	public void draw(NFGraphics g) {
		int translateX = 0, translateY = 0;
		for (NComponentWrapper nc : components) {
			// Translate context so the top-left of the component is 0,0
			translateX = (int)(nc.getBounds().getX()*width);
			translateY = (int)(nc.getBounds().getY()*height);
			g.translate(translateX,translateY);
			// Draw component
			nc.getComponent().update(g);
			// Translate context back to 0,0 of this layer
			g.translate(-translateX,-translateY);
		}
	}
	
	private class NComponentWrapper {
		
		/** The component being wrapped. */
		private NComponent comp;
		/** The location and size of the component. */
		private NBounds bounds;
		
		NComponentWrapper(NComponent nc, NBounds nb) {
			comp = nc;
			bounds = nb;
		}
		
		public NComponent getComponent() {
			return comp;
		}
		
		public NBounds getBounds() {
			return bounds;
		}
		
	}
}
