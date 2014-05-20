package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class PlayerMetaInfo extends ContextAccessor {
	public PlayerMetaInfo(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Skill[] getSkills() {
		final Object[] arr = engine.access(this, Object[].class);
		final Skill[] arr2 = arr != null ? new Skill[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Skill(engine, arr[i]);
			}
		}
		return arr2;
	}

	public Settings getSettings() {
		return new Settings(engine, engine.access(this));
	}
}
