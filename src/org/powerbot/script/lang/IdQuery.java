package org.powerbot.script.lang;

import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.wrappers.Identifiable;

public abstract class IdQuery<K extends Identifiable> extends AbstractQuery<IdQuery<K>, K>
		implements Identifiable.Query<IdQuery<K>> {
	public IdQuery(final ClientFactory factory) {
		super(factory);
	}

	@Override
	protected IdQuery<K> getThis() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IdQuery<K> id(int... ids) {
		return select(new Identifiable.Matcher(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IdQuery<K> id(Identifiable... identifiables) {
		return select(new Identifiable.Matcher(identifiables));
	}
}
