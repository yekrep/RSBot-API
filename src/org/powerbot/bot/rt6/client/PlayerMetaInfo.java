package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class PlayerMetaInfo extends ContextAccessor {
	public PlayerMetaInfo(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public Skill[] getSkills() {
		return engine.access(this, Skill[].class);
	}

	public Settings getSettings() {
		return new Settings(engine, engine.access(this));
	}
}
