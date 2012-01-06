package cx.it.nullpo.nm8.gui.framework;

import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.plugin.AbstractPlugin;

/**
 * A NFGame implementation that handles most NEURO-related stuff for you
 * (Currently it's mostly empty)
 */
public class NFNEUROGame extends AbstractPlugin implements NFGame {
	private static final long serialVersionUID = 4495602927839234542L;

	public String getName() {
		return "Untitled NEURO Game";
	}

	public float getVersion() {
		return 0;
	}

	public String getAuthor() {
		return "Unknown";
	}

	public void stop() {
	}

	public boolean isEnableNEURO() {
		return true;
	}

	public void init(NFSystem sys) {
	}

	public void update(NFSystem sys, long delta) {
	}

	public void render(NFSystem sys, NFGraphics g) {
	}

	public void onExit(NFSystem sys) {
	}

	@Override
	protected void init() throws PluginInitializationException {
	}
}
