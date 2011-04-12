package com.jreadability.main;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.select.Elements;
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
    private String imageSrc;
    private String title;
    private boolean preferEmptyTextOverGarbageText = false;

    /** 
     * @param html extracts article text from given html string. 
     * wasn't tested with improper HTML, although jSoup should be able 
     * to handle minor stuff.
     * @returns extracted article, all HTML tags stripped
     */
    public String getArticleText(String html) throws Exception {
        if(html.isEmpty())
            throw new IllegalArgumentException("html string is empty!?");
        
        // http://jsoup.org/cookbook/extracting-data/selector-syntax
        Document doc = Jsoup.parse(html);

//        for(Element e: doc.select("storyContent")) {
//            print(e);
//        }

        imageSrc = "";
        // grabbing the title should be easy
        title = doc.select("head title").text();

        if (title.isEmpty())
            title = doc.select("head meta[name=title]").attr("content");

        String text = doc.select("head meta[name=description]").attr("content");

        // now remove the clutter
        prepareDocument(doc);

        // init elements
        Map<Element, Integer> scores = new HashMap<Element, Integer>();
        for (Element el : doc.select("body").select("*")) {
            if (el.tag().getName().equals("p") || el.tag().getName().equals("td") || el.tag().getName().equals("div")) {
                scores.put(el, 0);
            }
        }

        int maxWeight;
        if (preferEmptyTextOverGarbageText)
            maxWeight = 0;
        else
            maxWeight = Integer.MIN_VALUE;

        Element bestMatchElement = null;
        for (Element entry : scores.keySet()) {
//            print(entry);                
            int currentWeight = getWeight(entry);
            if (currentWeight > maxWeight) {
                maxWeight = currentWeight;
                bestMatchElement = entry;
            }
        }

        if (bestMatchElement != null && !bestMatchElement.text().isEmpty())
            text = bestMatchElement.text();

        if (bestMatchElement != null) {
            Element imgEl = determineImageSource(bestMatchElement);
            if (imgEl != null) {
                imageSrc = imgEl.attr("src");
                // TODO remove parent container of image if it is contained in bestMatchElement
                // to avoid image subtitles flooding in
            }
        }

        // use open graph tag to get image
        if (imageSrc.isEmpty())
            imageSrc = doc.select("head meta[property=og:image]").attr("content");
                
        // prefer link over thumbnail-meta if empty
        if (imageSrc.isEmpty())
            imageSrc = doc.select("link[rel=image_src]").attr("href");
        
        if (imageSrc.isEmpty())
            imageSrc = doc.select("head meta[name=thumbnail]").attr("content");

        return text;
    }

    public String getImageSource() {
        return imageSrc;
    }

    public String getTitle() {
        return title;
    }

    public Element determineImageSource(Element el) {
        for (Element e : el.getElementsByTag("img")) {
            if (!e.attr("src").isEmpty())
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
            if ("storyContent".equals(child.className()))
                print(child);

            String className = child.className().toLowerCase();
            String id = child.id().toLowerCase();

            if (negative.matcher(className).matches()
                    || negative.matcher(id).matches()) {

                child.remove();
            }
        }
    }

    private static boolean isHighLinkDensity(Element e) {
        Elements links = e.getElementsByTag("a");

        if (links.size() == 0)
            return false;

        float score = 0;
        String text = e.text();
        String[] words = text.split(" ");
        float numberOfWords = words.length;


        // let's loop through all the links and calculate the number of words that make up the links
        StringBuilder sb = new StringBuilder();
        for (Element link : links) {
            sb.append(link.text());
        }
        String linkText = sb.toString();
        String[] linkWords = linkText.split(" ");
        float numberOfLinkWords = linkWords.length;

        float numberOfLinks = links.size();

        float linkDivisor = (float) (numberOfLinkWords / numberOfWords);
        score = (float) linkDivisor * numberOfLinks;

        if (score > 1) {
            return true;
        }

        return false;
    }

    /** 
     *  Weights current element. By matching it with positive candidates and weighting child nodes. Since it's impossible to predict which
     *    exactly names, ids or class names will be used in HTML, major role is played by child nodes
     *  @param e Element to weight, along with child nodes
     */
    protected int getWeight(Element e) {
        Integer weight = 0;
        if (positive.matcher(e.className()).matches())
            weight += 25;

        if (positive.matcher(e.id()).matches())
            weight += 25;

        if (unlikely.matcher(e.className()).matches())
            weight -= 20;

        if (unlikely.matcher(e.id()).matches())
            weight -= 20;

        if (weight >= 0) {
            int childNodesWeight = weightChildNodes(e);
            if (childNodesWeight == 0 && e.ownText().length() > 100) {
                weight += Math.round(e.ownText().length() / 100) * 5;
            } else {
                weight += childNodesWeight;
            }
        }
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
            if (child.tag().getName().equals("div") || child.tag().getName().equals("p")) {
                if (child.ownText().length() > 100) {
                    if (child.parent() == e) {
                        weight += Math.round(child.ownText().length() / 100) * 4;
                    } else if (child.parent().parent() == e) {
                        weight += Math.round(child.ownText().length() / 100) * 3;
                    }
                }
            }
        }
        return weight;
    }

    private void print(Element child) {
        logger.info(child.nodeName() + " id=" + child.id() + " class=" + child.className() + " : " + child.text());
    }
}
