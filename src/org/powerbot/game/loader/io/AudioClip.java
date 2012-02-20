package org.powerbot.game.loader.io;

import java.net.URL;

public class AudioClip implements java.applet.AudioClip {
	public static final short STATE_STOPPED = 0;
	public static final short STATE_PLAYING = 1;
	public static final short STATE_LOOPING = 2;
	private URL sourceURL;
	private short audioClipState;

	public AudioClip(URL sourceURL) {
		this.sourceURL = sourceURL;
		this.audioClipState = 0;
	}

	public short getAudioClipState() {
		return this.audioClipState;
	}

	public URL getURL() {
		return this.sourceURL;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof AudioClip)) {
			return false;
		}
		AudioClip ac = (AudioClip) obj;
		return (ac.getAudioClipState() == this.audioClipState) && (ac.getURL().equals(this.sourceURL));
	}

	public void play() {
		this.audioClipState = STATE_PLAYING;
	}

	public void loop() {
		this.audioClipState = STATE_LOOPING;
	}

	public void stop() {
		this.audioClipState = STATE_STOPPED;
	}
}