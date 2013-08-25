package org.powerbot.script.methods;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.powerbot.script.golem.Antipattern;
import org.powerbot.script.golem.CameraPattern;
import org.powerbot.script.golem.ExaminePattern;

public class Antipatterns extends MethodProvider {
	private final CopyOnWriteArrayList<Antipattern> active;
	private final List<Antipattern> patterns;

	public Antipatterns(MethodContext ctx) {
		super(ctx);
		this.active = new CopyOnWriteArrayList<>();
		this.patterns = Arrays.asList(
				new CameraPattern(ctx),
				new ExaminePattern(ctx)
		);

		reset();
	}

	public void run(EnumSet<Antipattern.Preference> preferences) {
		for (Antipattern antipattern : active) {
			antipattern.run(preferences);
		}
	}

	public List<Antipattern> getAntipatterns() {
		return active;
	}

	public boolean add(Antipattern antipattern) {
		return active.addIfAbsent(antipattern);
	}

	public boolean remove(Antipattern antipattern) {
		return active.remove(antipattern);
	}

	public void reset() {
		active.retainAll(patterns);
		active.addAllAbsent(patterns);
	}
}
