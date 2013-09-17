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
		final BotChrome chrome = BotChrome.getInstance();
		final int panelInputMask = chrome.getInputMask();

		final Map<String, Integer> map = new LinkedHashMap<>();
		map.put(BotLocale.ALLOW, BotChrome.INPUT_MOUSE | BotChrome.INPUT_KEYBOARD);
		map.put(BotLocale.KEYBOARD, BotChrome.INPUT_KEYBOARD);
		map.put(BotLocale.BLOCK, 0);

		for (final Map.Entry<String, Integer> inputMask : map.entrySet()) {
			final int mask = inputMask.getValue();
			item = new JCheckBoxMenuItem(inputMask.getKey(), panelInputMask == mask);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e1) {
					chrome.setInputMask(mask);
				}
			});
			menu.add(item);
		}
	}
}