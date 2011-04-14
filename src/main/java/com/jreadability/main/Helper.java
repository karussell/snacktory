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

/**
 *
 * @author Peter Karich, jetwick_@_pannous_._info
 */
public class Helper {

    public static int count(String str, String substring) {
        int c = 0;
        int index1 = str.indexOf(substring);
        if (index1 >= 0) {
            c++;
            c += count(str.substring(index1 + substring.length()), substring);
        }
        return c;
    }

    /**
     * remove more than two spaces or newlines
     */
    public static String innerTrim(String str) {
        StringBuilder sb = new StringBuilder();
        boolean previousSpace = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == ' ' || c == '\n') {
                previousSpace = true;
                continue;
            }

            if (previousSpace)
                sb.append(' ');

            previousSpace = false;
            sb.append(c);
        }
        return sb.toString().trim();
    }

    public static String longestSubstring(String str1, String str2) {
        StringBuilder sb = new StringBuilder();
        if (str1 == null || str1.isEmpty() || str2 == null || str2.isEmpty())
            return "";

        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();

        // dynamic programming => save already identical length into array
        // to understand this algo simply print identical length in every entry of the array
        // i+1, j+1 then reuses information from i,j
        // java initializes them already with 0
        int[][] num = new int[str1.length()][str2.length()];
        int maxlen = 0;
        int lastSubsBegin = 0;
        for (int i = 0; i < str1.length(); i++) {
            for (int j = 0; j < str2.length(); j++) {
                if (str1.charAt(i) == str2.charAt(j)) {
                    if ((i == 0) || (j == 0))
                        num[i][j] = 1;
                    else
                        num[i][j] = 1 + num[i - 1][j - 1];

                    if (num[i][j] > maxlen) {
                        maxlen = num[i][j];
                        // generate substring from str1 => i
                        int thisSubsBegin = i - num[i][j] + 1;
                        if (lastSubsBegin == thisSubsBegin) {
                            //if the current LCS is the same as the last time this block ran
                            sb.append(str1.charAt(i));
                        } else {
                            //this block resets the string builder if a different LCS is found
                            lastSubsBegin = thisSubsBegin;
                            sb = new StringBuilder();
                            sb.append(str1.substring(lastSubsBegin, i + 1));
                        }
                    }
                }
            }
        }
        return sb.toString();
    }
}
