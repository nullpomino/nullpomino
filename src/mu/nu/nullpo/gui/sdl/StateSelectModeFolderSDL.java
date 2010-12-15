package mu.nu.nullpo.gui.sdl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import sdljava.SDLException;
import sdljava.video.SDLSurface;

/**
 * Mode folder select (SDL)
 */
public class StateSelectModeFolderSDL extends DummyMenuScrollStateSDL {
	/** Log */
	static Logger log = Logger.getLogger(StateSelectModeFolderSDL.class);

	/** Number of folders in one page */
	public static final int PAGE_HEIGHT = 24;

	/** Top-level mode list */
	public static LinkedList<String> listTopLevelModes;

	/** Folder names list */
	public static LinkedList<String> listFolder;

	/** HashMap of mode folder (FolderName->ModeNames) */
	public static HashMap<String, LinkedList<String>> mapFolder;

	/** Current folder name */
	public static String strCurrentFolder;

	/**
	 * Constructor
	 */
	public StateSelectModeFolderSDL() {
		super();
		pageHeight = PAGE_HEIGHT;

		loadFolderListFile();
		prepareFolderList();
	}

	/**
	 * Load folder list file
	 */
	public static void loadFolderListFile() {
		if(listTopLevelModes == null) listTopLevelModes = new LinkedList<String>();
		else listTopLevelModes.clear();

		if(listFolder == null) listFolder = new LinkedList<String>();
		else listFolder.clear();

		if(mapFolder == null) mapFolder = new HashMap<String, LinkedList<String>>();
		else mapFolder.clear();

		strCurrentFolder = NullpoMinoSDL.propGlobal.getProperty("name.folder", "");

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
					if(strFolder.length() == 0) {
						log.debug("(top-level)." + str);
						listTopLevelModes.add(str);
					} else {
						LinkedList<String> listMode = mapFolder.get(strFolder);
						if((listMode != null) && !listMode.contains(str)) {
							log.debug(strFolder + "." + str);
							listMode.add(str);
							mapFolder.put(strFolder, listMode);
						}
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
		String result = NullpoMinoSDL.propModeDesc.getProperty("Folder_" + str2);
		if(result == null) {
			result = NullpoMinoSDL.propDefaultModeDesc.getProperty("Folder_" + str2, "Folder_" + str2);
		}
		return result;
	}

	/*
	 * Render screen
	 */
	@Override
	protected void onRenderSuccess(SDLSurface screen) throws SDLException {
		NormalFontSDL.printFontGrid(1, 1, "SELECT MODE FOLDER (" + (cursor + 1) + "/" + list.length + ")", NormalFontSDL.COLOR_ORANGE);
		NormalFontSDL.printTTFFont(16, 440, getFolderDesc(list[cursor]));
	}

	/*
	 * Decide
	 */
	@Override
	protected boolean onDecide() throws SDLException {
		ResourceHolderSDL.soundManager.play("decide");
		if(cursor < listFolder.size()) {
			strCurrentFolder = list[cursor];
		} else {
			strCurrentFolder = "";
		}
		NullpoMinoSDL.propGlobal.setProperty("name.folder", strCurrentFolder);
		NullpoMinoSDL.saveConfig();
		StateSelectModeSDL.isTopLevel = false;
		NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_SELECTMODE);
		return false;
	}

	/*
	 * Cancel
	 */
	@Override
	protected boolean onCancel() throws SDLException {
		StateSelectModeSDL.isTopLevel = true;
		NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_SELECTMODE);
		return false;
	}
}
