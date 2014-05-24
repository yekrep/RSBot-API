package org.powerbot.script.rt6;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.RSPlayer;

public class Player extends Actor {
	public static final Color TARGET_COLOR = new Color(255, 0, 0, 15);
	private final RSPlayer player;

	public Player(final ClientContext ctx, final RSPlayer player) {
		super(ctx);
		this.player = player;
	}

	@Override
	protected RSPlayer getAccessor() {
		return player;
	}

	@Override
	public String name() {
		final String n = player.getName();
		return n != null ? n : "";
	}

	@Override
	public int combatLevel() {
		return player.getLevel();
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
		return player.getComposite().getNPCID();
	}

	public int[] appearance() {
		final int[] arr = player.getComposite().getEquipment();
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
		if (client == null) {
			return false;
		}
		final RSPlayer character = getAccessor();
		final RSPlayer[] players = client.getRSPlayerArray();
		return character != null && players != null && Arrays.asList(players).contains(character);
	}

	@Override
	public void draw(final Graphics render) {
		draw(render, 15);
	}

	@Override
	public void draw(final Graphics render, final int alpha) {
		Color c = TARGET_COLOR;
		final int rgb = c.getRGB();
		if (((rgb >> 24) & 0xff) != alpha) {
			c = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, alpha);
		}
		render.setColor(c);
		final BoundingModel m2 = boundingModel.get();
		if (m2 != null) {
			m2.drawWireFrame(render);
		}
	}

	@Override
	public String toString() {
		return Player.class.getSimpleName() + "[name=" + name() + ",level=" + combatLevel() + "]";
	}
}
