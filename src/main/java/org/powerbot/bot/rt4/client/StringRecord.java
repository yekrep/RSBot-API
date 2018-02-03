package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class StringRecord extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public StringRecord(final Reflector engine, final Object parent) {
		super(engine, parent);
  }

	public String getValue() {
		return reflector.accessString(this, a);
	}
}
