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

	/**
	 * Uses the heal poison action on the combat bar.
	 *
	 * @return <tt>true</tt> if the action was selected; otherwise <tt>fales</tt>
	 */
	public boolean healPoison() {
		return ctx.widgets.get(WIDGET, COMPONENT_BUTTON_HEAL).interact("Heal");
	}

	/**
	 * Changes the retaliation mode.
	 *
	 * @param retaliate <tt>true</tt> to automatically retaliate; otherwise <tt>false</tt>
	 * @return <tt>true</tt> if the retaliation mode was successfully changed; otherwise <tt>false</tt>
	 */
	public boolean setRetaliating(boolean retaliate) {
		if (retaliate != isRetaliating() &&
				ctx.widgets.get(WIDGET, COMPONENT_BUTTON_RETALIATE).interact("Toggle")) {
			for (int i = 0; i < 10 && retaliate != isRetaliating(); i++) {
				sleep(50, 150);
			}
		}
		return retaliate == isRetaliating();
	}

	/**
	 * Determines if you are currently set to auto retaliate.
	 *
	 * @return <tt>true</tt> if retaliating; otherwise <tt>false</tt>
	 */
	public boolean isRetaliating() {
		return ctx.settings.get(SETTING_RETALIATION) == 0;
	}

	/**
	 * Determines the current health.
	 *
	 * @return the current health
	 */
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


	/**
	 * Determines the maximum health.
	 *
	 * @return the maximum health
	 */
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

	/**
	 * Determines the current level of adrenaline.
	 *
	 * @return the current level of adrenaline
	 */
	public int getAdrenaline() {
		return ctx.settings.get(SETTING_ADRENALINE);
	}

	/**
	 * Determines if the combat bar is expanded.
	 *
	 * @return <tt>true</tt> if expanded; otherwise <tt>false</tt>
	 */
	public boolean isExpanded() {
		return ctx.widgets.get(WIDGET, COMPONENT_BAR).isVisible();
	}

	/**
	 * Changes the state of the combat bar's expansion.
	 *
	 * @param expanded <tt>true</tt> to be expanded; <tt>false</tt> to be collapsed
	 * @return <tt>true</tt> if the state was successfully changed; otherwise <tt>false</tt>
	 */
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

	/**
	 * Returns the action at the specified slot.
	 *
	 * @param slot the slot to get the action at
	 * @return the {@link Action}
	 */
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

	/**
	 * Returns an array of all the actions on the combat bar.
	 *
	 * @return an array of {@link Action}s
	 */
	public Action[] getActions() {
		final Action[] actions = new Action[NUM_SLOTS];
		for (int i = 0; i < NUM_SLOTS; i++) {
			actions[i] = getActionAt(i);
		}
		return actions;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * Deletes the provided {@link Action} on the combat bar.
	 *
	 * @param action the {@link Action} to delete
	 * @return <tt>true</tt> if the {@link Action} was deleted; otherwise <tt>false</tt>
	 */
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

	/**
	 * Determines if the combat bar is locked.
	 *
	 * @return <tt>true</tt> if combat bar is locked; otherwise <tt>false</tt>
	 */
	public boolean isLocked() {
		return ((ctx.settings.get(682) >> 4) & 0x1) != 0;
	}

	/**
	 * Sets the locked state of the combat bar.
	 *
	 * @param locked <tt>true</tt> to be locked; otherwise <tt>false</tt>
	 * @return <tt>true</tt> if the state was successfully changed; otherwise <tt>false</tt>
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Action getNil() {
		return new Action(ctx, 0, Action.Type.UNKNOWN, -1);
	}
}
