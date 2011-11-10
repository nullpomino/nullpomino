package cx.it.nullpo.nm8.gui.game;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class MainScreenController implements ScreenController {
	/** Log */
	private Log log = LogFactory.getLog(MainScreenController.class);

	/** Owner class */
	protected NullpoMinoNiftyGUI owner;

	public MainScreenController() {
		log.warn("Parameter-less constractor is called");
	}

	public MainScreenController(NullpoMinoNiftyGUI owner) {
		this.owner = owner;
		log.trace("Constractor MainScreenController(NullpoMinoNiftyGUI owner) is called");
	}

	public void bind(Nifty nifty, Screen screen) {
		log.trace("bind");
	}

	public void onStartScreen() {
		log.trace("onStartScreen");
	}

	public void onEndScreen() {
		log.trace("onEndScreen");
	}

	@NiftyEventSubscriber(id="buttonStart")
	public void onButtonStartClickEvent(final String id, final ButtonClickedEvent e) {
		if(owner == null) {
			log.error("owner == null! Maybe parameter-less constractor was used?");
		} else {
			owner.gameStart();
		}
	}

	@NiftyEventSubscriber(id="buttonQuit")
	public void onButtonQuitClickEvent(final String id, final ButtonClickedEvent e) {
		owner.sys.exit();
	}
}
