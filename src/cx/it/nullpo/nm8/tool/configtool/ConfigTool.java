package cx.it.nullpo.nm8.tool.configtool;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import cookxml.cookswing.CookSwing;
import cookxml.core.IdReference;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.util.CustomProperties;
import cx.it.nullpo.nm8.util.NGlobalConfig;

/**
 * NullpoMino Config Tool
 */
public class ConfigTool {
	// Constants for key config
	protected static final int MAX_PLAYERS = 2;
	protected static final int MAX_RULE_KIND = 2;
	protected static final int MAX_KEY_SLOTS = 4;
	protected static final int MAX_KEY_KINDS = 9;

	/** CookSwing */
	protected CookSwing cookSwing;
	/** Main Window */
	protected Window mainWindow;

	/** Keycodes */
	protected int[][][][] keycodes = new int[MAX_PLAYERS][MAX_RULE_KIND][MAX_KEY_SLOTS][MAX_KEY_KINDS];

	/** Keycode Textboxes */
	protected JTextField[][][][] txtfldKeyCodeArray = new JTextField[MAX_PLAYERS][MAX_RULE_KIND][MAX_KEY_SLOTS][MAX_KEY_KINDS];

	// Copy from Classic/Standard button actions
	public ActionListener actCopyFrom0_0 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			copyKeyConfig(0, 1, 0);
		}
	};
	public ActionListener actCopyFrom0_1 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			copyKeyConfig(0, 0, 1);
		}
	};
	public ActionListener actCopyFrom1_0 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			copyKeyConfig(1, 1, 0);
		}
	};
	public ActionListener actCopyFrom1_1 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			copyKeyConfig(1, 0, 1);
		}
	};

	// Clear All button actions
	public ActionListener actClearAll0_0 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			clearKeyConfig(0, 0);
		}
	};
	public ActionListener actClearAll0_1 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			clearKeyConfig(0, 1);
		}
	};
	public ActionListener actClearAll1_0 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			clearKeyConfig(1, 0);
		}
	};
	public ActionListener actClearAll1_1 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			clearKeyConfig(1, 1);
		}
	};

	/**
	 * KeyAdapter for KeyConfig TextFields
	 */
	public KeyAdapter klKeyConfigTextField = new KeyAdapter() {
		@Override
		public void keyTyped(KeyEvent e) {
			e.consume();
		}
		@Override
		public void keyReleased(KeyEvent e) {
			e.consume();
		}
		@Override
		public void keyPressed(KeyEvent e) {
			int[] index = getKeyConfigTextFieldIndex(e.getSource());

			int player = index[0];
			int rule = index[1];
			int slot = index[2];
			int key = index[3];
			int keyCode = e.getKeyCode();
			//System.out.println("" + player + "," + rule + "," + slot + "," + key + " = " + keyCode);

			keycodes[player][rule][slot][key] = keyCode;
			updateKeyConfigTextField(player, rule, slot, key);

			e.consume();
		}
	};

	/**
	 * MouseAdapter for KeyConfig TextFields
	 */
	public MouseAdapter maKeyConfigTextField = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if(e.getButton() != MouseEvent.BUTTON1) {
				int[] index = getKeyConfigTextFieldIndex(e.getSource());
				int player = index[0];
				int rule = index[1];
				int slot = index[2];
				int key = index[3];
				keycodes[player][rule][slot][key] = KeyEvent.VK_UNDEFINED;
				updateKeyConfigTextField(player, rule, slot, key);
				e.consume();
			}
		}
	};

	/**
	 * Action for OK button
	 */
	public ActionListener actOK = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			saveConfigFromGUI(NGlobalConfig.getConfig());
			NGlobalConfig.save();
			mainWindow.dispose();
		}
	};

	/**
	 * Action for Cancel button
	 */
	public ActionListener actCancel = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			mainWindow.dispose();
		}
	};

	/**
	 * Get a JComponent by ID
	 * @param id ID
	 * @return JComponent
	 */
	protected JComponent getJComponent(String id) {
		IdReference ref = cookSwing.getId(id);

		if(ref != null) {
			if(ref.object instanceof JComponent) {
				return (JComponent)ref.object;
			} else {
				throw new UnsupportedOperationException("Specified element '" + id + "' is not a JComponent (" + ref.object + ")");
			}
		} else {
			throw new RuntimeException("Specified element '" + id + "' does not exist");
		}
	}

	/**
	 * Load config from a CustomProperties then set values to GUI components
	 * @param p CustomProperties
	 */
	protected void loadConfigToGUI(CustomProperties p) {
		// System tab
		JComboBox comboboxResolution = (JComboBox)getJComponent("sys_combobox_resolution");
		comboboxResolution.setSelectedItem(p.getProperty("sys.resolution.string", "640x480"));
		JComboBox comboboxSoundProvider = (JComboBox)getJComponent("sys_combobox_soundprovider");
		comboboxSoundProvider.setSelectedIndex(p.getProperty("sys.soundprovider", NFSystem.SOUND_PROVIDER_OPENAL));

		JTextField txtfldVolume = (JTextField)getJComponent("sys_txtfld_volume");
		txtfldVolume.setText(String.valueOf(p.getProperty("sys.soundvolume", 1f)));

		JSpinner spinnerFPS = (JSpinner)getJComponent("sys_spinner_fps");
		try {
			spinnerFPS.setValue(p.getProperty("sys.fps", 60));
		} catch (IllegalArgumentException e) {}

		JCheckBox chkboxFullscreen = (JCheckBox)getJComponent("sys_chkbox_fullscreen");
		chkboxFullscreen.setSelected(p.getProperty("sys.fullscreen", false));
		JCheckBox chkboxEnableSound = (JCheckBox)getJComponent("sys_chkbox_enablesound");
		chkboxEnableSound.setSelected(p.getProperty("sys.enablesound", true));
		JCheckBox chkboxNativeLookFeel = (JCheckBox)getJComponent("sys_chkbox_nativelookfeel");
		chkboxNativeLookFeel.setSelected(p.getProperty("sys.nativelookfeel", true));
		JCheckBox chkboxEnableAlpha = (JCheckBox)getJComponent("sys_chkbox_enablealpha");
		chkboxEnableAlpha.setSelected(p.getProperty("sys.enablealpha", true));
		JCheckBox chkboxEnableColorFilter = (JCheckBox)getJComponent("sys_chkbox_enablecolorfilter");
		chkboxEnableColorFilter.setSelected(p.getProperty("sys.enablecolorfilter", true));
		JCheckBox chkboxEnableGradient = (JCheckBox)getJComponent("sys_chkbox_enablegradient");
		chkboxEnableGradient.setSelected(p.getProperty("sys.enablegradient", true));

		// Swing Tab
		JCheckBox chkboxSwingUseBufferStrategy = (JCheckBox)getJComponent("swing_chkbox_usebufferstrategy");
		chkboxSwingUseBufferStrategy.setSelected(p.getProperty("swing.useBufferStrategy", true));

		// Slick tab
		JCheckBox chkboxSlickAWTKey = (JCheckBox)getJComponent("slick_chkbox_awtkey");
		chkboxSlickAWTKey.setSelected(p.getProperty("slick.awtkey", false));
		JCheckBox chkboxSlickJInput = (JCheckBox)getJComponent("slick_chkbox_jinput");
		chkboxSlickJInput.setSelected(p.getProperty("slick.jinput.enable", false));
		JTextField txtfldSlickJInput = (JTextField)getJComponent("slick_txtfld_jinput");
		try {
			txtfldSlickJInput.setText(p.getProperty("slick.jinput.keyboardID", "0"));
		} catch (IllegalArgumentException e) {}

		// Key Config
		for(int player = 0; player < MAX_PLAYERS; player++) {
			for(int rule = 0; rule < MAX_RULE_KIND; rule++) {
				for(int slot = 0; slot < MAX_KEY_SLOTS; slot++) {
					for(int key = 0; key < MAX_KEY_KINDS; key++) {
						String strID = getKeyConfigID(player,rule,slot,key);
						keycodes[player][rule][slot][key] = p.getProperty(strID, KeyEvent.VK_UNDEFINED);

						JTextField txtfldKey = (JTextField)getJComponent(strID);
						txtfldKeyCodeArray[player][rule][slot][key] = txtfldKey;

						updateKeyConfigTextField(player,rule,slot,key);
					}
				}
			}
		}
	}

	/**
	 * Save config to a CustomProperties
	 * @param p CustomProperties
	 */
	protected void saveConfigFromGUI(CustomProperties p) {
		// System tab
		JComboBox comboboxResolution = (JComboBox)getJComponent("sys_combobox_resolution");
		if(comboboxResolution.getSelectedItem() instanceof String) {
			String strResolution = (String)comboboxResolution.getSelectedItem();

			if(strResolution.indexOf('x') != -1) {
				p.setProperty("sys.resolution.string", strResolution);

				String strWidth = strResolution.substring(0, strResolution.indexOf('x'));
				String strHeight = strResolution.substring(strResolution.indexOf('x') + 1, strResolution.length());
				p.setProperty("sys.resolution.width", strWidth);
				p.setProperty("sys.resolution.height", strHeight);
			}
		}

		JComboBox comboboxSoundProvider = (JComboBox)getJComponent("sys_combobox_soundprovider");
		p.setProperty("sys.soundprovider", comboboxSoundProvider.getSelectedIndex());

		JTextField txtfldVolume = (JTextField)getJComponent("sys_txtfld_volume");
		try {
			p.setProperty("sys.soundvolume", Float.valueOf(txtfldVolume.getText()));
		} catch (NumberFormatException e) {}

		JSpinner spinnerFPS = (JSpinner)getJComponent("sys_spinner_fps");
		if(spinnerFPS.getValue() instanceof Integer) {
			p.setProperty("sys.fps", (Integer)spinnerFPS.getValue());
		}

		JCheckBox chkboxFullscreen = (JCheckBox)getJComponent("sys_chkbox_fullscreen");
		p.setProperty("sys.fullscreen", chkboxFullscreen.isSelected());
		JCheckBox chkboxEnableSound = (JCheckBox)getJComponent("sys_chkbox_enablesound");
		p.setProperty("sys.enablesound", chkboxEnableSound.isSelected());
		JCheckBox chkboxNativeLookFeel = (JCheckBox)getJComponent("sys_chkbox_nativelookfeel");
		p.setProperty("sys.nativelookfeel", chkboxNativeLookFeel.isSelected());
		JCheckBox chkboxEnableAlpha = (JCheckBox)getJComponent("sys_chkbox_enablealpha");
		p.setProperty("sys.enablealpha", chkboxEnableAlpha.isSelected());
		JCheckBox chkboxEnableColorFilter = (JCheckBox)getJComponent("sys_chkbox_enablecolorfilter");
		p.setProperty("sys.enablecolorfilter", chkboxEnableColorFilter.isSelected());
		JCheckBox chkboxEnableGradient = (JCheckBox)getJComponent("sys_chkbox_enablegradient");
		p.setProperty("sys.enablegradient", chkboxEnableGradient.isSelected());

		// Swing Tab
		JCheckBox chkboxSwingUseBufferStrategy = (JCheckBox)getJComponent("swing_chkbox_usebufferstrategy");
		p.setProperty("swing.useBufferStrategy", chkboxSwingUseBufferStrategy.isSelected());

		// Slick tab
		JCheckBox chkboxSlickAWTKey = (JCheckBox)getJComponent("slick_chkbox_awtkey");
		p.setProperty("slick.awtkey", chkboxSlickAWTKey.isSelected());
		JCheckBox chkboxSlickJInput = (JCheckBox)getJComponent("slick_chkbox_jinput");
		p.setProperty("slick.jinput.enable", chkboxSlickJInput.isSelected());
		JTextField txtfldSlickJInput = (JTextField)getJComponent("slick_txtfld_jinput");
		try {
			p.setProperty("slick.jinput.keyboardID", Integer.parseInt(txtfldSlickJInput.getText()));
		} catch (NumberFormatException e) {}

		// Key Config
		for(int player = 0; player < MAX_PLAYERS; player++) {
			for(int rule = 0; rule < MAX_RULE_KIND; rule++) {
				for(int slot = 0; slot < MAX_KEY_SLOTS; slot++) {
					for(int key = 0; key < MAX_KEY_KINDS; key++) {
						String strID = getKeyConfigID(player,rule,slot,key);
						p.setProperty(strID, keycodes[player][rule][slot][key]);
					}
				}
			}
		}
	}

	/**
	 * Get key config ID
	 * @param player Player
	 * @param rule Rule Kind
	 * @param slot Config Slot
	 * @param key Key Kind
	 * @return Key config ID
	 */
	protected String getKeyConfigID(int player, int rule, int slot, int key) {
		return "key_" + player + "_" + rule + "_" + slot + "_" + key;
	}

	/**
	 * Update specific key config textbox
	 * @param player Player
	 * @param rule Rule Kind
	 * @param slot Config Slot
	 * @param key Key Kind
	 */
	protected void updateKeyConfigTextField(int player, int rule, int slot, int key) {
		int keyCode = keycodes[player][rule][slot][key];
		if(keyCode != KeyEvent.VK_UNDEFINED) {
			txtfldKeyCodeArray[player][rule][slot][key].setText(KeyEvent.getKeyText(keyCode));
		} else {
			txtfldKeyCodeArray[player][rule][slot][key].setText("");
		}
	}

	/**
	 * Get the array indexes of specified JTextField in txtfldKeyCodeArray
	 * @param obj JTextField
	 * @return [0]=player, [1]=rule, [2]=slot, [3]=key
	 */
	protected int[] getKeyConfigTextFieldIndex(Object obj) {
		for(int player = 0; player < MAX_PLAYERS; player++) {
			for(int rule = 0; rule < MAX_RULE_KIND; rule++) {
				for(int slot = 0; slot < MAX_KEY_SLOTS; slot++) {
					for(int key = 0; key < MAX_KEY_KINDS; key++) {
						if(obj == txtfldKeyCodeArray[player][rule][slot][key]) {
							return (new int[]{player,rule,slot,key});
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Copy key config between rules
	 * @param player Player
	 * @param ruleFrom Rule Kind to copy from
	 * @param ruleTo Rule Kind to copy to
	 */
	protected void copyKeyConfig(int player, int ruleFrom, int ruleTo) {
		for(int slot = 0; slot < MAX_KEY_SLOTS; slot++) {
			for(int key = 0; key < MAX_KEY_KINDS; key++) {
				int keyCode = keycodes[player][ruleFrom][slot][key];
				keycodes[player][ruleTo][slot][key] = keyCode;
				updateKeyConfigTextField(player, ruleTo, slot, key);
			}
		}
	}

	/**
	 * Clear key config
	 * @param player Player
	 * @param rule Rule Kind
	 */
	protected void clearKeyConfig(int player, int rule) {
		for(int slot = 0; slot < MAX_KEY_SLOTS; slot++) {
			for(int key = 0; key < MAX_KEY_KINDS; key++) {
				keycodes[player][rule][slot][key] = KeyEvent.VK_UNDEFINED;
				updateKeyConfigTextField(player, rule, slot, key);
			}
		}
	}

	/**
	 * Constructor
	 */
	public ConfigTool() {
		NGlobalConfig.load();
		NGlobalConfig.applySwingLookFeel();

		cookSwing = new CookSwing(this);
		Container c = cookSwing.render("data/xml/configtool.xml");

		mainWindow = (Window)c;
		mainWindow.pack();
		mainWindow.setVisible(true);

		loadConfigToGUI(NGlobalConfig.getConfig());
	}

	/**
	 * Entry point of our program
	 * @param args Command-line parameters
	 */
	public static void main(String[] args) {
		new ConfigTool();
	}
}
