package org.powerbot.script.rs3;

public interface Stackable {
	public int getStackSize();

	public interface Query<T> {
		public int count();

		public int count(boolean stacks);
	}
}
