package org.powerbot.script.rt4;

import java.awt.Color;
import java.lang.ref.SoftReference;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.bot.rt4.client.PlayerComposite;

public class Player extends Actor {
	public static final Color TARGET_COLOR = new Color(255, 0, 0, 15);
	private final SoftReference<org.powerbot.bot.rt4.client.Player> player;
	private final int hash;

	Player(final ClientContext ctx, final org.powerbot.bot.rt4.client.Player player) {
		super(ctx);
		this.player = new SoftReference<org.powerbot.bot.rt4.client.Player>(player);
		hash = player != null ? System.identityHashCode(player) : -1;
	}

	@Override
	protected org.powerbot.bot.rt4.client.Actor getActor() {
		return player.get();
	}

	@Override
	public String name() {
		final org.powerbot.bot.rt4.client.Player player = this.player.get();
		final String str = player != null ? player.getName() : "";
		return str != null ? str : "";
	}

	@Override
	public int combatLevel() {
		final org.powerbot.bot.rt4.client.Player player = this.player.get();
		return player != null ? player.getCombatLevel() : -1;
	}

	public int team() {
		final org.powerbot.bot.rt4.client.Player player = this.player.get();
		return player != null ? player.getTeam() : -1;
	}

	public int[] appearance() {
		final org.powerbot.bot.rt4.client.Player player = this.player.get();
		final PlayerComposite composite = player != null ? player.getComposite() : null;
		final int[] arr = composite != null ? composite.getAppearance() : new int[0];
		return arr != null ? arr.clone() : new int[0];
	}

	@Override
	public boolean valid() {
		final Client client = ctx.client();
		final org.powerbot.bot.rt4.client.Player player = this.player.get();
		if (client == null || player == null) {
			return false;
		}
		final org.powerbot.bot.rt4.client.Player[] arr = client.getPlayers();
		for (final org.powerbot.bot.rt4.client.Player a : arr) {
			if (a == player) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (hash != -1) {
			return hash;
		}
		return super.hashCode();
	}

	@Override
	public String toString() {
		return String.format("%s[name=%s/level=%d/team=%d]",
				Player.class.getName(), name(), combatLevel(), team());
	}
}
