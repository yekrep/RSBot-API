package org.powerbot.util.google.analytics;

import java.util.Random;

import org.powerbot.util.StringUtil;

/**
 * @author Paris
 */
public final class Analytics {
	public static final String URL_PREFIX = "http://www.google-analytics.com/__utm.gif";

	private final Config config;
	private final Random random = new Random((long) (Math.random() * Long.MAX_VALUE));
	private int cookie1, cookie2;

	public Analytics(final Config config) {
		this.config = config;
		resetSession();
	}

	public String getVersion() {
		return "4.7.2";
	}

	public String buildURL(final Request req) {
		final StringBuilder sb = new StringBuilder();
		sb.append(URL_PREFIX);

		final long now = System.currentTimeMillis();

		sb.append("?utmwv=" + getVersion());
		sb.append("&utmn=" + random.nextInt());
		sb.append("&utmac=" + config.code);
		sb.append("&utmje=1");
		sb.append("&utmhid=" + random.nextInt());
		sb.append("&utmcs=" + (config.encoding == null ? "-" : StringUtil.urlEncode(config.encoding)));
		if (config.screenResolution != null) {
			sb.append("&utmsr=" + StringUtil.urlEncode(config.screenResolution));
		}
		if (config.colorDepth != null) {
			sb.append("&utmsc=" + StringUtil.urlEncode(config.colorDepth));
		}
		if (config.userLanguage != null) {
			sb.append("&utmul=" + StringUtil.urlEncode(config.userLanguage));
		}
		if (config.flashVersion != null) {
			sb.append("&utmfl=" + StringUtil.urlEncode(config.flashVersion));
		}
		if (req.pageTitle != null) {
			sb.append("&utmdt=" + StringUtil.urlEncode(req.pageTitle));
		}
		if (req.pageURL != null) {
			sb.append("&utmp=" + StringUtil.urlEncode(req.pageURL));
		}
		if (req.hostName != null) {
			sb.append("&utmhn=" + StringUtil.urlEncode(req.hostName));
		}

		if (req.eventAction != null && req.eventCategory != null) {
			sb.append("&utmt=event");
			final String category = StringUtil.urlEncode(req.eventCategory);
			final String action = StringUtil.urlEncode(req.eventAction);

			sb.append("&utme=5(");
			sb.append(category);
			sb.append("*");
			sb.append(action);

			if (req.eventLabel != null) {
				sb.append("*");
				sb.append(StringUtil.urlEncode(req.eventLabel));
			}
			sb.append(")");

			if (req.eventValue != null) {
				sb.append("(");
				sb.append(req.eventValue);
				sb.append(")");
			}
		}

		final String utmctr = StringUtil.urlEncode(req.utmctr);
		final String utmcct = StringUtil.urlEncode(req.utmcct);

		sb.append("&utmcc=__utma%3D");
		sb.append(cookie1);
		sb.append(".");
		sb.append(cookie2);
		sb.append(".");
		sb.append(now);
		sb.append(".");
		sb.append(now);
		sb.append(".");
		sb.append(now);
		sb.append(".");
		sb.append("13%3B%2B__utmz%3D");
		sb.append(cookie1);
		sb.append(".");
		sb.append(now);
		sb.append(".1.1.utmcsr%3D");
		sb.append(StringUtil.urlEncode(req.utmcsr));
		sb.append("%7Cutmccn%3D");
		sb.append(StringUtil.urlEncode(req.utmccn));
		sb.append("%7utmcmd%3D");
		sb.append(StringUtil.urlEncode(req.utmcmd));
		if (utmctr != null) {
			sb.append("%7Cutmctr%3D");
			sb.append(utmctr);
		}
		if (utmcct != null) {
			sb.append("%7Cutmcct%3D");
			sb.append(utmcct);
		}
		sb.append("%3B&gaq=1");

		return sb.toString();
	}

	public void resetSession() {
		cookie1 = random.nextInt();
		cookie2 = random.nextInt();
	}
}
