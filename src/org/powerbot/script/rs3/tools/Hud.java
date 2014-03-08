package org.powerbot.script.rs3.tools;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;

/**
 * Utilities for manipulating the hud.
 *
 */
public class Hud extends ClientAccessor {
	public static final int WIDGET_HUD = 1477;
	public static final int WIDGET_MENU = 1431;
	public static final int WIDGET_MENU_BOUNDS = 32;
	public static final int WIDGET_MENU_WINDOWS = 1432;
	public static final int COMPONENT_MENU_WINDOWS_LIST = 4;
	private Rectangle[] boundsCache;
	private long cachedTime;

	public Hud(final ClientContext factory) {
		super(factory);
	}

	/**
	 * An enumeration of menu options.
	 *
	 */
	public enum Menu {
		NONE(-1),
		OTHER(-1),
		HERO(18829, Window.SKILLS, Window.ACTIVE_TASK),
		GEAR(18830, Window.BACKPACK, Window.WORN_EQUIPMENT),
		ADVENTURES(18831, Window.ACTIVE_TASK),
		POWERS(18832, Window.PRAYER_ABILITIES, Window.MAGIC_ABILITIES, Window.MELEE_ABILITIES, Window.RANGED_ABILITIES, Window.DEFENCE_ABILITIES),
		SOCIAL(18833, Window.FRIENDS, Window.FRIENDS_CHAT_INFO, Window.CLAN),
		EXTRAS(18836),
		HELP(18838),
		OPTIONS(18835, Window.NOTES, Window.MUSIC_PLAYER);
		private final int texture;
		private final Window[] windows;

		Menu(final int texture, final Window... windows) {
			this.texture = texture;
			this.windows = windows;
		}

		public int getTexture() {
			return texture;
		}

		public Window[] getWindows() {
			return windows;
		}
	}

	/**
	 * An enumeration of known possible windows.
	 *
	 */
	public enum Window {
		ALL_CHAT(Menu.NONE, 18726, 18754, 137, 82),
		PRIVATE_CHAT(Menu.NONE, 18727, 18755, 1467, 55),
		FRIENDS_CHAT(Menu.NONE, 18728, 18756, 1472, 55),
		CLAN_CHAT(Menu.NONE, 18729, 18757, 1471, 55),
		GUEST_CLAN_CHAT(Menu.NONE, 18731, 18790, 1470, 55),
		EMOTES(Menu.NONE, 18741, 18776, 590, 14),
		MINIMAP(Menu.NONE, 18742, 0, 1465, 12),

		SKILLS(Menu.HERO, 18738, 18775, 1466),
		ACTIVE_TASK(Menu.HERO, 18735, 18789, 1220),
		BACKPACK(Menu.GEAR, 18732, 18772, Backpack.WIDGET),
		WORN_EQUIPMENT(Menu.GEAR, 18733, 18773, Equipment.WIDGET),
		PRAYER_ABILITIES(Menu.POWERS, 18734, 18774, Powers.WIDGET_PRAYER, Powers.COMPONENT_PRAYER_CONTAINER),
		MAGIC_ABILITIES(Menu.POWERS, 18724, 18752, 1461),
		MELEE_ABILITIES(Menu.POWERS, 18722, 18750, 1460),
		RANGED_ABILITIES(Menu.POWERS, 18723, 18751, 1452),
		DEFENCE_ABILITIES(Menu.POWERS, 18725, 18753, 1449),
		FRIENDS(Menu.SOCIAL, 18737, 18759, 550, 33),
		FRIENDS_CHAT_INFO(Menu.SOCIAL, 18739, 18761, 1427),
		CLAN(Menu.SOCIAL, 18740, 18762, 1110, 2),
		NOTES(Menu.OPTIONS, 18744, 18779, 1417),
		MUSIC_PLAYER(Menu.OPTIONS, 18745, 18780, 1416),

