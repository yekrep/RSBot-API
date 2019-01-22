package org.powerbot.script.rt6;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.powerbot.script.AbstractQuery;

public class Components extends AbstractQuery<Components, Component, ClientContext> {
	public Components(ClientContext ctx) {
		super(ctx);
	}

	@Override
	protected Components getThis() {
		return this;
	}

	/**
	 * Loads components, along with their children, contained in the provided widgets.
	 *
	 * @param widgets widgets to load components for.
	 * @return {@code this} for the purpose of chaining.
	 */
	public Components select(final Widget... widgets) {
		return select(true, widgets);
	}

	/**
	 * Loads components contained in the provided widgets.
	 *
	 * @param children whether or not to load children components (nested components).
	 * @param widgets  widgets to load components for.
	 * @return {@code this} for the purpose of chaining.
	 */
	public Components select(final boolean children, final Widget... widgets) {
		return select(get(children, Arrays.asList(widgets)));
	}

	/**
	 * Loads components, along with their children, contained in the widgets loaded from the provided ids.
	 *
	 * @param widgetIds ids of widgets to load components for.
	 * @return {@code this} for the purpose of chaining.
	 */
	public Components select(int... widgetIds) {
		return select(true, widgetIds);
	}

	/**
	 * Loads components contained in the widgets loaded from the provided ids.
	 *
	 * @param children  whether or not to load children components (nested components).
	 * @param widgetIds ids of widgets to load components for.
	 * @return {@code this} for the purpose of chaining.
	 */
	public Components select(boolean children, int... widgetIds) {
		final List<Widget> widgets = new ArrayList<>();
		for (final int id : widgetIds) {
			widgets.add(ctx.widgets.widget(id));
		}
		return select(get(children, widgets));
	}

	/**
	 * Loads sub-components contained in the parent component inside the widget.
	 *
	 * @param widget    id of the widget to load.
	 * @param component index of the component whose children to load.
	 * @return {@code this} for the purpose of chaining.
	 */
	public Components select(final int widget, final int component) {
		return select(Arrays.asList(ctx.widgets.component(widget, component).components()));
	}

	@Override
	protected List<Component> get() {
		return get(true, ctx.widgets.select());
	}

	private List<Component> get(final boolean children, final Iterable<Widget> widgets) {
		final LinkedList<Component> base = new LinkedList<>();
		final List<Component> components = new ArrayList<>();
		for (final Widget w : widgets) {
			Collections.addAll(base, w.components());
		}
		while (!base.isEmpty()) {
			final Component c = base.poll();
			if (children && c.components().length > 0) {
				Collections.addAll(base, c.components());
			} else {
				components.add(c);
			}
		}
		return components;
	}


	@Override
	public Component nil() {
		return ctx.widgets.component(0, 0);
	}

	public Components visible() {
		return select(Component::visible);
	}

	public Components inViewport() {
		return select(Interactive::inViewport);
	}

	/**
	 * Filters for components which are containers of other components (parents of children components).
	 *
	 * @return {@code this} for the purpose of chaining.
	 */
	public Components parents() {
		return select(c -> c.childrenCount() > 0);
	}


	public Components textContains(final String... text) {
		final String[] arr = new String[text.length];
		for (int i = 0; i < text.length; i++) {
			arr[i] = text[i].toLowerCase();
		}
		return select(component -> {
			final String text1 = component.text().toLowerCase().trim();
			for (final String s : arr) {
				if (text1.contains(s)) {
					return true;
				}
			}
			return false;
		});
	}

	public Components text(final String... strings) {
		return select(component -> {
			final String text = component.text().trim();
			for (final String s : strings) {
				if (s.equalsIgnoreCase(text)) {
					return true;
				}
			}
			return false;
		});
	}


	public Components actionsContain(final String... text) {
		final String[] arr = new String[text.length];
		for (int i = 0; i < text.length; i++) {
			arr[i] = text[i].toLowerCase();
		}
		return select(component -> {
			final String[] comp_actions = component.actions();
			final String[] actions = new String[text.length];
			for (int i = 0; i < text.length; i++) {
				actions[i] = comp_actions[i].toLowerCase().trim();
			}
			for (final String s : arr) {
				for (final String a : actions) {
					if (a.contains(s)) {
						return true;
					}
				}
			}
			return false;
		});
	}

	public Components actions(final String... strings) {
		return select(component -> {
			final String[] actions = component.actions();
			for (final String s : strings) {
				for (final String a : actions) {
					if (s.equalsIgnoreCase(a.trim())) {
						return true;
					}
				}
			}
			return false;
		});
	}

	public Components tooltipContains(final String... text) {
		final String[] arr = new String[text.length];
		for (int i = 0; i < text.length; i++) {
			arr[i] = text[i].toLowerCase();
		}
		return select(component -> {
			final String text1 = component.tooltip().toLowerCase().trim();
			for (final String s : arr) {
				if (text1.contains(s)) {
					return true;
				}
			}
			return false;
		});
	}

	public Components tooltip(final String... strings) {
		return select(component -> {
			final String text = component.tooltip().trim();
			for (final String s : strings) {
				if (s.equalsIgnoreCase(text)) {
					return true;
				}
			}
			return false;
		});
	}

	public Components contentType(final int... types) {
		return select(component -> {
			for (int type : types) {
				if (type == component.contentType()) {
					return true;
				}
			}
			return false;
		});
	}

	public Components modelId(final int... ids) {
		return select(component -> {
			for (int id : ids) {
				if (id == component.modelId()) {
					return true;
				}
			}
			return false;
		});
	}

	public Components itemId(final int... ids) {
		return select(component -> {
			final int itemId = component.itemId();
			for (int id : ids) {
				if (itemId == id) {
					return true;
				}
			}
			return false;
		});
	}

	public Components texture(final int... textures) {
		return select(component -> {
			final int textureId = component.textureId();
			for (final int i : textures) {
				if (textureId == i) {
					return true;
				}
			}
			return false;
		});
	}

	public Components bounds(final Rectangle... rectangles) {
		return select(component -> {
			final int width = component.width();
			final int height = component.height();

			for (final Rectangle i : rectangles) {
				if (i.width == width && i.height == height) {
					return true;
				}
			}
			return false;
		});
	}

	public Components width(final int... widths) {
		return select(c -> {
			final int width = c.width();
			for (final int w : widths) {
				if (width == w) {
					return true;
				}
			}
			return false;
		});
	}

	public Components height(final int... heights) {
		return select(c -> {
			final int height = c.height();
			for (final int h : heights) {
				if (height == h) {
					return true;
				}
			}
			return false;
		});
	}

	public Components scrollWidth(final int... widths) {
		return select(c -> {
			final int width = c.scrollWidth();
			for (final int w : widths) {
				if (width == w) {
					return true;
				}
			}
			return false;
		});
	}

	public Components scrollHeight(final int... heights) {
		return select(c -> {
			final int height = c.scrollHeight();
			for (final int h : heights) {
				if (height == h) {
					return true;
				}
			}
			return false;
		});
	}

	public Components id(final int... ids) {
		return select(component -> {
			final int compId = component.id();
			for (int id : ids) {
				if (compId == id) {
					return true;
				}
			}
			return false;
		});
	}

}