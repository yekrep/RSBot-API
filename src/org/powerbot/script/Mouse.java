package org.powerbot.script;

import java.awt.Point;

public class Mouse<C extends ClientContext> extends ClientAccessor<C> {

	public Mouse(final C ctx) {
		super(ctx);
	}

	public boolean apply(final Targetable targetable, final Filter<Point> filter) {
		return ctx.input.apply(targetable, filter);
	}

	public boolean click(final boolean left) {
		return ctx.input.click(left);
	}

	public boolean click(final int button) {
		return ctx.input.click(button);
	}

	public boolean click(final int x, final int y, final boolean left) {
		return ctx.input.click(x, y, left);
	}

	public boolean click(final int x, final int y, final int button) {
		return ctx.input.click(x, y, button);
	}

	public boolean click(final Point point, final boolean left) {
		return ctx.input.click(point, left);
	}

	public boolean click(final Point point, final int button) {
		return ctx.input.click(point, button);
	}

	public boolean drag(final Point p, final boolean left) {
		return ctx.input.drag(p, left);
	}

	public boolean drag(final Point p, final int button) {
		return ctx.input.drag(p, button);
	}

	public Point getLocation() {
		return ctx.input.getLocation();
	}

	public Point getPressLocation() {
		return ctx.input.getPressLocation();
	}

	public long getPressWhen() {
		return ctx.input.getPressWhen();
	}

	public boolean hop(final int x, final int y) {
		return ctx.input.hop(x, y);
	}

	public boolean hop(final Point p) {
		return ctx.input.hop(p);
	}

	public boolean move(final int x, final int y) {
		return ctx.input.move(x, y);
	}

	public boolean move(final Point p) {
		return ctx.input.move(p);
	}

	public boolean press(final int button) {
		return ctx.input.press(button);
	}

	public boolean release(final int button) {
		return ctx.input.release(button);
	}

	public boolean scroll() {
		return ctx.input.scroll();
	}

	public boolean scroll(final boolean down) {
		return ctx.input.scroll(down);
	}
}
