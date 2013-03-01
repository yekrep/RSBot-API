package org.powerbot.gui.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.powerbot.gui.BotChrome;
import org.powerbot.util.StringUtil;

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

		final JTextPane pane = this;
		final BotLogPaneHandler handler = new BotLogPaneHandler(this);
		Logger.getLogger("").addHandler(handler);

		final JPopupMenu pop = new JPopupMenu();
		final JMenuItem copy = new JMenuItem(BotLocale.COPY);
		copy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final String s = pane.getSelectedText();
				if (s == null || s.isEmpty()) {
					return;
				}
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
			}
		});
		pop.add(copy);
		final JMenuItem saveas = new JMenuItem(BotLocale.SAVEAS);
		saveas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				BotFileChooser.saveAs(new ByteArrayInputStream(StringUtil.getBytesUtf8(handler.getText())), BotChrome.getInstance());
			}
		});
		pop.add(saveas);
		final JMenuItem clear = new JMenuItem(BotLocale.CLEAR);
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				handler.clear();
			}
		});
		pop.add(clear);
		pop.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
				final String s = pane.getSelectedText();
				copy.setEnabled(s != null && !s.isEmpty());
				saveas.setEnabled(!handler.isEmpty());
			}
			@Override
			public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
			}
			@Override
			public void popupMenuCanceled(final PopupMenuEvent e) {
			}
	    });

		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.isMetaDown()) {
					pop.show(pane, e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(final MouseEvent e) {
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
			}

			@Override
			public void mouseExited(final MouseEvent e) {
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				final String s = pane.getSelectedText();
				if (s != null && !s.isEmpty() && e.getKeyCode() == KeyEvent.VK_C && (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
				}
			}
		});
	}

	private final class BotLogPaneHandler extends Handler {
		private StringBuilder s;
		private final String lf = "\n", html1 = "<html><body style=\"font-size: 11px; font-family: Consolas, Bitstream Vera Sans Mono, Liberation Mono, Courier, monospace; color: #999999;\">", html2 = "<body></html>";
		private int n = 0;
		private final int max = 100;
		private final JTextPane c;

		public BotLogPaneHandler(final JTextPane c) {
			this.c = c;
			s = new StringBuilder();
		}

		public void clear() {
			s = new StringBuilder();
			n = 0;
			c.setText("");
		}

		public String getText() {
			return StringUtil.stripHtml(c.getText().replace("<br>", "\r\n")).trim().replace("&#160;&#160;&#160;", "\t").replace("&#160;", " ").replaceAll("  +", "");
		}

		public boolean isEmpty() {
			return n == 0;
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
			if (record.getMessage().length() > 100) {
				m.append("<span style=\"font-size: 8px;\">");
				m.append(record.getMessage());
				m.append("</span>");
			} else {
				m.append(record.getMessage());
			}

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
