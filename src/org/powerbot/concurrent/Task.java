package org.powerbot.concurrent;

import java.util.concurrent.Callable;

/**
 * A representation of a task submittable to a container for propagation, caching, and execution.
 *
 * @author Timer
 */
public interface Task extends Callable<Object> {
}