		MINIGAMES(Menu.OTHER, 18749, 18788, 939),
		FAMILIAR(Menu.OTHER, 18748, 18787, Summoning.WIDGET);
		private final Menu menu;
		private final int miniTexture;
		private final int texture;
		private final int widget;
		private final int component;

		Window(final Menu menu, final int texture, final int miniTexture, final int widget) {
			this(menu, texture, miniTexture, widget, 0);
		}

		Window(final Menu menu, final int texture, final int miniTexture, final int widget, final int component) {
			this.menu = menu;
			this.texture = texture;
			this.miniTexture = miniTexture;
			this.widget = widget;
			this.component = component;
		}

		public Menu getMenu() {
			return menu;
		}

		public int getTexture() {
			return texture;
		}

		public int getMiniTexture() {
			return miniTexture;
		}

		public int getWidget() {
			return widget;
		}

		private int getComponent() {
			return component;
		}
	}

	/**
	 * Returns an array of all the HUD boundaries blocking game interaction.
	 *
	 * @return an array of HUD bounds
	 */
	public Rectangle[] getBounds() {
		if (TimeUnit.MILLISECONDS.convert(System.nanoTime() - cachedTime, TimeUnit.NANOSECONDS) < 1000) {
			if (boundsCache != null) {
				return boundsCache;
			}
		}

		final int[][] indexArr = {{1484, 1}, {1189, 6}, {1184, 1}, {1490, 10}};
		final Rectangle[] arr = new Rectangle[Window.values().length + 2 + indexArr.length];
		int index = 0;
		arr[index++] = ctx.widgets.get(WIDGET_MENU, WIDGET_MENU_BOUNDS).getViewportRect();//TODO: auto detect
		arr[index++] = ctx.widgets.get(CombatBar.WIDGET, CombatBar.COMPONENT_BOUNDS).getViewportRect();
		//subscribe, chat, chat
		for (final int[] pair : indexArr) {
			final Component c = ctx.widgets.get(pair[0], pair[1]);
			if (!c.isVisible()) {
				continue;
			}
			arr[index++] = c.getViewportRect();
		}
		for (final Window window : Window.values()) {
			final Component sprite = getSprite(window);
			if (sprite == null) {
				continue;
			}
			arr[index++] = sprite.getParent().getViewportRect();
		}
		cachedTime = System.nanoTime();
		for (final Rectangle r : arr) {
			if (r == null) {
				break;
			}

			r.grow(5, 5);
		}
		return boundsCache = Arrays.copyOf(arr, index);
	}

	/**
	 * Returns if a {@link Window} is open or not.
	 * Open does not mean visible.
	 *
	 * @param window the {@link Window} to check if open
	 * @return <tt>true</tt> if the window is open; otherwise <tt>false</tt>
	 */
	public boolean isOpen(final Window window) {
		return isVisible(window) || getTab(window) != null;
	}

	/**
	 * Returns if a {@link Window} is visible or not.
	 * A tab must be open for it to be visible.
	 *
	 * @param window the {@link Window} to check if visible
	 * @return <tt>true</tt> if the window is visible; otherwise <tt>false</tt>
	 */
	public boolean isVisible(final Window window) {
		return ctx.widgets.get(window.getWidget(), window.getComponent()).isVisible();
	}

	/**
	 * Opens a menu (even if it's already open).
	 *
	 * @param menu the menu to open.
	 * @return <tt>true</tt> if the menu was opened; otherwise <tt>false</tt>
	 */
	public boolean open(final Menu menu) {
		final Component m = getMenu(menu);
		return m != null && m.click();
	}

