package cx.it.nullpo.nm8.neuro.network;

import cx.it.nullpo.nm8.neuro.error.NetworkInitializationException;

public class NetworkCommunicatorFactory {

	public static NetworkCommunicator create(String hostname) throws NetworkInitializationException {
		return new DummyCommunicator();
	}
}
