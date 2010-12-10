package mu.nu.nullpo.gui.slick;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Mode folder select
 */
public class StateSelectModeFolder extends DummyMenuScrollState {
	/** Log */
	static Logger log = Logger.getLogger(StateSelectModeFolder.class);

	/** This state's ID */
	public static final int ID = 19;

	/** Number of folders in one page */
	public static final int PAGE_HEIGHT = 24;

	/** Folder names list */
	public static LinkedList<String> listFolder;

	/** HashMap of mode folder (FolderName->ModeNames) */
	public static HashMap<String, LinkedList<String>> mapFolder;

	/** Current folder name */
	public static String strCurrentFolder;

	/**
	 * Constructor
	 */
	public StateSelectModeFolder() {
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
		loadFolderListFile();
		prepareFolderList();
	}

	/**
	 * Load folder list file
	 */
	public static void loadFolderListFile() {
		if(listFolder == null) {
			listFolder = new LinkedList<String>();
		} else {
			listFolder.clear();
		}
		if(mapFolder == null) {
			mapFolder = new HashMap<String, LinkedList<String>>();
		} else {
			mapFolder.clear();
		}
		strCurrentFolder = NullpoMinoSlick.propGlobal.getProperty("name.folder", "");

		try {
			BufferedReader in = new BufferedReader(new FileReader("config/list/modefolder.lst"));
			String strFolder = "";

			String str;
			while((str = in.readLine()) != null) {
				str = str.trim();	// Trim the space

				if(str.startsWith("#")) {
					// Commment-line. Ignore it.
				} else if(str.startsWith(":")) {
					// New folder
					strFolder = str.substring(1);
					if(!listFolder.contains(strFolder)) {
						listFolder.add(strFolder);
						LinkedList<String> listMode = new LinkedList<String>();
						mapFolder.put(strFolder, listMode);
					}
				} else if(str.length() > 0) {
					// Mode name
					LinkedList<String> listMode = mapFolder.get(strFolder);
					if((listMode != null) && !listMode.contains(str)) {
						log.debug(strFolder + "." + str);
						listMode.add(str);
						mapFolder.put(strFolder, listMode);
					}
				}
			}

			in.close();
		} catch (IOException e) {
			log.error("Failed to load mode folder list file", e);
		}
	}

	/**
	 * Prepare folder list
	 */
	protected void prepareFolderList() {
		list = new String[listFolder.size() + 1];
		maxCursor = list.length - 1;
		for(int i = 0; i < listFolder.size(); i++) {
			list[i] = listFolder.get(i);

			if(strCurrentFolder.equals(list[i])) {
				cursor = i;
			}
		}
		list[list.length - 1] = "[ALL MODES]";
	}

	/**
	 * Get folder description
	 * @param str Folder name
	 * @return Description
	 */
	protected String getFolderDesc(String str) {
		String str2 = str.replace(' ', '_');
		str2 = str2.replace('(', 'l');
		str2 = str2.replace(')', 'r');
		String result = NullpoMinoSlick.propModeDesc.getProperty("Folder_" + str2);
		if(result == null) {
			result = NullpoMinoSlick.propDefaultModeDesc.getProperty("Folder_" + str2, "Folder_" + str2);
		}
		return result;
	}

	/*
	 * Render screen
	 */
	@Override
	protected void onRenderSuccess(GameContainer container, StateBasedGame game, Graphics graphics) {
		NormalFont.printFontGrid(1, 1, "SELECT MODE FOLDER (" + (cursor + 1) + "/" + list.length + ")", NormalFont.COLOR_ORANGE);
		NormalFont.printTTFFont(16, 440, getFolderDesc(list[cursor]));
	}

	/*
	 * Decide
	 */
	@Override
	protected boolean onDecide(GameContainer container, StateBasedGame game, int delta) {
		ResourceHolder.soundManager.play("decide");
		if(cursor < listFolder.size()) {
			strCurrentFolder = list[cursor];
		} else {
			strCurrentFolder = "";
		}
		NullpoMinoSlick.propGlobal.setProperty("name.folder", strCurrentFolder);
		NullpoMinoSlick.saveConfig();
		game.enterState(StateSelectMode.ID);
		return false;
	}

	/*
	 * Cancel
	 */
	@Override
	protected boolean onCancel(GameContainer container, StateBasedGame game, int delta) {
		game.enterState(StateTitle.ID);
		return false;
	}
}
