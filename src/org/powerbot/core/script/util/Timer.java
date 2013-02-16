package org.powerbot.core.script.util;

public class Timer {
	private final long start;
	private final long period;
	private long end;

	public Timer(final long period) {
		this.period = period * 1000000;
		start = System.nanoTime();
		end = start + this.period;
	}

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

	public long getElapsed() {
		return (System.nanoTime() - start) / 1000000;
	}

	public long getRemaining() {
		if (isRunning()) {
			return (end - System.nanoTime()) / 1000000;
		}
		return 0;
	}

	public boolean isRunning() {
		return System.nanoTime() < end;
	}

	public void reset() {
		end = System.nanoTime() + period;
	}

	public long setEndIn(final long ms) {
		end = System.nanoTime() + ms * 1000000;
		return end;
	}

	public String toElapsedString() {
		return format(getElapsed());
	}

	public String toRemainingString() {
		return format(getRemaining());
	}
}
