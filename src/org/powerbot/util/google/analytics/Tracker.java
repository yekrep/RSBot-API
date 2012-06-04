package org.powerbot.util.google.analytics;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Paris
 */
public final class Tracker {
	private final ExecutorService exec;
	private Analytics builder;
	public boolean enabled;

	public Tracker(final String code) {
		this(new Config(code));
	}

	public Tracker(final Config config) {
		exec = Executors.newSingleThreadExecutor();
		builder = new Analytics(config);
		enabled = true;
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				if (!exec.isShutdown()) {
					exec.shutdown();
					try {
						exec.awaitTermination(5, TimeUnit.SECONDS);
					} catch (InterruptedException ignored) {
					}
				}
			}
		}));
	}

	public void resetSession() {
		builder.resetSession();
	}

	/**
	 * Tracks a page view.
	 * 
	 * @param pageUrl
	 *            required, Google won't track without it. Ex:
	 *            <code>"org/me/javaclass.java"</code>, or anything you want as
	 *            the page url.
	 * @param pageTitle
	 *            content title
	 * @param host
	 *            the host name for the url
	 */
	public void trackPageView(final String pageUrl, final String pageTitle, final String host) {
		trackPageViewFromReferrer(pageUrl, pageTitle, host, "", "");
	}

	/**
	 * Tracks a page view.
	 * 
	 * @param pageUrl
	 *            required, Google won't track without it. Ex:
	 *            <code>"org/me/javaclass.java"</code>, or anything you want as
	 *            the page url.
	 * @param pageTitle
	 *            content title
	 * @param host
	 *            the host name for the url
	 * @param referrerSite
	 *            site of the referrer. ex, www.example.com
	 * @param referrerPage
	 *            page of the referrer. ex, /mypage.php
	 */
	public void trackPageViewFromReferrer(final String pageUrl, final String pageTitle, final String host, final String referrerSite, final String referrerPage) {
		final Request data = new Request();
		data.hostName = host;
		data.pageTitle = pageTitle;
		data.pageURL = pageUrl;
		data.setReferrer(referrerSite, referrerPage);
		makeCustomRequest(data);
	}

	/**
	 * Tracks a page view.
	 * 
	 * @param pageUrl
	 *            required, Google won't track without it. Ex:
	 *            <code>"org/me/javaclass.java"</code>, or anything you want as
	 *            the page url.
	 * @param pageTitle
	 *            content title
	 * @param host
	 *            the host name for the url
	 * @param searchSource
	 *            source of the search engine. ex: google
	 * @param searchKeywords
	 *            the keywords of the search. ex: java google analytics tracking
	 *            utility
	 */
	public void trackPageViewFromSearch(final String pageUrl, final String pageTitle, final String host, final String searchSource, final String searchKeywords) {
		final Request data = new Request();
		data.hostName = host;
		data.pageTitle = pageTitle;
		data.pageURL = pageUrl;
		data.setSearchReferrer(searchSource, searchKeywords);
		makeCustomRequest(data);
	}

	/**
	 * Tracks an event. To provide more info about the page, use
	 * {@link #makeCustomRequest(Request)}.
	 * 
	 * @param category
	 * @param action
	 */
	public void trackEvent(final String category, final String action) {
		trackEvent(category, action, null, null);
	}

	/**
	 * Tracks an event. To provide more info about the page, use
	 * {@link #makeCustomRequest(Request)}.
	 * 
	 * @param category
	 * @param action
	 * @param label
	 */
	public void trackEvent(final String category, final String action, final String label) {
		trackEvent(category, action, label, null);
	}

	/**
	 * Tracks an event. To provide more info about the page, use
	 * {@link #makeCustomRequest(Request)}.
	 * 
	 * @param category
	 *            required
	 * @param action
	 *            required
	 * @param label
	 *            optional
	 * @param value
	 *            optional
	 */
	public void trackEvent(final String category, final String action, final String label, final Integer value) {
		final Request data = new Request();
		data.eventCategory = category;
		data.eventAction = action;
		data.eventLabel = label;
		data.eventValue = value;
		makeCustomRequest(data);
	}

	/**
	 * Makes a custom tracking request based from the given data.
	 * 
	 * @param data
	 * @throws NullPointerException
	 *             if argData is null or if the URL builder is null
	 */
	public synchronized void makeCustomRequest(final Request data) {
		final String url = builder.buildURL(data);
		dispatchRequest(url);
	}

	private void dispatchRequest(final String url) {
		exec.execute(new Runnable() {
			@Override
			public void run() {
				try {
					final HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
					con.setRequestMethod("GET");
					con.setInstanceFollowRedirects(true);
					con.connect();
					con.getInputStream().read();
					con.disconnect();
				} catch (final IOException ignored) {
				}
			}
		});
	}
}
