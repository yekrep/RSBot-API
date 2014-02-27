package org.powerbot.script.rs3.tools;

public class FloatingMessage implements Validatable {
	public static final int TEXTURE_INFO = 8515;
	public static final int TEXTURE_WARNING = 8524;

	private final String text;
	private final int texture;

	public FloatingMessage(final String text, final int texture) {
		this.text = text;
		this.texture = texture;
	}

	public String getText() {
		return text;
	}

	public int getTexture() {
		return texture;
	}

	@Override
	public boolean isValid() {
		return !(text.isEmpty() || texture == -1);
	}
}
