package cx.it.nullpo.nm8.gui.common.font.angelcode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cx.it.nullpo.nm8.util.NUtil;

/**
 * Each page info of AngelCodeFont fnt file.
 */
public class AngelCodePageInfo implements Serializable {
	private static final long serialVersionUID = 6301149935421406336L;

	private static final String ATTR_ID = "id=";
	private static final String ATTR_FILE = "file=\"";
	private static final String ATTR_COUNT = "count=";
	private static final String ATTR_X = "x=";
	private static final String ATTR_Y = "y=";
	private static final String ATTR_WIDTH = "width=";
	private static final String ATTR_HEIGHT = "height=";
	private static final String ATTR_XOFFSET = "xoffset=";
	private static final String ATTR_YOFFSET = "yoffset=";
	private static final String ATTR_XADVANCE = "xadvance=";
	private static final String ATTR_PAGE = "page=";
	private static final String ATTR_CHNL = "chnl=";

	private Log log = LogFactory.getLog(AngelCodePageInfo.class);

	private int id;

	private String fileName;

	private int charsCount;

	private Map<Integer, AngelCodeCharInfo> charInfoMap = new HashMap<Integer, AngelCodeCharInfo>();

	public AngelCodePageInfo(List<String> list) {
		parseStringList(list);
	}

