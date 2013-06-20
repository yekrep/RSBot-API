package org.powerbot.gui.component;

import org.powerbot.bot.Bot;
import org.powerbot.event.debug.*;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.BotSettingExplorer;
import org.powerbot.gui.BotWidgetExplorer;
import org.powerbot.util.io.Resources;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Paris
 */
public final class BotMenuView implements ActionListener {//TODO revamp debugging options
	private final Map<String, Class<? extends EventListener>> map;
	private static Map<Bot, Map<String, EventListener>> listeners;

	private static final String ALL = "All";
	private static final String MOUSE = "Mouse";
	private static final String PLAYERS = "Players";
	private static final String NPCS = "NPCs";
	private static final String GROUND_ITEMS = "Ground Items";
	private static final String SCENEENTITIES = "Objects";
	private static final String MODELS = "Models";
	private static final String ITEMS = "Items";
	private static final String ABILTIIES = "Abilities";
	private static final String TAB = "Tab";
	private static final String CLIENTSTATE = "Client State";
	private static final String MENU = "Menu";
	private static final String PLANE = "Plane";
	private static final String CAMERA = "Camera";
	private static final String MAPBASE = "Map Base";
	private static final String LOCATION = "Location";
	private static final String DESTINATION = "Destination";
	private static final String MESSAGES = "Messages";
	private static final String MOUSEPOS = "Mouse Position";
	private static final String SEPERATOR = "-";

	public BotMenuView(final JMenu menu) {
		final JMenuItem widgetExplorer = new JMenuItem(BotLocale.WIDGETEXPLORER);
		widgetExplorer.addActionListener(this);
		widgetExplorer.setIcon(new ImageIcon(Resources.Paths.EDIT));
		menu.add(widgetExplorer);
		final JMenuItem settingExplorer = new JMenuItem(BotLocale.SETTINGEXPLORER);
		settingExplorer.addActionListener(this);
		settingExplorer.setIcon(new ImageIcon(Resources.Paths.SETTINGS));
		menu.add(settingExplorer);

		menu.addSeparator();

		if (listeners == null) {
			listeners = new HashMap<>();
		}

		map = new LinkedHashMap<>();
		map.put(MODELS, DrawModels.class);
		map.put(SCENEENTITIES, DrawObjects.class);
		map.put(PLAYERS, DrawPlayers.class);
		map.put(NPCS, DrawNPCs.class);
		map.put(GROUND_ITEMS, DrawGroundItems.class);
		map.put(CLIENTSTATE, TClientState.class);
		map.put(MENU, TMenu.class);
		map.put(PLANE, TPlane.class);
		map.put(CAMERA, TCamera.class);
		map.put(MAPBASE, TMapBase.class);
		map.put(LOCATION, TLocation.class);
		map.put(DESTINATION, TDestination.class);
		map.put(MOUSE, ViewMouse.class);
		map.put(ITEMS, DrawItems.class);
		map.put(ABILTIIES, DrawAbilities.class);
		map.put(TAB, TCurrentTab.class);
		map.put(MOUSEPOS, TMousePosition.class);
		map.put(MESSAGES, MessageLogger.class);

		final List<String> items = new ArrayList<>(map.size());
		items.add(MOUSE);
		items.add(PLAYERS);
		items.add(NPCS);
		items.add(GROUND_ITEMS);
		items.add(SCENEENTITIES);
		items.add(MODELS);
		items.add(ITEMS);
		items.add(ABILTIIES);
		items.add(SEPERATOR);
		items.add(TAB);
		items.add(CLIENTSTATE);
		items.add(MENU);
		items.add(PLANE);
		items.add(CAMERA);
		items.add(MAPBASE);
		items.add(LOCATION);
		items.add(DESTINATION);
		items.add(MOUSEPOS);
		items.add(SEPERATOR);
		items.add(MESSAGES);

		final Bot bot = BotChrome.getInstance().getBot();
		Map<String, EventListener> listeners = BotMenuView.listeners.get(bot);
		if (listeners == null) {
			listeners = new HashMap<>();
			BotMenuView.listeners.put(bot, listeners);
		}

		boolean selectedAll = true;

		for (final String key : items) {
			if (key.equals(SEPERATOR)) {
				continue;
			}
			if (!listeners.containsKey(map.get(key).getName())) {
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
			final Class<? extends EventListener> eventListener = map.get(key);
			final boolean selected = listeners.containsKey(eventListener.getName());
			final JCheckBoxMenuItem item = new JCheckBoxMenuItem(key, selected);
			item.addActionListener(this);
			menu.add(item);
		}
	}

	public void actionPerformed(final ActionEvent e) {
		final String s = e.getActionCommand();
		if (s.equals(BotLocale.WIDGETEXPLORER)) {
			BotWidgetExplorer.display();
		} else if (s.equals(BotLocale.SETTINGEXPLORER)) {
			BotSettingExplorer.display();
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

	private void setView(final Class<? extends EventListener> eventListener, final boolean selected) {
		final Bot bot = BotChrome.getInstance().getBot();
		final String name = eventListener.getName();
		Map<String, EventListener> listeners = BotMenuView.listeners.get(bot);
		if (listeners == null) {
			listeners = new HashMap<>();
			BotMenuView.listeners.put(bot, listeners);
		}
		if (!selected) {
			if (listeners.containsKey(name)) {
				return;
			}
			try {
				EventListener listener;
				try {
					Constructor<?> constructor = eventListener.getConstructor(Bot.class);
					listener = (EventListener) constructor.newInstance(bot);
				} catch (final Exception ignored) {
					listener = eventListener.asSubclass(EventListener.class).newInstance();
				}
				listeners.put(name, listener);
				bot.getEventMulticaster().addListener(listener);
			} catch (final Exception ignored) {
			}
		} else {
			final EventListener listener = listeners.get(name);
			if (listener != null) {
				listeners.remove(name);
				bot.getEventMulticaster().removeListener(listener);
			}
		}
	}
}
