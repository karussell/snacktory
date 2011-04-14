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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Peter Karich, jetwick_@_pannous_._info
 */
public class HtmlFetcher {

    static {
        Helper.enableCookieMgmt();
        Helper.enableUserAgentOverwrite();
    }

    public static JResult fetchAndExtract(String url, int timeout) throws Exception {
        JResult res = new ArticleTextExtractor().extractContent(fetchAsString(url, timeout));
        String domain = Helper.extractDomain(url, false);

        // some images are relative to root and do not include the url :/
        if (res.getImageUrl().startsWith("/"))
            res.setImageUrl("http://" + domain + res.getImageUrl());

        // some websites do not store favicon links within the page
        if (res.getFaviconUrl().isEmpty())
            res.setFaviconUrl(Helper.getDefaultFavicon(url));

        return res;
    }

    public static String fetchAsString(String urlAsString, int timeout) {
        try {
            URL url = new URL(urlAsString);
            //using proxy may increase latency
            HttpURLConnection hConn = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
            hConn.setRequestProperty("User-Agent", "Mozilla/5.0 Gecko/20100915 Firefox/3.6.10");

            boolean goose = true;
            if (goose) {
                hConn.setRequestProperty("Accept-Language", "en-us");
                hConn.setRequestProperty("content-charset", "UTF-8");
                hConn.addRequestProperty("Referer", "http://jetwick.com/s");
                // why should we avoid the cache?
//                hConn.setRequestProperty("Cache-Control", "max-age=0");                
            }

            // on android we got problems because of this
            // so do not allow gzip compression for now
//            hConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            hConn.setConnectTimeout(timeout);
            hConn.setReadTimeout(timeout);
            // default length of bufferedinputstream is 8k
            byte[] arr = new byte[Converter.K4];
            InputStream is = hConn.getInputStream();

            if ("gzip".equals(hConn.getContentEncoding()))
                is = new GZIPInputStream(is);

            BufferedInputStream in = new BufferedInputStream(is, arr.length);
            in.read(arr);

            String enc = Converter.extractEncoding(hConn.getContentType());
            return new Converter().streamToString(is, enc);
        } catch (Exception ex) {
        }
        return "";
    }
}
