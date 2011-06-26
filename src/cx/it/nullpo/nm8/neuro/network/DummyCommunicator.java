package cx.it.nullpo.nm8.neuro.network;

import cx.it.nullpo.nm8.network.NMMPMessage;
import cx.it.nullpo.nm8.network.NMTPRequest;
import cx.it.nullpo.nm8.network.NMTPResponse;

/**
 * A dummy NetworkCommunicator class. This class does not attempt to do any networking, or much of anything at all.
 * @author Zircean
 *
 */
public class DummyCommunicator implements NetworkCommunicator {

	public NMTPResponse send(NMTPRequest req) {
		return null;
	}

	public void send(NMMPMessage message) { }

}
