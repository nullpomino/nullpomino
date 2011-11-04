package cx.it.nullpo.nm8.neuro.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cx.it.nullpo.nm8.neuro.core.NEURO;
import cx.it.nullpo.nm8.neuro.core.NEUROBase;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.plugin.nullterm.Nullterm;

/**
 * A type of NEURO which is used to verify that events are being passed around correctly.
 * @author Zircean
 *
 */
public class TestingNEURO extends NEUROBase {
	/** Log */
	private static Log log = LogFactory.getLog(TestingNEURO.class);

	/**
	 * Constructs a TestingNEURO.
	 */
	private TestingNEURO() {
		super();

	}

	public static NEURO create() {
		TestingNEURO neuro = new TestingNEURO();
		try {
			new Nullterm().init(neuro);
			new EventDispatcherPlugin().init(neuro);
		} catch (PluginInitializationException e) {
			log.error("Shit is all wrong, cap'n...", e);
		}
		return neuro;
	}

	public String getName() {
		return "Test NEURO";
	}

	public float getVersion() {
		return 0.0F;
	}

}
