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
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.powerbot.core.Bot;
import org.powerbot.core.bot.handlers.ScriptHandler;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.controller.BotInteract;
import org.powerbot.service.NetworkAccount;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;

/**
 * @author Paris
 */
public final class Controller implements Runnable {
	private final static Logger log = Logger.getLogger(Controller.class.getName());
	private static Controller instance;
	private final DatagramSocket sock;
	private final List<Event> callbacks;
	private final ExecutorService executor;
	public static final int MAX_INSTANCES = 24;
	private final int[] ports = new int[MAX_INSTANCES + 1];
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
		executor = Executors.newCachedThreadPool();
		StringUtil.getBytesUtf8(Long.toBinaryString(Configuration.getUID()) + Integer.toHexString(Configuration.VERSION));
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
		if (executor.isShutdown()) {
			return;
		}

		while (true) {
			boolean stop = false;
			byte[] buf = new byte[4096];
			final DatagramPacket packet = new DatagramPacket(buf, buf.length);

			try {
				sock.receive(packet);
				packet.setLength(buf.length);
				final ObjectInput in = new ObjectInputStream(new InflaterInputStream(new ByteArrayInputStream(packet.getData()), new Inflater(true)));
				final Message msg = (Message) in.readObject();
				in.close();

				log.fine("[" + sock.getLocalPort() + "] received from: " + packet.getSocketAddress().toString() + " " + (msg.isResponse() ? "response" : "broadcast") + " " + msg.getMessageType());

				if (msg.isResponse()) {
					executor.execute(new Callbacks(msg, packet.getSocketAddress(), callbacks));
					continue;
				}

				Message reply = new Message(true, msg.getMessageType());

				switch (msg.getMessageType()) {
				case Message.NONE:
					reply = null;
					break;

				case Message.ALIVE:
					reply.setArgs(true);
					break;

				case Message.DIE:
					stop = true;
					BotChrome.getInstance().windowClosing(null);
					break;

				case Message.MODE:
					reply.setArgs(Configuration.SUPERDEV ? 2 : 0);
					break;

				case Message.LISTENING:
					reply.setArgs(sock.getLocalPort());
					break;

				case Message.LOADED:
					reply.setArgs(BotChrome.loaded ? 1 : 2);
					break;

				case Message.SESSION:
					reply.setArgs(ScheduledChecks.SESSION_TIME);
					break;

				case Message.SCRIPT:
					final ConcurrentLinkedQueue<String> list = new ConcurrentLinkedQueue<String>();
					if (Bot.instantiated()) {
						final ScriptHandler script = Bot.instance().getScriptHandler();
						if (script != null && script.isActive()) {
							final ScriptDefinition def = script.getDefinition();
							if (def != null && def.getID() != null && !def.getID().isEmpty()) {
								list.add(def.getID());
							}
						}
					}
					reply.setArgs(list.toArray());
					break;

				case Message.SIGNIN:
					reply = null;
					NetworkAccount.getInstance().revalidate();
					BotChrome.getInstance().panel.loadingPanel.setAdVisible(!NetworkAccount.getInstance().hasPermission(NetworkAccount.VIP));
					BotInteract.tabClose(true);
					break;

				default:
					break;
				}

				if (reply != null) {
					send(reply, packet.getAddress(), packet.getPort());
				}
			} catch (final IOException ignored) {
				continue;
			} catch (final ClassNotFoundException ignored) {
				continue;
			}

			if (stop || sock.isClosed() || !sock.isBound()) {
				break;
			}
		}

		executor.shutdown();
	}

	private void send(final Message msg, final InetAddress addr, final int port) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final DeflaterOutputStream dos = new DeflaterOutputStream(bos, new Deflater(1, true));
		final ObjectOutput out = new ObjectOutputStream(dos);
		out.writeObject(msg);
		dos.finish();
		out.close();
		final byte[] buf = bos.toByteArray();
		sock.send(new DatagramPacket(buf, buf.length, addr, port));
	}

	public void broadcast(final Message msg) {
		final List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < ports.length; i++) {
			if (ports[i] == sock.getLocalPort()) {
				continue;
			}
			try {
				new DatagramSocket(ports[i], InetAddress.getLocalHost()).close();
			} catch (final IOException ignored) {
				list.add(ports[i]);
			}
		}
		if (list.isEmpty()) {
			return;
		}
		final AtomicInteger n = new AtomicInteger(list.size());
		final Event e = new Event() {
			@Override
			public boolean call(final Message rmsg, SocketAddress sender) {
				if (msg.getMessageType() == rmsg.getMessageType()) {
					n.decrementAndGet();
				}
				return false;
			}
		};
		callbacks.add(e);
		for (final int port : list) {
			try {
				send(msg, InetAddress.getLocalHost(), port);
			} catch (final IOException ignored) {
			}
		}
		Thread.yield();
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
		final int type = Message.RUNNING;
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
		final int type = Message.MODE;
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
		final int type = Message.LOADED;
		final AtomicBoolean n = new AtomicBoolean(false);
		final Event c = new Event() {
			@Override
			public boolean call(Message msg, final SocketAddress sender) {
				if (msg.getMessageType() == type && msg.getIntArg() != 1) {
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
		final int type = Message.SESSION;
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

	public Collection<String> getRunningScripts() {
		final int type = Message.SCRIPT;
		final ConcurrentLinkedQueue<String> list = new ConcurrentLinkedQueue<String>();
		final Event c = new Event() {
			@Override
			public boolean call(Message msg, final SocketAddress sender) {
				if (msg.getMessageType() == type) {
					for (final Object def : msg.getArgs()) {
						list.add((String) def);
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

	private final class Callbacks implements Runnable {
		private final Message msg;
		private final SocketAddress sender;
		private final Iterable<Event> tasks;

		public Callbacks(final Message msg, final SocketAddress sender, final Iterable<Event> tasks) {
			this.msg = msg;
			this.sender = sender;
			this.tasks = tasks;
		}

		public void run() {
			for (final Event task : tasks) {
				try {
					if (!task.call(msg, sender)) {
						break;
					}
				} catch (final Exception ignored) {
				}
			}
		}
	}
}
