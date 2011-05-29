package cx.it.nullpo.nm8.neuro.light;

import java.io.IOException;
import java.util.List;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.NEURO;
import cx.it.nullpo.nm8.neuro.NEUROPlugin;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.event.EndGameListener;
import cx.it.nullpo.nm8.neuro.event.NEUROEvent;
import cx.it.nullpo.nm8.neuro.event.TCPSendEvent;
import cx.it.nullpo.nm8.neuro.event.TCPSendListener;
import cx.it.nullpo.nm8.neuro.plugin.TCPStack;

public class NEUROLight implements NEURO {
	
	List<NEUROPlugin> registeredPlugins;
	List<TCPSendListener> registeredTCPSendListeners;
	
	public NEUROLight() {
		try {
			TCPStack tcp = new TCPStack(this, "", 0);
		} catch (PluginInitializationException e) {
			System.out.println("NEUROLight error: plugin initialization exception");
		}
	}

	@Override
	public void addPlugin(NEUROPlugin p) {
		registeredPlugins.add(p);
	}

	@Override
	public void addEndGameListener(EndGameListener l) { }
	
	@Override
	public void addTCPSendListener(TCPSendListener l) {
		registeredTCPSendListeners.add(l);		
	}

	@Override
	public void dispatchEvent(NEUROEvent e) {
		if (e instanceof TCPSendEvent) {
			for (TCPSendListener l : registeredTCPSendListeners) {
				try {
					l.sendData((TCPSendEvent)e);
				} catch (IOException e1) {
					System.out.println("NEUROLight error: failed to send data");
				}
			}
		}
	}

	@Override
	public void draw(NFGraphics g) {
		g.drawString("NEUROLight",0,0);
	}	

}
