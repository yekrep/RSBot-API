package org.powerbot.os.api.wrappers;

import java.awt.Point;

import org.powerbot.os.api.ClientAccessor;
import org.powerbot.os.api.ClientContext;

public abstract class Interactive extends ClientAccessor implements Validatable {
	public Interactive(ClientContext ctx) {
		super(ctx);
	}

	public boolean isInViewport() {
		return ctx.game.isPointInViewport(getInteractPoint());
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
