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
public final class BotInputView extends JMenu {
	private static final long serialVersionUID = 1L;

	public BotInputView(final BotMenu parent) {
		super(BotLocale.INPUT);
		setIcon(new ImageIcon(Resources.getImage(Resources.Paths.KEYBOARD)));

		if (parent.parent.getActiveTab() == -1) {
			setEnabled(false);
			return;
		}

		final int panelInputMask = BotChrome.panel.getInputMask();

		final Map<String, Integer> inputMap = new LinkedHashMap<String, Integer>();
		inputMap.put("Allow", BotPanel.INPUT_MOUSE | BotPanel.INPUT_KEYBOARD);
		inputMap.put("Keyboard only", BotPanel.INPUT_KEYBOARD);
		inputMap.put("Mouse only", BotPanel.INPUT_MOUSE);
		inputMap.put("Block", 0);

		for (final Map.Entry<String, Integer> inputMask : inputMap.entrySet()) {
			final int mask = inputMask.getValue();
			final JCheckBoxMenuItem item = new JCheckBoxMenuItem(inputMask.getKey(), panelInputMask == mask);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					BotChrome.panel.setInputMask(mask);
				}
			});

			add(item);
		}
	}
}