	/**
	 * Opens a {@link Window} if not already open.
	 * Does not guarantee the desired {@link Window} will be visible.
	 *
	 * @param window the {@link Window} desired to be opened
	 * @return <tt>true</tt> if the window was opened or is already open; otherwise <tt>false</tt>
	 */
	public boolean open(final Window window) {
		if (isViewable(window) || window.getMenu() == Menu.NONE) {
			return true;
		}
		if (window.getMenu() == Menu.OTHER) {
			return false;
		}
		final Component menu = getMenu(window.getMenu());
		if (menu != null && (getToggle(window) != null || menu.hover())) {
			final Component list = ctx.widgets.get(WIDGET_MENU_WINDOWS, COMPONENT_MENU_WINDOWS_LIST);
			if (list == null) {
				return false;
			}
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return list.isVisible();
				}
			}, 100, 20);
			Random.sleep();
			final Component toggle = getToggle(window);
			if (toggle != null && toggle.hover()) {
				if (toggle.isVisible() && ctx.mouse.click(true)) {
					return Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return isVisible(window);
						}
					}, 100, 20);
				}
			}
		}
		return false;
	}

	/**
	 * Makes a {@link Window} visible by either opening or switching tabs.
	 * Does not require {@link Window} to already be open.
	 *
	 * @param window the {@link Window} desired to be visible
	 * @return <tt>true</tt> if the {@link Window} is visible; otherwise <tt>false</tt>
	 */
	public boolean view(final Window window) {
		if (isVisible(window)) {
			return true;
		}
		if (open(window) && !isVisible(window)) {
			final Component tab = getTab(window);
			if (tab != null && tab.click()) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return isVisible(window);
					}
				}, 100, 20);
			}
		}
		return isVisible(window);
	}

	/**
	 * Closes a {@link Window}.
	 *
	 * @param window the {@link Window} to be closed
	 * @return <tt>true</tt> if the {@link Window} was closed; otherwise <tt>false</tt>
	 */
	public boolean close(final Window window) {
		if (window.getMenu() == Menu.NONE) {
			return false;
		}
		if (!isOpen(window)) {
			return true;
		}
		if (view(window)) {
			final Component sprite = getSprite(window);
			if (sprite != null && sprite.getWidget().getComponent(sprite.getParent().getIndex() + 1).getChild(1).interact("Close")) {
				return Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !isOpen(window);
					}
				}, 125, 20);
			}
		}
		return !isOpen(window);
	}

	public FloatingMessage getFloatingMessage() {
		final Component c = ctx.widgets.get(1177, 0);
		final Component type = c.getChild(0), text = c.getChild(9);
		return new FloatingMessage(text.getText(), type.getTextureId());
	}

	private boolean isViewable(final Window window) {
		if (!isOpen(window)) {
			return false;
		}
		final Component tab = getTab(window);
		return tab != null && tab.getParent().getViewportRect().contains(tab.getViewportRect());
	}

	private Component getToggle(final Window window) {
		final int texture = window.getMiniTexture();
		for (final Component sub : ctx.widgets.get(WIDGET_MENU_WINDOWS, COMPONENT_MENU_WINDOWS_LIST).getChildren()) {
			if (sub.getTextureId() == texture && sub.isVisible()) {
				return sub;
			}
		}
		return null;
	}

	Component getMenu(final Menu menu) {
		final int texture = menu.getTexture();
		for (final Component child : ctx.widgets.get(WIDGET_MENU)) {
			if (child.getTextureId() == texture && child.isValid()) {
				return child;
			}
		}
		return null;
	}

	Component getTab(final Window window) {
		final int texture = window.getMiniTexture();
		for (final Component child : ctx.widgets.get(WIDGET_HUD)) {
			for (final Component sub : child.getChildren()) {
				if (sub.getTextureId() == texture && sub.isValid()) {
					return sub;
				}
			}
		}
		return null;
	}

	Component getSprite(final Window window) {
		final int texture = window.getTexture();
		for (final Component child : ctx.widgets.get(WIDGET_HUD)) {
			for (final Component sub : child.getChildren()) {
				if (sub.getTextureId() == texture && sub.isVisible()) {
					return sub;
				}
			}
		}
		return null;
	}
}
