package cx.it.nullpo.nm8.neuro.nwt.toolkit;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.nwt.NScreen;

/**
 * An interface for classes that can be used to do the overlay drawing work behind the scenes.
 * @author Zircean
 *
 */
public interface NToolkit {

	/**
	 * Draws the given screen using the supplied graphics context.
	 */
	public void draw(NScreen sc, NFGraphics g);
	
}
