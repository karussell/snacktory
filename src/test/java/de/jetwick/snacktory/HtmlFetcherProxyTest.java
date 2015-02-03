/*
 *  Copyright 2015 Peter Karich 
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.jetwick.snacktory;

import static org.junit.Assert.assertEquals;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.junit.Test;

/**
 * Tests for HtmlFetcher proxy feature.
 */
public class HtmlFetcherProxyTest {

    public HtmlFetcherProxyTest() {
    }

    @Test
    public void testSocksProxy() {
        HtmlFetcher fetcher = new HtmlFetcher();
        Proxy proxy = new Proxy(Type.valueOf("SOCKS"), new InetSocketAddress("127.0.0.1", 3128));
        fetcher.setProxy(proxy);

        assertEquals("Invalid SOCKS proxy type name", "SOCKS", fetcher.getProxy().type().name());
    }

    @Test
    public void testNoProxy() {
        HtmlFetcher fetcher = new HtmlFetcher();
        assertEquals("HtmlFetch proxy server was not a NO_PROXY proxy", Proxy.NO_PROXY, fetcher.getProxy());
    }

}
