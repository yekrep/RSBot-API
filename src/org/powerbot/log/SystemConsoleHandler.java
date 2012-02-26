package org.powerbot.log;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class SystemConsoleHandler extends Handler {
	@Override
	public void close() throws SecurityException {
		System.out.close();
	}

	@Override
	public void flush() {
		System.out.flush();
	}

	@Override
	public void publish(final LogRecord record) {
		final String text = record.getMessage().trim();
		if (text.length() == 0) {
			return;
		}
		final int level = record.getLevel().intValue();
		final PrintStream out = level >= Level.WARNING.intValue() ? System.err : System.out;
		out.println(text);
	}
}
