package org.objectweb.proactive.extra.scheduler.core;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Tools class.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 26, 2007
 * @since ProActive 3.2
 */
public class Tools implements Serializable {

	/** Serial version UID */
	private static final long serialVersionUID = 2410081686855631412L;

	
	/**
	 * Format the given integer 'toFormat' to a String containing 'nbChar'
	 * characters
	 * 
	 * @param toFormat
	 *            the number to format
	 * @param nbChar
	 *            the number of characters of the formatted result string
	 * @return the given integer formatted as a 'nbChar' length String.
	 */
	public static String formatNChar(int toFormat, int nbChar) {
		String formatted = toFormat + "";
		while (formatted.length() < nbChar) {
			formatted = "0" + formatted;
		}
		return formatted;
	}

	
	/**
	 * Format 2 long times into a single duration as a String. The string will
	 * contains the duration in Days, hours, minutes, seconds, and millis.
	 * 
	 * @param start
	 *            the first date (time)
	 * @param end
	 *            the second date (time)
	 * @return the duration as a formatted string.
	 */
	public static String getFormattedDuration(long start, long end) {
		if ((start == -1) || (end == -1))
			return "Not yet";

		long duration = Math.abs(start - end);
		String formatted = "";
		int tmp;
		// Millisecondes
		tmp = (int) duration % 1000;
		duration = duration / 1000;
		formatted = tmp + "ms" + formatted;
		// Secondes
		tmp = (int) duration % 60;
		duration = duration / 60;
		if (tmp > 0)
			formatted = tmp + "s " + formatted;
		// Minutes
		tmp = (int) duration % 60;
		duration = duration / 60;
		if (tmp > 0)
			formatted = tmp + "m " + formatted;
		// Hours
		tmp = (int) duration % 24;
		duration = duration / 24;
		if (tmp > 0)
			formatted = tmp + "h " + formatted;
		// Days
		tmp = (int) duration;
		if (tmp > 0)
			formatted = tmp + " day" + ((tmp > 1) ? "s" : "") + " - " + formatted;
		return formatted;
	}

	
	/**
	 * Return the given date as a formatted string.
	 * 
	 * @param time the date as a long.
	 * @return the given date as a formatted string.
	 */
	public static String getFormattedDate(long time) {
		if (time == -1)
			return "Not yet";
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return String.format("%1$tT  %1$tD", calendar);
	}
	
}
