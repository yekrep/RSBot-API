package org.powerbot.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import org.powerbot.gui.BotChrome;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public final class BotInputView extends JMenu implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final BotChrome parent;
	final Map<String, Integer> map;

	public BotInputView(final BotMenu parent) {
		super(BotLocale.INPUT);
		this.parent = parent.parent.parent;
		setIcon(new ImageIcon(Resources.getImage(Resources.Paths.KEYBOARD)));

		if (parent.parent.getActiveTab() == -1) {
			setEnabled(false);
			map = null;
			return;
		}

		final int currentMask = this.parent.panel.getInputMask();

		map = new LinkedHashMap<String, Integer>();
		map.put(BotLocale.ALLOW, BotPanel.INPUT_MOUSE | BotPanel.INPUT_KEYBOARD);
		map.put(BotLocale.KEYBOARD, BotPanel.INPUT_KEYBOARD);
		map.put(BotLocale.MOUSE, BotPanel.INPUT_MOUSE);
		map.put(BotLocale.BLOCK, 0);

		for (final Map.Entry<String, Integer> entry : map.entrySet()) {
			final int mask = entry.getValue();
			final JCheckBoxMenuItem item = new JCheckBoxMenuItem(entry.getKey(), currentMask == mask);
			item.addActionListener(this);
			add(item);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (map == null) {
			return;
		}
		parent.panel.setInputMask(map.get(e.getActionCommand()));
	}
}
