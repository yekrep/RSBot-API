package org.powerbot.script.rt6;

import org.powerbot.script.Validatable;

public class FloatingMessage implements Validatable {

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
