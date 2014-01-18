package org.powerbot.os.ui.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

/**
 * @author Paris
 */
public class BotPanel extends JPanel {
	private final LoadingIcon loading;

	public BotPanel() {
		setBackground(Color.BLACK);
		setForeground(Color.WHITE);

		setLayout(new GridBagLayout());

		loading = new LoadingIcon();
		loading.setPreferredSize(new Dimension(45, 45));
		add(loading);
	}

	public void setProgress(final int p) {
		loading.setProgress(p);
	}
}
