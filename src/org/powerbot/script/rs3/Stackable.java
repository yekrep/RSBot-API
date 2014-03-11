package org.powerbot.script.rs3;

public interface Stackable {
	public int stackSize();

	public interface Query<T> {
		public int count();

		public int count(boolean stacks);
	}
}
