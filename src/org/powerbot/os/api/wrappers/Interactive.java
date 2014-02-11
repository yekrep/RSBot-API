package org.powerbot.os.api.wrappers;

import java.awt.Point;

import org.powerbot.os.api.ClientAccessor;
import org.powerbot.os.api.ClientContext;
import org.powerbot.os.api.Menu;
import org.powerbot.os.api.util.Filter;

public abstract class Interactive extends ClientAccessor implements Validatable {
	public Interactive(ClientContext ctx) {
		super(ctx);
	}

	public boolean isInViewport() {
		return ctx.game.isPointInViewport(getInteractPoint());
	}

	public final boolean click(final Filter<Menu.Command> f) {
		return false;//TODO: this
	}

	public final boolean interact(final Filter<Menu.Command> f) {
		return false;//TODO this
	}

	public abstract Point getInteractPoint();

	public abstract Point getNextPoint();

	public abstract Point getCenterPoint();

	public abstract boolean contains(final Point point);

	@Override
	public boolean isValid() {
		return true;
	}
}
