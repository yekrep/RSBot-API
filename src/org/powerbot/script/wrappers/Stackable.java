package org.powerbot.script.wrappers;

public interface Stackable {
	public int getStackSize();

	public interface Query<T> {
		public int count();

		public int count(boolean stacks);
	}
}
