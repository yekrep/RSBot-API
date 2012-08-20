package org.powerbot.ipc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import javax.crypto.Cipher;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.bot.Bot;
import org.powerbot.gui.BotChrome;
import org.powerbot.ipc.Message.MessageType;
import org.powerbot.service.NetworkAccount;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.XORInputStream;
import org.powerbot.util.io.XOROutputStream;

/**
 * @author Paris
 */
public final class Controller implements Runnable {
	private final static Logger log = Logger.getLogger(Controller.class.getName());
	private static Controller instance;
	private final DatagramSocket sock;
	private final List<Event> callbacks;
	public static final int MAX_INSTANCES = 8;
	private final int[] ports = new int[MAX_INSTANCES];
	private final byte[] key;
	public final int instanceID;
	private static final int RESPONSE_TIMEOUT = 1000;

	private Controller() throws IOException {
		for (int i = 0; i < ports.length; i++) {
			ports[i] = 43600 + i;
		}

		DatagramSocket sock = null;
		int instanceID = -1;
		for (int i = 0; i < ports.length; i++) {
			if ((sock = getIfAvailable(ports[i])) != null) {
				instanceID = i;
				break;
			}
		}
		this.sock = sock;
		this.instanceID = instanceID;

		if (this.sock == null) {
			throw new IOException();
		}

		callbacks = new ArrayList<Event>();
		key = StringUtil.getBytesUtf8(Long.toBinaryString(Configuration.getUID()) + Integer.toHexString(Configuration.VERSION));
	}

	public static Controller getInstance() {
		if (instance == null) {
			try {
				instance = new Controller();
				new Thread(instance).start();
			} catch (final IOException ignored) {
				ignored.printStackTrace();
			}
		}
		return instance;
	}

	private static DatagramSocket getIfAvailable(int port) {
	    DatagramSocket sock = null;
	    try {
	        sock = new DatagramSocket(port, InetAddress.getLocalHost());
	    } catch (IOException ignored) {
	    }
	    return sock;
	}

	public boolean isBound() {
		return sock != null && sock.isBound();
	}

	@Override
	public void run() {
		while (true) {
			boolean stop = false;
			byte[] buf = new byte[4096];
			final DatagramPacket packet = new DatagramPacket(buf, buf.length);

			try {
				sock.receive(packet);
				final ObjectInput in = new ObjectInputStream(new XORInputStream(new ByteArrayInputStream(packet.getData()), key, Cipher.DECRYPT_MODE));
				final Message msg = (Message) in.readObject();
				in.close();

				log.fine("[" + sock.getLocalPort() + "] received from: " + packet.getSocketAddress().toString() + " " + (msg.isResponse() ? "response" : "broadcast") + " " + msg.getMessageType());

				Message reply = new Message(true, msg.getMessageType());

				switch (msg.getMessageType()) {
				case NONE:
					reply = null;
					break;

				case ALIVE:
					reply.setArgs(true);
					break;

				case DIE:
					stop = true;
					BotChrome.getInstance().windowClosing(null);
					break;

				case MODE:
					reply.setArgs(Configuration.SUPERDEV ? 2 : Configuration.DEVMODE ? 1 : 0);
					break;

				case LISTENING:
					reply.setArgs(sock.getLocalPort());
					break;

				case LOADED:
					reply.setArgs(BotChrome.loaded ? 1 : 2);
					break;

				case SESSION:
					reply.setArgs(ScheduledChecks.SESSION_TIME);
					break;

				case SCRIPT:
					final ConcurrentLinkedQueue<ScriptDefinition> list = new ConcurrentLinkedQueue<ScriptDefinition>();
					for (final Bot bot : Collections.unmodifiableList(Bot.bots)) {
						final ActiveScript script = bot.getActiveScript();
						if (script != null && !script.getContainer().isShutdown()) {
							final ScriptDefinition def = script.getDefinition();
							if (def != null) {
								list.add(def);
							}
						}
					}
					reply.setArgs(list.toArray());
					break;

				case SIGNIN:
					reply = null;
					NetworkAccount.getInstance().revalidate();
					BotChrome.getInstance().panel.loadingPanel.setAdVisible(!NetworkAccount.getInstance().isVIP());
					BotChrome.getInstance().toolbar.closeInactiveTabs();
					break;
				}

				if (msg.isResponse()) {
					for (final Event task : callbacks) {
						try {
							if (!task.call(reply, packet.getSocketAddress())) {
								break;
							}
						} catch (final Exception ignored) {
							continue;
						}
					}
				} else if (reply != null) {
					reply(reply, packet.getAddress(), packet.getPort());
				}

				packet.setLength(buf.length);
			} catch (final IOException ignored) {
				continue;
			} catch (final ClassNotFoundException ignored) {
				continue;
			}

			if (stop || sock.isClosed() || !sock.isBound()) {
				break;
			}
		}
	}

