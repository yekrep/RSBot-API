package org.powerbot.game;

import org.powerbot.asm.NodeProcessor;
import org.powerbot.concurrent.TaskHandler;
import org.powerbot.concurrent.TaskContainer;
import org.powerbot.game.loader.ClientStub;
import org.powerbot.game.loader.io.Crawler;
import org.powerbot.game.loader.io.PackEncryption;
import org.powerbot.game.loader.wrapper.Rs2Applet;
import org.powerbot.lang.AdaptException;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.HttpClient;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * A definition of a <code>GameEnvironment</code> that manages all the data associated with this environment.
 *
 * @author Timer
 */
public abstract class GameDefinition implements GameEnvironment {
	protected TaskContainer processor;
	private Map<String, byte[]> classes;

	public Crawler crawler;
	public Rs2Applet appletContainer;
	public Runnable callback;
	public ClientStub stub;
	protected String packHash;
	public ThreadGroup threadGroup;

	public GameDefinition() {
		this.threadGroup = new ThreadGroup("GameDefinition-" +hashCode());
		this.processor = new TaskHandler(this.threadGroup);
		this.classes = new HashMap<String, byte[]>();

		this.crawler = new Crawler();
		this.appletContainer = null;
		this.callback = null;
		this.stub = null;
		this.packHash = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean initializeEnvironment() {
		this.classes.clear();
		if (!crawler.crawl()) {
			return false;
		}
		byte[] loader = getLoader(crawler);
		if (loader != null) {
			String secretKeySpecKey = crawler.parameters.get("0");
			String ivParameterSpecKey = crawler.parameters.get("-1");
			if (secretKeySpecKey == null || ivParameterSpecKey == null) {
				return false;
			}
			byte[] secretKeySpecBytes = PackEncryption.toByte(secretKeySpecKey);
			byte[] ivParameterSpecBytes = PackEncryption.toByte(ivParameterSpecKey);
			Map<String, byte[]> classes = PackEncryption.extract(secretKeySpecBytes, ivParameterSpecBytes, loader);
			packHash = StringUtil.byteArrayToHexString(PackEncryption.inner_pack_hash);
			if (classes != null && classes.size() > 0) {
				this.classes.putAll(classes);
				classes.clear();
			}
			if (this.classes.size() > 0) {
				NodeProcessor nodeProcessor = getProcessor();
				if (nodeProcessor != null) {
					try {
						nodeProcessor.adapt();
					} catch (AdaptException e) {
						e.printStackTrace();
						return false;
					}
					for (Map.Entry<String, byte[]> clazz : this.classes.entrySet()) {
						String name = clazz.getKey();
						this.classes.put(name, nodeProcessor.process(name, clazz.getValue()));
					}
				}
				return true;
			}
		}
		return false;
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

	public Map<String, byte[]> classes() {
		Map<String, byte[]> classes = new HashMap<String, byte[]>();
		classes.putAll(this.classes);
		return classes;
	}
}
