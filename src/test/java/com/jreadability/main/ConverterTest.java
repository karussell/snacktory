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

import junit.framework.TestCase;

/**
 *
 * @author Peter Karich, jetwick_@_pannous_._info
 */
public class ConverterTest extends TestCase {

    public ConverterTest(String testName) {
        super(testName);
    }

    public void testDetermineEncoding() throws Exception {
        Converter d = new Converter();
        d.streamToString(getClass().getResourceAsStream("test.html"), Integer.MAX_VALUE);
        assertEquals("utf-8", d.getEncoding());

        d = new Converter();
        d.streamToString(getClass().getResourceAsStream("yomiuri.html"), Integer.MAX_VALUE);
        assertEquals("shift_jis", d.getEncoding());

        d = new Converter();
        d.streamToString(getClass().getResourceAsStream("test_spiegel.html"), Integer.MAX_VALUE);
        assertEquals("iso-8859-1", d.getEncoding());

        d = new Converter();
        d.streamToString(getClass().getResourceAsStream("test_itunes.html"), Integer.MAX_VALUE);
        assertEquals("utf-8", d.getEncoding());

        d = new Converter();
        d.streamToString(getClass().getResourceAsStream("test_twitter.html"), Integer.MAX_VALUE);
        assertEquals("utf-8", d.getEncoding());

        d = new Converter();
        d.streamToString(getClass().getResourceAsStream("test_nyt.html"), Integer.MAX_VALUE);
        assertEquals("utf-8", d.getEncoding());
    }
}
