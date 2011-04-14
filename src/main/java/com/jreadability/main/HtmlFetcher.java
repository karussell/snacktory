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

    public static String getHtmlAsString(String urlAsString, int timeout) {
        try {
            // TODO steal some stuff from goose to set request properties            
            URL url = new URL(urlAsString);
            //using proxy may increase latency
            HttpURLConnection hConn = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
            hConn.setRequestProperty("User-Agent", "Mozilla/5.0 Gecko/20100915 Firefox/3.6.10");

            // on android we got problems because of this
            // so disable that for now
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
            return new Converter().streamToString(is, 1000000, enc);
        } catch (Exception ex) {
        }
        return "";
    }
}
