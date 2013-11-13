package org.powerbot.script.wrappers;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import org.powerbot.client.Client;
import org.powerbot.client.RSPlayer;
import org.powerbot.client.RSPlayerComposite;
import org.powerbot.script.methods.MethodContext;

public class Player extends Actor {
	public static final Color TARGET_COLOR = new Color(255, 0, 0, 15);
	private final WeakReference<RSPlayer> player;

	public Player(final MethodContext ctx, final RSPlayer player) {
		super(ctx);
		this.player = new WeakReference<RSPlayer>(player);
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

	public int[] getOverheads() {
		int[] arr = new int[0];
		int[] arr1 = getOverheadArray1();
		int[] arr2 = getOverheadArray2();
		if (arr1.length != arr2.length) {
			int c = 0;
			arr = new int[arr1.length];
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] == 440) {
					arr[c++] = arr2[i];
				}
			}
			arr = Arrays.copyOf(arr, c);
		}
		return arr;
	}

	private int[] getOverheadArray1() {
		final RSPlayer player = getAccessor();
		if (player != null) {
			final int[] arr = player.getOverheadArray1();
			if (arr != null) {
				return arr;
			}
		}
		return new int[0];
	}

	private int[] getOverheadArray2() {
		final RSPlayer player = getAccessor();
		if (player != null) {
			final int[] arr = player.getOverheadArray2();
			if (arr != null) {
				return arr;
			}
		}
		return new int[0];
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
		final Client client = ctx.getClient();
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
