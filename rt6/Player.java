package org.powerbot.script.rt6;

import java.awt.Color;

import org.powerbot.bot.rt6.client.Client;

/**
 * Player
 */
public class Player extends Actor {
	public static final Color TARGET_COLOR = new Color(255, 0, 0, 15);
	private final org.powerbot.bot.rt6.client.Player player;

	public Player(final ClientContext ctx, final org.powerbot.bot.rt6.client.Player player) {
		super(ctx);
		this.player = player;
	}

	@Override
	protected org.powerbot.bot.rt6.client.Player getAccessor() {
		return player;
	}

	@Override
	public String name() {
		final String n = player.getName();
		return n != null ? n : "";
	}

	@Override
	public int combatLevel() {
		return player.getCombatLevel();
	}

	public int team() {
		return player.getTeam();
	}

	public int prayerIcon() {
		final int[] a1 = getOverheadArray1(), a2 = getOverheadArray2();
		final int len = a1.length;
		if (len != a2.length) {
			return -1;
		}

		for (int i = 0; i < len; i++) {
			if (a1[i] == 440) {
				return a2[i];
			}
		}
		return -1;
	}

	public int skullIcon() {
		return -1;
	}

	private int[] getOverheadArray1() {
		final int[] arr = player.getOverheadArray1();
		return arr != null ? arr : new int[0];
	}

	private int[] getOverheadArray2() {
		final int[] arr = player.getOverheadArray2();
		return arr != null ? arr : new int[0];
	}

	public int npcId() {
		return player.getComposite().getNpcId();
	}

	public int[] appearance() {
		final int[] arr = player.getComposite().getAppearance();
		if (arr == null) {
			return new int[0];
		}

		final int[] appearance = arr.clone();
		for (int i = 0; i < appearance.length; i++) {
			if ((appearance[i] & 0x40000000) > 0) {
				appearance[i] &= 0x3fffffff;
			} else {
				appearance[i] = -1;
			}
		}
		return appearance;
	}

	@Override
	public boolean valid() {
		final Client client = ctx.client();
		final org.powerbot.bot.rt6.client.Player character = getAccessor();
		if (client == null || character.isNull()) {
			return false;
		}
		final org.powerbot.bot.rt6.client.Player[] players = client.getPlayers();
		for (final org.powerbot.bot.rt6.client.Player p : players) {
			if (character.equals(p)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return Player.class.getSimpleName() + "[name=" + name() + ",level=" + combatLevel() + "]";
	}
}
