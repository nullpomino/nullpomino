package cx.it.nullpo.nm8.gui.common.font.angelcode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cx.it.nullpo.nm8.util.NUtil;

/**
 * Main info of AngelCode fnt file.
 */
public class AngelCodeMainInfo implements Serializable {
	private static final long serialVersionUID = 4547634833907066379L;

	private static final String ATTR_ID = "id=";

	private static final String ATTR_FACE = "face=\"";	// String
	private static final String ATTR_SIZE = "size=";
	private static final String ATTR_BOLD = "bold=";
	private static final String ATTR_ITALIC = "italic=";
	private static final String ATTR_CHARSET = "charset=\"";	// String
	private static final String ATTR_UNICODE = "unicode=";
	private static final String ATTR_STRETCHH = "stretchH=";
	private static final String ATTR_SMOTTH = "smooth=";
	private static final String ATTR_AA = "aa=";
	private static final String ATTR_PADDING = "padding=";
	private static final String ATTR_SPACING = "spacing=";
	private static final String ATTR_OUTLINE = "outline=";	// optional

	private static final String ATTR_LINEHEIGHT = "lineHeight=";
	private static final String ATTR_BASE = "base=";
	private static final String ATTR_SCALEW = "scaleW=";
	private static final String ATTR_SCALEH = "scaleH=";
	private static final String ATTR_PAGES = "pages=";
	private static final String ATTR_PACKED = "packed=";
	private static final String ATTR_ALPHACHNL = "alphaChnl=";	// optional
	private static final String ATTR_REDCHNL = "redChnl=";	// optional
	private static final String ATTR_GREENCHNL = "greenChnl=";	// optional
	private static final String ATTR_BLUECHNL = "blueChnl=";	// optional

	private Log log = LogFactory.getLog(AngelCodeMainInfo.class);

	private String face;
	private int size;
	private boolean bold;
	private boolean italic;
	private String charset;
	private boolean unicode;
	private int stretchH;
	private boolean smooth;
	private boolean aa;
	private int[] padding = new int[4];
	private int[] spacing = new int[2];
	private int outline = 0;	// optional

	private int lineHeight;
	private int base;
	private int scaleW;
	private int scaleH;
	private int pages;
	private int packed;
	private int alphaChnl = 0;	// optional
	private int redChnl = 0;	// optional
	private int greenChnl = 0;	// optional
	private int blueChnl = 0;	// optional

	private List<AngelCodePageInfo> pageInfoList = new ArrayList<AngelCodePageInfo>();

	public AngelCodeMainInfo(List<String> list) {
		parseStringList(list);
	}

