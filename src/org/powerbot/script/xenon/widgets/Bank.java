package org.powerbot.script.xenon.widgets;

import org.powerbot.script.xenon.Settings;
import org.powerbot.script.xenon.Widgets;
import org.powerbot.script.xenon.wrappers.Component;
import org.powerbot.script.xenon.wrappers.Item;
import org.powerbot.script.xenon.wrappers.Widget;

public class Bank {
	public static final int WIDGET = 762;

	public static boolean isOpen() {
		final Widget widget = getWidget();
		return widget != null && widget.isValid();
	}

	private static Widget getWidget() {
		return Widgets.get(WIDGET);
	}

	public static Tab getCurrentTab() {
		return isOpen() ? Tab.getTab((Settings.get(Settings.BANK_TAB) >>> 0x1B) ^ 0x10) : Tab.NONE;
	}

	public static boolean setCurrentTab(final Tab tab) {
		return !(!isOpen() || tab == Tab.NONE || tab == Tab.SEARCH || tab.index > getTabCount()) &&
				(getCurrentTab() == tab || tab.open());
	}

	public static int getTabCount() {
		if (!isOpen()) {
			return -1;
		}
		int count = 1;
		for (final Tab tab : Tab.values()) {
			final Component child = tab.getComponent();
			if (child != null && child.getItemId() != -1 && tab != Tab.SEARCH) {
				count++;
			}
		}
		return count;
	}

	public static enum Tab {
		NONE(-1), SEARCH(0), ALL(1), SECOND(2), THIRD(3), FOURTH(4),
		FIFTH(5), SIXTH(6), SEVENTH(7), EIGHTH(8), NINTH(9);
		private final int index;

		private Tab(final int index) {
			this.index = index;
		}

		public static Tab getTab(final int index) {
			for (final Tab tab : Tab.values()) {
				if (tab.index == index) {
					return tab;
				}
			}
			return NONE;
		}

		public Component getComponent() {
			if (this != NONE && this != SEARCH && Bank.isOpen()) {
				return Widgets.get(WIDGET, 67 - (index * 2));
			}
			return null;
		}

		private boolean open() {
			final Component tab = getComponent();
			return tab != null && tab.click(true);
		}

		public Item getSymbolItem() {
			final Component tab = getComponent();
			if (tab != null && tab.getItemId() != -1) {
				return new Item(tab);
			}
			return null;
		}
	}
}
