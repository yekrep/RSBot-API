package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class Cache  extends ContextAccessor{
	public Cache(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public HashTable getTable(){
		return new HashTable(engine, engine.access(this));
	}
}
