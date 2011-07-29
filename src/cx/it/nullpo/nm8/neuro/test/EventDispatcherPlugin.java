package cx.it.nullpo.nm8.neuro.test;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
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
		return "TEST EVENT DISPATCHER (THIS IS STUPID)";
	}

	public float getVersion() {
		return 0.0F;
	}
	
	public String getAuthor() {
		return "KARKAT VANTAS";
	}
	
	public void init() throws PluginInitializationException {
		new EventThread().start();
	}
	
	public void stop() {
		stopping = true;
	}

	public void draw(NFGraphics g) { }
	
	protected synchronized void sendDebugMessage(String str) {
	}
	
	class EventThread extends Thread {
		public void run() {
			while (!stopping) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					dispatchEvent(new DebugEvent(this,DebugEvent.TYPE_ERROR,"Sleep interrupted"));
				}
				dispatchEvent(new DebugEvent(this,DebugEvent.TYPE_DEBUG,"Debug event"));
			}
		}
	}

}
