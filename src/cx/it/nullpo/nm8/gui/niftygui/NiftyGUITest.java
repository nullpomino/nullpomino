package cx.it.nullpo.nm8.gui.niftygui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cx.it.nullpo.nm8.gui.framework.NFGame;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.neuro.core.NEURO;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.tools.TimeProvider;

public class NiftyGUITest implements NFGame {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(NiftyGUITest.class);

	private Nifty nifty;
	private boolean niftyInited;

	public boolean isEnableNEURO() {
		return false;
	}

	public String getName() {
		return "NiftyGUITest";
	}

	public float getVersion() {
		return 0;
	}

	public String getAuthor() {
		return "The NullpoMino Team";
	}

	public void init(NEURO parent) throws PluginInitializationException {
	}

	public void draw(NFGraphics g) {
	}

	public void stop() {
	}

	public void init(NFSystem sys) {

	}

	public void update(NFSystem sys, long delta) {
		if(nifty != null) {
			nifty.update();
		}
		sys.setWindowTitle("NullpoMino GUI Test (FPS:" + sys.getFPS() + "/" + sys.getTargetFPS() + ") " + delta);
	}

	public void render(NFSystem sys, NFGraphics g) {
		try {
			if(!niftyInited) {
				try {
					nifty = new Nifty(new NFRenderDevice(sys),
									  new NFSoundDevice(sys),
									  new NFInputSystem(sys.getKeyboard(), sys.getMouse()),
									  new TimeProvider());
					log.debug("NiftyGUI created");

					nifty.fromXml("data/xml/niftyguitest.xml", "start");
				} catch (Exception e) {
					log.error("NiftyGUI init fail", e);
				}
				niftyInited = true;
			}

			if(nifty != null) {
				nifty.render(true);
			}
		} catch (Exception e) {
			log.error("NiftyGUI render fail", e);
		}
	}

	public void onExit(NFSystem sys) {
		try {
			if(nifty != null) {
				nifty.exit();
			}
		} catch (Exception e) {
			log.debug("Nifty exception on exit()", e);
		}

		// Sometimes nifty's thread is keep running. I don't know why
		System.exit(0);
	}
}
