package mu.nu.nullpo.game.net;

import java.io.Serializable;
import java.util.LinkedList;

import mu.nu.nullpo.util.CustomProperties;

/**
 * Single player personal record manager
 */
public class NetSPPersonalBest implements Serializable {
	/** serialVersionUID for Serialize */
	private static final long serialVersionUID = 1L;

	/** Player Name */
	public String strPlayerName;

	/** Records */
	public LinkedList<NetSPRecord> listRecord;

	/**
	 * Constructor
	 */
	public NetSPPersonalBest() {
		reset();
	}

	/**
	 * Copy Constructor
	 * @param s Source
	 */
	public NetSPPersonalBest(NetSPPersonalBest s) {
		copy(s);
	}

	/**
	 * Constructor that imports data from a String Array
	 * @param s String Array (String[2])
	 */
	public NetSPPersonalBest(String[] s) {
		importStringArray(s);
	}

	/**
	 * Constructor that imports data from a String
	 * @param s String (Split by ;)
	 */
	public NetSPPersonalBest(String s) {
		importString(s);
	}

	/**
	 * Initialization
	 */
	public void reset() {
		strPlayerName = "";
		listRecord = new LinkedList<NetSPRecord>();
	}

	/**
	 * Copy from other NetSPPersonalBest
	 * @param s Source
	 */
	public void copy(NetSPPersonalBest s) {
		strPlayerName = s.strPlayerName;
		listRecord = new LinkedList<NetSPRecord>();
		for(int i = 0; i < s.listRecord.size(); i++) {
			listRecord.add(new NetSPRecord(s.listRecord.get(i)));
		}
	}

	/**
	 * Get specific NetSPRecord
	 * @param rule Rule Name
	 * @param mode Mode Name
	 * @param gtype Game Type
	 * @return NetSPRecord (null if not found)
	 */
	public NetSPRecord getRecord(String rule, String mode, int gtype) {
		for(int i = 0; i < listRecord.size(); i++) {
			NetSPRecord r = listRecord.get(i);
			if(r.strRuleName.equals(rule) && r.strModeName.equals(mode) && r.gameType == gtype) {
				return r;
			}
		}
		return null;
	}

	/**
	 * Checks if r1 is a new record.
	 * @param rtype Ranking Type
	 * @param r1 Newer Record
	 * @return Returns <code>true</code> if there are no previous record of this player, or if the newer record (r1) is better than old one.
	 */
	public boolean isNewRecord(int rtype, NetSPRecord r1) {
		NetSPRecord r2 = getRecord(r1.strRuleName, r1.strModeName, r1.gameType);
		if(r2 == null) return true;
		return r1.compare(rtype, r2);
	}

	/**
	 * Register a record.
	 * @param rtype Ranking Type
	 * @param r1 Newer Record
	 * @return Returns <code>true</code> if the newer record (r1) is registered.
	 */
	public boolean registerRecord(int rtype, NetSPRecord r1) {
		NetSPRecord r2 = getRecord(r1.strRuleName, r1.strModeName, r1.gameType);

		if(r2 != null) {
			if(r1.compare(rtype, r2)) {
				// Replace with a new record
				r2.copy(r1);
			} else {
				return false;
			}
		} else {
			// Register a new record
			listRecord.add(r1);
		}

		return true;
	}

	/**
	 * Write to a CustomProperties
	 * @param prop CustomProperties
	 */
	public void writeProperty(CustomProperties prop) {
		String strKey = "sppersonal." + strPlayerName + ".";
		prop.setProperty(strKey + "numRecords", listRecord.size());

		for(int i = 0; i < listRecord.size(); i++) {
			NetSPRecord record = listRecord.get(i);
			String strRecordCompressed = NetUtil.compressString(record.exportString());
			prop.setProperty(strKey + i, strRecordCompressed);
		}
	}

	/**
	 * Read from a CustomProperties
	 * @param prop CustomProperties
	 */
	public void readProperty(CustomProperties prop) {
		String strKey = "sppersonal." + strPlayerName + ".";
		int numRecords = prop.getProperty(strKey + "numRecords", 0);

		listRecord.clear();
		for(int i = 0; i < numRecords; i++) {
			String strRecordCompressed = prop.getProperty(strKey + i);
			if(strRecordCompressed != null) {
				String strRecord = NetUtil.decompressString(strRecordCompressed);
				NetSPRecord record = new NetSPRecord(strRecord);
				listRecord.add(record);
			}
		}
	}

	/**
	 * Export the records to a String
	 * @return String (Split by ;)
	 */
	public String exportListRecord() {
		String strResult = "";
		for(int i = 0; i < listRecord.size(); i++) {
			if(i > 0) strResult += ";";
			strResult += NetUtil.compressString(listRecord.get(i).exportString());
		}
		return strResult;
	}

	/**
	 * Import the record from a String
	 * @param s String (Split by ;)
	 */
	public void importListRecord(String s) {
		listRecord.clear();

		String[] array = s.split(";");
		for(int i = 0; i < array.length; i++) {
			String strTemp = NetUtil.decompressString(array[i]);
			NetSPRecord record = new NetSPRecord(strTemp);
			listRecord.add(record);
		}
	}

	/**
	 * Export to a String Array
	 * @return String Array (String[2])
	 */
	public String[] exportStringArray() {
		String[] s = new String[2];
		s[0] = NetUtil.urlEncode(strPlayerName);
		s[1] = exportListRecord();
		return s;
	}

	/**
	 * Export to a String
	 * @return String (Split by ;)
	 */
	public String exportString() {
		String[] array = exportStringArray();
		String result = "";

		for(int i = 0; i < array.length; i++) {
			if(i > 0) result += ";";
			result += array[i];
		}

		return result;
	}


	/**
	 * Import from a String Array
	 * @param s String Array (String[8])
	 */
	public void importStringArray(String[] s) {
		if(s.length > 0) strPlayerName = NetUtil.urlDecode(s[0]);
		if(s.length > 1) importListRecord(s[1]);
	}

	/**
	 * Import from a String
	 * @param s String (Split by ;)
	 */
	public void importString(String s) {
		importStringArray(s.split(";"));
	}
}
