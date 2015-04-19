package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.Condition;
import org.powerbot.util.StringUtils;

public class CombatBar extends IdQuery<Action> {
	@Deprecated
	public static final int WIDGET = Constants.COMBATBAR_WIDGET;
	@Deprecated
	public static final int SETTING_ADRENALINE = Constants.COMBATBAR_ADRENALINE_STATE;
	@Deprecated
	public static final int COMPONENT_BUTTON_HEAL = Constants.COMBATBAR_BUTTON_HEAL;
	@Deprecated
	public static final int SETTING_RETALIATION = Constants.COMBATBAR_RETALIATE_STATE;
	@Deprecated
	public static final int COMPONENT_BUTTON_RETALIATE = Constants.COMBATBAR_RETALIATE;
	@Deprecated
	public static final int COMPONENT_BUTTON_PRAYER = Constants.COMBATBAR_PRAYER_BUTTON;
	@Deprecated
	public static final int COMPONENT_BUTTON_SUMMONING = Constants.COMBATBAR_SUMMONING_BUTTON;
	@Deprecated
	public static final int COMPONENT_HEALTH = Constants.COMBATBAR_HEALTH;
	@Deprecated
	public static final int COMPONENT_ADRENALINE = Constants.COMBATBAR_ADRENALINE;
	@Deprecated
	public static final int COMPONENT_PRAYER = Constants.COMBATBAR_PRAYER;
	@Deprecated
	public static final int COMPONENT_SUMMONING = Constants.COMBATBAR_SUMMONING;
	@Deprecated
	public static final int COMPONENT_TEXT = Constants.COMBATBAR_TEXT;
	@Deprecated
	public static final int COMPONENT_BOUNDS = Constants.COMBATBAR_BOUNDS;

	@Deprecated
	public static final int NUM_SLOTS = Constants.COMBATBAR_SLOTS;
	@Deprecated
	public static final int COMPONENT_BAR = Constants.COMBATBAR_BAR;
	@Deprecated
	public static final int COMPONENT_LOCK = Constants.COMBATBAR_LOCK;
	@Deprecated
	public static final int WIDGET_LAYOUT = Constants.COMBATBAR_LAYOUT;
	@Deprecated
	public static final int SETTING_ITEM = Constants.COMBATBAR_ITEM_STATE;
	@Deprecated
	public static final int SETTING_ABILITY = Constants.COMBATBAR_ABILITY_STATE;
	@Deprecated
	public static final int COMPONENT_SLOT_ACTION = Constants.COMBATBAR_SLOT_ACTION;
	@Deprecated
	public static final int COMPONENT_SLOT_COOL_DOWN = Constants.COMBATBAR_SLOT_COOLDOWN;
	@Deprecated
	public static final int COMPONENT_SLOT_BIND = Constants.COMBATBAR_SLOT_BIND;
	@Deprecated
	public static final int COMPONENT_SLOT_LENGTH = Constants.COMBATBAR_SLOT_LENGTH;

	public CombatBar(final ClientContext factory) {
		super(factory);
	}

