package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class MenuGroupNode extends NodeSub {
	public MenuGroupNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public NodeSubQueue getItems(){
		return new NodeSubQueue(reflector, reflector.access(this));
	}

	public int getSize(){
		return reflector.accessInt(this);
	}
}
