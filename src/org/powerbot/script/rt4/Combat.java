package org.powerbot.script.rt4;

public class Combat extends ClientAccessor {
	public Combat(final ClientContext ctx) {
		super(ctx);
	}

	public int health() {
		try {
			return Integer.parseInt(ctx.widgets.component(548, 77).text().trim());
		} catch (final NumberFormatException ignored) {
		}
		return -1;
	}

	public int prayerPoints() {
		try {
			return Integer.parseInt(ctx.widgets.component(548, 87).text().trim());
		} catch (final NumberFormatException ignored) {
		}
		return -1;
	}
}
