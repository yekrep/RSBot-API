package org.powerbot.script;

public interface Stackable {
	public int stackSize();

	public interface Query<T> {
		public int count();

		public int count(boolean stacks);
	}
}
