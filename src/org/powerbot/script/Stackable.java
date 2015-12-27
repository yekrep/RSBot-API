package org.powerbot.script;

public interface Stackable {
	int stackSize();

	interface Query<T> {
		int count();

		int count(boolean stacks);
	}
}
