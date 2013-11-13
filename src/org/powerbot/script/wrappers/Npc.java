package org.powerbot.script.wrappers;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.ref.WeakReference;

import org.powerbot.client.Client;
import org.powerbot.client.OverheadSprites;
import org.powerbot.client.RSNPC;
import org.powerbot.client.RSNPCDef;
import org.powerbot.client.RSNPCNode;
import org.powerbot.script.methods.MethodContext;

public class Npc extends Actor implements Identifiable {
	public static final Color TARGET_COLOR = new Color(255, 0, 255, 15);
	private final WeakReference<RSNPC> npc;

	public Npc(final MethodContext ctx, final RSNPC npc) {
		super(ctx);
		this.npc = new WeakReference<RSNPC>(npc);
	}

	@Override
	protected RSNPC getAccessor() {
		return npc.get();
	}

	@Override
	public String getName() {
		final RSNPC npc = getAccessor();
		final RSNPCDef def;
		return npc != null && (def = npc.getRSNPCDef()) != null ? def.getName() : "";
	}

	@Override
	public int getLevel() {
		final RSNPC npc = getAccessor();
		final RSNPCDef def;
		return npc != null && (def = npc.getRSNPCDef()) != null ? def.getLevel() : -1;
	}

	@Override
	public int getId() {
		final RSNPC npc = getAccessor();
		final RSNPCDef def;
		return npc != null && (def = npc.getRSNPCDef()) != null ? def.getID() : -1;
	}

	public String[] getActions() {
		final RSNPC npc = getAccessor();
		final RSNPCDef def;
		return npc != null && (def = npc.getRSNPCDef()) != null ? def.getActions() : new String[0];
	}

	public int[] getOverheadArray1() {
		final RSNPC npc = getAccessor();
		final OverheadSprites sprites = npc != null ? npc.getOverheadSprites() : null;
		final RSNPCDef def;
		if (sprites != null) {
			return sprites.getArray1();
		} else if (npc != null && (def = npc.getRSNPCDef()) != null) {
			return def.getOverheadArray1();
		}
		return new int[0];
	}

	public short[] getOverheadArray2() {
		final RSNPC npc = getAccessor();
		final OverheadSprites sprites = npc != null ? npc.getOverheadSprites() : null;
		final RSNPCDef def;
		if (sprites != null) {
			return sprites.getArray2();
		} else if (npc != null && (def = npc.getRSNPCDef()) != null) {
			return def.getOverheadArray2();
		}
		return new short[0];
	}

	@Override
	public boolean isValid() {
		final Client client = ctx.getClient();
		if (client == null) {
			return false;
		}
		final RSNPC npc = getAccessor();
		if (npc != null) {
			final int[] indices = client.getRSNPCIndexArray();
			final org.powerbot.client.HashTable npcTable = client.getRSNPCNC();
			for (final int index : indices) {
				Object node = ctx.game.lookup(npcTable, index);
				if (node == null) {
					continue;
				}
				if (node instanceof RSNPCNode) {
					node = ((RSNPCNode) node).getRSNPC();
				}
				if (node instanceof RSNPC) {
					if (node.equals(npc)) {
						return true;
					}
				}
			}
		}
		return false;
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

	@Override
	public String toString() {
		return Npc.class.getSimpleName() + "[id=" + getId() + ", name=" + getName() + ", level=" + getLevel() + "]";
	}
}
