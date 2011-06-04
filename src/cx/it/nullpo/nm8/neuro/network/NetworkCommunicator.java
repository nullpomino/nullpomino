package cx.it.nullpo.nm8.neuro.network;

import cx.it.nullpo.nm8.network.NMMPMessage;
import cx.it.nullpo.nm8.network.NMTPRequest;
import cx.it.nullpo.nm8.network.NMTPResponse;

public interface NetworkCommunicator {

	/**
	 * Sends the given NMTPRequest and returns its corresponding NMTPResponse.
	 */
	NMTPResponse send(NMTPRequest req);
	
	/**
	 * Sends the given NMMPMessage.
	 */
	void send(NMMPMessage message);
}
