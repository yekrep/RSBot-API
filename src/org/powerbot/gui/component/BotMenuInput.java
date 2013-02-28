package org.powerbot.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

import org.powerbot.core.Bot;
import org.powerbot.gui.BotChrome;

/**
 * @author Paris
 */
public final class BotMenuInput extends JPopupMenu {
	private static final long serialVersionUID = 9119892162553131816L;

	public BotMenuInput() {
		super(BotLocale.INPUT);
		setEnabled(Bot.instantiated());

		JCheckBoxMenuItem item;
		final BotPanel panel = BotChrome.getInstance().panel;
		final int panelInputMask = panel.getInputMask();

		final Map<String, Integer> map = new LinkedHashMap<String, Integer>();
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

			add(item);
		}
	}
}
