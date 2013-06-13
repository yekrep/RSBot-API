package org.powerbot.script.lang;

import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.wrappers.Identifiable;
import org.powerbot.script.wrappers.Locatable;

public abstract class GameBasicQuery<K extends Locatable & Identifiable> extends AbstractQuery<GameBasicQuery<K>, K> {
	public GameBasicQuery(final ClientFactory factory) {
		super(factory);
	}

	@Override
	protected GameBasicQuery<K> getThis() {
		return this;
	}

	public GameBasicQuery<K> at(Locatable l) {
		return select(new Locatable.Matcher(l));
	}

	public GameBasicQuery<K> within(double distance) {
		return within(ctx.players.getLocal(), distance);
	}

	public GameBasicQuery<K> within(Locatable target, double distance) {
		return select(new Locatable.WithinRange(target, distance));
	}

	public GameBasicQuery<K> nearest() {
		return nearest(ctx.players.getLocal());
	}

	public GameBasicQuery<K> nearest(Locatable target) {
		return sort(new Locatable.NearestTo(target));
	}

	public GameBasicQuery<K> id(int... ids) {
		return select(new Identifiable.Matcher(ids));
	}

	public GameBasicQuery<K> id(Identifiable[] identifiables) {
		return select(new Identifiable.Matcher(identifiables));
	}
}
