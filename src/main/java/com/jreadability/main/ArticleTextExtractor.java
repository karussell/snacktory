package com.jreadability.main;

import java.util.ArrayList;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Pattern;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArticleTextExtractor {

    private static final Logger logger = LoggerFactory.getLogger(ArticleTextExtractor.class);
    // Unlikely candidates
    private static final Pattern unlikely =
            Pattern.compile("(combx.*)|(comment.*)|(community.*)|(disqus.*)|(extra.*)|"
            + "(foot.*)|(header.*)|(menu.*)|(remark.*)|(rss.*)|(shoutbox.*)|(sidebar.*)|"
            + "(sponsor.*)|(ad.*)|(agegate.*)|(pagination.*)|(pager.*)|(popup.*)|"
            + "(print.*)|(archive.*)|(comment.*)|(discuss.*)|(e[-]?mail.*)|(share.*)|"
            + "(reply.*)|(all.*)|(login.*)|(sign.*)|(single.*)|(attachment.*)");
    // Most likely positive candidates
    private static final Pattern positive =
            Pattern.compile("(article.*)|(body.*)|(content.*)|(entry.*)|(hentry.*)|(main.*)|"
            + "(page.*)|(pagination.*)|(post.*)|(text.*)|(blog.*)|(story.*)|(haupt.*)");
    // Most likely negative candidates
    private static final Pattern negative =
            Pattern.compile("(.*navigation.*)|(.*user.*)|(.*nav)|(.*combx.*)|(.*comment.*)|(com-.*)|(.*contact.*)|"
            + "(.*foot.*)|(.*footer.*)|(.*footnote.*)|(.*masthead.*)|(.*media.*)|(.*meta.*)|"
            + "(.*outbrain.*)|(.*promo.*)|(.*related.*)|(.*scroll.*)|(.*shoutbox.*)|"
            + "(.*sidebar.*)|(.*sponsor.*)|(.*shopping.*)|(.*tags.*)|(.*tool.*)|(.*widget.*)");
    private static final Pattern imageCaption =
            Pattern.compile("(.*caption.*)");

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
        // grabbing the title should be easy or use doc.title()
        res.setTitle(cleanTitle(doc.select("head title").text()));

        if (res.getTitle().isEmpty())
            res.setTitle(cleanTitle(doc.select("head meta[name=title]").attr("content")));

        res.setDescription(Helper.innerTrim(doc.select("head meta[name=description]").attr("content")));

        List<Element> h1Orh2Nodes = new ArrayList<Element>();
        for (Element el : doc.select("body").select("h1")) {
            h1Orh2Nodes.add(el.parent());
        }

        for (Element el : doc.select("body").select("h2")) {
            h1Orh2Nodes.add(el.parent());
        }
        // now remove the clutter
        prepareDocument(doc);

        // init elements
        List<Element> scores = new ArrayList<Element>();
        for (Element el : doc.select("body").select("*")) {
            if (el.tag().getName().equals("p") || el.tag().getName().equals("td") || el.tag().getName().equals("div")) {
//                print(el);
                scores.add(el);
            }
        }

        int maxWeight = 0;
        Element bestMatchElement = null;
        for (Element entry : scores) {
            int currentWeight = getWeight(entry);
            if (currentWeight > maxWeight) {
                // TODO REMOVE
//                currentWeight = getWeight(entry);
                maxWeight = currentWeight;
                bestMatchElement = entry;
            }
        }

        // determine text with nodes containing h1 or h2
        if (bestMatchElement == null) {
            for (Element entry : h1Orh2Nodes) {
                int currentWeight = getWeight(entry);
                if (currentWeight > maxWeight) {
                    maxWeight = currentWeight;
                    bestMatchElement = entry;
                }
            }
        }

        if (bestMatchElement != null) {
            Element imgEl = determineImageSource(bestMatchElement);
            if (imgEl != null) {
                res.setImageUrl(Helper.innerTrim(imgEl.attr("src")));
                // TODO remove parent container of image if it is contained in bestMatchElement
                // to avoid image subtitles flooding in
            }

            String text = Helper.innerTrim(bestMatchElement.text());
            text = removeTitleFromText(text, res.getTitle());
            if (text.length() > res.getDescription().length() && text.length() > res.getTitle().length()) {
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

//        if(res.getFaviconUrl().isEmpty())
//            res.setFaviconUrl(Helper.getDefaultFavicon(url));
        return res;
    }

    public Element determineImageSource(Element el) {
        for (Element e : el.getElementsByTag("img")) {
            int height = Integer.MAX_VALUE;
            try {
                height = Integer.parseInt(e.attr("height"));
            } catch (Exception ex) {
            }
            int width = Integer.MAX_VALUE;
            try {
                width = Integer.parseInt(e.attr("width"));
            } catch (Exception ex) {
            }
            String sourceUrl = e.attr("src");
            if (!sourceUrl.isEmpty() && !isAdImage(sourceUrl) && height > 30 && width > 30)
                return e;
        }

        return null;
    }

    /** Prepares document. Currently only stipping unlikely candidates, since from time to time they're getting more score than good ones 
     *         especially in cases when major text is short.
     *  @param doc document to prepare. Passed as reference, and changed inside of function
     */
    protected void prepareDocument(Document doc) {
        stripUnlikelyCandidates(doc);
    }

    /** 
     *  Removes unlikely candidates from HTML. Currently takes id and class name and matches them against list of patterns
     *  @param doc document to strip unlikely candidates from
     */
    protected void stripUnlikelyCandidates(Document doc) {
        for (Element child : doc.select("body").select("*")) {
            String className = child.className().toLowerCase();
            String id = child.id().toLowerCase();

            if (negative.matcher(className).matches()
                    || negative.matcher(id).matches()) {
//                print("REMOVE:", child);
                child.remove();
            }
        }
    }

    /** 
     *  Weights current element. By matching it with positive candidates and weighting child nodes. Since it's impossible to predict which
     *    exactly names, ids or class names will be used in HTML, major role is played by child nodes
     *  @param e Element to weight, along with child nodes
     */
    protected int getWeight(Element e) {
        Integer weight = 0;
        if (positive.matcher(e.className()).matches())
            weight += 20;

        if (positive.matcher(e.id()).matches())
            weight += 20;

        if (unlikely.matcher(e.className()).matches() || unlikely.matcher(e.id()).matches())
            return -1;


        weight += (int) Math.round(e.ownText().length() / 100.0 * 10);
        weight += weightChildNodes(e);
        if (weight <= 40)
            return -1;

        return weight;
    }

    /** 
     *  Weights a child nodes of given Element. During tests some difficulties were met. For instanance, not every single document has nested 
     *  paragraph tags inside of the major article tag. Sometimes people are adding one more nesting level. So, we're adding 4 points for every 
     *  100 symbols contained in tag nested inside of the current weighted element, but only 3 points for every element that's nested 2 levels
     *  deep. This way we give more chances to extract the element that has less nested levels, increasing probability of the correct extraction.
     *  @param e Element, who's child nodes will be weighted
     */
    protected int weightChildNodes(Element e) {
        int weight = 0;
        for (Element child : e.children()) {
            if (child.ownText().length() < 10 || e.tagName().equals("script"))
                continue;

            if (imageCaption.matcher(e.id()).matches() || imageCaption.matcher(e.className()).matches())
                weight += 30;

            if (child.tag().getName().equals("h1") || child.tag().getName().equals("h2")) {
                weight += 30;
            } else if (child.tag().getName().equals("div") || child.tag().getName().equals("p")) {
                weight += calcWeightForChild(child, e);
            }
//            else if (child.className().isEmpty() && child.id().isEmpty() && child.attr("style").isEmpty()) {
//                if (child.ownText().length() > 0)
//                    weight += calcWeightForChild(child, e);
//                else
//                    // got deeper if a container has no styling attributes like ol, li, strong, ...
//                    weight += weightChildNodes(child);
//            }
        }
        return weight;
    }

    public int calcWeightForChild(Element child, Element e) {
        // garbled html:
        int c = Helper.count(child.ownText(), "&quot;");
        c += Helper.count(child.ownText(), "&lt;");
        c += Helper.count(child.ownText(), "&gt;");
        c += Helper.count(child.ownText(), "px");
        if (c > 5)
            return -30;

        if (child.parent() == e) {
            return (int) Math.round(child.ownText().length() / 100.0 * 4);
        } else if (child.parent().parent() == e) {
            return (int) Math.round(child.ownText().length() / 100.0 * 3);
        }

        return 0;
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
     * TODO improve algo if title has parts which should be removed!
     */
    public String removeTitleFromText(String text, String title) {
        int index1 = text.toLowerCase().indexOf(title.toLowerCase());
        if (index1 >= 0)
            text = text.substring(index1 + title.length());
        return text;
    }

    public String cleanTitle(String title) {
        boolean usedDelimeter = false;

        // THIS PROCESS is a bit unreliable as it sometimes removes information from title
        // as it is based on length comparison of the title parts
        // and the title is the most important thing of the article
//        if (title.contains("|")) {
//            title = doTitleSplits(title, "\\|");
//            usedDelimeter = true;
//        }
//
//        if (!usedDelimeter && title.contains("»")) {
//            title = doTitleSplits(title, "»");
//            usedDelimeter = true;
//        }
//
//        if (!usedDelimeter && title.contains("«")) {
//            title = doTitleSplits(title, "«");
//            usedDelimeter = true;
//        }

        // removes author in youtube :/
//        if (!usedDelimeter && title.contains("-")) {
//            title = doTitleSplits(title, " - ");
//            usedDelimeter = true;
//        }

        // do not do this as titles contain colon in rare cases (twitter blog, golem)
//        if (!usedDelimeter && title.contains(":")) {
//            title = doTitleSplits(title, ":");
//            usedDelimeter = true;
//        }

        // encode unicode charz
//        title = StringEscapeUtils.escapeHtml(titleText);
        return Helper.innerTrim(title);
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
}
