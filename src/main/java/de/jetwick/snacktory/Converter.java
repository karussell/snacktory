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
import java.net.URLEncoder;
import org.apache.log4j.Logger;

/**
 * This class is not thread safe. Use one new instance every time due to encoding
 * variable.
 * 
 * @author Peter Karich, jetwick_@_pannous_._info
 */
public class Converter {

    private final static Logger logger = Logger.getLogger(Converter.class);    
    public final static String UTF8 = "UTF-8";
    public final static String ISO = "ISO-8859-1";
    public final static int K4 = 4096;
    private int maxBytes = 1000000;
    private String encoding;
    private String url;

    public Converter(String urlOnlyHint) {
        url = urlOnlyHint;
    }

    public Converter() {
    }

    public void setMaxBytes(int maxBytes) {
        this.maxBytes = maxBytes;
    }        

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
        return streamToString(is, maxBytes, encoding);
    }

    public String streamToString(InputStream is, String enc) {
        return streamToString(is, maxBytes, enc);
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
            encoding = UTF8;

        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(is, K4);
            StringBuilder sb = new StringBuilder();
            int bytesRead = K4;

            // detect with the help of meta tag
            try {
                String tmpEnc = detectCharset("charset=", sb, in);
                if (tmpEnc != null)
                    encoding = tmpEnc;
                else {
                    // detect with the help of xml beginning ala encoding="charset"
                    tmpEnc = detectCharset("encoding=", sb, in);
                    if (tmpEnc != null)
                        encoding = tmpEnc;
                }

                // try if detected is valid
                URLEncoder.encode("test", encoding);
            } catch (UnsupportedEncodingException e) {
                logger.info("Using default encoding:" + UTF8
                        + " problem:" + e.getMessage() + " encoding:" + encoding + " " + url);
                encoding = UTF8;
            }

            // SocketException: Connection reset
            // IOException: missing CR    => problem on server (probably some xml character thing?)
            // IOException: Premature EOF => socket unexpectly closed from server
            byte[] arr = new byte[K4];
            while (true) {
                if (bytesRead >= maxBytes) {
                    logger.warn("Maxbyte of " + maxBytes + " exceeded! Maybe html is now broken but try it nevertheless " + url);
                    break;
                }

                int n = in.read(arr);
                bytesRead += K4;
                if (n < 0)
                    break;
                sb.append(new String(arr, 0, n, encoding));
            }

            return sb.toString();

        } catch (SocketTimeoutException e) {
            logger.info(e.toString() + " url:" + url);
        } catch (IOException e) {
            logger.warn(e.toString() + " url:" + url);
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

    /**
     * This method detects the charset even if the first call only returns
     * some bytes. It will read until 4K bytes are reached and then try to 
     * determine the encoding
     * 
     * @throws IOException 
     */
    public String detectCharset(String key, StringBuilder sb, BufferedInputStream in) throws IOException {
        in.mark(K4 * 2);
        // Grab better encoding from stream
        byte[] arr = new byte[K4];
        int nSum = 0;
        while (nSum < K4) {
            int n = in.read(arr);
            nSum += n;
            if (n < 0)
                break;

            sb.append(new String(arr, 0, n, encoding));
        }

        int encIndex = sb.indexOf(key);
        int clength = key.length();
        if (encIndex > 0) {
            char startChar = sb.charAt(encIndex + clength);
            int lastEncIndex;
            if (startChar == '\'')
                // if we have charset='something'
                lastEncIndex = sb.indexOf("'", ++encIndex + clength);
            else if (startChar == '\"')
                // if we have charset="something"
                lastEncIndex = sb.indexOf("\"", ++encIndex + clength);
            else {
                // if we have "text/html; charset=utf-8"                    
                int first = sb.indexOf("\"", encIndex + clength);
                if (first < 0)
                    first = Integer.MAX_VALUE;

                // or "text/html; charset=utf-8 "
                int sec = sb.indexOf(" ", encIndex + clength);
                if (sec < 0)
                    sec = Integer.MAX_VALUE;
                lastEncIndex = Math.min(first, sec);

                // or "text/html; charset=utf-8 '
                int third = sb.indexOf("'", encIndex + clength);
                if (third > 0)
                    lastEncIndex = Math.min(lastEncIndex, third);
            }

            // re-read byte array with different encoding
            // assume that the encoding string cannot be greater than 40 chars
            if (lastEncIndex > encIndex + clength && lastEncIndex < encIndex + clength + 40) {
                String tmpEnc = Helper.encodingCleanup(sb.substring(encIndex + clength, lastEncIndex));
                try {
                    in.reset();
                    sb.setLength(0);
                    return tmpEnc;
                } catch (IOException ex) {
                    logger.warn("Couldn't reset stream to re-read with new encoding " + tmpEnc + " "
                            + ex.toString());
                }
            }
        }
        return null;
    }
}
