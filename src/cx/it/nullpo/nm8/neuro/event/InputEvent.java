package cx.it.nullpo.nm8.neuro.event;

/**
 * This is a marker class, to be used for events that are input events. I was going to use it to try
 * and queue events better, but I'm not sure if I'm going to continue with that...
 * @author Zircean
 *
 */
public class InputEvent extends NEUROEvent {
	
	private static final long serialVersionUID = 2518551253228166178L;

	public InputEvent(Object source) {
		super(source);
	}

}
