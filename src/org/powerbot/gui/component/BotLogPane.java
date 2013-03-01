package org.powerbot.gui.component;

import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JTextPane;

/**
 * @author Paris
 */
public final class BotLogPane extends JTextPane {
	private static final long serialVersionUID = 8356685636339523410L;

	public BotLogPane() {
		setContentType("text/html");
		setEditable(false);
		setBackground(Color.DARK_GRAY);

		setText(".");
		setPreferredSize(new Dimension(getPreferredSize().width, (int) (getPreferredSize().height * 3.5d)));
		setText("");

		Logger.getLogger("").addHandler(new BotLogPaneHandler(this));
	}

	private final class BotLogPaneHandler extends Handler {
		private final StringBuilder s;
		private final String lf = "\n", html1 = "<html><body style=\"font-size: 11px; font-family: Consolas, Bitstream Vera Sans Mono, Liberation Mono, Courier, monospace; color: #999999;\">", html2 = "<body></html>";
		private int n = 0;
		private final int max = 100;
		private final JTextPane c;

		public BotLogPaneHandler(final JTextPane c) {
			this.c = c;
			s = new StringBuilder();
		}

		@Override
		public void publish(final LogRecord record) {
			if (++n > max) {
				n--;
				s.delete(0, s.indexOf(lf) + lf.length());
			}

			final StringBuilder m = new StringBuilder();
			final String timestamp = new SimpleDateFormat("H:m").format(new Date(record.getMillis()));
			m.append("<span style=\"color: #666666;\">");
			m.append(timestamp);
			m.append("</span>");
			m.append("&nbsp;&nbsp;&nbsp;");
			m.append(record.getMessage());

			if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
				s.append("<span style=\"color: #dc143c;\">");
				s.append(m);
				s.append("</span>");
			} else {
				s.append(m);
			}
			s.append("<br/>");
			s.append(lf);

			final StringBuilder t = new StringBuilder(s);
			t.insert(0, html1);
			t.append(html2);
			c.setText(t.toString());
			c.setCaretPosition(c.getDocument().getLength());
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() throws SecurityException {
		}
	}
}
