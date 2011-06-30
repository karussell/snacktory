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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to fetch articles.
 * This class is thread safe.
 * 
 * @author Peter Karich, jetwick_@_pannous_._info
 */
public class HtmlFetcher {

    static {
        SHelper.enableCookieMgmt();
        SHelper.enableUserAgentOverwrite();
    }
    private static final Logger logger = LoggerFactory.getLogger(HtmlFetcher.class);

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("urls.txt"));
        String line = null;
        Set<String> existing = new LinkedHashSet<String>();
        while ((line = reader.readLine()) != null) {
            int index1 = line.indexOf("\"");
            int index2 = line.indexOf("\"", index1 + 1);
            String url = line.substring(index1 + 1, index2);
            String domainStr = SHelper.extractDomain(url, true);
            String counterStr = "";
            // TODO more similarities
            if (existing.contains(domainStr))
                counterStr = "2";
            else
                existing.add(domainStr);

            String html = new HtmlFetcher().fetchAsString(url, 20000);
            String outFile = domainStr + counterStr + ".html";
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
            writer.write(html);
            writer.close();
        }
        reader.close();
    }
    private String referrer = "http://jetsli.de";
    private String userAgent = "Mozilla/5.0 (compatible; Jetslide; +" + referrer + ")";
    private String cacheControl = "max-age=0";
    private String language = "en-us";
    private String accept = "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5";
    private String charset = "UTF-8";
    private SCache cache;
    private AtomicInteger cacheCounter = new AtomicInteger(0);;
    private int maxTextLength = -1;
    private ArticleTextExtractor extractor = new ArticleTextExtractor();

    public HtmlFetcher() {
    }

    public void setExtractor(ArticleTextExtractor extractor) {
        this.extractor = extractor;
    }

    public HtmlFetcher setCache(SCache cache) {
        this.cache = cache;
        return this;
    }

    public SCache getCache() {
        return cache;
    }

    public int getCacheCounter() {
        return cacheCounter.get();
    }
    
    public HtmlFetcher clearCacheCounter() {
        cacheCounter.set(0);
        return this;
    }

    public HtmlFetcher setMaxTextLength(int maxTextLength) {
        this.maxTextLength = maxTextLength;
        return this;
    }

    public int getMaxTextLength() {
        return maxTextLength;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setCacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getReferrer() {
        return referrer;
    }

    public HtmlFetcher setReferrer(String referrer) {
        this.referrer = referrer;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getAccept() {
        return accept;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    public String getCharset() {
        return charset;
    }

    public JResult fetchAndExtract(String url, int timeout, boolean resolve) throws Exception {
        url = SHelper.removeHashbang(url);
        String gUrl = SHelper.getUrlFromUglyGoogleRedirect(url);
        if (gUrl != null)
            url = gUrl;
        else {
            gUrl = SHelper.getUrlFromUglyFacebookRedirect(url);
            if (gUrl != null)
                url = gUrl;
        }

        if (resolve) {
            // TODO remove the time (from timeout) it has taken to call getResolveUrl!
            String resUrl = getResolvedUrl(url, timeout);
            // if resolved url is longer: use it!
            if (resUrl != null && resUrl.trim().length() > url.length()) {
                // this is necessary e.g. for some homebaken url resolvers which returl 
                // the resolved url relative to url!
                url = SHelper.useDomainOfFirst4Second(url, resUrl);
            }

            if (cache != null) {
                JResult res = cache.get(url);
                if (res != null) {
                    cacheCounter.addAndGet(1);
                    return res;
                }
            }
        }

        JResult result = new JResult();
        result.setDate(SHelper.estimateDate(url));
        String lowerUrl = url.toLowerCase();
        if (SHelper.isDoc(lowerUrl) || SHelper.isApp(lowerUrl) || SHelper.isPackage(lowerUrl)) {
            result.setUrl(url);
            return save(result);
        } else if (SHelper.isVideo(lowerUrl) || SHelper.isAudio(lowerUrl)) {
            result.setUrl(url);
            result.setVideoUrl(url);
            return save(result);
        } else if (SHelper.isImage(lowerUrl)) {
            result.setUrl(url);
            result.setImageUrl(url);
            return save(result);
        }

        JResult tmp = extractor.extractContent(fetchAsString(url, timeout));
        result = tmp.setDate(result.getDate());

        // or should we use? <link rel="canonical" href="http://www.N24.de/news/newsitem_6797232.html"/>
        result.setUrl(url);

        if (result.getFaviconUrl().isEmpty())
            result.setFaviconUrl(SHelper.getDefaultFavicon(url));

        // some links are relative to root and do not include the domain of the url :/
        result.setImageUrl(fixUrl(url, result.getImageUrl()));
        result.setFaviconUrl(fixUrl(url, result.getFaviconUrl()));
        result.setVideoUrl(fixUrl(url, result.getVideoUrl()));
        return save(result);
    }

    protected JResult save(JResult res) {
        res.setText(lessText(res.getText()));
        if (cache != null)
            cache.put(res.getUrl(), res);
        return res;
    }

    public String lessText(String text) {
        if (text == null)
            return "";

        if (maxTextLength >= 0 && text.length() > maxTextLength)
            return text.substring(0, maxTextLength);

        return text;
    }

    private static String fixUrl(String url, String urlOrPath) {
        return SHelper.useDomainOfFirst4Second(url, urlOrPath);
    }

    public String fetchAsString(String urlAsString, int timeout) throws MalformedURLException, IOException {
        return fetchAsString(urlAsString, timeout, true);
    }

    public String fetchAsString(String urlAsString, int timeout, boolean includeSomeGooseOptions) throws MalformedURLException, IOException {
        HttpURLConnection hConn = createUrlConnection(urlAsString, timeout, includeSomeGooseOptions);
        hConn.setInstanceFollowRedirects(true);
        InputStream is = hConn.getInputStream();

//            if ("gzip".equals(hConn.getContentEncoding()))
//                is = new GZIPInputStream(is);                        

        String enc = Converter.extractEncoding(hConn.getContentType());
        return new Converter(urlAsString).streamToString(is, enc);
    }

    /**
     * On some devices we have to hack:
     * http://developers.sun.com/mobility/reference/techart/design_guidelines/http_redirection.html
     * @return the resolved url if any. Or null if it couldn't resolve the url
     * (within the specified time) or the same url if response code is OK
     */
    public String getResolvedUrl(String urlAsString, int timeout) {
        try {
            HttpURLConnection hConn = createUrlConnection(urlAsString, timeout, true);
            // force no follow
            hConn.setInstanceFollowRedirects(false);
            // the program doesn't care what the content actually is !!
            // http://java.sun.com/developer/JDCTechTips/2003/tt0422.html
            hConn.setRequestMethod("HEAD");
            hConn.connect();
            int responseCode = hConn.getResponseCode();
            hConn.getInputStream().close();
            if (responseCode == HttpURLConnection.HTTP_OK)
                return urlAsString;

            String loc = hConn.getHeaderField("Location");
            if (responseCode / 100 == 3 && loc != null) {
                loc = loc.replaceAll(" ", "+");
                if (urlAsString.startsWith("http://bit.ly") || urlAsString.startsWith("http://is.gd"))
                    loc = encodeUriFromHeader(loc);
                return loc;
            }

        } catch (Exception ex) {
        }
        return "";
    }

    /**
     * Takes a URI that was decoded as ISO-8859-1 and applies percent-encoding
     * to non-ASCII characters. Workaround for broken origin servers that send
     * UTF-8 in the Location: header.
     */
    static String encodeUriFromHeader(String badLocation) {
        StringBuilder sb = new StringBuilder();

        for (char ch : badLocation.toCharArray()) {
            if (ch < (char) 128) {
                sb.append(ch);
            } else {
                // this is ONLY valid if the uri was decoded using ISO-8859-1
                sb.append(String.format("%%%02X", (int) ch));
            }
        }

        return sb.toString();
    }

    private HttpURLConnection createUrlConnection(String urlAsStr, int timeout,
            boolean includeSomeGooseOptions) throws MalformedURLException, IOException {
        URL url = new URL(urlAsStr);
        //using proxy may increase latency
        HttpURLConnection hConn = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
        hConn.setRequestProperty("User-Agent", userAgent);
        hConn.setRequestProperty("Accept", accept);

        if (includeSomeGooseOptions) {
            hConn.setRequestProperty("Accept-Language", language);
            hConn.setRequestProperty("content-charset", charset);
            hConn.addRequestProperty("Referer", referrer);
            // avoid the cache for testing purposes only?
            hConn.setRequestProperty("Cache-Control", cacheControl);
        }

        // On android we got timeouts because of this!!   
        // this also results in invalid html for http://twitpic.com/4kuem8
//        hConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        hConn.setConnectTimeout(timeout);
        hConn.setReadTimeout(timeout);
        return hConn;
    }
}
