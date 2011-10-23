package cx.it.nullpo.nm8.gui.niftygui;

import cx.it.nullpo.nm8.gui.framework.NFGame;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.neuro.core.NEURO;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.tools.TimeProvider;

public class NiftyGUITest implements NFGame {
	private static final long serialVersionUID = 1L;
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
	}

	public void render(NFSystem sys, NFGraphics g) {
		try {
			if(!niftyInited) {
				try {
					nifty = new Nifty(new NFRenderDevice(sys),
									  new NFSoundDevice(sys),
									  new NFInputSystem(sys.getKeyboard(), sys.getMouse()),
									  new TimeProvider());
					System.out.println("NiftyGUI created");

					nifty.fromXml("data/xml/niftyguitest.xml", "start");
				} catch (Exception e) {
					e.printStackTrace();
				}
				niftyInited = true;
			}

			if(nifty != null) {
				nifty.render(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onExit(NFSystem sys) {
	}
}
