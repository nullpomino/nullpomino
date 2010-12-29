package mu.nu.nullpo.game.net;

import java.io.Serializable;
import java.util.LinkedList;

import mu.nu.nullpo.util.CustomProperties;

/**
 * Single player mode ranking
 */
public class NetSPRanking implements Serializable {
	/** serialVersionUID for Serialize */
	private static final long serialVersionUID = 1L;

	/** Game Mode Name */
	public String strModeName;

	/** Rule Name */
	public String strRuleName;

	/** Game Type ID */
	public int gameType;

	/** Game Style ID */
	public int style;

	/** Ranking Type */
	public int rankingType;

	/** Max number of records (-1:Unlimited) */
	public int maxRecords;

	/** Records */
	public LinkedList<NetSPRecord> listRecord;

	/**
	 * Default Constructor
	 */
	public NetSPRanking() {
		reset();
	}

	/**
	 * Copy Constructor
	 * @param s Source
	 */
	public NetSPRanking(NetSPRanking s) {
		copy(s);
	}

	/**
	 * Constructor
	 * @param modename Game Mode Name
	 * @param rulename Rule Name
	 * @param gtype Game Type ID
	 * @param style Game Style ID
	 * @param rtype Ranking Type
	 * @param max Max number of records
	 */
	public NetSPRanking(String modename, String rulename, int gtype, int style, int rtype, int max) {
		reset();
		this.strModeName = modename;
		this.strRuleName = rulename;
		this.gameType = gtype;
		this.style = style;
		this.rankingType = rtype;
		this.maxRecords = max;
	}

	/**
	 * Initialization
	 */
	public void reset() {
		strModeName = "";
		strRuleName = "";
		gameType = 0;
		style = 0;
		rankingType = 0;
		maxRecords = 100;
		listRecord = new LinkedList<NetSPRecord>();
	}

	/**
	 * Copy from other NetSPRankingData
	 * @param s Source
	 */
	public void copy(NetSPRanking s) {
		strModeName = s.strModeName;
		strRuleName = s.strRuleName;
		gameType = s.gameType;
		style = s.style;
		rankingType = s.rankingType;
		maxRecords = s.maxRecords;
		listRecord = new LinkedList<NetSPRecord>();
		for(int i = 0; i < s.listRecord.size(); i++) {
			listRecord.add(new NetSPRecord(s.listRecord.get(i)));
		}
	}

	/**
	 * Get specific player's record
	 * @param strPlayerName Player Name
	 * @return NetSPRecord (null if not found)
	 */
	public NetSPRecord getRecord(String strPlayerName) {
		int index = indexOf(strPlayerName);
		return (index == -1) ? null : listRecord.get(index);
	}

	/**
	 * Get specific player's record
	 * @param pInfo NetPlayerInfo
	 * @return NetSPRecord (null if not found)
	 */
	public NetSPRecord getRecord(NetPlayerInfo pInfo) {
		return getRecord(pInfo.strName);
	}

	/**
	 * Get specific player's index
	 * @param strPlayerName Player Name
	 * @return Index (-1 if not found)
	 */
	public int indexOf(String strPlayerName) {
		for(int i = 0; i < listRecord.size(); i++) {
			NetSPRecord r = listRecord.get(i);
			if(r.strPlayerName.equals(strPlayerName)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get specific player's index
	 * @param pInfo NetPlayerInfo
	 * @return Index (-1 if not found)
	 */
	public int indexOf(NetPlayerInfo pInfo) {
		return indexOf(pInfo.strName);
	}

	/**
	 * Remove specific player's record
	 * @param strPlayerName Player Name
	 * @return Number of records removed (0 if not found)
	 */
	public int removeRecord(String strPlayerName) {
		int count = 0;

		LinkedList<NetSPRecord> list = new LinkedList<NetSPRecord>(listRecord);
		for(int i = 0; i < list.size(); i++) {
			NetSPRecord r = list.get(i);

			if(r.strPlayerName.equals(strPlayerName)) {
				listRecord.remove(i);
				count++;
			}
		}

		return count;
	}

	/**
	 * Remove specific player's record
	 * @param pInfo NetPlayerInfo
	 * @return Number of records removed (0 if not found)
	 */
	public int removeRecord(NetPlayerInfo pInfo) {
		return removeRecord(pInfo.strName);
	}

	/**
	 * Checks if r1 is a new record.
	 * @param r1 Newer Record
	 * @return Returns <code>true</code> if there are no previous record of this player, or if the newer record (r1) is better than old one.
	 */
	public boolean isNewRecord(NetSPRecord r1) {
		NetSPRecord r2 = getRecord(r1.strPlayerName);
		if(r2 == null) return true;
		return r1.compare(rankingType, r2);
	}

	/**
	 * Register a new record
	 * @param r1 Record
	 * @return Rank (-1 if out of rank)
	 */
	public int registerRecord(NetSPRecord r1) {
		if(!isNewRecord(r1)) return -1;

		// Remove older records
		removeRecord(r1.strPlayerName);

		// Insert new record
		LinkedList<NetSPRecord> list = new LinkedList<NetSPRecord>(listRecord);
		int rank = -1;

		for(int i = 0; i < list.size(); i++) {
			if(r1.compare(rankingType, list.get(i))) {
				listRecord.add(i, r1);
				rank = i;
				break;
			}
		}

		// Couldn't rank in? Add to last.
		if(rank == -1) {
			listRecord.add(r1);
			rank = listRecord.size() - 1;
		}

		// Remove anything after maxRecords
		while(listRecord.size() >= maxRecords) listRecord.removeLast();

		// Done
		return (rank >= maxRecords) ? -1 : rank;
	}

	/**
	 * Write to a CustomProperties
	 * @param prop CustomProperties
	 */
	public void writeProperty(CustomProperties prop) {
		String strKey = "spranking." + strRuleName + "." + strModeName + "." + gameType + ".";
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
		String strKey = "spranking." + strRuleName + "." + strModeName + "." + gameType + ".";
		int numRecords = prop.getProperty(strKey + "numRecords", 0);
		if(numRecords > maxRecords) numRecords = maxRecords;

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
	 * Condense a list of rankings into a single ranking file.
	 * @param s The list of rankings.
	 * @return A ranking that is the combination of all of the rankings.
	 */
	public static NetSPRanking mergeRankings(LinkedList<NetSPRanking> s) {
		if (s == null || s.size() == 0) { return null; }
		NetSPRanking acc = new NetSPRanking(s.get(0));
		for (NetSPRanking r : s) {
			for(int i = 0; i < r.listRecord.size(); i++) {
				acc.registerRecord(new NetSPRecord(r.listRecord.get(i)));
			}
		}
		return acc;
	}
}
