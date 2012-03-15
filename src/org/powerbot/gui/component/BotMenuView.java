package org.powerbot.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

/**
 * @author Paris
 */
public final class BotMenuView extends JMenu implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final Map<String, EventListener> map;

	private static final String ALL = "All";

	public BotMenuView() {
		super(BotLocale.VIEW);

		final JCheckBoxMenuItem all = new JCheckBoxMenuItem(ALL);
		all.addActionListener(this);
		add(all);
		addSeparator();
	
		map = new HashMap<String, EventListener>();
		map.put("Client State", null);
		map.put("Plane", null);
		map.put("Position", null);
		
		for (final Entry<String, EventListener> entry : map.entrySet()) {
			final JCheckBoxMenuItem item = new JCheckBoxMenuItem(entry.getKey(), false);
			item.addActionListener(this);
			add(item);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
		item.setSelected(!item.isSelected());
		if (item.getText().equals(ALL)) {
			for (final Entry<String, EventListener> entry : map.entrySet()) {
				setView(entry.getValue(), item.isSelected());
			}
		} else {
			setView(map.get(item.getText()), item.isSelected());
		}
	}

	private void setView(final EventListener eventListener, final boolean selected) {
		// TODO: add/remove event listener
	}
}
