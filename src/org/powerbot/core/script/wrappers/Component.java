package org.powerbot.core.script.wrappers;

import org.powerbot.game.client.RSInterface;

public class Component {
	private final Widget container;
	private final Component parent;
	private final int index;

	public Component(final Widget container, final int index) {
		this(container, null, index);
	}

	public Component(final Widget container, final Component parent, final int index) {
		this.container = container;
		this.parent = parent;
		this.index = index;
	}

	private RSInterface getInternalComponent() {
		RSInterface[] components;
		if (parent != null) {
			final RSInterface parentComponent = parent.getInternalComponent();
			components = parentComponent != null ? parentComponent.getComponents() : null;
		} else {
			components = container.getInternalComponents();
		}
		return components != null && index < components.length ? components[index] : null;
	}

}
