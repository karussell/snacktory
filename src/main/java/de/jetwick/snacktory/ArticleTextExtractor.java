package de.jetwick.snacktory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Pattern;
import java.util.List;
import java.util.Set;
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
    // Unlikely candidates
    private static final Pattern UNLIKELY =
            Pattern.compile("(combx.*)|(comment.*)|(community.*)|(disqus.*)|(extra.*)|"
            + "(foot.*)|(header.*)|(menu.*)|(remark.*)|(rss.*)|(shoutbox.*)|(sidebar.*)|"
            + "(sponsor.*)|(ad.*)|(agegate.*)|(pagination.*)|(pager.*)|(popup.*)|"
            + "(print.*)|(archive.*)|(comment.*)|(discuss.*)|(e[-]?mail.*)|(share.*)|"
            + "(reply.*)|(all.*)|(login.*)|(sign.*)|(single.*)|(attachment.*)");
    // Most likely positive candidates
    private static final Pattern POSITIVE =
            Pattern.compile("(article.*)|(body.*)|(content.*)|(entry.*)|(hentry.*)|(main.*)|"
            + "(page.*)|(pagination.*)|(post.*)|(text.*)|(blog.*)|(story.*)|(haupt.*)|(.*artikel.*)");
    // Most likely negative candidates
    private static final Pattern NEGATIVE =
            Pattern.compile("(.*navigation.*)|(.*user.*)|(.*nav)|(.*combx.*)|(.*comment.*)|(com-.*)|(.*contact.*)|"
            + "(.*foot.*)|(.*footer.*)|(.*footnote.*)|(.*masthead.*)|(.*media.*)|(.*meta.*)|"
            + "(.*outbrain.*)|(.*promo.*)|(.*related.*)|(.*scroll.*)|(.*shoutbox.*)|"
            + "(.*sidebar.*)|(.*sponsor.*)|(.*shopping.*)|(.*tags.*)|(.*tool.*)|(.*widget.*)");
    private static final Pattern IMAGE_CAPTION =
            Pattern.compile("(.*caption.*)");
    private static final Set<String> set = new LinkedHashSet<String>() {

        {
            add("hacker news");
            add("facebook");
        }
    };

    /** 
     * @param html extracts article text from given html string. 
     * wasn't tested with improper HTML, although jSoup should be able 
     * to handle minor stuff.
     * @returns extracted article, all HTML tags stripped
     */
    public JResult extractContent(String html) throws Exception {
        if (html.isEmpty())
            throw new IllegalArgumentException("html string is empty!?");

        // http://jsoup.org/cookbook/extracting-data/selector-syntax
        Document doc = Jsoup.parse(html);

        JResult res = new JResult();
        res.setTitle(cleanTitle(doc.title()));

        if (res.getTitle().isEmpty())
            res.setTitle(Helper.innerTrim(doc.select("head title").text()));

        if (res.getTitle().isEmpty())
            res.setTitle(Helper.innerTrim(doc.select("head meta[name=title]").attr("content")));

        res.setDescription(Helper.innerTrim(doc.select("head meta[name=description]").attr("content")));

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
                res.setImageUrl(Helper.innerTrim(imgEl.attr("src")));
                // TODO remove parent container of image if it is contained in bestMatchElement
                // to avoid image subtitles flooding in
            }

            // clean before grabbing text
            String text = new OutputFormatter().getFormattedText(bestMatchElement);
            text = removeTitleFromText(text, res.getTitle());
            // this fails for short facebook post and probably tweets: text.length() > res.getDescription().length()
            if (text.length() > res.getTitle().length()) {
                res.setText(text);
//                print("best element:", bestMatchElement);
            }
        }

        // use open graph tag to get image
        if (res.getImageUrl().isEmpty())
            res.setImageUrl(Helper.innerTrim(doc.select("head meta[property=og:image]").attr("content")));

        // prefer link over thumbnail-meta if empty
        if (res.getImageUrl().isEmpty())
            res.setImageUrl(Helper.innerTrim(doc.select("link[rel=image_src]").attr("href")));

        if (res.getImageUrl().isEmpty())
            res.setImageUrl(Helper.innerTrim(doc.select("head meta[name=thumbnail]").attr("content")));

        res.setVideoUrl(Helper.innerTrim(doc.select("head meta[property=og:video]").attr("content")));

        res.setFaviconUrl(Helper.innerTrim(doc.select("head link[rel=icon]").attr("href")));
        if (res.getFaviconUrl().contains(" "))
            res.setFaviconUrl("");

        if (res.getFaviconUrl().isEmpty())
            // I don't know how to select rel=shortcut icon => select start==shortcut and end==icon
            res.setFaviconUrl(Helper.innerTrim(doc.select("head link[rel^=shortcut],link[rel$=icon]").attr("href")));

        if (res.getFaviconUrl().contains(" "))
            res.setFaviconUrl("");

        return res;
    }

    /** 
     * Weights current element. By matching it with positive candidates and 
     * weighting child nodes. Since it's impossible to predict which
     * exactly names, ids or class names will be used in HTML, major
     * role is played by child nodes
     * @param e Element to weight, along with child nodes
     */
    protected int getWeight(Element e) {
        Integer weight = 0;
        if (POSITIVE.matcher(e.className()).matches())
            weight += 20;

        if (POSITIVE.matcher(e.id()).matches())
            weight += 20;

        if (UNLIKELY.matcher(e.className()).matches())
            weight -= 10;

        if (UNLIKELY.matcher(e.id()).matches())
            weight -= 10;

        if (NEGATIVE.matcher(e.className()).matches())
            weight -= 30;

        if (NEGATIVE.matcher(e.id()).matches())
            weight -= 30;

        weight += (int) Math.round(e.ownText().length() / 100.0 * 10);
        weight += weightChildNodes(e);
        return weight;
    }

    /** 
     * Weights a child nodes of given Element. During tests some difficulties
     * were met. For instanance, not every single document has nested 
     * paragraph tags inside of the major article tag. Sometimes people 
     * are adding one more nesting level. So, we're adding 4 points for every 
     * 100 symbols contained in tag nested inside of the current weighted 
     * element, but only 3 points for every element that's nested 2 levels
     * deep. This way we give more chances to extract the element that has 
     * less nested levels, increasing probability of the correct extraction.
     * @param e Element, who's child nodes will be weighted
     */
    protected int weightChildNodes(Element e) {
        int weight = 0;
        Element caption = null;
        Element image = null;
        List<Element> headerEls = new ArrayList<Element>(5);
        List<Element> pEls = new ArrayList<Element>(5);

        image = determineImageSource(e);

        for (Element child : e.children()) {
            int ownTextLength = child.ownText().length();
            if (ownTextLength < 10)
                continue;

            if (IMAGE_CAPTION.matcher(e.id()).matches() || IMAGE_CAPTION.matcher(e.className()).matches())
                weight += 30;

            if (child.tagName().equals("h1") || child.tagName().equals("h2")) {
                weight += 30;
            } else if (child.tagName().equals("div") || child.tagName().equals("p")) {
                weight += calcWeightForChild(child, e);
                if (child.tagName().equals("p") && ownTextLength > 50)
                    pEls.add(child);

                if (child.className().toLowerCase().equals("caption"))
                    caption = child;
            }
        }

        // TODO use caption!
        // TODO use image
        if (image != null || caption != null)
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
        // TODO use headerEls for replacement of title?

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
        el.attr("gravityScore", "" + score);
    }

    public int calcWeightForChild(Element child, Element e) {
        // garbled html:
        String str = child.ownText();
        int c = Helper.count(str, "&quot;");
        c += Helper.count(str, "&lt;");
        c += Helper.count(str, "&gt;");
        c += Helper.count(str, "px");
        int val;
        if (c > 5)
            val = -30;
        else
            val = (int) Math.round(str.length() / 100.0 * 4);

        addScore(child, val);
        return val;
    }

    public Element determineImageSource(Element el) {
        int maxWeight = 0;
        Element maxNode = null;
        Elements els = el.select("img");
        if (els.isEmpty())            
            els = el.parent().select("img");        

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
            if (alt.length() > 50)
                weight += 20;

            String title = e.attr("title");
            if (title.length() > 50)
                weight += 20;

            if (weight > maxWeight) {
                maxWeight = weight;
                maxNode = e;
            }
        }

        return maxNode;
    }

    /** 
     * Prepares document. Currently only stipping unlikely candidates, 
     * since from time to time they're getting more score than good ones 
     * especially in cases when major text is short.
     * 
     * @param doc document to prepare. Passed as reference, and changed inside of function
     */
    protected void prepareDocument(Document doc) {
//        stripUnlikelyCandidates(doc);
        removeScriptsAndStyles(doc);
    }

    /** 
     *  Removes unlikely candidates from HTML. Currently takes id and class name and matches them against list of patterns
     *  @param doc document to strip unlikely candidates from
     */
    protected void stripUnlikelyCandidates(Document doc) {
        for (Element child : doc.select("body").select("*")) {
            String className = child.className().toLowerCase();
            String id = child.id().toLowerCase();

            if (NEGATIVE.matcher(className).matches()
                    || NEGATIVE.matcher(id).matches()) {
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
        return Helper.count(imageUrl, "ad") >= 2;
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
     * based on a delimeter in the title take the longest piece or do some custom logic based on the site
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
        Set<Element> nodes = new LinkedHashSet<Element>(32);
        int score = 100;
        for (Element el : doc.select("body").select("*")) {
            if ("p;div;td;h1;h2".contains(el.tagName())) {
                nodes.add(el);
                nodes.add(el.parent());
                setScore(el, score);
                score = score / 2;
            }
        }
        return nodes;
    }

    public String cleanTitle(String title) {
        StringBuilder res = new StringBuilder();
//        int index = title.lastIndexOf("|");
//        if (index > 0 && title.length() / 2 < index)
//            title = title.substring(0, index + 1);

        int counter = 0;
        String[] strs = title.split("\\|");
        for (String part : strs) {
            if (set.contains(part.toLowerCase().trim()))
                continue;

            if (counter == strs.length - 1 && res.length() > part.length())
                continue;

            if (counter > 0)
                res.append("|");

            res.append(part);
            counter++;
        }

        return Helper.innerTrim(res.toString());
    }
}
