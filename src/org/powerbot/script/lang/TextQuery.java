package org.powerbot.script.lang;

import org.powerbot.script.tools.MethodContext;
import org.powerbot.script.tools.Textable;

public abstract class TextQuery<K extends Textable> extends AbstractQuery<TextQuery<K>, K>
		implements Textable.Query<TextQuery<K>> {
	public TextQuery(final MethodContext factory) {
		super(factory);
	}

	@Override
	protected TextQuery<K> getThis() {
		return this;
	}

	@Override
	public TextQuery<K> text(final String... texts) {
		return select(new Textable.Matcher(texts));
	}
}
