package mu.nu.nullpo.gui.slick;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import mu.nu.nullpo.util.CustomProperties;

import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Rule select (after mode selection)
 */
public class StateSelectRuleFromList extends DummyMenuScrollState {
	/** Log */
	static Logger log = Logger.getLogger(StateSelectRuleFromList.class);

	/** This state's ID */
	public static final int ID = 18;

	/** Number of rules in one page */
	public static final int PAGE_HEIGHT = 24;

	/** HashMap of rules (ModeName->RuleEntry) */
	protected HashMap<String, RuleEntry> mapRuleEntries;

	/** Current mode */
	protected String strCurrentMode;

	/**
	 * Constructor
	 */
	public StateSelectRuleFromList() {
		super();
		pageHeight = PAGE_HEIGHT;
	}

	/*
	 * Fetch this state's ID
	 */
	@Override
	public int getID() {
		return ID;
	}

	/*
	 * State initialization
	 */
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		loadRecommendedRuleList();
	}

	/**
	 * Load list file
	 */
	protected void loadRecommendedRuleList() {
		mapRuleEntries = new HashMap<String, RuleEntry>();

		try {
			BufferedReader in = new BufferedReader(new FileReader("config/list/recommended_rules.lst"));
			String strMode = "";

			String str;
			while((str = in.readLine()) != null) {
				str = str.trim();	// Trim the space

				if(str.startsWith("#")) {
					// Commment-line. Ignore it.
				} else if(str.startsWith(":")) {
					// Mode change
					strMode = str.substring(1);
				} else {
					// File Path
					File file = new File(str);
					if(file.exists() && file.isFile()) {
						try {
							FileInputStream ruleIn = new FileInputStream(file);
							CustomProperties propRule = new CustomProperties();
							propRule.load(ruleIn);
							ruleIn.close();

							String strRuleName = propRule.getProperty("0.ruleopt.strRuleName", "");
							if(strRuleName.length() > 0) {
								RuleEntry entry = mapRuleEntries.get(strMode);
								if(entry == null) {
									entry = new RuleEntry();
									mapRuleEntries.put(strMode, entry);
								}
								entry.listName.add(strRuleName);
								entry.listPath.add(str);
							}
						} catch (IOException e2) {
							log.error("File " + str + " doesn't exist", e2);
						}
					}
				}
			}

			in.close();
		} catch (IOException e) {
			log.error("Failed to load recommended rules list", e);
		}
	}

	/**
	 * Prepare rule list
	 */
	protected void prepareRuleList() {
		strCurrentMode = NullpoMinoSlick.propGlobal.getProperty("name.mode", "");
		if(strCurrentMode != null) {
			RuleEntry entry = mapRuleEntries.get(strCurrentMode);

			if(entry == null) {
				list = new String[1];
				maxCursor = list.length - 1;
				list[0] = "(CURRENT RULE)";
			} else {
				list = new String[1 + entry.listName.size()];
				maxCursor = list.length - 1;
				list[0] = "(CURRENT RULE)";
				for(int i = 0; i < entry.listName.size(); i++) {
					list[i + 1] = entry.listName.get(i);
				}
			}
		} else {
			list = new String[1];
			maxCursor = list.length - 1;
			list[0] = "(CURRENT RULE)";
		}

		int defaultCursor = 0;
		String strLastRule = NullpoMinoSlick.propGlobal.getProperty("lastrule." + strCurrentMode);
		if((strLastRule != null) && (strLastRule.length() > 0)) {
			for(int i = 0; i < list.length; i++) {
				if(list[i].equals(strLastRule)) {
					defaultCursor = i;
				}
			}
		}
		cursor = defaultCursor;
	}

	/*
	 * When the player enters this state
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		prepareRuleList();
	}

	/*
	 * Render screen
	 */
	@Override
	protected void onRenderSuccess(GameContainer container, StateBasedGame game, Graphics graphics) {
		NormalFont.printFontGrid(1, 1, strCurrentMode + " (" + (cursor + 1) + "/" + list.length + ")",
				NormalFont.COLOR_ORANGE);
	}

	/*
	 * Decide
	 */
	@Override
	protected boolean onDecide(GameContainer container, StateBasedGame game, int delta) {
		ResourceHolder.soundManager.play("decide");
		NullpoMinoSlick.propGlobal.setProperty("lastrule." + strCurrentMode, list[cursor]);
		NullpoMinoSlick.saveConfig();

		String strRulePath = null;
		if(cursor >= 1) {
			RuleEntry entry = mapRuleEntries.get(strCurrentMode);
			strRulePath = entry.listPath.get(cursor - 1);
		}

		NullpoMinoSlick.stateInGame.startNewGame(strRulePath);
		game.enterState(StateInGame.ID);
		return false;
	}

	/*
	 * Cancel
	 */
	@Override
	protected boolean onCancel(GameContainer container, StateBasedGame game, int delta) {
		game.enterState(StateSelectMode.ID);
		return false;
	}

	/**
	 * RuleEntry
	 */
	protected class RuleEntry {
		public LinkedList<String> listPath = new LinkedList<String>();
		public LinkedList<String> listName = new LinkedList<String>();
	}
}
