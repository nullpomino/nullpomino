package cx.it.nullpo.nm8.gui.game;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import cx.it.nullpo.nm8.gui.framework.NFSound;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.util.NUtil;

/**
 * ResourceHolder: Contains Graphics, Sounds, etc
 */
public class ResourceHolder {
	/** Sound effects map (name->sound) */
	public static Map<String, NFSound> seMap = Collections.synchronizedMap(new HashMap<String, NFSound>());
	/** Sound effects map (file->sound) */
	public static Map<String, NFSound> seFilename2SoundMap = Collections.synchronizedMap(new HashMap<String, NFSound>());
	/** List of sound effects loaded */
	public static List<NFSound> seList = Collections.synchronizedList(new LinkedList<NFSound>());

	/**
	 * Load sound effects
	 * @param sys NFSystem
	 * @param skinDirName Skin Directory Name
	 */
	public static void loadSoundEffects(NFSystem sys, String skinDirName) {
		// Load XML
		String strXMLFilePath = "data/res/se/" + skinDirName + "/seindex.xml";
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			doc = builder.build(NUtil.getURL(strXMLFilePath));
		} catch (JDOMException e) {
			System.err.println("XML parse error in sound effects index file (File:" + strXMLFilePath + ")");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.err.println("Sound effects index file not found (File:" + strXMLFilePath + ")");
			e.printStackTrace();
			return;
		}

		// Load sound effects
		boolean noMoreSoundEffects = false;
		List list = doc.getRootElement().getChildren();
		Iterator it = list.iterator();
		while(it.hasNext()) {
			Element element = (Element)it.next();

			if(element.getName().equals("se")) {
				String strName = element.getAttributeValue("name");
				String strFile = element.getAttributeValue("file");

				if((strName == null) && (strFile == null)) {
					// Ignore it if both name and file is null
				} else if(strName == null) {
					if(strFile.endsWith(".wav")) {
						strName = strFile.substring(0, strFile.length() - 4);
					} else {
						strName = strFile;
					}
				} else if(strFile == null) {
					strFile = strName + ".wav";
				}
				String strFilePath = "data/res/se/" + skinDirName + "/" + strFile;

				if(seFilename2SoundMap.containsKey(strFile)) {
					// The same file is already loaded, so let's reuse it
					System.out.println("Reuse " + strFile + " for " + strName);
					NFSound sound = seFilename2SoundMap.get(strFile);
					seMap.put(strName, sound);
				} else if(!noMoreSoundEffects) {
					// Load a sound effect
					try {
						NFSound sound = sys.loadSound(strFilePath);
						seMap.put(strName, sound);
						seFilename2SoundMap.put(strFile, sound);
						seList.add(sound);
					} catch (IllegalStateException e) {
						System.err.println("No more sound effects can be loaded. File:" + strFilePath + " (Name:" + strName + ")");
						e.printStackTrace();
						noMoreSoundEffects = true;
					} catch (Exception e) {
						System.err.println("Can't load sound effect file:" + strFilePath + " (Name:" + strName + ")");
						e.printStackTrace();
					}
				} else {
					System.err.println("No more sound effects can be loaded. File:" + strFilePath + " (Name:" + strName + ")");
				}
			}
		}
	}

	/**
	 * Play a sound effect
	 * @param name Sound effect name
	 */
	public static void playSE(String name) {
		NFSound sound = seMap.get(name);
		if(sound != null) sound.play();
	}

	/**
	 * Unload all sound effects
	 */
	public static void unloadSoundEffects() {
		synchronized (seList) {
			Iterator<NFSound> it = seList.iterator();

			while(it.hasNext()) {
				it.next().dispose();
			}
		}
		seList.clear();
		seMap.clear();
		seFilename2SoundMap.clear();
	}
}
