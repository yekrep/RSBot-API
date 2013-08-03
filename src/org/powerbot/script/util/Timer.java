package org.powerbot.script.util;

import java.util.concurrent.atomic.AtomicLong;

public class Timer {
	private final AtomicLong start, end, period;

	public Timer(final long period) {
		this.period = new AtomicLong(period * 1000000);
		start = new AtomicLong(now());
		end = new AtomicLong(start.get() + this.period.get());
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
		return (now() - start.get()) / 1000000;
	}

	public long getRemaining() {
		if (isRunning()) {
			return (end.get() - now()) / 1000000;
		}
		return 0;
	}

	public boolean isRunning() {
		return now() < end.get();
	}

	public void reset() {
		end.set(now() + period.get());
	}

	public long setEndIn(final long ms) {
		end.set(now() + ms * 1000000);
		return end.get();
	}

	public String toElapsedString() {
		return format(getElapsed());
	}

	public String toRemainingString() {
		return format(getRemaining());
	}

	private long now() {
		return System.nanoTime();
	}
}
