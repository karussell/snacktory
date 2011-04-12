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
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import org.apache.log4j.Logger;

/**
 *
 * @author Peter Karich, jetwick_@_pannous_._info
 */
public class Converter {

    private final static Logger logger = Logger.getLogger(Converter.class);
    public final static String UTF8 = "UTF8";
    public final static String ISO = "ISO-8859-1";
    public final static int K4 = 4096;
    private String encoding;

    public static String extractEncoding(String contentType) {
        String[] values = contentType.split(";");
        String charset = "";

        for (String value : values) {
            value = value.trim().toLowerCase();

            if (value.startsWith("charset="))
                charset = value.substring("charset=".length());
        }

        // http1.1 says ISO-8859-1 is the default charset
        if (charset.length() == 0)
            charset = ISO;

        return charset;
    }

    public String getEncoding() {
        if (encoding == null)
            return "";
        return encoding.toLowerCase();
    }

    public String streamToString(InputStream is) {
        return streamToString(is, 1000000, encoding);
    }

    public String streamToString(InputStream is, int maxBytes) {
        return streamToString(is, maxBytes, encoding);
    }

    /**
     * reads bytes off the string and returns a string
     * @param is
     * @param maxBytes The max bytes that we want to read from the input stream
     * @return String
     */
    public String streamToString(InputStream is, int maxBytes, String enc) {
        encoding = enc;
        // Http 1.1. standard is iso-8859-1 not utf8 :(
        if (encoding == null)
            encoding = "ISO-8859-1";

        byte[] arr = new byte[K4];
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(is, arr.length);
            StringBuilder s = new StringBuilder();
            int bytesRead = K4;

            // Grab better encoding from stream
            int n = in.read(arr);
            String res = new String(arr, 0, n, encoding);
            int encIndex = res.indexOf("charset=");
            if (encIndex > 0) {
                char startChar = res.charAt(encIndex + 8);
                int lastEncIndex;
                if (startChar == '\'')
                    // if we have charset='something'
                    lastEncIndex = res.indexOf("'", ++encIndex + 8);
                else if (startChar == '\"')
                    // if we have charset="something"
                    lastEncIndex = res.indexOf("\"", ++encIndex + 8);
                else {
                    // if we have "text/html; charset=utf-8"                    
                    int first = res.indexOf("\"", encIndex + 8);
                    // or "text/html; charset=utf-8 "
                    int sec = res.indexOf(" ", encIndex + 8);
                    lastEncIndex = Math.min(first, sec);

                    // or "text/html; charset=utf-8 '
                    int third = res.indexOf("'", encIndex + 8);
                    lastEncIndex = Math.min(lastEncIndex, third);
                }

                // re-read byte array with different encoding
                if (lastEncIndex > encIndex + 8 && lastEncIndex < encIndex + 8 + 40) {
                    encoding = res.substring(encIndex + 8, lastEncIndex);
                    try {
                        res = new String(arr, 0, n, encoding);
                    } catch (UnsupportedEncodingException e) {
                        logger.warn("Problem with encoding: " + encoding + " " + e.toString());
                    }
                }
            }

            s.append(res);

            if (n > 0)
                while (true) {
                    if (bytesRead >= maxBytes)
                        throw new IllegalStateException("Maxbyte exceeds!");

                    n = in.read(arr);
                    bytesRead += K4;
                    if (n < 0)
                        break;
                    s.append(new String(arr, 0, n, encoding));
                }

            return s.toString();

        } catch (SocketTimeoutException e) {
            logger.warn(e.toString() + " " + e.getMessage());
        } catch (IOException e) {
            logger.warn(e.toString() + " " + e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }
        return "";
    }
}
