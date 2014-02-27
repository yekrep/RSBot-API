package org.powerbot.script.rs3.tools;

public interface Stackable {
	public int getStackSize();

	public interface Query<T> {
		public int count();

		public int count(boolean stacks);
	}
}
