package org.powerbot.script.internal;

import org.powerbot.script.Script;

/**
 * @author Timer
 */
public class ScriptHandler {
	private Script script;

	public ScriptHandler() {
		this.script = null;
	}

	public boolean start(final Script script) {
		if (isActive()) {
			return false;
		}
		script.start();
		return true;
	}

	public void pause() {
		if (script != null) {
			script.setPaused(true);
		}
	}

	public void resume() {
		if (script != null) {
			script.setPaused(false);
		}
	}

	public void shutdown() {
		if (script != null) {
			script.shutdown();
		}
	}

	public void stop() {
		if (script != null) {
			script.stop();
		}
	}

	public boolean isPaused() {
		return script.isPaused();
	}

	public boolean isActive() {
		return script != null && script.isActive();
	}

	public boolean isShutdown() {
		return script != null && script.isShutdown();
	}

	public void reset() {
		script = null;
	}
}
