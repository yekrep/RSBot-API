package org.powerbot.os.api;

import java.awt.Point;

public abstract class Interactive extends MethodProvider {
	public Interactive(MethodContext ctx) {
		super(ctx);
	}

	public abstract Point getCenterPoint();
}
