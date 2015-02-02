package de.jetwick.snacktory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * Tests for HtmlFetcher proxy feature.
 */
public class HtmlFetcherProxyTest {

    public HtmlFetcherProxyTest() {
    }

    @Test
    public void testNoProxy() {
        HtmlFetcher fetcher = new HtmlFetcher();
        assertFalse("HtmlFetcher proxy was already set", fetcher.isProxySet());
    }

    @Test
    public void testSocksProxy() {
        HtmlFetcher fetcher = new HtmlFetcher();
        fetcher.setProxyPort(3128);
        fetcher.setProxyServer("127.0.0.1");
        fetcher.setProxyType("SOCKS");

        assertEquals("Invalid SOCKS proxy type name", "SOCKS", fetcher.getProxyType());
    }

    @Test
    public void testProxyPort() {
        HtmlFetcher fetcher = new HtmlFetcher();
        assertEquals("HtmlFetcher proxy port was not zero", Integer.valueOf(0), Integer.valueOf(fetcher.getProxyPort()));
    }

    @Test
    public void testProxyServer() {
        HtmlFetcher fetcher = new HtmlFetcher();
        assertEquals("HtmlFetch proxy server was not empty", null, fetcher.getProxyServer());
    }

    public void testProxyType() {
        HtmlFetcher fetcher = new HtmlFetcher();
        assertEquals("HtmlFetch proxy type was not null", null, fetcher.getProxyType());
    }

}
