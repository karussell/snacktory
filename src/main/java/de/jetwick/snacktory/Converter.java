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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import org.apache.log4j.Logger;

/**
 * This class is not thread safe. Use one new instance every time due to encoding
 * variable.
 * 
 * @author Peter Karich, jetwick_@_pannous_._info
 */
public class Converter {

    private final static Logger logger = Logger.getLogger(Converter.class);
    public final static int MAX_BYTES = 1000000;
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
        return streamToString(is, MAX_BYTES, encoding);
    }

    public String streamToString(InputStream is, String enc) {
        return streamToString(is, MAX_BYTES, enc);
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
        // but we force utf-8 as youtube assumes it ;)
        if (encoding == null || encoding.isEmpty())
            encoding = "utf-8";

        byte[] arr = new byte[K4];
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(is, K4);
            StringBuilder sb = new StringBuilder();
            int bytesRead = K4;

            // Grab better encoding from stream
            int n = in.read(arr);

            String res = new String(arr, 0, n, encoding);
            // detect with the help of meta tag
            String tmpEnc = detectCharset("charset=", res);
            if (tmpEnc != null) {
                try {
                    res = new String(arr, 0, n, tmpEnc);
                    encoding = tmpEnc;
                } catch (UnsupportedEncodingException e) {
                    logger.warn("Problem with charset: " + tmpEnc + " " + e.toString());
                }
            }

            if (encoding == tmpEnc) {
                // detect with the help of xml beginning ala encoding="charset"
                tmpEnc = detectCharset("encoding=", res);
                if (tmpEnc != null)
                    try {
                        res = new String(arr, 0, n, tmpEnc);
                        encoding = tmpEnc;
                    } catch (UnsupportedEncodingException e) {
                        logger.warn("Problem with encoding: " + tmpEnc + " " + e.toString());
                    }
            }

            sb.append(res);

            if (n > 0)
                while (true) {
                    if (bytesRead >= maxBytes)
                        throw new IllegalStateException("Maxbyte of " + MAX_BYTES + " exceeded!");

                    n = in.read(arr);
                    bytesRead += K4;
                    if (n < 0)
                        break;
                    sb.append(new String(arr, 0, n, encoding));
                }

            return sb.toString();

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

    public String detectCharset(String key, String res) {
        int encIndex = res.indexOf(key);
        int clength = key.length();
        if (encIndex > 0) {
            char startChar = res.charAt(encIndex + clength);
            int lastEncIndex;
            if (startChar == '\'')
                // if we have charset='something'
                lastEncIndex = res.indexOf("'", ++encIndex + clength);
            else if (startChar == '\"')
                // if we have charset="something"
                lastEncIndex = res.indexOf("\"", ++encIndex + clength);
            else {
                // if we have "text/html; charset=utf-8"                    
                int first = res.indexOf("\"", encIndex + clength);
                if (first < 0)
                    first = Integer.MAX_VALUE;

                // or "text/html; charset=utf-8 "
                int sec = res.indexOf(" ", encIndex + clength);
                if (sec < 0)
                    sec = Integer.MAX_VALUE;
                lastEncIndex = Math.min(first, sec);

                // or "text/html; charset=utf-8 '
                int third = res.indexOf("'", encIndex + clength);
                if (third > 0)
                    lastEncIndex = Math.min(lastEncIndex, third);
            }

            // re-read byte array with different encoding
            // assume that the encoding string cannot be greater than 40 chars
            if (lastEncIndex > encIndex + clength && lastEncIndex < encIndex + clength + 40) {
                return res.substring(encIndex + clength, lastEncIndex);
            }
        }
        return null;
    }
}
