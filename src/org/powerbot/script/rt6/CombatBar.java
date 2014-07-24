package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.Condition;

public class CombatBar extends IdQuery<Action> {
	public static final int WIDGET = 1430;
	public static final int SETTING_ADRENALINE = 679;
	public static final int COMPONENT_BUTTON_HEAL = 3;
	public static final int SETTING_RETALIATION = 462;
	public static final int COMPONENT_BUTTON_RETALIATE = 45;
	public static final int COMPONENT_BUTTON_PRAYER = 8;
	public static final int COMPONENT_BUTTON_SUMMONING = 14;
	public static final int COMPONENT_HEALTH = 4;
	public static final int COMPONENT_ADRENALINE = 28;
	public static final int COMPONENT_PRAYER = 24;
	public static final int COMPONENT_SUMMONING = 30;
	public static final int COMPONENT_TEXT = 7;
	public static final int COMPONENT_BOUNDS = 0;

	public static final int NUM_SLOTS = 12;
	public static final int COMPONENT_BAR = 49;
	public static final int COMPONENT_LOCK = 246;
	public static final int WIDGET_LAYOUT = 1477;
	public static final int SETTING_ITEM = 811, SETTING_ABILITY = 727;
	public static final int COMPONENT_SLOT_ACTION = 54;
	public static final int COMPONENT_SLOT_COOL_DOWN = 55;
	public static final int COMPONENT_SLOT_BIND = 57;
	public static final int COMPONENT_SLOT_LENGTH = 13;

	public CombatBar(final ClientContext factory) {
		super(factory);
	}

	/**
	 * Uses the heal poison action on the combat bar.
	 *
	 * @return <tt>true</tt> if the action was selected; otherwise <tt>false</tt>
	 */
	public boolean regenerate() {
		return ctx.widgets.component(WIDGET, COMPONENT_BUTTON_HEAL).interact("Regenerate");
	}

	/**
	 * Uses the heal poison action on the combat bar.
	 *
	 * @return <tt>true</tt> if the action was selected; otherwise <tt>false</tt>
	 */
	public boolean healPoison() {
		return ctx.widgets.component(WIDGET, COMPONENT_BUTTON_HEAL).interact("Heal");
	}

