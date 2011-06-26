package cx.it.nullpo.nm8.neuro.test;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.core.NEUROCore;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.plugin.Nullterm;

public class TestingNEURO extends NEUROCore {
	
	public TestingNEURO() {
		super();
		try {
			new Nullterm().init(this);
		} catch (PluginInitializationException e) {
			System.err.println("Shit is all wrong, cap'n...");
		}
	}

	public String getName() {
		return "NEURO Light";
	}

	public float getVersion() {
		return 0.1F;
	}

	public void draw(NFGraphics g) {
		// TODO Draw this NEURO (will probably not draw anything except the game itself)
	}

}
