package de.jetwick.snacktory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is thread safe.
 *
 * @author Peter Karich, jetwick_@_pannous_._info
 */
public class ArticleTextExtractor {

    private static final Logger logger = LoggerFactory.getLogger(ArticleTextExtractor.class);
    // Interessting nodes
    private static final Pattern NODES =
            Pattern.compile("p|div|td|h1|h2|article|section");
    // Unlikely candidates
    private static final Pattern UNLIKELY =
            Pattern.compile("^(com(bx|ment|munity)|dis(qus|cuss)|e(xtra|[-]?mail)|foot|"
            + "header|menu|re(mark|ply)|rss|sh(are|outbox)|sponsor"
            + "a(d|ll|gegate|rchive|ttachment)|(pag(er|ination))|popup|print|"
            + "login|si(debar|gn|ngle))");
    // Most likely positive candidates
    private static final Pattern POSITIVE =
            Pattern.compile("(^(body|content|h?entry|main|page|post|text|blog|story|haupt))"
            + "|arti(cle|kel)|instapaper_body");
    // Most likely negative candidates
    private static final Pattern NEGATIVE =
            Pattern.compile("nav($|igation)|user|com(ment|bx)|(^com-)|contact|"
            + "foot|masthead|(me(dia|ta))|outbrain|promo|related|scroll|(sho(utbox|pping))|"
            + "sidebar|sponsor|tags|tool|widget|player|disclaimer");
    private static final Pattern NEGATIVE_STYLE = Pattern.compile("hidden|display: ?none");
    private static final String IMAGE_CAPTION = "caption";
    private static final Set<String> IGNORED_TITLE_PARTS = new LinkedHashSet<String>() {
        {
            add("hacker news");
            add("facebook");
        }
    };
    private static final OutputFormatter DEFAULT_FORMATTER = new OutputFormatter();

    /**
     * @param html extracts article text from given html string. wasn't tested with improper HTML,
     * although jSoup should be able to handle minor stuff.
     * @returns extracted article, all HTML tags stripped
     */
    public JResult extractContent(String html) throws Exception {
        return extractContent(new JResult(), html);
    }

    public JResult extractContent(JResult res, String html) throws Exception {
        return extractContent(res, html, DEFAULT_FORMATTER);
    }

    public JResult extractContent(JResult res, String html, OutputFormatter formatter) throws Exception {
        if (html.isEmpty())
            throw new IllegalArgumentException("html string is empty!?");

        // http://jsoup.org/cookbook/extracting-data/selector-syntax
        return extractContent(res, Jsoup.parse(html), formatter);
    }

    public JResult extractContent(JResult res, Document doc, OutputFormatter formatter) throws Exception {
        if (doc == null)
            throw new NullPointerException("missing document");

        res.setTitle(extractTitle(doc));

        res.setDescription(extractDescription(doc));

        res.setCanonicalUrl(extractCanonicalUrl(doc));

        // now remove the clutter
        prepareDocument(doc);

        // init elements
        Collection<Element> nodes = getNodes(doc);
        int maxWeight = 0;
        Element bestMatchElement = null;
        for (Element entry : nodes) {
            int currentWeight = getWeight(entry);
            if (currentWeight > maxWeight) {
                maxWeight = currentWeight;
                bestMatchElement = entry;
                if (maxWeight > 200)
                    break;
            }
        }

        if (bestMatchElement != null) {
            Element imgEl = determineImageSource(bestMatchElement);
            if (imgEl != null) {
                res.setImageUrl(SHelper.replaceSpaces(imgEl.attr("src")));
                // TODO remove parent container of image if it is contained in bestMatchElement
                // to avoid image subtitles flooding in
            }

            // clean before grabbing text
            String text = formatter.getFormattedText(bestMatchElement);
            text = removeTitleFromText(text, res.getTitle());
            // this fails for short facebook post and probably tweets: text.length() > res.getDescription().length()
            if (text.length() > res.getTitle().length()) {
                res.setText(text);
//                print("best element:", bestMatchElement);
            }
        }

        if (res.getImageUrl().isEmpty()) {
            res.setImageUrl(extractImageUrl(doc));
        }

        res.setRssUrl(extractRssUrl(doc));

        res.setVideoUrl(extractVideoUrl(doc));

        res.setFaviconUrl(extractFaviconUrl(doc));

        res.setKeywords(extractKeywords(doc));

        return res;
    }

