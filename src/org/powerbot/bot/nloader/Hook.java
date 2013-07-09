package org.powerbot.bot.nloader;

public class Hook {
	/**
	 * TARGET: defineClass
	 * class extends ClassLoader
	 * aload_0
	 * [
	 * aload (String name)
	 * aload (byte[] b)
	 * iload (int off)
	 * iload (int len)
	 * aload (ProtectionDomain protectionDomain)
	 * ]
	 * invokevirtual class/defineClass(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)Ljava/lang/Class;
	 * pop
	 */
}
