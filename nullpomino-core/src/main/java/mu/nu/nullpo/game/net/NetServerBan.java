package mu.nu.nullpo.game.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.TimeZone;

import mu.nu.nullpo.util.GeneralUtil;

import org.apache.log4j.Logger;

import biz.source_code.base64Coder.Base64Coder;

public class NetServerBan {
	static Logger log = Logger.getLogger(NetServerBan.class);

	public String addr;

	public Calendar startDate;
	public int banLength;

	public static final int BANLENGTH_1HOUR = 0,
							BANLENGTH_6HOURS = 1,
							BANLENGTH_24HOURS = 2,
							BANLENGTH_1WEEK = 3,
							BANLENGTH_1MONTH = 4,
							BANLENGTH_1YEAR = 5,
							BANLENGTH_PERMANENT = 6;

	public static final int BANLENGTH_TOTAL = 7;

	/**
	 * Empty Constructor
	 */
	public NetServerBan() {
	}

	/**
	 * Creates a new NetServerBan object representing a permanent ban starting now.
	 * @param addr the remote address this NetServerBan affects.
	 */
	public NetServerBan(String addr) {
		this(addr, BANLENGTH_PERMANENT);
	}

	/**
	 * Creates a new NetServerBan object representing a ban starting now.
	 * @param addr the remote address this NetServerBan affects.
	 * @param banLength an integer representing the length of the ban.
	 */
	public NetServerBan(String addr, int banLength) {
		this.addr = addr;
		startDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		this.banLength = banLength;
	}

	/**
	 * Returns the end date of the ban, or null if no such date exists (i.e. permanent).
	 * @return the end date or null
	 */
	public Calendar getEndDate() {
		Calendar res = (Calendar) startDate.clone();
		switch (banLength) {
			case BANLENGTH_1HOUR: res.add(Calendar.HOUR, 1); break;
			case BANLENGTH_6HOURS: res.add(Calendar.HOUR, 6); break;
			case BANLENGTH_24HOURS: res.add(Calendar.HOUR, 24); break;
			case BANLENGTH_1WEEK: res.add(Calendar.WEEK_OF_MONTH, 1); break;
			case BANLENGTH_1MONTH: res.add(Calendar.MONTH, 1); break;
			case BANLENGTH_1YEAR: res.add(Calendar.YEAR, 1); break;
			default: res = null;
		}
		return res;
	}

	/**
	 * Returns a boolean representing whether or not this NetServerBan is expired.
	 * @return true if the ban is expired.
	 */
	public boolean isExpired() {
		Calendar endDate = getEndDate();
		if (endDate == null) {
			return false;
		} else {
			return Calendar.getInstance(TimeZone.getTimeZone("GMT")).after(getEndDate());
		}
	}

	/**
	 * Export startDate to String
	 * @return String (null if fails)
	 */
	public String exportStartDate() {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bout);
			oos.writeObject(startDate);
			byte[] bTemp = NetUtil.compressByteArray(bout.toByteArray());
			char[] cTemp = Base64Coder.encode(bTemp);
			return new String(cTemp);
		} catch (Exception e) {
			log.error("Failed to export startDate", e);
		}
		return null;
	}

	/**
	 * Import startDate from String
	 * @param strInput String
	 * @return true if success
	 */
	public boolean importStartDate(String strInput) {
		try {
			if(strInput.startsWith("GMT")) {
				// GMT String
				Calendar c = GeneralUtil.importCalendarString(strInput.substring(3));
				if(c != null) {
					startDate = c;
					return true;
				}
			} else {
				// Object Stream
				byte[] bTemp = Base64Coder.decode(strInput);
				byte[] bTemp2 = NetUtil.decompressByteArray(bTemp);
				ByteArrayInputStream bin = new ByteArrayInputStream(bTemp2);
				ObjectInputStream oin = new ObjectInputStream(bin);
				startDate = (Calendar)oin.readObject();
				return true;
			}
		} catch (Exception e) {
			log.error("Failed to import startDate", e);
		}
		return false;
	}

	/**
	 * Export to String
	 * @return String
	 */
	public String exportString() {
		//String strStartDate = exportStartDate();
		String strTemp = GeneralUtil.exportCalendarString(startDate);
		String strStartDate = "";
		if(strTemp != null) strStartDate = "GMT" + strTemp;
		return addr + ";" + banLength + ";" + strStartDate;
	}

	/**
	 * Import from String
	 * @param strInput String
	 */
	public void importString(String strInput) {
		String[] strArray = strInput.split(";");
		addr = strArray[0];
		banLength = Integer.parseInt(strArray[1]);
		importStartDate(strArray[2]);
	}
}
