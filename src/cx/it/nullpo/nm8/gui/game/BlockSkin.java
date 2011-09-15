package cx.it.nullpo.nm8.gui.game;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import cx.it.nullpo.nm8.gui.framework.NFImage;
import cx.it.nullpo.nm8.gui.framework.NFSystem;

/**
 * This class represents each entry of block skin
 */
public class BlockSkin {
	/** Map of "normal" block skin images (size->image) */
	public Map<Integer, NFImage> mapImageNormal = Collections.synchronizedMap(new HashMap<Integer, NFImage>());

	/** Map of "locked" block skin images (size->image) */
	public Map<Integer, NFImage> mapImageLocked = Collections.synchronizedMap(new HashMap<Integer, NFImage>());

	/** true if this block skin contains all special blocks */
	public boolean hasSpecials;

	/** true if this block skin is sticky */
	public boolean isSticky;

	/** true if this block skin has special lock animation */
	public boolean hasLockAnim;

	/**
	 * Create a BlockSkin by using a XML document
	 * @param sys NFSystem
	 * @param skinDirName Block skin directory name
	 * @param doc XML document
	 * @return BlockSkin
	 */
	public static BlockSkin loadByXML(NFSystem sys, String skinDirName, Document doc) {
		BlockSkin b = new BlockSkin();

		// Common options
		b.hasSpecials = Boolean.valueOf(doc.getRootElement().getAttributeValue("hasSpecials", "false"));
		b.isSticky = Boolean.valueOf(doc.getRootElement().getAttributeValue("isSticky", "false"));
		b.hasLockAnim = Boolean.valueOf(doc.getRootElement().getAttributeValue("hasLockAnim", "false"));

		// Image files
		List listChildren = doc.getRootElement().getChildren();
		Iterator itChildren = listChildren.iterator();
		while(itChildren.hasNext()) {
			Element element = (Element)itChildren.next();

			if(element.getName().equals("state")) {
				String strStateType = element.getAttributeValue("type", "normal");
				List listImages = element.getChildren();
				Iterator itImages = listImages.iterator();

				while(itImages.hasNext()) {
					Element eImage = (Element)itImages.next();

					int size = Integer.valueOf(eImage.getAttributeValue("size"));
					String strFilename = eImage.getAttributeValue("filename");
					//System.out.println("size:" + size + ", filename:" + strFilename);

					try {
						NFImage nfImage = sys.loadImage("data/res/graphics/block/" + skinDirName + "/" + strFilename);

						if(strStateType.equals("locked")) {
							b.mapImageLocked.put(size, nfImage);
						} else {
							b.mapImageNormal.put(size, nfImage);
						}
					} catch (Exception e) {
						System.err.println("Failed to load block image " + strFilename);
						e.printStackTrace();
					}
				}
			}
		}

		System.out.println("b.mapImageNormal.size():" + b.mapImageNormal.size());
		System.out.println("b.mapImageLocked.size():" + b.mapImageLocked.size());

		return b;
	}
}
