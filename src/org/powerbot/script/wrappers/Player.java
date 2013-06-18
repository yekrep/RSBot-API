package org.powerbot.script.wrappers;

import org.powerbot.client.Client;
import org.powerbot.client.RSPlayer;
import org.powerbot.client.RSPlayerComposite;
import org.powerbot.script.methods.MethodContext;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.ref.WeakReference;
import java.util.Arrays;

public class Player extends Actor {
	public static final Color TARGET_COLOR = new Color(255, 0, 0, 15);
	private final WeakReference<RSPlayer> player;

	public Player(MethodContext ctx, final RSPlayer player) {
		super(ctx);
		this.player = new WeakReference<>(player);
	}

	@Override
	protected RSPlayer getAccessor() {
		return player.get();
	}

	@Override
	public String getName() {
		final RSPlayer player = getAccessor();
		return player != null ? player.getName() : "";
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
		return new int[0];
	}

	@Override
	public boolean isValid() {
		Client client = ctx.getClient();
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
		final Model m = getModel();
		if (m != null) {
			m.drawWireFrame(render);
		}
	}
}
