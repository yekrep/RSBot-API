package org.powerbot.script.rt4;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;

/**
 * Widgets
 */
public class Widgets extends IdQuery<Widget> {
	private Widget[] sparseCache;

	public Widgets(final ClientContext ctx) {
		super(ctx);
		sparseCache = new Widget[0];
	}

	public synchronized Widget widget(final int index) {
		if (index < 0) {
			return new Widget(ctx, 0);
		}
		if (index < sparseCache.length && sparseCache[index] != null) {
			return sparseCache[index];
		}
		final Widget c = new Widget(ctx, index);
		final int l = sparseCache.length;
		if (index >= l) {
			sparseCache = Arrays.copyOf(sparseCache, index + 1);
			for (int i = l; i < index + 1; i++) {
				sparseCache[i] = new Widget(ctx, i);
			}
		}
		return sparseCache[index] = c;
	}

	/**
	 * Retrieves the cached {@link Component} of the given {@link Widget} index.
	 *
	 * @param index          the index of the desired {@link Widget}
	 * @param componentIndex the index of the desired {@link Component} of the given {@link Widget}
	 * @return the {@link Component} belonging to the {@link Widget} requested
	 */
	public Component component(final int index, final int componentIndex) {
		return widget(index).component(componentIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Widget> get() {
		final Client client = ctx.client();
		final org.powerbot.bot.rt4.client.Widget[][] a = client != null ? client.getWidgets() : null;
		final int len = a != null ? a.length : 0;
		if (len <= 0) {
			return new ArrayList<Widget>(0);
		}
		widget(len - 1);
		return new ArrayList<Widget>(Arrays.asList(Arrays.copyOf(sparseCache, len)));
	}

	/**
	 * Returns all the {@link Widget}s that are currently loaded in the game.
	 *
	 * @return an array of {@link Widget}s which are currently loaded
	 * @deprecated use queries
	 */
	public Widget[] array() {
		final List<Widget> w = get();
		return w.toArray(new Widget[w.size()]);
	}

	/**
	 * @return <ii>true</ii> if scrolled to view, otherwise <ii>false</ii>
	 * @deprecated use {@link #scroll(Component, Component, Component, boolean) scroll(component, pane, bar, scroll)}
	 */
	public boolean scroll(final Component container, final Component component, final Component bar) {
		return scroll(component, container, bar, true);
	}

	/**
	 * Scrolls to view the provided component, if it's not already in view.
	 *
	 * @param component   the component to scroll to
	 * @param pane        the viewport component
	 * @param bar         the scrollbar
	 * @param mouseScroll whether to use mouse wheel to scroll or not
	 * @return <ii>true</ii> if scrolled to view or is already in view, otherwise <ii>false</ii>
	 */
	public boolean scroll(final Component component, final Component pane, final Component bar, final boolean mouseScroll) {
		if (component == null || !component.valid()) {
			return false;
		}
		final int childrenCount;
		if (bar == null || !bar.valid() || ((childrenCount = bar.componentCount()) != 6 && childrenCount != 7)) {
			return false;
		}
		if (pane == null || !pane.valid() || pane.scrollHeight() == 0) {
			return false;
		}
		final Point view = pane.screenPoint();
		final int height = pane.height();
		if (view.x < 0 || view.y < 0 || height < 1) {
			return false;
		}
		final Point pos = component.screenPoint();
		final int length = component.height();
		if (pos.y >= view.y && pos.y <= view.y + height - length) {
			return true;
		}
		final Component thumbHolder = bar.component(0);
		final Component thumb = bar.component(1);
		final int thumbSize = thumbHolder.height();
		int y = (int) ((float) thumbSize / pane.scrollHeight() *
				(component.relativeY() + Random.nextInt(-height / 2, height / 2 - length)));
		if (y < 0) {
			y = 0;
		} else if (y >= thumbSize) {
			y = thumbSize - 1;
		}
		final Point p = thumbHolder.screenPoint();
		p.translate(Random.nextInt(0, thumbHolder.width()), y);
		if (!mouseScroll) {
			if (!ctx.input.click(p, true)) {
				return false;
			}
			Condition.sleep();
		}
		Point a;
		Component c;
		int tY = thumb.screenPoint().y;
		final long start = System.nanoTime();
		long mark = System.nanoTime();
		int scrolls = 0;
		while ((a = component.screenPoint()).y < view.y || a.y > view.y + height - length) {
			if (System.nanoTime() - start >= TimeUnit.SECONDS.toNanos(20)) {
				break;
			}
			if (mouseScroll && (pane.contains(ctx.input.getLocation()) || pane.hover())) {
				if (ctx.input.scroll(a.y > view.y)) {
					if (++scrolls >= Random.nextInt(9, 13)) {
						Condition.sleep(Random.getDelay() * Random.nextInt(3, 9));
						scrolls = 0;
					}
					Condition.sleep(Random.getDelay() / Random.nextInt(1, 3));
					if (System.nanoTime() - mark > TimeUnit.SECONDS.toNanos(2)) {
						final int l = thumb.screenPoint().y;
						if (tY == l) {
							return scroll(component, pane, bar, false);
						} else {
							mark = System.nanoTime();
							tY = l;
						}
					}
				} else {
					break;
				}
			} else {
				c = bar.component(a.y < view.y ? 4 : 5);
				if (c == null) {
					break;
				}
				if (c.hover()) {
					ctx.input.press(MouseEvent.BUTTON1);
					if (!Condition.wait(new Condition.Check() {
						@Override
						public boolean poll() {
							final Point a = component.screenPoint();
							return a.y >= view.y && a.y <= view.y + height - length;
						}
					}, 500, 10)) {
						++scrolls;
					}
					ctx.input.release(MouseEvent.BUTTON1);
				}
				if (scrolls >= 3) {
					return false;
				}
			}
		}
		return a.y >= view.y && a.y <= height + view.y + height - length;
	}

	@Override
	public Widget nil() {
		return new Widget(ctx, 0);
	}
}
