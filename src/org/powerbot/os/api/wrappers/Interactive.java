package org.powerbot.os.api.wrappers;

import java.awt.Point;

import org.powerbot.os.api.MethodContext;
import org.powerbot.os.api.MethodProvider;

public abstract class Interactive extends MethodProvider implements Validatable {
	public Interactive(MethodContext ctx) {
		super(ctx);
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
