package org.powerbot.script.rt4;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;

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

	public boolean scroll(final Component container, final Component component, final Component bar) {
		final Rectangle rect_d = container.boundingRect();
		if (rect_d.contains(component.boundingRect())) {
			return true;
		}
		final Point p = rect_d.getLocation();
		p.translate(Random.nextInt(10, rect_d.width - 10), Random.nextInt(10, rect_d.height - 10));
		if (!ctx.input.move(p)) {
			return false;
		}
		for (; Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				final Rectangle r = component.boundingRect();
				final int y = r.y;
				return ctx.input.scroll(r.y > rect_d.y) && Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						final Rectangle r = component.boundingRect();
						return r.y != y;
					}
				}, 10, 10);
			}
		}, 70, 3); ) {
			if (rect_d.contains(component.boundingRect())) {
				Condition.sleep();
				break;
			}
		}
		return rect_d.contains(component.boundingRect());
	}

	@Override
	public Widget nil() {
		return new Widget(ctx, -1);
	}
}
