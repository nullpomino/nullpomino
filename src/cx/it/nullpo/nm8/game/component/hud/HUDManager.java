package cx.it.nullpo.nm8.game.component.hud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * HUDManager: Manages HUD elements
 */
public class HUDManager implements Serializable {
	private static final long serialVersionUID = 2230832613218790688L;

	/** HUD element list */
	public List<HUDElement> listHUD = Collections.synchronizedList(new ArrayList<HUDElement>());

	/**
	 * Constructor
	 */
	public HUDManager() {
	}

	/**
	 * Copy Constructor
	 * @param s Copy Source
	 */
	public HUDManager(HUDManager s) {
		copy(s);
	}

	/**
	 * Copy from another HUDManager
	 * @param s Copy Source
	 */
	public void copy(HUDManager s) {
		synchronized (listHUD) {
			listHUD.clear();
			listHUD.addAll(s.listHUD);
		}
	}

	/**
	 * Get the number of text lines of all HUD elements
	 * @return Total number of text lines
	 */
	public int getAllNumberOfLines() {
		int total = 0;
		synchronized (listHUD) {
			Iterator<HUDElement> it = listHUD.iterator();

			while(it.hasNext()) {
				total += it.next().getNumberOfLines();
			}
		}
		return total;
	}
}
