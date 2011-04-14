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

import org.junit.AfterClass;
import org.junit.BeforeClass;
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
        assertEquals("hello how", Helper.longestSubstring("hi hello how are you?", "hello how"));
        assertEquals(" people if ", Helper.longestSubstring("x now if people if todo?", "I know people if you"));
        assertEquals("", Helper.longestSubstring("?", "people"));
        assertEquals("people", Helper.longestSubstring(" people ", "people"));
    }
}
