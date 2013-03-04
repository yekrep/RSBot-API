package org.powerbot.script.xenon.wrappers;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSPlayer;
import org.powerbot.game.client.RSPlayerComposite;

public class Player extends Character {
	private final WeakReference<RSPlayer> player;

	public Player(final RSPlayer player) {
		this.player = new WeakReference<>(player);
	}

	@Override
	protected RSPlayer getAccessor() {
		return player.get();
	}

	@Override
	public String getName() {
		final RSPlayer player = getAccessor();
		return player != null ? player.getName() : null;
	}

	@Override
	public int getLevel() {
		final RSPlayer player = getAccessor();
		return player != null ? player.getLevel() : -1;
	}

	public int getTeam() {
		final RSPlayer player = getAccessor();
		return player != null ? player.getTeam() : -1;
	}

	public int getPrayerIcon() {
		final RSPlayer player = getAccessor();
		return player != null ? player.getPrayerIcon() : -1;
	}

	public int getSkullIcon() {
		final RSPlayer player = getAccessor();
		return player != null ? player.getSkullIcon() : -1;
	}

	public int getNpcId() {
		final RSPlayer player = getAccessor();
		final RSPlayerComposite composite;
		return player != null && (composite = player.getComposite()) != null ? composite.getNPCID() : -1;
	}

	public int[] getAppearance() {
		final RSPlayer player = getAccessor();
		final RSPlayerComposite composite = player != null ? player.getComposite() : null;
		if (composite != null) {
			final int[] appearance = composite.getEquipment().clone();
			for (int i = 0; i < appearance.length; i++) {
				if ((appearance[i] & 0x40000000) > 0) {
					appearance[i] &= 0x3fffffff;
				} else {
					appearance[i] = -1;
				}
			}
			return appearance;
		}
		return null;
	}

	@Override
	public boolean isValid() {
		final Client client = Bot.client();
		if (client == null) return false;
		final RSPlayer character = getAccessor();
		final RSPlayer[] players = client.getRSPlayerArray();
		return character != null && players != null && Arrays.asList(players).contains(character);
	}
}
