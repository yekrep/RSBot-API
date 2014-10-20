package org.powerbot.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class TextFormatter extends Formatter {
	private final DateFormat f;

	public TextFormatter() {
		f = new SimpleDateFormat("HHmm ");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	public String format(final LogRecord record) {
		return f.format(new Date()) + record.getMessage();
	}
}