    protected String extractTitle(Document doc) {
        String title = cleanTitle(doc.title());
        if (title.isEmpty()) {
            title = SHelper.innerTrim(doc.select("head title").text());
            if (title.isEmpty()) {
                title = SHelper.innerTrim(doc.select("head meta[name=title]").attr("content"));
                if (title.isEmpty()) {
                    title = SHelper.innerTrim(doc.select("head meta[property=og:title]").attr("content"));
                }
            }
        }
        return title;
    }

    protected String extractCanonicalUrl(Document doc) {
        String url = SHelper.replaceSpaces(doc.select("head link[rel=canonical]").attr("href"));
        if (url.isEmpty()) {
            url = SHelper.replaceSpaces(doc.select("head meta[property=og:url]").attr("content"));
        }
        return url;
    }

    protected String extractDescription(Document doc) {
        String description = SHelper.innerTrim(doc.select("head meta[name=description]").attr("content"));
        if (description.isEmpty()) {
            description = SHelper.innerTrim(doc.select("head meta[property=og:description]").attr("content"));
        }
        return description;
    }

    protected Collection<String> extractKeywords(Document doc) {
        String content = SHelper.innerTrim(doc.select("head meta[name=keywords]").attr("content"));

        if (content != null) {
            if (content.startsWith("[") && content.endsWith("]"))
                content = content.substring(1, content.length() - 1);

            String[] split = content.split("\\s*,\\s*");

            if (split.length > 1 || !split[0].equals(""))
                return Arrays.asList(split);
        }

        return Collections.emptyList();
    }

    /**
     * *
     * Tries to extract an image url from metadata if determineImageSource failed
     *
     * @param doc
     * @return image url or empty str
     */
    protected String extractImageUrl(Document doc) {
        // use open graph tag to get image
        String imageUrl = SHelper.replaceSpaces(doc.select("head meta[property=og:image]").attr("content"));
        if (imageUrl.isEmpty()) {
            // prefer link over thumbnail-meta if empty
            imageUrl = SHelper.replaceSpaces(doc.select("link[rel=image_src]").attr("href"));
            if (imageUrl.isEmpty()) {
                imageUrl = SHelper.replaceSpaces(doc.select("head meta[name=thumbnail]").attr("content"));
            }
        }
        return imageUrl;
    }

    protected String extractRssUrl(Document doc) {
        return SHelper.replaceSpaces(doc.select("link[rel=alternate]").select("link[type=application/rss+xml]").attr("href"));
    }

    protected String extractVideoUrl(Document doc) {
        return SHelper.replaceSpaces(doc.select("head meta[property=og:video]").attr("content"));
    }

    protected String extractFaviconUrl(Document doc) {
        String faviconUrl = SHelper.replaceSpaces(doc.select("head link[rel=icon]").attr("href"));
        if (faviconUrl.isEmpty()) {
            faviconUrl = SHelper.replaceSpaces(doc.select("head link[rel^=shortcut],link[rel$=icon]").attr("href"));
        }
        return faviconUrl;
    }

    /**
     * Weights current element. By matching it with positive candidates and weighting child nodes.
     * Since it's impossible to predict which exactly names, ids or class names will be used in
     * HTML, major role is played by child nodes
     *
     * @param e Element to weight, along with child nodes
     */
    protected int getWeight(Element e) {
        Integer weight = 0;
        if (POSITIVE.matcher(e.className()).find())
            weight += 35;

        if (POSITIVE.matcher(e.id()).find())
            weight += 40;

        if (UNLIKELY.matcher(e.className()).find())
            weight -= 20;

        if (UNLIKELY.matcher(e.id()).find())
            weight -= 20;

        if (NEGATIVE.matcher(e.className()).find())
            weight -= 50;

        if (NEGATIVE.matcher(e.id()).find())
            weight -= 50;

        String style = e.attr("style");
        if (style != null && !style.isEmpty() && NEGATIVE_STYLE.matcher(style).find())
            weight -= 50;

        weight += (int) Math.round(e.ownText().length() / 100.0 * 10);
        weight += weightChildNodes(e);
        return weight;
    }