	private void reply(final Message msg, final InetAddress addr, final int port) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final ObjectOutput out = new ObjectOutputStream(new XOROutputStream(bos, key, Cipher.ENCRYPT_MODE));
		out.writeObject(msg);
		final byte[] buf = bos.toByteArray();
		bos.close();
		sock.send(new DatagramPacket(buf, buf.length, addr, port));
	}

	public void broadcast(final Message msg) {
		final AtomicInteger n = new AtomicInteger(0);
		for (int i = 0; i < ports.length; i++) {
			try {
				new DatagramSocket(ports[i], InetAddress.getLocalHost()).close();
			} catch (final Exception ignored) {
				n.incrementAndGet();
			}
		}
		for (final int port : ports) {
			if (port != sock.getLocalPort()) {
				try {
					reply(msg, InetAddress.getLocalHost(), port);
				} catch (final IOException ignored) {
				}
			}
		}
		final Event e = new Event() {
			@Override
			public boolean call(final Message rmsg, SocketAddress sender) {
				if (msg == rmsg) {
					n.decrementAndGet();
				}
				return true;
			}
		};
		callbacks.add(e);
		final long mark = System.currentTimeMillis() + RESPONSE_TIMEOUT;
		while (n.get() > 0 && System.currentTimeMillis() < mark) {
			try {
				Thread.sleep(0);
			} catch (final InterruptedException ignored) {
			}
		}
		callbacks.remove(e);
	}

	public int getRunningInstances() {
		final MessageType type = MessageType.RUNNING;
		final AtomicInteger i = new AtomicInteger(1);
		final Event c = new Event() {
			@Override
			public boolean call(final Message msg, final SocketAddress sender) {
				if (msg.getMessageType() == type) {
					i.incrementAndGet();
					return false;
				}
				return true;
			}
		};
		callbacks.add(c);
		broadcast(new Message(type));
		callbacks.remove(c);
		return i.get();
	}

	public Collection<Integer> getRunningModes() {
		final MessageType type = MessageType.MODE;
		final ConcurrentLinkedQueue<Integer> list = new ConcurrentLinkedQueue<Integer>();
		final Event c = new Event() {
			@Override
			public boolean call(final Message msg, final SocketAddress sender) {
				if (msg.getMessageType() == type) {
					list.add(msg.getIntArg());
					return false;
				}
				return true;
			}
		};
		callbacks.add(c);
		broadcast(new Message(type));
		callbacks.remove(c);
		return list;
	}

	public boolean isAnotherInstanceLoading() {
		final MessageType type = MessageType.LOADED;
		final AtomicBoolean n = new AtomicBoolean(false);
		final Event c = new Event() {
			@Override
			public boolean call(Message msg, final SocketAddress sender) {
				if (msg.getMessageType() == type && msg.getIntArg() == 2) {
					n.set(true);
					return false;
				}
				return true;
			}
		};
		callbacks.add(c);
		broadcast(new Message(type));
		callbacks.remove(c);
		return n.get();
	}

	public long getLastSessionUpdateTime() {
		final MessageType type = MessageType.SESSION;
		final AtomicLong l = new AtomicLong(0);
		final Event c = new Event() {
			@Override
			public boolean call(Message msg, final SocketAddress sender) {
				if (msg.getMessageType() == type) {
					synchronized (l) {
						final long a = msg.getLongArg();
						if (a > l.get()) {
							l.set(a);
						}
					}
					return false;
				}
				return true;
			}
		};
		callbacks.add(c);
		broadcast(new Message(type));
		callbacks.remove(c);
		return l.get();
	}

	public Collection<ScriptDefinition> getRunningScripts() {
		final MessageType type = MessageType.SCRIPT;
		final ConcurrentLinkedQueue<ScriptDefinition> list = new ConcurrentLinkedQueue<ScriptDefinition>();
		final Event c = new Event() {
			@Override
			public boolean call(Message msg, final SocketAddress sender) {
				if (msg.getMessageType() == type) {
					for (final Object def : msg.getArgs()) {
						list.add((ScriptDefinition) def);
					}
					return false;
				}
				return true;
			}
		};
		callbacks.add(c);
		broadcast(new Message(type));
		callbacks.remove(c);
		return list;
	}
}
