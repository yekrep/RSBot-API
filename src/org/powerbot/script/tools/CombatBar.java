package org.powerbot.script.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.powerbot.script.lang.IdQuery;
import org.powerbot.script.util.Condition;

public class CombatBar extends IdQuery<Action> {
	public static final int WIDGET = 1430;
	public static final int SETTING_ADRENALINE = 679;
	public static final int COMPONENT_BUTTON_HEAL = 103;
	public static final int SETTING_RETALIATION = 462;
	public static final int COMPONENT_BUTTON_RETALIATE = 113;
	public static final int COMPONENT_BUTTON_PRAYER = 107;
	public static final int COMPONENT_BUTTON_SUMMONING = 109;
	public static final int COMPONENT_HEALTH = 104;
	public static final int COMPONENT_ADRENALINE = 114;
	public static final int COMPONENT_PRAYER = 110;
	public static final int COMPONENT_SUMMONING = 116;
	public static final int COMPONENT_TEXT = 7;
	public static final int COMPONENT_BOUNDS = 93;

	public static final int NUM_SLOTS = 12;
	public static final int COMPONENT_BAR = 98;
	public static final int COMPONENT_LOCK = 25;
	public static final int WIDGET_LAYOUT = 1477;
	public static final int SETTING_ITEM = 811, SETTING_ABILITY = 727;
	public static final int COMPONENT_SLOT_ACTION = 118;
	public static final int COMPONENT_SLOT_COOL_DOWN = 119;
	public static final int COMPONENT_SLOT_BIND = 121;
	public static final int COMPONENT_SLOT_LENGTH = 6;

	public CombatBar(final MethodContext factory) {
		super(factory);
	}

	/**
	 * Uses the heal poison action on the combat bar.
	 *
	 * @return <tt>true</tt> if the action was selected; otherwise <tt>false</tt>
	 */
	public boolean regenerate() {
		return ctx.widgets.get(WIDGET, COMPONENT_BUTTON_HEAL).interact("Regenerate");
	}

	/**
	 * Uses the heal poison action on the combat bar.
	 *
	 * @return <tt>true</tt> if the action was selected; otherwise <tt>false</tt>
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
	public boolean setRetaliating(final boolean retaliate) {
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

	public int getTargetHealth() {
		final Component component = ctx.widgets.get(1490, 28);
		final String text;
		if (component.isVisible() && !(text = component.getText()).isEmpty()) {
			try {
				return Integer.parseInt(text.trim());
			} catch (final NumberFormatException ignored) {
			}
		}
		return -1;
	}

	public int getTargetHealthPercent() {
		final Component bar = ctx.widgets.get(1490, 27);
		final Component overlap = ctx.widgets.get(1490, 29);
		if (!bar.isVisible() || !overlap.isVisible()) {
			return -1;
		}
		final double w = bar.getScrollWidth(), p = overlap.getScrollWidth();
		return w > 0 ? (int) Math.ceil(p / w * 100d) : -1;
	}

	/**
	 * Determines the current health.
	 *
	 * @return the current health
	 */
	public int getHealth() {
		final String text = ctx.widgets.get(WIDGET, COMPONENT_HEALTH).getChild(COMPONENT_TEXT).getText();
		final int index = text.indexOf('/');
		if (index != -1) {
			try {
				return Integer.parseInt(text.substring(0, index));
			} catch (final NumberFormatException ignored) {
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
		final String text = ctx.widgets.get(WIDGET, COMPONENT_HEALTH).getChild(COMPONENT_TEXT).getText();
		final int index = text.indexOf('/');
		if (index != -1) {
			try {
				return Integer.parseInt(text.substring(index + 1));
			} catch (final NumberFormatException ignored) {
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
		Component comp = null;
		for (final Component c : ctx.widgets.get(WIDGET_LAYOUT)) {
			if (c.getChildrenCount() != 2) {
				continue;
			}
			if (c.getChild(1).getTextureId() == 18612) {
				comp = c.getChild(1);
				break;
			}
		}
		if (comp != null && comp.interact(expanded ? "Maximise" : "Minimise")) {
			for (int i = 0; i < 5 && isExpanded() != expanded; i++) {
				sleep(200, 500);
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
		final Action.Type type;
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
		final List<Action> actions = new ArrayList<Action>(NUM_SLOTS);
		final Action[] arr = getActions();
		for (final Action a : arr) {
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
		if (!setExpanded(true)) {
			return false;
		}
		final int slot = action.getSlot();
		action = getActionAt(slot);
		if (action.getId() == -1) {
			return true;
		}
		return action.getComponent().hover() && ctx.mouse.drag(ctx.players.local().getNextPoint(), true) &&
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() {
						return getActionAt(slot).getId() == -1;
					}
				}, 20, 20);
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
