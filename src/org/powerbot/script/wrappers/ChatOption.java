package org.powerbot.script.wrappers;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;

public class ChatOption extends MethodProvider implements Textable, Validatable {
	private int index;
	private Component option;

	public ChatOption(MethodContext ctx, int index, Component option) {
		super(ctx);
		this.index = index;
		this.option = option;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String getText() {
		if (option == null) {
			return "";
		}
		return option.getText();
	}

	public boolean select() {
		return select(false);
	}

	public boolean select(boolean key) {
		if (!isValid()) {
			return false;
		}
		if (key) {
			return ctx.keyboard.send(Integer.toString(index + 1));
		}
		return option.click();
	}

	@Override
	public boolean isValid() {
		return index >= 0 && index < 5 && option != null && option.isValid();
	}
}
