package org.powerbot.ipc;

import java.io.Serializable;

/**
 * @author Paris
 */
public final class Message implements Serializable {
	private static final long serialVersionUID = 5767516607322553568L;
	private final boolean response;
	private int type;
	private Object[] args;

	private static final int BASE = 0xfa43da23;
	public static final int NONE = BASE + 10;
	public static final int ALIVE = BASE + 20;
	public static final int DIE = BASE + 30;
	public static final int MODE = BASE + 40;
	public static final int RUNNING = BASE + 50;
	public static final int LISTENING = BASE + 60;
	public static final int LOADED = BASE + 70;
	public static final int SESSION = BASE + 80;
	public static final int SIGNIN = BASE + 90;
	public static final int SIGNIN_SESSION = BASE + 95;
	public static final int SCRIPT = BASE + 100;

	public Message(final int type) {
		this(false, type);
	}

	public Message(final boolean response, final int type) {
		this(response, type, new Object[]{});
	}

	public Message(final boolean response, final int type, final Object... args) {
		this.response = response;
		this.type = type;
		this.args = args;
	}

	public boolean isResponse() {
		return response;
	}

	public void setMessageType(final int type) {
		this.type = type;
	}

	public int getMessageType() {
		return type;
	}

	public void setArgs(final Object... args) {
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

	public int getIntArg() {
		return args.length > 0 && args[0] instanceof Integer ? (Integer) args[0] : -1;
	}

	public long getLongArg() {
		return args.length > 0 && args[0] instanceof Long ? (Long) args[0] : -1;
	}

	public String getStringArg() {
		return args.length > 0 && args[0] instanceof String ? (String) args[0] : null;
	}
}
