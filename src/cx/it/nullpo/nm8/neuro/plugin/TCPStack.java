package cx.it.nullpo.nm8.neuro.plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.core.NEURO;
import cx.it.nullpo.nm8.neuro.core.NEUROPlugin;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.event.TCPSendEvent;
import cx.it.nullpo.nm8.neuro.event.TCPSendListener;

/**
 * TCPStack handles sending and receiving data from the NullpoMino server it is connected to.
 * @author Zircean
 *
 */
public class TCPStack extends AbstractPlugin {
	
	private Socket sock;
	private Writer out;
	private Reader in;

	public TCPStack(NEURO parent, String hostname, int port) throws PluginInitializationException {
		super(parent);
		
		// Create the socket
		sock = null;
		try {
			sock = new Socket(hostname, port);
		} catch (UnknownHostException e) {
			throw new PluginInitializationException("Unknown host: "+hostname+":"+port, e);
		} catch (IOException e) {
			throw new PluginInitializationException("Could not create socket to server", e);
		}
		
		// Get the writer and reader streams
		try {
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException e) {
			throw new PluginInitializationException("Could not create send stream", e);
		}
		
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} catch (IOException e) {
			throw new PluginInitializationException("Could not create recieve stream", e);
		}
	}

	@Override
	public String getName() {
		return "TCP Stack";
	}

	@Override
	public float getVersion() {
		return 0.1F;
	}

	@Override
	public void draw(NFGraphics g) { }
	
	/**
	 * Writes the given string to the socket.
	 * @param s the data to write
	 * @throws IOException if the data could not be sent
	 */
	protected void write(String s) throws IOException {
		out.write(s);
	}

}
