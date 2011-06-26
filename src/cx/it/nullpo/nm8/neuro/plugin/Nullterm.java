package cx.it.nullpo.nm8.neuro.plugin;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.core.NEURO;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.event.DebugEvent;

/**
 * Nullterm is a basic plugin which currently listens for DebugEvents and prints their detail message out to the terminal.
 * @author Zircean
 *
 */
public class Nullterm extends AbstractPlugin {

	public String getName() {
		return "nullterm";
	}

	public float getVersion() {
		return 0.1F;
	}
	
	public void init(NEURO parent) throws PluginInitializationException {
		super.init(parent);
		parent.addListener(this,DebugEvent.class);
	}

	public void stop() { }

	public void draw(NFGraphics g) {
		// TODO Auto-generated method stub
	}
	
	public void receiveEvent(DebugEvent e) {
		System.out.println(e.getSource()+" dispatched (systime="+System.currentTimeMillis()+"): "+e.getMessage());
	}

}
