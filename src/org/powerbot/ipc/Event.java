package org.powerbot.ipc;

/**
 * @author Paris
 */
public interface Event<V> {
	public boolean call(V r, V s);
}
