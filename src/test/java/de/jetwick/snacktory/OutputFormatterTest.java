/*
 *  Copyright 2012 Peter Karich info@jetsli.de
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

import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Karich
 */
public class OutputFormatterTest {

    @Test
    public void testSkipHidden() {
        OutputFormatter formatter = new OutputFormatter();
        Document doc = Jsoup.parse("<div><div style=\"display:none\">xy</div>test</div>");
        StringBuilder sb = new StringBuilder();
        formatter.appendTextSkipHidden(doc, sb);
        assertEquals("test", sb.toString());
    }

    @Test
    public void testTextList() {
        OutputFormatter formatter = new OutputFormatter();
        Document doc = Jsoup.parse("<div><p><p>aa</p></p><p>bb</p><p>cc</p></div>");
        assertEquals(Arrays.asList("aa", "bb", "cc"), formatter.getTextList(doc));
    }
}
