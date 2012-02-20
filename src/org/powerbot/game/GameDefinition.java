package org.powerbot.game;

import org.powerbot.concurrent.ContainedTask;
import org.powerbot.concurrent.TaskContainer;
import org.powerbot.concurrent.TaskProcessor;
import org.powerbot.game.loader.Crawler;
import org.powerbot.game.loader.PackEncryption;
import org.powerbot.util.io.HttpClient;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * A definition of a <code>GameEnvironment</code> that manages all the data associated with this environment.
 *
 * @author Timer
 */
public class GameDefinition implements GameEnvironment {
	protected TaskProcessor processor;
	public Map<String, byte[]> classes;

	public GameDefinition() {
		this.processor = new TaskContainer();
		this.classes = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Future<Object> initializeEnvironment() {
		ContainedTask loader = new ContainedTask() {
			public Object call() {
				Crawler crawler = new Crawler();
				if (!crawler.crawl()) {
					return null;
				}
				byte[] loader = getLoader(crawler);
				if (loader != null) {
					String secretKeySpecKey = crawler.parameters.get("0");
					String ivParameterSpecKey = crawler.parameters.get("-1");
					if (secretKeySpecKey == null || ivParameterSpecKey == null) {
						return null;
					}
					byte[] secretKeySpecBytes = PackEncryption.toByte(secretKeySpecKey);
					byte[] ivParameterSpecBytes = PackEncryption.toByte(ivParameterSpecKey);
					Map<String, byte[]> classes = PackEncryption.extract(secretKeySpecBytes, ivParameterSpecBytes, loader);
					return classes == null || classes.size() == 0 ? null : classes;
				}
				return null;
			}
		};
		processor.submit(loader);
		return loader.future;
	}

	public static byte[] getLoader(Crawler crawler) {
		try {
			URLConnection clientConnection = HttpClient.getHttpConnection(new URL(crawler.archive));
			clientConnection.addRequestProperty("Referer", crawler.game);
			return HttpClient.downloadBinary(clientConnection);
		} catch (IOException ignored) {
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void killEnvironment() {
	}
}
