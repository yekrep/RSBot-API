package org.powerbot.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import org.powerbot.game.bot.Bot;
import org.powerbot.game.bot.event.impl.TClientState;
import org.powerbot.game.bot.event.impl.TPlane;

/**
 * @author Paris
 */
public final class BotMenuView extends JMenu implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final BotMenu parent;
	private final Map<String, Class<? extends EventListener>> map;
	private static Map<Bot, Map<String, EventListener>> listeners;

	private static final String ALL = "All";

	public BotMenuView(final BotMenu parent) {
		super(BotLocale.VIEW);
		this.parent = parent;

		final JCheckBoxMenuItem all = new JCheckBoxMenuItem(ALL);
		all.addActionListener(this);
		add(all);
		addSeparator();

		if (listeners == null) {
			listeners = new HashMap<Bot, Map<String, EventListener>>();
		}
		map = new TreeMap<String, Class<? extends EventListener>>();
		map.put("Client State", TClientState.class);
		map.put("Plane", TPlane.class);

		final Bot bot = Bot.bots.get(parent.parent.getOpenedTab());
		Map<String, EventListener> listeners = BotMenuView.listeners.get(bot);
		if (listeners == null) {
			listeners = new HashMap<String, EventListener>();
			BotMenuView.listeners.put(bot, listeners);
		}

		for (final Entry<String, Class<? extends EventListener>> entry : map.entrySet()) {
			final boolean selected = listeners.containsKey(entry.getValue().getName());
			final JCheckBoxMenuItem item = new JCheckBoxMenuItem(entry.getKey(), selected);
			item.addActionListener(this);
			add(item);
		}
	}

	public void actionPerformed(final ActionEvent e) {
		final JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
		item.setSelected(!item.isSelected());
		if (item.getText().equals(ALL)) {
			for (final Entry<String, Class<? extends EventListener>> entry : map.entrySet()) {
				setView(entry.getValue(), item.isSelected());
			}
		} else {
			setView(map.get(item.getText()), item.isSelected());
		}
	}

	private void setView(final Class<? extends EventListener> eventListener, final boolean selected) {
		final Bot bot = Bot.bots.get(parent.parent.getOpenedTab());
		final String name = eventListener.getName();
		Map<String, EventListener> listeners = BotMenuView.listeners.get(bot);
		if (listeners == null) {
			listeners = new HashMap<String, EventListener>();
			BotMenuView.listeners.put(bot, listeners);
		}
		if (!selected) {
			if (listeners.containsKey(name)) {
				return;
			}
			try {
				final EventListener listener = eventListener.asSubclass(EventListener.class).newInstance();
				listeners.put(name, listener);
				bot.eventDispatcher.accept(listener);
			} catch (final Exception ignored) {
			}
		} else {
			final EventListener listener = listeners.get(name);
			if (listener != null) {
				listeners.remove(name);
				bot.eventDispatcher.remove(listener);
			}
		}
	}
}
