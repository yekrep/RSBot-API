package org.powerbot.script.wrappers;

import org.powerbot.script.lang.Identifiable;
import org.powerbot.script.lang.Validatable;
import org.powerbot.script.methods.CombatBar;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;

public class Action extends MethodProvider implements Identifiable, Validatable {
	private final int slot;
	private final Type type;
	private final int id;

	public Action(MethodContext ctx, final int slot, final Type type, final int id) {
		super(ctx);
		if (slot < 0 || slot >= CombatBar.NUM_SLOTS || type == null) {
			throw new IllegalArgumentException();
		}
		this.slot = slot;
		this.type = type;
		this.id = id;
	}

	public int getSlot() {
		return slot;
	}

	public Type getType() {
		return this.type;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getBind() {
		final Component c = ctx.widgets.get(CombatBar.WIDGET, CombatBar.COMPONENT_SLOT_BIND + slot * 4);
		return c.getText().trim();
	}

	public boolean select() {//TODO if bind is in-capable, click + add a method for clicking
		if (!isValid()) {
			return false;
		}
		final String b = getBind();
		return b.length() == 1 && ctx.keyboard.send(getBind());
	}

	public boolean isReady() {
		Component cooldown = ctx.widgets.get(CombatBar.WIDGET, CombatBar.COMPONENT_SLOT_COOL_DOWN + slot * 4);
		Component action = ctx.widgets.get(CombatBar.WIDGET, CombatBar.COMPONENT_SLOT_ACTION + slot * 4);
		return cooldown.getTextureId() != CombatBar.TEXTURE_COOL_DOWN && action.getTextColor() == 0xFFFFFF;
	}

	public Component getComponent() {
		return ctx.widgets.get(CombatBar.WIDGET, CombatBar.COMPONENT_SLOT_ACTION + slot * 4);
	}

	@Override
	public int hashCode() {
		return Math.max(this.id, 0);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Action)) {
			return false;
		}
		Action action = (Action) o;
		return slot == action.slot && type == action.type && id == action.id;
	}

	@Override
	public boolean isValid() {
		return this.type != Type.UNKNOWN && this.id == (this.type == Type.ABILITY ?
				ctx.settings.get(CombatBar.SETTING_ABILITY + this.slot) :
				ctx.settings.get(CombatBar.SETTING_ITEM + this.slot));
	}

	public static enum Type {
		ABILITY, ITEM, UNKNOWN
	}
}
