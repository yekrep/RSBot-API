package org.powerbot.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import org.powerbot.bot.SelectiveEventQueue;

final class BotMenuInput {
	public BotMenuInput(final JMenu menu) {
		JCheckBoxMenuItem item;
		final SelectiveEventQueue eq = SelectiveEventQueue.getInstance();

		final Map<String, Boolean> map = new LinkedHashMap<String, Boolean>();
		map.put(BotLocale.ALLOW, false);
		map.put(BotLocale.BLOCK, true);

		for (final Map.Entry<String, Boolean> inputMask : map.entrySet()) {
			final boolean b = inputMask.getValue();
			item = new JCheckBoxMenuItem(inputMask.getKey(), eq.isBlocking() == b);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e1) {
					eq.setBlocking(b);
					//TODO focus if blocking
				}
			});
			menu.add(item);
		}
	}
}