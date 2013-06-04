package org.powerbot.script.methods.tabs;

import org.powerbot.script.methods.Widgets;
import org.powerbot.script.methods.widgets.Bank;
import org.powerbot.script.wrappers.Widget;

public class Equipment {
	public static final int WIDGET = 387;
	public static final int WIDGET_BANK = 667;
	public static final int COMPONENT_BANK = 7;
	public static final int NUM_SLOTS = 13;
	public static final int NUM_APPEARANCE_SLOTS = 9;

	public static enum Slot {
		HEAD(7, 0, 0, -1),
		CAPE(10, 1, 1, -1),
		NECK(13, 2, 2, -1),
		MAIN_HAND(16, 3, 3, 15),
		TORSO(19, 4, 4, -1),
		OFF_HAND(22, 5, 5, 16),
		LEGS(25, 7, 7, -1),
		HANDS(28, 9, 9, -1),
		FEET(31, 10, 10, -1),
		RING(34, 12, -1, -1),
		QUIVER(39, 13, -1, -1),
		AURA(48, 14, 14, -1),
		POCKET(70, 15, -1, -1);
		private final int component;
		private final int bank;
		private final int appearance;
		private final int sheathed;

		Slot(final int component, final int bank, final int appearance, final int sheathed) {
			this.component = component;
			this.bank = bank;
			this.appearance = appearance;
			this.sheathed = sheathed;
		}

		public int getComponentIndex() {
			return component;
		}

		public int getBankComponentIndex() {
			return bank;
		}

		public int getAppearanceIndex() {
			return appearance;
		}

		public int getSheathedIndex() {
			return sheathed;
		}
	}

	private static Widget getWidget() {
		if (Bank.isOpen()) return Widgets.get(WIDGET_BANK);
		return Widgets.get(WIDGET);
	}
}
