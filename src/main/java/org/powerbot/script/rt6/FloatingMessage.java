package org.powerbot.script.rt6;

import org.powerbot.script.Validatable;

/**
 * FloatingMessage
 */
public class FloatingMessage implements Validatable {
	@Deprecated
	public static final int TEXTURE_INFO = Constants.FLOATINGMESSAGE_INFO;
	@Deprecated
	public static final int TEXTURE_WARNING = Constants.FLOATINGMESSAGE_WARNING;

	private final String text;
	private final int texture;

	public FloatingMessage(final String text, final int texture) {
		this.text = text;
		this.texture = texture;
	}

	public String text() {
		return text;
	}

	public int texture() {
		return texture;
	}

	@Override
	public boolean valid() {
		return !(text.isEmpty() || texture == -1);
	}
}
