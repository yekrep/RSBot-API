package org.powerbot.script.rt6;

import org.powerbot.script.Validatable;

public class FloatingMessage implements Validatable {
	public static final int TEXTURE_INFO = 8515;
	public static final int TEXTURE_WARNING = 8524;

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
