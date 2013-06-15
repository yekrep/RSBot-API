package org.powerbot.script.internal.randoms;

import org.powerbot.client.event.PaintListener;
import org.powerbot.script.internal.ScriptGroup;
import org.powerbot.script.lang.Validatable;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.task.PollingTask;

import java.awt.Graphics;

public abstract class PollingPassive extends PollingTask implements Validatable, PaintListener {
	public PollingPassive(final MethodContext ctx, final ScriptGroup container) {
		super(ctx, container);
	}

	@Override
	public final boolean isSuspended() {
		return false;
	}

	@Override
	public void repaint(Graphics render) {
	}
}