	/**
	 * Changes the retaliation mode.
	 *
	 * @param retaliate <tt>true</tt> to automatically retaliate; otherwise <tt>false</tt>
	 * @return <tt>true</tt> if the retaliation mode was successfully changed; otherwise <tt>false</tt>
	 */
	public boolean retaliating(final boolean retaliate) {
		return retaliate == retaliating() || (ctx.widgets.component(WIDGET, COMPONENT_BUTTON_RETALIATE).interact("Toggle") &&
				Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						return retaliating() == retaliate;
					}
				}, 200, 10));
	}

	/**
	 * Determines if you are currently set to auto retaliate.
	 *
	 * @return <tt>true</tt> if retaliating; otherwise <tt>false</tt>
	 */
	public boolean retaliating() {
		return ctx.varpbits.varpbit(SETTING_RETALIATION) == 0;
	}

	public int targetHealth() {
		final Component component = ctx.widgets.component(1490, 28);
		final String text;
		if (component.visible() && !(text = component.text()).isEmpty()) {
			try {
				return Integer.parseInt(text.trim());
			} catch (final NumberFormatException ignored) {
			}
		}
		return -1;
	}

	public int targetHealthPercent() {
		final Component bar = ctx.widgets.component(1490, 27);
		final Component overlap = ctx.widgets.component(1490, 29);
		if (!bar.visible() || !overlap.visible()) {
			return -1;
		}
		final double w = bar.scrollWidth(), p = overlap.scrollWidth();
		return w > 0 ? (int) Math.ceil(p / w * 100d) : -1;
	}

	/**
	 * Determines the current health.
	 *
	 * @return the current health
	 */
	public int health() {
		if (ctx.hud.legacy()) {
			final String text = ctx.widgets.component(1504, 3).component(7).text().trim();
			try {
				return Integer.parseInt(text);
			} catch (final NumberFormatException ignored) {
			}
			return -1;
		}
		final String text = ctx.widgets.component(WIDGET, COMPONENT_HEALTH).component(COMPONENT_TEXT).text();
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
	public int maximumHealth() {
		if (ctx.hud.legacy()) {
			return ctx.skills.realLevel(Skills.CONSTITUTION) * 10;
		}
		final String text = ctx.widgets.component(WIDGET, COMPONENT_HEALTH).component(COMPONENT_TEXT).text();
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
	public int adrenaline() {
		return ctx.varpbits.varpbit(SETTING_ADRENALINE);
	}

	/**
	 * Determines if the combat bar is expanded.
	 *
	 * @return <tt>true</tt> if expanded; otherwise <tt>false</tt>
	 */
	public boolean expanded() {
		return ctx.widgets.component(WIDGET, COMPONENT_BAR).visible();
	}

	/**
	 * Changes the state of the combat bar's expansion.
	 *
	 * @param expanded <tt>true</tt> to be expanded; <tt>false</tt> to be collapsed
	 * @return <tt>true</tt> if the state was successfully changed; otherwise <tt>false</tt>
	 */
	public boolean expanded(final boolean expanded) {
		if (ctx.hud.legacy()) {
			return false;
		}
		if (expanded() == expanded) {
			return true;
		}
		Component comp = null;
		for (final Component c : ctx.widgets.widget(WIDGET_LAYOUT)) {
			if (c.childrenCount() != 2) {
				continue;
			}
			if (c.component(1).textureId() == 18612) {
				comp = c.component(1);
				break;
			}
		}
		return comp != null && comp.interact(expanded ? "Maximise" : "Minimise") &&
				Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						return expanded() == expanded;
					}
				}, 300, 10);
	}

	/**
	 * Returns the action at the specified slot.
	 *
	 * @param slot the slot to get the action at
	 * @return the {@link Action}
	 */
	public Action actionAt(final int slot) {
		if (slot < 0 || slot >= NUM_SLOTS) {
			throw new IndexOutOfBoundsException("0 > " + slot + " >= " + NUM_SLOTS);
		}
		final Action.Type type;
		int id = ctx.varpbits.varpbit(SETTING_ABILITY + slot);
		if (id > 0) {
			type = Action.Type.ABILITY;
		} else if ((id = ctx.varpbits.varpbit(SETTING_ITEM + slot)) > 0) {
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
	public Action[] actions() {
		final Action[] actions = new Action[NUM_SLOTS];
		for (int i = 0; i < NUM_SLOTS; i++) {
			actions[i] = actionAt(i);
		}
		return actions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Action> get() {
		if (ctx.hud.legacy()) {
			return new ArrayList<Action>(0);
		}
		final List<Action> actions = new ArrayList<Action>(NUM_SLOTS);
		final Action[] arr = actions();
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
		if (!expanded(true)) {
			return false;
		}
		final int slot = action.slot();
		action = actionAt(slot);
		return action.id() == -1 || action.component().hover() &&
				ctx.input.drag(ctx.players.local().nextPoint(), true) && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return actionAt(slot).id() == -1;
			}
		}, 20, 20);
	}

	/**
	 * Determines if the combat bar is locked.
	 *
	 * @return <tt>true</tt> if combat bar is locked; otherwise <tt>false</tt>
	 */
	public boolean locked() {
		return ((ctx.varpbits.varpbit(682) >> 4) & 0x1) != 0;
	}

	/**
	 * Sets the locked state of the combat bar.
	 *
	 * @param locked <tt>true</tt> to be locked; otherwise <tt>false</tt>
	 * @return <tt>true</tt> if the state was successfully changed; otherwise <tt>false</tt>
	 */
	public boolean locked(final boolean locked) {
		if (locked() == locked) {
			return true;
		}
		final Component c = ctx.widgets.component(WIDGET, COMPONENT_LOCK);
		return c.visible() && c.interact("lock") &&
				Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						return locked() == locked;
					}
				}, 300, 10);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Action nil() {
		return new Action(ctx, 0, Action.Type.UNKNOWN, -1);
	}
}
