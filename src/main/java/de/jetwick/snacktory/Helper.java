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

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLDecoder;
import java.net.URLEncoder;
import org.jsoup.nodes.Element;

/**
 *
 * @author Peter Karich, jetwick_@_pannous_._info
 */
public class Helper {

    public static final String UTF8 = "UTF-8";

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

    /**
     * Starts reading the encoding from the first valid character until an invalid
     * encoding character occurs.
     */
    public static String encodingCleanup(String str) {
        StringBuilder sb = new StringBuilder();
        boolean startedWithCorrectString = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isDigit(c) || Character.isLetter(c) || c == '-' || c == '_') {
                startedWithCorrectString = true;
                sb.append(c);
                continue;
            }

            if (startedWithCorrectString)
                break;
        }
        return sb.toString().trim();
    }

    /**
     * @return the longest substring as str1.substring(result[0], result[1]);
     */
    public static String getLongestSubstring(String str1, String str2) {
        int res[] = longestSubstring(str1, str2);
        if (res == null || res[0] >= res[1])
            return "";

        return str1.substring(res[0], res[1]);
    }

    public static int[] longestSubstring(String str1, String str2) {
        if (str1 == null || str1.isEmpty() || str2 == null || str2.isEmpty())
            return null;

        // dynamic programming => save already identical length into array
        // to understand this algo simply print identical length in every entry of the array
        // i+1, j+1 then reuses information from i,j
        // java initializes them already with 0
        int[][] num = new int[str1.length()][str2.length()];
        int maxlen = 0;
        int lastSubstrBegin = 0;
        int endIndex = 0;
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
                        lastSubstrBegin = i - num[i][j] + 1;
                        endIndex = i + 1;
                    }
                }
            }
        }
        return new int[]{lastSubstrBegin, endIndex};
    }

    public static String getDefaultFavicon(String url) {
        return useDomainOfFirst4Sec(url, "/favicon.ico");
    }

    /**
     * @param urlForDomain extract the domain from this url
     * @param path this url does not have a domain
     * @param includeMobile
     * @return 
     */
    public static String useDomainOfFirst4Sec(String urlForDomain, String path) {
        if ("favicon.ico".equals(path))
            path = "/favicon.ico";

        if (path.startsWith("http"))
            return path;
        else if (path.startsWith("/"))
            return "http://" + extractHost(urlForDomain) + path;
        else if (path.startsWith("../")) {
            int slashIndex = urlForDomain.lastIndexOf("/");
            if (slashIndex > 0 && slashIndex + 1 < urlForDomain.length())
                urlForDomain = urlForDomain.substring(0, slashIndex + 1);

            return urlForDomain + path;
        }
        return path;
    }

    public static String extractHost(String url) {
        return extractDomain(url, false);
    }

    public static String extractDomain(String url, boolean aggressive) {
        if (url.startsWith("http://"))
            url = url.substring("http://".length());
        else if (url.startsWith("https://"))
            url = url.substring("https://".length());

        if (aggressive) {
            if (url.startsWith("www."))
                url = url.substring("www.".length());

            // strip mobile from start
            if (url.startsWith("m."))
                url = url.substring("m.".length());
        }

        int slashIndex = url.indexOf("/");
        if (slashIndex > 0)
            url = url.substring(0, slashIndex);

        return url;
    }

    public static boolean isVideoLink(String url) {
        url = extractDomain(url, true);
        return url.startsWith("youtube.com") || url.startsWith("video.yahoo.com")
                || url.startsWith("vimeo.com") || url.startsWith("blip.tv");
    }

    public static boolean isVideo(String url) {
        url = url.toLowerCase();
        return url.endsWith(".mpeg") || url.endsWith(".avi")
                || url.endsWith(".mpg4") || url.endsWith(".flv");
    }

    public static boolean isImage(String url) {
        url = url.toLowerCase();
        return url.endsWith(".png") || url.endsWith(".jpeg")
                || url.endsWith(".jpg") || url.endsWith(".bmp") || url.endsWith(".ico");
    }

    /**
     * @see http://blogs.sun.com/CoreJavaTechTips/entry/cookie_handling_in_java_se
     */
    public static void enableCookieMgmt() {
        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
    }

    /**
     * @see http://stackoverflow.com/questions/2529682/setting-user-agent-of-a-java-urlconnection
     */
    public static void enableUserAgentOverwrite() {
        System.setProperty("http.agent", "");
    }

    public static String getUrlFromUglyGoogleRedirect(String url) {
        if (url.startsWith("http://www.google.com/url?")) {
            url = url.substring("http://www.google.com/url?".length());
            String arr[] = urlDecode(url).split("\\&");
            if (arr != null)
                for (String str : arr) {
                    if (str.startsWith("q="))
                        return str.substring("q=".length());
                }
        }

        return null;
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, UTF8);
        } catch (UnsupportedEncodingException ex) {
            return str;
        }
    }

    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, UTF8);
        } catch (UnsupportedEncodingException ex) {
            return str;
        }
    }

    /**
     * Popular sites uses the #! to indicate the importance of
     * the following chars. Ugly but true.
     * Such as: facebook, twitter, gizmodo, ...
     */
    public static String removeHashbang(String url) {
        return url.replaceFirst("#!", "");
    }

    public static String printNode(Element root) {
        return printNode(root, 0);
    }

    public static String printNode(Element root, int indentation) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentation; i++) {
            sb.append(' ');
        }
        sb.append(root.tagName());
        sb.append(":");
        sb.append(root.ownText());
        sb.append("\n");
        for (Element el : root.children()) {
            sb.append(printNode(el, indentation + 1));
            sb.append("\n");
        }
        return sb.toString();
    }
}
