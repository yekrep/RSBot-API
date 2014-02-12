package org.powerbot.os.api.wrappers;

import java.awt.Point;

import org.powerbot.os.api.methods.ClientAccessor;
import org.powerbot.os.api.methods.ClientContext;
import org.powerbot.os.api.methods.Menu;
import org.powerbot.os.api.util.Filter;

public abstract class Interactive extends ClientAccessor implements Targetable, Validatable {
	public Interactive(ClientContext ctx) {
		super(ctx);
	}

	public boolean isInViewport() {
		return ctx.game.isPointInViewport(getNextPoint());
	}

	public abstract Point getCenterPoint();

	public final boolean click(final Filter<Menu.Command> f) {
		return false;//TODO: this
	}

	public final boolean interact(final Filter<Menu.Command> f) {
		return false;//TODO this
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
