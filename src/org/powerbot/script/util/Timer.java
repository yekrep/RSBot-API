package org.powerbot.script.util;

import java.util.concurrent.atomic.AtomicLong;

public class Timer {
	private final long MS_TO_NANOS = 1000000L;
	private final AtomicLong start, end, period;

	public Timer(final long period) {
		this.period = new AtomicLong(period * MS_TO_NANOS);
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
		return (now() - start.get()) / MS_TO_NANOS;
	}

	public long getRemaining() {
		return Math.max(0, (end.get() - now()) / MS_TO_NANOS);
	}

	public boolean isRunning() {
		return now() < end.get();
	}

	public void reset() {
		start.set(now());
		end.set(start.get() + period.get());
	}

	public long setEndIn(final long ms) {
		end.set(now() + ms * MS_TO_NANOS);
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
