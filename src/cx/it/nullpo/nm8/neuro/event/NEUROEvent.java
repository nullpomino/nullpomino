package cx.it.nullpo.nm8.neuro.event;

import java.util.EventObject;

/**
 * A NEUROEvent is a unit of data which is passed around between NEURO and its registered plugins.
 * @author Zircean
 *
 */
public class NEUROEvent extends EventObject {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -148210211609711271L;
	
	private long time;

	/**
	 * Creates a new NEUROEvent with the given object as the source.
	 */
	public NEUROEvent(Object source) {
		super(source);
		time = System.currentTimeMillis();
	}
	
	/**
	 * Gets the time of creation of this event.
	 */
	public long getTime() {
		return time;
	}

}
