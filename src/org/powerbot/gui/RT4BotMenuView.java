package org.powerbot.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.rt4.DrawGroundItems;
import org.powerbot.bot.rt4.DrawItems;
import org.powerbot.bot.rt4.DrawMobs;
import org.powerbot.bot.rt4.DrawObjects;
import org.powerbot.bot.rt4.DrawPlayers;
import org.powerbot.bot.rt4.MessageLogger;
import org.powerbot.bot.rt4.TCamera;
import org.powerbot.bot.rt4.TClientState;
import org.powerbot.bot.rt4.TDestination;
import org.powerbot.bot.rt4.TFloor;
import org.powerbot.bot.rt4.TLocation;
import org.powerbot.bot.rt4.TMapBase;
import org.powerbot.bot.rt4.TMenu;
import org.powerbot.bot.rt4.TMousePosition;
import org.powerbot.bot.rt4.TPlayer;
import org.powerbot.bot.rt4.ViewMouse;
import org.powerbot.bot.rt4.ViewMouseTrails;
import org.powerbot.script.Bot;

final class RT4BotMenuView implements ActionListener {
	private final Map<String, Class<? extends EventListener>> map;

	private final BotChrome chrome;

	public RT4BotMenuView(final BotChrome chrome, final JMenu menu) {
		this.chrome = chrome;
		final Bot b = chrome.bot.get();

		final JMenuItem widgetExplorer = new JMenuItem(BotLocale.UTIL_WIDGET);
		widgetExplorer.addActionListener(this);
		menu.add(widgetExplorer);
		final JMenuItem settingExplorer = new JMenuItem(BotLocale.UTIL_VARPBITS);
		settingExplorer.addActionListener(this);
		menu.add(settingExplorer);

		final JMenuItem boundingUtility = new JMenuItem(BotLocale.UTIL_MODELING);
		boundingUtility.addActionListener(this);
		menu.add(boundingUtility);

		menu.addSeparator();

		map = new LinkedHashMap<String, Class<? extends EventListener>>();
		map.put(BotLocale.VIEW_SCENE_ENTITIES, DrawObjects.class);
		map.put(BotLocale.VIEW_PLAYERS, DrawPlayers.class);
		map.put(BotLocale.VIEW_PLAYER, TPlayer.class);
		map.put(BotLocale.VIEW_NPCS, DrawMobs.class);
		map.put(BotLocale.VIEW_ITEMS, DrawItems.class);
		map.put(BotLocale.VIEW_GROUND_ITEMS, DrawGroundItems.class);
		map.put(BotLocale.VIEW_CLIENT_STATE, TClientState.class);
		map.put(BotLocale.VIEW_CAMERA, TCamera.class);
		map.put(BotLocale.VIEW_MENU, TMenu.class);
		map.put(BotLocale.VIEW_FLOOR, TFloor.class);
		map.put(BotLocale.VIEW_MAP_BASE, TMapBase.class);
		map.put(BotLocale.VIEW_LOCATION, TLocation.class);
		map.put(BotLocale.VIEW_DESTINATION, TDestination.class);
		map.put(BotLocale.VIEW_MOUSE, ViewMouse.class);
		map.put(BotLocale.VIEW_MOUSE_TRAILS, ViewMouseTrails.class);
		map.put(BotLocale.VIEW_MOUSE_POSITION, TMousePosition.class);
		map.put(BotLocale.VIEW_MESSAGES, MessageLogger.class);

		final List<String> items = new ArrayList<String>(map.size());
		items.add(BotLocale.VIEW_MOUSE);
		items.add(BotLocale.VIEW_MOUSE_TRAILS);
		items.add(BotLocale.VIEW_PLAYERS);
		items.add(BotLocale.VIEW_PLAYER);
		items.add(BotLocale.VIEW_NPCS);
		items.add(BotLocale.VIEW_ITEMS);
		items.add(BotLocale.VIEW_GROUND_ITEMS);
		items.add(BotLocale.VIEW_SCENE_ENTITIES);
		items.add(BotLocale.SEPARATOR);
		items.add(BotLocale.VIEW_CLIENT_STATE);
		items.add(BotLocale.VIEW_CAMERA);
		items.add(BotLocale.VIEW_MENU);
		items.add(BotLocale.VIEW_FLOOR);
		items.add(BotLocale.VIEW_MAP_BASE);
		items.add(BotLocale.VIEW_LOCATION);
		items.add(BotLocale.VIEW_DESTINATION);
		items.add(BotLocale.VIEW_MOUSE_POSITION);
		items.add(BotLocale.SEPARATOR);
		items.add(BotLocale.VIEW_MESSAGES);

		final EventDispatcher d = b.dispatcher;

		boolean selectedAll = true;

		for (final String key : items) {
			if (key.equals(BotLocale.SEPARATOR)) {
				continue;
			}
			if (!d.contains(map.get(key))) {
				selectedAll = false;
				break;
			}
		}

		final JCheckBoxMenuItem all = new JCheckBoxMenuItem(BotLocale.VIEW_ALL, selectedAll);
		all.addActionListener(this);
		menu.add(all);
		menu.addSeparator();

		for (final String key : items) {
			if (key.equals(BotLocale.SEPARATOR)) {
				menu.addSeparator();
				continue;
			}
			final JCheckBoxMenuItem item = new JCheckBoxMenuItem(key, d.contains(map.get(key)));
			item.addActionListener(this);
			menu.add(item);
		}
	}

	public void actionPerformed(final ActionEvent e) {
		final String s = e.getActionCommand();
		if (s.equals(BotLocale.UTIL_WIDGET)) {
			RT4WidgetExplorer.getInstance(chrome).display();
		} else if (s.equals(BotLocale.UTIL_VARPBITS)) {
			BotSettingExplorer.getInstance(chrome).display();
		} else if (s.equals(BotLocale.UTIL_MODELING)) {
			RT4BotBoundingUtility.getInstance(chrome).setVisible(true);
		} else {
			final JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
			item.setSelected(!item.isSelected());
			if (item.getText().equals(BotLocale.VIEW_ALL)) {
				for (final Entry<String, Class<? extends EventListener>> entry : map.entrySet()) {
					setView(entry.getValue(), item.isSelected());
				}
			} else {
				setView(map.get(item.getText()), item.isSelected());
			}
		}
	}

	private void setView(final Class<? extends EventListener> e, final boolean s) {
		final Bot b = chrome.bot.get();

		if (b == null) {
			return;
		}

		final EventDispatcher d = b.dispatcher;
		final boolean c = d.contains(e);

		if (!s && !c) {
			EventListener l = null;

			try {
				l = e.getConstructor(b.ctx.getClass()).newInstance(b.ctx);
			} catch (final Exception ignored) {
			}

			if (l != null) {
				d.add(l);
			}
		} else if (s && c) {
			for (final EventListener l : d) {
				if (l.getClass().isAssignableFrom(e)) {
					d.remove(l);
				}
			}
		}
	}
}
