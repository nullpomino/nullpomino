package cx.it.nullpo.nm8.neuro.light;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.neuro.core.NEUROCore;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.plugin.Nullterm;

public class NEUROLight extends NEUROCore {
	
	/**
	 * Constructs a NEUROLight.
	 */
	public NEUROLight(NFSystem sys) {
		super(sys);
		try {
			new Nullterm().init(this);
		} catch (PluginInitializationException e) {}
	}

	public String getName() {
		return "NEURO Light";
	}

	public float getVersion() {
		return 0.1F;
	}

	public void draw(NFGraphics g) { }

}