    /**
     * Weights a child nodes of given Element. During tests some difficulties were met. For
     * instanance, not every single document has nested paragraph tags inside of the major article
     * tag. Sometimes people are adding one more nesting level. So, we're adding 4 points for every
     * 100 symbols contained in tag nested inside of the current weighted element, but only 3 points
     * for every element that's nested 2 levels deep. This way we give more chances to extract the
     * element that has less nested levels, increasing probability of the correct extraction.
     *
     * @param e Element, who's child nodes will be weighted
     */
    protected int weightChildNodes(Element e) {
        int weight = 0;
        Element caption = null;
        List<Element> headerEls = new ArrayList<Element>(5);
        List<Element> pEls = new ArrayList<Element>(5);

        for (Element child : e.children()) {
            String ownText = child.ownText();
            int ownTextLength = ownText.length();
            if (ownTextLength < 20)
                continue;

            if (ownTextLength > 200)
                weight += Math.max(50, ownTextLength / 10);

            if (e.id().contains(IMAGE_CAPTION) || e.className().contains(IMAGE_CAPTION))
                weight += 30;

            if (child.tagName().equals("h1") || child.tagName().equals("h2")) {
                weight += 30;
            } else if (child.tagName().equals("div") || child.tagName().equals("p")) {
                weight += calcWeightForChild(child, e, ownText);
                if (child.tagName().equals("p") && ownTextLength > 50)
                    pEls.add(child);

                if (child.className().toLowerCase().equals("caption"))
                    caption = child;
            }
        }

        // use caption and image
        if (caption != null)
            weight += 30;

        if (pEls.size() >= 2) {
            for (Element subEl : e.children()) {
                if ("h1;h2;h3;h4;h5;h6".contains(subEl.tagName())) {
                    weight += 20;
                    headerEls.add(subEl);
                }

                if ("p".contains(subEl.tagName()))
                    addScore(subEl, 30);
            }
            weight += 60;
        }
        return weight;
    }

    public int getScore(Element el) {
        int old = 0;
        try {
            old = Integer.parseInt(el.attr("gravityScore"));
        } catch (Exception ex) {
        }
        return old;
    }

    public void addScore(Element el, int score) {
        int old = getScore(el);
        setScore(el, score + old);
    }

    public void setScore(Element el, int score) {
        el.attr("gravityScore", Integer.toString(score));
    }

    public int calcWeightForChild(Element child, Element e, String ownText) {
        int c = SHelper.count(ownText, "&quot;");
        c += SHelper.count(ownText, "&lt;");
        c += SHelper.count(ownText, "&gt;");
        c += SHelper.count(ownText, "px");
        int val;
        if (c > 5)
            val = -30;
        else
            val = (int) Math.round(ownText.length() / 25.0);

        addScore(child, val);
        return val;
    }

    public Element determineImageSource(Element el) {
        int maxWeight = 0;
        Element maxNode = null;
        Elements els = el.select("img");
        if (els.isEmpty())
            els = el.parent().select("img");

        double score = 1;
        for (Element e : els) {
            String sourceUrl = e.attr("src");
            if (sourceUrl.isEmpty() || isAdImage(sourceUrl))
                continue;

            int weight = 0;
            try {
                int height = Integer.parseInt(e.attr("height"));
                if (height > 50)
                    weight += 20;
                else if (height < 50)
                    weight -= 20;
            } catch (Exception ex) {
            }

            try {
                int width = Integer.parseInt(e.attr("width"));
                if (width > 50)
                    weight += 20;
                else if (width < 50)
                    weight -= 20;
            } catch (Exception ex) {
            }
            String alt = e.attr("alt");
            if (alt.length() > 35)
                weight += 20;

            String title = e.attr("title");
            if (title.length() > 35)
                weight += 20;

            if (e.parent() != null) {
                String rel = e.parent().attr("rel");
                if (rel != null && rel.contains("nofollow"))
                    weight -= 40;
            }

            weight = (int) (weight * score);
            if (weight > maxWeight) {
                maxWeight = weight;
                maxNode = e;
                score = score / 2;
            }
        }

        return maxNode;
    }

