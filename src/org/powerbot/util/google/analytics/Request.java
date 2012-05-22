package org.powerbot.util.google.analytics;

/**
 * @author Paris
 */
public final class Request {
	public String pageTitle = null, hostName = null, pageURL = null, eventCategory = null, eventAction = null, eventLabel = null;
	public Integer eventValue = null;
	public String utmcsr = "(direct)", utmccn = "(direct)", utmctr = null, utmcmd = "(none)", utmcct = null;

	public void setReferrer(final String site, final String page) {
		utmcmd = "referral";
		utmcct = page;
		utmccn = "(referral)";
		utmcsr = site;
		utmctr = null;
	}

	public void setSearchReferrer(final String searchSource, final String searchKeywords) {
		utmcsr = searchSource;
		utmctr = searchKeywords;
		utmcmd = "organic";
		utmccn = "(organic)";
		utmcct = null;
	}
}
