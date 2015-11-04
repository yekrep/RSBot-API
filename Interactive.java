package org.powerbot.script;

import java.awt.Point;

public interface Interactive extends Targetable, Validatable, Viewable, Drawable {
	Point centerPoint();

	boolean hover();

	boolean click();

	boolean click(boolean left);

	boolean click(int button);

	boolean click(String action);

	boolean click(String action, String option);

	boolean click(Filter<? super MenuCommand> c);

	boolean interact(String action);

	boolean interact(String action, String option);

	boolean interact(boolean auto, String action);

	boolean interact(boolean auto, String action, String option);

	boolean interact(Filter<? super MenuCommand> c);

	boolean interact(boolean auto, Filter<? super MenuCommand> c);

	void bounds(final int[] arr);
}
