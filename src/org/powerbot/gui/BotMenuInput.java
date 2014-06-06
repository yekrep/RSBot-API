package org.powerbot.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import org.powerbot.script.Input;

final class BotMenuInput {
	public BotMenuInput(final JMenu menu, final Input input) {
		JCheckBoxMenuItem item;

		final Map<String, Boolean> map = new LinkedHashMap<String, Boolean>();
		map.put(BotLocale.ALLOW, false);
		map.put(BotLocale.BLOCK, true);

		for (final Map.Entry<String, Boolean> inputMask : map.entrySet()) {
			final boolean b = inputMask.getValue();
			item = new JCheckBoxMenuItem(inputMask.getKey(), input.blocking() == b);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e1) {
					input.blocking(b);
				}
			});
			menu.add(item);
		}
	}
}