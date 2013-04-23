package org.powerbot.script.xenon;

import java.awt.event.KeyEvent;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.script.xenon.util.Delay;
import org.powerbot.script.xenon.util.Timer;

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

	public static int setPitch(final int pitch) {
		int p = getPitch();
		if (p == pitch) return 0;
		final boolean up = pitch > p;
		Keyboard.pressKey(up ? KeyEvent.VK_UP : KeyEvent.VK_DOWN);
		int curr;
		final Timer timer = new Timer(100);
		while (timer.isRunning()) {
			curr = getPitch();
			if (curr != p) {
				p = curr;
				timer.reset();
			}
			if (up && curr >= pitch) break;
			else if (!up && curr <= pitch) break;

			Delay.sleep(5, 10);
		}
		Keyboard.releaseKey(up ? KeyEvent.VK_UP : KeyEvent.VK_DOWN);
		return p - pitch;
	}
}
