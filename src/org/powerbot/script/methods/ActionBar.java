package org.powerbot.script.methods;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.lang.IdQuery;
import org.powerbot.script.util.Delay;
import org.powerbot.script.wrappers.Action;
import org.powerbot.script.wrappers.Component;

public class ActionBar extends IdQuery<Action> {
	public static final int NUM_SLOTS = 12;
	public static final int WIDGET = 1430;
	public static final int COMPONENT_BAR = 76;
	public static final int COMPONENT_LOCK = 19, COMPONENT_TRASH = 20;
	public static final int WIDGET_LAYOUT = 1477;
	public static final int COMPONENT_BUTTON_TOGGLE = 70, COMPONENT_BUTTON_TOGGLE_IDX = 1;
	public static final int SETTING_ITEM = 811, SETTING_ABILITY = 727;

	public static final int COMPONENT_SLOT_ACTION_START = 96;
	public static final int COMPONENT_SLOT_COOLDOWN_START = 97;
	public static final int COMPONENT_SLOT_BIND_START = 99;

	public static final int TEXTURE_COOL_DOWN = 14590;

	public ActionBar(MethodContext factory) {
		super(factory);
		//TODO: update this class
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
		final Component c = ctx.widgets.get(WIDGET_LAYOUT, COMPONENT_BUTTON_TOGGLE).getChild(COMPONENT_BUTTON_TOGGLE_IDX);
		if (c.isValid() && c.interact(expanded ? "Maximise" : "Minimise")) {
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
		if (id > 0) {
			type = Action.Type.ABILITY;
		} else if ((id = ctx.settings.get(SETTING_ITEM + slot)) > 0) {
			type = Action.Type.ITEM;
		} else {
			type = Action.Type.UNKNOWN;
			id = -1;
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
	protected List<Action> get() {
		List<Action> actions = new ArrayList<>(NUM_SLOTS);
		Action[] arr = getActions();
		for (Action a : arr) {
			if (a == null) {
				continue;
			}
			actions.add(a);
		}
		return actions;
	}

	public boolean deleteSlot(final int slot) {
		Component c;
		if (slot < 0 || slot >= NUM_SLOTS || (c = ctx.widgets.get(WIDGET, COMPONENT_SLOT_ACTION_START + slot * 4)) == null) {
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
		if (action.getComponent().hover() && ctx.mouse.drag(c.getInteractPoint(), true)) {
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

	@Override
	public Action getNil() {
		return new Action(ctx, 0, Action.Type.UNKNOWN, -1);
	}
}
