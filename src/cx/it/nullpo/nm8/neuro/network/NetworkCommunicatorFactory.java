package cx.it.nullpo.nm8.neuro.network;

import cx.it.nullpo.nm8.neuro.error.NetworkInitializationException;

/**
 * A factory for creating NetworkCommunicators.
 * @author Zircean
 *
 */
public class NetworkCommunicatorFactory {

	/**
	 * Tries to create a NetworkCommunicator to allow communication with the server at the given hostname.
	 * @param hostname the location of the server
	 * @return a NetworkCommunicator which connects to that server
	 * @throws NetworkInitializationException if something goes wrong creating the connection
	 */
	public static NetworkCommunicator create(String hostname) throws NetworkInitializationException {
		return new DummyCommunicator();
	}
}
