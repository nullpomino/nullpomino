package cx.it.nullpo.nm8.neuro.event;

/**
 * An event type representing a user quit operation.
 * @author Zircean
 *
 */
public class QuitEvent extends NEUROEvent {
	
	private static final long serialVersionUID = 6044706965290782124L;

	public QuitEvent(Object source) {
		super(source);
	}

}
