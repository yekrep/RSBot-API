package org.powerbot.script.lang;

import org.powerbot.script.methods.ClientFactory;

/**
 * @author Paris
 */
public abstract class BasicQuery<K> extends AbstractQuery<BasicQuery<K>, K> {

	public BasicQuery(ClientFactory factory) {
		super(factory);
	}

	@Override
	public BasicQuery<K> getThis() {
		return this;
	}
}
