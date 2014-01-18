package org.powerbot.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.powerbot.Configuration;
import org.powerbot.gui.component.BotLocale;
import org.powerbot.misc.Resources;
import org.powerbot.misc.Tracker;
import org.powerbot.util.IOUtils;

/**
 * @author Paris
 */
public final class BotLicense extends JDialog {

	private static final long serialVersionUID = 6709745218034283085L;

	public BotLicense(final Frame owner) {
		super(owner, BotLocale.LICENSETCS, true);

		String license = "Unable to locate license file, please visit " + Configuration.URLs.DOMAIN + " to view license information";
		String acknowledgements = "To view acknowledgements, please visit " + Configuration.URLs.DOMAIN;
		try {
			license = IOUtils.readString(Resources.getResourceURL(Resources.Paths.LICENSE)).trim();
			acknowledgements = IOUtils.readString(Resources.getResourceURL(Resources.Paths.ACKNOWLEDGEMENTS)).trim();
		} catch (final IOException ignored) {
		}

		final String lf = System.getProperty("line.separator");
		final StringBuilder s = new StringBuilder(license.length() + acknowledgements.length() + lf.length() * 2);
		s.append(license).append(lf).append(lf).append(acknowledgements);

		final JTextArea text = new JTextArea(s.toString());
		text.setEditable(false);
		text.setFont(getFont());
		final JScrollPane scroll = new JScrollPane(text);
		scroll.setPreferredSize(new Dimension(600, 250));
		add(scroll);

		pack();
		setMinimumSize(getSize());
		setResizable(false);
		setLocationRelativeTo(getParent());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);

		Tracker.getInstance().trackPage("license/", getTitle());
	}
}
