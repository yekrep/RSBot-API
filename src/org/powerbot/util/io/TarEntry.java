package org.powerbot.util.io;

import org.powerbot.util.StringUtil;

import java.io.*;

public final class TarEntry {
	public static int BLOCKSIZE = 512;

	public String name = "", linked = "";
	public int mode = 100777, owner = 0, group = 0, checksum = 0;
	public long length, modified = System.currentTimeMillis();
	public byte type = 1;

	public TarEntry() {
	}

	public static TarEntry read(final InputStream is) throws IOException {
		final TarEntry entry = new TarEntry();
		final byte[] b100 = new byte[100], b8 = new byte[8], b12 = new byte[12], b1 = new byte[1];
		is.read(b100);
		entry.name = StringUtil.newStringUtf8(b100).trim();
		is.read(b8);
		entry.mode = Integer.parseInt(StringUtil.newStringUtf8(b8).trim());
		is.read(b8);
		entry.owner = Integer.parseInt(StringUtil.newStringUtf8(b8).trim());
		is.read(b8);
		entry.group = Integer.parseInt(StringUtil.newStringUtf8(b8).trim());
		is.read(b12);
		entry.length = Long.parseLong(StringUtil.newStringUtf8(b12).trim());
		is.read(b12);
		entry.modified = Long.parseLong(StringUtil.newStringUtf8(b12).trim());
		is.read(b8);
		entry.checksum = Integer.parseInt(StringUtil.newStringUtf8(b8).trim());
		is.read(b1);
		entry.type = Byte.parseByte(StringUtil.newStringUtf8(b8).trim());
		is.read(b100);
		entry.linked = StringUtil.newStringUtf8(b100).trim();
		return entry;
	}

	public static TarEntry read(final byte[] b) throws IOException {
		return read(new ByteArrayInputStream(b));
	}

	public void write(final OutputStream os) throws IOException {
		byte[] b;
		int l, p = 100;
		b = StringUtil.getBytesUtf8(name);
		os.write(b, 0, l = Math.min(p, b.length));
		b = new byte[p - l];
		os.write(b);
		p = 8;
		b = StringUtil.getBytesUtf8(Integer.toString(mode));
		os.write(b, 0, l = Math.min(p, b.length));
		b = new byte[p - l];
		os.write(b);
		b = StringUtil.getBytesUtf8(Integer.toString(owner));
		os.write(b, 0, l = Math.min(p, b.length));
		b = new byte[p - l];
		os.write(b);
		b = StringUtil.getBytesUtf8(Integer.toString(group));
		os.write(b, 0, l = Math.min(p, b.length));
		b = new byte[p - l];
		os.write(b);
		p = 12;
		b = StringUtil.getBytesUtf8(Long.toString(length));
		os.write(b, 0, l = Math.min(p, b.length));
		b = new byte[p - l];
		os.write(b);
		b = StringUtil.getBytesUtf8(Long.toString(modified));
		os.write(b, 0, l = Math.min(p, b.length));
		b = new byte[p - l];
		os.write(b);
		p = 8;
		b = StringUtil.getBytesUtf8(Integer.toString(checksum));
		os.write(b, 0, l = Math.min(p, b.length));
		b = new byte[p - l];
		os.write(b);
		p = 1;
		b = StringUtil.getBytesUtf8(Integer.toString(type));
		os.write(b, 0, l = Math.min(p, b.length));
		b = new byte[p - l];
		os.write(b);
		p = 100;
		b = StringUtil.getBytesUtf8(linked);
		os.write(b, 0, l = Math.min(p, b.length));
		b = new byte[p - l];
		os.write(b);
	}

	public byte[] getBytes() throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		write(bos);
		return bos.toByteArray();
	}
}
