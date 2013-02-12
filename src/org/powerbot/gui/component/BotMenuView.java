package org.powerbot.gui.component;

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

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.powerbot.core.Bot;
import org.powerbot.core.event.impl.DrawBoundaries;
import org.powerbot.core.event.impl.DrawGroundItems;
import org.powerbot.core.event.impl.DrawInventory;
import org.powerbot.core.event.impl.DrawModels;
import org.powerbot.core.event.impl.DrawMouse;
import org.powerbot.core.event.impl.DrawNPCs;
import org.powerbot.core.event.impl.DrawPlayers;
import org.powerbot.core.event.impl.DrawScene;
import org.powerbot.core.event.impl.MessageLogger;
import org.powerbot.core.event.impl.TCamera;
import org.powerbot.core.event.impl.TClientState;
import org.powerbot.core.event.impl.TDestination;
import org.powerbot.core.event.impl.TLocation;
import org.powerbot.core.event.impl.TMapBase;
import org.powerbot.core.event.impl.TMenu;
import org.powerbot.core.event.impl.TMousePosition;
import org.powerbot.core.event.impl.TPlane;
import org.powerbot.gui.BotSettingExplorer;
import org.powerbot.gui.BotWidgetExplorer;

/**
 * @author Paris
 */
public final class BotMenuView extends JMenu implements ActionListener {//TODO revamp debugging options
	private static final long serialVersionUID = 1L;
	private final Map<String, Class<? extends EventListener>> map;
	private static Map<Bot, Map<String, EventListener>> listeners;

	private static final String ALL = "All";
	private static final String MOUSE = "Mouse";
	private static final String PLAYERS = "Players";
	private static final String NPCS = "NPCs";
	private static final String GROUND_ITEMS = "Ground Items";
	private static final String SCENEENTITIES = "Scene Entities";
	private static final String MODELS = "Models";
	private static final String BOUNDARIES = "Boundaries";
	private static final String INVENTORY = "Inventory";
	private static final String CLIENTSTATE = "Client State";
	private static final String MOUSEPOSITION = "Mouse Position";
	private static final String MENU = "Menu";
	private static final String PLANE = "Plane";
	private static final String CAMERA = "Camera";
	private static final String MAPBASE = "Map Base";
	private static final String LOCATION = "Location";
	private static final String DESTINATION = "Destination";
	private static final String MESSAGES = "Messages";
	private static final String SEPERATOR = "-";

	public BotMenuView(final BotMenu parent) {
		super(BotLocale.VIEW);

		if (!Bot.instantiated()) {
			setEnabled(false);
			map = null;
			return;
		}

		if (listeners == null) {
			listeners = new HashMap<>();
		}

		map = new LinkedHashMap<>();
		map.put(BOUNDARIES, DrawBoundaries.class);
		map.put(MODELS, DrawModels.class);
		map.put(SCENEENTITIES, DrawScene.class);
		map.put(PLAYERS, DrawPlayers.class);
		map.put(NPCS, DrawNPCs.class);
		map.put(GROUND_ITEMS, DrawGroundItems.class);
		map.put(CLIENTSTATE, TClientState.class);
		map.put(MOUSEPOSITION, TMousePosition.class);
		map.put(MENU, TMenu.class);
		map.put(PLANE, TPlane.class);
		map.put(CAMERA, TCamera.class);
		map.put(MAPBASE, TMapBase.class);
		map.put(LOCATION, TLocation.class);
		map.put(DESTINATION, TDestination.class);
		map.put(MOUSE, DrawMouse.class);
		map.put(INVENTORY, DrawInventory.class);
		map.put(MESSAGES, MessageLogger.class);

		final List<String> items = new ArrayList<>(map.size());
		items.add(MOUSE);
		items.add(PLAYERS);
		items.add(NPCS);
		items.add(GROUND_ITEMS);
		items.add(SCENEENTITIES);
		items.add(MODELS);
		items.add(BOUNDARIES);
		items.add(INVENTORY);
		items.add(SEPERATOR);
		items.add(CLIENTSTATE);
		items.add(MOUSEPOSITION);
		items.add(MENU);
		items.add(PLANE);
		items.add(CAMERA);
		items.add(MAPBASE);
		items.add(LOCATION);
		items.add(DESTINATION);
		items.add(SEPERATOR);
		items.add(MESSAGES);

		final Bot bot = Bot.instance();
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

		final JMenuItem widgetExplorer = new JMenuItem(BotLocale.WIDGETEXPLORER);
		widgetExplorer.addActionListener(this);
		add(widgetExplorer);
		final JMenuItem settingExplorer = new JMenuItem(BotLocale.SETTINGEXPLORER);
		settingExplorer.addActionListener(this);
		add(settingExplorer);
		addSeparator();

		final JCheckBoxMenuItem all = new JCheckBoxMenuItem(ALL, selectedAll);
		all.addActionListener(this);
		add(all);
		addSeparator();

		for (final String key : items) {
			if (key.equals(SEPERATOR)) {
				addSeparator();
				continue;
			}
			final Class<? extends EventListener> eventListener = map.get(key);
			final boolean selected = listeners.containsKey(eventListener.getName());
			final JCheckBoxMenuItem item = new JCheckBoxMenuItem(key, selected);
			item.addActionListener(this);
			add(item);
		}
	}

	public void actionPerformed(final ActionEvent e) {
		final String s = e.getActionCommand();
		if (s.equals(BotLocale.WIDGETEXPLORER)) {
			BotWidgetExplorer.display(Bot.context());
		} else if (s.equals(BotLocale.SETTINGEXPLORER)) {
			BotSettingExplorer.display(Bot.context());
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
		final Bot bot = Bot.instance();
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
				bot.getEventManager().addListener(listener);
			} catch (final Exception ignored) {
			}
		} else {
			final EventListener listener = listeners.get(name);
			if (listener != null) {
				listeners.remove(name);
				bot.getEventManager().removeListener(listener);
			}
		}
	}
}
