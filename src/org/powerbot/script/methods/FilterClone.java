package org.powerbot.script.methods;

/**
 * @author Timer
 */
class FilterClone<T> extends Filtering<T> {
	private T[] list;

	public FilterClone(ClientFactory factory, T[] list) {
		super(factory);
		this.list = list;
	}

	@Override
	public T[] list() {
		return list.clone();
	}
}