	private void parseStringList(List<String> list) {
		log.debug("Starting parse of a fnt file. Lines:" + list.size());

		List<String> pageList = null;
		int nowPageID = -1;

		for(String str: list) {
			String[] sArray = str.split(" ");

			if(sArray.length > 0 && sArray[0].length() > 0) {
				if(sArray[0].equals("info")) {
					// Main info
					for(int i = 1; i < sArray.length; i++) {
						if(sArray[i].startsWith(ATTR_FACE)) {
							// face="Something"
							face = sArray[i].substring(ATTR_FACE.length(), sArray[i].length() - 1);
							log.trace("info face=" + face);
						} else if(sArray[i].startsWith(ATTR_SIZE)) {
							// size=NUM
							try {
								size = Integer.parseInt(sArray[i].substring(ATTR_SIZE.length()));
							} catch (NumberFormatException e) {
								log.error("'info' line's 'size' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_BOLD)) {
							// bold=BOOL
							bold = !sArray[i].substring(ATTR_BOLD.length()).equals("0");
						} else if(sArray[i].startsWith(ATTR_ITALIC)) {
							// italic=BOOL
							italic = !sArray[i].substring(ATTR_ITALIC.length()).equals("0");
						} else if(sArray[i].startsWith(ATTR_CHARSET)) {
							// charset=""
							charset = sArray[i].substring(ATTR_CHARSET.length(), sArray[i].length() - 1);
						} else if(sArray[i].startsWith(ATTR_UNICODE)) {
							// unicode=BOOL
							unicode = !sArray[i].substring(ATTR_UNICODE.length()).equals("0");
						} else if(sArray[i].startsWith(ATTR_STRETCHH)) {
							// stretchH=NUM
							try {
								stretchH = Integer.parseInt(sArray[i].substring(ATTR_STRETCHH.length()));
							} catch (NumberFormatException e) {
								log.error("'info' line's 'stretchH' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_SMOTTH)) {
							// smooth=BOOL
							smooth = !sArray[i].substring(ATTR_SMOTTH.length()).equals("0");
						} else if(sArray[i].startsWith(ATTR_AA)) {
							// aa=BOOL
							aa = !sArray[i].substring(ATTR_AA.length()).equals("0");
						} else if(sArray[i].startsWith(ATTR_PADDING)) {
							// padding=1,2,3,4
							String tempString = sArray[i].substring(ATTR_PADDING.length());
							String[] tempArray = tempString.split(",");
							int[] paddingArray = new int[tempArray.length];

							for(int j = 0; j < tempArray.length; j++) {
								try {
									paddingArray[j] = Integer.parseInt(tempArray[j]);
								} catch (NumberFormatException e) {
									log.error("'info' line's #" + (j+1) + " value of 'padding' attribute has an invalid number", e);
								}
							}

							if(paddingArray.length != padding.length) {
								log.warn("'info' line's 'padding' attribute has " + paddingArray.length +
										 " values, but we expected " + padding.length + " values");
							}

							for(int j = 0; j < Math.min(paddingArray.length, padding.length); j++) {
								padding[j] = paddingArray[j];
							}
						} else if(sArray[i].startsWith(ATTR_SPACING)) {
							// spacing=1,2
							String tempString = sArray[i].substring(ATTR_SPACING.length());
							String[] tempArray = tempString.split(",");
							int[] spacingArray = new int[tempArray.length];

							for(int j = 0; j < tempArray.length; j++) {
								try {
									spacingArray[j] = Integer.parseInt(tempArray[j]);
								} catch (NumberFormatException e) {
									log.error("'info' line's #" + (j+1) + " value of 'spacing' attribute has an invalid number", e);
								}
							}

							if(spacingArray.length != spacing.length) {
								log.warn("'info' line's 'spacing' attribute has " + spacingArray.length +
										 " values, but we expected " + spacing.length + " values");
							}

							for(int j = 0; j < Math.min(spacingArray.length, spacing.length); j++) {
								spacing[j] = spacingArray[j];
							}
						} else if(sArray[i].startsWith(ATTR_OUTLINE)) {
							// outline=NUM
							try {
								outline = Integer.parseInt(sArray[i].substring(ATTR_OUTLINE.length()));
							} catch (NumberFormatException e) {
								log.error("'info' line's 'outline' attribute has an invalid number", e);
							}
						}
					}
				} else if(sArray[0].equals("common")) {
					// Common info
					for(int i = 1; i < sArray.length; i++) {
						if(sArray[i].startsWith(ATTR_LINEHEIGHT)) {
							// lineHeight=NUM
							try {
								lineHeight = Integer.parseInt(sArray[i].substring(ATTR_LINEHEIGHT.length()));
							} catch (NumberFormatException e) {
								log.error("'common' line's 'lineHeight' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_BASE)) {
							// base=NUM
							try {
								base = Integer.parseInt(sArray[i].substring(ATTR_BASE.length()));
							} catch (NumberFormatException e) {
								log.error("'common' line's 'base' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_SCALEW)) {
							// scaleW=NUM
							try {
								scaleW = Integer.parseInt(sArray[i].substring(ATTR_SCALEW.length()));
							} catch (NumberFormatException e) {
								log.error("'common' line's 'scaleW' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_SCALEH)) {
							// scaleH=NUM
							try {
								scaleH = Integer.parseInt(sArray[i].substring(ATTR_SCALEH.length()));
							} catch (NumberFormatException e) {
								log.error("'common' line's 'scaleH' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_PAGES)) {
							// pages=NUM
							try {
								pages = Integer.parseInt(sArray[i].substring(ATTR_PAGES.length()));
							} catch (NumberFormatException e) {
								log.error("'common' line's 'pages' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_PACKED)) {
							// packed=NUM
							try {
								packed = Integer.parseInt(sArray[i].substring(ATTR_PACKED.length()));
							} catch (NumberFormatException e) {
								log.error("'common' line's 'packed' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_ALPHACHNL)) {
							// alphaChnl=NUM
							try {
								alphaChnl = Integer.parseInt(sArray[i].substring(ATTR_ALPHACHNL.length()));
							} catch (NumberFormatException e) {
								log.error("'common' line's 'packed' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_REDCHNL)) {
							// redChnl=NUM
							try {
								redChnl = Integer.parseInt(sArray[i].substring(ATTR_REDCHNL.length()));
							} catch (NumberFormatException e) {
								log.error("'common' line's 'redChnl' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_GREENCHNL)) {
							// greenChnl=NUM
							try {
								greenChnl = Integer.parseInt(sArray[i].substring(ATTR_GREENCHNL.length()));
							} catch (NumberFormatException e) {
								log.error("'common' line's 'greenChnl' attribute has an invalid number", e);
							}
						} else if(sArray[i].startsWith(ATTR_BLUECHNL)) {
							// blueChnl=NUM
							try {
								blueChnl = Integer.parseInt(sArray[i].substring(ATTR_BLUECHNL.length()));
							} catch (NumberFormatException e) {
								log.error("'common' line's 'blueChnl' attribute has an invalid number", e);
							}
						}
					}
				} else if(sArray[0].equals("page")) {
					// Page info start
					if(nowPageID != -1 && pageList != null) {
						// Stop the current page info
						log.trace("End page #" + nowPageID + ". New page. Processing...");
						AngelCodePageInfo pageInfo = new AngelCodePageInfo(pageList);
						pageInfoList.add(pageInfo);
					}

					// Start the page info
					pageList = new ArrayList<String>();
					pageList.add(str);

					if(sArray.length > 1 && sArray[1].startsWith(ATTR_ID)) {
						// id=NUM
						String sID = sArray[1].substring(ATTR_ID.length());

						try {
							nowPageID = Integer.parseInt(sID);
						} catch (NumberFormatException e) {
							nowPageID++;
							log.error("'page' line's 'id' attribute has an invalid number. Using ID #" + nowPageID + " for now.", e);
						}
					} else {
						nowPageID++;
						log.error("'page' line's 'id' attribute is missing. Using ID #" + nowPageID + " for now.");
					}
					log.trace("Starting page #" + nowPageID + "...");
				} else if(sArray[0].equals("chars")) {
					// Page info chars
					if(nowPageID != -1 && pageList != null) {
						pageList.add(str);
					}
				} else if(sArray[0].equals("char")) {
					// Page info char
					if(nowPageID != -1 && pageList != null) {
						pageList.add(str);
					}
				}
			}
		}

		// Stop the current page info
		if(nowPageID != -1 && pageList != null) {
			log.trace("End page #" + nowPageID + ". End of file. Processing...");
			AngelCodePageInfo pageInfo = new AngelCodePageInfo(pageList);
			pageInfoList.add(pageInfo);
		}

		if(pages != pageInfoList.size()) {
			log.warn(pageInfoList.size() + " page(s) loaded, but 'common' line's 'pages' attribute says there are " + pages + " page(s).");
			pages = pageInfoList.size();
		} else if(pages == 0) {
			log.warn("This fnt file doesn't seem to contain any pages.");
		} else {
			log.debug("Successfully loaded " + pages + " page(s).");
		}
	}

	public String getFace() {
		return face;
	}
	public int getSize() {
		return size;
	}
	public boolean isBold() {
		return bold;
	}
	public boolean isItalic() {
		return italic;
	}
	public String getCharset() {
		return charset;
	}
	public boolean isUnicode() {
		return unicode;
	}
	public int getStretchH() {
		return stretchH;
	}
	public boolean isSmooth() {
		return smooth;
	}
	public boolean isAa() {
		return aa;
	}
	public int[] getPadding() {
		return padding;
	}
	public int[] getSpacing() {
		return spacing;
	}
	public int getOutline() {
		return outline;
	}
	public int getLineHeight() {
		return lineHeight;
	}
	public int getBase() {
		return base;
	}
	public int getScaleW() {
		return scaleW;
	}
	public int getScaleH() {
		return scaleH;
	}
	public int getPages() {
		return pages;
	}
	public int getPacked() {
		return packed;
	}
	public int getAlphaChnl() {
		return alphaChnl;
	}
	public int getRedChnl() {
		return redChnl;
	}
	public int getGreenChnl() {
		return greenChnl;
	}
	public int getBlueChnl() {
		return blueChnl;
	}
	public List<AngelCodePageInfo> getPageInfoList() {
		return pageInfoList;
	}

	@Override
	public String toString() {
		return "AngelCodeMainInfo [face=" + face + ", size=" + size + ", bold="
				+ bold + ", italic=" + italic + ", charset=" + charset
				+ ", unicode=" + unicode + ", stretchH=" + stretchH
				+ ", smooth=" + smooth + ", aa=" + aa + ", padding="
				+ Arrays.toString(padding) + ", spacing="
				+ Arrays.toString(spacing) + ", outline=" + outline
				+ ", lineHeight=" + lineHeight + ", base=" + base + ", scaleW="
				+ scaleW + ", scaleH=" + scaleH + ", pages=" + pages
				+ ", packed=" + packed + ", alphaChnl=" + alphaChnl
				+ ", redChnl=" + redChnl + ", greenChnl=" + greenChnl
				+ ", blueChnl=" + blueChnl + ", pageInfoList=" + pageInfoList
				+ "]";
	}

	// for debug
	public static void main(String[] args) throws Exception {
		String filename = (args.length > 0) ? args[0] : "console.fnt";
		List<String> list = NUtil.getStringListFromURLE(NUtil.getURL(filename));
		AngelCodeMainInfo info = new AngelCodeMainInfo(list);
		LogFactory.getLog(AngelCodeMainInfo.class).debug(info.toString());
	}
}
