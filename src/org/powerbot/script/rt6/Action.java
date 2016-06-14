package org.powerbot.script.rt6;

import org.powerbot.script.Identifiable;
import org.powerbot.script.Validatable;

/**
 * Action
 */
public class Action extends ClientAccessor implements Identifiable, Validatable, Displayable {
	private final int bar, slot;
	private final Type type;
	private final int id;

	public Action(final ClientContext ctx, final int bar, final int slot, final Type type, final int id) {
		super(ctx);
		if (slot < 0 || type == null) {
			throw new IllegalArgumentException();
		}
		this.bar = bar;
		this.slot = slot;
		this.type = type;
		this.id = id;
	}

	public int slot() {
		return slot;
	}

	public Type type() {
		return this.type;
	}

	@Override
	public int id() {
		return id;
	}

	public String bind() {
		final Component c = ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_SLOT_BIND + slot * Constants.COMBATBAR_SLOT_LENGTH);
		return c.text().trim();
	}

	public boolean select() {
		return select(true);
	}

	public boolean select(final boolean key) {
		if (!valid()) {
			return false;
		}
		final String b = bind();
		if (key) {
			if (b.length() == 1) {
				ctx.input.send(bind());
				return true;
			}
		} else {
			return component().click();
		}
		return false;
	}

	public boolean ready() {
		final Component cooldown = ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_SLOT_COOLDOWN + slot * Constants.COMBATBAR_SLOT_LENGTH);
		final Component action = ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_SLOT_ACTION + slot * Constants.COMBATBAR_SLOT_LENGTH);
		return valid() && !cooldown.visible() && action.textColor() == 0xFFFFFF;
	}

	public int cooldownPercentage() {
		final Component cooldown = ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_SLOT_COOLDOWN + slot * Constants.COMBATBAR_SLOT_LENGTH);
		if (!cooldown.visible()) {
			return 100;
		}
		return (cooldown.textureId() - Constants.COMBATBAR_TEXTURE_COOLDOWN_MIN) * 100 /
				(Constants.COMBATBAR_TEXTURE_COOLDOWN_MAX - Constants.COMBATBAR_TEXTURE_COOLDOWN_MIN);
	}

	public boolean queued() {
		final Component queued = ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_SLOT_QUEUED + slot * Constants.COMBATBAR_SLOT_LENGTH);
		return valid() && queued.visible();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component component() {
		return ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_SLOT_ACTION + slot * Constants.COMBATBAR_SLOT_LENGTH);
	}

	@Override
	public int hashCode() {
		return Math.max(id, 0);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Action)) {
			return false;
		}
		final Action action = (Action) o;
		return slot == action.slot && type == action.type && id == action.id;
	}

	@Override
	public boolean valid() {
		return type != Type.UNKNOWN && id == (type == Type.ABILITY ?
				ctx.varpbits.varpbit((bar >= 6 ? Constants.COMBATBAR_ABILITY_STATE_2 : Constants.COMBATBAR_ABILITY_STATE) + slot + bar * Constants.COMBATBAR_SLOTS) :
				ctx.varpbits.varpbit((bar >= 6 ? Constants.COMBATBAR_ITEM_STATE_2 : Constants.COMBATBAR_ITEM_STATE) + slot + bar * Constants.COMBATBAR_SLOTS));
	}

	public enum Type {
		ABILITY, ITEM, UNKNOWN
	}
}