    /**
     * Prepares document. Currently only stipping unlikely candidates, since from time to time
     * they're getting more score than good ones especially in cases when major text is short.
     *
     * @param doc document to prepare. Passed as reference, and changed inside of function
     */
    protected void prepareDocument(Document doc) {
//        stripUnlikelyCandidates(doc);
        removeScriptsAndStyles(doc);
    }

    /**
     * Removes unlikely candidates from HTML. Currently takes id and class name and matches them
     * against list of patterns
     *
     * @param doc document to strip unlikely candidates from
     */
    protected void stripUnlikelyCandidates(Document doc) {
        for (Element child : doc.select("body").select("*")) {
            String className = child.className().toLowerCase();
            String id = child.id().toLowerCase();

            if (NEGATIVE.matcher(className).find()
                    || NEGATIVE.matcher(id).find()) {
//                print("REMOVE:", child);
                child.remove();
            }
        }
    }

    private Document removeScriptsAndStyles(Document doc) {
        Elements scripts = doc.getElementsByTag("script");
        for (Element item : scripts) {
            item.remove();
        }

        Elements styles = doc.getElementsByTag("style");
        for (Element style : styles) {
            style.remove();
        }

        return doc;
    }

    private void print(Element child) {
        print("", child, "");
    }

    private void print(String add, Element child) {
        print(add, child, "");
    }

    private void print(String add1, Element child, String add2) {
        logger.info(add1 + " " + child.nodeName() + " id=" + child.id()
                + " class=" + child.className() + " text=" + child.text() + " " + add2);
    }

    private boolean isAdImage(String imageUrl) {
        return SHelper.count(imageUrl, "ad") >= 2;
    }

    /**
     * Match only exact matching as longestSubstring can be too fuzzy
     */
    public String removeTitleFromText(String text, String title) {
        // don't do this as its terrible to read
//        int index1 = text.toLowerCase().indexOf(title.toLowerCase());
//        if (index1 >= 0)
//            text = text.substring(index1 + title.length());
//        return text.trim();
        return text;
    }

    /**
     * based on a delimeter in the title take the longest piece or do some custom logic based on the
     * site
     *
     * @param title
     * @param delimeter
     * @return
     */
    private String doTitleSplits(String title, String delimeter) {
        String largeText = "";
        int largetTextLen = 0;
        String[] titlePieces = title.split(delimeter);

        // take the largest split
        for (String p : titlePieces) {
            if (p.length() > largetTextLen) {
                largeText = p;
                largetTextLen = p.length();
            }
        }

        largeText = largeText.replace("&raquo;", " ");
        largeText = largeText.replace("»", " ");
        return largeText.trim();
    }

    /**
     * @return a set of all important nodes
     */
    public Collection<Element> getNodes(Document doc) {
        Map<Element, Object> nodes = new LinkedHashMap<Element, Object>(64);
        int score = 100;
        for (Element el : doc.select("body").select("*")) {
            if (NODES.matcher(el.tagName()).matches()) {
                nodes.put(el, null);
                setScore(el, score);
                score = score / 2;
            }
        }
        return nodes.keySet();
    }

    public String cleanTitle(String title) {
        StringBuilder res = new StringBuilder();
//        int index = title.lastIndexOf("|");
//        if (index > 0 && title.length() / 2 < index)
//            title = title.substring(0, index + 1);

        int counter = 0;
        String[] strs = title.split("\\|");
        for (String part : strs) {
            if (IGNORED_TITLE_PARTS.contains(part.toLowerCase().trim()))
                continue;

            if (counter == strs.length - 1 && res.length() > part.length())
                continue;

            if (counter > 0)
                res.append("|");

            res.append(part);
            counter++;
        }

        return SHelper.innerTrim(res.toString());
    }
}
