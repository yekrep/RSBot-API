package org.powerbot.script.methods;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.powerbot.script.wrappers.Component;

import static org.powerbot.script.util.Constants.getInt;

/**
 * @author Timer
 */
public class Hud extends MethodProvider {
	public static final int WIDGET_HUD = getInt("hud.widget.hud");
	public static final int WIDGET_MENU = getInt("hud.widget.menu");
	public static final int WIDGET_MENU_BOUNDS = getInt("hud.widget.menu.bounds");
	public static final int WIDGET_MENU_WINDOWS = getInt("hud.widget.menu.windows");
	public static final int COMPONENT_MENU_WINDOWS_LIST = getInt("hud.component.menu.windows.list");
	private Rectangle[] boundsCache;
	private long cachedTime;

	public Hud(MethodContext factory) {
		super(factory);
	}

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

		Menu(int texture, Window... windows) {
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

	public enum Window {
		ALL_CHAT(Menu.NONE, 18726, 18754, 137, 82),
		PRIVATE_CHAT(Menu.NONE, 18727, 18755, 1467, 55),
		FRIENDS_CHAT(Menu.NONE, 18728, 18756, 1472, 55),
		CLAN_CHAT(Menu.NONE, 18792, 18757, 1471, 55),
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

		FAMILIAR(Menu.OTHER, 18748, 18787, Summoning.WIDGET);
		private final Menu menu;
		private final int miniTexture;
		private final int texture;
		private final int widget;
		private final int component;

		Window(Menu menu, int texture, int miniTexture, int widget) {
			this(menu, texture, miniTexture, widget, 0);
		}

		Window(Menu menu, int texture, int miniTexture, int widget, int component) {
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
		Rectangle[] arr = new Rectangle[Window.values().length + 2];
		int index = 0;
		arr[index++] = ctx.widgets.get(WIDGET_MENU, WIDGET_MENU_BOUNDS).getViewportRect();//TODO: auto detect
		arr[index++] = ctx.widgets.get(CombatBar.WIDGET, CombatBar.COMPONENT_BOUNDS).getViewportRect();
		for (Window window : Window.values()) {
			Component sprite = getSprite(window);
			if (sprite == null) {
				continue;
			}
			arr[index++] = sprite.getParent().getViewportRect();
		}
		cachedTime = System.nanoTime();
		return boundsCache = Arrays.copyOf(arr, index);
	}

	/**
	 * Returns if a {@link Window} is open or not.
	 * Open does not mean visible.
	 *
	 * @param window the {@link Window} to check if open
	 * @return <tt>true</tt> if the window is open; otherwise <tt>false</tt>
	 */
	public boolean isOpen(Window window) {
		return isVisible(window) || getTab(window) != null;
	}

	/**
	 * Returns if a {@link Window} is visible or not.
	 * A tab must be open for it to be visible.
	 *
	 * @param window the {@link Window} to check if visible
	 * @return <tt>true</tt> if the window is visible; otherwise <tt>false</tt>
	 */
	public boolean isVisible(Window window) {
		return ctx.widgets.get(window.getWidget(), window.getComponent()).isVisible();
	}

	/**
	 * Opens a {@link Window} if not already open.
	 * Does not guarantee the desired {@link Window} will be visible.
	 *
	 * @param window the {@link Window} desired to be opened
	 * @return <tt>true</tt> if the window was opened or is already open; otherwise <tt>false</tt>
	 */
	public boolean open(Window window) {
		if (isViewable(window) || window.getMenu() == Menu.NONE) {
			return true;
		}
		if (window.getMenu() == Menu.OTHER) {
			return false;
		}
		Component menu = getMenu(window.getMenu());
		if (menu != null && (getToggle(window) != null || menu.hover())) {
			Component list = ctx.widgets.get(WIDGET_MENU_WINDOWS, COMPONENT_MENU_WINDOWS_LIST);
			for (int i = 0; i < 20; i++) {
				if (list.isVisible()) {
					break;
				}
				sleep(50, 150);
			}
			sleep(300, 700);
			Component toggle = getToggle(window);
			if (toggle != null && toggle.hover()) {
				if (toggle.isVisible() && ctx.mouse.click(true)) {
					for (int i = 0; i < 20; i++) {
						if (isVisible(window)) {
							break;
						}
						sleep(50, 150);
					}
					return isViewable(window);
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
	public boolean view(Window window) {
		if (isVisible(window)) {
			return true;
		}
		if (open(window) && !isVisible(window)) {
			Component tab = getTab(window);
			if (tab != null && tab.click()) {
				for (int i = 0; i < 20; i++) {
					if (isVisible(window)) {
						break;
					}
					sleep(50, 150);
				}
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
	public boolean close(Window window) {
		if (window.getMenu() == Menu.NONE) {
			return false;
		}
		if (!isOpen(window)) {
			return true;
		}
		if (view(window)) {
			Component sprite = getSprite(window);
			if (sprite != null && sprite.getWidget().getComponent(sprite.getParent().getIndex() + 1).getChild(1).interact("Close")) {
				for (int i = 0; i < 20; i++) {
					if (!isOpen(window)) {
						break;
					}
					sleep(100, 150);
				}
			}
		}
		return !isOpen(window);
	}

	private boolean isViewable(Window window) {
		if (!isOpen(window)) {
			return false;
		}
		Component tab = getTab(window);
		return tab != null && tab.getParent().getViewportRect().contains(tab.getViewportRect());
	}

	private Component getToggle(Window window) {
		int texture = window.getMiniTexture();
		for (Component sub : ctx.widgets.get(WIDGET_MENU_WINDOWS, COMPONENT_MENU_WINDOWS_LIST).getChildren()) {
			if (sub.getTextureId() == texture && sub.isVisible()) {
				return sub;
			}
		}
		return null;
	}

	private Component getMenu(Menu menu) {
		int texture = menu.getTexture();
		for (Component child : ctx.widgets.get(WIDGET_MENU)) {
			if (child.getTextureId() == texture && child.isValid()) {
				return child;
			}
		}
		return null;
	}

	private Component getTab(Window window) {
		int texture = window.getMiniTexture();
		for (Component child : ctx.widgets.get(WIDGET_HUD)) {
			for (Component sub : child.getChildren()) {
				if (sub.getTextureId() == texture && sub.isValid()) {
					return sub;
				}
			}
		}
		return null;
	}

	private Component getSprite(Window window) {
		int texture = window.getTexture();
		for (Component child : ctx.widgets.get(WIDGET_HUD)) {
			for (Component sub : child.getChildren()) {
				if (sub.getTextureId() == texture && sub.isVisible()) {
					return sub;
				}
			}
		}
		return null;
	}
}