	private void parseStringList(List<String> list) {
		log.debug("Starting parse of a page secion. Lines:" + list.size());

		int numInvalidCharEntries = 0;

		for(String str: list) {
			String[] sArray = str.split(" ");

			if(sArray.length > 0 && sArray[0].length() > 0) {
				if(sArray[0].equals("page")) {
					// Page info
					for(int i = 1; i < sArray.length; i++) {
						if(sArray[i].startsWith(ATTR_ID)) {
							// id=NUM
							String sID = sArray[i].substring(ATTR_ID.length());
							log.trace("page id=" + sID);

							try {
								id = Integer.parseInt(sID);
							} catch (NumberFormatException e) {
								log.error("'page' line's 'id' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_FILE)) {
							// file="foo.png"
							fileName = sArray[i].substring(ATTR_FILE.length(), sArray[i].length() - 1);
							log.trace("page file=\"" + fileName + "\"");
						}
					}
				} else if(sArray[0].equals("chars")) {
					// Number of chars
					for(int i = 1; i < sArray.length; i++) {
						if(sArray[i].startsWith(ATTR_COUNT)) {
							// count=NUM
							String sCount = sArray[i].substring(ATTR_COUNT.length());
							log.trace("chars count=" + sCount);

							try {
								charsCount = Integer.parseInt(sCount);
							} catch (NumberFormatException e) {
								log.error("'chars' line's 'count' attribute has an invalid number", e);
							}
						}
					}
				} else if(sArray[0].equals("char")) {
					// Each character entry
					boolean valid = true;
					int id = Integer.MIN_VALUE;
					int x = Integer.MIN_VALUE;
					int y = Integer.MIN_VALUE;
					int width = Integer.MIN_VALUE;
					int height = Integer.MIN_VALUE;
					int xoffset = Integer.MIN_VALUE;
					int yoffset = Integer.MIN_VALUE;
					int xadvance = Integer.MIN_VALUE;
					int page = Integer.MIN_VALUE;
					int chnl = Integer.MIN_VALUE;

					for(int i = 1; i < sArray.length; i++) {
						if(sArray[i].startsWith(ATTR_ID)) {
							// id=NUM
							try {
								id = Integer.parseInt(sArray[i].substring(ATTR_ID.length()));
							} catch (NumberFormatException e) {
								valid = false;
								log.error("'char' line's 'id' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_X)) {
							// x=NUM
							try {
								x = Integer.parseInt(sArray[i].substring(ATTR_X.length()));
							} catch (NumberFormatException e) {
								valid = false;
								log.error("'char' line's 'x' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_Y)) {
							// y=NUM
							try {
								y = Integer.parseInt(sArray[i].substring(ATTR_Y.length()));
							} catch (NumberFormatException e) {
								valid = false;
								log.error("'char' line's 'y' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_WIDTH)) {
							// width=NUM
							try {
								width = Integer.parseInt(sArray[i].substring(ATTR_WIDTH.length()));
							} catch (NumberFormatException e) {
								valid = false;
								log.error("'char' line's 'width' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_HEIGHT)) {
							// height=NUM
							try {
								height = Integer.parseInt(sArray[i].substring(ATTR_HEIGHT.length()));
							} catch (NumberFormatException e) {
								valid = false;
								log.error("'char' line's 'height' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_XOFFSET)) {
							// xoffset=NUM
							try {
								xoffset = Integer.parseInt(sArray[i].substring(ATTR_XOFFSET.length()));
							} catch (NumberFormatException e) {
								valid = false;
								log.error("'char' line's 'xoffset' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_YOFFSET)) {
							// yoffset=NUM
							try {
								yoffset = Integer.parseInt(sArray[i].substring(ATTR_YOFFSET.length()));
							} catch (NumberFormatException e) {
								valid = false;
								log.error("'char' line's 'yoffset' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_XADVANCE)) {
							// xadvance=NUM
							try {
								xadvance = Integer.parseInt(sArray[i].substring(ATTR_XADVANCE.length()));
							} catch (NumberFormatException e) {
								valid = false;
								log.error("'char' line's 'xadvance' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_PAGE)) {
							// page=NUM
							try {
								page = Integer.parseInt(sArray[i].substring(ATTR_PAGE.length()));

								if(page != this.id) {
									log.warn("'char' line's 'page' attribute doesn't match with current page ID '" + this.id + "'");
									page = this.id;
								}
							} catch (NumberFormatException e) {
								valid = false;
								log.error("'char' line's 'page' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_CHNL)) {
							// chnl=NUM
							try {
								chnl = Integer.parseInt(sArray[i].substring(ATTR_CHNL.length()));
							} catch (NumberFormatException e) {
								valid = false;
								log.error("'char' line's 'chnl' attribute has an invalid number", e);
							}
						}
					}

					// Check for missing attributes
					if(valid) {
						if(id == Integer.MIN_VALUE) {
							valid = false;
							log.error("'char' line's 'id' attribute is missing");
						}
						if(x == Integer.MIN_VALUE) {
							valid = false;
							log.error("'char' line's 'x' attribute is missing");
						}
						if(y == Integer.MIN_VALUE) {
							valid = false;
							log.error("'char' line's 'y' attribute is missing");
						}
						if(width == Integer.MIN_VALUE) {
							valid = false;
							log.error("'char' line's 'width' attribute is missing");
						}
						if(height == Integer.MIN_VALUE) {
							valid = false;
							log.error("'char' line's 'height' attribute is missing");
						}
						if(xoffset == Integer.MIN_VALUE) {
							valid = false;
							log.error("'char' line's 'xoffset' attribute is missing");
						}
						if(yoffset == Integer.MIN_VALUE) {
							valid = false;
							log.error("'char' line's 'yoffset' attribute is missing");
						}
						if(xadvance == Integer.MIN_VALUE) {
							valid = false;
							log.error("'char' line's 'xadvance' attribute is missing");
						}
						if(page == Integer.MIN_VALUE) {
							valid = false;
							log.error("'char' line's 'page' attribute is missing");
						}
						if(chnl == Integer.MIN_VALUE) {
							valid = false;
							log.error("'char' line's 'chnl' attribute is missing");
						}
					}

					if(charInfoMap.containsKey(id)) {
						log.error("Character ID '" + id + "' already exists");
						valid = false;
					}

					// If everything is OK, add to charInfoMap
					if(valid) {
						AngelCodeCharInfo info = new AngelCodeCharInfo(id, x, y, width, height, xoffset, yoffset, xadvance, page, chnl);
						log.trace(info.toString());
						charInfoMap.put(info.getId(), info);
					} else {
						numInvalidCharEntries++;
					}
				} else {
					log.warn("Ignored an unknown line '" + sArray[0] + "'");
				}
			}
		}

		if(charsCount != charInfoMap.size()) {
			if(numInvalidCharEntries > 0) {
				log.warn(charInfoMap.size() + " out of " + charsCount + " characters loaded. Ignored " + numInvalidCharEntries + " due to error(s)." +
						" Page ID: #" + id);
			} else {
				log.info(charInfoMap.size() + " characters loaded, but 'chars' line's 'count' says there are " + charsCount + " characters." +
						" Page ID: #" + id);
			}
			charsCount = charInfoMap.size();
		} else if(charsCount == 0) {
			log.warn("This page #" + id + " doesn't seem to contain any characters.");
		} else {
			log.debug("Successfully loaded " + charsCount + " characters of page ID #" + id + ".");
		}
	}

	public int getId() {
		return id;
	}

	public String getFileName() {
		return fileName;
	}

	public int getCharsCount() {
		return charsCount;
	}

	public Map<Integer, AngelCodeCharInfo> getCharInfoMap() {
		return charInfoMap;
	}

	@Override
	public String toString() {
		return "AngelCodePageInfo [id=" + id + ", fileName=" + fileName
				+ ", charsCount=" + charsCount + ", charInfoMap=" + charInfoMap
				+ "]";
	}

	// for debug
	public static void main(String[] args) throws Exception {
		String filename = (args.length > 0) ? args[0] : "console.fnt";
		List<String> list = NUtil.getStringListFromURLE(NUtil.getURL(filename));
		AngelCodePageInfo pageInfo = new AngelCodePageInfo(list);
		LogFactory.getLog(AngelCodePageInfo.class).info(pageInfo.toString());
	}
}
