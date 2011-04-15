/*
 *  Copyright 2011 Peter Karich jetwick_@_pannous_._info
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
package com.jreadability.main;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Karich, jetwick_@_pannous_._info
 */
public class HtmlFetcherTest {

    public HtmlFetcherTest() {
    }

    @Test
    public void testFetchAndExtract() throws Exception {
        JResult res = HtmlFetcher.fetchAndExtract("http://www.yomiuri.co.jp/e-japan/gifu/news/20110410-OYT8T00124.htm?from=tw", 10000, true);
        System.out.println("yomiuri:" + res);
    }

    @Test
    public void testNoException() throws Exception {
        JResult res = HtmlFetcher.fetchAndExtract("http://www.tumblr.com/xeb22gs619", 10000, true);
//        System.out.println("tumblr:" + res.getUrl());

        res = HtmlFetcher.fetchAndExtract("http://www.faz.net/-01s7fc", 10000, true);
//        System.out.println("faz:" + res.getUrl());

        res = HtmlFetcher.fetchAndExtract("http://www.google.com/url?sa=x&q=http://www.taz.de/1/politik/asien/artikel/1/anti-atomkraft-nein-danke/&ct=ga&cad=caeqargbiaaoataaoabaltmh7qrialaawabibwrllurf&cd=d5glzns5m_4&usg=afqjcnetx___sph8sjwhjwi-_mmdnhilra&utm_source=twitterfeed&utm_medium=twitter", 10000, true);
        assertEquals("http://www.taz.de/1/politik/asien/artikel/1/anti-atomkraft-nein-danke/", res.getUrl());
//        System.out.println("google redirect:" + res.getUrl());
    }
}
