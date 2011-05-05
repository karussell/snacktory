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
package de.jetwick.snacktory;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Karich, jetwick_@_pannous_._info
 */
public class HelperTest {

    public HelperTest() {
    }

    @Test
    public void testInnerTrim() {
        assertEquals("", Helper.innerTrim("   "));
        assertEquals("t", Helper.innerTrim("  t "));
        assertEquals("t t t", Helper.innerTrim("t t t "));
        assertEquals("t t", Helper.innerTrim("t    \nt "));
        assertEquals("t peter", Helper.innerTrim("t  peter "));
    }

    @Test
    public void testCount() {
        assertEquals(1, Helper.count("hi wie &test; gehts", "&test;"));
        assertEquals(1, Helper.count("&test;", "&test;"));
        assertEquals(2, Helper.count("&test;&test;", "&test;"));
        assertEquals(2, Helper.count("&test; &test;", "&test;"));
        assertEquals(3, Helper.count("&test; test; &test; plu &test;", "&test;"));
    }

    @Test
    public void longestSubstring() {
//        assertEquals(9, ArticleTextExtractor.longestSubstring("hi hello how are you?", "hello how"));
        assertEquals("hello how", Helper.getLongestSubstring("hi hello how are you?", "hello how"));
        assertEquals(" people if ", Helper.getLongestSubstring("x now if people if todo?", "I know people if you"));
        assertEquals("", Helper.getLongestSubstring("?", "people"));
        assertEquals("people", Helper.getLongestSubstring(" people ", "people"));
    }

    @Test
    public void testHashbang() {
        assertEquals("sdfiasduhf+asdsad+sdfsdf#!", Helper.removeHashbang("sdfiasduhf+asdsad#!+sdfsdf#!"));
        assertEquals("sdfiasduhf+asdsad+sdfsdf#!", Helper.removeHashbang("sdfiasduhf+asdsad#!+sdfsdf#!"));
    }

    @Test
    public void testIsVideoLink() {
        assertTrue(Helper.isVideoLink("m.vimeo.com"));
        assertTrue(Helper.isVideoLink("m.youtube.com"));
        assertTrue(Helper.isVideoLink("www.youtube.com"));
        assertTrue(Helper.isVideoLink("http://youtube.com"));
        assertTrue(Helper.isVideoLink("http://www.youtube.com"));

        assertTrue(Helper.isVideoLink("https://youtube.com"));

        assertFalse(Helper.isVideoLink("test.com"));
        assertFalse(Helper.isVideoLink("irgendwas.com/youtube.com"));
    }

    @Test public void testExctractHost() {
        assertEquals("techcrunch.com",
                Helper.extractHost("http://techcrunch.com/2010/08/13/gantto-takes-on-microsoft-project-with-web-based-project-management-application/"));
    }

    @Test public void testFavicon() {
        assertEquals("http://www.n24.de/news/../../../media/imageimport/images/content/favicon.ico",
                Helper.useDomainOfFirst4Sec("http://www.n24.de/news/newsitem_6797232.html", "../../../media/imageimport/images/content/favicon.ico"));
        Helper.useDomainOfFirst4Sec("http://www.n24.de/favicon.ico", "/favicon.ico");
        Helper.useDomainOfFirst4Sec("http://www.n24.de/favicon.ico", "favicon.ico");
    }

    @Test public void testEncodingCleanup() {
        assertEquals("utf-8", Helper.encodingCleanup("utf-8"));
        assertEquals("utf-8", Helper.encodingCleanup("utf-8\""));
        assertEquals("utf-8", Helper.encodingCleanup("utf-8'"));
        assertEquals("test-8", Helper.encodingCleanup(" test-8 &amp;"));
    }

    @Test public void testUglyFacebook() {        
        assertEquals("http://www.bet.com/collegemarketingreps&h=42263",
                Helper.getUrlFromUglyFacebookRedirect("http://www.facebook.com/l.php?u=http%3A%2F%2Fwww.bet.com%2Fcollegemarketingreps&h=42263"));
    }
}
