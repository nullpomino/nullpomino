package cx.it.nullpo.nm8.neuro.network;

import cx.it.nullpo.nm8.network.NMMPMessage;
import cx.it.nullpo.nm8.network.NMTPRequest;
import cx.it.nullpo.nm8.network.NMTPResponse;

public class DummyCommunicator implements NetworkCommunicator {

	public NMTPResponse send(NMTPRequest req) {
		return null;
	}

	public void send(NMMPMessage message) { }

}
