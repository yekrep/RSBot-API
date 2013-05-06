package org.powerbot.script.xenon.widgets;

import org.powerbot.script.xenon.Mouse;
import org.powerbot.script.xenon.Settings;
import org.powerbot.script.xenon.Widgets;
import org.powerbot.script.xenon.util.Delay;
import org.powerbot.script.xenon.wrappers.Action;
import org.powerbot.script.xenon.wrappers.Component;

public class ActionBar {
	public static final int NUM_SLOTS = 12;
	public static final int WIDGET = 640;
	public static final int COMPONENT_BAR = 4;
	public static final int COMPONENT_LOCK = 26, COMPONENT_TRASH = 27;
	public static final int COMPONENT_BUTTON_EXPAND = 3, COMPONENT_BUTTON_COLLAPSE = 30;
	public static final int[] COMPONENT_SLOTS = {34, 38, 41, 44, 47, 50, 53, 56, 59, 62, 65, 68};
	public static final int[] COMPONENT_SLOTS_ACTION = {32, 72, 76, 80, 84, 88, 92, 96, 100, 104, 108, 112};
	public static final int[] COMPONENT_SLOTS_BIND = {70, 75, 79, 83, 87, 91, 95, 99, 103, 107, 111, 115};
	public static final int[] COMPONENT_SLOTS_COOLDOWN = {36, 73, 77, 81, 85, 89, 93, 97, 101, 105, 109, 113};
	public static final int SETTING_ITEM = 811, SETTING_ABILITY = 727;

	public static State getState() {
		final Component c = Widgets.get(WIDGET, COMPONENT_BAR);
		if (c == null || !c.isValid()) return State.UNAVAILABLE;
		return c.isVisible() ? State.EXPANDED : State.COLLAPSED;
	}

	public static boolean setState(final State state) {
		if (getState() == state) return true;
		final Component c;
		switch (state) {
		case EXPANDED:
			c = Widgets.get(WIDGET, COMPONENT_BUTTON_EXPAND);
			break;
		case COLLAPSED:
			c = Widgets.get(WIDGET, COMPONENT_BUTTON_COLLAPSE);
			break;
		default:
			c = null;
			break;
		}
		if (c != null && c.isValid() && c.interact(state.action)) {
			for (int i = 0; i < 5 && getState() != state; i++) Delay.sleep(20, 50);
		}
		return getState() == state;
	}

	public static Action getAction(final int... ids) {
		final Action[] actions = getActions();
		for (final Action action : actions) for (final int id : ids) if (action.getId() == id) return action;
		return null;
	}

	public static Action getActionAt(final int slot) {
		if (slot < 0 || slot >= NUM_SLOTS) return null;
		Action.Type type;
		int id = Settings.get(SETTING_ABILITY + slot);
		if (id != 0) type = Action.Type.ABILITY;
		else if ((id = Settings.get(SETTING_ITEM + slot)) != 0) type = Action.Type.ITEM;
		else type = null;
		if (type == null) return null;
		return new Action(slot, type, id);
	}

	public static Action[] getActions() {
		final Action[] actions = new Action[NUM_SLOTS];
		for (int i = 0; i < NUM_SLOTS; i++) actions[i] = getActionAt(i);
		return actions;
	}

	public static boolean deleteSlot(final int slot) {
		final Component c;
		if (slot < 0 || slot >= NUM_SLOTS || (c = Widgets.get(WIDGET, COMPONENT_SLOTS[slot])) == null) return false;
		final Action action = getActionAt(slot);
		if (action == null) return true;
		if (action.hover() && Mouse.drag(c.getInteractPoint(), true)) {
			for (int i = 0; i < 5 && getActionAt(slot) != null; i++) Delay.sleep(100, 200);
		}
		return getActionAt(slot) == null;
	}

	public static boolean isLocked() {
		return (Settings.get(682) & 0x10) != 0;
	}

	public static boolean setLocked(final boolean locked) {
		if (isLocked() == locked) return true;
		final Component c = Widgets.get(WIDGET, COMPONENT_LOCK);
		if (c != null && c.isValid() && c.interact("Toggle Lock")) {
			for (int i = 0; i < 25 && locked != isLocked(); i++) Delay.sleep(100, 150);
		}
		return isLocked() == locked;
	}

	public static enum State {
		EXPANDED("Expand"), COLLAPSED("Minimise"), UNAVAILABLE(null);
		private final String action;

		State(final String action) {
			this.action = action;
		}
	}
}
