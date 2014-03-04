package org.powerbot.script.rs3.tools;

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
		final Component c = ctx.widgets.get(CombatBar.WIDGET, CombatBar.COMPONENT_SLOT_BIND + slot * CombatBar.COMPONENT_SLOT_LENGTH);
		return c.getText().trim();
	}

	public boolean select() {
		return select(true);
	}

	public boolean select(final boolean key) {
		if (!isValid()) {
			return false;
		}
		final String b = getBind();
		return key ? b.length() == 1 && ctx.keyboard.send(getBind()) : getComponent().click();
	}

	public boolean isReady() {
		final Component cooldown = ctx.widgets.get(CombatBar.WIDGET, CombatBar.COMPONENT_SLOT_COOL_DOWN + slot * CombatBar.COMPONENT_SLOT_LENGTH);
		final Component action = ctx.widgets.get(CombatBar.WIDGET, CombatBar.COMPONENT_SLOT_ACTION + slot * CombatBar.COMPONENT_SLOT_LENGTH);
		return isValid() && !cooldown.isVisible() && action.getTextColor() == 0xFFFFFF;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getComponent() {
		return ctx.widgets.get(CombatBar.WIDGET, CombatBar.COMPONENT_SLOT_ACTION + slot * CombatBar.COMPONENT_SLOT_LENGTH);
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
		final Action action = (Action) o;
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
