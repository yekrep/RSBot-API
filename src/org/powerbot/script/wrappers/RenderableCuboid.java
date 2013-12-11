package org.powerbot.script.wrappers;

import java.awt.Point;
import java.awt.geom.Area;
import java.lang.ref.WeakReference;

import org.powerbot.client.RSAnimable;
import org.powerbot.client.RSInteractable;
import org.powerbot.client.RSInteractableData;
import org.powerbot.client.RSInteractableLocation;
import org.powerbot.client.RSPlayer;
import org.powerbot.script.methods.MethodContext;

public class RenderableCuboid extends Interactive {
	private final WeakReference<RSInteractable> interactable;

	public RenderableCuboid(final MethodContext ctx, final RSInteractable interactable) {
		super(ctx);
		this.interactable = new WeakReference<RSInteractable>(interactable);
	}

	private Area cuboid() {
		final RSInteractable interactable = this.interactable.get();
		final RSInteractableData data;
		final RSInteractableLocation location;
		if (interactable == null || (data = interactable.getData()) == null ||
				(location = data.getLocation()) == null) {
			return null;
		}

		final int x = Math.round(location.getX()), z = Math.round(location.getY()),
				y = Math.round(location.getZ()), p = interactable.getPlane();
		final int h;
		if (interactable instanceof RSPlayer) {
			final RSPlayer player = (RSPlayer) interactable;
			h = player.getHeight();
		} else {
			h = 0;
		}
		if (ctx.game.groundToScreen(x, z, p, h / 2).x == -1) {
			return null;
		}
		int ldx = 256, rdx = 256;
		int ldz = 256, rdz = 256;
		if (interactable instanceof RSAnimable) {
			final int tx = x >> 9, ty = z >> 9;
			final RSAnimable animable = (RSAnimable) interactable;
			final int x1 = animable.getX1(), y1 = animable.getY1();
			final int x2 = animable.getX2(), y2 = animable.getY2();
			int b = Math.abs(x1 - tx);
			ldx += b * 512;
			b = Math.abs(x2 - tx);
			rdx += b * 512;
			b = Math.abs(y1 - ty);
			ldz += b * 512;
			b = Math.abs(y2 - ty);
			rdz += b * 512;
		}
		//TODO generate boundary polygons from ldx to rdx and ldz to rdz
		final Area a = new Area();
		//TODO: compress polygons
		return a;
	}

	@Override
	public Point getInteractPoint() {
		final Area area = cuboid();
		return null;//TODO: compute point
	}

	@Override
	public Point getNextPoint() {
		final Area area = cuboid();
		return null;//TODO: compute point
	}

	@Override
	public Point getCenterPoint() {
		final Area area = cuboid();
		return null;//TODO: compute point
	}

	@Override
	public boolean contains(Point point) {
		final Area area = cuboid();
		return area != null && area.contains(point);
	}
}
