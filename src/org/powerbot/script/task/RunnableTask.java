package org.powerbot.script.task;

/**
 * A {@link org.powerbot.script.task.Task} version of {@link java.lang.Runnable}
 *
 * @author Paris
 */
public abstract class RunnableTask implements Task, Runnable {

    @Override
    public boolean isValid() {
        return true;
    }
}
