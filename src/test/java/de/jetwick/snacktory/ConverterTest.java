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

import junit.framework.TestCase;
import org.jsoup.Jsoup;

/**
 *
 * @author Peter Karich
 */
public class ConverterTest extends TestCase {

    public ConverterTest(String testName) {
        super(testName);
    }

    public void testDetermineEncoding() throws Exception {
        Converter d = new Converter();
        d.streamToString(getClass().getResourceAsStream("faz.html"));
        assertEquals("utf-8", d.getEncoding());

        d = new Converter();
        d.streamToString(getClass().getResourceAsStream("yomiuri.html"));
        assertEquals("shift_jis", d.getEncoding());

        d = new Converter();
        d.streamToString(getClass().getResourceAsStream("yomiuri2.html"));
        assertEquals("shift_jis", d.getEncoding());

        d = new Converter();
        d.streamToString(getClass().getResourceAsStream("spiegel.html"));
        assertEquals("iso-8859-1", d.getEncoding());

        d = new Converter();
        d.streamToString(getClass().getResourceAsStream("itunes.html"));
        assertEquals("utf-8", d.getEncoding());

        d = new Converter();
        d.streamToString(getClass().getResourceAsStream("twitter.html"));
        assertEquals("utf-8", d.getEncoding());

        // youtube DOES not specify the encoding AND assumes utf-8 !?
        d = new Converter();
        d.streamToString(getClass().getResourceAsStream("youtube.html"));
        assertEquals("utf-8", d.getEncoding());

        d = new Converter();
        d.streamToString(getClass().getResourceAsStream("nyt.html"));
        assertEquals("utf-8", d.getEncoding());

        d = new Converter();
        d.streamToString(getClass().getResourceAsStream("badenc.html"));
        assertEquals("utf-8", d.getEncoding());

        d = new Converter();
        d.streamToString(getClass().getResourceAsStream("br-online.html"));
        assertEquals("iso-8859-15", d.getEncoding());
    }

    public void testMaxBytesExceedingButGetTitleNevertheless() throws Exception {
        Converter d = new Converter();
        d.setMaxBytes(10000);
        String str = d.streamToString(getClass().getResourceAsStream("faz.html"));
        assertEquals("utf-8", d.getEncoding());
        assertEquals("Im Gespräch: Umweltaktivist Stewart Brand: Ihr Deutschen steht allein da "
                + "- Atomdebatte - FAZ.NET", Jsoup.parse(str).select("title").text());
    }
}
