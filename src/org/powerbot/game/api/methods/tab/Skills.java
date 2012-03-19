package org.powerbot.game.api.methods.tab;

import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.Client;

/**
 * @author Timer
 */
public class Skills {
	public static int[] getLevels() {
		final Client client = Bot.resolve().client;
		return client.getSkillLevels();
	}

	public static int[] getMaxLevels() {
		final Client client = Bot.resolve().client;
		return client.getSkillLevelMaxes();
	}

	public static int[] getExperiences() {
		final Client client = Bot.resolve().client;
		return client.getSkillExperiences();
	}

	public static int[] getMaxExperiences() {
		final Client client = Bot.resolve().client;
		return client.getSkillExperienceMaxes();
	}
}
