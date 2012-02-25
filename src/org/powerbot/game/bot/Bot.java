package org.powerbot.game.bot;

import org.powerbot.Chrome;
import org.powerbot.asm.NodeProcessor;
import org.powerbot.game.GameDefinition;
import org.powerbot.game.client.Client;
import org.powerbot.game.loader.Loader;
import org.powerbot.game.loader.script.ModScript;
import org.powerbot.util.io.IOHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Bot extends GameDefinition {
	public BufferedImage image;
	private BufferedImage backBuffer;

	private Client client;

	private static final List<Bot> bots = new ArrayList<Bot>();

	public Bot() {
		Dimension d = new Dimension(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT);
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		client = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void startEnvironment() {
		bots.add(this);
		this.callback = new Runnable() {
			public void run() {
				//setClient((Client) appletContainer.clientInstance);
				appletContainer.paint(image.getGraphics());
				resize(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT);
			}
		};
		processor.submit(new Loader(this));
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeProcessor getProcessor() {
		return new ModScript(IOHelper.read(new File("C:\\Users\\Joe\\Desktop\\Bots\\Modscript_reparse\\out.ms")));
	}

	/**
	 * {@inheritDoc}
	 */
	public void killEnvironment() {
		if (stub != null) {
			stub.setActive(false);
		}
		if (appletContainer != null) {
			appletContainer.stop();
			appletContainer.destroy();
			appletContainer = null;
			stub = null;
		}
		bots.remove(this);
	}

	private void setClient(Client clientInstance) {
		this.client = clientInstance;
		this.client.setCallback(new CallbackImpl(this));
	}

	public void resize(int width, int height) {
		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		appletContainer.setSize(width, height);
		appletContainer.update(backBuffer.getGraphics());
		appletContainer.paint(backBuffer.getGraphics());
	}

	public Graphics getBufferGraphics() {
		Graphics back = backBuffer.getGraphics();
		back.setColor(Color.white);
		back.drawString("Hi WeiSu", 10, 50);
		back.dispose();
		image.getGraphics().drawImage(backBuffer, 0, 0, null);
		return backBuffer.getGraphics();
	}

	public static Bot getBot(Object o) {
		ClassLoader cl = o.getClass().getClassLoader();
		for (Bot bot : Bot.bots) {
			Component c = bot.appletContainer.getComponent(0);
			ClassLoader componentParent = c.getClass().getClassLoader();
			if (cl == componentParent) {
				return bot;
			}
		}
		return null;
	}
}
