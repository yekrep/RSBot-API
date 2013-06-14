package org.powerbot.script.methods;

import org.powerbot.script.lang.IdQuery;
import org.powerbot.script.util.Delay;
import org.powerbot.script.wrappers.Action;
import org.powerbot.script.wrappers.Component;

import java.util.ArrayList;
import java.util.List;

public class ActionBar extends IdQuery<Action> {
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

	public ActionBar(ClientFactory factory) {
		super(factory);
	}

	public boolean isExpanded() {
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_BAR);
		if (c == null || !c.isValid()) {
			return false;
		}
		return c.isVisible();
	}

	public boolean setExpanded(final boolean expanded) {
		if (isExpanded() == expanded) {
			return true;
		}
		final Component c = ctx.widgets.get(WIDGET, expanded ? COMPONENT_BUTTON_EXPAND : COMPONENT_BUTTON_COLLAPSE);
		if (c != null && c.isValid() && c.interact(expanded ? "Expand" : "Collapse")) {
			for (int i = 0; i < 5 && isExpanded() != expanded; i++) {
				Delay.sleep(20, 50);
			}
		}
		return isExpanded() == expanded;
	}

	public Action getActionAt(final int slot) {
		if (slot < 0 || slot >= NUM_SLOTS) {
			return null;
		}
		Action.Type type;
		int id = ctx.settings.get(SETTING_ABILITY + slot);
		if (id != 0) {
			type = Action.Type.ABILITY;
		} else if ((id = ctx.settings.get(SETTING_ITEM + slot)) != 0) {
			type = Action.Type.ITEM;
		} else {
			type = null;
		}
		if (type == null) {
			return null;
		}
		return new Action(ctx, slot, type, id);
	}

	public Action[] getActions() {
		final Action[] actions = new Action[NUM_SLOTS];
		for (int i = 0; i < NUM_SLOTS; i++) {
			actions[i] = getActionAt(i);
		}
		return actions;
	}

	@Override
	protected List<Action> list() {
		List<Action> actions = new ArrayList<>(NUM_SLOTS);
		Action[] arr = getActions();
		for (Action a : arr) {
			if (a == null) continue;
			actions.add(a);
		}
		return actions;
	}

	public boolean deleteSlot(final int slot) {
		Component c;
		if (slot < 0 || slot >= NUM_SLOTS || (c = ctx.widgets.get(WIDGET, COMPONENT_SLOTS[slot])) == null) {
			return false;
		}
		final Action action = getActionAt(slot);
		if (action == null) {
			return true;
		}
		c = ctx.widgets.get(WIDGET, COMPONENT_TRASH);
		if (c == null || !c.isValid()) {
			return false;
		}
		if (action.hover() && ctx.mouse.drag(c.getInteractPoint(), true)) {
			for (int i = 0; i < 5 && getActionAt(slot) != null; i++) {
				Delay.sleep(100, 200);
			}
		}
		return getActionAt(slot) == null;
	}

	public boolean isLocked() {
		return ((ctx.settings.get(682) >> 4) & 0x1) != 0;
	}

	public boolean setLocked(final boolean locked) {
		if (isLocked() == locked) {
			return true;
		}
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_LOCK);
		if (c != null && c.isValid() && c.interact("Toggle Lock")) {
			for (int i = 0; i < 25 && locked != isLocked(); i++) {
				Delay.sleep(100, 150);
			}
		}
		return isLocked() == locked;
	}
}
