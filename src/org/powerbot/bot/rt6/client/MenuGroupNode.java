package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ReflectionEngine;

public class MenuGroupNode extends NodeSub {
	public MenuGroupNode(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public NodeSubQueue getItems(){
		return new NodeSubQueue(engine, engine.access(this));
	}

	public int getSize(){
		return engine.accessInt(this);
	}
}
