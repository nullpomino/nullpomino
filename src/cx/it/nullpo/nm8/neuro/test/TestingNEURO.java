package cx.it.nullpo.nm8.neuro.test;

import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.neuro.core.NEUROCore;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.plugin.nullterm.Nullterm;

/**
 * A type of NEURO which is used to verify that events are being passed around correctly.
 * @author Zircean
 *
 */
public class TestingNEURO extends NEUROCore {
	private static final long serialVersionUID = -6550770298827744267L;

	/**
	 * Constructs a TestingNEURO.
	 */
	public TestingNEURO() {
		super(null);
		try {
			new Nullterm().init(this);
			new EventDispatcherPlugin().init(this);
		} catch (PluginInitializationException e) {
			System.err.println("Shit is all wrong, cap'n...");
		}
	}

	public String getName() {
		return "Test NEURO";
	}

	public float getVersion() {
		return 0.0F;
	}

	@Override
	public void drawComponent(NFGraphics g) { }

}
