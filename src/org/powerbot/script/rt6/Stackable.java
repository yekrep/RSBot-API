package org.powerbot.script.rt6;

public interface Stackable {
	public int stackSize();

	public interface Query<T> {
		public int count();

		public int count(boolean stacks);
	}
}
