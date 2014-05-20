package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class Cache  extends ContextAccessor{
	public Cache(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public HashTable getTable(){
		return new HashTable(engine, engine.access(this));
	}
}
