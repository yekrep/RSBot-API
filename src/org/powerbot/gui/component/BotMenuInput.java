package org.powerbot.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import org.powerbot.gui.BotChrome;

/**
 * @author Paris
 */
public final class BotMenuInput {
	public BotMenuInput(final JMenu menu) {
		JCheckBoxMenuItem item;
		final BotChrome eq = BotChrome.getInstance();

		final Map<String, Boolean> map = new LinkedHashMap<>();
		map.put(BotLocale.ALLOW, false);
		map.put(BotLocale.BLOCK, true);

		for (final Map.Entry<String, Boolean> inputMask : map.entrySet()) {
			final boolean b = inputMask.getValue();
			item = new JCheckBoxMenuItem(inputMask.getKey(), eq.isBlocking() == b);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e1) {
					eq.setBlocking(b);
				}
			});
			menu.add(item);
		}
	}
}