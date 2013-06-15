package org.powerbot.script.internal.randoms;

import org.powerbot.event.PaintListener;
import org.powerbot.script.internal.ScriptContainer;
import org.powerbot.script.lang.Validatable;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.task.PollingTask;

import java.awt.Graphics;

public abstract class PollingPassive extends PollingTask implements Validatable, PaintListener {
	public PollingPassive(MethodContext ctx, ScriptContainer container) {
		super(ctx, container);
	}

	@Override
	public final boolean isSuspended() {
		return false;
	}

	@Override
	public void onRepaint(Graphics render) {
	}
}
