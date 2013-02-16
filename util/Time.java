package org.powerbot.game.api.util;

/**
 * A utility for manipulating time.
 *
 * @author Timer
 */
public class Time {
	/**
	 * Converts milliseconds to a String in the format
	 * hh:mm:ss.
	 *
	 * @param time The number of milliseconds.
	 * @return The formatted String.
	 */
	public static String format(final long time) {
		final StringBuilder t = new StringBuilder();
		final long total_secs = time / 1000;
		final long total_mins = total_secs / 60;
		final long total_hrs = total_mins / 60;
		final long total_days = total_hrs / 24;
		final int secs = (int) total_secs % 60;
		final int mins = (int) total_mins % 60;
		final int hrs = (int) total_hrs % 24;
		final int days = (int) total_days;
		if (days > 0) {
			if (days < 10) {
				t.append("0");
			}
			t.append(days);
			t.append(":");
		}
		if (hrs < 10) {
			t.append("0");
		}
		t.append(hrs);
		t.append(":");
		if (mins < 10) {
			t.append("0");
		}
		t.append(mins);
		t.append(":");
		if (secs < 10) {
			t.append("0");
		}
		t.append(secs);
		return t.toString();
	}
}