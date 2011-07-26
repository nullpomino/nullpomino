package cx.it.nullpo.nm8.neuro.test;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.core.NEURO;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.event.DebugEvent;
import cx.it.nullpo.nm8.neuro.plugin.AbstractPlugin;

/**
 * Simple test plugin which dispatches events every second.
 * @author Zircean
 *
 */
public class EventDispatcherPlugin extends AbstractPlugin {
	
	boolean stopping;

	public String getName() {
		return "Test Event Dispatcher";
	}

	public float getVersion() {
		return 0.0F;
	}
	
	public String getAuthor() {
		return "Sollux Captor";
	}
	
	public void init(NEURO parent) throws PluginInitializationException {
		super.init(parent);
		new EventThread().start();
	}
	
	public void stop() {
		stopping = true;
	}

	public void draw(NFGraphics g) { }
	
	protected synchronized void sendDebugMessage(String str) {
		neuro.dispatchEvent(new DebugEvent(this,DebugEvent.TYPE_DEBUG,str));
	}
	
	class EventThread extends Thread {
		public void run() {
			while (!stopping) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					sendDebugMessage("ERROR: Sleep interrupted");
				}
				sendDebugMessage("Debug event");
			}
		}
	}

}
