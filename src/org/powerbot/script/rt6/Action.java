package org.powerbot.script.rt6;

import org.powerbot.script.Identifiable;
import org.powerbot.script.Validatable;

public class Action extends ClientAccessor implements Identifiable, Validatable, Displayable {
	private final int slot;
	private final Type type;
	private final int id;

	public Action(final ClientContext ctx, final int slot, final Type type, final int id) {
		super(ctx);
		if (slot < 0 || slot >= CombatBar.NUM_SLOTS || type == null) {
			throw new IllegalArgumentException();
		}
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
		final Component c = ctx.widgets.component(CombatBar.WIDGET, CombatBar.COMPONENT_SLOT_BIND + slot * CombatBar.COMPONENT_SLOT_LENGTH);
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
		final Component cooldown = ctx.widgets.component(CombatBar.WIDGET, CombatBar.COMPONENT_SLOT_COOL_DOWN + slot * CombatBar.COMPONENT_SLOT_LENGTH);
		final Component action = ctx.widgets.component(CombatBar.WIDGET, CombatBar.COMPONENT_SLOT_ACTION + slot * CombatBar.COMPONENT_SLOT_LENGTH);
		return valid() && !cooldown.visible() && action.textColor() == 0xFFFFFF;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component component() {
		return ctx.widgets.component(CombatBar.WIDGET, CombatBar.COMPONENT_SLOT_ACTION + slot * CombatBar.COMPONENT_SLOT_LENGTH);
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
				ctx.varpbits.varpbit(CombatBar.SETTING_ABILITY + slot) :
				ctx.varpbits.varpbit(CombatBar.SETTING_ITEM + slot));
	}

	public static enum Type {
		ABILITY, ITEM, UNKNOWN
	}
}
