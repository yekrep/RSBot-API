package org.powerbot.script.wrappers;

import org.powerbot.script.lang.Identifiable;
import org.powerbot.script.lang.Validatable;
import org.powerbot.script.methods.ActionBar;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;

public class Action extends MethodProvider implements Identifiable, Validatable {
	private final int slot;
	private final Type type;
	private final int id;

	public Action(MethodContext ctx, final int slot, final Type type, final int id) {
		super(ctx);
		if (slot < 0 || slot >= ActionBar.NUM_SLOTS || type == null) {
			throw new IllegalArgumentException();
		}
		this.slot = slot;
		this.type = type;
		this.id = id;
	}

	public Type getType() {
		return this.type;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getBind() {
		final Component c = ctx.widgets.get(ActionBar.WIDGET, ActionBar.COMPONENT_SLOTS_BIND[slot]);
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
		final Component reload = ctx.widgets.get(ActionBar.WIDGET, ActionBar.COMPONENT_SLOTS_COOLDOWN[slot]);
		final Component action = ctx.widgets.get(ActionBar.WIDGET, ActionBar.COMPONENT_SLOTS_ACTION[slot]);
		return reload.isValid() && !reload.isVisible() &&
				action.isValid() && action.getTextColor() == 0xFFFFFF;
	}

	public Component getComponent() {
		return ctx.widgets.get(ActionBar.WIDGET, ActionBar.COMPONENT_SLOTS[slot]);
	}

	@Override
	public int hashCode() {
		return Math.max(this.id, 0);
	}

	@Override
	public boolean isValid() {
		return this.type != Type.UNKNOWN && this.id == (this.type == Type.ABILITY ?
				ctx.settings.get(ActionBar.SETTING_ABILITY + this.slot) :
				ctx.settings.get(ActionBar.SETTING_ITEM + this.slot));
	}

	public static enum Type {
		ABILITY, ITEM, UNKNOWN
	}
}
