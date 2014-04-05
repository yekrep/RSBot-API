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
import org.powerbot.bot.rt6.activation.*;
import org.powerbot.script.Bot;

final class RT6BotMenuView implements ActionListener {
	private final Map<String, Class<? extends EventListener>> map;

	private static final String ALL = "All";
	private static final String MOUSE = "Mouse";
	private static final String MOUSETRAILS = "Mouse Trails";
	private static final String BOUNDARIES = "Landscape";
	private static final String PROJECTILES = "Projectiles";
	private static final String PLAYERS = "Players";
	private static final String NPCS = "Npcs";
	private static final String GROUND_ITEMS = "Ground Items";
	private static final String SCENEENTITIES = "Objects";
	private static final String MODELS = "Models";
	private static final String ITEMS = "Items";
	private static final String ABILTIIES = "Abilities";
	private static final String CLIENTSTATE = "Client State";
	private static final String CAMERA = "Camera";
	private static final String MENU = "Menu";
	private static final String PLANE = "Plane";
	private static final String MAPBASE = "Map Base";
	private static final String LOCATION = "Location";
	private static final String DESTINATION = "Destination";
	private static final String MESSAGES = "Messages";
	private static final String MOUSEPOS = "Mouse Position";
	private static final String SEPERATOR = "-";

	private final BotChrome chrome;

	public RT6BotMenuView(final BotChrome chrome, final JMenu menu) {
		this.chrome = chrome;
		final Bot b = chrome.bot.get();

		final JMenuItem widgetExplorer = new JMenuItem(BotLocale.WIDGETEXPLORER);
		widgetExplorer.addActionListener(this);
		menu.add(widgetExplorer);
		final JMenuItem settingExplorer = new JMenuItem(BotLocale.SETTINGEXPLORER);
		settingExplorer.addActionListener(this);
		menu.add(settingExplorer);

			final JMenuItem boundingUtility = new JMenuItem(BotLocale.BOUNDINGUTILITY);
			boundingUtility.addActionListener(this);
			menu.add(boundingUtility);

		menu.addSeparator();

		map = new LinkedHashMap<String, Class<? extends EventListener>>();
		//RS3
		map.put(BOUNDARIES, DrawBoundaries.class);
		map.put(MODELS, DrawModels.class);
		map.put(SCENEENTITIES, DrawObjects.class);
		map.put(PLAYERS, DrawPlayers.class);
		map.put(NPCS, DrawMobs.class);
		map.put(PROJECTILES, DrawProjectiles.class);
		map.put(GROUND_ITEMS, DrawGroundItems.class);
		map.put(CLIENTSTATE, TClientState.class);
		map.put(MENU, TMenu.class);
		map.put(PLANE, TPlane.class);
		map.put(MAPBASE, TMapBase.class);
		map.put(LOCATION, TLocation.class);
		map.put(DESTINATION, TDestination.class);
		map.put(MOUSE, ViewMouse.class);
		map.put(MOUSETRAILS, ViewMouseTrails.class);
		map.put(ITEMS, DrawItems.class);
		map.put(ABILTIIES, DrawAbilities.class);
		map.put(MOUSEPOS, TMousePosition.class);
		map.put(MESSAGES, MessageLogger.class);
		map.put(CAMERA, TCamera.class);

		final List<String> items = new ArrayList<String>(map.size());
		items.add(MOUSE);
		items.add(MOUSETRAILS);
		items.add(PLAYERS);
		items.add(NPCS);
		items.add(GROUND_ITEMS);
		items.add(PROJECTILES);
		items.add(SCENEENTITIES);
		items.add(MODELS);
		items.add(BOUNDARIES);
		items.add(ITEMS);
		items.add(ABILTIIES);
		items.add(SEPERATOR);
		items.add(CLIENTSTATE);
		items.add(CAMERA);
		items.add(MENU);
		items.add(PLANE);
		items.add(MAPBASE);
		items.add(LOCATION);
		items.add(DESTINATION);
		items.add(MOUSEPOS);
		items.add(SEPERATOR);
		items.add(MESSAGES);

		final EventDispatcher d = b.dispatcher;

		boolean selectedAll = true;

		for (final String key : items) {
			if (key.equals(SEPERATOR)) {
				continue;
			}
			if (!d.contains(map.get(key))) {
				selectedAll = false;
				break;
			}
		}

		final JCheckBoxMenuItem all = new JCheckBoxMenuItem(ALL, selectedAll);
		all.addActionListener(this);
		menu.add(all);
		menu.addSeparator();

		for (final String key : items) {
			if (key.equals(SEPERATOR)) {
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
		if (s.equals(BotLocale.WIDGETEXPLORER)) {
			RT6WidgetExplorer.getInstance(chrome).display();
		} else if (s.equals(BotLocale.SETTINGEXPLORER)) {
			BotSettingExplorer.getInstance(chrome).display();
		} else if (s.equals(BotLocale.BOUNDINGUTILITY)) {
			RT6BotBoundingUtility.getInstance(chrome).setVisible(true);
		} else {
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
