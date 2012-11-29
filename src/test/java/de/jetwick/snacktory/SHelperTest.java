/*
 *  Copyright 2011 Peter Karich 
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
 * @author Peter Karich
 */
public class SHelperTest {

    public SHelperTest() {
    }

    @Test
    public void testInnerTrim() {
        assertEquals("", SHelper.innerTrim("   "));
        assertEquals("t", SHelper.innerTrim("  t "));
        assertEquals("t t t", SHelper.innerTrim("t t t "));
        assertEquals("t t", SHelper.innerTrim("t    \nt "));
        assertEquals("t peter", SHelper.innerTrim("t  peter "));        
        assertEquals("t t", SHelper.innerTrim("t    \n     t "));
    }

    @Test
    public void testCount() {
        assertEquals(1, SHelper.count("hi wie &test; gehts", "&test;"));
        assertEquals(1, SHelper.count("&test;", "&test;"));
        assertEquals(2, SHelper.count("&test;&test;", "&test;"));
        assertEquals(2, SHelper.count("&test; &test;", "&test;"));
        assertEquals(3, SHelper.count("&test; test; &test; plu &test;", "&test;"));
    }

    @Test
    public void longestSubstring() {
//        assertEquals(9, ArticleTextExtractor.longestSubstring("hi hello how are you?", "hello how"));
        assertEquals("hello how", SHelper.getLongestSubstring("hi hello how are you?", "hello how"));
        assertEquals(" people if ", SHelper.getLongestSubstring("x now if people if todo?", "I know people if you"));
        assertEquals("", SHelper.getLongestSubstring("?", "people"));
        assertEquals("people", SHelper.getLongestSubstring(" people ", "people"));
    }

    @Test
    public void testHashbang() {
        assertEquals("sdfiasduhf+asdsad+sdfsdf#!", SHelper.removeHashbang("sdfiasduhf+asdsad#!+sdfsdf#!"));
        assertEquals("sdfiasduhf+asdsad+sdfsdf#!", SHelper.removeHashbang("sdfiasduhf+asdsad#!+sdfsdf#!"));
    }

    @Test
    public void testIsVideoLink() {
        assertTrue(SHelper.isVideoLink("m.vimeo.com"));
        assertTrue(SHelper.isVideoLink("m.youtube.com"));
        assertTrue(SHelper.isVideoLink("www.youtube.com"));
        assertTrue(SHelper.isVideoLink("http://youtube.com"));
        assertTrue(SHelper.isVideoLink("http://www.youtube.com"));

        assertTrue(SHelper.isVideoLink("https://youtube.com"));

        assertFalse(SHelper.isVideoLink("test.com"));
        assertFalse(SHelper.isVideoLink("irgendwas.com/youtube.com"));
    }

    @Test
    public void testExctractHost() {
        assertEquals("techcrunch.com",
                SHelper.extractHost("http://techcrunch.com/2010/08/13/gantto-takes-on-microsoft-project-with-web-based-project-management-application/"));
    }

    @Test
    public void testFavicon() {
        assertEquals("http://www.n24.de/news/../../../media/imageimport/images/content/favicon.ico",
                SHelper.useDomainOfFirstArg4Second("http://www.n24.de/news/newsitem_6797232.html", "../../../media/imageimport/images/content/favicon.ico"));
        SHelper.useDomainOfFirstArg4Second("http://www.n24.de/favicon.ico", "/favicon.ico");
        SHelper.useDomainOfFirstArg4Second("http://www.n24.de/favicon.ico", "favicon.ico");
    }

    @Test
    public void testFaviconProtocolRelative() throws Exception {
        assertEquals("http://de.wikipedia.org/apple-touch-icon.png",
                SHelper.useDomainOfFirstArg4Second("http://de.wikipedia.org/favicon", "//de.wikipedia.org/apple-touch-icon.png"));
    }

    @Test
    public void testImageProtocolRelative() throws Exception {
        assertEquals("http://upload.wikimedia.org/wikipedia/commons/thumb/5/5c/Flag_of_Greece.svg/150px-Flag_of_Greece.svg.png",
                SHelper.useDomainOfFirstArg4Second("http://de.wikipedia.org/wiki/Griechenland", "//upload.wikimedia.org/wikipedia/commons/thumb/5/5c/Flag_of_Greece.svg/150px-Flag_of_Greece.svg.png"));
    }


    @Test
    public void testEncodingCleanup() {
        assertEquals("utf-8", SHelper.encodingCleanup("utf-8"));
        assertEquals("utf-8", SHelper.encodingCleanup("utf-8\""));
        assertEquals("utf-8", SHelper.encodingCleanup("utf-8'"));
        assertEquals("test-8", SHelper.encodingCleanup(" test-8 &amp;"));
    }

    @Test
    public void testUglyFacebook() {
        assertEquals("http://www.bet.com/collegemarketingreps&h=42263",
                SHelper.getUrlFromUglyFacebookRedirect("http://www.facebook.com/l.php?u=http%3A%2F%2Fwww.bet.com%2Fcollegemarketingreps&h=42263"));
    }
        
    @Test
    public void testEstimateDate() {
        assertNull(SHelper.estimateDate("http://www.facebook.com/l.php?u=http%3A%2F%2Fwww.bet.com%2Fcollegemarketin"));
        assertEquals("2010/02/15", SHelper.estimateDate("http://www.vogella.de/blog/2010/02/15/twitter-android/"));
        assertEquals("2010/02", SHelper.estimateDate("http://www.vogella.de/blog/2010/02/twitter-android/12"));
        assertEquals("2009/11/05", SHelper.estimateDate("http://cagataycivici.wordpress.com/2009/11/05/mobile-twitter-client-with-jsf/"));        
        assertEquals("2009", SHelper.estimateDate("http://cagataycivici.wordpress.com/2009/sf/12/1/"));        
        assertEquals("2011/06", SHelper.estimateDate("http://bdoughan.blogspot.com/2011/06/using-jaxbs-xmlaccessortype-to.html"));
        assertEquals("2011", SHelper.estimateDate("http://bdoughan.blogspot.com/2011/13/using-jaxbs-xmlaccessortype-to.html"));
    }
    
    @Test
    public void testCompleteDate() {
        assertNull(SHelper.completeDate(null));
        assertEquals("2001/01/01", SHelper.completeDate("2001"));
        assertEquals("2001/11/01", SHelper.completeDate("2001/11"));
        assertEquals("2001/11/02", SHelper.completeDate("2001/11/02"));
    }
}
