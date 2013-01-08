package org.powerbot.core.event.impl;

import java.awt.Graphics;

import org.powerbot.core.event.listeners.TextPaintListener;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.util.StringUtil;

public class TCamera implements TextPaintListener {
	@Override
	public int draw(int idx, final Graphics render) {
		StringUtil.drawLine(render, idx++,
				new StringBuilder("Camera: ").
						append("X: ").append(Camera.getX()).
						append(" Y: ").append(Camera.getY()).
						append(" Z: ").append(Camera.getZ()).
						append(" Yaw: ").append(Camera.getYaw()).
						append(" Pitch: ").append(Camera.getPitch())
						.toString()
		);
		return idx;
	}
}
