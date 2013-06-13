package org.powerbot.script.lang;

import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.wrappers.Identifiable;
import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Nameable;

public abstract class GameDualQuery<K extends Locatable & Identifiable & Nameable> extends AbstractQuery<GameDualQuery<K>, K> {
	public GameDualQuery(final ClientFactory factory) {
		super(factory);
	}

	@Override
	protected GameDualQuery<K> getThis() {
		return this;
	}

	public GameDualQuery<K> at(Locatable l) {
		return select(new Locatable.Matcher(l));
	}

	public GameDualQuery<K> within(double distance) {
		return within(ctx.players.getLocal(), distance);
	}

	public GameDualQuery<K> within(Locatable target, double distance) {
		return select(new Locatable.WithinRange(target, distance));
	}

	public GameDualQuery<K> nearest() {
		return nearest(ctx.players.getLocal());
	}

	public GameDualQuery<K> nearest(Locatable target) {
		return sort(new Locatable.NearestTo(target));
	}

	public GameDualQuery<K> id(int... ids) {
		return select(new Identifiable.Matcher(ids));
	}

	public GameDualQuery<K> id(Identifiable[] identifiables) {
		return select(new Identifiable.Matcher(identifiables));
	}
}
