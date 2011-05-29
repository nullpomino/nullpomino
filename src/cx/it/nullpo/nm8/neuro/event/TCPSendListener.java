package cx.it.nullpo.nm8.neuro.event;

import java.io.IOException;

/**
 * The TCPSendListener interface is implemented by all plugins that wish to send data to the server.
 * Normally, this is only TCPStack.
 * @author Zircean
 *
 */
public interface TCPSendListener {

	/**
	 * Called when a request to send data is made.
	 * @param e the event holding the requested data
	 * @throws IOException if something goes wrong sending the data
	 */
	public void sendData(TCPSendEvent e) throws IOException;
	
}
