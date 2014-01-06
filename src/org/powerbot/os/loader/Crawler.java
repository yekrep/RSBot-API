package org.powerbot.os.loader;

import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Paris
 */
public class Crawler implements Runnable {
    private AtomicBoolean run, passed;
    public final Map<String, String> parameters;
    public String game, archive, clazz;

    public Crawler() {
        run = new AtomicBoolean(false);
        passed = new AtomicBoolean(false);
        parameters = new HashMap<String, String>();
    }

    public boolean crawl() {
        if (!run.get()) {
            run();
        }
        return passed.get();
    }

    @Override
    public void run() {
        if (!run.compareAndSet(false, true)) {
            return;
        }

        Pattern p;
        Matcher m;
        String url, referer, html;

        url = "http://oldschool69.runescape.com/j1";
        referer = null;
        html = download(url, referer);
        if (html == null) {
            return;
        }
        game = url;

        p = Pattern.compile(".+\\barchive=(\\S+)", Pattern.CASE_INSENSITIVE);
        m = p.matcher(html);
        if (!m.find()) {
            return;
        }
        archive = game.substring(0, game.lastIndexOf('/') + 1) + m.group(1);
        p = Pattern.compile(".+\\bcode=(\\S+).class", Pattern.CASE_INSENSITIVE);
        m = p.matcher(html);
        if (!m.find()) {
            return;
        }
        clazz = m.group(1);

        p = Pattern.compile("<param name=\"([^\\s]+)\"\\s+value=\"([^>]*)\">", Pattern.CASE_INSENSITIVE);
        m = p.matcher(html);

        while (m.find()) {
            parameters.put(m.group(1), m.group(2));
        }
        parameters.remove("haveie6");

        passed.set(true);
    }

    private String download(final String url, final String referer) {
        try {
            final HttpURLConnection con = HttpClient.getHttpConnection(new URL(url));
            con.setRequestProperty("User-Agent", HttpClient.HTTP_USERAGENT_FAKE);
            if (referer != null) {
                con.setRequestProperty("Referer", referer);
            }
            return IOHelper.readString(HttpClient.getInputStream(con));
        } catch (final IOException ignored) {
            ignored.printStackTrace();
            return null;
        }
    }
}
