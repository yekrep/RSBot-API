package org.powerbot.script.wrappers;

import org.powerbot.script.lang.Identifiable;
import org.powerbot.script.methods.ActionBar;
import org.powerbot.script.methods.ClientFactory;

import java.awt.Point;

public class Action extends Interactive implements Identifiable {
	private final int slot;
	private final Type type;
	private final int id;

	public Action(ClientFactory ctx, final int slot, final Type type, final int id) {
		super(ctx);
		if (slot < 0 || slot >= ActionBar.NUM_SLOTS || type == null || id <= 0) {
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
		if (c == null) {
			return "";
		}
		final String str = c.getText();
		return str != null ? str.trim() : "";
	}

	public boolean select() {//TODO if bind is in-capable, click + add a method for clicking
		if (!isValid()) {
			return false;
		}
		final String b = getBind();
		return b != null && ctx.keyboard.send(b);
	}

	public boolean isReady() {
		final Component reload = ctx.widgets.get(ActionBar.WIDGET, ActionBar.COMPONENT_SLOTS_COOLDOWN[slot]);
		final Component action = ctx.widgets.get(ActionBar.WIDGET, ActionBar.COMPONENT_SLOTS_ACTION[slot]);
		return reload != null && action != null &&
				reload.isValid() && !reload.isVisible() &&
				action.isValid() && action.getTextColor() == 0xFFFFFF;
	}

	@Override
	public Point getInteractPoint() {
		final Component c = ctx.widgets.get(ActionBar.WIDGET, ActionBar.COMPONENT_SLOTS[slot]);
		if (c == null) {
			return new Point(-1, -1);
		}
		return c.getInteractPoint();
	}

	@Override
	public Point getNextPoint() {
		final Component c = ctx.widgets.get(ActionBar.WIDGET, ActionBar.COMPONENT_SLOTS[slot]);
		if (c == null) {
			return new Point(-1, -1);
		}
		return c.getNextPoint();
	}

	@Override
	public Point getCenterPoint() {
		final Component c = ctx.widgets.get(ActionBar.WIDGET, ActionBar.COMPONENT_SLOTS[slot]);
		if (c == null) {
			return new Point(-1, -1);
		}
		return c.getCenterPoint();
	}

	@Override
	public boolean contains(final Point point) {
		final Component c = ctx.widgets.get(ActionBar.WIDGET, ActionBar.COMPONENT_SLOTS[slot]);
		return c != null && c.contains(point);
	}

	@Override
	public boolean isValid() {
		return this.id == (this.type == Type.ABILITY ?
				ctx.settings.get(ActionBar.SETTING_ABILITY + this.slot) :
				ctx.settings.get(ActionBar.SETTING_ITEM + this.slot));
	}

	public static enum Type {
		ABILITY, ITEM
	}
}
