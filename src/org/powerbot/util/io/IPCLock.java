package org.powerbot.util.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.util.Random;

import org.powerbot.Configuration;

/**
 * @author Paris
 */
public class IPCLock {
	private static IPCLock instance;
	private final static int SIZE = 8192;
	private final File file;

	private IPCLock() {
		file = new File(Configuration.TEMP, CryptFile.getHashedName("ipc.lock"));

		if (!file.isFile() || file.length() < SIZE) {
			try (final FileOutputStream out = new FileOutputStream(file)) {
				final Random r = new Random();
				final byte[] b = new byte[(int) (SIZE * (1 + r.nextInt(3) + r.nextDouble()))];
				r.nextBytes(b);
				out.write(b);
			} catch (final IOException ignored) {
			}
		}
	}

	public static synchronized IPCLock getInstance() {
		if (instance == null) {
			instance = new IPCLock();
		}
		return instance;
	}

	public FileLock getLock(final int id) {
		for (; ; ) {
			try {
				return new RandomAccessFile(file, "rwd").getChannel().tryLock(SIZE % id, 1, false);
			} catch (final IOException ignored) {
			}
			try {
				Thread.sleep(60);
			} catch (final InterruptedException ignored) {
			}
		}
	}

	public void release(final FileLock lock) {
		try (final Channel channel = lock.acquiredBy()) {
			lock.release();
		} catch (final IOException ignored) {
		}
	}
}
