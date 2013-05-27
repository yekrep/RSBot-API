package org.powerbot.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import org.powerbot.bot.Bot;
import org.powerbot.gui.BotChrome;

/**
 * @author Paris
 */
public final class BotMenuInput {
	private static final long serialVersionUID = 9119892162553131816L;

	public BotMenuInput(final JMenu menu) {
		final boolean enabled = Bot.instantiated();

		JCheckBoxMenuItem item;
		final BotPanel panel = BotChrome.getInstance().panel;
		final int panelInputMask = panel.getInputMask();

		final Map<String, Integer> map = new LinkedHashMap<>();
		map.put(BotLocale.ALLOW, BotPanel.INPUT_MOUSE | BotPanel.INPUT_KEYBOARD);
		map.put(BotLocale.KEYBOARD, BotPanel.INPUT_KEYBOARD);
		map.put(BotLocale.BLOCK, 0);

		for (final Map.Entry<String, Integer> inputMask : map.entrySet()) {
			final int mask = inputMask.getValue();
			item = new JCheckBoxMenuItem(inputMask.getKey(), panelInputMask == mask);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e1) {
					panel.setInputMask(mask);
				}
			});

			item.setEnabled(enabled);
			menu.add(item);
		}
	}
}
