package org.powerbot.core.script.wrappers;

public interface Interactive extends Targetable {
	public boolean hover();

	public boolean click(boolean left);

	public boolean interact(String action);

	public boolean interact(String action, String option);
}
