package org.powerbot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.powerbot.gui.component.BotLocale;
import org.powerbot.util.Configuration;
import org.powerbot.util.io.Resources;

public final class BotAbout extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JLabel visit;
	private final JButton ok;

	public BotAbout(final Frame parent) {
		super(parent, BotLocale.ABOUT, true);

		final int f = 5;
		final GridLayout gridText = new GridLayout(0, 1);
		final JPanel panelText = new JPanel(gridText);
		panelText.setBorder(BorderFactory.createEmptyBorder(f * 2, f * 3, f, f));
		panelText.setBackground(Color.WHITE);

		final JLabel name = new JLabel(Configuration.NAME + " " + Integer.toString(Configuration.VERSION));
		name.setFont(name.getFont().deriveFont(Font.BOLD));
		name.setForeground(Color.DARK_GRAY);
		panelText.add(name);
		panelText.add(Box.createVerticalStrut(1));
		panelText.add(new JLabel(BotLocale.COPYRIGHT));
		panelText.add(new JLabel("Unauthorised use of this application is prohibited."));
		panelText.add(Box.createVerticalStrut(1));

		final String[] jagex = {
				"RuneScape\u00ae is a trademark of Jagex \u00a9 1999 - 2012 Jagex Ltd.",
				"RuneScape content and materials are trademarks and copyrights of Jagex or its licensees.",
				"This program is issued with no warranty and is not affiliated with Jagex Ltd., nor do they endorse usage of our software." };

		for (final String line : jagex) {
			final JLabel item = new JLabel(line);
			item.setFont(item.getFont().deriveFont(item.getFont().getSize() - 2f));
			item.setForeground(Color.GRAY);
			panelText.add(item);
		}

		final GridLayout gridAction = new GridLayout(1, 2);
		gridAction.setHgap(f);
		final JPanel panelAction = new JPanel(gridAction);
		final int pad = gridAction.getHgap();
		panelAction.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));

		visit = new JLabel("<html><a href='#'>" + BotLocale.WEBSITE + "</a></html>");
		visit.setPreferredSize(new Dimension((int) (visit.getPreferredSize().width * 1.2), (int) (visit.getPreferredSize().height * 1.2)));
		visit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent arg0) {
				BotChrome.openURL(Resources.getServerLinks().get("site"));
			}
		});
		ok = new JButton(BotLocale.OK);
		ok.setPreferredSize(new Dimension((int) (ok.getPreferredSize().width * 1.2), (int) (ok.getPreferredSize().height * 1.2)));
		ok.setFocusable(false);
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				dispose();
			}
		});
		panelAction.add(visit);
		for (int i = 0; i < 3; i++) {
			panelAction.add(Box.createHorizontalGlue());
		}
		panelAction.add(ok);

		final JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(panelText);

		add(panel);
		add(panelAction, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(ok);
		ok.requestFocusInWindow();

		pack();
		setMinimumSize(getSize());
		setResizable(false);
		setLocationRelativeTo(getParent());
		setVisible(true);
	}
}
