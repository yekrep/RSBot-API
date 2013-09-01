package org.powerbot.script.methods;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.lang.IdQuery;
import org.powerbot.script.wrappers.Action;
import org.powerbot.script.wrappers.Component;

import static org.powerbot.script.util.Constants.getInt;

public class CombatBar extends IdQuery<Action> {
	public static final int WIDGET = getInt("combatbar.widget");
	public static final int SETTING_ADRENALINE = getInt("combatbar.setting.adrenaline");
	public static final int COMPONENT_BUTTON_HEAL = getInt("combatbar.component.button.heal");
	public static final int SETTING_RETALIATION = getInt("combatbar.setting.retaliation");
	public static final int COMPONENT_BUTTON_RETALIATE = getInt("combatbar.component.button.retaliate");
	public static final int COMPONENT_BUTTON_PRAYER = getInt("combatbar.component.button.prayer");
	public static final int COMPONENT_BUTTON_SUMMONING = getInt("combatbar.component.button.summoning");
	public static final int COMPONENT_HEALTH = getInt("combatbar.component.health");
	public static final int COMPONENT_ADRENALINE = getInt("combatbar.component.adrenaline");
	public static final int COMPONENT_PRAYER = getInt("combatbar.component.prayer");
	public static final int COMPONENT_SUMMONING = getInt("combatbar.component.summoning");
	public static final int COMPONENT_TEXT = getInt("combatbar.component.text");
	public static final int COMPONENT_BOUNDS = getInt("combatbar.component.bounds");

	public static final int NUM_SLOTS = getInt("combatbar.num.slots");
	public static final int COMPONENT_BAR = getInt("combatbar.component.bar");
	public static final int COMPONENT_LOCK = getInt("combatbar.component.lock");
	public static final int COMPONENT_TRASH = getInt("combatbar.component.trash");
	public static final int WIDGET_LAYOUT = getInt("combatbar.widget.layout");
	public static final int COMPONENT_BUTTON_TOGGLE = getInt("combatbar.component.button.toggle");
	public static final int COMPONENT_BUTTON_TOGGLE_IDX = getInt("combatbar.component.button.toggle.idx");
	public static final int SETTING_ITEM = getInt("combatbar.setting.item");
	public static final int SETTING_ABILITY = getInt("combatbar.setting.ability");
	public static final int COMPONENT_SLOT_ACTION = getInt("combatbar.component.slot.action");
	public static final int COMPONENT_SLOT_COOL_DOWN = getInt("combatbar.component.slot.cool.down");
	public static final int COMPONENT_SLOT_BIND = getInt("combatbar.component.slot.bind");

	public CombatBar(MethodContext factory) {
		super(factory);
	}

	public boolean healPoison() {
		return ctx.widgets.get(WIDGET, COMPONENT_BUTTON_HEAL).interact("Heal");
	}

	@Deprecated
	public boolean setRealiating(boolean retaliate) {
		return setRetaliating(retaliate);
	}

	public boolean setRetaliating(boolean retaliate) {
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
