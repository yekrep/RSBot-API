package org.powerbot.script.xenon;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;

public class Camera {
	public static int getX() {
		final Client client = Bot.client();
		return client != null ? client.getCamPosX() : -1;
	}

	public static int getY() {
		final Client client = Bot.client();
		return client != null ? client.getCamPosY() : -1;
	}

	public static int getZ() {
		final Client client = Bot.client();
		return client != null ? client.getCamPosZ() : -1;
	}

	public static int getYaw() {
		final Client client = Bot.client();
		return client != null ? (int) (client.getCameraYaw() / 45.51) : -1;
	}

	public static int getPitch() {
		final Client client = Bot.client();
		return client != null ? (int) ((client.getCameraPitch() - 1024) / 20.48) : -1;
	}

	public int setPitch(final int pitch) {
		int p = getPitch();
		if (p == pitch) return 0;
		final boolean up = pitch > p;
		//TODO set pitch
		return p - pitch;
	}
}
