package org.powerbot.os.misc;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Paris
 */
public class PrintStreamHandler extends Handler {
	@Override
	public void publish(final LogRecord record) {
		final PrintStream out = record.getLevel().intValue() >= Level.WARNING.intValue() ? System.err : System.out;
		out.print('[');
		out.print(record.getLoggerName());
		out.print("] ");
		out.println(record.getMessage());
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() {
	}
}