	/**
	 * Uses the heal poison action on the combat bar.
	 *
	 * @return <tt>true</tt> if the action was selected; otherwise <tt>false</tt>
	 */
	public boolean regenerate() {
		return ctx.hud.legacy() ? ctx.widgets.component(1504, 1).interact("Regenerate") :
				ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_BUTTON_HEAL).interact("Regenerate");
	}

	/**
	 * Uses the heal poison action on the combat bar.
	 *
	 * @return <tt>true</tt> if the action was selected; otherwise <tt>false</tt>
	 */
	public boolean healPoison() {
		return ctx.hud.legacy() ? ctx.widgets.component(1504, 1).interact("Cure") :
				ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_BUTTON_HEAL).interact("Heal");
	}

	/**
	 * Changes the retaliation mode.
	 *
	 * @param retaliate <tt>true</tt> to automatically retaliate; otherwise <tt>false</tt>
	 * @return <tt>true</tt> if the retaliation mode was successfully changed; otherwise <tt>false</tt>
	 */
	public boolean retaliating(final boolean retaliate) {
		return retaliate == retaliating() ||
				ctx.hud.legacy() ? (ctx.hud.open(Hud.Window.MELEE_ABILITIES) && ctx.widgets.component(1503, 49).click()) :
				((ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_RETALIATE).interact("Toggle")) && Condition.wait(new Condition.Check() {
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
		return ctx.varpbits.varpbit(Constants.COMBATBAR_RETALIATE_STATE) == 0;
	}

	public int targetHealth() {
		final Component component = ctx.widgets.component(1490, 30);
		final String text;
		if (component.visible() && !(text = component.text()).isEmpty()) {
			return StringUtils.parseInt(text);
		}
		return -1;
	}

	public int targetHealthPercent() {
		final Component bar = ctx.widgets.component(1490, 29);
		final Component overlap = ctx.widgets.component(1490, 31);
		if (!bar.visible() || !overlap.visible()) {
			return -1;
		}
		final double w = bar.scrollWidth(), p = overlap.scrollWidth();
		return w > 0 ? (int) Math.ceil(p / w * 100d) : -1;
	}

	public int targetCombatLevel() {
		final Component component = ctx.widgets.component(1490, 25);
		final String text;
		if (component.visible() && !(text = component.text()).isEmpty()) {
			return StringUtils.parseInt(text);
		}
		return -1;
	}

	public int targetWeakness() {
		final Component component = ctx.widgets.component(1490, 13);
		return component.textureId();
	}

	public String targetName() {
		return ctx.widgets.component(1490, 3).text();
	}

	public List<Integer> targetEffects() {
		final ArrayList<Integer> list = new ArrayList<Integer>();
		for (int c = 47; c <= 81; c += 3) {
			final Component component = ctx.widgets.component(1490, c);
			final int id;
			if ((id = component.textureId()) == -1 || !component.visible()) {
				continue;
			}
			list.add(id);
		}
		return list;
	}

	/**
	 * Determines the current health.
	 *
	 * @return the current health
	 */
	public int health() {
		if (ctx.hud.legacy()) {
			return StringUtils.parseInt(ctx.widgets.component(1504, 3).component(7).text());
		}
		final String text = ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_HEALTH).component(Constants.COMBATBAR_TEXT).text();
		final int index = text.indexOf('/');
		if (index != -1) {
			return StringUtils.parseInt(text.substring(0, index));
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
			return ctx.skills.realLevel(Constants.SKILLS_CONSTITUTION) * 10;
		}
		final String text = ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_HEALTH).component(Constants.COMBATBAR_TEXT).text();
		final int index = text.indexOf('/');
		if (index != -1) {
			return StringUtils.parseInt(text.substring(index + 1));
		}
		return -1;
	}

	/**
	 * Determines the current level of adrenaline.
	 *
	 * @return the current level of adrenaline
	 */
	public int adrenaline() {
		return ctx.varpbits.varpbit(Constants.COMBATBAR_ADRENALINE_STATE);
	}

	/**
	 * Determines if the combat bar is expanded.
	 *
	 * @return <tt>true</tt> if expanded; otherwise <tt>false</tt>
	 */
	public boolean expanded() {
		return ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_BAR).visible();
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
		for (final Component c : ctx.widgets.widget(Constants.COMBATBAR_LAYOUT)) {
			if (c.childrenCount() != 2) {
				continue;
			}
			if (c.component(1).textureId() == 18612 || c.component(1).textureId() == 24004) {
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
		if (slot < 0 || slot >= Constants.COMBATBAR_SLOTS) {
			throw new IndexOutOfBoundsException("0 > " + slot + " >= " + Constants.COMBATBAR_SLOTS);
		}
		final Action.Type type;
		int id = ctx.varpbits.varpbit(Constants.COMBATBAR_ABILITY_STATE + slot);
		if (id > 0) {
			type = Action.Type.ABILITY;
		} else if ((id = ctx.varpbits.varpbit(Constants.COMBATBAR_ITEM_STATE + slot)) > 0) {
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
		final Action[] actions = new Action[Constants.COMBATBAR_SLOTS];
		for (int i = 0; i < Constants.COMBATBAR_SLOTS; i++) {
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
		final List<Action> actions = new ArrayList<Action>(Constants.COMBATBAR_SLOTS);
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
		final Component c = ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_LOCK);
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
