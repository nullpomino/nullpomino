package cx.it.nullpo.nm8.util;

import java.io.File;

import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cx.it.nullpo.nm8.gui.framework.NFSystem;

/**
 * This class handles the global config file
 */
public class NGlobalConfig {
	/** Log */
	private static Log log = LogFactory.getLog(NGlobalConfig.class);

	/** Global config */
	protected static CustomProperties propGlobal = null;

	/**
	 * Get global config
	 * @return Global config
	 */
	public static CustomProperties getConfig() {
		if(propGlobal == null) load();
		return propGlobal;
	}

	/**
	 * (Re-)load the global config file. If does not exist it will create one.
	 */
	public static void load() {
		propGlobal = CustomProperties.load("user/setting/global.cfg");

		// When load fails
		if(propGlobal == null) {
			propGlobal = new CustomProperties();

			// Create folder if does not exist
			File dir = new File("user/setting/");
			if(!dir.exists()) {
				if(!dir.mkdirs()) {
					log.error("Can't create user/setting/ directory!");
				} else {
					log.info("user/setting/ directory created.");
				}
			}

			// Create config file
			save();
		}
	}

	/**
	 * Save the global config file.
	 * @return true if successful
	 */
	public static boolean save() {
		if(propGlobal == null) load();

		boolean result = propGlobal.save("user/setting/global.cfg", "NullpoMino Global Config");
		if(!result) {
			log.error("Can't write global config file to user/setting/global.cfg");
		}

		return result;
	}

	/**
	 * Set Swing's native look&feel
	 */
	public static void applySwingLookFeel() {
		if(getConfig().getProperty("sys.nativelookfeel", true)) {
			try {
				UIManager.getInstalledLookAndFeels();
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch(Throwable e) {
				log.warn("Failed to set native look&feel", e);
			}
		}
	}

	/**
	 * Apply common options to NFSystem. It should be called before sys.init()
	 * @param sys NFSystem
	 */
	public static void applyNFSystem(NFSystem sys) {
		CustomProperties prop = getConfig();
		applyNFSystem(sys, prop);
	}

	/**
	 * Apply common options to NFSystem. It should be called before sys.init()
	 * @param sys NFSystem
	 * @param prop CustomProperties
	 */
	public static void applyNFSystem(NFSystem sys, CustomProperties prop) {
		sys.setTargetFPS(prop.getProperty("sys.fps", 60));
		sys.setSoundProviderType(prop.getProperty("sys.soundprovider", NFSystem.SOUND_PROVIDER_OPENAL));
		sys.setEnableAlphaImage(prop.getProperty("sys.enablealpha", true));
		sys.setEnableColorFilter(prop.getProperty("sys.enablecolorfilter", true));
		sys.setEnableGradient(prop.getProperty("sys.enablegradient", true));
	}
}
