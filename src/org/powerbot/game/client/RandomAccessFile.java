package org.powerbot.game.client;

import java.io.File;
import java.io.FileNotFoundException;

public class RandomAccessFile extends java.io.RandomAccessFile {
	public RandomAccessFile(final File file, final String mode) throws FileNotFoundException {
		super(file, mode);
	}

	public RandomAccessFile(final String name, final String mode) throws FileNotFoundException {
		super(name, mode);
	}
}
