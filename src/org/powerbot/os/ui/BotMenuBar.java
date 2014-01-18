package org.powerbot.os.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.powerbot.os.Boot;
import org.powerbot.os.Configuration;

/**
 * @author Paris
 */
public class BotMenuBar extends JMenuBar {
	private final BotChrome chrome;

	public BotMenuBar(final BotChrome chrome) {
		this.chrome = chrome;

		final JMenu file = new JMenu("File"), edit = new JMenu("Edit"), view = new JMenu("View"), help = new JMenu("Help");

		final JMenuItem fork = new JMenuItem("New Window");
		file.add(fork);
		fork.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				Boot.fork();
			}
		});

		if (Configuration.OS != Configuration.OperatingSystem.MAC) {
			file.addSeparator();

			final JMenuItem exit = new JMenuItem("Exit");
			file.add(exit);
			exit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					chrome.close();
				}
			});
		}

		final JMenuItem license = new JMenuItem("License");
		help.add(license);
		license.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				BotChrome.openURL(Configuration.URLs.LICENSE);
			}
		});

		final JMenuItem about = new JMenuItem("About");
		if (Configuration.OS != Configuration.OperatingSystem.MAC) {
			help.add(about);
		}
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				showAbout();
			}
		});

		add(file);
		add(edit);
		add(view);
		add(help);
	}

	public void showAbout() {
		final String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		final String msg = "<b>" + Configuration.NAME + " " + Integer.toString(Configuration.VERSION) + "</b>\n\n" +
				"Copyright \u00a9 2011 - " + year + " Dequeue Ltd and its licensors.\n" +
				"By using this software you agree to be bound by the terms of the license agreement.\n\n" +
				"RuneScape\u00ae is a trademark of Jagex \u00a9 1999 - " + year + " Jagex Ltd.\n" +
				"RuneScape content and materials are trademarks and copyrights of Jagex or its licensees.\n" +
				"This program is issued with no warranty and is not affiliated with Jagex Ltd., nor do they endorse usage of our software.";
		final JLabel text = new JLabel("<html>" + msg.replace("\n", "<br>") + "</html>");
		final Font f = text.getFont();
		text.setFont(new Font(f.getName(), f.getStyle(), f.getSize() - 2));
		JOptionPane.showMessageDialog(chrome, text, "About", JOptionPane.PLAIN_MESSAGE);
	}
}
