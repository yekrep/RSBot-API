package org.powerbot.script.methods;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.lang.IdQuery;
import org.powerbot.script.wrappers.Action;
import org.powerbot.script.wrappers.Component;

public class CombatBar extends IdQuery<Action> {
	public static final int WIDGET = 1430;
	public static final int SETTING_ADRENALINE = 679;
	public static final int COMPONENT_BUTTON_HEAL = 2;
	public static final int SETTING_RETALIATION = 462;
	public static final int COMPONENT_BUTTON_RETALIATE = 6;
	public static final int COMPONENT_BUTTON_PRAYER = 4;
	public static final int COMPONENT_BUTTON_SUMMONING = 5;
	public static final int COMPONENT_HEALTH = 82;
	public static final int COMPONENT_ADRENALINE = 92;
	public static final int COMPONENT_PRAYER = 88;
	public static final int COMPONENT_SUMMONING = 94;
	public static final int COMPONENT_TEXT = 7;

	public static final int NUM_SLOTS = 12;
	public static final int COMPONENT_BAR = 76;
	public static final int COMPONENT_LOCK = 19, COMPONENT_TRASH = 20;
	public static final int WIDGET_LAYOUT = 1477;
	public static final int COMPONENT_BUTTON_TOGGLE = 73, COMPONENT_BUTTON_TOGGLE_IDX = 1;
	public static final int SETTING_ITEM = 811, SETTING_ABILITY = 727;
	public static final int COMPONENT_SLOT_ACTION = 96;
	public static final int COMPONENT_SLOT_COOL_DOWN = 97;
	public static final int COMPONENT_SLOT_BIND = 99;

	public CombatBar(MethodContext factory) {
		super(factory);
	}

	public boolean healPoison() {
		return ctx.widgets.get(WIDGET, COMPONENT_BUTTON_HEAL).interact("Heal");
	}

	public boolean setRealiating(boolean retaliate) {
		if (retaliate != isRetaliating() &&
				ctx.widgets.get(WIDGET, COMPONENT_BUTTON_RETALIATE).interact("Toggle")) {
			for (int i = 0; i < 10 && retaliate != isRetaliating(); i++) {
				sleep(50, 150);
			}
		}
		return retaliate == isRetaliating();
	}

	public boolean isRetaliating() {
		return ctx.settings.get(SETTING_RETALIATION) == 0;
	}

	public int getHealth() {
		String text = ctx.widgets.get(WIDGET, COMPONENT_HEALTH).getChild(COMPONENT_TEXT).getText();
		int index = text.indexOf('/');
		if (index != -1) {
			try {
				return Integer.parseInt(text.substring(0, index));
			} catch (NumberFormatException ignored) {
			}
		}
		return -1;
	}

	public int getMaximumHealth() {
		String text = ctx.widgets.get(WIDGET, COMPONENT_HEALTH).getChild(COMPONENT_TEXT).getText();
		int index = text.indexOf('/');
		if (index != -1) {
			try {
				return Integer.parseInt(text.substring(index + 1));
			} catch (NumberFormatException ignored) {
			}
		}
		return -1;
	}

	public int getAdrenaline() {
		return ctx.settings.get(SETTING_ADRENALINE);
	}

	public boolean isExpanded() {
		return ctx.widgets.get(WIDGET, COMPONENT_BAR).isVisible();
	}

	public boolean setExpanded(final boolean expanded) {
		if (isExpanded() == expanded) {
			return true;
		}
		final Component c = ctx.widgets.get(WIDGET_LAYOUT, COMPONENT_BUTTON_TOGGLE).getChild(COMPONENT_BUTTON_TOGGLE_IDX);
		if (c.isValid() && c.interact(expanded ? "Maximise" : "Minimise")) {
			for (int i = 0; i < 5 && isExpanded() != expanded; i++) {
				sleep(20, 50);
			}
		}
		return isExpanded() == expanded;
	}

	public Action getActionAt(final int slot) {
		if (slot < 0 || slot >= NUM_SLOTS) {
			throw new IndexOutOfBoundsException("0 > " + slot + " >= " + NUM_SLOTS);
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

	public boolean deleteAction(Action action) {
		int slot = action.getSlot();
		action = getActionAt(slot);
		if (action.getId() == -1) {
			return true;
		}
		Component c = ctx.widgets.get(WIDGET, COMPONENT_TRASH);
		if (!c.isVisible()) {
			return false;
		}
		if (action.getComponent().hover() && ctx.mouse.drag(c.getInteractPoint(), true)) {
			for (int i = 0; i < 5 && getActionAt(slot).getId() != -1; i++) {
				sleep(100, 200);
			}
		}
		return getActionAt(slot).getId() == -1;
	}

	public boolean isLocked() {
		return ((ctx.settings.get(682) >> 4) & 0x1) != 0;
	}

	public boolean setLocked(final boolean locked) {
		if (isLocked() == locked) {
			return true;
		}
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_LOCK);
		if (c.isVisible() && c.interact("lock")) {
			for (int i = 0; i < 25 && locked != isLocked(); i++) {
				sleep(100, 150);
			}
		}
		return isLocked() == locked;
	}

	@Override
	public Action getNil() {
		return new Action(ctx, 0, Action.Type.UNKNOWN, -1);
	}
}
