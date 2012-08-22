package org.powerbot.concurrent;

import java.util.concurrent.Callable;

/**
 * @deprecated
 * @see java.util.concurrent.Callable
 */
@Deprecated
public interface CallableTask<V> extends Callable<V> {
}